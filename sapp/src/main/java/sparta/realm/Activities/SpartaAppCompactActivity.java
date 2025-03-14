package sparta.realm.Activities;

import static com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG;
import static com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner;
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import sparta.realm.R;
import sparta.realm.Realm;
import sparta.realm.Services.DatabaseManager;
import sparta.realm.spartautils.camera.CameraActivity;
import sparta.realm.spartautils.camera.sparta_camera;
import sparta.realm.spartautils.biometrics.face.face_handler;
import sparta.realm.spartautils.svars;
import sparta.realm.utils.Conversions;


public class SpartaAppCompactActivity extends AppCompatActivity {
    public Activity act;
    //public sdbw sd;
//    public DatabaseManager dbm;
    public Drawable error_drawable;
    protected Button next, previous, clear_all;
    Boolean registering = false;
    protected String select_item_index = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        try {
            DatabaseManager.log_event(this, "AppNavigation:" + this.getClass().getName());

        } catch (Exception ex) {
        }
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        error_drawable = getResources().getDrawable(R.drawable.ic_error_24dp);
        error_drawable.setBounds(0, 0, 40, 40);
    }

    static MediaPlayer player;

    public void play_notification_tone(boolean success) {
        AssetManager am;
        try {
            player = MediaPlayer.create(act, success ? R.raw.dong : R.raw.stop);
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.release();
                }

            });
            player.setLooping(false);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("MEDIA PLAY Eror =>", " " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void start_activity(Intent i) {

        startActivity(i);
        //    overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Deprecated
    protected boolean setup_reg_control() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        try {
            clear_all = (Button) findViewById(R.id.clear_all);
            previous = (Button) findViewById(R.id.btn_prev);
            next = (Button) findViewById(R.id.btn_next);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    procceed();
                }
            });
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });


            registering = true;

        } catch (Exception ex) {
            return false;
        }
        return true;
    }


    //bdmmjkjjvxvxsawqertyjuip
    public void procceed() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    public static void hideKeyboardFrom(final Context context, final View view) {

        view.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });
    }


    protected void populate_date(EditText edt, Calendar cb) {
        Calendar cc = Calendar.getInstance();
        int mYear = cc.get(Calendar.YEAR);
        int mMonth = cc.get(Calendar.MONTH);
        int mDay = cc.get(Calendar.DAY_OF_MONTH);


        try {


            mYear = cb.get(Calendar.YEAR);
            mMonth = cb.get(Calendar.MONTH);
            mDay = cb.get(Calendar.DAY_OF_MONTH);

        } catch (Exception ex) {
        }

        // Launch Date Picker Dialog..Continuous reg
        DatePickerDialog dpd = new DatePickerDialog(act,
                new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {


                        edt.setText((dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + " - " + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + " - " + year);


                        try {
                            cb.setTime(Conversions.sdf_user_friendly_date.parse(edt.getText().toString()));
                        } catch (Exception ex) {
                        }

                    }
                }, mYear, mMonth, mDay);
        dpd.show();
        Calendar calendar_min = Calendar.getInstance();
        calendar_min.set(Calendar.YEAR, calendar_min.get(Calendar.YEAR));


        dpd.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());

        //  dpd.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
    }


    public static String logTag = "SpartaAppCompactActivity";

    public static String save_app_image(Bitmap fpb) {
        String img_name = "RE_DAT" + System.currentTimeMillis() + "_IMG.JPG";

        File file = new File(svars.current_app_config(Realm.context).appDataFolder);
        if (!file.exists()) {
            Log.e(logTag, "Creating data dir: " + (file.mkdirs() ? "Successfully created" : "Failed to create !"));
        }
        file = new File(svars.current_app_config(Realm.context).appDataFolder, img_name);

        try (OutputStream fOutputStream = new FileOutputStream(file)) {


            fpb.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

            fOutputStream.flush();
//            fOutputStream.close();

            //  MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return null;
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
        return img_name;
    }

    public static String saveUncompressedPng(Bitmap fpb) {
        String img_name = "RE_DAT" + System.currentTimeMillis() + "_IMG.PNG";

        File file = new File(svars.current_app_config(Realm.context).appDataFolder);
        if (!file.exists()) {
            Log.e(logTag, "Creating data dir: " + (file.mkdirs() ? "Successfully created" : "Failed to create !"));
        }
        file = new File(svars.current_app_config(Realm.context).appDataFolder, img_name);
        try (OutputStream fOutputStream = new FileOutputStream(file)) {
            fpb.compress(Bitmap.CompressFormat.PNG, 100, fOutputStream);
            fOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return img_name;
    }

    public boolean isPackageInstalled(String packageName) {
        try {
            return act.getPackageManager().getApplicationInfo(packageName, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void show_camera_config_dialog(int img_index) {

//        photo_index=svars.image_indexes.id_photo;
//        startActivityForResult(new Intent("sparta.icaochecker.doc_camera"),1);
        View aldv = LayoutInflater.from(act).inflate(R.layout.dialog_config_camera_parameters, null);
        final AlertDialog ald = new AlertDialog.Builder(act)
                .setView(aldv)
                .show();
        ald.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RadioButton icao_rdb = aldv.findViewById(R.id.icao_rdb),
                doc_scanner_rdb = aldv.findViewById(R.id.doc_scanner_rdb);
        String icao_package_name = "sparta.icaochecker";
        RadioGroup camera_types = aldv.findViewById(R.id.camera_types);
        if (!isPackageInstalled(icao_package_name)) {
            icao_rdb.setEnabled(false);
            icao_rdb.setText(icao_rdb.getText() + "!!! (Imaging package not installed)");
            doc_scanner_rdb.setEnabled(false);
            doc_scanner_rdb.setText(doc_scanner_rdb.getText() + "!!! (Imaging package not installed)");
//            if(svars.photo_camera_type(act,img_index)==1||svars.photo_camera_type(act,img_index)==2)
//            {
//                svars.set_photo_camera_type(act,img_index,rb_index+1);
//
//            }
        }
        ((RadioButton) camera_types.getChildAt(svars.photo_camera_type(act, img_index) - 1)).setChecked(true);
        camera_types.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                for (int rb_index = 0; rb_index < camera_types.getChildCount(); rb_index++) {
                    Log.e("RADIOBUTTON :", "" + rb_index + "/" + camera_types.getChildCount());
                    if (((RadioButton) camera_types.getChildAt(rb_index)).isChecked()) {
                        Log.e("RADIOBUTTON :", " OK " + rb_index);
                        svars.set_photo_camera_type(act, img_index, rb_index + 1);
                    }
                }


            }
        });
        aldv.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ald.dismiss();
            }
        });

        aldv.findViewById(R.id.restore_defaults).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                svars.set_photo_camera_type(act, img_index, svars.default_photo_camera_type(act));
                ((RadioButton) camera_types.getChildAt(svars.photo_camera_type(act, img_index) - 1)).setChecked(true);

            }
        });

    }

    public void showImageCameraConfigDialog(String img_index) {

//        photo_index=svars.image_indexes.id_photo;
//        startActivityForResult(new Intent("sparta.icaochecker.doc_camera"),1);
        View aldv = LayoutInflater.from(act).inflate(R.layout.dialog_config_camera_parameters, null);
        final AlertDialog ald = new AlertDialog.Builder(act)
                .setView(aldv)
                .show();
        ald.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RadioButton icao_rdb = aldv.findViewById(R.id.icao_rdb),
                doc_scanner_rdb = aldv.findViewById(R.id.doc_scanner_rdb);
        String icao_package_name = "sparta.icaochecker";
        RadioGroup camera_types = aldv.findViewById(R.id.camera_types);
        if (!isPackageInstalled(icao_package_name)) {
            icao_rdb.setEnabled(false);
            icao_rdb.setText(icao_rdb.getText() + "!!! (Imaging package not installed)");
            doc_scanner_rdb.setEnabled(false);
            doc_scanner_rdb.setText(doc_scanner_rdb.getText() + "!!! (Imaging package not installed)");
//            if(svars.photo_camera_type(act,img_index)==1||svars.photo_camera_type(act,img_index)==2)
//            {
//                svars.set_photo_camera_type(act,img_index,rb_index+1);
//
//            }
        }
        ((RadioButton) camera_types.getChildAt(svars.imageCameraType(act, img_index) - 1)).setChecked(true);
        camera_types.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                for (int rb_index = 0; rb_index < camera_types.getChildCount(); rb_index++) {
                    Log.e("RADIOBUTTON :", "" + rb_index + "/" + camera_types.getChildCount());
                    if (((RadioButton) camera_types.getChildAt(rb_index)).isChecked()) {
                        Log.e("RADIOBUTTON :", " OK " + rb_index);
                        svars.setImageCameraType(act, img_index, rb_index + 1);
                    }
                }


            }
        });
        aldv.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ald.dismiss();
            }
        });

        aldv.findViewById(R.id.restore_defaults).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                svars.setImageCameraType(act, img_index, svars.default_photo_camera_type(act));
                ((RadioButton) camera_types.getChildAt(svars.imageCameraType(act, img_index) - 1)).setChecked(true);

            }
        });

    }

    public void show_remember_me_config_dialog() {

//        photo_index=svars.image_indexes.id_photo;
//        startActivityForResult(new Intent("sparta.icaochecker.doc_camera"),1);
        View aldv = LayoutInflater.from(act).inflate(R.layout.dialog_config_remember_me, null);
        final AlertDialog ald = new AlertDialog.Builder(act)
                .setView(aldv)
                .show();
        ald.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CheckBox remember_username_chk = aldv.findViewById(R.id.remember_username_chk),
                remember_password_chk = aldv.findViewById(R.id.remember_password_chk);

        remember_username_chk.setChecked(svars.remember(act, svars.remember_indexes.username));
        remember_password_chk.setChecked(svars.remember(act, svars.remember_indexes.password));

        remember_username_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                svars.set_remember(act, svars.remember_indexes.username, isChecked);
            }
        });
        remember_password_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                svars.set_remember(act, svars.remember_indexes.password, isChecked);

            }
        });


        aldv.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ald.dismiss();
            }
        });

        aldv.findViewById(R.id.restore_defaults).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                svars.set_remember(act, svars.remember_indexes.username, svars.default_remember_username);
                svars.set_remember(act, svars.remember_indexes.password, svars.default_remember_password);


                remember_username_chk.setChecked(svars.remember(act, svars.remember_indexes.username));
                remember_password_chk.setChecked(svars.remember(act, svars.remember_indexes.password));


            }
        });

    }

    boolean taking_picture = false;
    private String photo_index;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && photo_index != null) {
            int photo_camera_type = svars.imageCameraType(act, photo_index);
            Bitmap bitmap = null;
            String data_url = null;
            data = data == null ? new Intent() : data;
            switch (photo_camera_type) {
                case 1:
                    bitmap = BitmapFactory.decodeFile(data.getExtras().getParcelable("scannedResult").toString());
                    data.putExtra("ImageUrl", saveUncompressedPng(bitmap));
                    data.putExtra("ImageIndex", photo_index);

                    break;
                case 2:
                    Uri uri = data.getExtras().getParcelable("scannedResult");
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        getContentResolver().delete(uri, null, null);
                        data_url = saveUncompressedPng(bitmap);
                        data.putExtra("ImageUrl", data_url);
                        data.putExtra("ImageIndex", photo_index);

                        bitmap.recycle();
                        bitmap = null;
                    } catch (Exception ex) {

                    }

                    break;

                case 3:

                    data_url = data.getStringExtra("ImageUrl");
                    data.putExtra("ImageUrl", data_url);
                    data.putExtra("ImageIndex", photo_index);
                    break;

                case 5:
                    ContentResolver contentResolver = getContentResolver();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, latestCameraPhotoUri);
                    } catch (IOException e) {
//                        throw new RuntimeException(e);
                    }
                    if (bitmap != null) {
                        contentResolver.delete(latestCameraPhotoUri, MediaStore.Images.Media.TITLE + "=?", new String[]{latestCameraPhotoName});
                    }
                    latestCameraPhotoName = save_app_image(bitmap);
