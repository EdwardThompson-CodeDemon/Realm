package sparta.realm.spartautils.biometrics.fp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.miaxis.bean.AESConfig;
import com.miaxis.bean.CaptureConfig;
import com.miaxis.common.MxImage;
import com.miaxis.common.MxResult;
import com.miaxis.driver.FingerApi;
import com.miaxis.driver.FingerApiFactory;
import com.miaxis.justouch.JustouchFingerAPI;
import com.miaxis.utils.BmpLoader;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SM9XFingerprintManager extends FingerprintManger {

    private static final String TAG = "SM9XFingerprintManager";

    public SM9XFingerprintManager(Activity activity) {
        super(activity);
        com.miaxis.common.LogUtils.setLogLevel(-1);

    }

    @Override
    public void start() {
        super.start();
        registerReceiver();
        connect();
    }

    private USBReceiver mUsbReceiver;

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        mUsbReceiver = new USBReceiver();
        activity.registerReceiver(mUsbReceiver, filter);
    }

    @Override
    public void stop() {
        super.stop();
        closeDevice();
    }

    boolean connected = false;
    boolean busy = false;
    boolean vc = true;
    private FingerApi mFingerApi;

    void connect() {
        if (!connected) {
            UsbManager usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
            HashMap<String, UsbDevice> map = usbManager.getDeviceList();
            for (UsbDevice usbDevice : map.values()) {
                if ((usbDevice.getVendorId() == FingerApiFactory.VC_VID &&
                        usbDevice.getProductId() == FingerApiFactory.VC_PID) ||
                        (usbDevice.getVendorId() == FingerApiFactory.MSC_VID &&
                                usbDevice.getProductId() == FingerApiFactory.MSC_PID)) {

                    if (usbDevice.getVendorId() == FingerApiFactory.MSC_VID) {
                        vc = false;
                    }
                    mFingerApi = FingerApiFactory.getInstance(activity, FingerApiFactory.USB);


                    Log.d(TAG, "USBReceiver start open devices");
                    openDevice(vc);
                    getFinalImage();
                    break;
                }
            }
        }

    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
//    private JustouchFingerAPI mJustouchApi;

    public void openDevice(final boolean isSleep) {
        Log.e(TAG, "start open devices isSleep: " + isSleep);

        executor.execute(() -> {

            int i = mFingerApi.openDevice();
            if (i >= 0) {
                //get device module
                MxResult<String[]> deviceFactoryInfo = mFingerApi.getDeviceFactoryInfo();
                if (deviceFactoryInfo.isSuccess()) {
                    Log.e(TAG, "Module: " + deviceFactoryInfo.getData()[1]);
//                    moduleName.postValue(deviceFactoryInfo.getData()[1]);
                }

                //get device Store Max Number
                MxResult<int[]> getStoreMaxNumber = mFingerApi.getStoreMaxNumber(0);
                if (getStoreMaxNumber.isSuccess()) {
//                    storeMaxNumber.postValue(getStoreMaxNumber.getData()[1]);
                }

                //get device version for show MOSIP or L1
                MxResult<String> version = mFingerApi.getDeviceInfo();
                if (version.isSuccess()) {
//                    deviceVersion.postValue(version.getData());
                }

                StringBuilder msg = new StringBuilder();
//                opened.postValue(true);
                connected = true;
                msg.append("[OPEN]\nSuccess");
                //auth alg
                MxResult<?> algHandshakeInit = mFingerApi.algHandshakeInit();
                if (!algHandshakeInit.isSuccess()) {
                    busy = false;
//                    busy.postValue(false);
                    return;
                }
                if (version.getData() != null && !version.getData().contains("1.0.6")) {
                    //V1.0.6 does not support algorithmic handshakes

                    //get module feature type
                    MxResult<Integer> result = mFingerApi.getFeatureType();
                    if (!result.isSuccess()) {
//                        log.postValue("[GET FEATURE TYPE]\nFailed\n" + result.getCode() + "\n" + result.getMsg() + "\n" + result.getSw1());
//                        busy.postValue(false);
                        busy = false;
                        return;
                    }
//                    featureType.postValue(result.getData());
                    msg.append("\n[GET FEATURE TYPE]\nSuccess");

//                    int initMiaxisDevice = mJustouchApi.initMiaxisDevice(activity);
//                    if (initMiaxisDevice != 0) {
//                        busy = false;
//                        return;
//                    }
                    msg.append("\n[AUTH JUSTOUCH ALG]\nSuccess");
                } else {
//                    supportsDeviceAlgorithm.postValue(false);
                }

                if (version.getData() != null && !version.getData().contains("811") &&
                        !version.getData().contains("812") && !version.getData().contains("822")) {
                    //auth lfd alg, If it is serial communication, the algorithm can not be authenticated
                    MxResult<?> authLFDResidualAlg = mFingerApi.authLFDResidualAlg(activity);
                    if (!authLFDResidualAlg.isSuccess()) {
//                        log.postValue("[AUTH LFD ALG]\nFailed\n" + authLFDResidualAlg);
//                        busy.postValue(false);
                        busy = false;
                        return;
                    }
                    msg.append("\n[AUTH LFD ALG]\nSuccess");
//                    supportsLFDAlgorithm.postValue(true);
                } else {
//                    supportsLFDAlgorithm.postValue(false);
                }


                Log.e(TAG, msg.toString());
            } else {
//                if (communicationType.getValue() == 1 && i == MxErrorCode.FAILED_TO_READ_DATA.getCode())
//                    log.postValue("[OPEN]\nFailed\n" + i + "\nPlease check your com and baud rate");
//                else
//                    log.postValue("[OPEN]\nFailed\n" + i);
            }
            busy = false;

        });

    }
    public void closeDevice() {
     try {
         if (mFingerApi == null) {
             return;
         }
         busy = false;
         connected = false;
         int i = mFingerApi.closeDevice();
         mFingerApi.algHandshakeFree();
         mFingerApi.freeLFDResidualAlg();
         if (i >= 0) {
             Log.e(TAG, "[CLOSE]\nSuccess");
         } else {
             Log.e(TAG, "[CLOSE]\nFailed\n" + i);
         }
         connected = false;
         executor.isShutdown();
         mFingerApi.lamp(0, 0);
     }catch (Exception exception){
         Log.e(TAG, "[CLOSE]\nFailed\n" + exception.getMessage());

     }

    }
    public void closeDevice_() {

        executor.execute(() -> {
            if (mFingerApi == null) {
//                log.postValue("[CLOSE]\nHas been closed");
                return;
            }
            connected=false;

            int i = mFingerApi.closeDevice();
            mFingerApi.algHandshakeFree();
            mFingerApi.freeLFDResidualAlg();
//            mJustouchApi.free();
            if (i >= 0) {
//                opened.postValue(false);
                Log.e(TAG, "[CLOSE]\nSuccess");
            } else {
                Log.e(TAG, "[CLOSE]\nFailed\n" + i);
            }
            connected=false;
        });
    }

    public void getFinalImage() {
        executor.execute(() -> {
            busy = true;
            while (connected) {
                long st = System.currentTimeMillis();
                MxResult<MxImage> result = getImage();
                long getImageTime = System.currentTimeMillis();
                long endTime = 0;

                busy = false;
                MxImage mxImage = result.getData();
                if (result.isSuccess()&&mxImage!=null) {
                    if (endTime == 0) {
                        long time = 0L;
                        if (result.getData() != null) time = result.getData().time;
                        Log.e(TAG, "[CAPTURE]\nSuccess\nTime: " + time + "ms");
                    } else {
                        Log.e(TAG, "[CAPTURE]\nSuccess\nTime: " + (getImageTime - st) + "ms\nUploadTime: " + (endTime - getImageTime) + "ms");
                    }
                    byte[] imageDate = new byte[mxImage.width * mxImage.height + 1078];
                    int raw2Bmp = BmpLoader.Raw2Bmp(imageDate, mxImage.data, mxImage.width, mxImage.height);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageDate, 0, imageDate.length);
                    try {
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) (bitmap.getWidth() * 0.85), (int) bitmap.getHeight() - 10);

                        interf.on_result_image_obtained(bitmap);

                        interf.on_result_wsq_obtained(imageToWsq(bitmap));

                        interf.on_result_obtained(imageToIso(bitmap));
                    }catch (Exception exception){
                        Log.e(TAG, ("[CAPTURE]\nFailed"),exception);
                        interf.on_device_error(exception.getMessage());
                    }
                } else {
                    Log.e(TAG, ("[CAPTURE]\nFailed"));
                }
            }
//            getFinalImage();
        });
