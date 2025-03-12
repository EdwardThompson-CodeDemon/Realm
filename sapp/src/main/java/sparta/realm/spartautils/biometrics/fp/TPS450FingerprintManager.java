package sparta.realm.spartautils.biometrics.fp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.PowerManager;
import android.util.Log;

import com.suprema.BioMiniFactory;
import com.suprema.CaptureResponder;
import com.suprema.IBioMiniDevice;
import com.suprema.util.Logger;
import com.telpo.tps550.api.fingerprint.FingerPrint;

import java.util.HashMap;
import java.util.Iterator;

import sparta.realm.spartautils.biometrics.fp.sdks.tps450.DataStorage;

public class TPS450FingerprintManager extends FingerprintManger {

    private static final String logTag = "TPS450FPManager";
    private PendingIntent mPermissionIntent;

    public TPS450FingerprintManager(Activity activity) {
        super(activity);
        initUsbListener();
        initDataStorage();
    }


    @Override
    public void start() {
        super.start();

        addDeviceToUsbDeviceList();
    }


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            Log.e(logTag, "onReceive: " + usbDevice.toString());
            int interfaceCount = usbDevice.getInterfaceCount();
            for (int i = 0; i < interfaceCount; i++) {
                UsbInterface anInterface = usbDevice.getInterface(i);
                Log.e(logTag, "UsbInterface: " + anInterface.toString());
                int interfaceClass = anInterface.getInterfaceClass();
                Log.e(logTag, "interfaceClass: " + interfaceClass);
            }

            Log.e(logTag, "UsbDevice \nVendorID: " + usbDevice.getVendorId() + "\nProductId:" + usbDevice.getProductId());