//                    try
//                    {
//                        bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), latestCameraPhotoUri);
//                    }
//                    catch (Exception e)
//                    {
//                        Log.e(logTag, "Failed to load", e);
//                    }

//                    latestCameraPhotoName = saveUncompressedPng(bitmap);
//                    data.putExtra("ThumbnailUrl", saveUncompressedPng((Bitmap) data.getExtras().get("data")));
                    data.putExtra("ImageUrl", latestCameraPhotoName);
                    data.putExtra("ImageIndex", photo_index);
                    bitmap.recycle();
                    bitmap = null;


                    break;
                case 4:
                    bitmap = BitmapFactory.decodeByteArray(sparta_camera.latest_image, 0, sparta_camera.latest_image.length, null);
                    data_url = saveUncompressedPng(bitmap);
                    data.putExtra("ImageUrl", data_url);
                    data.putExtra("ImageIndex", photo_index);
                    bitmap.recycle();
                    bitmap = null;

                    sparta_camera.latest_image = null;
                    break;
                case 6:
                    try {
                        GmsDocumentScanningResult gmsDocumentScanningResult = GmsDocumentScanningResult.fromActivityResultIntent(data);
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), gmsDocumentScanningResult.getPages().get(0).getImageUri());
                      try{
                          getContentResolver().delete(gmsDocumentScanningResult.getPages().get(0).getImageUri(), null, null);
                      }catch (Exception exception){

                      }
                        data_url = saveUncompressedPng(bitmap);
                        data.putExtra("ImageUrl", data_url);
                        data.putExtra("ImageIndex", photo_index);

                        bitmap.recycle();
                        bitmap = null;
                    } catch (Exception ex) {

                    }


                    break;


            }
            try {
                data.putExtra("FaceUrl", face_handler.extract_face(svars.current_app_config(Realm.context).appDataFolder + data_url));

            } catch (Throwable ex) {
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Deprecated
    public void take_photo(int image_index, String sid) {
        photo_index = image_index + "";
        int photo_camera_type = svars.photo_camera_type(act, image_index);
        String icao_package_name = "sparta.icaochecker";
        if (photo_camera_type == 1 || photo_camera_type == 2 && !isPackageInstalled(icao_package_name)) {
            if (svars.default_photo_camera_type(act) == 1 || svars.default_photo_camera_type(act) == 2) {
                image_index = 5;
            } else {
                image_index = svars.default_photo_camera_type(act);
            }

        }
        switch (photo_camera_type) {
            case 1:
                startActivityForResult(new Intent("sparta.icaochecker.icao_camera"), image_index);

                break;
            case 2:
                startActivityForResult(new Intent("sparta.icaochecker.doc_camera"), image_index);


                break;
            case 3:
                Intent intt = new Intent(act, sparta.realm.spartautils.biometrics.face.SpartaFaceCamera.class);
                intt.putExtra("sid", sid);
                startActivityForResult(intt, image_index);


                break;
            case 4:
                startActivityForResult(new Intent(act, CameraActivity.class), image_index);

                sparta_camera.latest_image = null;

                break;

            case 5:

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(act.getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, image_index);
                }
                break;


        }

    }

    public void takePhoto(int photo_camera_type, String sid) {
        photo_index = photo_camera_type + "";
//        String icao_package_name = "sparta.realm.iccaoluxand";
        String icao_package_name = "sparta.realm.iccaocamera";
        String doc_scanner_package_name = "sparta.icaochecker";
        if ((photo_camera_type == 1 && !isPackageInstalled(icao_package_name)) || (photo_camera_type == 2 && !isPackageInstalled(doc_scanner_package_name))) {
            Toast.makeText(this, "Camera unavailable", Toast.LENGTH_LONG).show();

            return;
        }

        switch (photo_camera_type) {
            case 1:
                Intent iccao_int = new Intent("sparta.icaochecker.icao_camera2");
                iccao_int.putExtra("sid", "memberPhoto.transaction_no");
                iccao_int.putExtra("camera_index", 0);
                iccao_int.putExtra("camera_rotation", 90);
                iccao_int.putExtra("replaceFromTrackerOnRegistration", true);
                iccao_int.putExtra("replaceOnRegistration", true);
                iccao_int.putExtra("perform_checks", true);
                startActivityForResult(iccao_int, photo_camera_type);
//                            startActivityForResult(new Intent("sparta.icaochecker.icao_camera"), image_index);

                break;
            case 2:
                startActivityForResult(new Intent("sparta.icaochecker.doc_camera"), photo_camera_type);


                break;
            case 3:
                Intent intt = new Intent(act, sparta.realm.spartautils.biometrics.face.SpartaFaceCamera.class);
                intt.putExtra("sid", sid);
                startActivityForResult(intt, photo_camera_type);


                break;
            case 4:
                startActivityForResult(new Intent(act, CameraActivity.class), photo_camera_type);

                sparta_camera.latest_image = null;

                break;

            case 5:

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(act.getPackageManager()) != null) {

                    latestCameraPhotoName = "RE_DAT" + System.currentTimeMillis() + "_IMG.PNG";

                    latestCameraPhotoUri = Uri.parse(svars.current_app_config(Realm.context).appDataFolder + latestCameraPhotoName);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, latestCameraPhotoUri);

                    startActivityForResult(takePictureIntent, 1);
                }
                break;


        }

    }

    public void takePhoto(String image_index) {
        photo_index = image_index;
        int photo_camera_type = svars.imageCameraType(act, image_index);
//        String icao_package_name = "sparta.realm.iccaoluxand";
        String icao_package_name = "sparta.realm.iccaocamera";
        String doc_scanner_package_name = "sparta.icaochecker";
        if ((photo_camera_type == 1 && !isPackageInstalled(icao_package_name)) || (photo_camera_type == 2 && !isPackageInstalled(doc_scanner_package_name))) {
            Toast.makeText(this, "Camera unavailable", Toast.LENGTH_LONG).show();

            return;
        }

        switch (photo_camera_type) {
            case 1:
                Intent iccao_int = new Intent("sparta.icaochecker.icao_camera2");
                iccao_int.putExtra("sid", "memberPhoto.transaction_no");
                iccao_int.putExtra("camera_index", 0);
                iccao_int.putExtra("camera_rotation", 90);
                iccao_int.putExtra("replaceFromTrackerOnRegistration", true);
                iccao_int.putExtra("replaceOnRegistration", true);
                iccao_int.putExtra("perform_checks", true);
                startActivityForResult(iccao_int, 1);
//                            startActivityForResult(new Intent("sparta.icaochecker.icao_camera"), image_index);

                break;
            case 2:
                startActivityForResult(new Intent("sparta.icaochecker.doc_camera"), 1);


                break;
            case 3:
                Intent intt = new Intent(act, sparta.realm.spartautils.biometrics.face.SpartaFaceCamera.class);
                intt.putExtra("sid", image_index);
                startActivityForResult(intt, 1);


                break;
            case 4:
                startActivityForResult(new Intent(act, CameraActivity.class), 1);

                sparta_camera.latest_image = null;

                break;

            case 5:

                takeCameraPhoto();
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(act.getPackageManager()) != null) {
//
//                    latestCameraPhotoName="RE_DAT" + System.currentTimeMillis() + "_IMG.PNG";
//
//                    latestCameraPhotoUri = Uri.parse(svars.current_app_config(Realm.context).appDataFolder+latestCameraPhotoName);
//                    File file=new File(svars.current_app_config(Realm.context).appDataFolder+latestCameraPhotoName);
//                    try {
//                        file.createNewFile();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    latestCameraPhotoUri=Uri.fromFile(file);
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, latestCameraPhotoUri);
//
//                    startActivityForResult(takePictureIntent, 1);
//                }

                break;
              case 6:
scanDocument();
            break;


        }

    }
    ActivityResultLauncher<IntentSenderRequest> scannerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartIntentSenderForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {

                        }
                    });

   public void scanDocument(){
        GmsDocumentScannerOptions options = new GmsDocumentScannerOptions.Builder()
                .setGalleryImportAllowed(false)
                .setPageLimit(1)
                .setResultFormats(RESULT_FORMAT_JPEG)
                .setScannerMode(SCANNER_MODE_FULL)
                .build();
        GmsDocumentScanner scanner = GmsDocumentScanning.getClient(options);

        scanner.getStartScanIntent(this)
                .addOnSuccessListener(intentSender ->
                        scannerLauncher.launch(new IntentSenderRequest.Builder(intentSender).build()))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    void takeCameraPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(act.getPackageManager()) != null) {

            latestCameraPhotoName = "RE_DAT" + System.currentTimeMillis() + "_IMG.PNG";

            latestCameraPhotoUri = Uri.parse(svars.current_app_config(Realm.context).appDataFolder + latestCameraPhotoName);


            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, latestCameraPhotoName);
            values.put(MediaStore.Images.Media.DESCRIPTION, "Realm Image");

            latestCameraPhotoUri = act.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, latestCameraPhotoUri);

            act.startActivityForResult(intent, 1);
        }
    }

    public Uri latestCameraPhotoUri;
    public String latestCameraPhotoName;

    private File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir = Environment.getExternalStorageDirectory();
        tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        return File.createTempFile(part, ext, tempDir);
    }

}


