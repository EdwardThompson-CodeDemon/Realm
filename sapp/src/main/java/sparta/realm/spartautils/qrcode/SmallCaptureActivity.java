package sparta.realm.spartautils.qrcode;

import android.content.pm.PackageManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import sparta.realm.R;


/**
 * This activity has a margin.
 */
public class SmallCaptureActivity extends CaptureActivity implements DecoratedBarcodeView.TorchListener{
    Button btnManual, btnFlashlight;
    private boolean isFlashLightOn = false;
    private DecoratedBarcodeView bcodeScannerView;

    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.capture_small);

        btnManual = (Button)findViewById(R.id.btnManual);
        btnManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), PostPaidActivity.class));
            }
        });

        // Flashlight on/off button
        btnFlashlight = (Button)findViewById(R.id.flashlight_button);

        if(!hasFlash()){
            btnFlashlight.setVisibility(View.GONE);
        }else {
            setDBV();
            btnFlashlight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     switchFlashlight();
                    //Toast.makeText(getApplicationContext(), "flashlight works", Toast.LENGTH_SHORT).show();
                }
            });
        }

//        ImageView imgnextss = (ImageView)findViewById(R.id.imgnextss);
//        if (GlobalVariables.selected_item.equalsIgnoreCase("Water")){
//            imgnextss.setBackgroundResource(R.drawable.sodeci);
//        }
//        else if(GlobalVariables.selected_item.equals("Electricity") || GlobalVariables.selected_item.equals("Elect")){
//            imgnextss.setBackgroundResource(R.drawable.cieimage);
//            imgnextss.getLayoutParams().height = 90;
//            imgnextss.getLayoutParams().width = 70;
//        }

        return (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);

    }

    // set the torch listener
    public void setDBV(){
        bcodeScannerView =  (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        bcodeScannerView.setTorchListener(this);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_A:
            {
                //your Action code
                Log.i("KA", "KA");
                return true;
            }
            case KeyEvent.KEYCODE_BACK:
            {
                SmallCaptureActivity.this.finish();

            }
        }
        return super.onKeyDown(keyCode, event);
    }

    // to be done when torch turned on
    @Override
    public void onTorchOn() {
btnFlashlight.setText(R.string.turn_flashlight_off);
    }

    //to be done when torch turned off
    @Override
    public void onTorchOff() {
btnFlashlight.setText(R.string.turn_flashlight_on);
    }

    // check if the device has a flashlight
    private boolean hasFlash(){
        return getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

     //switch the flashlight on and off
    private void switchFlashlight(){
        if(isFlashLightOn){
            bcodeScannerView.setTorchOff();
            isFlashLightOn = false;
        }else {
            bcodeScannerView.setTorchOn();
            isFlashLightOn = true;
        }
    }

}