            String action = intent.getAction();
            switch (action) {
                case ACTION_USB_PERMISSION:
                    Log.e(logTag, "ACTION_USB_PERMISSION");
                    boolean hasUsbPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                    if (hasUsbPermission && usbDevice != null) {
                        Log.e(logTag, usbDevice.getDeviceName() + " has acquired the usb permission. activating this device.");
                        mUsbDevice = usbDevice;
                        createBioMiniDevice();
                    } else {
                        Log.e(logTag, "USB permission is not granted!");
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    Log.e(logTag, "ACTION_USB_DEVICE_ATTACHED");
                    addDeviceToUsbDeviceList();
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    Log.e(logTag, "USB detached");
                    removeDevice();
                    break;
                default:
                    break;
            }
        }
    };

    private void removeDevice() {
        Log.e(logTag, "ACTION_USB_DEVICE_DETACHED");
        if (mBioMiniFactory != null) {
            mBioMiniFactory.removeDevice(mUsbDevice);
            mBioMiniFactory.close();
        }
        mUsbDevice = null;
        mCurrentDevice = null;

    }

    public void addDeviceToUsbDeviceList() {
        Logger.d("start!");
        if (mUsbManager == null) {
            Logger.d("mUsbManager is null");
            return;
        }
        if (mUsbDevice != null) {
            Logger.e("usbdevice is not null!");
            return;
        }

        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIter = deviceList.values().iterator();
        while (deviceIter.hasNext()) {
            UsbDevice _device = deviceIter.next();
            Logger.e("usbdevice: " + _device.getDeviceName());
            if (_device.getVendorId() == 0x16d1) {
                Logger.d("found suprema usb device");
                mUsbDevice = _device;
                if (mUsbManager.hasPermission(_device) == false) {
                    Logger.d("This device need to Usb Permission!");
//                    mHandler.sendEmptyMessage(REQUEST_USB_PERMISSION);
                    mPermissionIntent = PendingIntent.getBroadcast(activity, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    mUsbManager.requestPermission(mUsbDevice, mPermissionIntent);
                } else {
                    Logger.e("This device already has USB permission! please activate this device.");
//                    mHandler.sendEmptyMessage(ACTIVATE_USB_DEVICE);
                    mUsbDevice = _device;
                    createBioMiniDevice();
                }
            } else {
                Logger.d("This device is not suprema device!  : " + _device.getVendorId());
            }
        }
    }

    PowerManager.WakeLock mWakeLock = null;

    private void requestWakeLock() {
        Log.e(logTag, "START!");
        PowerManager powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ":BioMini WakeLock");
        mWakeLock.acquire();
    }

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private void initUsbListener() {
        Log.e(logTag, "start!");
        mUsbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
//        PendingIntent pi = PendingIntent.getBroadcast(activity, 0, new Intent(ACTION_USB_PERMISSION), 0);
        activity.registerReceiver(mUsbReceiver, new IntentFilter(ACTION_USB_PERMISSION));
        IntentFilter attachfilter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        activity.registerReceiver(mUsbReceiver, attachfilter);
        IntentFilter detachfilter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        activity.registerReceiver(mUsbReceiver, detachfilter);
    }

    private BioMiniFactory mBioMiniFactory;
    public IBioMiniDevice mCurrentDevice;
    UsbManager mUsbManager;
    UsbDevice mUsbDevice;

    private void createBioMiniDevice() {
        Log.e(logTag, "START!");
        if (mUsbDevice == null) {
//            Toast.makeText(mContext,getResources().getString(R.string.error_device_not_conneted),Toast.LENGTH_SHORT).show();
            return;
        }
        if (mBioMiniFactory != null) {
            mBioMiniFactory.close();
        }

        Log.e(logTag, "new BioMiniFactory( )");
        mBioMiniFactory = new BioMiniFactory(activity, mUsbManager) { //for android sample
            @Override
            public void onDeviceChange(DeviceChangeEvent event, Object dev) {
                Log.e(logTag, "onDeviceChange : " + event);

            }
        };
        Log.e(logTag, "new BioMiniFactory( ) : " + mBioMiniFactory);

        boolean _result = mBioMiniFactory.addDevice(mUsbDevice);

        if (_result == true) {
            mCurrentDevice = mBioMiniFactory.getDevice(0);
            if (mCurrentDevice != null) {
                interf.onConnectionStatusChanged(true, mUsbDevice);
                Log.e(logTag, "mCurrentDevice attached : " + mCurrentDevice);
                Log.e("logTag", String.format("Device name: %s\tDevice serial no: %s\tDevice firmware version: %s\tSDK version: %s", mCurrentDevice.getDeviceInfo().deviceName, mCurrentDevice.getDeviceInfo().deviceSN, mCurrentDevice.getDeviceInfo().versionFW, mBioMiniFactory.getSdkVersionInfo()));
                getDefaultParameterFromDevice(mUsbDevice);
                doAutoCapture();
            } else {
                Log.e(logTag, "mCurrentDevice is null");
            }

        } else {
            Log.e(logTag, "addDevice is fail!");
        }
    }

    public static DataStorage mDataStorage = null;
    //Device ID
    private static final int BioMiniOC4 = 0x0406;
    private static final int BioMiniSlim = 0x0407;
    private static final int BioMiniSlim2 = 0x0408;
    private static final int BioMiniPlus2 = 0x0409;
    private static final int BioMiniSlimS = 0x0420;
    private static final int BioMiniSlim2S = 0x0421;
    private static final int BioMiniSlim3 = 0x0460;
    private static final int BioMiniSlim3HID = 0x0423;
    private void initDataStorage() {
        mDataStorage = new DataStorage();
        mDataStorage.init(activity);
    }

    private void getDefaultParameterFromDevice(UsbDevice mUsbDevice) {
        Log.e(logTag, "START!");
        Context mContext = activity;
        if (mCurrentDevice != null) {
            DataStorage _dataStorage = mDataStorage;
            int hw_lfd_level = 0;
            int sw_lfd_level = 0;
            int result = -1;

            if (_dataStorage.containKey(mContext, _dataStorage.getSecurityKey()) == false) {
                if (mUsbDevice.getProductId() == BioMiniSlim2S) {
                    int security_level = (int) mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.SECURITY_LEVEL).value;
                    _dataStorage.setSecurityParam(security_level);
                    result = setParameterToDevice(_dataStorage.getSecurityKey(), _dataStorage.getSecurityParam(), true);
                } else {
                    result = setParameterToDevice(_dataStorage.getSecurityKey(), _dataStorage.DEFAULT_SECURITY_VALUE, true);
                }
                if (result != 0) {
//                    mSettingFragment.setSensitivityPref(false);
                }
            } else {
                setParameterToDevice(_dataStorage.getSecurityKey(), _dataStorage.getSecurityParam(), true);
            }

            if (_dataStorage.containKey(mContext, _dataStorage.getSensitivityKey()) == false) {
                if (mUsbDevice.getProductId() == BioMiniSlim2S) {
                    int sensitivity_level = (int) mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.SENSITIVITY).value;
                    _dataStorage.setSensitivityPram(sensitivity_level);
                    setParameterToDevice(_dataStorage.getSensitivityKey(), _dataStorage.getSensitivityPram(), true);
                } else {
                    setParameterToDevice(_dataStorage.getSensitivityKey(), _dataStorage.DEFAULT_SENSITIVITY_VALUE, true);
                }
                if (result != 0) {
//                    mSettingFragment.setSecurityPref(false);
                }
            } else {
                setParameterToDevice(_dataStorage.getSensitivityKey(), _dataStorage.getSensitivityPram(), true);
            }

            if (_dataStorage.containKey(mContext, _dataStorage.getTimeoutKey()) == false) {
                if (mUsbDevice.getProductId() == BioMiniSlim2S) {
                    int timeout = (int) mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.TIMEOUT).value;
                    _dataStorage.setTimeoutParam(timeout / 1000);
                    setParameterToDevice(_dataStorage.getTimeoutKey(), _dataStorage.getTimeoutParam(), true);
                } else
                    setParameterToDevice(_dataStorage.getTimeoutKey(), _dataStorage.DEFAULT_TIMEOUT_VALUE, true);

                if (result != 0) {
//                    mSettingFragment.setTimeOutPref(false);
                }
            } else {
                setParameterToDevice(_dataStorage.getTimeoutKey(), _dataStorage.getTimeoutParam(), true);
            }

            if (_dataStorage.containKey(mContext, _dataStorage.getLfdWithDeviceKey()) == false) {
                if (mUsbDevice.getProductId() == BioMiniSlim2S) {
                    hw_lfd_level = (int) mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.DETECT_FAKE_HW).value;
                    _dataStorage.setLfdWithDeviceParam(hw_lfd_level);
                    setParameterToDevice(_dataStorage.getLfdWithDeviceKey(), _dataStorage.getLfdWithDeviceParam(), true);
                } else
                    result = setParameterToDevice(_dataStorage.getLfdWithDeviceKey(), _dataStorage.DEFAULT_LFD_WITH_DEVICE_VALUE, true);

                if (result != 0) {
//                    mSettingFragment.setHwLfdPref(false);
                }
            } else {
                setParameterToDevice(_dataStorage.getLfdWithDeviceKey(), _dataStorage.getLfdWithDeviceParam(), true);
            }

            if (_dataStorage.containKey(mContext, _dataStorage.getLfdWithSdkKey()) == false) {
                if (mUsbDevice.getProductId() == BioMiniSlim2S) {
                    sw_lfd_level = (int) mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.DETECT_FAKE_SW).value;
                    _dataStorage.setLfdWithSdkParam(sw_lfd_level);
                    setParameterToDevice(_dataStorage.getLfdWithSdkKey(), _dataStorage.getLfdWithSdkParam(), true);
                } else {
                    result = setParameterToDevice(_dataStorage.getLfdWithSdkKey(), _dataStorage.DEFAULT_LFD_WITH_SDK_VALUE, true);
                }

                if (result != 0) {
//                    mSettingFragment.setSwLfdPref(false);
                }
            } else {
                setParameterToDevice(_dataStorage.getLfdWithSdkKey(), _dataStorage.getLfdWithSdkParam(), true);
            }
            if (_dataStorage.containKey(mContext, _dataStorage.getFastModeKey()) == false) {
                boolean fast_mode = mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.FAST_MODE).value == 1;
                _dataStorage.setFastModeParam(fast_mode);
            } else {
                boolean fast_mode = _dataStorage.getFastModeParam();
                setParameterToDevice(_dataStorage.getFastModeKey(), fast_mode == true ? 1 : 0, true);
            }

            if (_dataStorage.containKey(mContext, _dataStorage.getCropModeKey()) == false) {
                Log.e(logTag, "this key is not used.");
                boolean crop_mode = false;
                crop_mode = mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.SCANNING_MODE).value == 1;
                result = setParameterToDevice(_dataStorage.getCropModeKey(), crop_mode == true ? 1 : 0, true);
                if (result != 0) {
//                    mSettingFragment.setCropModePref(false);
                } else {
                    _dataStorage.setCropModeParam(crop_mode);
                }
                Log.e(logTag, "crop_mode: " + crop_mode);
            } else {
                Log.e(logTag, "this key is used.");
                boolean crop_mode = _dataStorage.getCropModeParam();
                setParameterToDevice(_dataStorage.getCropModeKey(), crop_mode == true ? 1 : 0, true);
            }

            if (_dataStorage.containKey(mContext, _dataStorage.getExtTriggerKey()) == false) {
                boolean ext_trigger = false;
                if (mUsbDevice.getProductId() == BioMiniSlim2S) {
                    ext_trigger = mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.EXT_TRIGGER).value == 1;
                    _dataStorage.setExtTriggerParam(ext_trigger);
                    setParameterToDevice(_dataStorage.getExtTriggerKey(), ext_trigger == true ? 1 : 0, true);
                } else {
                    setParameterToDevice(_dataStorage.getExtTriggerKey(), _dataStorage.DEFAULT_EXT_TRIGGER_VALUE == true ? 1 : 0, true);
                }
            } else {
                setParameterToDevice(_dataStorage.getExtTriggerKey(), _dataStorage.getExtTriggerParam() == true ? 1 : 0, true);
            }

            boolean auto_sleep = mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.ENABLE_AUTOSLEEP).value == 1;
            _dataStorage.setAutoSleepParam(auto_sleep);
            result = setParameterToDevice(_dataStorage.getAutoSleepKey(), _dataStorage.getAutoSleepParam() == true ? 1 : 0, true);
            if (result != 0) {
//                mSettingFragment.setAutoSleepPref(false);
            }

            boolean image_flip_180d = _dataStorage.getImageFlip180dParam();
            result = setParameterToDevice(_dataStorage.getImageFlip180dKey(), image_flip_180d == true ? 1 : 0, true);
            if (result != 0) {
//                mSettingFragment.setImageFlip180dPref(false);
            }

            boolean omnidir_verify = _dataStorage.getOmniDirVerifyParam();
            result = setParameterToDevice(_dataStorage.getOmniDirVerifyKey(), omnidir_verify == true ? 1 : 0, true);
            if (result != 0) {
//                mSettingFragment.setOmniDirVerifyPref(false);
            }

            boolean detect_core = _dataStorage.getDetectCoreParam();
            result = setParameterToDevice(_dataStorage.getDetectCoreKey(), detect_core == true ? 1 : 0, true);
            if (result != 0) {
//                mSettingFragment.setDetectCorePref(false);
            }

            boolean template_qualityEx = _dataStorage.getTemplateQualityExParam();
            result = setParameterToDevice(_dataStorage.getTemplateQualityExKey(), template_qualityEx == true ? 1 : 0, true);
            if (result != 0) {
//                mSettingFragment.setTemplateQualityExPref(false);
            }
        }
