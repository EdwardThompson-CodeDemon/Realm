package sparta.realm.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.infideap.blockedittext.BlockEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sparta.realm.MainActivity;
import sparta.realm.R;
import sparta.realm.Realm;
import sparta.realm.Services.DatabaseManager;
import sparta.realm.spartaadapters.dyna_data_adapter;
import sparta.realm.spartaadapters.dyna_data_adapter_;
import sparta.realm.spartamodels.dyna_data;
import sparta.realm.spartamodels.dyna_data_obj;
import sparta.realm.Services.sdbw;
import sparta.realm.spartautils.camera.CameraActivity;
import sparta.realm.spartautils.camera.sparta_camera;
import sparta.realm.spartautils.biometrics.face.face_handler;
import sparta.realm.spartautils.svars;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;


public class SpartaAppCompactActivity extends AppCompatActivity {
public Activity act;
//public sdbw sd;
public DatabaseManager dbm;
public Drawable error_drawable;
protected  Button next,previous,clear_all;
Boolean registering=false;
protected String select_item_index="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);act=this;
        DatabaseManager.log_event(this, "AppNavigation:"+this.getClass().getName());


        try{
           dbm= Realm.databaseManager;
//           sd=new sdbw(Realm.context);
        }catch (Exception ex){}
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        error_drawable= getResources().getDrawable(R.drawable.ic_error_24dp);
        error_drawable.setBounds(0, 0,40,40);
    }
    static MediaPlayer player;

    public void play_notification_tone(boolean success)
    {
        AssetManager am;
        try {
            player = MediaPlayer.create(act, success? R.raw.dong:R.raw.stop);
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
            Log.e("MEDIA PLAY Eror =>"," "+e.getMessage());
            e.printStackTrace();
        }
    }
    public static boolean isNumeric(String str) {
        if(str==null||str.length()==0)return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    public static boolean containsNumeric(String str) {
        for(char cct:str.toCharArray()){

            try {
                Double.parseDouble(cct+"");
                return true;
            } catch(NumberFormatException e){

            }
        }return false;

    }
protected void set_text_error(EditText edt, String error)
{
    edt.setError(error);
    edt.setBackground(act.getResources().getDrawable( R.drawable.textback_error));
    edt.requestFocus();


    Toast.makeText(act,error, Toast.LENGTH_LONG).show();

}
protected void set_text_error(BlockEditText edt,String error)
{
    //edt.setError(Html.fromHtml("<marquee direction='down' width='100%'height='100%'><font color='red' background-color='red'>"+error+"</font></marquee>"),error_drawable);
    edt.setBackground(act.getResources().getDrawable( R.drawable.textback_error));
    edt.requestFocus();


    Toast.makeText(act,error, Toast.LENGTH_LONG).show();

}
protected boolean set_conditional_input_error(boolean valid, View edt, String error, String input, int min_length)
{
    if(input==null||input.length()<min_length)
    {
       try {
           if (edt.getClass().isInstance(new AppCompatEditText(edt.getContext()))) {
               ((AppCompatEditText) edt).setError(error);

           }
           edt.setBackground(act.getResources().getDrawable(R.drawable.textback_error));
           edt.requestFocus();
       }catch (Exception ex){}
if(error!=null){
    Toast.makeText(act,error, Toast.LENGTH_LONG).show();

}
        valid=false;
        return valid;
    }else{
         if (edt.getClass().isInstance(new AppCompatEditText(edt.getContext()))) {
            edt.setBackground(act.getResources().getDrawable(R.drawable.textback));
             ((AppCompatEditText) edt).setError(null);

         }

    }

return valid;
}
   public void start_activity(Intent i)
    {

         startActivity(i);
    //    overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

   protected boolean setup_reg_control()
    {
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
    clear_all.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            clear_all(MainActivity.class);
        }
    });

    registering = true;

}catch (Exception ex){return false;}
        return true;
    }

    void clear_all(Class<?> mm)
    {
        View aldv= LayoutInflater.from(act).inflate(R.layout.dialog_confirm_clear_all,null);
        final AlertDialog ald=new AlertDialog.Builder(act)
                .setView(aldv)
                .show();
        aldv.findViewById(R.id.clear_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ald.dismiss();

                svars.set_working_employee(act,null);
                svars.working_member =null;
                Intent reg_intent=new Intent(act, mm);
                reg_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK|FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(reg_intent);

                finish();
            }
        });

        aldv.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ald.dismiss();
            }
        });

    }
