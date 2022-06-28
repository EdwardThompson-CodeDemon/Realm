package sparta.realm.Activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import sparta.realm.spartautils.biometrics.fp.BTV2;
import sparta.realm.spartautils.biometrics.fp.fp_handler_bt;
import sparta.realm.spartautils.biometrics.fp.fp_handler_stf_usb_8_inch;
import sparta.realm.spartautils.biometrics.fp.fp_handler_wall_mounted;
import sparta.realm.spartautils.biometrics.fp.sfp_i;
import sparta.realm.spartautils.svars;


public class SpartaAppCompactFingerPrintActivity extends SpartaAppCompactActivity implements sfp_i {
    public fp_handler_wall_mounted fph_wall_mounted;
  public  fp_handler_stf_usb_8_inch fph_8_inch;
  //  fp_handler_biomini fph_biomini;
  public BTV2 fph_bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    FingerprintManger fingerprintManger;
    void startFPModule(FingerprintManger fingerprintManger)
    {
        this.fingerprintManger=fingerprintManger;
        fingerprintManger.start();


    }
    @Override
    protected void onPause() {
        super.onPause();
        fingerprintManger.stop();
        if (svars.current_device()== svars.DEVICE.UAREU.ordinal())
        {
            try{   fph_8_inch.close();}catch (Exception ex){}
        }else if(svars.current_device()== svars.DEVICE.WALL_MOUNTED.ordinal())
        {
            try{    fph_wall_mounted.close();}catch (Exception ex){}

        }else if(svars.current_device()== svars.DEVICE.BIO_MINI.ordinal())
        {
//        try{    fph_biomini.stop_biomini();}catch (Exception ex){}

        }
        if(svars.use_bt_fp_device(act))
        {
            //CLOSE BT COMMUNICATION


        }

    }
    @Override
    protected void onStart() {
        super.onStart();

        if(svars.current_device()== svars.DEVICE.BIO_MINI.ordinal())
        {
            try {
                Log.e("Starting =>","Biomini");
             //   try{ fph_biomini.stop_biomini();}catch (Exception ex){}
                //  fph_biomini = new fp_handler_biomini(act);

//                findViewById(R.id.verify).setVisibility(View.VISIBLE);
//                ((Button)findViewById(R.id.verify)).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        try{
//
//                 //           fph_biomini.capture_single();
//                            //    fph_biomini.start_capture();
//                        }catch (Exception ex){
//                            Log.e("Capture error =>",""+ex.getMessage());
//                        }
//                    }
//                });
            }catch (Exception ex){
                Log.e("FP error =>",""+ex.getMessage());
            }




        }
    }

    @Override
    protected void onResume() {
        fingerprintManger.start();
        if (svars.current_device()== svars.DEVICE.UAREU.ordinal())
        {
            fph_8_inch=new fp_handler_stf_usb_8_inch(act);
        }else if(svars.current_device()== svars.DEVICE.WALL_MOUNTED.ordinal())
        {
            fph_wall_mounted=new fp_handler_wall_mounted(act);

        }else if(svars.current_device()== svars.DEVICE.BIO_MINI.ordinal())
        {
            try {
                Log.e("Resuming =>","Biomini");
//                if(fph_biomini==null)
//                {
//               //     fph_biomini = new fp_handler_biomini(act);
//                }
//                findViewById(R.id.verify).setVisibility(View.VISIBLE);
//                ((Button)findViewById(R.id.verify)).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        try{
//                            Log.e("Biomini","Capturing");
//
//                         //   fph_biomini.capture_single();
//                            //  fph_biomini.start_capture();
//                        }catch (Exception ex){
//                            Log.e("Capture error =>",""+ex.getMessage());
//                        }
//                    }
//                });
            }catch (Exception ex){}


        }
        if(svars.use_bt_fp_device(act))
        {
            //SETUP BT COMMUNICATION
            fph_bt=new BTV2(act);


        }

        super.onResume();

    }

    @Override
    public void onDeviceStarted() {

    }

    @Override
    public void onDeviceClosed() {

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
}