//        mSettingFragment.mIsParamInitiated = true;
//        mSettingFragment.mHandler.sendEmptyMessage(mSettingFragment.EVENT_SCREEN_UPDATE);
    }

    public int setParameterToDevice(String paramName, int value, boolean bInit) {
        int result = -1;
        Log.e(logTag, "paramName : " + paramName + " value : " + value);
        switch (paramName) {
            case "pref_sensitivity":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.SENSITIVITY, value));
                break;
            case "pref_security":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.SECURITY_LEVEL, value));
                break;
            case "pref_timeout":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.TIMEOUT, value * 1000));
                break;
            case "pref_hwlfd":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.DETECT_FAKE_HW, value));
                break;
            case "pref_swlfd":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.DETECT_FAKE_SW, value));
                break;
            case "pref_fastmode":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.FAST_MODE, value));

                break;
            case "pref_cropmode":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.SCANNING_MODE, value));
                break;
            case "pref_exttriger":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.EXT_TRIGGER, value));
                break;
            case "pref_autosleep":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.ENABLE_AUTOSLEEP, value));
                break;
            case "pref_manualsleep":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.ENABLE_MANUAL_SLEEP, value));
                break;
            case "pref_imageflip180d":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.IMAGE_FLIP, value));
                break;
            case "pref_omniDirVerify":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.AUTO_ROTATE, value));
                break;
            case "pref_detectcore":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.DETECT_CORE, value));