//bdmmjkjjvxvxsawqertyjuip
    public void procceed()
     {

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
    protected void search_field(final Spinner spn, final ArrayList<dyna_data_obj> original_list, String field_name)
    {
      //  hideKeyboardFrom(act,spn);

        final List<dyna_data_obj>[] searched = new List[]{new ArrayList<>()};
searched[0].addAll(original_list);
        final View aldv= LayoutInflater.from(act).inflate(R.layout.dialog_search_field,null);
        final AlertDialog ald=new AlertDialog.Builder(act)
                .setView(aldv)
                .show();
        final GridView result_list=(GridView)aldv.findViewById(R.id.result_list);
        result_list.setAdapter(new dyna_data_adapter(act,original_list));
        Button dismiss=(Button)aldv.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ald.dismiss();
            }
        });
        final AutoCompleteTextView serch_field=(AutoCompleteTextView)aldv.findViewById(R.id.search_field);
        final String[] searchterm = {""};
        final Thread[] search_thread = {null};

         hideKeyboardFrom(act,serch_field);


        serch_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search_thread[0] =new Thread(new Runnable() {
                    @Override
                    public void run() {
                        searched[0].clear();
                        // searchterm[0].startsWith(serch_field.getText().toString())?searched[0]:
                        searched[0] =  Stream.of(original_list).filter(new Predicate<dyna_data_obj>() {
                            @Override
                            public boolean test(dyna_data_obj item) {
                                return item.data.value.toUpperCase().contains(serch_field.getText().toString().toUpperCase());
                            }
                        }).collect(Collectors.<dyna_data_obj>toList());
                        searchterm[0] =serch_field.getText().toString();
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                result_list.setAdapter(new dyna_data_adapter(act,searched[0]));

                            }
                        });
                    }
                });
                search_thread[0].start();

               /* for (dyna_data_obj obj:original_list)
                {
                    if(obj.data.toLowerCase().trim().contains(serch_field.getText().toString().toLowerCase().trim()))
                    {
                        searched.add(obj);
                    }
                    result_list.setAdapter(new dyna_data_adapter(act,searched));
                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        result_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for(int j=0;j<original_list.size();j++)
                {
                    dyna_data_obj obj=original_list.get(j);
                    if(obj.sid.value.equalsIgnoreCase(searched[0].get(i).sid.value))
                    {
                        spn.setSelection(j);
                        ald.dismiss();
                        break;
                    }
                }

            }
        });
    }
    protected void search_field_(final Spinner spn, final ArrayList<dyna_data> original_list, String field_name)
    {
        //  hideKeyboardFrom(act,spn);

        final List<dyna_data>[] searched = new List[]{new ArrayList<>()};
        searched[0].addAll(original_list);
        final View aldv= LayoutInflater.from(act).inflate(R.layout.dialog_search_field,null);
        final AlertDialog ald=new AlertDialog.Builder(act)
                .setView(aldv)
                .show();
        final GridView result_list=(GridView)aldv.findViewById(R.id.result_list);
        result_list.setAdapter(new dyna_data_adapter_(act,original_list));
        Button dismiss=(Button)aldv.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ald.dismiss();
            }
        });
        final AutoCompleteTextView serch_field=(AutoCompleteTextView)aldv.findViewById(R.id.search_field);
        final String[] searchterm = {""};
        final Thread[] search_thread = {null};

        hideKeyboardFrom(act,serch_field);


        serch_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search_thread[0] =new Thread(new Runnable() {
                    @Override
                    public void run() {
                        searched[0].clear();
                        // searchterm[0].startsWith(serch_field.getText().toString())?searched[0]:
                        searched[0] =  Stream.of(original_list).filter(item -> item.data.toUpperCase().contains(serch_field.getText().toString().toUpperCase())).collect(Collectors.<dyna_data>toList());
                        searchterm[0] =serch_field.getText().toString();
                        act.runOnUiThread(() -> result_list.setAdapter(new dyna_data_adapter_(act,searched[0])));
                    }
                });
                search_thread[0].start();

               /* for (dyna_data_obj obj:original_list)
                {
                    if(obj.data.toLowerCase().trim().contains(serch_field.getText().toString().toLowerCase().trim()))
                    {
                        searched.add(obj);
                    }
                    result_list.setAdapter(new dyna_data_adapter(act,searched));
                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        result_list.setOnItemClickListener((adapterView, view, i, l) -> {
            for(int j=0;j<original_list.size();j++)
            {
                dyna_data obj=original_list.get(j);
                if(obj.sid.equalsIgnoreCase(searched[0].get(i).sid))
                {
                    spn.setSelection(j);
                    ald.dismiss();
                    break;
                }
            }

        });
    }
    protected void search_field_code(final Spinner spn, final ArrayList<dyna_data> original_list)
    {
        //  hideKeyboardFrom(act,spn);

        final List<dyna_data>[] searched = new List[]{new ArrayList<>()};
        searched[0].addAll(original_list);
        final View aldv= LayoutInflater.from(act).inflate(R.layout.dialog_search_field,null);
        final AlertDialog ald=new AlertDialog.Builder(act)
                .setView(aldv)
                .show();
        final GridView result_list=(GridView)aldv.findViewById(R.id.result_list);
        result_list.setAdapter(new dyna_data_adapter_(act,original_list));
        Button dismiss=(Button)aldv.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ald.dismiss();
            }
        });
        final AutoCompleteTextView serch_field=(AutoCompleteTextView)aldv.findViewById(R.id.search_field);
        final String[] searchterm = {""};
        final Thread[] search_thread = {null};

        hideKeyboardFrom(act,serch_field);


        serch_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search_thread[0] =new Thread(new Runnable() {
                    @Override
                    public void run() {
                        searched[0].clear();
                        // searchterm[0].startsWith(serch_field.getText().toString())?searched[0]:
                        searched[0] =  Stream.of(original_list).filter(item ->item.code!=null&&item.code.toUpperCase().contains(serch_field.getText().toString().toUpperCase())).collect(Collectors.<dyna_data>toList());
                        searchterm[0] =serch_field.getText().toString();
                        act.runOnUiThread(() -> result_list.setAdapter(new dyna_data_adapter_(act,searched[0])));
                    }
                });
                search_thread[0].start();

               /* for (dyna_data_obj obj:original_list)
                {
                    if(obj.data.toLowerCase().trim().contains(serch_field.getText().toString().toLowerCase().trim()))
                    {
                        searched.add(obj);
                    }
                    result_list.setAdapter(new dyna_data_adapter(act,searched));
                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        result_list.setOnItemClickListener((adapterView, view, i, l) -> {
            for(int j=0;j<original_list.size();j++)
            {
                dyna_data obj=original_list.get(j);
                if(obj.sid.equalsIgnoreCase(searched[0].get(i).sid))
                {
                    spn.setSelection(j);
                    ald.dismiss();
                    break;
                }
            }

        });
    }


    protected void populate_spiner(final Spinner spn, final ArrayList<dyna_data_obj> original_list, String sid)
    {
        for (int i = 0; i < original_list.size(); i++) {
            if (original_list.get(i).sid.value.equalsIgnoreCase(sid)) {
                final int finalI = i;
                spn.post(new Runnable() {
                    @Override
                    public void run() {
                        spn.setSelection(finalI, true);

                    }
                });

            }
        }
    }
 protected void populate_spiner_(final Spinner spn, final ArrayList<dyna_data> original_list, String sid)
    {
        for (int i = 0; i < original_list.size(); i++) {
            if (original_list.get(i).sid.equalsIgnoreCase(sid)) {
                final int finalI = i;
                spn.post(new Runnable() {
                    @Override
                    public void run() {
                        spn.setSelection(finalI, true);

                    }
                });

            }
        }
    }


    protected void populate_date(EditText edt,Calendar cb)
    {
        Calendar cc= Calendar.getInstance();
        int mYear = cc.get(Calendar.YEAR);
        int mMonth = cc.get(Calendar.MONTH);
        int mDay = cc.get(Calendar.DAY_OF_MONTH);


        try{


            mYear = cb.get(Calendar.YEAR);
            mMonth = cb.get(Calendar.MONTH);
            mDay = cb.get(Calendar.DAY_OF_MONTH);

        }catch (Exception ex){}

        // Launch Date Picker Dialog..Continuous reg
        DatePickerDialog dpd = new DatePickerDialog(act,
                new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {



                        edt.setText((dayOfMonth<10?"0"+dayOfMonth:dayOfMonth) + " - "  + ((monthOfYear + 1)<10?"0"+(monthOfYear + 1):(monthOfYear + 1)) +" - "+year );


                        try{
    cb.setTime(svars.sdf_user_friendly_date.parse(edt.getText().toString()));
}catch (Exception ex){}

                    }
                }, mYear, mMonth, mDay);
        dpd.show();
        Calendar calendar_min = Calendar.getInstance();
        calendar_min.set(Calendar.YEAR,calendar_min.get(Calendar.YEAR));


        dpd.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());

      //  dpd.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
    }

    protected void setup_dob_field(BlockEditText edt)
    {
        edt.setNumberOfBlock(3);
        edt.setDefaultLength(4);
        edt.setLengthAt(0,2);
        edt.setLengthAt(1,2);
        edt.setLengthAt(2,4);
        edt.setSeparatorCharacter('-');
        edt.setInputType(InputType.TYPE_CLASS_NUMBER);
    }
    protected void populate_date(BlockEditText edt, Calendar cb)
    {
        Calendar cc= Calendar.getInstance();
        int mYear = cc.get(Calendar.YEAR);
        int mMonth = cc.get(Calendar.MONTH);
        int mDay = cc.get(Calendar.DAY_OF_MONTH);


        try{


            mYear = cb.get(Calendar.YEAR);
            mMonth = cb.get(Calendar.MONTH);
            mDay = cb.get(Calendar.DAY_OF_MONTH);

        }catch (Exception ex){}

        // Launch Date Picker Dialog..Continuous reg
        DatePickerDialog dpd = new DatePickerDialog(act,
                new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

edt.post(new Runnable() {
    @Override
    public void run() {
        edt.setText("");
        setup_dob_field(edt);
        String input =((dayOfMonth<10?"0"+dayOfMonth:dayOfMonth) + "-"  + ((monthOfYear + 1)<10?"0"+(monthOfYear + 1):(monthOfYear + 1)) +"-"+year );
        Log.e("dt output :",""+input);
        edt.setText(input);
        Log.e("dt output :",""+edt.getText());

    }
});


                        try{
    cb.setTime(svars.sdf_user_friendly_date.parse(((dayOfMonth<10?"0"+dayOfMonth:dayOfMonth) + "-"  + ((monthOfYear + 1)<10?"0"+(monthOfYear + 1):(monthOfYear + 1)) +"-"+year )));
}catch (Exception ex){}

                    }
                }, mYear, mMonth, mDay);
        dpd.show();
        Calendar calendar_min = Calendar.getInstance();
        calendar_min.set(Calendar.YEAR,calendar_min.get(Calendar.YEAR));


        dpd.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());

      //  dpd.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
    }
    public static String logTag="SpartaAppCompactActivity";
    public static String save_app_image(Bitmap fpb)
    {
        String img_name="RE_DAT"+ System.currentTimeMillis()+"_IMG.JPG";

        File file = new File(svars.current_app_config(Realm.context).file_path_employee_data);
        if (!file.exists()) {
            Log.e(logTag,"Creating data dir: "+ (file.mkdirs()?"Successfully created":"Failed to create !"));
        }
        file = new File(svars.current_app_config(Realm.context).file_path_employee_data, img_name);

        try (OutputStream fOutputStream = new FileOutputStream(file)){


            fpb.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

            fOutputStream.flush();
//            fOutputStream.close();

            //  MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return "--------------";
        } catch (IOException e) {
            e.printStackTrace();

            return "--------------";
        }
        return img_name;
    }

    public  boolean isPackageInstalled(String packageName) {
        try {
            return act.getPackageManager().getApplicationInfo(packageName, 0).enabled;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
   public void show_camera_config_dialog(int img_index)
    {

//        photo_index=svars.image_indexes.id_photo;
//        startActivityForResult(new Intent("sparta.icaochecker.doc_camera"),1);
        View aldv= LayoutInflater.from(act).inflate(R.layout.dialog_config_camera_parameters,null);
        final AlertDialog ald =new AlertDialog.Builder(act)
                .setView(aldv)
                .show();
        ald.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RadioButton icao_rdb=aldv.findViewById(R.id.icao_rdb),
                doc_scanner_rdb=aldv.findViewById(R.id.doc_scanner_rdb);
        String icao_package_name="sparta.icaochecker";
        RadioGroup camera_types=aldv.findViewById(R.id.camera_types);
        if(!isPackageInstalled(icao_package_name))
        {
            icao_rdb.setEnabled(false);
            icao_rdb.setText(icao_rdb.getText()+"!!! (Imaging package not installed)");
            doc_scanner_rdb.setEnabled(false);
            doc_scanner_rdb.setText(doc_scanner_rdb.getText()+"!!! (Imaging package not installed)");
//            if(svars.photo_camera_type(act,img_index)==1||svars.photo_camera_type(act,img_index)==2)
//            {
//                svars.set_photo_camera_type(act,img_index,rb_index+1);
//
//            }
        }
        ((RadioButton) camera_types.getChildAt(svars.photo_camera_type(act,img_index)-1)).setChecked(true);
        camera_types.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                for (int rb_index=0;rb_index<camera_types.getChildCount();rb_index++)
                {
                    Log.e("RADIOBUTTON :",""+rb_index+"/"+camera_types.getChildCount());
                    if(((RadioButton) camera_types.getChildAt(rb_index)).isChecked()){
                        Log.e("RADIOBUTTON :"," OK "+rb_index);
                        svars.set_photo_camera_type(act,img_index,rb_index+1);
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

                svars.set_photo_camera_type(act,img_index,svars.default_photo_camera_type(act));
                ((RadioButton) camera_types.getChildAt(svars.photo_camera_type(act,img_index)-1)).setChecked(true);

            }
        });

    }

  public void show_remember_me_config_dialog()
    {

//        photo_index=svars.image_indexes.id_photo;
//        startActivityForResult(new Intent("sparta.icaochecker.doc_camera"),1);
        View aldv= LayoutInflater.from(act).inflate(R.layout.dialog_config_remember_me,null);
        final AlertDialog ald =new AlertDialog.Builder(act)
                .setView(aldv)
                .show();
        ald.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CheckBox remember_username_chk=aldv.findViewById(R.id.remember_username_chk),
                remember_password_chk=aldv.findViewById(R.id.remember_password_chk);

        remember_username_chk.setChecked(svars.remember(act,svars.remember_indexes.username));
        remember_password_chk.setChecked(svars.remember(act,svars.remember_indexes.password));

        remember_username_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                svars.set_remember(act,svars.remember_indexes.username,isChecked);
            }
        });
        remember_password_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                svars.set_remember(act,svars.remember_indexes.password,isChecked);

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
                svars.set_remember(act,svars.remember_indexes.username,svars.default_remember_username);
                svars.set_remember(act,svars.remember_indexes.password,svars.default_remember_password);


                remember_username_chk.setChecked(svars.remember(act,svars.remember_indexes.username));
                remember_password_chk.setChecked(svars.remember(act,svars.remember_indexes.password));


            }
        });

    }
boolean taking_picture=false;
private int photo_index=0;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK&&photo_index!=0) {
            int photo_camera_type = svars.photo_camera_type(act, photo_index);
            Bitmap bitmap = null;
            String data_url = null;
            switch (photo_camera_type) {
                case 1:
                case 2:
                    Uri uri = data.getExtras().getParcelable("scannedResult");
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        getContentResolver().delete(uri, null, null);
                        data_url = save_app_image(bitmap);
                        data.putExtra("ImageUrl", data_url);
                        data.putExtra("ImageIndex", photo_index);

                        bitmap.recycle();
                        bitmap = null;
                    } catch (Exception ex) {

                    }

                    break;

                case 3:

                    data_url=data.getStringExtra("ImageUrl");
                    data.putExtra("ImageUrl", data_url);
                    data.putExtra("ImageIndex", photo_index);
break;

case 5:

                    Bundle extras = data.getExtras();

                    bitmap = (Bitmap) extras.get("data");
                    data_url = save_app_image(bitmap);
                    data.putExtra("ImageUrl", data_url);
                    data.putExtra("ImageIndex", photo_index);
                    bitmap.recycle();
                    bitmap = null;


                    break;
                case 4:
                    bitmap = BitmapFactory.decodeByteArray(sparta_camera.latest_image, 0, sparta_camera.latest_image.length, null);
                    data_url = save_app_image(bitmap);
                    data.putExtra("ImageUrl", data_url);
                    data.putExtra("ImageIndex", photo_index);
                    bitmap.recycle();
                    bitmap = null;

                    sparta_camera.latest_image = null;
                    break;





            }
            try{
                data.putExtra("FaceUrl", face_handler.extract_face(svars.current_app_config(Realm.context).file_path_employee_data+data_url));

            }catch (Throwable ex){}
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }

                    }
                    public void take_photo ( int image_index,String sid)
                {
                    photo_index = image_index;
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
                             Intent intt=new Intent(act,sparta.realm.spartautils.biometrics.face.SpartaFaceCamera.class);
                             intt.putExtra("sid",sid);
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
            }


