package sparta.realm.spartautils.biometrics.fp;

import static android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.biominiseries.BioMiniFactory;
import com.biominiseries.CaptureResponder;
import com.biominiseries.IBioMiniDevice;
import com.biominiseries.IUsbEventHandler;
import com.biominiseries.enums.DeviceDataHandler;
import com.biominiseries.util.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Biominiv3_0_3_3FingerprintManager extends FingerprintManger{
    private IBioMiniDevice.CaptureOption mCaptureOption = new IBioMiniDevice.CaptureOption();
    private IBioMiniDevice.TemplateData mTemplateData;
    long mCaptureStartTime = 0;
    private BioMiniFactory mBioMiniFactory;
    public IBioMiniDevice mCurrentDevice;
    private UsbManager mUsbManager;
    public UsbDevice mUsbDevice;
    private PendingIntent mPermissionIntent;
    public DeviceDataHandler mDeviceDataHandler;

    public Biominiv3_0_3_3FingerprintManager(Activity activity) {
        super(activity);
        mDeviceDataHandler = DeviceDataHandler.getInstance();
        mDeviceDataHandler.setSupportLowCpuDeviceMode(true);

        if(mUsbManager == null)
            mUsbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);

        initUsbListener(activity);
        addDeviceToUsbDeviceList();
    }

    private void initUsbListener(Context mContext) {
        Logger.d("start!");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            mContext.registerReceiver(mUsbReceiver, new IntentFilter(ACTION_USB_PERMISSION), Context.RECEIVER_EXPORTED | Context.RECEIVER_VISIBLE_TO_INSTANT_APPS);
            mContext.registerReceiver(mUsbReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED), Context.RECEIVER_EXPORTED);
            mContext.registerReceiver(mUsbReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED), Context.RECEIVER_EXPORTED);
        } else {
            mContext.registerReceiver(mUsbReceiver, new IntentFilter(ACTION_USB_PERMISSION));
            mContext.registerReceiver(mUsbReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED));
            mContext.registerReceiver(mUsbReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));
        }
    }
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action)
            {
                case ACTION_USB_PERMISSION:
                    Logger.d("ACTION_USB_PERMISSION");
                    boolean hasUsbPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,false);
                    Logger.d("haspermission = " + hasUsbPermission);
                    if(hasUsbPermission && mUsbDevice != null)
                    {
                        Logger.d(mUsbDevice.getDeviceName() + " is acquire the usb permission. activate this device.");
                        sendEmptyMsgToHandler(ACTIVATE_USB_DEVICE);
                    }
                    else
                    {
                        Logger.d("USB permission is not granted!");
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    Logger.d("ACTION_USB_DEVICE_ATTACHED");
                    addDeviceToUsbDeviceList();
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    Logger.d("ACTION_USB_DEVICE_DETACHED");
                 Log.e(logTag,"usb_detached");
//                    mViewPager.setCurrentItem(0);
                    if(mCurrentDevice != null) {
                        if (mCurrentDevice.isCapturing()) {
//                            doAbortCapture();
                        }
                    }
                    removeDevice();
                    break;
                default:
                    break;
            }
        }
    };

    private void createBioMiniDevice() {
        Logger.d("START!");
        if(mUsbDevice == null) {
                 Log.e(logTag,"error_device_not_conneted");
            return;
        }
        if(mBioMiniFactory != null) {
            mBioMiniFactory.close();
        }
        mDataStorage = new DataStorage();
        mDataStorage.init(activity);
        Logger.d("new BioMiniFactory( )");
        mBioMiniFactory = new BioMiniFactory(activity,mUsbManager) { //for android sample
            @Override
            public void onDeviceChange(DeviceChangeEvent event, Object dev) {
                Logger.d("onDeviceChange : " + event);
                handleDevChange(event, dev);
            }
        };
        Logger.d("new BioMiniFactory( ) : " + mBioMiniFactory);
        boolean _transferMode = mDataStorage.getUseNativeUsbModeParam();
        Logger.d("_transferMode : " + _transferMode);
        setTransferMode(_transferMode,false);

        boolean _result = mBioMiniFactory.addDevice(mUsbDevice);

        if(_result == true) {
            mCurrentDevice = mBioMiniFactory.getDevice(0);
            if(mCurrentDevice != null) {
                     Log.e(logTag,"device_attached");
                Logger.d("mCurrentDevice attached : " + mCurrentDevice);
                if (mCurrentDevice != null){
                    Log.e(logTag,"deviceName:"+mCurrentDevice.getDeviceInfo().deviceName
                            +"\ndeviceSN:"+mCurrentDevice.getDeviceInfo().deviceSN
                            +"\nversionFW:"+mCurrentDevice.getDeviceInfo().versionFW
                            +"\nSdkVersionInfo:"+mBioMiniFactory.getSdkVersionInfo());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestWritePermission();
                    }

                    getDefaultParameterFromDevice();

doAutoCapture();
                }

            }else{
                Logger.d("mCurrentDevice is null");
            }

        }else{
            Logger.d("addDevice is fail!");
        }
        //mBioMiniFactory.setTransferMode(IBioMiniDevice.TransferMode.MODE2);
    }

    private void handleDevChange(IUsbEventHandler.DeviceChangeEvent event, Object dev) {
        Logger.d("START!");
    }
    String[] PERMISSIONS = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    String[] PERMISSIONS_33 = new String[] {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO};
    private static final int PERMISSION_ALL = 786;

    private boolean hasPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission: permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false;
                }
            }
        }
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestWritePermission() {
        Logger.d("start!");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!hasPermissions(activity, PERMISSIONS_33)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.requestPermissions((String[]) PERMISSIONS_33, PERMISSION_ALL);
                }
            }else{
                Logger.d("WRITE_EXTERNAL_STORAGE permission already granted!");
                requestBatteryOptimization();
            }
        }else{
            if (!hasPermissions(activity, PERMISSIONS)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.requestPermissions((String[]) PERMISSIONS, PERMISSION_ALL);
                }else{
                    Logger.d("WRITE_EXTERNAL_STORAGE permission already granted!");
                    requestBatteryOptimization();
                }
            }
        }
        if (Build.VERSION.SDK_INT >= 30){
            if (!Environment.isExternalStorageManager()){
                Intent getPermissionIntent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package",activity.getPackageName(), null);
                getPermissionIntent.setData(uri);
                if (getPermissionIntent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivity(getPermissionIntent);
                }
            }
        }
    }
    public static final String POWER_SERVICE = "power";

    private void requestBatteryOptimization() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = activity.getPackageName();
            PowerManager pm = (PowerManager) activity.getSystemService(activity.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                activity.startActivity(intent);
            }
        }
    }
    DataStorage mDataStorage = new DataStorage();
    private static final int BioMiniOC4 = 0x0406;
    private static final int BioMiniSlim = 0x0407;
    private static final int BioMiniSlim2 = 0x0408;
    private static final int BioMiniSlim2RK = 0x0462;
    private static final int BioMiniPlus2 = 0x0409;
    private static final int BioMiniSlimS = 0x0420;
    private static final int BioMiniSlim2S = 0x0421;
    private static final int BioMiniSlim3 = 0x0460;
    private static final int BioMiniSlim3HID = 0x0423;
    private void getDefaultParameterFromDevice() {
        Logger.d("START!");
        if(mCurrentDevice != null) {
            DataStorage _dataStorage = mDataStorage;
            int hw_lfd_level = 0;
            int sw_lfd_level = 0;
            int result= -1;
Context mContext=activity;
            if(_dataStorage.containKey(mContext,_dataStorage.getSecurityKey()) == false)
            {
                if(mUsbDevice.getProductId() == BioMiniSlim2S)
                {
                    int security_level = (int) mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.SECURITY_LEVEL).value;
                    _dataStorage.setSecurityParam(security_level);
                    result = setParameterToDevice(_dataStorage.getSecurityKey(),_dataStorage.getSecurityParam(),true);
                }
                else
                {
                    result = setParameterToDevice(_dataStorage.getSecurityKey(),_dataStorage.DEFAULT_SECURITY_VALUE,true);
                }
                if(result != 0)
                {
//                    mSettingFragment.setSensitivityPref(false);
                }
            }
            else
            {
                setParameterToDevice(_dataStorage.getSecurityKey(),_dataStorage.getSecurityParam(),true);
            }

            if(_dataStorage.containKey(mContext,_dataStorage.getSensitivityKey()) == false)
            {
                if(mUsbDevice.getProductId() == BioMiniSlim2S) {
                    int sensitivity_level = (int) mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.SENSITIVITY).value;
                    _dataStorage.setSensitivityPram(sensitivity_level);
                    setParameterToDevice(_dataStorage.getSensitivityKey(),_dataStorage.getSensitivityPram(),true);
                }
                else
                {
                    setParameterToDevice(_dataStorage.getSensitivityKey(),_dataStorage.DEFAULT_SENSITIVITY_VALUE,true);
                }
                if(result != 0)
                {
//                    mSettingFragment.setSecurityPref(false);
                }
            }
            else
            {
                setParameterToDevice(_dataStorage.getSensitivityKey(),_dataStorage.getSensitivityPram(),true);
            }

            if(_dataStorage.containKey(mContext,_dataStorage.getTimeoutKey()) == false)
            {
                if(mUsbDevice.getProductId() == BioMiniSlim2S)
                {
                    int timeout = (int) mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.TIMEOUT).value;
                    _dataStorage.setTimeoutParam(timeout/1000);
                    setParameterToDevice(_dataStorage.getTimeoutKey(),_dataStorage.getTimeoutParam(),true);
                }
                else
                    setParameterToDevice(_dataStorage.getTimeoutKey(),_dataStorage.DEFAULT_TIMEOUT_VALUE,true);

                if(result != 0)
                {
//                    mSettingFragment.setTimeOutPref(false);
                }
            }
            else
            {
                setParameterToDevice(_dataStorage.getTimeoutKey(),_dataStorage.getTimeoutParam(),true);
            }

            if(_dataStorage.containKey(mContext,_dataStorage.getLfdWithDeviceKey()) == false)
            {
                if(mUsbDevice.getProductId() == BioMiniSlim2S)
                {
                    hw_lfd_level = (int) mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.DETECT_FAKE_HW).value;
                    _dataStorage.setLfdWithDeviceParam(hw_lfd_level);
                    setParameterToDevice(_dataStorage.getLfdWithDeviceKey(),_dataStorage.getLfdWithDeviceParam(),true);
                }
                else
                    result = setParameterToDevice(_dataStorage.getLfdWithDeviceKey(),_dataStorage.DEFAULT_LFD_WITH_DEVICE_VALUE,true);

                if(result != 0)
                {
//                    mSettingFragment.setHwLfdPref(false);
                }
            }
            else
            {
                setParameterToDevice(_dataStorage.getLfdWithDeviceKey(),_dataStorage.getLfdWithDeviceParam(),true);
            }

            if(_dataStorage.containKey(mContext,_dataStorage.getLfdWithSdkKey()) == false)
            {
                if(mUsbDevice.getProductId() == BioMiniSlim2S)
                {
                    sw_lfd_level = (int) mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.DETECT_FAKE_SW).value;
                    _dataStorage.setLfdWithSdkParam(sw_lfd_level);
                    setParameterToDevice(_dataStorage.getLfdWithSdkKey(),_dataStorage.getLfdWithSdkParam(),true);
                }
                else
                {
                    result = setParameterToDevice(_dataStorage.getLfdWithSdkKey(),_dataStorage.DEFAULT_LFD_WITH_SDK_VALUE,true);
                }

                if(result != 0)
                {
//                    mSettingFragment.setSwLfdPref(false);
                }
            }
            else
            {
                setParameterToDevice(_dataStorage.getLfdWithSdkKey(),_dataStorage.getLfdWithSdkParam(),true);
            }
            if(_dataStorage.containKey(mContext,_dataStorage.getFastModeKey()) == false)
            {
                boolean fast_mode = mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.FAST_MODE).value == 1;
                _dataStorage.setFastModeParam(fast_mode);
            }
            else
            {
                boolean fast_mode = _dataStorage.getFastModeParam();
                setParameterToDevice(_dataStorage.getFastModeKey() ,fast_mode == true ? 1: 0, true);
            }

            if(_dataStorage.containKey(mContext,_dataStorage.getCropModeKey()) == false)
            {
                Logger.d("this key is not used.");
                boolean crop_mode = false;
                crop_mode = mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.SCANNING_MODE).value == 1;
                result = setParameterToDevice(_dataStorage.getCropModeKey() ,crop_mode == true ? 1: 0, true);
                if(result != 0)
                {
//                    mSettingFragment.setCropModePref(false);
                }
                else
                {
                    _dataStorage.setCropModeParam(crop_mode);
                }
                Logger.d("crop_mode: "+ crop_mode);
            }
            else
            {
                Logger.d("this key is used.");
                boolean crop_mode = _dataStorage.getCropModeParam();
                setParameterToDevice(_dataStorage.getCropModeKey() ,crop_mode == true ? 1: 0, true);
            }

            if(_dataStorage.containKey(mContext,_dataStorage.getExtTriggerKey()) == false)
            {
                boolean ext_trigger = false;
                if(mUsbDevice.getProductId() == BioMiniSlim2S)
                {
                    ext_trigger  = mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.EXT_TRIGGER).value == 1;
                    _dataStorage.setExtTriggerParam(ext_trigger);
                    setParameterToDevice(_dataStorage.getExtTriggerKey() ,ext_trigger == true ? 1: 0, true);
                }
                else
                {
                    setParameterToDevice(_dataStorage.getExtTriggerKey() ,_dataStorage.DEFAULT_EXT_TRIGGER_VALUE == true ? 1: 0, true);
                }
            }
            else
            {
                setParameterToDevice(_dataStorage.getExtTriggerKey() ,_dataStorage.getExtTriggerParam() == true ? 1: 0, true);
            }

            boolean auto_sleep = mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.ENABLE_AUTOSLEEP).value == 1;
            _dataStorage.setAutoSleepParam(auto_sleep);
            result = setParameterToDevice(_dataStorage.getAutoSleepKey(),_dataStorage.getAutoSleepParam() == true? 1: 0 , true);
            if(result != 0)
            {
//                mSettingFragment.setAutoSleepPref(false);
            }

            boolean image_flip_180d = _dataStorage.getImageFlip180dParam();
            result = setParameterToDevice(_dataStorage.getImageFlip180dKey() ,image_flip_180d == true ? 1: 0, true);
            if(result != 0)
            {
//                mSettingFragment.setImageFlip180dPref(false);
            }

            boolean omnidir_verify = _dataStorage.getOmniDirVerifyParam();
            result = setParameterToDevice(_dataStorage.getOmniDirVerifyKey() ,omnidir_verify == true ? 1: 0, true);
            if(result != 0) {
//                mSettingFragment.setOmniDirVerifyPref(false);
            }

            boolean detect_core = _dataStorage.getDetectCoreParam();
            result = setParameterToDevice(_dataStorage.getDetectCoreKey() ,detect_core == true ? 1: 0, true);
            if(result != 0) {
//                mSettingFragment.setDetectCorePref(false);
            }

            boolean template_qualityEx = _dataStorage.getTemplateQualityExParam();
            result = setParameterToDevice(_dataStorage.getTemplateQualityExKey() ,template_qualityEx == true ? 1: 0, true);
            if(result != 0) {
//                mSettingFragment.setTemplateQualityExPref(false);
            }
            setTransferMode(_dataStorage.getUseNativeUsbModeParam(),false);
        }