//                mDetect_core = value;
                break;
            case "pref_templatequalityex":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.TEMPLATE_QUALITY_EX, value));
//                mTemplateQualityEx = value;
                break;
            case "pref_exporttemplatetype":
                Log.e(logTag, "setTemplateType : " + value);
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.TEMPLATE_TYPE, value));
                break;
        }
        Log.e(logTag, "result : " + result);
        if (bInit == false) {
            if (result == 0) {
                Log.e(logTag, paramName.substring(5) + " parameter was successfully set.");
            } else {
                Log.e(logTag, paramName.substring(5) + " parameter was not set due to " + IBioMiniDevice.ErrorCode.fromInt(result));
                interf.on_device_error(paramName.substring(5) + " parameter was not set due to " + IBioMiniDevice.ErrorCode.fromInt(result));
            }
        }
        return result;
    }

    private IBioMiniDevice.CaptureOption mCaptureOption = new IBioMiniDevice.CaptureOption();
    private IBioMiniDevice.TemplateData mTemplateData;

    @Override
    public void capture() {
        super.capture();
        doCapture(IBioMiniDevice.CaptureFuntion.CAPTURE_SINGLE);
    }
    private void doAutoCapture() {
        doCapture(IBioMiniDevice.CaptureFuntion.CAPTURE_SINGLE);
    }
    private void doCapture(IBioMiniDevice.CaptureFuntion captureFuntion) {
        mTemplateData = null;
        mCaptureOption.captureFuntion = captureFuntion;
        mCaptureOption.extractParam.captureTemplate = true;
        if(mCurrentDevice != null) {
            if(captureFuntion==IBioMiniDevice.CaptureFuntion.CAPTURE_SINGLE||captureFuntion==IBioMiniDevice.CaptureFuntion.ENROLLMENT){
                boolean result = mCurrentDevice.captureSingle(
                        mCaptureOption,
                        mCaptureCallBack,
                        true);

            }else{
                int result = mCurrentDevice.captureAuto(mCaptureOption, mCaptureCallBack);

                if (result == IBioMiniDevice.ErrorCode.ERR_NOT_SUPPORTED.value()) {
                    Log.e(logTag, "This device does not support the capture function!");
                    interf.on_device_error("This device does not support "+captureFuntion.toString());
                }
            }


        }
    }

    private void doAutoCapture_() {
        mTemplateData = null;
        mCaptureOption.extractParam.captureTemplate = true;
        mCaptureOption.captureFuntion = IBioMiniDevice.CaptureFuntion.CAPTURE_AUTO;

        Log.e(logTag, "Capture started");
        if (mCurrentDevice != null) {
            int result = mCurrentDevice.captureAuto(mCaptureOption, mCaptureCallBack);
            if (result == IBioMiniDevice.ErrorCode.ERR_NOT_SUPPORTED.value()) {
                Log.e(logTag, "This device does not support auto Capture!");
                interf.on_device_error("This device does not support auto Capture!");
            }
        }
    }

    //    mCurrentDevice.verify(    mTemplateData.data, mTemplateData.data.length, ud.template, ud.template.length)//matching
    CaptureResponder mCaptureCallBack = new CaptureResponder() {
        @Override
        public void onCapture(Object context, IBioMiniDevice.FingerState fingerState) {
            super.onCapture(context, fingerState);
        }

        @Override
        public boolean onCaptureEx(Object context, IBioMiniDevice.CaptureOption option, final Bitmap capturedImage, IBioMiniDevice.TemplateData capturedTemplate, IBioMiniDevice.FingerState fingerState) {

            Log.e(logTag, "START! : " + mCaptureOption.captureFuntion.toString());

            if (capturedTemplate != null) {
                Log.e(logTag, "TemplateData is not null!");
                mTemplateData = capturedTemplate;

                Log.e(logTag, "check additional capture result.");
                if (mCurrentDevice != null && mCurrentDevice.getLfdLevel() > 0) {
                    Log.e(logTag, "LFD SCORE : " + mCurrentDevice.getLfdScoreFromCapture());
                }
                if (true) {
                    int[] _coord = mCurrentDevice.getCoreCoordinate();
                    Log.e(logTag, "Core Coordinate X : " + _coord[0] + " Y : " + _coord[1]);
                    int _templateQualityExValue = mCurrentDevice.getTemplateQualityExValue();
                    Log.e(logTag, "template Quality : " + _templateQualityExValue);
                }
            }

            //fpquality example
            if (mCurrentDevice != null) {
                byte[] imageData = mCurrentDevice.getCaptureImageAsRAW_8();
                if (imageData != null) {
                    IBioMiniDevice.FpQualityMode mode = IBioMiniDevice.FpQualityMode.NQS_MODE_DEFAULT;
                    int _fpquality = mCurrentDevice.getFPQuality(imageData, mCurrentDevice.getImageWidth(), mCurrentDevice.getImageHeight(), mode.value());
                    Log.e(logTag, "_fpquality : " + _fpquality);
                }
            }

            interf.on_result_image_obtained(capturedImage);
            interf.on_result_wsq_obtained(imageToWsq(capturedImage));

            interf.on_result_obtained(imageToIso(capturedImage));
            return true;

        }

        @Override
        public void onCaptureError(Object context, int errorCode, String error) {
            if (errorCode == IBioMiniDevice.ErrorCode.CTRL_ERR_IS_CAPTURING.value()) {
                Log.e(logTag, "Other capture function is running. abort capture function first!");
            } else if (errorCode == IBioMiniDevice.ErrorCode.CTRL_ERR_CAPTURE_ABORTED.value()) {
                Log.e(logTag, "CTRL_ERR_CAPTURE_ABORTED occured.");
            } else if (errorCode == IBioMiniDevice.ErrorCode.CTRL_ERR_FAKE_FINGER.value()) {
                Log.e(logTag, "Fake Finger Detected");
                if (mCurrentDevice != null && mCurrentDevice.getLfdLevel() > 0) {
                    Log.e(logTag, "LFD SCORE : " + mCurrentDevice.getLfdScoreFromCapture());
                }
            } else {
                Log.e(logTag, "Capture Sinlge is fail by " + error + ".Please try again.");
            }
        }
    };

    @Override
    public void stop() {
        super.stop();
        closeDevice();
    }

    boolean connected = false;
    boolean busy = false;
    boolean vc = true;

    void connect() {
        if (!connected) {


        }

    }


    public void closeDevice() {
        removeDevice();
        activity.unregisterReceiver(mUsbReceiver);
        mUsbDevice = null;
        mCurrentDevice = null;
        FingerPrint.fingerPrintPower(0);
    }

    boolean isConnected() {
        return connected;
    }

    boolean encrypted = false;


}
