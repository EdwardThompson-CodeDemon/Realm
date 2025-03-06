package sparta.realm.Activities;

import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.Log;

import sparta.realm.spartautils.biometrics.fp.BTV2;
import sparta.realm.spartautils.biometrics.fp.FingerprintManger;
import sparta.realm.spartautils.biometrics.fp.fp_handler_bt;
import sparta.realm.spartautils.biometrics.fp.fp_handler_stf_usb_8_inch;
import sparta.realm.spartautils.biometrics.fp.sfp_i;
import sparta.realm.spartautils.svars;


public class SpartaAppCompactFingerPrintActivity extends SpartaAppCompactActivity implements sfp_i {
//    public fp_handler_wall_mounted fph_wall_mounted;

  public BTV2 fph_bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    public  FingerprintManger fingerprintManger=new FingerprintManger(act);
   public  void startFPModule(FingerprintManger fingerprintManger)
    {
        this.fingerprintManger=fingerprintManger;
        fingerprintManger.start();


    }
    @Override
    protected void onPause() {
        super.onPause();
        if(fingerprintManger!=null){
            fingerprintManger.stop();
        }


    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        if(fingerprintManger!=null){
            fingerprintManger.start();
        }


        super.onResume();

    }

    @Override
    public void onDeviceStarted() {

    }

    @Override
    public void onDeviceClosed() {

    }
    public void onMatchFound(String sid, String data_index, String match_time, int v_type, int verrification_mode) {

    }


    @Override
    public void on_result_obtained(String capt_result) {

    }

    @Override
    public void on_result_image_obtained(Bitmap capt_result_img) {

    }

    @Override
    public void on_result_wsq_obtained(byte[] wsq) {

    }

    @Override
    public void on_result_error(String capt_error) {

    }

    @Override
    public void on_device_error(String device_error) {

    }

    @Override
    public void on_device_status_changed(String status) {

    }

    @Override
    public void on_result_image_error(String s) {

    }

    @Override
    public void onConnectionStatusChanged(boolean connected, UsbDevice usbDevice) {

    }

    @Override
    public void onRegistrationComplete() {

    }

    @Override
    public void onRegistrationSegmentChanged(int segment_type) {

    }

    @Override
    public void on_result_obtained(int fp_index, String capt_result) {

    }

    @Override
    public void onGlobalImageObtained(Bitmap image) {

    }

    @Override
    public void on_result_image_obtained(int fp_index, Bitmap capt_result_img) {

    }

    @Override
    public void on_result_wsq_obtained(int fp_index, byte[] wsq) {

    }
}