//        getFinalImage();

    }

    boolean encrypted = false;

    private MxResult<MxImage> getImage() {
        CaptureConfig.Builder captureConfigBuilder = new CaptureConfig.Builder()
                .setTimeout(CaptureConfig.DEFAULT_TIMEOUT)
                .setAreaScore(25);
        if (Boolean.TRUE.equals(encrypted)) {
            captureConfigBuilder
                    .setAESConfig(new AESConfig.Builder().setKey("1234567890123456").setMode("ECB").setPadding("PKCS5Padding").build())
                    .setAESStatus(CaptureConfig.AES_DEVICE);
        }
        CaptureConfig captureConfig = captureConfigBuilder.build();

        return mFingerApi.getImage(captureConfig);

    }

    private class USBReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 这里可以拿到插入的USB设备对象
            UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            Log.e(TAG, "onReceive: " + usbDevice.toString());
            int interfaceCount = usbDevice.getInterfaceCount();
            for (int i = 0; i < interfaceCount; i++) {
                UsbInterface anInterface = usbDevice.getInterface(i);
                Log.e(TAG, "UsbInterface: " + anInterface.toString());
                int interfaceClass = anInterface.getInterfaceClass();
                Log.e(TAG, "interfaceClass: " + interfaceClass);
            }
            switch (intent.getAction()) {
                case UsbManager.ACTION_USB_DEVICE_ATTACHED: // 插入USB设备
                    Log.e(TAG, "onReceive:  插入USB设备");
                    if (busy) {
                        break;
                    }
                    connect();
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED: // 拔出USB设备
                    Log.e(TAG, "onReceive:  拔出USB设备");
                    if ((usbDevice.getVendorId() == FingerApiFactory.VC_VID &&
                            usbDevice.getProductId() == FingerApiFactory.VC_PID) ||
                            (usbDevice.getVendorId() == FingerApiFactory.MSC_VID &&
                                    usbDevice.getProductId() == FingerApiFactory.MSC_PID)) {

                        closeDevice();
                    }
                    break;
                default:
                    break;
            }
        }

    }
}