//        mSettingFragment.mIsParamInitiated = true;
//        mSettingFragment.mHandler.sendEmptyMessage(mSettingFragment.EVENT_SCREEN_UPDATE);
    }
    private IBioMiniDevice.TransferMode mTransferMode;
    public int setTransferMode(boolean _value, boolean _prefchanged) {
        Logger.d("setTransferMode : " + _value + " _prefchanged : " + _prefchanged);
        int result = 0;
        if(_value == true) {
            mTransferMode = IBioMiniDevice.TransferMode.MODE2;
        }else{
            mTransferMode = IBioMiniDevice.TransferMode.MODE1;
        }
        result = mBioMiniFactory.setTransferMode(mTransferMode);
        if(result != IBioMiniDevice.ErrorCode.OK.value()){
            if(result == IBioMiniDevice.ErrorCode.ERR_NOT_SUPPORTED.value()){
//                mSettingFragment.setUseNativeUsbModePref(false);
            }
            return IBioMiniDevice.ErrorCode.ERR_NOT_SUPPORTED.value();
        }

        if(_prefchanged){
//            recreate();
        }

        return result;
    }
    public int setParameterToDevice(String paramName, int value, boolean bInit)
    {
        int result = -1;
        Logger.d("paramName : " + paramName + " value : " + value);
        switch(paramName)
        {
            case "pref_sensitivity":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.SENSITIVITY, value));
                break;
            case "pref_security":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.SECURITY_LEVEL, value));
                break;
            case "pref_timeout":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.TIMEOUT, value*1000));
                break;
            case "pref_hwlfd":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.DETECT_FAKE_HW, value));
                break;
            case "pref_swlfd":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.DETECT_FAKE_SW, value));
                break;
            case "pref_fastmode":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.FAST_MODE, value));
                if(result != IBioMiniDevice.ErrorCode.OK.value()) {
//                    mSettingFragment.setFastModePref(false);
                }
                break;
            case "pref_cropmode":
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.SCANNING_MODE, value));
                if(result != IBioMiniDevice.ErrorCode.OK.value()) {
//                    mSettingFragment.setCropModePref(false);
                }
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
                Logger.d("setTemplateType : " + value);
                result = mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.TEMPLATE_TYPE, value));
                break;
        }
        Logger.d("result : " + result);
        if(bInit == false)
        {
            if(result == 0 )
            {
                     Log.e(logTag,paramName.substring(5) + " parameter was successfully set.");
            }
            else
            {
                     Log.e(logTag,paramName.substring(5)  + " parameter was not set due to " + IBioMiniDevice.ErrorCode.fromInt(result));
            }
        }
        return result;
    }

    @Override
    public void capture() {
        super.capture();
        doSinlgeCapture();
    }

    private void doSinlgeCapture() {
        Logger.d("START!");
        mCaptureStartTime = System.currentTimeMillis();
        mTemplateData = null;
        mCaptureOption.captureFuntion = IBioMiniDevice.CaptureFuntion.CAPTURE_SINGLE;
        mCaptureOption.extractParam.captureTemplate = true;
        mCaptureOption.extractParam.maxTemplateSize = IBioMiniDevice.MaxTemplateSize.MAX_TEMPLATE_1024;

        if (mCurrentDevice != null) {
            boolean result = mCurrentDevice.captureSingle(
                    mCaptureOption,
                    mCaptureCallBack,
                    true);
        }
    }



    private void doAutoCapture() {
        Logger.d("buttonCaptureAuto clicked");
        mTemplateData = null;
        mCaptureOption.extractParam.captureTemplate = true;
        mCaptureOption.captureFuntion = IBioMiniDevice.CaptureFuntion.CAPTURE_AUTO;
        mCaptureOption.extractParam.maxTemplateSize = IBioMiniDevice.MaxTemplateSize.MAX_TEMPLATE_1024;


        mCaptureOption.frameRate = IBioMiniDevice.FrameRate.LOW;
             Log.e(logTag,"Capturing ...");
        if (mCurrentDevice != null) {
            int result = mCurrentDevice.captureAuto(mCaptureOption, mCaptureCallBack);
            if (result == IBioMiniDevice.ErrorCode.ERR_NOT_SUPPORTED.value()) {
                     Log.e(logTag,"This device does not support auto Capture!");
            }
        }
    }

    static String logTag = "Biominiv3_0_3_3FingerprintManager";
    private int[] _coord;

    CaptureResponder mCaptureCallBack = new CaptureResponder() {
        @Override
        public void onCapture(Object context, IBioMiniDevice.FingerState fingerState) {
            super.onCapture(context, fingerState);
        }

        @Override
        public boolean onCaptureEx(Object context, IBioMiniDevice.CaptureOption option, final Bitmap capturedImage, IBioMiniDevice.TemplateData capturedTemplate, IBioMiniDevice.FingerState fingerState) {

            Logger.d("START! : " + mCaptureOption.captureFuntion.toString());

            if (capturedTemplate != null) {
                Logger.d("TemplateData is not null!");
                mTemplateData = capturedTemplate;
            }

            if (option.captureFuntion == IBioMiniDevice.CaptureFuntion.ENROLLMENT && mTemplateData != null) {
                Logger.d("register user template data.");
//                boolean result = mUsers.add(new UserData(mUserName, mTemplateData.data, mTemplateData.data.length));
//                if(result) {
//                    setLogInTextView(getResources().getString(R.string.enroll_ok) + " for User : " + mUserName);
//                }else {
//                    setLogInTextView(mUserName + " " + getResources().getString(R.string.enroll_fail) + " for ID : " + mUserName);
//                }
            }


            if (capturedTemplate != null) {
                Logger.d("check additional capture result.");
                if (mCurrentDevice != null && mCurrentDevice.getLfdLevel() > 0) {
                    Log.e(logTag, "LFD SCORE : " + mCurrentDevice.getLfdScoreFromCapture());
                }
                _coord = mCurrentDevice.getCoreCoordinate();
                Log.e(logTag, "Core Coordinate X : " + _coord[0] + " Y : " + _coord[1]);
                int _templateQualityExValue = mCurrentDevice.getTemplateQualityExValue();
                Log.e(logTag, "template Quality : " + _templateQualityExValue);

            }

            //fpquality example
            if (mCurrentDevice != null) {
                byte[] imageData = mCurrentDevice.getCaptureImageAsRAW_8();
                if (imageData != null) {
                    IBioMiniDevice.FpQualityMode mode = IBioMiniDevice.FpQualityMode.NQS_MODE_DEFAULT;
                    int _fpquality = mCurrentDevice.getFPQuality(imageData, mCurrentDevice.getImageWidth(), mCurrentDevice.getImageHeight(), mode.value());
                    Logger.d("_fpquality : " + _fpquality);
                    Log.e(logTag, "_fpquality : " + _fpquality);
                }
            }

            if (option.captureFuntion == IBioMiniDevice.CaptureFuntion.CAPTURE_SINGLE) {
                Log.e(logTag, "capture_single_ok");
            }

            if (option.captureFuntion == IBioMiniDevice.CaptureFuntion.CAPTURE_AUTO) {
                Log.e(logTag, "capture_auto_ok");
            }

            if (capturedImage != null) {
                Bundle extrasBundle = new Bundle();
                extrasBundle.putParcelable("capturedImage", capturedImage);
                sendMsgToHandler(SHOW_CAPTURE_IMAGE_DEVICE, extrasBundle);
            }

            if (option.captureFuntion == IBioMiniDevice.CaptureFuntion.CAPTURE_SINGLE ||
                    option.captureFuntion == IBioMiniDevice.CaptureFuntion.ENROLLMENT ||
                    option.captureFuntion == IBioMiniDevice.CaptureFuntion.VERIFY) {
                Logger.d("set ui event is available.");

            }
            return true;
        }

        @Override
        public void onCaptureError(Object context, int errorCode, String error) {
            if (errorCode == IBioMiniDevice.ErrorCode.CTRL_ERR_IS_CAPTURING.value()) {
                Log.e(logTag,"Other capture function is running. abort capture function first!");
            } else if (errorCode == IBioMiniDevice.ErrorCode.CTRL_ERR_CAPTURE_ABORTED.value()) {
                Logger.d("CTRL_ERR_CAPTURE_ABORTED occured.");
            } else if (errorCode == IBioMiniDevice.ErrorCode.CTRL_ERR_FAKE_FINGER.value()) {
                     Log.e(logTag,"Fake Finger Detected");
                if (mCurrentDevice != null && mCurrentDevice.getLfdLevel() > 0) {
                         Log.e(logTag,"LFD SCORE : " + mCurrentDevice.getLfdScoreFromCapture());
                }
            } else {
                     Log.e(logTag,mCaptureOption.captureFuntion.name() + " is fail by " + error);
                     Log.e(logTag,"Please try again.");
            }

        }
    };
    private void sendMsgToHandler(int what, Bundle bundleToSend) {
        Message msg = new Message();
        msg.what = what;
        msg.setData(bundleToSend);
        mHandler.sendMessage(msg);
    }
    private static final int BASE_EVENT = 3000;
    private static final int ACTIVATE_USB_DEVICE = BASE_EVENT + 1;
    private static final int REMOVE_USB_DEVICE = BASE_EVENT + 2;
    private static final int UPDATE_DEVICE_INFO = BASE_EVENT + 3;
    private static final int REQUEST_USB_PERMISSION = BASE_EVENT+4;
    private static final int MAKE_DELAY_1SEC = BASE_EVENT+5;
    private static final int ADD_DEVICE = BASE_EVENT+6;
    private static final int DO_FIRMWARE_UPDATE = BASE_EVENT+7;
    private static final int CLEAR_VIEW_FOR_CAPTURE = BASE_EVENT+8;
    private static final int SET_TEXT_LOGVIEW = BASE_EVENT+10;
    private static final int MAKE_TOAST = BASE_EVENT+11;
    private static final int SHOW_CAPTURE_IMAGE_DEVICE = BASE_EVENT+12;
    private static final int SET_USER_INPUT_ENABLED = BASE_EVENT+13;
    private static final int SET_UI_CLICKED_ENABLED = BASE_EVENT+14;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private Canvas mImageViewCanvas;


    Handler mHandler = new Handler(Looper.getMainLooper())
    {
        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case ACTIVATE_USB_DEVICE:
                    if(mUsbDevice != null)
                        Logger.d("ACTIVATE_USB_DEVICE : " + mUsbDevice.getDeviceName());
                    createBioMiniDevice();
                    break;
                case REMOVE_USB_DEVICE:
                    Logger.d("REMOVE_USB_DEVICE");
                    //_rsApi.terminate();
                    removeDevice();
                    break;
                case UPDATE_DEVICE_INFO:
                    Logger.d("UPDATE_DEVICE_INFO");

                    break;
                case REQUEST_USB_PERMISSION:
                    //https://developer.android.com/reference/android/app/PendingIntent#FLAG_MUTABLE
                    int FLAG_MUTABLE = 0; //PendingIntent.FLAG_MUTABLE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        FLAG_MUTABLE = PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT;
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        FLAG_MUTABLE = PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT; // setting the mutability flag
                    }
                    mPermissionIntent = PendingIntent.getBroadcast(activity, 0, new Intent(ACTION_USB_PERMISSION), FLAG_MUTABLE);
                    mUsbManager.requestPermission(mUsbDevice , mPermissionIntent);
                    break;
                case MAKE_DELAY_1SEC:
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case ADD_DEVICE:
                    addDeviceToUsbDeviceList();
                    break;
                case DO_FIRMWARE_UPDATE:
                    File firmware_file = (File) msg.obj;
                    try {

//                        bioMiniFirmwareDownloadAsync(firmware_file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CLEAR_VIEW_FOR_CAPTURE:
//                    cleareViewForCapture();
                    break;
                case SET_TEXT_LOGVIEW:
                    String _log = (String) msg.obj;
                    // append the new string
//                    scrollBottom(_log);
                    break;
                case MAKE_TOAST:
                    Logger.d("MAKE_TOAST : " + (String)msg.obj);
                    Toast.makeText(activity,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case SHOW_CAPTURE_IMAGE_DEVICE:
                    Logger.d("SHOW_CAPTURE_IMAGE_DEVICE");


                    Bundle bundle = msg.getData();
                    if(bundle != null){
                        Bitmap _captureImgDev = (Bitmap) bundle.getParcelable("capturedImage");
                        //int [] coord = bundle.getIntArray("coordinate");
                        Bitmap _captureOtherImgDev = _captureImgDev.copy(Bitmap.Config.ARGB_8888, true);
                        ArrayList<Integer> coord= bundle.getIntegerArrayList("coordinate");
                        mImageViewCanvas = new Canvas(_captureOtherImgDev);
                        if(coord != null && coord.size() > 0){
                            Logger.d("Core Coordinate X : " + coord.get(0) + " Y : " + coord.get(1));
                            Paint paint= new Paint();
                            paint.setAntiAlias(true);
                            paint.setColor(Color.RED);
                            mImageViewCanvas.drawCircle(coord.get(0),  coord.get(1), 10, paint);
                        }
//                        mImageView.setImageBitmap(_captureOtherImgDev);
//                        _captureOtherImgDev = Bitmap.createBitmap(_captureOtherImgDev, 0, 0, (int) (_captureOtherImgDev.getWidth() * 0.85), (int) _captureOtherImgDev.getHeight() - 10);
                        _captureOtherImgDev= cropBitmapCenter(_captureOtherImgDev, 256, 360);

                        interf.on_result_image_obtained(_captureOtherImgDev);
                        interf.on_result_wsq_obtained(imageToWsq(_captureOtherImgDev));
                        interf.on_result_obtained(imageToIso(_captureOtherImgDev));
                    }
                    break;
                case SET_USER_INPUT_ENABLED:
                    Logger.d("SET_USER_INPUT_ENABLED");
                    boolean user_input_enabled = (boolean)msg.obj;
//                    mViewPager.setUserInputEnabled(user_input_enabled);
                    break;
                case SET_UI_CLICKED_ENABLED:
                    Logger.d("SET_UI_CLICKED_ENABLED");
                    boolean ui_click_enabled = (boolean)msg.obj;
//                    setUiClickable(ui_click_enabled);
                    break;
            }
        }
    };
    private void sendEmptyMsgToHandler(int what) {
        mHandler.sendEmptyMessage(what);
    }

    @Override
    public void stop() {
        super.stop();
        Logger.d("START!");
        int result = 0;
        if(mCurrentDevice != null)
        {
            if(mCurrentDevice.isCapturing())
            {
                doAbortCapture();
                while (mCurrentDevice.isCapturing())
                {
                    SystemClock.sleep(10);
                }
            }
        }

        if(mBioMiniFactory != null)
        {
            if(mUsbDevice != null)
                result = mBioMiniFactory.removeDevice(mUsbDevice);

            if(result == IBioMiniDevice.ErrorCode.OK.value() || result == IBioMiniDevice.ErrorCode.ERR_NO_DEVICE.value())
            {
                mBioMiniFactory.close();
                try{
                    activity.unregisterReceiver(mUsbReceiver);
                }catch (Exception exception){

                }
                mUsbDevice = null;
                mCurrentDevice = null;
            }
        }
        powerControl(NODE_27M,false);
    }
    private   final String NODE_27M =  "/sys/devices/platform/ext_power_otg/id_card_enable";

    private   void powerControl(String node5v, boolean enable) {
        FileOutputStream node_1 = null;
        try {
            byte[] open_one = new byte[]{0x31};
            byte[] close = new byte[]{0x30};
            node_1 = new FileOutputStream(node5v);
            node_1.write(enable ? open_one : close);
            Log.v("OtgUtils", "write  success :" + node5v);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (node_1 != null) {
                    node_1.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private boolean isAbortCapturing = false;

    private void doAbortCapture() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(900);
                    if (mCurrentDevice != null) {
                        //mCaptureOption.captureFuntion = IBioMiniDevice.CaptureFuntion.NONE;
                        if(mCurrentDevice.isCapturing() == false) {
                          Log.e(logTag,"Capture Function is already aborted.");
                            mCaptureOption.captureFuntion = IBioMiniDevice.CaptureFuntion.NONE;

                            isAbortCapturing = false;
                            return;
                        }
                        int result = mCurrentDevice.abortCapturing();
                        int nRetryCount = 0;
//                    while (mCurrentDevice != null && mCurrentDevice.isCapturing()) {
//                        SystemClock.sleep(10);
//                        nRetryCount++;
//                    }
                        Logger.d("run: abortCapturing : " + result);
                        if (result == 0) {
                            if(mCaptureOption.captureFuntion != IBioMiniDevice.CaptureFuntion.NONE)
                                Log.e(logTag,mCaptureOption.captureFuntion.name() + " is aborted.");

                            mCaptureOption.captureFuntion = IBioMiniDevice.CaptureFuntion.NONE;

                            isAbortCapturing = false;
                        }else {
                            if(result == IBioMiniDevice.ErrorCode.ERR_CAPTURE_ABORTING.value()) {
                                Log.e(logTag,"abortCapture is still running.");
                            }else{
                                Log.e(logTag,"abort capture fail!");
                            }
                        }
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void addDeviceToUsbDeviceList()
    {
        Logger.d("start!");
        if(mUsbManager == null)
        {
            Logger.d("mUsbManager is null");
            return;
        }
        if(mUsbDevice !=null)
        {
            Logger.d("usbdevice is not null!");
            return;
        }

        HashMap<String , UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIter = deviceList.values().iterator();
        while(deviceIter.hasNext()){
            UsbDevice _device = deviceIter.next();
            if( _device.getVendorId() ==0x16d1 ){
                Logger.d("found Xperix usb device");
                mUsbDevice = _device;
                if(mUsbManager.hasPermission(mUsbDevice) == false)
                {
                    Logger.d("This device need to Usb Permission!");
                    sendEmptyMsgToHandler(REQUEST_USB_PERMISSION);
                }
                else
                {
                    Logger.d("This device alread have USB permission! please activate this device.");
//                    _rsApi.deviceAttached(mUsbDevice);
                    sendEmptyMsgToHandler(ACTIVATE_USB_DEVICE);
                }
            }
            else
            {
                Logger.d("This device is not biominiseries device!  : " + _device.getVendorId());
            }
        }
    }


    private void removeDevice() {
        Logger.d("ACTION_USB_DEVICE_DETACHED");
        if(mBioMiniFactory != null)
        {
            mBioMiniFactory.removeDevice(mUsbDevice);
            mBioMiniFactory.close();
        }
        mUsbDevice = null;
        mCurrentDevice = null;
//        clearDeviceInfoView();
//        cleareViewForCapture();
//        resetSettingMenu();
//        clearSharedPreferenceData();
    }

}
