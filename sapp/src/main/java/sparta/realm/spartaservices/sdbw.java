package sparta.realm.spartaservices;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;


import com.fpcore.FPMatch;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Stopwatch;
import com.google.common.reflect.ClassPath;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.os.EnvironmentCompat;
import dalvik.system.DexFile;
import sparta.realm.Activities.RecordList;
import sparta.realm.Activities.SpartaAppCompactActivity;
import sparta.realm.BuildConfig;
import sparta.realm.R;
import sparta.realm.spartamodels.db_class;
import sparta.realm.spartamodels.dependant;
import sparta.realm.spartamodels.dyna_data;
import sparta.realm.spartamodels.dyna_data_obj;
import sparta.realm.spartamodels.dynamic_property;
import sparta.realm.spartamodels.member;
import sparta.realm.spartamodels.geo_fence;
import sparta.realm.spartamodels.percent_calculation;
import sparta.realm.spartamodels.sdb_model;
import sparta.realm.spartautils.Gpsprobe_r;
import sparta.realm.spartautils.app_control.models.sparta_app_version;
import sparta.realm.spartautils.face.face_handler;
import sparta.realm.spartautils.fp.sdks.fgtit.utils.ExtApi;
import sparta.realm.spartautils.s_bitmap_handler;
import sparta.realm.spartautils.s_cryptor;
import sparta.realm.spartautils.sparta_loc_util;
import sparta.realm.spartautils.sparta_mail_probe;
import sparta.realm.spartautils.sparta_string_compairer;
import sparta.realm.spartautils.svars;
import sparta.spartaannotations.DynamicProperty;

import static sparta.realm.spartautils.fp.fp_handler_stf_usb_8_inch.main_fmd_format;


/**
 * Created by Thompsons on 01-Feb-17.
 */

public class sdbw {
   static Context act;
    public static sdb_model main_db=null;
    public static SQLiteDatabase database;
    public static sdbw sd;
    public static boolean loaded_db=false;
    public sdbw(Context act)
    {
        this.act=act;
        if(!loaded_db)
        {
            //setup_db_model();
            try {
                setup_db();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }



public sdbw getInstance()
{
    if(sd==null)
    {
sd=new sdbw(act);
        return sd;
    }else {
        return sd;
    }
}







    static void setup_db_model()
    {
        sdb_model dbm=new sdb_model();
        dbm.db_name= svars.DB_NAME;
        dbm.db_path=act.getExternalFilesDir(null).getAbsolutePath()+"/"+svars.DB_NAME;
        dbm.db_password=svars.DB_PASS;

        SQLiteDatabase.loadLibs(act);



        //  SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile.getAbsolutePath(), , null);
        database = SQLiteDatabase.openOrCreateDatabase(dbm.db_path, dbm.db_password, null);

        if(svars.version_action_done(act, svars.version_action.DB_CHECK)){  loaded_db=true;return;}

        sdb_model.sdb_table.column id=new sdb_model.sdb_table.column(false,"id","INTEGER");
        id.extra_params="PRIMARY KEY AUTOINCREMENT";
        sdb_model.sdb_table.column reg_time=new sdb_model.sdb_table.column(false,"reg_time","DATETIME","(datetime('now','localtime'))");
        sdb_model.sdb_table.column sync_status=new sdb_model.sdb_table.column(false,"sync_status");
        sdb_model.sdb_table.column transaction_no=new sdb_model.sdb_table.column(false,"transaction_no");
        sdb_model.sdb_table.column sid=new sdb_model.sdb_table.column(true,"sid");
        sdb_model.sdb_table.column sync_var=new sdb_model.sdb_table.column(true,"sync_var");
        sdb_model.sdb_table.column data_status=new sdb_model.sdb_table.column(true,"data_status");
        sdb_model.sdb_table.column user_id=new sdb_model.sdb_table.column(false,"user_id");
        sdb_model.sdb_table.column data_usage_frequency=new sdb_model.sdb_table.column(true,"data_usage_frequency");
        sdb_model.sdb_table.column lat=new sdb_model.sdb_table.column(false,"lat");
        sdb_model.sdb_table.column lon=new sdb_model.sdb_table.column(false,"lon");

        ArrayList<sdb_model.sdb_table.column> common_columns=new ArrayList();
        common_columns.add(id);
        common_columns.add(user_id);
        common_columns.add(reg_time);
        common_columns.add(data_status);
        common_columns.add(transaction_no);
        common_columns.add(sync_status);
        common_columns.add(sid);
        common_columns.add(sync_var);
        common_columns.add(data_usage_frequency);


//        List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
//        classLoadersList.add(ClasspathHelper.contextClassLoader());
//        classLoadersList.add(ClasspathHelper.staticClassLoader());
//        Reflections reflections = new Reflections(new ConfigurationBuilder()
//                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
//                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
//                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("org.your.package"))));
        try {
            dynamic_property dpex = new dynamic_property(null, null, null);
            Class<?> cex = dpex.getClass();
            String dpex_path=cex.getName();
            Log.e("Class path =>",""+dpex_path);
            String package_path = dpex_path.substring(0, dpex_path.lastIndexOf('.'));
            Log.e("Package path =>",""+package_path);
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if (info.getName().startsWith(package_path)) {
                    final Class<?> clazz = info.load();
                    Log.e("Class reflected =>",""+clazz.getName());
                    // do something with your clazz
                }
            }
        }catch (Exception ex){}
        sdb_model.sdb_table member_info_table=new sdb_model.sdb_table("member_info_table");
        member_info_table.columns.addAll(common_columns);
        member_info_table.columns.add(new sdb_model.sdb_table.column(true,"first_name"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(true,"last_name"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(true,"full_name"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(true,"idno"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"dob"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"phone_no"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"email"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"category"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"sub_category"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"department"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"nationality"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"nfc"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"kra"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"nssf"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"nhif"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"employee_no"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"gender"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"reg_start_time"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"reg_end_time"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"site_id"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false,"job_department"));

        sdb_model.sdb_table member_data_table=new sdb_model.sdb_table("member_data_table");

        member_data_table.columns.addAll(common_columns);
        member_data_table.columns.add(new sdb_model.sdb_table.column(true,"data_type"));
        member_data_table.columns.add(new sdb_model.sdb_table.column(false,"data_index"));
        member_data_table.columns.add(new sdb_model.sdb_table.column(false,"data"));
        member_data_table.columns.add(new sdb_model.sdb_table.column(false,"data_format"));
        member_data_table.columns.add(new sdb_model.sdb_table.column(false,"data_storage_mode"));
        member_data_table.columns.add(new sdb_model.sdb_table.column(true,"employee_id"));


        sdb_model.sdb_table verification_activity_table=new sdb_model.sdb_table("verification_activity_table");

        verification_activity_table.columns.addAll(common_columns);
        verification_activity_table.columns.add(new sdb_model.sdb_table.column(false,"activity_name"));
        verification_activity_table.columns.add(new sdb_model.sdb_table.column(false,"activity_result_uom"));
        verification_activity_table.columns.add(new sdb_model.sdb_table.column(false,"workers_limit"));
        verification_activity_table.columns.add(new sdb_model.sdb_table.column(false,"activity_operation_mode"));
        verification_activity_table.columns.add(new sdb_model.sdb_table.column(false,"working_field"));
        verification_activity_table.columns.add(new sdb_model.sdb_table.column(false,"working_period_from"));
        verification_activity_table.columns.add(new sdb_model.sdb_table.column(false,"working_period_to"));

        sdb_model.sdb_table employee_verification_activities_table=new sdb_model.sdb_table("employee_verification_activities_table");

        employee_verification_activities_table.columns.addAll(common_columns);
        employee_verification_activities_table.columns.add(new sdb_model.sdb_table.column(true,"activity_id"));
        employee_verification_activities_table.columns.add(new sdb_model.sdb_table.column(true,"employee_id"));
        employee_verification_activities_table.columns.add(new sdb_model.sdb_table.column(false,"result"));
        employee_verification_activities_table.columns.add(new sdb_model.sdb_table.column(false,"working_field"));
        employee_verification_activities_table.columns.add(new sdb_model.sdb_table.column(false,"result_update_time"));
        employee_verification_activities_table.columns.add(new sdb_model.sdb_table.column(true,"site_id"));

        sdb_model.sdb_table geo_fences_table=new sdb_model.sdb_table("geo_fences_table");

        geo_fences_table.columns.addAll(common_columns);
        geo_fences_table.columns.add(new sdb_model.sdb_table.column(false,"fence_type"));
        geo_fences_table.columns.add(new sdb_model.sdb_table.column(false,"parent_type"));
        geo_fences_table.columns.add(new sdb_model.sdb_table.column(true,"parent_id"));


        sdb_model.sdb_table geo_fence_points_table=new sdb_model.sdb_table("geo_fence_points_table");

        geo_fence_points_table.columns.addAll(common_columns);
        geo_fence_points_table.columns.add(new sdb_model.sdb_table.column(true,"fence_id"));
        geo_fence_points_table.columns.add(new sdb_model.sdb_table.column(false,"lat"));
        geo_fence_points_table.columns.add(new sdb_model.sdb_table.column(false,"lon"));
        geo_fence_points_table.columns.add(new sdb_model.sdb_table.column(false,"radius"));




        sdb_model.sdb_table employee_verifications_table=new sdb_model.sdb_table("employee_verifications_table");

        employee_verifications_table.columns.addAll(common_columns);
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(true,"employee_id"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(false,"verification_mode"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(false,"verification_mode_index"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(false,"verification_type"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(false,"verification_data"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(false,"lat"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(false,"lon"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(true,"verification_field"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(true,"verification_activity"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(true,"site_id"));


        sdb_model.sdb_table member_clock_table=new sdb_model.sdb_table("member_clock_table");

        member_clock_table.columns.addAll(common_columns);
        member_clock_table.columns.add(new sdb_model.sdb_table.column(true,"employee_id"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"clock_in_time"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"clock_in_verification_mode"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"clock_in_verification_mode_index"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"clock_in_verification_data"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"clock_in_transaction_no"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"clock_in_user_id"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"clock_in_lat"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"clock_in_lon"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"clock_out_lat"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"clock_out_lon"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"clock_out_verification_mode"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"clock_out_verification_mode_index"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"clock_out_verification_data"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"clock_out_transaction_no"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"clock_out_user_id"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"clock_out_time"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false,"verification_field"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(true,"site_id"));

        sdb_model.sdb_table employee_checking_table=new sdb_model.sdb_table("employee_checking_table");

        employee_checking_table.columns.addAll(common_columns);
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(true,"employee_id"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"check_in_time"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"check_in_verification_mode"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"check_in_verification_mode_index"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"check_in_verification_data"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"check_in_transaction_no"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"check_in_user_id"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"check_in_lat"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"check_in_lon"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"check_out_lat"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"check_out_lon"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"check_out_verification_mode"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"check_out_verification_mode_index"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"check_out_verification_data"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"check_out_transaction_no"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"check_out_user_id"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"check_out_time"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false,"verification_field"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(true,"site_id"));


        sdb_model.sdb_table weighbridge_weighment_table=new sdb_model.sdb_table("weighbridge_weighment_table");

        weighbridge_weighment_table.columns.addAll(common_columns);
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(true,"employee_id"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"first_weight_verification_mode"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"first_weight_verification_mode_index"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"first_weight_verification_data"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"first_weight_transaction_no"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"first_weight_user_id"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"first_weight_lat"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"first_weight_lon"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"first_weight"));

        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"second_weight"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"second_weight_lat"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"second_weight_lon"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"second_weight_verification_mode"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"second_weight_verification_mode_index"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"second_weight_verification_data"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"second_weight_transaction_no"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"second_weight_user_id"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"second_weight_time"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"verification_field"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"site_id"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(true,"vehicle_id"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"employee_vehicle_assignment_id"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false,"vehicle_plate_no"));



        sdb_model.sdb_table employee_vehicle_assignment_table=new sdb_model.sdb_table("employee_vehicle_assignment_table");

        employee_vehicle_assignment_table.columns.addAll(common_columns);
        employee_vehicle_assignment_table.columns.add(new sdb_model.sdb_table.column(true,"employee_id"));
        employee_vehicle_assignment_table.columns.add(new sdb_model.sdb_table.column(true,"vehicle_id"));
        employee_vehicle_assignment_table.columns.add(new sdb_model.sdb_table.column(false,"vehicle_plate_no"));
        employee_vehicle_assignment_table.columns.add(new sdb_model.sdb_table.column(false,"vehicle_type"));



        sdb_model.sdb_table device_room_access_ids_table=new sdb_model.sdb_table("device_room_access_ids_table");

        device_room_access_ids_table.columns.addAll(common_columns);
        device_room_access_ids_table.columns.add(new sdb_model.sdb_table.column(true,"room_id"));
        device_room_access_ids_table.columns.add(new sdb_model.sdb_table.column(false,"room_index"));
        device_room_access_ids_table.columns.add(new sdb_model.sdb_table.column(false,"room_description"));



        sdb_model.sdb_table employee_room_access_table=new sdb_model.sdb_table("employee_room_access_table");

        employee_room_access_table.columns.addAll(common_columns);
        employee_room_access_table.columns.add(new sdb_model.sdb_table.column(true,"employee_id"));
        employee_room_access_table.columns.add(new sdb_model.sdb_table.column(true,"room_id"));
        employee_room_access_table.columns.add(new sdb_model.sdb_table.column(false,"room_description"));





        sdb_model.sdb_table dyna_data_table=new sdb_model.sdb_table("dyna_data_table");

        dyna_data_table.columns.addAll(common_columns);
        dyna_data_table.columns.add(new sdb_model.sdb_table.column(true,"data_type"));
        dyna_data_table.columns.add(new sdb_model.sdb_table.column(false,"data_code"));
        dyna_data_table.columns.add(new sdb_model.sdb_table.column(true,"parent"));
        dyna_data_table.columns.add(new sdb_model.sdb_table.column(false,"data"));


        sdb_model.sdb_table activity_fields_table=new sdb_model.sdb_table("activity_fields_table");

        activity_fields_table.columns.addAll(common_columns);
        activity_fields_table.columns.add(new sdb_model.sdb_table.column(false,"field_name"));
        activity_fields_table.columns.add(new sdb_model.sdb_table.column(true,"field_department_id"));




        sdb_model.sdb_table user_table=new sdb_model.sdb_table("user_table");

        user_table.columns.addAll(common_columns);
        user_table.columns.add(new sdb_model.sdb_table.column(false,"user_fullname"));
        user_table.columns.add(new sdb_model.sdb_table.column(false,"username"));
        user_table.columns.add(new sdb_model.sdb_table.column(false,"password"));


        sdb_model.sdb_table app_versions_table=new sdb_model.sdb_table("app_versions_table");

        app_versions_table.columns.addAll(common_columns);
        app_versions_table.columns.add(new sdb_model.sdb_table.column(false,"version_name"));
        app_versions_table.columns.add(new sdb_model.sdb_table.column(false,"version_code"));
        app_versions_table.columns.add(new sdb_model.sdb_table.column(false,"release_name"));
        app_versions_table.columns.add(new sdb_model.sdb_table.column(false,"release_time"));
        app_versions_table.columns.add(new sdb_model.sdb_table.column(false,"release_notes"));
        app_versions_table.columns.add(new sdb_model.sdb_table.column(false,"creation_time"));
        app_versions_table.columns.add(new sdb_model.sdb_table.column(false,"download_link"));
        app_versions_table.columns.add(new sdb_model.sdb_table.column(false,"file"));



        sdb_model.sdb_table leave_application_table=new sdb_model.sdb_table("leave_application_table");

        leave_application_table.columns.addAll(common_columns);
        leave_application_table.columns.add(new sdb_model.sdb_table.column(true,"employee_id"));
        leave_application_table.columns.add(new sdb_model.sdb_table.column(false,"leave_id"));
        leave_application_table.columns.add(new sdb_model.sdb_table.column(false,"from_time"));
        leave_application_table.columns.add(new sdb_model.sdb_table.column(false,"to_time"));
        leave_application_table.columns.add(new sdb_model.sdb_table.column(false,"note"));



        sdb_model.sdb_table employee_leave_table=new sdb_model.sdb_table("employee_leave_table");

        employee_leave_table.columns.addAll(common_columns);
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(true,"employee_id"));
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(true,"leave_id"));
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(false,"leave_name"));
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(false,"total_days"));
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(false,"accumulated_days"));
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(false,"leave_balance"));
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(false,"deduction_mode"));
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(false,"authenticate"));
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(false,"minimum_prior_days"));


        sdb_model.sdb_table employee_random_calls_table=new sdb_model.sdb_table("employee_random_calls_table");

        employee_random_calls_table.columns.addAll(common_columns);
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(true,"employee_id"));
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(true,"lat_raised"));
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(false,"lon_raised"));
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(false,"time_raised"));
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(false,"time_checked"));
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(false,"lat_checked"));
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(false,"lon_checked"));
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(false,"default_reason"));
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(false,"answered"));







        dbm.tables.add(user_table);
        dbm.tables.add(app_versions_table);

        dbm.tables.add(dyna_data_table);
        dbm.tables.add(activity_fields_table);
        dbm.tables.add(verification_activity_table);
        dbm.tables.add(employee_vehicle_assignment_table);
        dbm.tables.add(device_room_access_ids_table);
        dbm.tables.add(employee_room_access_table);
        dbm.tables.add(geo_fences_table);
        dbm.tables.add(geo_fence_points_table);

        dbm.tables.add(member_info_table);
        dbm.tables.add(member_data_table);
        dbm.tables.add(employee_verifications_table);
        dbm.tables.add(employee_verification_activities_table);
        dbm.tables.add(member_clock_table);
        dbm.tables.add(employee_checking_table);
        dbm.tables.add(weighbridge_weighment_table);
        dbm.tables.add(employee_random_calls_table);


        dbm.tables.add(employee_leave_table);
        dbm.tables.add(leave_application_table);


        main_db=dbm;



        for(sdb_model.sdb_table tb:dbm.tables) {
            try {
                Cursor cursor1 = database.rawQuery("SELECT * FROM "+tb.table_name, null);
                cursor1.moveToFirst();
                if (!cursor1.isAfterLast()) {
                    do {
                        cursor1.getString(0);
                    } while (cursor1.moveToNext());
                }
                cursor1.close();
            } catch (Exception e) {
                database.execSQL(tb.create_statement());
                String crt_stt=tb.create_indexes_statement();
                if(crt_stt.length()>1&crt_stt.contains(";"))
                {

                    for(String st:crt_stt.split(";"))
                    {
                        try{
                            Log.e("DB :","Index statement creating =>"+st);
                            database.execSQL(st);
                            Log.e("DB :","Index statement created =>"+st);
                        }catch (Exception ex1){}

                    }



                }
                continue;
            }

            for (sdb_model.sdb_table.column col : tb.columns) {
                try {
                    Cursor cursor1 = database.rawQuery("SELECT count(" + col.column_name + ") FROM "+tb.table_name, null);
                    cursor1.moveToFirst();
                    if (!cursor1.isAfterLast()) {
                        do {
                            cursor1.getString(0);
                        } while (cursor1.moveToNext());
                    }
                    cursor1.close();
                } catch (Exception e) {
                    database.execSQL("ALTER TABLE "+tb.table_name+" ADD COLUMN " + col.column_name + " "+col.data_type+" "+col.default_value);
                }
            }
        }
        svars.set_version_action_done(act, svars.version_action.DB_CHECK);
        Log.e("DB","Finished DB Verification");
        main_db=null;
        loaded_db=true;

    }
    void setup_db() throws IOException {
        sdb_model dbm=new sdb_model();
        dbm.db_name=svars.DB_NAME;
        dbm.db_path=svars.WORKING_APP.file_path_db(act);
     //   dbm.db_path=act.getExternalFilesDir(null).getAbsolutePath()+"/"+svars.DB_NAME;
        dbm.db_password=svars.DB_PASS;

        SQLiteDatabase.loadLibs(act);



        //  SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile.getAbsolutePath(), , null);
        database = SQLiteDatabase.openOrCreateDatabase(dbm.db_path, dbm.db_password, null);
      // if(svars.version_action_done(act, svars.version_action.DB_CHECK)){  loaded_db=true;return;}

        svars.set_photo_camera_type(act,svars.image_indexes.profile_photo,1);

        dynamic_property dpex = new dynamic_property(null, null, null);
        Class<?> cex = dpex.getClass();
        String dpex_path = cex.getName();

        String package_path = dpex_path.substring(0, dpex_path.lastIndexOf('.'));

        String codepath=act.getPackageCodePath();

        DexFile df = new DexFile(codepath);
        Stopwatch sw=new Stopwatch();
        sw.start();
        // List<String> resultList = Lists.newArrayList(Collections.list(df.entries())).stream().filter(s -> s.startsWith(package_path)).collect(Collectors.toList());
//        for (String iter :resultList) {
//            String s = iter;
//            Log.e("Classes reflected 3 =>", "" + s);
//
//        }
//        Log.e("Classes reflected 3 in :", "" + sw.elapsed(TimeUnit.MICROSECONDS));
        sw.stop();
        sw.reset();
        sw.start();
        sdb_model.sdb_table.column id=new sdb_model.sdb_table.column(false,"id","INTEGER");
        id.extra_params="PRIMARY KEY AUTOINCREMENT";
        sdb_model.sdb_table.column reg_time=new sdb_model.sdb_table.column(false,"reg_time","DATETIME","(datetime('now','localtime'))");
        sdb_model.sdb_table.column sync_status=new sdb_model.sdb_table.column(false,"sync_status");
        sdb_model.sdb_table.column transaction_no=new sdb_model.sdb_table.column(false,"transaction_no");
        sdb_model.sdb_table.column sid=new sdb_model.sdb_table.column(true,"sid");
        sdb_model.sdb_table.column sync_var=new sdb_model.sdb_table.column(true,"sync_var");
        sdb_model.sdb_table.column data_status=new sdb_model.sdb_table.column(true,"data_status");
        sdb_model.sdb_table.column user_id=new sdb_model.sdb_table.column(false,"user_id");
        sdb_model.sdb_table.column data_usage_frequency=new sdb_model.sdb_table.column(true,"data_usage_frequency");
        sdb_model.sdb_table.column lat=new sdb_model.sdb_table.column(false,"lat");
        sdb_model.sdb_table.column lon=new sdb_model.sdb_table.column(false,"lon");

        ArrayList<sdb_model.sdb_table.column> common_columns=new ArrayList();
        common_columns.add(id);
        common_columns.add(user_id);
        common_columns.add(reg_time);
        common_columns.add(data_status);
        common_columns.add(transaction_no);
        common_columns.add(sync_status);
        common_columns.add(sid);
        common_columns.add(sync_var);
        common_columns.add(data_usage_frequency);



        for (Enumeration<String> iter = df.entries(); iter.hasMoreElements();) {
            String s = iter.nextElement();
            if(s.startsWith(package_path))
            {
                 try {
                    Class<?> clazz = Class.forName(s);


                    db_class db_tb_temp=new db_class("");
                    if(db_tb_temp.getClass().isAssignableFrom(clazz)&&!db_tb_temp.getClass().getName().equalsIgnoreCase(clazz.getName()))
                    {
                        Log.e("Classes reflected =>", "DB :" + s);
                        sdb_model.sdb_table db_tb=table_from_dyna_property_class(clazz);
                        if(db_tb==null){continue;}
                        db_tb.columns.addAll(common_columns);
                     //   dbm.tables.add(db_tb);
                        try {
                            Cursor cursor1 = database.rawQuery("SELECT * FROM "+db_tb.table_name, null);
                            cursor1.moveToFirst();
                            if (!cursor1.isAfterLast()) {
                                do {
                                    cursor1.getString(0);
                                } while (cursor1.moveToNext());
                            }
                            cursor1.close();
                        } catch (Exception e) {
                            database.execSQL(db_tb.create_statement());
                            String crt_stt=db_tb.create_indexes_statement();
                            if(crt_stt.length()>1&crt_stt.contains(";"))
                            {

                                for(String st:crt_stt.split(";"))
                                {
                                    try{
                                        Log.e("DB :","Index statement creating =>"+st);
                                        database.execSQL(st);
                                        Log.e("DB :","Index statement created =>"+st);
                                    }catch (Exception ex1){}

                                }



                            }
                            continue;
                        }

                        for (sdb_model.sdb_table.column col : db_tb.columns) {
                            try {
                                Cursor cursor1 = database.rawQuery("SELECT count(" + col.column_name + ") FROM "+db_tb.table_name, null);
                                cursor1.moveToFirst();
                                if (!cursor1.isAfterLast()) {
                                    do {
                                        cursor1.getString(0);
                                    } while (cursor1.moveToNext());
                                }
                                cursor1.close();
                            } catch (Exception e) {
                                database.execSQL("ALTER TABLE "+db_tb.table_name+" ADD COLUMN " + col.column_name + " "+col.data_type+" "+col.default_value);
                            }
                        }
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                // Object date = clazz.newInstance();
            }

        }

        Log.e("Classes reflected :", "4 :" + sw.elapsed(TimeUnit.MICROSECONDS));

        svars.set_version_action_done(act, svars.version_action.DB_CHECK);
        Log.e("DB","Finished DB Verification");
        main_db=null;
        loaded_db=true;

    }

    sdb_model.sdb_table table_from_dyna_property_class(Class<?> main_class)
    {

        Object main_obj=null;
        sdb_model.sdb_table tabl=null;//new sdb_model.sdb_table();
        try {
            main_obj = main_class.newInstance();
        } catch (IllegalAccessException e) {
            return null;

        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        try {
            Field ff=main_class.getField("table_name");
            ff.setAccessible(true);
            try {
                String table_name_= (String) ff.get(main_class.newInstance());
                Log.e("TABLE CLASS ", "TABLE :" +table_name_);

                tabl=new sdb_model.sdb_table(table_name_);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
           // e.printStackTrace();
        }
if(tabl==null||tabl.table_name==null||tabl.table_name.length()<1)
{
    return null;
}
//Field[] fields =concatenate(main_class.getSuperclass().getFields(),main_class.getDeclaredFields());
        for (Field field : main_class.getDeclaredFields()) {
            field.setAccessible(true); // if you want to modify private fields
            if(field.getType()== dynamic_property.class)
            {
                try {

                    Log.e("DP CLASS ", "" + field.getName());
                    Class<?> clazz = field.get(main_obj).getClass();
                    Field dyna_column_name_field = clazz.getDeclaredField("column_name");
                    Field dyna_index_field = clazz.getDeclaredField("index");
                    dyna_column_name_field.setAccessible(true);
//                  sdb_model.sdb_table.column col=new sdb_model.sdb_table.column(true,(String) dyna_column_name_field.get(field.get(main_obj)));

                    tabl.columns.add(new sdb_model.sdb_table.column((boolean) dyna_index_field.get(field.get(main_obj)),(String) dyna_column_name_field.get(field.get(main_obj))));


                }catch (Exception ex){
                    Log.e("REFLECTION ERROR =>",""+ex.getMessage());
                }
            }else {
//                try {
//                    Log.e("CLASS ", field.getName()+ " - " + field.getType());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }
        return tabl;
    }
    public static <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

    //    @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }


    public static byte[] get_file_data(String data_name)
    {


        try {


            File file = new File(data_name);

            return org.apache.commons.io.FileUtils.readFileToByteArray(file);


        }catch (Exception ex){
            Log.e("Data file retreival :","Failed "+ex.getMessage());

        }



        return  null;
    }

public void save_dependant(dependant d, String transaction_no)
{
    ContentValues cv=new ContentValues();


    cv.put("dependant_type",d.dependant_type);
    cv.put("surname",d.surname);
    cv.put("other_names",d.other_names);
    cv.put("gender",d.gender);
    if(Pattern.compile("\\d{4}-\\d{2}-\\d{2}").matcher(d.dob).matches()) {

        cv.put("dob",d.dob);
    }else{
        cv.put("dob",svars.get_db_date_from_user_date(d.dob));

    }

cv.put("birth_cert_pic",d.birth_cert_photo);
cv.put("passport_pic",d.passport_photo);
    cv.put("parent_transaction_no",transaction_no);
    cv.put("transaction_no",transaction_no+"_"+ System.currentTimeMillis());
    cv.put("user_id",svars.user_id(act));
    cv.put("sync_status","p");
    Log.e("INSERTING DEPENDANT :",""+database.insert("employee_dependants_table",null,cv));

}

    public void save_employee(member empl)
    {

        ContentValues cv=new ContentValues();




//        public dynamic_property exception_code=new dynamic_property("exception_code","exception_code",null,false);
        empl.apk_version_saving.value=BuildConfig.VERSION_NAME;// =new dynamic_property("apk_version","apk_version",null,false);
        empl.user_id.value=svars.user_id(act);// =new dynamic_property("apk_version","apk_version",null,false);
        empl.device_id.value=svars.device_id(act);// =new dynamic_property("apk_version","apk_version",null,false);




try{
    if(empl.images[svars.image_indexes.id_photo_back]!=null&&empl.images[svars.image_indexes.id_photo]!=null)
    {
        empl.images[svars.image_indexes.combined_pic]=s_bitmap_handler.combineImages(empl.images[svars.image_indexes.id_photo],empl.images[svars.image_indexes.id_photo_back]);
    }else if(empl.images[svars.image_indexes.id_photo]!=null)
    {
        empl.images[svars.image_indexes.combined_pic]=empl.images[svars.image_indexes.id_photo];

    }

}catch (Exception ex){}

        for(int i=0;i<empl.finger_print_skipping_reason.length;i++) {
            try{
                if(empl.finger_print_skipping_reason[i]==null){continue;}

                cv = new ContentValues();
                cv.put("data", empl.finger_print_skipping_reason[i]);
                cv.put("data_index", "" + (i+1));
                cv.put("transaction_no", empl.transaction_no.value);
                cv.put("data_type", "" + svars.data_type_indexes.fingerprint_skipping_reason);
                cv.put("data_storage_mode", "db");
                cv.put("sync_status", "p");
                cv.put("user_id",svars.user_id(act));

                //  sd.database.insert("member_data_table", null, cv);
                long insrt=sd.database.insert("member_data_table", null, cv);
                Log.e("Inserted =>", "fingerprint skip : "+ insrt);


            }catch (Exception ex){
                Log.e("Insert error=>", "fingerprint : "+ex.getMessage());

            }
        }
int cnt=0;
        for(int i=0;i<empl.finger_prints.length;i++) {
            try{
                if(empl.finger_prints[i]==null){continue;}

                cv = new ContentValues();
                cv.put("data", empl.finger_prints[i]);
                cv.put("data_index", "" + (i+1));
               cv.put("transaction_no", empl.transaction_no.value);
                cv.put("data_type", "" + svars.data_type_indexes.fingerprints);
               cv.put("data_storage_mode", "db");
                cv.put("sync_status", "p");
                cv.put("user_id",svars.user_id(act));

              //  sd.database.insert("member_data_table", null, cv);
                long insrt=sd.database.insert("member_data_table", null, cv);
                Log.e("Inserted =>", "fingerprint : "+ insrt);
           cnt=insrt!=-1?cnt+1:cnt;
            }catch (Exception ex){
                Log.e("Insert error=>", "fingerprint : "+ex.getMessage());

            }
        }
        empl.fp_count.value=cnt+"";
        cnt=0;
        for(int i=0;i<empl.images.length;i++) {
            try{
                if(empl.images[i]==null){continue;}
                cv = new ContentValues();
                cv.put("data", empl.images[i]);
                cv.put("data_index", "" +i);
                cv.put("transaction_no", empl.transaction_no.value);
                cv.put("data_type", "" + svars.data_type_indexes.photo);
                cv.put("data_storage_mode", "path");
                cv.put("sync_status", "p");
                cv.put("user_id",svars.user_id(act));


                long insrt=sd.database.insert("member_data_table", null, cv);
                Log.e("Inserted =>", "Image : "+ insrt);
                cnt=insrt!=-1?cnt+1:cnt;
            }catch (Exception ex){

                Log.e("Insert error=>", "Image : "+ex.getMessage());

            }
        }
         empl.img_count.value=cnt+"";
        cnt=0;
        for(int i=0;i<empl.finger_print_images.length;i++) {
            try{
                if(empl.finger_print_images[i]==null){continue;}
                cv = new ContentValues();
                cv.put("data", empl.finger_print_images[i]);
                cv.put("data_index", "" +(i+1));
                cv.put("transaction_no", empl.transaction_no.value);
                cv.put("data_type", "" + svars.data_type_indexes.fingerprint_images_jpg);
                cv.put("data_storage_mode", "path");
                cv.put("sync_status", "p");
                cv.put("user_id",svars.user_id(act));


                long insrt=sd.database.insert("member_data_table", null, cv);
                Log.e("Inserted =>", "Fp Image : "+ insrt);
                cnt=insrt!=-1?cnt+1:cnt;
            }catch (Exception ex){
                Log.e("Insert error=>", "FP Image : "+ex.getMessage());

            }

       }
        empl.fp_img_count.value=cnt+"";
        cnt=0;
        for(int i=0;i<empl.finger_print_WSQ_images.length;i++) {
            try{
                if(empl.finger_print_WSQ_images[i]==null){continue;}
                cv = new ContentValues();
                cv.put("data", empl.finger_print_WSQ_images[i]);
                cv.put("data_index", "" +(i+1));
                cv.put("transaction_no", empl.transaction_no.value);
                cv.put("data_type", "" + svars.data_type_indexes.fingerprint_images_wsq);
                cv.put("data_storage_mode", "path");
                cv.put("sync_status", "p");
                cv.put("user_id",svars.user_id(act));


                long insrt=sd.database.insert("member_data_table", null, cv);
                Log.e("Inserted =>", "Fp WSQ Image : "+ insrt);
                cnt=insrt!=-1?cnt+1:cnt;
            }catch (Exception ex){
                Log.e("Insert error=>", "FP WSQ Image : "+ex.getMessage());
            }
        }
        empl.fp_wsq_img_count.value=cnt+"";
        cnt=0;

        try {
svars.add_used_code(act,empl.payment_code.value);
            cv=load_cv_from_object(empl);
            Log.e("Inserted =>", "Record : "+ sd.database.insert(empl.table_name, null, cv));

            if (empl.payment_code.value.length() > 0) {
                // database.execSQL("UPDATE exception_codes SET data_status='u' WHERE code='"+empl.exception_code+"'");
                database.execSQL("DELETE FROM payment_codes_table WHERE pay_code='" + empl.payment_code.value + "'");
            }

        }catch (Exception ex){
            Log.e("Insert error=>", "Record : "+ex.getMessage());
        }

try{
   // asbgw.upload(empl.ssds[0],empl);
}catch (Exception ex){}



    }

    public boolean code_exists(String code)
    {

       return database.rawQuery("SELECT * FROM exception_codes WHERE code ='"+code+"' AND data_status='i'",null).moveToFirst();

    }

    public void save_employee_docs(member empl)
    {
        /*


    */

        ContentValues cv=new ContentValues();





        for(int i=0;i<empl.images.length;i++) {
            try{
                if(empl.images[i]==null){continue;}
                cv = new ContentValues();
                cv.put("data", empl.images[i]);
                cv.put("data_index", "" +i);
           //     cv.put("transaction_no", empl.transaction_no);
                cv.put("data_type", "" + svars.data_type_indexes.photo);
                cv.put("data_storage_mode", "path");
                cv.put("sync_status", "p");
                cv.put("user_id",svars.user_id(act));


                Log.e("Inserted=>", "Image :"+sd.database.insert("member_data_table", null, cv));
            }catch (Exception ex){

                Log.e("Insert error=>", "Image : "+ex.getMessage());

            }
        }


      //  sbgw.sync_up_employee_data_path();


    }
  public void update_employee(member empl)
    {
        /*


    */
        //empl.transaction_no="TA_SP"+ System.currentTimeMillis()+"_"+svars.user_id(act)+"ARTA_TA";
      //  empl.reg_end_time=""+ System.currentTimeMillis();
        ContentValues cv=new ContentValues();



        database.update("member_info_table",cv,"sid='"+empl.sid+"'",null);
try{database.execSQL("DELETE FROM member_data_table WHERE employee_id='"+empl.sid+"'");}catch (Exception ex){}
        for(int i=0;i<empl.finger_prints.length;i++) {
            try{
                if(empl.finger_prints[i]==null){continue;}
                cv = new ContentValues();
                cv.put("data", empl.finger_prints[i]);
                cv.put("data_index", "" + (i+1));
//                cv.put("transaction_no", empl.transaction_no);
//                cv.put("employee_id", empl.sid);
                cv.put("data_type", "" + svars.data_type_indexes.fingerprints);
               cv.put("data_storage_mode", "db");
                cv.put("sync_status", "p");
                cv.put("user_id",svars.user_id(act));

              //  sd.database.insert("member_data_table", null, cv);
                Log.e("Inserted =>", "fingerprint : "+ sd.database.insert("member_data_table", null, cv));
            }catch (Exception ex){
                Log.e("Inserted =>", "fingerprint : "+i);

            }
        }
        for(int i=0;i<empl.images.length;i++) {
            try{
                if(empl.images[i]==null){continue;}
                cv = new ContentValues();
                cv.put("data", empl.images[i]);
                cv.put("data_index", "" +i);
//                cv.put("transaction_no", empl.transaction_no);
//                cv.put("employee_id", empl.sid);
                cv.put("data_type", "" + svars.data_type_indexes.photo);
                cv.put("data_storage_mode", "path");
                cv.put("sync_status", "p");
                cv.put("user_id",svars.user_id(act));

                sd.database.insert("member_data_table", null, cv);
                Log.e("Inserted=>", "Image");
            }catch (Exception ex){}
        }
       // sbgw.sync_up_employees();

    }

    public void update_biometrics(member empl)
    {

       empl.transaction_no.value="TA_SP"+ System.currentTimeMillis()+"_"+svars.user_id(act)+"ARTA_TA";
//empl.reg_end_time=""+ System.currentTimeMillis();
        ContentValues cv=null;


try{database.execSQL("DELETE FROM member_data_table WHERE member_id='"+empl.sid.value+"'");}catch (Exception ex){}
        for(int i=0;i<empl.finger_prints.length;i++) {
            try{
                if(empl.finger_prints[i]==null){continue;}
                cv = new ContentValues();
                cv.put("data", empl.finger_prints[i]);
                cv.put("data_index", "" + (i+1));
               cv.put("transaction_no", empl.transaction_no.value);
                cv.put("data_type", "" + svars.data_type_indexes.fingerprints);
               cv.put("member_id", empl.sid.value);
               cv.put("data_storage_mode", "db");
                cv.put("sync_status", "p");
                cv.put("user_id",svars.user_id(act));

              //  sd.database.insert("member_data_table", null, cv);
                Log.e("Inserted =>", "fingerprint : "+ sd.database.insert("member_data_table", null, cv));
            }catch (Exception ex){
                Log.e("Insert error =>", "fingerprint : "+ex.getMessage());

            }
        }
        for(int i=0;i<empl.images.length;i++) {
            try{
                if(empl.images[i]==null){continue;}
                cv = new ContentValues();
                cv.put("data", empl.images[i]);
                cv.put("data_index", "" +i);
                cv.put("transaction_no", empl.transaction_no.value);
                cv.put("member_id", empl.sid.value);
                cv.put("data_type", "" + svars.data_type_indexes.photo);
                cv.put("data_storage_mode", "path");
                cv.put("sync_status", "p");
                cv.put("user_id",svars.user_id(act));

                sd.database.insert("member_data_table", null, cv);
                Log.e("Inserted=>", "Image");
            }catch (Exception ex){          Log.e("Insert error =>", "Image :"+ex.getMessage());
            }
        }

    }
/////////////////////////////////////////////UPDATE //////////////////////////

    public long update_check_period()
    {
        //Date date = svars.sparta_EA_calendar().getTime();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
        String dttt = format1.format(date);



        if(svars.update_check_time(act)==null)
        {
            return svars.regsyncinterval_mins;
        }
        Date time1=null;
        try {
            try {
                time1 = new SimpleDateFormat("HH:mm:ss").parse(format1.format(date));
            }catch (Exception ex){
                Log.e("Time Error =>",ex.getMessage());
            }
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);
            Date time2 =null;
            try{
                Log.e("DATE 2 =>"," t "+date.getTime()+" Time=>"+svars.update_check_time(act).split(" ")[1]);
                time2 = new SimpleDateFormat("HH:mm:ss").parse(svars.update_check_time(act).split(" ")[1]);
            }catch (Exception ex){ Log.e("Time Error =>",ex.getMessage());}
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            long diffference=Calendar.getInstance().getTimeInMillis()-calendar2.getTimeInMillis();
            // return (int)Math.round((double)diffference/60000);
            int diff_1=(int) ((diffference/ (1000*60)) % 60);
            return  diff_1+(((int) ((diffference / (1000*60*60)) % 24))*60);
        }catch (Exception ex){return svars.regsyncinterval_mins;}



    }

    public ArrayList<sparta_app_version> load_undownloaded_apks(String[] downloading)
{


    ArrayList<sparta_app_version> versions=new ArrayList<>();


    Cursor c = database.rawQuery("SELECT * FROM app_versions_table WHERE data_status IS NULL AND version_name > '"+ BuildConfig.VERSION_NAME+"' AND sid NOT IN ("+conccat_sql_string(downloading)+")", null);

    if (c.moveToFirst()) {
        do {

            sparta_app_version sap = new sparta_app_version();

            sap.version_id = c.getString(c.getColumnIndex("sid"));
            sap.version_name = c.getString(c.getColumnIndex("version_name"));
            sap.release_notes = c.getString(c.getColumnIndex("release_notes"));
            sap.release_name = c.getString(c.getColumnIndex("release_name"));
            sap.release_date = c.getString(c.getColumnIndex("creation_time"));
            sap.download_link = c.getString(c.getColumnIndex("download_link"));
            sap.local_path = c.getString(c.getColumnIndex("local_path"));


              /*cv.put("sid",json_sav.getString("Version_id"));
                cv.put("version_name",json_sav.getString("Version_name"));
                cv.put("release_notes",json_sav.getString("Release_notes"));
                cv.put("release_name",json_sav.getString("Release_name"));
                cv.put("creation_time",json_sav.getString("creation_time"));
                cv.put("file",json_sav.getString("Landing_page_url"));
                */


            versions.add(sap);

        } while (c.moveToNext());
    }
    c.close();
    return versions;
}

    public sparta_app_version  load_latest_apk_to_install()
    {

        Cursor c = database.rawQuery("SELECT * FROM app_versions_table WHERE data_status IS NOT NULL AND version_name > '"+BuildConfig.VERSION_NAME+"' ORDER BY sid DESC LIMIT 1", null);

        if (c.moveToFirst()) {
            do {

                sparta_app_version sap = new sparta_app_version();

                sap.version_id = c.getString(c.getColumnIndex("sid"));
                sap.version_name = c.getString(c.getColumnIndex("version_name"));
                sap.release_notes = c.getString(c.getColumnIndex("release_notes"));
                sap.release_name = c.getString(c.getColumnIndex("release_name"));
                sap.release_date = c.getString(c.getColumnIndex("creation_time"));
                sap.download_link = c.getString(c.getColumnIndex("download_link"));
                sap.local_path = c.getString(c.getColumnIndex("local_path"));


              /*  cv.put("sid",json_sav.getString("Version_id"));
                cv.put("version_name",json_sav.getString("Version_name"));
                cv.put("release_notes",json_sav.getString("Release_notes"));
                cv.put("release_name",json_sav.getString("Release_name"));
                cv.put("creation_time",json_sav.getString("creation_time"));
                cv.put("file",json_sav.getString("Landing_page_url"));
                */


                return sap;

            } while (c.moveToNext());
        }
        c.close();
        return null;
    }

    public void save_versions(JSONObject json_sav) {
        try {

            if(!version_exists(json_sav.getString("Version_id")))
            {
                ContentValues cv=new ContentValues();

                cv.put("sid",json_sav.getString("Version_id"));
                cv.put("version_name",json_sav.getString("Version_name"));
                cv.put("release_notes",json_sav.getString("Release_notes"));
                cv.put("release_name",json_sav.getString("Release_name"));
                cv.put("creation_time",json_sav.getString("creation_time"));
                cv.put("download_link",json_sav.getString("Landing_page_url"));
//cv.put("sid",appcateg_j.getString("icon"));


                Log.e("Apk version ","About to insert ");

                //;
                Log.e("Apk version ","Inserted "+database.insert("app_versions_table",null,cv));

            }else{

                Log.e("Apk version ","App exists ");

            }
        }catch (Exception exx){
            Log.e("Version insert error :"," "+exx.getMessage());
        }

    }

    public void update_downloaded_versions(String sid,String file_path) {
        try {



            ContentValues cv=new ContentValues();

            cv.put("data_status","d");
            cv.put("local_path",file_path);



            Log.e("Apk version ","About to update ");

            //;
            Log.e("Apk version ","Inserted "+database.update("app_versions_table",cv,"sid='"+sid+"'",null));



        }catch (Exception exx){
            Log.e("Version update error :"," "+exx.getMessage());
        }

    }
    boolean version_exists(String sid)
    {
        return database.rawQuery("SELECT id FROM app_versions_table WHERE sid='"+sid+"'",null).moveToFirst();
    }

    //////////////////////////////////////////////////////////////////////////////





public String get_first_weight_lid(String emloyee_id)
{
Cursor c=database.rawQuery("SELECT id FROM weighbridge_weighment_table WHERE second_weight IS NULL ORDER BY reg_time DESC LIMIT 1",null);
if(c.moveToFirst())
{
    do{
        return c.getInt(0)+"";

    }while (c.moveToNext());
}
    return null;
}

    public String validate_credentials(String username, String pass)
    {
        String name=null;
        Cursor c=database.rawQuery("SELECT * FROM user_table WHERE username=\""+username+"\" AND password=\""+pass+"\"",null);

        if(c.moveToFirst())
        {
            do{

                name=c.getString(c.getColumnIndex("user_fullname"));
                SharedPreferences.Editor saver =act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

                saver.putString("user_name",c.getString(c.getColumnIndex("user_fullname")));
                saver.putString("username", c.getString(c.getColumnIndex("username")));
                saver.putString("pass", c.getString(c.getColumnIndex("password")));
                saver.putString("user_id",c.getString(c.getColumnIndex("sid")));

                saver.commit();
                return name==null?username:name;

            }while (c.moveToNext());
        }
        c.close();
        return name;
    }

   public ArrayList<dyna_data_obj> saved_users()
    {
        ArrayList<dyna_data_obj> saved_users=new ArrayList<>();
Cursor c=database.rawQuery("SELECT * FROM user_table"/*" WHERE data_status='1'"*/,null);
if(c.moveToFirst())
{
    do{
      //  dyna_data_obj user=new dyna_data_obj(c.getString(c.getColumnIndex("id")),c.getString(c.getColumnIndex("sid")),c.getString(c.getColumnIndex("sid")),c.getString(c.getColumnIndex("sid")),c.getString(c.getColumnIndex("user_fullname")),c.getString(c.getColumnIndex("user_fullname")),c.getString(c.getColumnIndex("username")));
//        dyna_data_obj objj=new dyna_data_obj("",US.getString("id"),"","mm",US.getString("code")+"-"+US.getString("full_name") +" "+US.getString("last_name"),"",US.getString("code") );
//        objj.data_2.value=US.getBoolean("changed_password")?act.getString(R.string.active):act.getString(R.string.inactive);

        dyna_data_obj usr=new dyna_data_obj();
     usr.data.value=c.getString(c.getColumnIndex("user_fullname"));
     usr.data_2.value=c.getString(c.getColumnIndex("data_status"));
     usr.code.value=c.getString(c.getColumnIndex("username"));
     usr.sid.value=c.getString(c.getColumnIndex("sid"));


        saved_users.add(usr);

    }while (c.moveToNext());

}

        return saved_users;
    }
    public void save_user(String username, String password, String user_id, String user_name, String user_type_id)
    {

        /*
          {"$id":"1","Result":{"$id":"2","IsOkay":true,"Message":"login successfull","Result":{"$id":"3","user_id":18,"username":"00076","password":null,"full_name":"KOUASSI","account_id":1,"user_type_id":3,"status":1}},"ModuleName":"Login Module"}

         */
        ContentValues cv= new ContentValues();

        try{

            try
            {

                SharedPreferences.Editor saver =act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

                 saver.putString("user_name", user_name);
                saver.putString("username", username);
                saver.putString("pass", password);
                saver.putString("user_id",user_id);
  saver.putString("user_type",user_type_id);



                saver.commit();
            }catch (Exception ex)
            {

            }
            // cv.put("account_id",jo.getString("account_id"));
            cv.put("sid",user_id);
            cv.put("username",username);
            cv.put("password",password);








       //   database.execSQL("DELETE FROM user_table WHERE sid='"+user_id+"'");
          database.execSQL("DELETE FROM user_table");
            database.insert("user_table",null,cv);
         //   validate_credentials(username,password);

        } catch (Exception e) {
            Log.e("User saving error =>",""+e.getMessage());

            e.printStackTrace();
        }

    }

  public void register_user(String users_name, String username, String password, String user_id, String activity_status)
    {

        /*
          {"$id":"1","Result":{"$id":"2","IsOkay":true,"Message":"login successfull","Result":{"$id":"3","user_id":18,"username":"00076","password":null,"full_name":"KOUASSI","account_id":1,"user_type_id":3,"status":1}},"ModuleName":"Login Module"}

         */
        Log.e("Password  :",""+password);
        ContentValues cv= new ContentValues();

        try{


            // cv.put("account_id",jo.getString("account_id"));
            cv.put("sid",user_id);
            cv.put("username",username);
            cv.put("user_fullname",users_name);
            cv.put("password",password);
  cv.put("data_status",activity_status);








           // database.execSQL("DELETE FROM user_table");
            Log.e("User saving ",""+database.insert("user_table",null,cv));


        } catch (Exception e) {
            Log.e("User saving error =>",""+e.getMessage());

            e.printStackTrace();
        }

    }


 public void logout_user()
    {
        ContentValues cv= new ContentValues();

        try{

            try
            {

                SharedPreferences.Editor saver =act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();
                saver.putString("user_name", "");
               saver.putString("user_type", "");
                saver.putString("username", "");
                saver.putString("pass", "");
                saver.putString("user_id","");
   saver.putString("zone","");


                saver.commit();
            }catch (Exception ex)
            {

            }


         database.execSQL("DELETE FROM user_table");

        } catch (Exception e) {
            Log.e("User logout error =>",""+e.getMessage());

            e.printStackTrace();
        }

    }




    public void register_member(Boolean first_record,JSONObject jempl)
    {
        if(first_record!=null&&first_record)
        {
         //   database.beginTransaction();
            Log.e("Members =>","transaction begun");

return;
        }else if(first_record!=null&&first_record==false)
        {
            Log.e("Members =>","transaction complete");

//            database.setTransactionSuccessful();
//            database.endTransaction();
            return;
        }
        try {
            try{
                String qry="DELETE FROM member_info_table WHERE (sid='"+jempl.getString("id")+"' OR sid="+jempl.getString("id")+") AND sync_status='i'";
//Log.e("Query :",""+qry);
//Log.e("JO :",""+jempl);
                database.execSQL(qry);
            }catch(Exception ex){

                Log.e("DELETING ERROR =>",""+ex.getMessage());
            }
            if(jempl.getBoolean("is_active")){

                ContentValues cv= load_object_cv_from_JSON(jempl,new member());

                Log.e("Inserted member =>"," "+database.insert("member_info_table", null, cv));

            }else{
                 }









        }catch (Exception ex){
            Log.e("insert error",""+ex.getMessage());}

        if(first_record!=null&&first_record==false)
        {
            Log.e("Dynadata ENDING =>","transaction complete");

            database.setTransactionSuccessful();
            database.endTransaction();

        }

    }
  public void register_object(Boolean first_record,JSONObject j_obj,Object primary_obj,String service_name)
    {

        if(first_record!=null&&first_record)
        {
         database.beginTransaction();
            Log.e(service_name+"::Insertion =>","transaction begun");

return;
        }else if(first_record!=null&&first_record==false)
        {
            Log.e(service_name+"::Insertion =>","transaction complete");

            database.setTransactionSuccessful();
            database.endTransaction();
            return;
        }
        try {
            try{
                String qry="DELETE FROM "+((db_class)primary_obj).table_name+" WHERE (sid='"+j_obj.getString(((db_class)primary_obj).sid.json_name)+"' OR sid="+j_obj.getString(((db_class)primary_obj).sid.json_name)+") AND sync_status='i'";
//Log.e("Query :",""+qry);
//Log.e("JO :",""+jempl);
                database.execSQL(qry);
            }catch(Exception ex){

                Log.e("DELETING ERROR =>",""+ex.getMessage());
            }
            String is_active_key=((db_class)primary_obj).data_status.json_name;
            if((j_obj.has(is_active_key)&&j_obj.getBoolean(((db_class)primary_obj).data_status.json_name))||!j_obj.has(is_active_key)){

                ContentValues cv= load_object_cv_from_JSON(j_obj,primary_obj);

                Log.e(service_name+":: Insert result =>"," "+database.insert(((db_class)primary_obj).table_name, null, cv));

            }else{
                 }


        }catch (Exception ex){
            Log.e("insert error",""+ex.getMessage());}




    }
  public void register_employee_dependant(JSONObject jempl)
    {

        try {
            try{

                database.execSQL("DELETE FROM employee_dependants_table WHERE sid=\""+jempl.getString("id")+"\" AND sync_status='i'");
            }catch(Exception ex){

                Log.e("DELETING ERROR =>",""+ex.getMessage());
            }
            if(!jempl.getBoolean("isactive")){return;}


            ContentValues cv = load_employee_dependant_from_JSON(jempl);


            Log.e("Inserted  =>"," "+database.insert("employee_dependants_table", null, cv));



        }catch (Exception ex){
            Log.e("Employee insert error",""+ex.getMessage());}

    }
 public member load_employee(String sid)
    {




        Cursor c = database.rawQuery("SELECT * FROM member_info_table WHERE sid='"+sid+"'", null);

        if (c.moveToFirst()) {
            do {
                member emp=load_member_from_Cursor(c);
                c.close();
return emp;
            } while (c.moveToNext());
        }

c.close();
return null;
    }
    public ArrayList<member> load_all_employees(String search_tearm, int tracking_index, int offset, int limit, final String category)
    {
        Stopwatch stw=new Stopwatch();
        stw.start();
        final ArrayList<member> objs=new ArrayList<>();
        if(RecordList.search_counter!=tracking_index){Log.e("STW =>","Returning ...");return objs;}

        Cursor c =null;
        String limit_stt="LIMIT " + limit + " OFFSET " + offset;
        String qry="";
        if(search_tearm==null ||search_tearm.length()<1) {


           qry="SELECT EIT.sid,EIT.fullname,EIT.idno," +
                   "COUNT(CASE WHEN EDT.data_type='"+svars.data_type_indexes.fingerprints+"' THEN 1 END) AS fp_count," +
                   "COUNT(CASE WHEN (EDT.data_type='"+svars.data_type_indexes.photo+"' AND EDT.data_index='"+svars.image_indexes.profile_photo+"') THEN 1 END) AS img_count, " +
                    "COUNT(CASE WHEN (EDT.data_type='"+svars.data_type_indexes.photo+"' AND EDT.data_index='"+svars.image_indexes.profile_photo+"') THEN 1 END) AS face_count " +
                   "FROM member_info_table EIT " +
                   "LEFT OUTER JOIN member_data_table EDT ON EIT.sid=EDT.member_id " +
                   "WHERE category='"+category+"' "+
                   "GROUP BY EIT.id "+limit_stt;
        }else {
            //c = database.rawQuery("SELECT sid,fullname,idno,category FROM employee_info_table WHERE (UPPER(fullname) LIKE '%" + search_tearm + "%' OR UPPER(idno) LIKE '%" + search_tearm + "%')  LIMIT " + limit + " OFFSET " + offset, null);
//            qry="SELECT EIT.sid,EIT.fullname,EIT.idno,EIT.category,\n" +
//                    "(SELECT COUNT(id) FROM member_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND parent_id=EIT.sid) AS fp_count, \n" +
//                    "(SELECT COUNT(id) FROM member_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND parent_id=EIT.sid) AS img_count \n" +
//                    "FROM member_info_table EIT WHERE (UPPER(EIT.fullname) LIKE '%" + search_tearm + "%' OR UPPER(EIT.idno) LIKE '%" + search_tearm + "%') "+limit_stt;
            qry="SELECT EIT.sid,EIT.fullname,EIT.idno," +
                    "COUNT(CASE WHEN EDT.data_type='"+svars.data_type_indexes.fingerprints+"' THEN 1 END) AS fp_count," +
                    "COUNT(CASE WHEN EDT.data_type='"+svars.data_type_indexes.photo+"' THEN 1 END) AS img_count " +
                    "FROM member_info_table EIT " +
                    "LEFT OUTER JOIN member_data_table EDT ON EIT.sid=EDT.member_id " +
                  "WHERE (UPPER(EIT.fullname) LIKE '%" + search_tearm + "%' OR UPPER(EIT.idno) LIKE '%" + search_tearm + "%') AND EIT.category='"+category+"'"+
            "GROUP BY EIT.id "+limit_stt;

        }
        c=database.rawQuery(qry,null);
        Log.e("STW =>",": Raw querry : Current :"+tracking_index+"    Main :"+RecordList.search_counter+" : "+stw.elapsed(TimeUnit.MILLISECONDS));
        Log.e("STW =>",": Raw querry : "+qry);
        if(RecordList.search_counter!=tracking_index){Log.e("STW =>","Returning ...");return objs;}
        stw.reset();
        stw.start();
        if (c.moveToFirst()&&RecordList.search_counter==tracking_index) {
            do {
                //   if(Employee_list.search_counter!=tracking_index){break;}

//                employee emp=new employee();
//                emp.sid = c.getString(c.getColumnIndex("sid"));
//                emp.idno = c.getString(c.getColumnIndex("idno"));
//                emp.full_name = c.getString(c.getColumnIndex("full_name"));
//                if(for_list)
//                {
//                    emp.finger_prints=load_employee_data(c.getString(c.getColumnIndex("sid")),svars.data_type_indexes.fingerprints);
//                    emp.images=load_employee_data(c.getString(c.getColumnIndex("sid")),svars.data_type_indexes.photo);
//                }else{
//                    /**/
//                    emp.finger_prints=null;
//                    emp.images=null;
//                    emp.finger_print_images=null;
//                }
member mem=load_member_from_Cursor(c);
mem.fp_count.value=c.getString(c.getColumnIndex("fp_count"));
mem.img_count.value=c.getString(c.getColumnIndex("img_count"));
mem.face_count=c.getString(c.getColumnIndex("face_count"));
                objs.add(mem);


            } while (c.moveToNext()&&RecordList.search_counter==tracking_index);
        }else{Log.e("STW =>","Returning ...");}
        Log.e("STW =>",": Array loading : Current"+tracking_index+"    Main :"+RecordList.search_counter+" : "+stw.elapsed(TimeUnit.MILLISECONDS));


        c.close();
        c=null;




        return objs;
    }
    public ArrayList<member> load_all_employees_without_fp(String search_tearm, int tracking_index)
    {
        ArrayList<member> objs=new ArrayList<>();

        Cursor c =null;// database.rawQuery("SELECT * FROM employee_info_table WHERE sid NOT IN(SELECT employee_id FROM member_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"') AND (UPPER(full_name) LIKE '%"+search_tearm+"%' OR UPPER(idno) LIKE '%"+search_tearm+"%') LIMIT 50", null);
        if(search_tearm==null ||search_tearm.length()<1)
        {
            c = database.rawQuery("SELECT * FROM employee_info_table WHERE sid NOT IN(SELECT employee_id FROM member_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"')", null);

        }else{
            c = database.rawQuery("SELECT * FROM employee_info_table WHERE sid NOT IN(SELECT employee_id FROM member_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"') AND (UPPER(full_name) LIKE '%"+search_tearm+"%' OR UPPER(idno) LIKE '%"+search_tearm+"%') LIMIT 100", null);

        }
        //
        if (c.moveToFirst()&&RecordList.search_counter==tracking_index) {
            do {
                //  if(Employee_list.search_counter!=tracking_index){break;}
                member emp=load_member_from_Cursor(c);
                // emp.finger_prints=load_employee_data(c.getString(c.getColumnIndex("sid")),svars.data_type_indexes.fingerprints);
                //  emp.images=load_employee_data(c.getString(c.getColumnIndex("sid")),svars.data_type_indexes.photo);
                objs.add(emp);
            } while (c.moveToNext()&&RecordList.search_counter==tracking_index);
        }




        c.close();
        c=null;

        return objs;
    }

    public ArrayList<member> load_all_employees(String search_tearm, int offset, int limit)
    {
        ArrayList<member> objs=new ArrayList<>();

        Cursor c =null;
        if(search_tearm==null ||search_tearm.length()<1) {
            c = database.rawQuery("SELECT id,sid,fullname,idno,category FROM member_info_table LIMIT " + limit + " OFFSET " + offset, null);
        }else {
            c = database.rawQuery("SELECT id,sid,fullname,idno,category FROM member_info_table WHERE (UPPER(fullname) LIKE '%" + search_tearm + "%' OR UPPER(idno) LIKE '%" + search_tearm + "%') LIMIT " + limit + " OFFSET " + offset, null);

        }
        if (c.moveToFirst()) {
            do {
                member emp=load_member_from_Cursor(c);


objs.add(emp);
            } while (c.moveToNext());
        }



        try {


        }catch (Exception ex){
            Log.e("Employee insert error",""+ex.getMessage());}
return objs;
    }
public ArrayList<member> load_pending_employees()
    {
        ArrayList<member> objs=new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM member_info_table WHERE sync_status='p'", null);


        if (c.moveToFirst()) {
            do {

objs.add(load_employee_from_cursor(c,true));
            } while (c.moveToNext());
        }
c.close();



return objs;
    }
    public static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
public ArrayList<Object> load_dynamic_records(Object obj,String[] table_filters)
    {
        ArrayList<Object> objs=new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM "+((db_class)obj).table_name+(table_filters==null?"":" "+conccat_sql_filters(table_filters)), null);


        if (c.moveToFirst()) {
            do {



                    objs.add(load_object_from_Cursor(c,obj));
                   // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
c.close();



return objs;
    }
    public ArrayList<Object> load_dynamic_records(Object obj,int limit,String[] table_filters)
    {
        ArrayList<Object> objs=new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM "+((db_class)obj).table_name+(table_filters==null?"":" "+conccat_sql_filters(table_filters))+" ORDER BY data_status DESC LIMIT "+limit, null);


        if (c.moveToFirst()) {
            do {



                objs.add(load_object_from_Cursor(c,obj));
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();



        return objs;
    }
 public ArrayList<dependant> load_pending_dependants()
    {
        ArrayList<dependant> objs=new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM employee_dependants_table WHERE sync_status='p' LIMIT 1", null);


        if (c.moveToFirst()) {
            do {

objs.add(load_dependant_from_cursor(c));
            } while (c.moveToNext());
        }
c.close();



return objs;
    }



   public ArrayList<member> load_all_employees_without_fp(String search_tearm)
    {
        ArrayList<member> objs=new ArrayList<>();

        Cursor c =null;// database.rawQuery("SELECT * FROM member_info_table WHERE sid NOT IN(SELECT employee_id FROM member_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"') AND (UPPER(full_name) LIKE '%"+search_tearm+"%' OR UPPER(idno) LIKE '%"+search_tearm+"%') LIMIT 50", null);
        if(search_tearm==null ||search_tearm.length()<1)
 {
     c = database.rawQuery("SELECT * FROM member_info_table WHERE sid NOT IN(SELECT employee_id FROM member_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"')", null);

 }else{
            c = database.rawQuery("SELECT * FROM member_info_table WHERE sid NOT IN(SELECT employee_id FROM member_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"') AND (UPPER(full_name) LIKE '%"+search_tearm+"%' OR UPPER(idno) LIKE '%"+search_tearm+"%')", null);

        }
        //
        if (c.moveToFirst()) {
            do {
                member emp=new member();
//                emp.sid = c.getString(c.getColumnIndex("sid"));
//                emp.idno = c.getString(c.getColumnIndex("idno"));


               // emp.finger_prints=load_employee_data(c.getString(c.getColumnIndex("sid")),svars.data_type_indexes.fingerprints);
              //  emp.images=load_employee_data(c.getString(c.getColumnIndex("sid")),svars.data_type_indexes.photo);
objs.add(emp);
            } while (c.moveToNext());
        }

/*{,"employee_no":"2","worker_name":"TEST 1","nat_id":"5092906","reg_date":"2018-06-05T00:00:00","dob":null,"phone_no":"254704201171","email":"","marital_status_id":3,"gender_id":3,"tribe_id":1,"isactive":true,"employee_sub_category_id":2,"employee_category_id":2,"passport_url":null,"account_branch_id":8,"department_id":4,"user_id":0,"is_allsite":null,"nssf":"2007891443","nhif":"","kra_pin":"","datecomparer":1495113280,"employee_category":null,"employee_sub_category":null,"gender":null,"department":null,"marital_status":null,"account_branches":null}*/
/*
member_info_table.columns.add(new sdb_model.sdb_table.column("first_name"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("last_name"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("full_name"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("idno"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("dob"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("phone_no"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("email"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("category"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("sub_category"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("nationality"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("nfc"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("kra"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("nssf"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("nhif"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("employee_no"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("gender"));



 */


        try {


        }catch (Exception ex){
            Log.e("Employee insert error",""+ex.getMessage());}
return objs;
    }
    public String[] load_employee_data(String sid, int data_type) {

        String raw_qry="SELECT * FROM member_data_table WHERE member_id ='"+sid+"' AND data_type='"+data_type+"'";
        Cursor c = database.rawQuery(raw_qry, null);
        String[] loaded_employee_data=new String[30];
        if (c.moveToFirst()) {
            do {
                loaded_employee_data[Integer.parseInt(c.getString(c.getColumnIndex("data_index")))]=c.getString(c.getColumnIndex("data"));

            } while (c.moveToNext());
        }
        c.close();

        return loaded_employee_data;
    }

 private String[] load_employee_data_from_transaction_no(String transaction_no, int data_type) {

        String raw_qry="SELECT * FROM member_data_table WHERE transaction_no ='"+transaction_no+"' AND data_type='"+data_type+"'";
        Cursor c = database.rawQuery(raw_qry, null);
        String[] loaded_employee_data=new String[30];
        if (c.moveToFirst()) {
            do {
                loaded_employee_data[Integer.parseInt(c.getString(c.getColumnIndex("data_index")))]=c.getString(c.getColumnIndex("data"));

            } while (c.moveToNext());
        }
        c.close();

        return loaded_employee_data;
    }

    public void register_dynadata(Boolean first_record, JSONObject dyna_obj, String dyna_type, String parent)
    {

        if(first_record!=null&&first_record)
        {
            database.beginTransaction();
            Log.e("Dynadata Starting =>","transaction begun");


        }

        try {







            ContentValues cv = new ContentValues();


                cv.put("sid", dyna_obj.getString("Id"));
                cv.put("data_type", dyna_type);
            cv.put("data",dyna_obj.getString("Name"));
         try{   cv.put("code",dyna_obj.getString("Code").equalsIgnoreCase("null")?null:dyna_obj.getString("Code"));
         }catch (Exception ex){}

             try {
    cv.put("parent",dyna_obj.getString("MotherId"));
}catch (Exception ex){

}
             try {
    cv.put("parent",dyna_obj.getString("CommuneId"));
}catch (Exception ex){

}
               // cv.put("data_code",dyna_obj.getString("Code"));




            database.insert("dyna_data_table", null, cv);
            Log.d("Dynadata inserted =>",""+dyna_obj.toString());




        }catch (Exception ex){
            Log.e("Dynadata insert error",""+ex.getMessage());}
        if(first_record!=null&&first_record==false)
        {
            Log.e("Dynadata ENDING =>","transaction complete");

            database.setTransactionSuccessful();
            database.endTransaction();

        }
    }
 public void register_exception_codes(JSONObject exception_code)
    {


        try {


            ContentValues cv = new ContentValues();

            cv.put("sid", exception_code.getString("id"));
            cv.put("code", exception_code.getString("excep_code"));
          cv.put("data_status", "i");








            Log.e("code insert=>",""+database.insert("exception_codes", null, cv));



        }catch (Exception ex){
            Log.e("Dynadata insert error",""+ex.getMessage());}

    }






    public ArrayList<dyna_data_obj> load_dyna_data(String dyna_type, String parent)
    {
        ArrayList<dyna_data_obj> objs=new ArrayList<>();

       // Cursor c=database.rawQuery("SELECT * FROM dyna_data_table WHERE data_type='"+dyna_type+"'"+(parent==null?" LIMIT 1000":" AND parent='"+parent+"' LIMIT 1000"),null);
  Cursor c;
  if(dyna_type.equalsIgnoreCase("orgs")&&parent!=null&&parent.equalsIgnoreCase("11")){
      c = database.rawQuery("SELECT * FROM dyna_data_table WHERE (data_code LIKE 'UPR%' OR data_code LIKE 'UPU%') AND data_type='"+dyna_type+"'",null);

  }else{
      c = database.rawQuery("SELECT * FROM dyna_data_table WHERE data_type='"+dyna_type+"'"+(parent==null?"":" AND parent='"+parent+"'"),null);

  }

        if(c.moveToFirst())
        {
            do{



                dyna_data_obj obj=(dyna_data_obj) load_object_from_Cursor(c,new dyna_data_obj());




                objs.add(obj);

            }while (c.moveToNext());
        }
        c.close();
        return objs;
    }
   public ArrayList<dyna_data_obj> load_dyna_data(dyna_data_obj.operatio_data_type op_dyna_type, String parent)
    {
        String dyna_type=op_dyna_type.ordinal()+"";
        ArrayList<dyna_data_obj> objs=new ArrayList<>();

       // Cursor c=database.rawQuery("SELECT * FROM dyna_data_table WHERE data_type='"+dyna_type+"'"+(parent==null?" LIMIT 1000":" AND parent='"+parent+"' LIMIT 1000"),null);
  Cursor c;
  if(dyna_type.equalsIgnoreCase("orgs")&&parent!=null&&parent.equalsIgnoreCase("11")){
      c = database.rawQuery("SELECT * FROM dyna_data_table WHERE (data_code LIKE 'UPR%' OR data_code LIKE 'UPU%') AND data_type='"+dyna_type+"'",null);

  }else{
      c = database.rawQuery("SELECT * FROM dyna_data_table WHERE data_type='"+dyna_type+"'"+(parent==null?"":" AND parent='"+parent+"'"),null);

  }

        if(c.moveToFirst())
        {
            do{



                dyna_data_obj obj=(dyna_data_obj) load_object_from_Cursor(c,new dyna_data_obj());




                objs.add(obj);

            }while (c.moveToNext());
        }
        c.close();
        return objs;
    }
   public ArrayList<dyna_data> load_dyna_data_annot(dyna_data_obj.operatio_data_type op_dyna_type, String parent)
    {
        String dyna_type=op_dyna_type.ordinal()+"";
        ArrayList<dyna_data> objs=new ArrayList<>();

       // Cursor c=database.rawQuery("SELECT * FROM dyna_data_table WHERE data_type='"+dyna_type+"'"+(parent==null?" LIMIT 1000":" AND parent='"+parent+"' LIMIT 1000"),null);
  Cursor c;
  if(dyna_type.equalsIgnoreCase("orgs")&&parent!=null&&parent.equalsIgnoreCase("11")){
      c = database.rawQuery("SELECT * FROM dyna_data_table WHERE (data_code LIKE 'UPR%' OR data_code LIKE 'UPU%') AND data_type='"+dyna_type+"'",null);

  }else{

      c = database.rawQuery("SELECT * FROM dyna_data_table WHERE data_type='"+dyna_type+"'"+(parent==null?"":" AND parent='"+parent+"'"),null);

  }

        if(c.moveToFirst())
        {
            do{



              //  dyna_data obj=new dyna_data(c.getString(c.getColumnIndex("id")),c.getString(c.getColumnIndex("sid")),"",c.getString(c.getColumnIndex("data_type")),c.getString(c.getColumnIndex("data")),"",c.getString(c.getColumnIndex("id")));
               dyna_data obj=(dyna_data) load_object_from_Cursor_annot(c,new dyna_data());




                objs.add(obj);

            }while (c.moveToNext());
        }
        c.close();
        return objs;
    }
  public ArrayList<dyna_data_obj> load_dyna_data_from_parent_of_parent(String dyna_type, String parent_type, String parent_parent)
    {
        ArrayList<dyna_data_obj> objs=new ArrayList<>();


   Cursor c;

   c = database.rawQuery("SELECT * FROM dyna_data_table WHERE data_type='"+dyna_type+"' AND parent IN (SELECT sid FROM dyna_data_table WHERE data_type='"+parent_type+"' AND parent ='"+parent_parent+"')",null);

    if(c.moveToFirst())
        {
            do{
//public dyna_data_obj(String lid,String sid, String name, String data_type, String data, String parent)

//                dyna_data_obj obj=new dyna_data_obj(c.getInt(c.getColumnIndex("id"))+"",c.getString(c.getColumnIndex("sid")),c.getString(c.getColumnIndex("data")),c.getString(c.getColumnIndex("data_type")),c.getString(c.getColumnIndex("data")),c.getString(c.getColumnIndex("parent")),c.getString(c.getColumnIndex("data_code")));
//
//              try{obj.code=c.getString(c.getColumnIndex("data_code"));}catch (Exception ex){}
//              try{obj.data_2=c.getString(c.getColumnIndex("data_2"));}catch (Exception ex){}
                dyna_data_obj obj=(dyna_data_obj) load_object_from_Cursor(c,new dyna_data_obj());

                objs.add(obj);

            }while (c.moveToNext());
        }
        c.close();
        return objs;
    }


    public void populate_dummy_data()
    {
        int pref_count=0;
  int commune_count=0;
  int locality_count=0;

  database.execSQL("DELETE FROM dyna_data_table WHERE data_type IN('rg','prf','cmn','lcl','jc')");
        Log.e("Data build","Begun");
database.beginTransaction();

        for(int i=0;i<5;i++)
        {

            try {
                ContentValues cv = new ContentValues();
                cv.put("sid", i+"");
                cv.put("data_type", "rg");
              //  cv.put("parent", parent);
                cv.put("data", "REGION "+i);
                database.insert("dyna_data_table", null, cv);

                for(int j=0;j<10;j++)
                {
                    pref_count++;
                    cv = new ContentValues();
                    cv.put("sid", pref_count+"");
                    cv.put("data_type", "prf");
                      cv.put("parent", ""+i);
                    cv.put("data", "REGION "+i+" Pref"+pref_count);
                    database.insert("dyna_data_table", null, cv);

                    for(int k=0;k<10;k++)
                    {
                        commune_count++;
                        cv = new ContentValues();
                        cv.put("sid", commune_count+"");
                        cv.put("data_type", "cmn");
                        cv.put("parent", ""+pref_count);
                        cv.put("data", "REGION "+i+" Pref "+pref_count+" COMMUNE "+commune_count);
                        database.insert("dyna_data_table", null, cv);
                        for(int l=0;l<10;l++)
                        {
                            locality_count++;
                            cv = new ContentValues();
                            cv.put("sid", locality_count+"");
                            cv.put("data_type", "lcl");
                            cv.put("parent", ""+commune_count);
                            cv.put("data", "REGION "+i+" Pref "+pref_count+" COMMUNE "+commune_count+" LOCAL "+locality_count);
                            database.insert("dyna_data_table", null, cv);
                        }

                    }

                }
                Log.e("Data build","Region built "+i);
            }catch (Exception ex){

                Toast.makeText(act,"Region data has failed to be built ", Toast.LENGTH_LONG).show();

            }


          }


        for(int i=0;i<10;i++) {

            try {
                ContentValues cv = new ContentValues();
                cv.put("sid", i + "");
                cv.put("data_type", "jc");

               cv.put("data", "JOB CATEGORY " + i);
                database.insert("dyna_data_table", null, cv);
                Log.e("Data build","Job category built "+i);


            } catch (Exception ex) {
            }
        }


  for(int i=0;i<10;i++) {

            try {
                ContentValues cv = new ContentValues();
                cv.put("sid", i + "");
                cv.put("data_type", "jc");

               cv.put("data", "JOB CATEGORY " + i);
                database.insert("dyna_data_table", null, cv);
            } catch (Exception ex) {
            }
        }

        database.setTransactionSuccessful();
        database.endTransaction();
                Toast.makeText(act,"Data has been built ", Toast.LENGTH_LONG).show();

    }
    public ArrayList<dyna_data_obj> load_gendas()
    {
        return load_dyna_data("g",null);

    }
 public ArrayList<dyna_data_obj> load_fields()
    {
        return load_dyna_data("c",null);

    }
public ArrayList<dyna_data_obj> load_courses()
    {
        return load_dyna_data("crs",null);

    }
 public ArrayList<dyna_data_obj> load_gates()
    {
        return load_dyna_data("g",null);

    }
 public ArrayList<dyna_data_obj> load_buses()
    {
        return load_dyna_data("b",null);

    }
public ArrayList<dyna_data_obj> load_canteens()
    {
        return load_dyna_data("cnt",null);

    }
 public ArrayList<dyna_data_obj> load_prefecture(String region)
    {
        return load_dyna_data("prf",region);

    }
 public ArrayList<dyna_data_obj> load_commune(String prefecture)
    {
        return load_dyna_data("cmn",prefecture);

    }
 public ArrayList<dyna_data_obj> load_locality_from_prefecture(String prefecture)
    {
        return load_dyna_data_from_parent_of_parent("lcl","cmn",prefecture);

    }

    public ArrayList<dyna_data_obj> load_job_categories()
    {
        return load_dyna_data("jc",null);

    }
    public String load_dynadata_name(String sid, String data_type)
    {
        Cursor c=database.rawQuery("SELECT data FROM dyna_data_table WHERE data_type='"+data_type+"' AND sid='"+sid+"'",null);

        if(c.moveToFirst())
        {
            do{

                return c.getString(c.getColumnIndex("data"));

            }while (c.moveToNext());
        }
        c.close();
        return act.getString(R.string.unavailable_field);
    }


public ArrayList<dyna_data_obj> load_insurance_types(String contriburor_id)
    {
        return load_dyna_data("instyp",contriburor_id);

    }

public ArrayList<dyna_data_obj> load_contibs()
    {
        return load_dyna_data("contb",null);

    }


    public ArrayList<member> load_searched_employee(String search_term)
    {
        ArrayList<member> objs=new ArrayList<>();

        Cursor c=database.rawQuery("SELECT * FROM employee_info WHERE employee_no LIKE '%"+search_term+"%' LIMIT 5",null);

        if(c.moveToFirst())
        {
            do{

                member emp=new member();





                objs.add(emp);

            }while (c.moveToNext());
        }
        return objs;
    }






public ArrayList<member> load_exact_employees(String employee_no, ArrayList<String> searched_transaction_nos)
    {


        ArrayList<member> objs=new ArrayList<>();


        Cursor c = database.rawQuery("SELECT * FROM member_info_table WHERE transaction_no NOT IN ("+conccat_sql_string(searched_transaction_nos)+") AND UPPER(employee_no) LIKE '%"+employee_no.toUpperCase().trim()+"%' LIMIT 50", null);

         if (c.moveToFirst()) {
            do {

                member emp =load_employee_from_cursor(c,false);
                searched_transaction_nos.add(c.getString(c.getColumnIndex("transaction_no")));
                objs.add(emp);

            } while (c.moveToNext());
        }
c.close();
         return re_orderd_employee_list(objs,employee_no);
    }


public ArrayList<member> load_exact_employees(String surname, String gender, String dob, ArrayList<String> searched_transaction_nos)
    {


        ArrayList<member> objs=new ArrayList<>();


       // Cursor c = database.rawQuery("SELECT * FROM member_info_table WHERE transaction_no NOT IN ("+conccat_sql_string(searched_transaction_nos)+") AND surname = '"+surname+"' AND gender = '"+gender+"' AND dob = '"+dob+"' LIMIT 50", null);
        Cursor c = database.rawQuery("SELECT * FROM member_info_table WHERE surname = '"+surname+"' AND gender = '"+gender+"' AND dob = '"+dob+"' LIMIT 100", null);

         if (c.moveToFirst()) {
            do {

                member emp =load_employee_from_cursor(c,false);
                searched_transaction_nos.add(c.getString(c.getColumnIndex("transaction_no")));
                objs.add(emp);

            } while (c.moveToNext());
        }
c.close();
         return re_orderd_employee_list(objs,surname);
    }


    dependant load_dependant_from_cursor(Cursor c)
    {
        dependant d = new dependant();



        d.lid=c.getInt(c.getColumnIndex("id"))+"";
        d.dependant_type=c.getString(c.getColumnIndex("dependant_type"));
        d.surname=c.getString(c.getColumnIndex("surname"));
        d.other_names=c.getString(c.getColumnIndex("other_names"));
        d.gender=c.getString(c.getColumnIndex("gender"));
        d.dob=c.getString(c.getColumnIndex("dob"));
        d.birth_cert_photo=c.getString(c.getColumnIndex("birth_cert_pic"));
        d.passport_photo=c.getString(c.getColumnIndex("passport_pic"));
        d.parent_transaction_no=c.getString(c.getColumnIndex("parent_transaction_no"));
        d.transaction_no=c.getString(c.getColumnIndex("transaction_no"));
         d.user_id=c.getString(c.getColumnIndex("user_id"));
         d.reg_time=c.getString(c.getColumnIndex("reg_time"));

        return d;
    }
  member load_employee_from_cursor(Cursor c, boolean full_data)
    {
        member emp = new member();







        if(!full_data){return emp;}



        return emp;
    }

    ContentValues load_employee_dependant_from_JSON(JSONObject j)
    {
        ContentValues cv=new ContentValues();
        try{
            /*

                 postData[0].put("dependant_type", "" + pending_record.dependant_type);
                    postData[0].put("first_name", "" + pending_record.surname);
                    postData[0].put("last_name", "" + pending_record.other_names);
                     postData[0].put("gender", "" + pending_record.gender);

                    postData[0].put("dob", "" + pending_record.dob);
                     postData[0].put("parent_transaction_no", "" + pending_record.parent_transaction_no);
                    postData[0].put("transaction_no", "" + pending_record.transaction_no);
                        postData[0].put("user_id",""+pending_record.user_id);
                    postData[0].put("unique_code", "" + transaction_no);
                    postData[0].put("passport_photo", get_saved_doc_base64(  pending_record.passport_photo));
                    postData[0].put("birth_cert_photo", get_saved_doc_base64( pending_record.birth_cert_photo));



             */
        cv.put("sid",j.getString("id"));
        cv.put("dependant_type",j.getString("dependant_type"));
        cv.put("surname",j.getString("first_name"));
        cv.put("other_names",j.getString("last_name"));
        cv.put("gender",j.getString("gender"));
        cv.put("dob",j.getString("dob").length()>3?j.getString("dob").split("T")[0]:"");
        cv.put("birth_cert_pic",save_doc(j.getString("birth_cert_photo")));
        cv.put("passport_pic",save_doc(j.getString("passport_photo")));
        cv.put("parent_transaction_no",j.getString("parent_transaction_no"));
        cv.put("transaction_no",j.getString("transaction_no"));
        cv.put("user_id",j.getString("user_id"));
        cv.put("sync_status","i");
        cv.put("sync_var",j.getString("datecomparer"));
        }catch (Exception ex){
Log.e("JSON ERROR =>","Dependants "+ex.getMessage());
        }
        
        return cv;
        
    }
    
 public JSONObject load_JSON_from_object(Object obj) {


     //employee mem=new employee();
JSONObject jo=new JSONObject();

Field[] fieldz=concatenate(obj.getClass().getDeclaredFields(),obj.getClass().getSuperclass().getDeclaredFields());
     for (Field field : fieldz) {
         field.setAccessible(true); // if you want to modify private fields
         if(field.getType()== dynamic_property.class)
         {
             try {

               //  Log.e("DB Field ", "" + field.getName());
                 Class<?> clazz = field.get(obj).getClass();
                 Field dyna_value_field = clazz.getDeclaredField("value");
                 Field dyna_json_name_field = clazz.getDeclaredField("json_name");
                 Field dyna_storage_mode_field = clazz.getDeclaredField("storage_mode");
                  dyna_json_name_field.setAccessible(true);
                 dyna_value_field.setAccessible(true);
                 dyna_storage_mode_field.setAccessible(true);
                 String j_key=(String) dyna_json_name_field.get(field.get(obj));
             if(j_key!=null)
             {
                 int storage_mode=(int) dyna_storage_mode_field.get(field.get(obj));
                 if(storage_mode==2){
                     String data=get_saved_doc_base64((String)dyna_value_field.get(field.get(obj)));
                     if(data==error_return)
                     {
                         Log.e("DATA ERROR =>","  :: "+data);

                         //   cv.put("data_status","e");
                         jo.put(j_key,data);
                     }else{
                         jo.put(j_key,data);

                     }

                 }else{
                     jo.put(j_key,(String)dyna_value_field.get(field.get(obj)));
                 }
             }


             }catch (Exception ex){
                 Log.e("REFLECTION ERROR =>","load_JSON_from_object :: "+ex.getMessage());
             }
         }else {
             try {
                 //   Log.e("CLASS ", field.getName()+ " - " + field.getType());
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
     }
     return jo;

 }
 public member load_member_from_JSON(JSONObject j) {


     member mem=new member();


Field[] fieldz=concatenate(mem.getClass().getDeclaredFields(),mem.getClass().getSuperclass().getDeclaredFields());
     for (Field field : fieldz) {
         field.setAccessible(true); // if you want to modify private fields
         if(field.getType()== dynamic_property.class)
         {
             try {

               //  Log.e("DB Field ", "" + field.getName());
                 Class<?> clazz = field.get(mem).getClass();
                 Field dyna_value_field = clazz.getDeclaredField("value");
                 Field dyna_json_name_field = clazz.getDeclaredField("json_name");
                 dyna_json_name_field.setAccessible(true);
                 dyna_value_field.setAccessible(true);
                 String j_key=(String) dyna_json_name_field.get(field.get(mem));
                 if(j.has(j_key))
                 {
                     dyna_value_field.set(field.get(mem), j.getString(j_key));
                 }
                 //     dynamic_property dp=(dynamic_property) field.getClass();
             }catch (Exception ex){
                 Log.e("REFLECTION ERROR =>",""+ex.getMessage());
             }
         }else {
             try {
                 //   Log.e("CLASS ", field.getName()+ " - " + field.getType());
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
     }
     return mem;

 }
 public member load_member_from_Cursor(Cursor c) {


     member mem=new member();


     Field[] fieldz=concatenate(mem.getClass().getDeclaredFields(),mem.getClass().getSuperclass().getDeclaredFields());
     for (Field field : fieldz) {

         // for (Field field : mem.getClass().getDeclaredFields()) {
         field.setAccessible(true); // if you want to modify private fields
         if(field.getType()== dynamic_property.class)
         {
             try {

               //  Log.e("DB Field ", "" + field.getName());
                 Class<?> clazz = field.get(mem).getClass();
                 Field dyna_value_field = clazz.getDeclaredField("value");
                 Field dyna_db_name_field = clazz.getDeclaredField("column_name");
                 dyna_db_name_field.setAccessible(true);
                 dyna_value_field.setAccessible(true);
                 String c_key=(String) dyna_db_name_field.get(field.get(mem));
                 if(c_key!=null&&c_key.length()>1&&c.getColumnIndex(c_key)!=-1)
                 {
                     dyna_value_field.set(field.get(mem), c.getString(c.getColumnIndex(c_key)));
                 }

             }catch (Exception ex){
                 Log.e("REFLECTION ERROR =>",""+ex.getMessage());
             }
         }else {
             try {
                 //   Log.e("CLASS ", field.getName()+ " - " + field.getType());
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
     }
     return mem;

 }
public Object load_object_from_Cursor(Cursor c,Object mem) {

   Field[] fieldz=concatenate(mem.getClass().getDeclaredFields(),mem.getClass().getSuperclass().getDeclaredFields());
     for (Field field : fieldz) {

         // for (Field field : mem.getClass().getDeclaredFields()) {
         field.setAccessible(true); // if you want to modify private fields
         if(field.getType()== dynamic_property.class)
         {
             try {

               //  Log.e("DB Field ", "" + field.getName());
                 Class<?> clazz = field.get(mem).getClass();
                 Field dyna_value_field = clazz.getDeclaredField("value");
                 Field dyna_db_name_field = clazz.getDeclaredField("column_name");
                 dyna_db_name_field.setAccessible(true);
                 dyna_value_field.setAccessible(true);
                 String c_key=(String) dyna_db_name_field.get(field.get(mem));
                 if(c_key!=null&&c_key.length()>1&&c.getColumnIndex(c_key)!=-1)
                 {
                     dyna_value_field.set(field.get(mem), c.getString(c.getColumnIndex(c_key)));
                 }

             }catch (Exception ex){
                 Log.e("REFLECTION ERROR =>","load_object_from_Cursor "+ex.getMessage());
             }
         }else {
             try {
                 //   Log.e("CLASS ", field.getName()+ " - " + field.getType());9999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
     }
    try {
        return deepClone(mem);
    } catch (Exception e) {
        e.printStackTrace();
    return mem;
    }

}

public Object load_object_from_Cursor_annot(Cursor c,Object mem) {

   Field[] fieldz=concatenate(mem.getClass().getDeclaredFields(),mem.getClass().getSuperclass().getDeclaredFields());
     for (Field field : fieldz) {
         field.setAccessible(true); // if you want to modify private fields
         DynamicProperty v = field.getAnnotation(DynamicProperty.class);
         if(v==null||v.column_name()==null||(v.column_name().length()<1)||(c.getColumnIndex(v.column_name())==-1)){continue;}
         try {
             field.set(mem, c.getString(c.getColumnIndex(v.column_name())));
         } catch (IllegalAccessException e) {
             e.printStackTrace();
             Log.e("REFLECTION ERROR =>","load_object_from_Cursor "+e.getMessage()); }

       //  Log.e("SANOT",""+field.getAnnotation(DynamicProperty.class).db_column_name());



     }
   return mem;

}



//    static  MethodHandle DIRECT_GET_MH=null, DIRECT_SET_MH=null;
//    static  MethodHandles.Lookup LOOKUP=MethodHandles.lookup();
//
//    public Object load_object_from_Cursor_mh(Cursor c,Object mem) {
//
//       Field[] fieldz=concatenate(mem.getClass().getDeclaredFields(),mem.getClass().getSuperclass().getDeclaredFields());
//     for (Field field : fieldz) {
//
//         // for (Field field : mem.getClass().getDeclaredFields()) {
//         field.setAccessible(true); // if you want to modify private fields
//         if(field.getType()== dynamic_property.class)
//         {
//             try {
//
//               //  Log.e("DB Field ", "" + field.getName());
//                 Class<?> clazz = field.get(mem).getClass();
//                 Field dyna_value_field = clazz.getDeclaredField("value");
//                 Field dyna_db_name_field = clazz.getDeclaredField("column_name");
//              //   dyna_db_name_field.setAccessible(true);
//                // dyna_value_field.setAccessible(true);
//
//                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                     DIRECT_GET_MH = LOOKUP.unreflectGetter(dyna_db_name_field);
//                     DIRECT_SET_MH = LOOKUP.unreflectSetter(dyna_value_field);
//                 }
//                 String c_key="";//(String) DIRECT_GET_MH.invokeExact(VALUE);
//                 try
//                 {
//
//                     c_key=(String) DIRECT_GET_MH.invokeExact(clazz);
//                 }
//                 catch(Throwable ex)
//                 {
//                     throw new AssertionError(ex);
//                 }
//            try
//                 {
//
//                     DIRECT_SET_MH.invokeExact(clazz, c.getString(c.getColumnIndex(c_key)));
//                 }
//                 catch(Throwable ex)
//                 {
//                     throw new AssertionError(ex);
//                 }
////                  if(c_key!=null&&c_key.length()>1&&c.getColumnIndex(c_key)!=-1)
////                 {
////                     dyna_value_field.set(field.get(mem), c.getString(c.getColumnIndex(c_key)));
////                 }
//
//             }catch (Exception ex){
//                 Log.e("DR REFLECTION ERROR =>","load_object_from_Cursor "+ex.getMessage());
//             }
//         }else {
//
//
//         }
//     }
//    try {
//        return deepClone(mem);
//    } catch (Exception e) {
//        e.printStackTrace();
//    return mem;
//    }
//
//}



public ContentValues load_object_cv_from_JSON(JSONObject j, Object obj) {

        ContentValues cv=new ContentValues();



     Field[] fieldz=concatenate(obj.getClass().getDeclaredFields(),obj.getClass().getSuperclass().getDeclaredFields());
     for (Field field : fieldz) {

         // for (Field field : obj.getClass().getDeclaredFields()) {
         field.setAccessible(true); // if you want to modify private fields
         if(field.getType()== dynamic_property.class)
         {
             try {

                // Log.e("DB Field ", "" + field.getName());
                 Class<?> clazz = field.get(obj).getClass();
                 Field dyna_column_name_field = clazz.getDeclaredField("column_name");
                 Field dyna_json_name_field = clazz.getDeclaredField("json_name");
                 Field dyna_storage_mode_field = clazz.getDeclaredField("storage_mode");
                 dyna_json_name_field.setAccessible(true);
                 dyna_column_name_field.setAccessible(true);
                 dyna_storage_mode_field.setAccessible(true);
                 String j_key=(String) dyna_json_name_field.get(field.get(obj));
              if(j.has(j_key))
              {
                  int storage_mode=(int) dyna_storage_mode_field.get(field.get(obj));
                  if(storage_mode==2){
                      String data=save_doc(j.getString(j_key));
                      if(data==error_return)
                      {

                          cv.put("data_status","e");
                          cv.put((String) dyna_column_name_field.get(field.get(obj)),data);
                      }else{
                          cv.put((String) dyna_column_name_field.get(field.get(obj)),data);

                      }

                  }else{
                      cv.put((String) dyna_column_name_field.get(field.get(obj)),j.getString(j_key));
                  }
              }


field=null;
             }catch (Exception ex){
                 Log.e("REFLECTION ERROR =>",""+ex.getMessage());
             }
         }else {


         }
     }
     cv.put("sync_status","i");
     return cv;

 }
public ContentValues load_cv_from_object(Object obj) {

        ContentValues cv=new ContentValues();



     Field[] fieldz=concatenate(obj.getClass().getDeclaredFields(),obj.getClass().getSuperclass().getDeclaredFields());
     for (Field field : fieldz) {

         // for (Field field : obj.getClass().getDeclaredFields()) {
         field.setAccessible(true); // if you want to modify private fields
         if(field.getType()== dynamic_property.class)
         {
             try {

                // Log.e("DB Field ", "" + field.getName());
                 Class<?> clazz = field.get(obj).getClass();
                 Field dyna_column_name_field = clazz.getDeclaredField("column_name");
                 Field dyna_value_field = clazz.getDeclaredField("value");


                 dyna_column_name_field.setAccessible(true);
                 dyna_value_field.setAccessible(true);
                 String cv_key=(String) dyna_column_name_field.get(field.get(obj));
                 String cv_value=(String) dyna_value_field.get(field.get(obj));
              if(cv_key!=null&&cv_key.length()>0&&cv_value!=null&&cv_value.length()>0)
              {

                      cv.put(cv_key,cv_value);
              }




             }catch (Exception ex){
                 Log.e("REFLECTION ERROR =>",""+ex.getMessage());
             }
         }else {


         }
     }
     cv.put("sync_status","p");
     return cv;

 }

 member load_employee_from_JSON(JSONObject j)
    {
        member emp = new member();
try{


    try {
        for (Field field : emp.getClass().getDeclaredFields()) {
           field.setAccessible(true); // if you want to modify private fields
            if(field.getType()==dynamic_property.class)
            {


            }
            System.out.println(field.getName()
                    + " - " + field.getType()
                    + " - " + field.get(dynamic_property.class));
        }





    }catch (Exception ex){
        Log.e("JSON ERROR =>","EMP "+ex.getMessage());
    }






}catch (Exception ex){
    Log.e("JSON ERROR =>","EMP "+ex.getMessage());
}

        return emp;
    }

    ArrayList<dependant> load_dependants_from_transaction_no(String transaction_no)
    {
        ArrayList<dependant> dependants=new ArrayList<>();
        Cursor c=database.rawQuery("SELECT * FROM employee_dependants_table WHERE parent_transaction_no='"+transaction_no+"'",null);
        if(c.moveToFirst())
        {
            do{
                dependant d=new dependant();
                d.dependant_type=c.getString(c.getColumnIndex("dependant_type"));
                d.surname=c.getString(c.getColumnIndex("surname"));
                d.other_names=c.getString(c.getColumnIndex("other_names"));
                d.gender=c.getString(c.getColumnIndex("gender"));
                d.dob=c.getString(c.getColumnIndex("dob"));
                d.birth_cert_photo=c.getString(c.getColumnIndex("birth_cert_pic"));
                d.passport_photo=c.getString(c.getColumnIndex("passport_pic"));
                d.transaction_no=c.getString(c.getColumnIndex("transaction_no"));
                d.parent_transaction_no=c.getString(c.getColumnIndex("parent_transaction_no"));
                d.user_id=c.getString(c.getColumnIndex("user_id"));
                dependants.add(d);
            }while (c.moveToNext());
        }
        c.close();

        return dependants;
    }





    ArrayList<member> re_orderd_employee_list(ArrayList<member> members, String employee_no)
    {
        ArrayList<member> objs=new ArrayList<>();
        double highest_score=0.0;
        for(member emp: members)
        {
            if(sparta_string_compairer.get_similarity_score(employee_no,emp.sid.value)>highest_score)
            {
objs.add(0,emp);
highest_score= sparta_string_compairer.get_similarity_score(employee_no,emp.sid.value);
            }else{
                objs.add(emp);
            }
        }


        return objs;

    }



    ArrayList<member> re_orderd_employee_list_via_name(ArrayList<member> members, String name)
    {
        ArrayList<member> temp_ref_list= members;
        ArrayList<member> objs=new ArrayList<>();
        for(int ii = 0; ii< members.size(); ii++) {
            double highest_score = 0.0;
            int highest_score_index = 0;

            for (int i=0;i<temp_ref_list.size();i++) {
                member emp=temp_ref_list.get(i);
                double name_no_score = sparta_string_compairer.get_similarity_score(name, emp.surname.value);
                if (name_no_score> highest_score) {
                    highest_score = name_no_score;
                    highest_score_index=i;
                } else {
                }
            }
            objs.add(temp_ref_list.get(highest_score_index));
            temp_ref_list.remove(highest_score_index);
        }


        return objs;

    }

    ArrayList<member> re_orderd_employee_list_via_name_and_no(ArrayList<member> members, String no, String name)
    {
        ArrayList<member> temp_ref_list= members;
        ArrayList<member> objs=new ArrayList<>();
        for(int ii = 0; ii< members.size(); ii++) {
            double highest_score = 0.0;
           int highest_score_index = 0;

            for (int i=0;i<temp_ref_list.size();i++) {
                member emp=temp_ref_list.get(i);
//                double name_score = sparta_string_compairer.get_similarity_score(name, emp.full_name);
//               double no_score =  sparta_string_compairer.get_similarity_score(no, emp.employee_no);
//               double name_no_score = name_score+no_score;
//             Log.e("Score sort =>","Original name =>"+name+" : "+ emp.full_name+" name score =>"+name_no_score+"  no score =>"+no_score);
//                if (name_no_score> highest_score) {
//                    highest_score = name_no_score;
//                    highest_score_index=i;
//                } else {
//                    }
            }
            objs.add(temp_ref_list.get(highest_score_index));
temp_ref_list.remove(highest_score_index);
        }


        return objs;

    }
    public String load_dyna_data_name(String sid,dyna_data_obj.operatio_data_type ot)
    {
        try {
            Cursor c = database.rawQuery("SELECT data FROM dyna_data_table WHERE data_type='"+ot.ordinal()+"' AND sid='" + sid + "'", null);


            if (c.moveToFirst()) {
                do {

                    return c.getString(c.getColumnIndex("data"));

                } while (c.moveToNext());
            }
            c.close();
        }catch (Exception ex){}
        return act.getString(R.string.unavailable_field);
    }
 public String load_insurance_name(String sid)
    {
        try {
            Cursor c = database.rawQuery("SELECT data FROM dyna_data_table WHERE data_type='mi' AND sid='" + sid + "'", null);


            if (c.moveToFirst()) {
                do {

                    return c.getString(c.getColumnIndex("data"));

                } while (c.moveToNext());
            }
            c.close();
        }catch (Exception ex){}
        return act.getString(R.string.unavailable_field);
    }
    public String load_division_name(String sid)
    {
        Cursor c=database.rawQuery("SELECT data FROM dyna_data_table WHERE data_type='dp' AND sid='"+sid+"'",null);

        if(c.moveToFirst())
        {
            do{

                return c.getString(c.getColumnIndex("data"));

            }while (c.moveToNext());
        }
        c.close();
        return act.getString(R.string.unavailable_field);
    }
 public String load_gender_name(String sid)
    {
        Cursor c=database.rawQuery("SELECT data FROM dyna_data_table WHERE data_type='g' AND sid='"+sid+"'",null);

        if(c.moveToFirst())
        {
            do{

                return c.getString(c.getColumnIndex("data"));

            }while (c.moveToNext());
        }
        c.close();
        return act.getString(R.string.unavailable_field);
    }

public String load_nationality_name(String sid)
    {
        Cursor c=database.rawQuery("SELECT data FROM dyna_data_table WHERE data_type='cntry' AND sid='"+sid+"'",null);

        if(c.moveToFirst())
        {
            do{

                return c.getString(c.getColumnIndex("data"));

            }while (c.moveToNext());
        }
        c.close();
        return act.getString(R.string.unavailable_field);
    }
public String load_contributor_name(String sid)
    {
        Cursor c=database.rawQuery("SELECT data FROM dyna_data_table WHERE data_type='contb' AND sid='"+sid+"'",null);

        if(c.moveToFirst())
        {
            do{

                return c.getString(c.getColumnIndex("data"));

            }while (c.moveToNext());
        }
        c.close();
        return act.getString(R.string.unavailable_field);
    }



public String greatest_sync_var(String table_name)
    {


  Cursor c=database.rawQuery("SELECT CAST(sync_var AS INTEGER) FROM "+table_name+" WHERE sync_var IS NOT NULL ORDER BY CAST(sync_var AS INTEGER) DESC LIMIT 1",null);

        if(c.moveToFirst())
        {
            do{

                return c.getString(0);

            }while (c.moveToNext());
        }
        c.close();
        return "0";
    }
public String greatest_sync_var(String table_name, @Nullable String...filters)
    {


  Cursor c=database.rawQuery("SELECT CAST(sync_var AS INTEGER) FROM "+table_name+(filters==null?"":" "+conccat_sql_filters(filters))+" ORDER BY CAST(sync_var AS INTEGER) DESC LIMIT 1",null);

        if(c.moveToFirst())
        {
            do{

                String res=c.getString(0);
                c.close();
                return res;
            }while (c.moveToNext());
        }
        c.close();
        return "0";
    }

    public String get_record_count(String table_name, @Nullable String...filters)
    {

        String qry="SELECT COUNT(*) FROM "+table_name+(filters==null?"":" "+conccat_sql_filters(filters));
      //  Log.e("QRY :",""+qry);
        Cursor c=database.rawQuery(qry,null);

        if(c.moveToFirst())
        {
            do{
String res=c.getString(0);
                c.close();
                return res;

            }while (c.moveToNext());
        }
        c.close();
        return "0";
    }
    String conccat_sql_filters(String[] str_to_join)
    {
        String result="";
        for(int i=0;i<str_to_join.length;i++)
        {
            result=result+(i==0?"WHERE ":" AND ")+str_to_join[i];
        }
        return result;

    }

    public String greatest_member_data_sync_var(String data_type)
    {


  Cursor c=database.rawQuery("SELECT CAST(sync_var AS INTEGER) FROM member_data_table WHERE sync_var IS NOT NULL AND data_type='"+data_type+"' ORDER BY CAST(sync_var AS INTEGER) DESC LIMIT 1",null);

        if(c.moveToFirst())
        {
            do{

                return c.getString(0);

            }while (c.moveToNext());
        }
        c.close();
        return "0";
    }
 public String greatest_fees_details_sync_var(String fees_type)
    {


  Cursor c=database.rawQuery("SELECT CAST(sync_var AS INTEGER) FROM fees_details_table WHERE sync_var IS NOT NULL AND fees_type='"+fees_type+"' ORDER BY CAST(sync_var AS INTEGER) DESC LIMIT 1",null);

        if(c.moveToFirst())
        {
            do{

                return c.getString(0);

            }while (c.moveToNext());
        }
        c.close();
        return "0";
    }
public String employee_count()
    {


        Cursor c=database.rawQuery("SELECT COUNT(*) FROM member_info_table",null);

        if(c.moveToFirst())
        {
            do{

                return c.getString(0);

            }while (c.moveToNext());
        }
        c.close();
        return "0";
    }
public String record_count(String table_name)
    {


        Cursor c=database.rawQuery("SELECT COUNT(*) FROM "+table_name,null);

        if(c.moveToFirst())
        {
            do{

                return c.getString(0);

            }while (c.moveToNext());
        }
        c.close();
        return "0";
    }


    String conccat_sql_string(String[] str_to_join)
    {
        String result="";
        for(int i=0;i<str_to_join.length;i++)
        {
            result=result+(i==0?"":",")+"'"+str_to_join[i]+"'";
        }
        return result;

    }


String conccat_sql_string(String[] str_to_join, ArrayList<String> str_to_join2)
    {
        String result="";
        for(int i=0;i<str_to_join.length;i++)
        {
            result=result+(i==0?"":",")+"'"+str_to_join[i]+"'";
        }
      for(int i=0;i<str_to_join2.size();i++)
        {
            result=result+(result.length()<0?"":",")+"'"+str_to_join2.get(i)+"'";
        }
        return result;

    }
String conccat_sql_string(ArrayList<String> str_to_join2)
    {
        String result="";


      for(int i=0;i<str_to_join2.size();i++)
        {
            result=result+(result.length()<1?"":",")+"'"+str_to_join2.get(i)+"'";
        }
        return result;

    }
















    public void register_employee_data(JSONObject data, String data_type)
    {
        /*
 "$id": "2",
            "id": 4,
            "employee_details_id": 1036,
            "biometric_type_details_id": 1,
            "biometric_type_id": 1,
            "biometric_string": "",
            "url_path": "",
            "biometric_type_name": "Finger Prints",
            "biometric_type_details_name": "Right Thumb",
            "user_id": 0,
            "datecomparer": 1557435600,
            "syn_date_time": "2019-05-10T00:00:00"

{"$id":"1","isOkay":true,"message":"Details Found","result":[{
"$id":"2","id":5873,"employee_details_id":4181,
"biometric_type_details_id":11,
"biometric_type_id":2,"biometric_string":"iVBORw0KGgoAAAANSUhEUgAAAMMAAAEECAIAAAAahxdFAA
         */
        try{

            database.execSQL("DELETE FROM member_data_table WHERE sid=\""+data.getString("id")+"\"");
        }catch(Exception ex){}



        try {
            ContentValues cv = new ContentValues();
            cv.put("sync_status", "i");
            cv.put("sid", data.getString("id"));
 cv.put("member_id", data.getString("employee_details_id"));


       //     cv.put("data", data_type.equalsIgnoreCase(""+svars.data_type_indexes.photo)?save_doc(data.getString("image_name")):data.getString("image_name"));
            cv.put("data", data_type.equalsIgnoreCase(""+svars.data_type_indexes.photo)?save_doc(data.getString("biometric_string")):data.getString("biometric_string"));
         if(cv.get("data").toString().equalsIgnoreCase("--------------"))
         {
return;
         }

              cv.put("data_type", ""+data_type);
              cv.put("data_index", data.getString("biometric_type_details_id"));
//              cv.put("transaction_no", data.getString("unique_code"));



              cv.put("sync_var", data.getString("datecomparer"));
            cv.put("data_storage_mode", data_type.equalsIgnoreCase(""+svars.data_type_indexes.photo)?"path":"db");
            Log.e("Inserting FP=>",""+  database.insert("member_data_table", null, cv));
        }catch (Exception ex){
            Log.e("Error inserting ED=>",""+ex.getMessage());}
    }

    public void register_fees_details(JSONObject data, String fees_type)
    {
        /*
 {"$id":"2",
 "id":233,
 "student_id":5611,
 "amount_paid":0.0,
 "amount_expected":330000.0,
 "balance":330000.0,
 "total_fees":330000.0,
 "allowed":false,
 "expiry":"05/01/2020 00:00:00",
 "fee_bal":0.0,"fee_bal_perperiod":330000.0}
         */
        try{

            database.execSQL("DELETE FROM fees_details_table WHERE sid='"+data.getString("id")+"' OR (fees_type='"+fees_type+"' AND member_id='"+data.getString("student_id")+"')");
        }catch(Exception ex){}


try{
    if(data.getBoolean("is_active")){return;}
}catch (Exception ex){}
        try {
            ContentValues cv = new ContentValues();
            cv.put("sync_status", "i");
            cv.put("sid", data.getString("id"));
            cv.put("member_id", data.getString("student_id"));
            cv.put("fees_type",fees_type);
   cv.put("balance",data.getString("fee_bal_perperiod"));
 cv.put("expiry_date",data.getString("expiry"));
cv.put("allowed",data.getString("allowed"));
try{
    cv.put("sync_var",data.getString("datecomparer"));

}catch (Exception ex){}


            Log.e("Inserting Fees=>",""+  database.insert("fees_details_table", null, cv));
        }catch (Exception ex){
            Log.e("Error inserting ED=>",""+ex.getMessage());}
    }

    String save_doc_us(String base64_bytes)
    {
        byte[] file_bytes= Base64.decode(base64_bytes,0);

        String img_name="TA_DAT"+ System.currentTimeMillis()+"JPG_IDC.JPG";
        //  String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOutputStream = null;
        //  File file = new File(path + "/TimeAndAttendance/.RAW_EMPLOYEE_DATA/");
        File file = new File(svars.WORKING_APP.file_path_employee_data);
        if (!file.exists()) {
            Log.e("Creating data dir=>",""+ String.valueOf(file.mkdirs()));
        }
        //  file = new File(path + "/TimeAndAttendance/.RAW_EMPLOYEE_DATA/", img_name);
        file = new File(svars.WORKING_APP.file_path_employee_data, img_name);

        try {
            fOutputStream = new FileOutputStream(file);
            fOutputStream.write(file_bytes);

            //fpb.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

            fOutputStream.flush();
            fOutputStream.close();

            //  MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //   Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return "--------------";
        } catch (IOException e) {
            e.printStackTrace();

            //   Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return "--------------";
        }
        return img_name;
    }
   public static  String error_return="!!!";
    public static String save_doc(String base64_bytes)
    {
        Bitmap bmp= s_bitmap_handler.getImage(Base64.decode(base64_bytes,0));
        if(1==1)
        {
            return SpartaAppCompactActivity.save_app_image(bmp);
        }
        byte[] file_bytes= s_bitmap_handler.getBytes_JPG(bmp);

        String img_name="TA_DAT"+ System.currentTimeMillis()+"JPG_IDC.JPG";
        //  String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOutputStream = null;
        //  File file = new File(path + "/TimeAndAttendance/.RAW_EMPLOYEE_DATA/");
        File file = new File(svars.WORKING_APP.file_path_employee_data);
        if (!file.exists()) {
            Log.e("Creating data dir=>",""+ String.valueOf(file.mkdirs()));
        }
        //  file = new File(path + "/TimeAndAttendance/.RAW_EMPLOYEE_DATA/", img_name);
        file = new File(svars.WORKING_APP.file_path_employee_data, img_name);
        Log.e("Creating file =>",""+ String.valueOf(file.getAbsolutePath()));

        try {
            fOutputStream = new FileOutputStream(file);
            fOutputStream.write(file_bytes);

            //fpb.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

            fOutputStream.flush();
            fOutputStream.close();

            //  MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //   Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return error_return;
        } catch (IOException e) {
            e.printStackTrace();

            //   Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return error_return;
        }
        return img_name;
    }
    public static String get_saved_doc_base64(String data_name)
    {
        String res="";
        try {
            String path = Environment.getExternalStorageDirectory().toString();

            File file = new File(svars.WORKING_APP.file_path_employee_data, data_name);
            //java.nio.file.Files.readAllBytes(Path path);
            //);
            //  res = Base64.encodeToString(s_bitmap_handler.getBytes(BitmapFactory.decodeFile(file.getAbsolutePath())), 0);
            res = Base64.encodeToString(org.apache.commons.io.FileUtils.readFileToByteArray(file), 0);
            return res;
        }catch (Exception ex){
            Log.e("Data file retreival :"," "+ex.getMessage());

        }



        return  res;
    }


    public long sync_period()
    {
        //Date date = svars.sparta_EA_calendar().getTime();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
        String dttt = format1.format(date);



         if(svars.sync_time(act)==null)
        {
            return svars.regsyncinterval_mins;
        }
        Date time1=null;
        try {
            try {
                time1 = new SimpleDateFormat("HH:mm:ss").parse(format1.format(date));
            }catch (Exception ex){
                Log.e("Time Error =>",ex.getMessage());
            }
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);
            Date time2 =null;
            try{
                Log.e("DATE 2 =>"," t "+date.getTime()+" Time=>"+svars.sync_time(act).split(" ")[1]);
                time2 = new SimpleDateFormat("HH:mm:ss").parse(svars.sync_time(act).split(" ")[1]);
            }catch (Exception ex){ Log.e("Time Error =>",ex.getMessage());}
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            long diffference= Calendar.getInstance().getTimeInMillis()-calendar2.getTimeInMillis();
            // return (int)Math.round((double)diffference/60000);
            int diff_1=(int) ((diffference/ (1000*60)) % 60);
            return  diff_1+(((int) ((diffference / (1000*60*60)) % 24))*60);
        }catch (Exception ex){return svars.regsyncinterval_mins;}



    }


    public static Gpsprobe_r gps;


    public static void log_event(Context act, String data)
    {

      gps =gps==null?new Gpsprobe_r(act):gps;
        String prefix=svars.sparta_EA_calendar().getTime().toString()+"   :   "+gps.getLatitude()+","+gps.getLongitude()+"     =>";

     //   String prefix=svars.sparta_EA_calendar().getTime().toString()+"     =>";
        String root = act.getExternalFilesDir(null).getAbsolutePath() + "/logs";
        Log.e("LOG_TAG", "PATH: " + root);

        File file = new File(root);
        file.mkdirs();
        try {
            File gpxfile = new File(file, svars.gett_date()+""+svars.Log_file_name);
            FileWriter writer = new FileWriter(gpxfile,true);
            writer.append(svars.APP_OPERATION_MODE==svars.OPERATION_MODE.DEV?prefix+data+"\n": s_cryptor.encrypt(prefix+data+"\n"));
            writer.flush();
            writer.close();
        }catch (Exception ex)
        {

        }

    }

    public static void log_String(Context act, String data)
    {
        gps =gps==null?new Gpsprobe_r(act):gps;
         String prefix=svars.sparta_EA_calendar().getTime().toString()+"   :   "+gps.getLatitude()+","+gps.getLongitude()+"     =>";

        //  String prefix=svars.sparta_EA_calendar().getTime().toString()+"     =>";
        String root = act.getExternalFilesDir(null).getAbsolutePath() + "/logs";
        Log.e("LOG_TAG", "PATH: " + root);

        File file = new File(root);
        file.mkdirs();
        try {
            File gpxfile = new File(file, svars.string_file_name);
            FileWriter writer = new FileWriter(gpxfile,true);
            writer.append(svars.APP_OPERATION_MODE!=svars.OPERATION_MODE.DEV?prefix+data+"\n":prefix+data+"\n");
            writer.flush();
            writer.close();
        }catch (Exception ex)
        {

        }

    }

    public static void backup_db(Activity act){



        File s_file = new File(act.getExternalFilesDir(null).getAbsolutePath());
        s_file.mkdirs();
        File t_file = new File(act.getExternalFilesDir(null).getAbsolutePath() + "/backups");
        t_file.mkdirs();

        try {


            File sourceLocation= new File(s_file,svars.DB_NAME);
            File targetLocation= new File(t_file, System.currentTimeMillis()+".spartadb");

            InputStream in = null;

            in = new FileInputStream(sourceLocation);

            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<geo_fence> load_site_geo_fences()
    {
        ArrayList<geo_fence> fences=new ArrayList<>();

/*
        Cursor c=database.rawQuery("SELECT * FROM geo_fences_table WHERE parent_type IN (1,2)",null);
        if(c.moveToFirst())
        {
            do{
                geo_fence gf=new geo_fence();
                gf.sid=c.getString(c.getColumnIndex("sid"));
              gf.fence_type=c.getString(c.getColumnIndex("fence_type"));
              gf.points=load_geo_fences(c.getString(c.getColumnIndex("sid")));

              fences.add(gf);

            }while (c.moveToNext());

        }

        c.close();*/
        for(int i=0;i<1;i++){
            geo_fence gf=new geo_fence();
            gf.parent_type_id=geo_fence.geo_fence_parent_type.values()[new Random().nextInt(5)+1].name();
 gf.fence_type=new String[]{"p","p"}[new Random().nextInt(2)];
 int point_count=gf.fence_type.equalsIgnoreCase("c")?1:new Random().nextInt(4)+4;
 gf.points=new ArrayList<>();
for(int j=0;j<point_count;j++)
{
  //  geo_fence.geo_fence_points po=new geo_fence.geo_fence_points(new LatLng(-1.2101769,36.7065049),(new Random().nextInt(100000)+1000)+"");
    geo_fence.geo_fence_points po=new geo_fence.geo_fence_points(new LatLng(new Random().nextDouble()-1.0,36+new Random().nextDouble()),(new Random().nextInt(100000)+1000)+"");
gf.points.add(po);
}
fences.add(gf);
        }

        return fences;

    }


 public ArrayList<geo_fence> load_geo_fences(String activity_field, String activity_id)
    {
        ArrayList<geo_fence> fences=new ArrayList<>();


        Cursor c=database.rawQuery("SELECT * FROM geo_fences_table WHERE parent_type IN (1,2)OR (parent_type='3' AND parent_id='"+activity_field+"') OR (parent_type='4' AND parent_id='"+activity_id+"')",null);
        if(c.moveToFirst())
        {
            do{
                geo_fence gf=new geo_fence();
                gf.sid=c.getString(c.getColumnIndex("sid"));
              gf.fence_type=c.getString(c.getColumnIndex("fence_type"));
              gf.points=load_geo_fences(c.getString(c.getColumnIndex("sid")));

              fences.add(gf);

            }while (c.moveToNext());

        }

        c.close();
        for(int i=0;i<10;i++){
            geo_fence gf=new geo_fence();
            gf.parent_type_id=geo_fence.geo_fence_parent_type.values()[new Random().nextInt(5)+1].name();
 gf.fence_type=new String[]{"c","p"}[new Random().nextInt(2)];
 int point_count=gf.fence_type.equalsIgnoreCase("c")?1:new Random().nextInt(4)+4;
 gf.points=new ArrayList<>();
for(int j=0;j<point_count;j++)
{
  //  geo_fence.geo_fence_points po=new geo_fence.geo_fence_points(new LatLng(-1.2101769,36.7065049),(new Random().nextInt(100000)+1000)+"");
    geo_fence.geo_fence_points po=new geo_fence.geo_fence_points(new LatLng(new Random().nextDouble()-1.0,36+new Random().nextDouble()),(new Random().nextInt(100000)+1000)+"");
gf.points.add(po);
}
fences.add(gf);
        }

        return fences;

    }
 public ArrayList<geo_fence.geo_fence_points> load_geo_fences(String fence_id)
    {
        ArrayList<geo_fence.geo_fence_points> fence_points=new ArrayList<>();


        Cursor c=database.rawQuery("SELECT * FROM geo_fence_points_table WHERE fence_id='"+fence_id+"'",null);
        if(c.moveToFirst())
        {
            do{
                fence_points.add(new geo_fence.geo_fence_points(new LatLng(Double.parseDouble(c.getString(c.getColumnIndex("lat"))), Double.parseDouble(c.getString(c.getColumnIndex("lon")))),c.getString(c.getColumnIndex("lat"))));


            }while (c.moveToNext());

        }

        c.close();
        return fence_points;

    }




    public interface backup_progress_listener{
       void on_primary_status_changed(String status);
       void on_secondary_status_changed(String status);
       void on_primary_progress_changed(int progress);
       void on_secondary_progress_changed(int progress);
       void on_error_encountered(String status);
  void on_backup_complete();



   }

    public static void backup_app_data(Activity act, final backup_progress_listener listener)
    {

        String backup_transaction_code="SPA_IDC"+ System.currentTimeMillis()+"$"+svars.device_code(act).replace("|","$")+"BU_RTA";
       listener.on_primary_status_changed(act.getString(R.string.verifying_files_to_backup));
        //   listener.on_secondary_status_changed("...");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
      //  File fp_images_folder = new File(Environment.getExternalStorageDirectory().toString() + "/realm", "/.UNCOMPRESSED_FP_IMGS");
        File fp_images_folder = new File(svars.WORKING_APP.file_path_employee_data);
        fp_images_folder.mkdirs();
     //   listener.on_secondary_status_changed(act.getString(R.string.image_files_ok));
        listener.on_secondary_status_changed(act.getString(R.string.employee_data_files_ok));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File db_folder = new File(act.getExternalFilesDir(null).getAbsolutePath());
        db_folder.mkdirs();
        listener.on_secondary_status_changed(act.getString(R.string.db_ok));
        listener.on_primary_progress_changed(10);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File db_backup_folder = new File(svars.WORKING_APP.file_path_db_backup);
        db_backup_folder.mkdirs();
        listener.on_secondary_status_changed(act.getString(R.string.db_backup_ok));
        listener.on_primary_progress_changed(20);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        File log_folder = new File(act.getExternalFilesDir(null).getAbsolutePath() + "/logs");
        log_folder.mkdirs();
        listener.on_secondary_status_changed(act.getString(R.string.log_file_ok));
        listener.on_primary_progress_changed(30);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        File log_files_backup_folder = new File(svars.WORKING_APP.file_path_log_backup);
        log_files_backup_folder.mkdirs();
         listener.on_secondary_status_changed(act.getString(R.string.log_backup_ok));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        File global_backup_folder = new File(svars.WORKING_APP.file_path_general_backup);
        global_backup_folder.mkdirs();
        listener.on_secondary_status_changed(act.getString(R.string.backup_location_ok));
        listener.on_primary_progress_changed(40);

        listener.on_primary_status_changed(act.getString(R.string.staging_current_database));
         listener.on_secondary_status_changed(act.getString(R.string.staging_current_database));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            File sourceLocation= new File(db_folder,svars.DB_NAME);
            File targetLocation= new File(db_backup_folder,"SPA"+ System.currentTimeMillis()+"DB_RTA");

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);


            byte[] buf = new byte[1024];
            int len;
            int per_counter = 0;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                per_counter += len;
                // final double finalPercent = Math.round(((double) per_counter / (double) c.getContentLength()) * 100);
                final double finalPercent = sparta_loc_util.round(((double) per_counter / (double) sourceLocation.length()),2)* 100;
                listener.on_secondary_progress_changed((int)finalPercent);
               // listener.on_secondary_progress_changed(Integer.parseInt((((""+finalPercent).split(".")[0])+((""+finalPercent).split(".")[1].toCharArray()[0])+((""+finalPercent).split(".")[1].toCharArray()[1]))));
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            listener.on_error_encountered(e.getMessage());
        }
 listener.on_primary_status_changed(act.getString(R.string.staging_log_files));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            File sourceLocation= new File(log_folder,svars.Log_file_name);
            File targetLocation= new File(log_files_backup_folder,"SPA"+ System.currentTimeMillis()+"LG_RTA");

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);


            byte[] buf = new byte[1024];
            int len;
            int per_counter = 0;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                per_counter += len;
                // final double finalPercent = Math.round(((double) per_counter / (double) c.getContentLength()) * 100);
                final double finalPercent = sparta_loc_util.round(((double) per_counter / (double) sourceLocation.length()),2)* 100;


                      listener.on_secondary_progress_changed((int)finalPercent);



               // listener.on_secondary_progress_changed(Integer.parseInt((((""+finalPercent).split(".")[0])+((""+finalPercent).split(".")[1].toCharArray()[0])+((""+finalPercent).split(".")[1].toCharArray()[1]))));
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            listener.on_error_encountered(e.getMessage());
        }
        listener.on_primary_progress_changed(50);

        if(Thread.currentThread().isInterrupted())
        {
            return;
        }
        listener.on_primary_status_changed(act.getString(R.string.prepairing_files_to_archive));

        ArrayList<String> files_tobackup=new ArrayList<>();
        File[] dump_files = fp_images_folder.listFiles();
        if(dump_files!=null) {
            for (int i = 0; i < dump_files.length; i++) {
                files_tobackup.add(dump_files[i].getAbsolutePath());
            }
        }

if(svars.recrusive_backup(act)){

    dump_files = db_backup_folder.listFiles();
    if(dump_files!=null) {
        for (int i = 0; i < dump_files.length; i++) {
            files_tobackup.add(dump_files[i].getAbsolutePath());
        }
    }

}else{
    files_tobackup.add(new File(db_folder,svars.DB_NAME).getAbsolutePath());

}





        dump_files = log_files_backup_folder.listFiles();
if(dump_files!=null) {
    for (int i = 0; i < dump_files.length; i++) {
        files_tobackup.add(dump_files[i].getAbsolutePath());
    }

}
        listener.on_primary_status_changed(act.getString(R.string.creating_archive));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
zip_array(act,files_tobackup,global_backup_folder.getAbsolutePath()+"/"+backup_transaction_code+".zip",listener);
        listener.on_primary_status_changed(act.getString(R.string.archive_creation_complete));
        listener.on_primary_progress_changed(60);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        listener.on_primary_status_changed(act.getString(R.string.initializing_backup));

        if(svars.email_backup(act))
{
    listener.on_secondary_status_changed(act.getString(R.string.email_backup_begun));

    try {
        Thread.sleep(100);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    send_backup_mail(global_backup_folder.getAbsolutePath()+"/"+backup_transaction_code+".zip",act,listener);

}
if(svars.ftp_backup(act))
{
    listener.on_secondary_status_changed(act.getString(R.string.sftp_backup_begun));

    try {
        Thread.sleep(100);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    send_backup_ftp(global_backup_folder.getAbsolutePath()+"/"+backup_transaction_code+".zip",act,listener);
}
if(svars.sd_backup(act))
{
    listener.on_secondary_status_changed(act.getString(R.string.sd_card_backup_begun));

    try {
        Thread.sleep(100);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
// String dstPath = Environment.getExternalStorageDirectory() + File.separator + ".realm_BACKUP" + File.separator;
// String dstPath = Environment.get()+ File.separator + ".realm_BACKUP" + File.separator;
//String dstPath =getSdCardPaths(act,false).get(0)+ File.separator + "realm_BACKUP";
    // String dstPath ="storage/emulated/1"+ File.separator + "realm_BACKUP" + File.separator;

    try {
        String dstPath =act.getExternalFilesDirs(null)[1]+ File.separator + "realm_BACKUP";
        File sourceLocation= new File(global_backup_folder,backup_transaction_code+".zip");
                File targetLocation= new File(dstPath);

        targetLocation.mkdirs();


if(!targetLocation.exists())
{
    if(Thread.currentThread().isInterrupted())
    {
        return;
    }  listener.on_error_encountered(act.getString(R.string.sd_card_not_available));
  listener.on_secondary_status_changed(act.getString(R.string.sd_card_not_available));
    try {
        Thread.sleep(100);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    listener.on_secondary_status_changed(act.getString(R.string.sd_card_backup_failed));
 try {
        Thread.sleep(100);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    listener.on_secondary_status_changed(act.getString(R.string.sd_card_backup_terminated));
 return;
}
      targetLocation= new File(targetLocation,backup_transaction_code+".zip");

        InputStream in = new FileInputStream(sourceLocation);
        OutputStream out = new FileOutputStream(targetLocation);


        byte[] buf = new byte[1024];
        int len;
        int per_counter = 0;

        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
            per_counter += len;
            if(Thread.currentThread().isInterrupted())
            {
                return;
            }
            // final double finalPercent = Math.round(((double) per_counter / (double) c.getContentLength()) * 100);
   final double finalPercent = sparta_loc_util.round(((double) per_counter / (double) sourceLocation.length()),2)* 100;


            listener.on_secondary_progress_changed((int)finalPercent);



            // listener.on_secondary_progress_changed(Integer.parseInt((((""+finalPercent).split(".")[0])+((""+finalPercent).split(".")[1].toCharArray()[0])+((""+finalPercent).split(".")[1].toCharArray()[1]))));
        }
        in.close();
        out.close();

        try {

            File oldFolder = new File(dstPath,backup_transaction_code+".zip");
            File newFolder = new File(dstPath, Calendar.getInstance().getTime().toString().split("G")[0].trim().replace(" ","_").replace(":","-")+".zip");
            boolean success = oldFolder.renameTo(newFolder);
            Log.e("Renaming backup =>",""+success);

        }catch (Exception ex){}

        listener.on_secondary_status_changed(act.getString(R.string.sd_card_backup_complete));
    } catch (Exception e) {
        e.printStackTrace();
        listener.on_error_encountered(e.getMessage());
         listener.on_secondary_status_changed(act.getString(R.string.sd_card_backup_failed));
        try {
            Thread.sleep(500);
        } catch (InterruptedException ez) {
            ez.printStackTrace();
        }
    }
    try {
        Thread.sleep(100);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
        listener.on_primary_progress_changed(100);
 listener.on_secondary_progress_changed(100);








        listener.on_primary_status_changed(act.getString(R.string.backup_complete));
        listener.on_secondary_status_changed(act.getString(R.string.backup_complete));
        svars.set_backup_time(act, Calendar.getInstance().getTime().toString().split("G")[0].trim());
        listener.on_backup_complete();
       /* File ff=new File(global_backup_folder.getAbsolutePath()+"/"+backup_transaction_code+".zip");
        ff.delete();*/
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    static void send_backup_mail(final String file_path, final Activity act, backup_progress_listener listener) {

        if(Thread.currentThread().isInterrupted())
        {
            return;
        }

                        sparta_mail_probe smp = new sparta_mail_probe("Time and Attendance App data Backup", "Device =" + svars.device_code(act) + "\nUsername =" + svars.user_name(act) + "\nUser id=" + svars.user_id(act) + "\nM link=" + svars.WORKING_APP.ACCOUNT + "\nApp version " + BuildConfig.VERSION_NAME);

                        try {
                            File file = new File(file_path);
                            long fileSizeInBytes = file.length();
                            long fileSizeInKB = fileSizeInBytes / 1024;
                            long fileSizeInMB = fileSizeInKB / 1024;

                                smp.send_attachement(file_path);


                        } catch (Exception ex) {


                        }
    }

    static void send_backup_ftp(String path, Activity act, backup_progress_listener listener) {
        if(Thread.currentThread().isInterrupted())
        {
            return;
        }
        Log.e("SSSSSSSSS", " UPLOADING TO SFTP NOW"+path);

        File uploadFilePath;
        Session session;
        Channel channel = null;
        ChannelSftp sftp = null;
        uploadFilePath = new File(path);
        //uploadFilePath=new File(Environment.getExternalStorageDirectory()+"/Android/data/sparta.farmercontractor/files/Output/"+filename);
        byte[] bufr = new byte[(int) uploadFilePath.length()];
        FileInputStream fis = null;
        Log.e("SSSSSSSSS", " UPLOADING TO SFTP NOW 2");
        try {
            fis = new FileInputStream(uploadFilePath);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            Log.e("LLLLLLLLLL", "dddddddddddddddd file not found");
            listener.on_error_encountered(act.getString(R.string.missing_file)+uploadFilePath.getName());
            e1.printStackTrace();

        }
        try {
            fis.read(bufr);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            listener.on_error_encountered(act.getString(R.string.missing_file)+uploadFilePath.getName());
        }


        JSch ssh = new JSch();
        int reconnection_count = 0;
        int max_reconnection_count = 10;
        while (true)
        {

            try {

//            session = ssh.getSession("Contractor", "www.cs4africa.com", 1989);
//
//
//          session.setConfig("StrictHostKeyChecking", "no");
//           session.setPassword("@contractor");


                session = ssh.getSession("Togo", "www.cs4africa.com", 1989);
                System.out.println("JSch JSch JSch Session created.");
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword("TOGO");


                session.connect();
                listener.on_secondary_status_changed(act.getString(R.string.connected_to_sftp_server));


                channel = session.openChannel("sftp");
                channel.connect();
                sftp = (ChannelSftp) channel;

                try {
                    sftp.mkdir(svars.ftp_folder(act));
                } catch (Exception ex) {
                }
                try {
                    sftp.cd(svars.ftp_folder(act));
                } catch (Exception ee) {
                }

break;
            } catch(Exception e){
                Log.e("Upload error =>", "" + e.getMessage());
                listener.on_error_encountered(act.getString(R.string.connection_failed));
                if (reconnection_count < max_reconnection_count) {
                    reconnection_count++;
                    listener.on_secondary_status_changed(act.getString(R.string.attempting_sftp_reconnection));

                }else{
                    return;
                }

            }
        }
        listener.on_secondary_status_changed(act.getString(R.string.uploading_backup));

        ByteArrayInputStream in = new ByteArrayInputStream(bufr);
        try {
            sftp.put(in, uploadFilePath.getName(), null);
           // listener.on_secondary_status_changed("Uploading backup");
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            listener.on_error_encountered(act.getString(R.string.sftp_upload_failed)+uploadFilePath.getName());
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            listener.on_error_encountered("SFTP server disconnection failed :"+uploadFilePath.getName());

            e.printStackTrace();
        }

        if (sftp.getExitStatus() == -1) {
            System.out.println("file uploaded");
            Log.e("upload result", "succeeded");
            listener.on_secondary_status_changed(act.getString(R.string.sftp_upload_successfull));

            //toast = 1;

            //toastHandlefinish.sendEmptyMessage(0);

        } else {
            Log.e("upload failed ", "failed");
            listener.on_error_encountered(act.getString(R.string.sftp_upload_failed));

        }
    }


    public static List<String> getSdCardPaths(final Context context, final boolean includePrimaryExternalStorage)
    {
        final File[] externalCacheDirs= ContextCompat.getExternalCacheDirs(context);
        if(externalCacheDirs==null||externalCacheDirs.length==0)
            return null;
        if(externalCacheDirs.length==1)
        {
            if(externalCacheDirs[0]==null)
                return null;
            final String storageState= EnvironmentCompat.getStorageState(externalCacheDirs[0]);
            if(!Environment.MEDIA_MOUNTED.equals(storageState))
                return null;
            if(!includePrimaryExternalStorage&& Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB&& Environment.isExternalStorageEmulated())
                return null;
        }
        final List<String> result=new ArrayList<>();
        if(includePrimaryExternalStorage||externalCacheDirs.length==1)
            result.add(getRootOfInnerSdCardFolder(externalCacheDirs[0]));
        for(int i=1;i<externalCacheDirs.length;++i)
        {
            final File file=externalCacheDirs[i];
            if(file==null)
                continue;
            final String storageState= EnvironmentCompat.getStorageState(file);
            if(Environment.MEDIA_MOUNTED.equals(storageState))
                result.add(getRootOfInnerSdCardFolder(externalCacheDirs[i]));
        }
        if(result.isEmpty())
            return null;
        return result;
    }
    private static String getRootOfInnerSdCardFolder(File file)
    {
        if(file==null)
            return null;
        final long totalSpace=file.getTotalSpace();
        while(true)
        {
            final File parentFile=file.getParentFile();
            if(parentFile==null||parentFile.getTotalSpace()!=totalSpace)
                return file.getAbsolutePath();
            file=parentFile;
        }
    }

    public static void zip_array(Activity act, ArrayList<String> _files, String zipFileName, backup_progress_listener listener) {
        try {
            if(Thread.currentThread().isInterrupted())
            {
                return;
            }
            int BUFFER=1024;
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.size(); i++) {
                if(Thread.currentThread().isInterrupted())
                {
                    return;
                }   Log.e("Compression :", "Adding: " + _files.get(i));
                FileInputStream fi = new FileInputStream(_files.get(i));
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files.get(i).substring(_files.get(i).lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
                listener.on_secondary_status_changed(act.getString(R.string.archiving)+_files.get(i).substring(_files.get(i).lastIndexOf("/") + 1));
                listener.on_secondary_progress_changed((int)((((double)i)/((double)_files.size()))*100));
               // listener.on_secondary_status_changed("Archiving :"+_files.get(i).substring(_files.get(i).lastIndexOf("/") + 1));
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            listener.on_error_encountered(e.getMessage());
        }
    }

   public void zip(String[] _files, String zipFileName) {
        try {

            int BUFFER=1250*1250;
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.length; i++) {
                Log.e("Compression :", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }











    Thread[] exceutioner_threads;
    boolean turbo_match_found=false;
    public enum verification_type {
        clock_in,
        clock_out,
        verification,
        door_verification,
        weighbridge_verification,
        check_out,
        check_in

    }
    public interface matching_interface{
        void on_match_complete(boolean match_found,String mils);
        void on_match_found(String employee_id,String data_index,String match_time,int v_type,int verrification_mode);
        void on_match_progress_changed(int progress);
        void on_match_faild_reason_found(int reason,String employee_id);

    }

    public void load_match(final byte[] model, final matching_interface inter,final int clock_mode)
    {
        Log.e("CPU CORE COUNT :",""+svars.getCpuCores());
        if(svars.current_device(act)==svars.DEVICE.WALL_MOUNTED.ordinal()||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()) {
            FPMatch.getInstance().InitMatch(0,"http://www.fgtit.com/");
        }
        int employee_count_actual=0;
        Cursor cnt_c=database.rawQuery("SELECT COUNT(*) FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"'",null);
        if(cnt_c.moveToFirst()){
            do{
                employee_count_actual=cnt_c.getInt(0);
            }while(cnt_c.moveToNext());}
        final Stopwatch stw=new Stopwatch();


        stw.start();
        final int employee_count=employee_count_actual;
        // final int emp_per_thread=2500;
        //    final int max_thread_count=(employee_count+(emp_per_thread-1))/emp_per_thread;
        final int max_thread_count=1;//svars.getCpuCores();
        final int emp_per_thread=(employee_count+(max_thread_count-1))/max_thread_count;


        Log.e("Turbo match =>","Employee loading_time =>"+stw.elapsed(TimeUnit.MILLISECONDS));
        Log.e("Turbo match =>","Employee fp count =>"+employee_count);


        stw.stop();
        stw.reset();
        stw.start();
        //final int max_thread_count=employee_count/emp_per_thread;
        // (value + 14) / 15;
        Log.e("Turbo match =>","Threads to generate =>"+max_thread_count);

        exceutioner_threads= new Thread[max_thread_count];

        turbo_match_found=false;
        final int[] finish_count = {0};
        for(int i=0;i<exceutioner_threads.length;i++)
        {
//           final FPMatch fpm=new FPMatch();
//           fpm.InitMatch(0,"http://www.fgtit.com/");
            Log.e("Turbo match =>","Generating thread =>"+i+"/"+exceutioner_threads.length);

            if(turbo_match_found)break;
            final int finalI = i;
            // try {
            exceutioner_threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int actual_var_index = (emp_per_thread * finalI);
                        int emp_per_thread_intt = employee_count - actual_var_index;
                        String qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id NOT NULL LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                        switch (clock_mode)
                        {
                            case 0:
                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id NOT NULL AND employee_id NOT IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;

                                break;
                            case 1:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                            case 5:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                            case 6:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_checking_table ect ON eit.sid=ect.employee_id WHERE ect.check_in_time IS NULL OR ect.check_in_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                        }
                        Cursor c = database.rawQuery(qry, null);
                        if (c.moveToFirst()) {
                            do {
                                if (turbo_match_found) break;
                                Log.e("Turbo match =>", finalI+" :Matching =>" + c.getString(c.getColumnIndex("employee_id")));
                                if(svars.current_device(act)==svars.DEVICE.WALL_MOUNTED.ordinal()/*||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()*/)
                                {
//            if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(svars.fp_my_r_thumb_bt_device)) > svars.matching_acuracy) {

                                    if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(c.getString(c.getColumnIndex("data")))) > svars.matching_acuracy) {



                                        Log.e("FPMATCH ", "OBTAINED ");


                                        inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), clock_mode,1);
                                        turbo_match_found = true;
                                    }
                                }else{
                                    int mm_score=1000;
                                    try {

                                        Fmd ffm1 = UareUGlobal.GetImporter().ImportFmd(model, main_fmd_format, main_fmd_format);
                                        Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(c.getString(c.getColumnIndex("data")), 0), main_fmd_format, main_fmd_format);
                                        //   Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(svars.fp_my_r_index_wall_mat_device, 0), main_fmd_format, main_fmd_format);
                                        //  Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(svars.fp_my_r_index_8_inch, 0), main_fmd_format, main_fmd_format);



                                        mm_score = UareUGlobal.GetEngine().Compare(ffm1, 0, ffm2, 0);
                                    }catch (UareUException EX){

                                        Log.e("Turbo match =>","Matching error ur=>"+EX.getMessage());

                                    }catch (Throwable ex){
                                        ex.printStackTrace();
                                        Log.e("Turbo match =>","Matching error th =>"+ex.getMessage());

                                    }
                                    Log.e("Turbo match =>","Matching error score=>"+mm_score);
                                    if (mm_score <svars.matching_error_margin) {

                                        Log.e("FPMATCH ", "OBTAINED ");
                                        ContentValues cv =new ContentValues();
                                        cv.put("data_usage_frequency",""+System.currentTimeMillis());
                                        database.update("employee_data_table",cv,"id="+c.getInt(c.getColumnIndex("id")),null);

                                        inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), clock_mode,1);
                                        turbo_match_found = true;
                                    }
                                }





                            } while (c.moveToNext());

                        } else {

                            Log.e("Turbo match =>", "Thread " + finalI + " has no records to match");
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                            if(!turbo_match_found){load_match_not_found_reason(model,inter,clock_mode);}

                        }
                        finish_count[0]++;
                        Log.e("Turbo match =>", "Thread finish : " + finish_count[0]);
                        percent_calculation pc = new percent_calculation(max_thread_count + "", finish_count[0] + "");
                        inter.on_match_progress_changed(Integer.parseInt(pc.per_balance));
                        if (max_thread_count == finish_count[0]) {
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                            if(!turbo_match_found){load_match_not_found_reason(model,inter,clock_mode);}
                        }


                    }catch (Throwable ex){

                        Log.e("Main Turbo match =>", "Thread " + finalI + " has no records to match");

                    }
                }
            });
            exceutioner_threads[i].start();
            /*}catch (Throwable ex){

                Log.e("Turbo match =>","Thread generation error =>"+i+ex.getMessage());

            }*/
        }
        Log.e("Turbo match =>","Thread generation time =>"+stw.elapsed(TimeUnit.MILLISECONDS));

    }
    public int load_match(final byte[] model, final matching_interface inter, final boolean load_match_failed_reason)
    {
        final int clock_mode=0;
        Log.e("CPU CORE COUNT :",""+svars.getCpuCores());
        if(svars.current_device(act)==svars.DEVICE.WALL_MOUNTED.ordinal()||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()) {
            FPMatch.getInstance().InitMatch(0,"http://www.fgtit.com/");
        }
        int employee_count_actual=0;
        Cursor cnt_c=database.rawQuery("SELECT COUNT(*) FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"'",null);
        if(cnt_c.moveToFirst()){
            do{
                employee_count_actual=cnt_c.getInt(0);
            }while(cnt_c.moveToNext());}
        final Stopwatch stw=new Stopwatch();


        stw.start();
        final int employee_count=employee_count_actual;
        // final int emp_per_thread=2500;
        //    final int max_thread_count=(employee_count+(emp_per_thread-1))/emp_per_thread;
        final int max_thread_count=1;//svars.getCpuCores();
        final int emp_per_thread=(employee_count+(max_thread_count-1))/max_thread_count;


        Log.e("Turbo match =>","Employee loading_time =>"+stw.elapsed(TimeUnit.MILLISECONDS));
        Log.e("Turbo match =>","Employee fp count =>"+employee_count);


        stw.stop();
        stw.reset();
        stw.start();
        //final int max_thread_count=employee_count/emp_per_thread;
        // (value + 14) / 15;
        Log.e("Turbo match =>","Threads to generate =>"+max_thread_count);

        exceutioner_threads= new Thread[max_thread_count];

        turbo_match_found=false;
        final int[] finish_count = {0};
        for(int i=0;i<exceutioner_threads.length;i++)
        {
//           final FPMatch fpm=new FPMatch();
//           fpm.InitMatch(0,"http://www.fgtit.com/");
            Log.e("Turbo match =>","Generating thread =>"+i+"/"+exceutioner_threads.length);

            if(turbo_match_found)break;
            final int finalI = i;
            // try {
            exceutioner_threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int actual_var_index = (emp_per_thread * finalI);
                        int emp_per_thread_intt = employee_count - actual_var_index;
                        String qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id NOT NULL AND employee_id NOT IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                        String qry2="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;


                        Cursor c = database.rawQuery(qry2, null);
                        if (c.moveToFirst()) {
                            do {
                                if (turbo_match_found) break;
                                Log.e("Turbo match =>", finalI+" :Matching =>" + c.getString(c.getColumnIndex("employee_id")));
                                if(svars.current_device(act)==svars.DEVICE.WALL_MOUNTED.ordinal()/*||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()*/)
                                {
//            if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(svars.fp_my_r_thumb_bt_device)) > svars.matching_acuracy) {

                                    if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(c.getString(c.getColumnIndex("data")))) > svars.matching_acuracy) {



                                        Log.e("FPMATCH ", "OBTAINED ");


                                        inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), verification_type.clock_out.ordinal(),1);
                                        turbo_match_found = true;
                                    }
                                }else{
                                    int mm_score=1000;
                                    try {

                                        Fmd ffm1 = UareUGlobal.GetImporter().ImportFmd(model, main_fmd_format, main_fmd_format);
                                        Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(c.getString(c.getColumnIndex("data")), 0), main_fmd_format, main_fmd_format);
                                        //   Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(svars.fp_my_r_index_wall_mat_device, 0), main_fmd_format, main_fmd_format);
                                        //  Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(svars.fp_my_r_index_8_inch, 0), main_fmd_format, main_fmd_format);



                                        mm_score = UareUGlobal.GetEngine().Compare(ffm1, 0, ffm2, 0);
                                    }catch (UareUException EX){

                                        Log.e("Turbo match =>","Matching error ur=>"+EX.getMessage());

                                    }catch (Throwable ex){
                                        ex.printStackTrace();
                                        Log.e("Turbo match =>","Matching error th =>"+ex.getMessage());

                                    }
                                    Log.e("Turbo match =>","Matching error score=>"+mm_score);
                                    if (mm_score <svars.matching_error_margin) {

                                        Log.e("FPMATCH ", "OBTAINED ");
                                        ContentValues cv =new ContentValues();
                                        cv.put("data_usage_frequency",""+System.currentTimeMillis());
                                        database.update("employee_data_table",cv,"id="+c.getInt(c.getColumnIndex("id")),null);

                                        inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), verification_type.clock_out.ordinal(),1);
                                        turbo_match_found = true;
                                    }
                                }





                            } while (c.moveToNext());

                        } else {

                            Log.e("Turbo match =>", "Thread " + finalI + " has no records to match");
//                                inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
//                                if(!turbo_match_found){load_match_not_found_reason(model,inter,clock_mode);}

                        }
                        if(!turbo_match_found)
                        {
                            c = database.rawQuery(qry, null);
                            if (c.moveToFirst()) {
                                do {
                                    if (turbo_match_found) break;
                                    Log.e("Turbo match =>", finalI+" :Matching =>" + c.getString(c.getColumnIndex("employee_id")));
                                    if(svars.current_device(act)==svars.DEVICE.WALL_MOUNTED.ordinal()/*||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()*/)
                                    {
//            if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(svars.fp_my_r_thumb_bt_device)) > svars.matching_acuracy) {

                                        if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(c.getString(c.getColumnIndex("data")))) > svars.matching_acuracy) {



                                            Log.e("FPMATCH ", "OBTAINED ");


                                            inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), 0,1);
                                            turbo_match_found = true;
                                        }
                                    }else{
                                        int mm_score=1000;
                                        try {

                                            Fmd ffm1 = UareUGlobal.GetImporter().ImportFmd(model, main_fmd_format, main_fmd_format);
                                            Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(c.getString(c.getColumnIndex("data")), 0), main_fmd_format, main_fmd_format);
                                            //   Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(svars.fp_my_r_index_wall_mat_device, 0), main_fmd_format, main_fmd_format);
                                            //  Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(svars.fp_my_r_index_8_inch, 0), main_fmd_format, main_fmd_format);



                                            mm_score = UareUGlobal.GetEngine().Compare(ffm1, 0, ffm2, 0);
                                        }catch (UareUException EX){

                                            Log.e("Turbo match =>","Matching error ur=>"+EX.getMessage());

                                        }catch (Throwable ex){
                                            ex.printStackTrace();
                                            Log.e("Turbo match =>","Matching error th =>"+ex.getMessage());

                                        }
                                        Log.e("Turbo match =>","Matching error score=>"+mm_score);
                                        if (mm_score <svars.matching_error_margin) {

                                            Log.e("FPMATCH ", "OBTAINED ");
                                            ContentValues cv =new ContentValues();
                                            cv.put("data_usage_frequency",""+System.currentTimeMillis());
                                            database.update("employee_data_table",cv,"id="+c.getInt(c.getColumnIndex("id")),null);

                                            inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), 0,1);
                                            turbo_match_found = true;
                                        }
                                    }





                                } while (c.moveToNext());

                            } else {

                                Log.e("Turbo match =>", "Thread " + finalI + " has no records to match");
                                inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                                if(!turbo_match_found){load_match_not_found_reason(model,inter,0);}

                            }
                        }
                        finish_count[0]++;
                        Log.e("Turbo match =>", "Thread finish : " + finish_count[0]);
                        percent_calculation pc = new percent_calculation(max_thread_count + "", finish_count[0] + "");
                        inter.on_match_progress_changed(Integer.parseInt(pc.per_balance));
                        if (max_thread_count == finish_count[0]) {
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                            if(!turbo_match_found&&load_match_failed_reason){load_match_not_found_reason(model,inter,0);}
                        }


                    }catch (Throwable ex){

                        Log.e("Main Turbo match =>", "Thread " + finalI + " has no records to match");

                    }
                }
            });
            exceutioner_threads[i].start();
            /*}catch (Throwable ex){

                Log.e("Turbo match =>","Thread generation error =>"+i+ex.getMessage());

            }*/
        }
        Log.e("Turbo match =>","Thread generation time =>"+stw.elapsed(TimeUnit.MILLISECONDS));
        return clock_mode;
    }
    public boolean can_clock(boolean in, String empid)
    {
        String qry="SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id " +
                "WHERE ect.employee_id='"+empid+"' AND (ect.clock_out_time IS NULL OR ect.clock_out_time ='')";//clocking out check if record of in without out



        Cursor c = database.rawQuery(qry, null);
        if (c.moveToFirst()) {
            c.close();
            return !in;
        }else{
            return in;
        }
        //  return false;
    }
    public void load_match(final byte[] model, final matching_interface inter,final int clock_mode,final String emp_id)
    {
        Log.e("CPU CORE COUNT :",""+svars.getCpuCores());
        if(svars.current_device(act)==svars.DEVICE.WALL_MOUNTED.ordinal()||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()) {
            FPMatch.getInstance().InitMatch(0,"http://www.fgtit.com/");
        }
        int employee_count_actual=0;
        Cursor cnt_c=database.rawQuery("SELECT COUNT(*) FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"'",null);
        if(cnt_c.moveToFirst()){
            do{
                employee_count_actual=cnt_c.getInt(0);
            }while(cnt_c.moveToNext());}
        final Stopwatch stw=new Stopwatch();


        stw.start();
        final int employee_count=employee_count_actual;
        // final int emp_per_thread=2500;
        //    final int max_thread_count=(employee_count+(emp_per_thread-1))/emp_per_thread;
        final int max_thread_count=1;//svars.getCpuCores();
        final int emp_per_thread=(employee_count+(max_thread_count-1))/max_thread_count;


        Log.e("Turbo match =>","Employee loading_time =>"+stw.elapsed(TimeUnit.MILLISECONDS));
        Log.e("Turbo match =>","Employee fp count =>"+employee_count);


        stw.stop();
        stw.reset();
        stw.start();
        //final int max_thread_count=employee_count/emp_per_thread;
        // (value + 14) / 15;
        Log.e("Turbo match =>","Threads to generate =>"+max_thread_count);

        exceutioner_threads= new Thread[max_thread_count];

        turbo_match_found=false;
        final int[] finish_count = {0};
        for(int i=0;i<exceutioner_threads.length;i++)
        {
//           final FPMatch fpm=new FPMatch();
//           fpm.InitMatch(0,"http://www.fgtit.com/");
            Log.e("Turbo match =>","Generating thread =>"+i+"/"+exceutioner_threads.length);

            if(turbo_match_found)break;
            final int finalI = i;
            // try {
            exceutioner_threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int actual_var_index = (emp_per_thread * finalI);
                        int emp_per_thread_intt = employee_count - actual_var_index;
                        String qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id='"+emp_id+"' LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                        switch (clock_mode)
                        {
                            case 0:
                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id ='"+emp_id+"' AND employee_id NOT IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;

                                break;
                            case 1:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id ='"+emp_id+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                            case 5:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id ='"+emp_id+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                            case 6:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id ='"+emp_id+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_checking_table ect ON eit.sid=ect.employee_id WHERE ect.check_in_time IS NULL OR ect.check_in_time ='') LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                        }
                        Cursor c = database.rawQuery(qry, null);
                        if (c.moveToFirst()) {
                            do {
                                if (turbo_match_found) break;
                                Log.e("Turbo match =>", finalI+" :Matching =>" + c.getString(c.getColumnIndex("employee_id")));
                                if(svars.current_device(act)==svars.DEVICE.WALL_MOUNTED.ordinal()/*||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()*/)
                                {
                                    if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(c.getString(c.getColumnIndex("data")))) > svars.matching_acuracy) {



                                        Log.e("FPMATCH ", "OBTAINED ");


                                        inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), clock_mode,1);
                                        turbo_match_found = true;
                                    }
                                }else{
                                    int mm_score=1000;
                                    try {

                                        Fmd ffm1 = UareUGlobal.GetImporter().ImportFmd(model, main_fmd_format, main_fmd_format);
                                        Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(c.getString(c.getColumnIndex("data")), 0), main_fmd_format, main_fmd_format);



                                        mm_score = UareUGlobal.GetEngine().Compare(ffm1, 0, ffm2, 0);
                                    }finally {
                                        //   ex.printStackTrace();
                                        //  Log.e("Turbo match =>","Matching error th =>"+ex.getMessage());

                                    }
                                    Log.e("Turbo match =>","Matching error score=>"+mm_score);
                                    if (mm_score <svars.matching_error_margin) {

                                        Log.e("FPMATCH ", "OBTAINED ");


                                        inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), clock_mode,1);
                                        c.close();
                                        turbo_match_found = true;
                                    }
                                }





                            } while (c.moveToNext());
                            c.close();

                        } else {
                            c.close();

                            Log.e("Turbo match =>", "Thread " + finalI + " has no records to match");

                        }
                        finish_count[0]++;
                        Log.e("Turbo match =>", "Thread finish : " + finish_count[0]);
                        percent_calculation pc = new percent_calculation(max_thread_count + "", finish_count[0] + "");
                        inter.on_match_progress_changed(Integer.parseInt(pc.per_balance));
                        if (max_thread_count == finish_count[0]) {
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                            if(!turbo_match_found){load_match_not_found_reason(model,inter,clock_mode);}
                        }


                    }catch (Throwable ex){

                        Log.e("Main Turbo match =>", "Thread " + finalI + " has no records to match");

                    }
                }
            });
            exceutioner_threads[i].start();
            /*}catch (Throwable ex){

                Log.e("Turbo match =>","Thread generation error =>"+i+ex.getMessage());

            }*/
        }
        Log.e("Turbo match =>","Thread generation time =>"+stw.elapsed(TimeUnit.MILLISECONDS));

    }

    public void load_face_match(final byte[] model, final matching_interface inter,final int clock_mode,boolean student)
    {
        final String member_category=student?"1":"2";
        Log.e("CPU CORE COUNT :",""+svars.getCpuCores());
        if(svars.current_device(act)==svars.DEVICE.WALL_MOUNTED.ordinal()||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()) {
            FPMatch.getInstance().InitMatch(0,"http://www.fgtit.com/");
        }
        int employee_count_actual=0;
        Cursor cnt_c=database.rawQuery("SELECT COUNT(id) FROM member_data_table WHERE data_type='"+svars.data_type_indexes.photo+"'",null);
        if(cnt_c.moveToFirst()){
            do{
                employee_count_actual=cnt_c.getInt(0);
            }while(cnt_c.moveToNext());}
        final Stopwatch stw=new Stopwatch();


        stw.start();
        final int employee_count=employee_count_actual;
        // final int emp_per_thread=2500;
        //    final int max_thread_count=(employee_count+(emp_per_thread-1))/emp_per_thread;
        final int max_thread_count=1;//svars.getCpuCores();
        final int emp_per_thread=(employee_count+(max_thread_count-1))/max_thread_count;


        Log.e("Turbo match =>","Data count loading_time =>"+stw.elapsed(TimeUnit.MILLISECONDS));
        Log.e("Turbo match =>","Data count =>"+employee_count);


        stw.stop();
        stw.reset();
        stw.start();
        //final int max_thread_count=employee_count/emp_per_thread;
        // (value + 14) / 15;
        Log.e("Turbo match =>","Threads to generate =>"+max_thread_count);

        exceutioner_threads= new Thread[max_thread_count];

        turbo_match_found=false;
        final int[] finish_count = {0};
        for(int i=0;i<exceutioner_threads.length;i++)
        {
//           final FPMatch fpm=new FPMatch();
//           fpm.InitMatch(0,"http://www.fgtit.com/");
            Log.e("Turbo match =>","Generating thread =>"+i+"/"+exceutioner_threads.length);

            if(turbo_match_found)break;
            final int finalI = i;
            // try {
            exceutioner_threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int actual_var_index = (emp_per_thread * finalI);
                        int emp_per_thread_intt = employee_count - actual_var_index;
                        String qry="SELECT mdt.id,mdt.member_id,mdt.data,mdt.data_index FROM member_data_table mdt INNER JOIN member_info_table mit ON mit.sid=mdt.member_id WHERE mit.category='"+member_category+"' AND mdt.data_type='"+svars.data_type_indexes.photo+"' AND mdt.data_index='"+svars.image_indexes.croped_face+"' AND mdt.member_id NOT NULL "+/*AND mdt.data_index IN (2,3) */"ORDER BY mdt.data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                        switch (clock_mode)
                        {
                            case 0:
                                qry="SELECT * FROM member_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND member_id NOT NULL AND member_id NOT IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN member_clock_table ect ON eit.sid=ect.member_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='' AND eit.category ='"+member_category+"') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;

                                break;
                            case 1:



                                qry="SELECT * FROM member_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND member_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN member_clock_table ect ON eit.sid=ect.member_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='' AND eit.category='"+member_category+"') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                            case 5:



                                qry="SELECT * FROM member_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND member_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN member_clock_table ect ON eit.sid=ect.member_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                            case 6:



                                qry="SELECT * FROM member_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND member_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_checking_table ect ON eit.sid=ect.member_id WHERE ect.check_in_time IS NULL OR ect.check_in_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                        }
                        Cursor c = database.rawQuery(qry, null);
                        if (c.moveToFirst()) {
                            do {
                                if (turbo_match_found) break;
                                Log.e("Turbo match =>", finalI+" :Matching =>" + c.getString(c.getColumnIndex("member_id")));





                                if (face_handler.match_ok(model,svars.WORKING_APP.file_path_employee_data+c.getString(c.getColumnIndex("data"))) ) {

                                    Log.e("FACE MATCH ", "OBTAINED ");
                                    ContentValues cv =new ContentValues();
                                    cv.put("data_usage_frequency",""+System.currentTimeMillis());
                                    database.update("member_data_table",cv,"id="+c.getInt(c.getColumnIndex("id")),null);

                                    inter.on_match_found(c.getString(c.getColumnIndex("member_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), clock_mode,1);
                                    turbo_match_found = true;
                                }






                            } while (c.moveToNext());

                        } else {

                            Log.e("Turbo match =>", "Thread " + finalI + " has no records to match");
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                            if(!turbo_match_found){load_match_not_found_reason(model,inter,clock_mode);}

                        }
                        finish_count[0]++;
                        Log.e("Turbo match =>", "Thread finish : " + finish_count[0]);
                        percent_calculation pc = new percent_calculation(max_thread_count + "", finish_count[0] + "");
                        inter.on_match_progress_changed(Integer.parseInt(pc.per_balance));
                        if (max_thread_count == finish_count[0]) {
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                            if(!turbo_match_found){load_match_not_found_reason(model,inter,clock_mode);}
                        }


                    }catch (Throwable ex){

                        Log.e("Main Turbo match =>", "Thread " + finalI + " Error :"+ex);

                    }
                }
            });
            exceutioner_threads[i].start();
            /*}catch (Throwable ex){

                Log.e("Turbo match =>","Thread generation error =>"+i+ex.getMessage());

            }*/
        }
        Log.e("Turbo match =>","Thread generation time =>"+stw.elapsed(TimeUnit.MILLISECONDS));

    }
    public void load_face_match(final String model, final matching_interface inter, final int clock_mode, boolean student)
    {
        final String member_category=student?"1":"2";
        Log.e("CPU CORE COUNT :",""+svars.getCpuCores());
        if(svars.current_device(act)==svars.DEVICE.WALL_MOUNTED.ordinal()||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()) {
            FPMatch.getInstance().InitMatch(0,"http://www.fgtit.com/");
        }
        int employee_count_actual=0;
        Cursor cnt_c=database.rawQuery("SELECT COUNT(id) FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.photo+"'",null);
        if(cnt_c.moveToFirst()){
            do{
                employee_count_actual=cnt_c.getInt(0);
            }while(cnt_c.moveToNext());}
        final Stopwatch stw=new Stopwatch();


        stw.start();
        final int employee_count=employee_count_actual;
        // final int emp_per_thread=2500;
        //    final int max_thread_count=(employee_count+(emp_per_thread-1))/emp_per_thread;
        final int max_thread_count=1;//svars.getCpuCores();
        final int emp_per_thread=(employee_count+(max_thread_count-1))/max_thread_count;


        Log.e("Turbo match =>","Data count loading_time =>"+stw.elapsed(TimeUnit.MILLISECONDS));
        Log.e("Turbo match =>","Data count =>"+employee_count);


        stw.stop();
        stw.reset();
        stw.start();
        //final int max_thread_count=employee_count/emp_per_thread;
        // (value + 14) / 15;
        Log.e("Turbo match =>","Threads to generate =>"+max_thread_count);

        exceutioner_threads= new Thread[max_thread_count];

        turbo_match_found=false;
        final int[] finish_count = {0};
        for(int i=0;i<exceutioner_threads.length;i++)
        {
//           final FPMatch fpm=new FPMatch();
//           fpm.InitMatch(0,"http://www.fgtit.com/");
            Log.e("Turbo match =>","Generating thread =>"+i+"/"+exceutioner_threads.length);

            if(turbo_match_found)break;
            final int finalI = i;
            // try {
            exceutioner_threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int actual_var_index = (emp_per_thread * finalI);
                        int emp_per_thread_intt = employee_count - actual_var_index;
                        String qry="SELECT mdt.id,mdt.employee_id,mdt.data,mdt.data_index FROM employee_data_table mdt " +
                                "INNER JOIN employee_info_table mit ON mit.sid=mdt.employee_id " +
                                "WHERE mdt.data_type='"+svars.data_type_indexes.photo+"' " +
                                "AND mdt.data_index='"+svars.image_indexes.croped_face+"' " +
                                "AND mdt.employee_id NOT NULL "+/*AND mdt.data_index IN (2,3) */"" +
                                "ORDER BY mdt.data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                        /// qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id NOT NULL LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                        switch (clock_mode)
                        {
                            case 0:
                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND data_index='"+svars.image_indexes.croped_face+"' AND employee_id NOT NULL AND employee_id NOT IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;

                                break;
                            case 1:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND data_index='"+svars.image_indexes.croped_face+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                            case 5:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND data_index='"+svars.image_indexes.croped_face+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit " +
                                        "INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id " +
                                        "WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') " +
                                        "AND eit.sid NOT IN(SELECT employee_id FROM employee_checking_table echt WHERE  ect.check_in_time IS NULL OR ect.check_in_time ='' )" +
                                        "ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                            case 6:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND data_index='"+svars.image_indexes.croped_face+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit " +
                                        "INNER JOIN employee_checking_table ect ON eit.sid=ect.employee_id " +
                                        "WHERE ect.check_in_time IS NULL OR ect.check_in_time ='') " +
                                        "ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                        }
                        Cursor c = database.rawQuery(qry, null);
                        if (c.moveToFirst()) {
                            do {
                                if (turbo_match_found) break;
                                Log.e("Turbo match =>", finalI+" :Matching =>" + c.getString(c.getColumnIndex("member_id")));





                                if (face_handler.match_ok(model,svars.WORKING_APP.file_path_employee_data+c.getString(c.getColumnIndex("data"))) ) {

                                    Log.e("FACE MATCH ", "OBTAINED ");
                                    ContentValues cv =new ContentValues();
                                    cv.put("data_usage_frequency",""+System.currentTimeMillis());
                                    database.update("member_data_table",cv,"id="+c.getInt(c.getColumnIndex("id")),null);

                                    inter.on_match_found(c.getString(c.getColumnIndex("member_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), clock_mode,2);
                                    turbo_match_found = true;
                                }






                            } while (c.moveToNext());

                        } else {

                            Log.e("Turbo match =>", "Thread " + finalI + " has no records to match");
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                            if(!turbo_match_found){/*load_match_not_found_reason(null,inter,clock_mode);*/}

                        }
                        finish_count[0]++;
                        Log.e("Turbo match =>", "Thread finish : " + finish_count[0]);
                        percent_calculation pc = new percent_calculation(max_thread_count + "", finish_count[0] + "");
                        //    inter.on_match_progress_changed(Integer.parseInt(pc.per_balance));
                        if (max_thread_count == finish_count[0]) {
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                            if(!turbo_match_found){/*load_match_not_found_reason(null,inter,clock_mode);*/}
                        }


                    }catch (Throwable ex){

                        Log.e("Main Turbo match =>", "Thread " + finalI + " Error :"+ex);

                    }
                }
            });
            exceutioner_threads[i].start();
            /*}catch (Throwable ex){

                Log.e("Turbo match =>","Thread generation error =>"+i+ex.getMessage());

            }*/
        }
        Log.e("Turbo match =>","Thread generation time =>"+stw.elapsed(TimeUnit.MILLISECONDS));

    }

    public void load_match_unique(final byte[] model, final matching_interface inter, final String mem_id)
    {
        Log.e("CPU CORE COUNT :",""+ svars.getCpuCores());
        if(svars.current_device(act)== svars.DEVICE.WALL_MOUNTED.ordinal()|| svars.current_device(act)== svars.DEVICE.BIO_MINI.ordinal()) {
            FPMatch.getInstance().InitMatch(0,"http://www.fgtit.com/");
        }
        int employee_count_actual=0;
        Cursor cnt_c=database.rawQuery("SELECT COUNT(*) FROM employee_data_table WHERE data_type='"+ svars.data_type_indexes.fingerprints+"'",null);
        if(cnt_c.moveToFirst()){
            do{
                employee_count_actual=cnt_c.getInt(0);
            }while(cnt_c.moveToNext());}
        final Stopwatch stw=new Stopwatch();


        stw.start();
        final int employee_count=employee_count_actual;
        // final int emp_per_thread=2500;
        //    final int max_thread_count=(employee_count+(emp_per_thread-1))/emp_per_thread;
        final int max_thread_count=1;//svars.getCpuCores();
        final int emp_per_thread=(employee_count+(max_thread_count-1))/max_thread_count;


        Log.e("Turbo match =>","Employee loading_time =>"+stw.elapsed(TimeUnit.MILLISECONDS));
        Log.e("Turbo match =>","Employee fp count =>"+employee_count);


        stw.stop();
        stw.reset();
        stw.start();
        //final int max_thread_count=employee_count/emp_per_thread;
        // (value + 14) / 15;
        Log.e("Turbo match =>","Threads to generate =>"+max_thread_count);

        exceutioner_threads= new Thread[max_thread_count];

        turbo_match_found=false;
        final int[] finish_count = {0};
        for(int i=0;i<exceutioner_threads.length;i++)
        {
//           final FPMatch fpm=new FPMatch();
//           fpm.InitMatch(0,"http://www.fgtit.com/");
            Log.e("Turbo match =>","Generating thread =>"+i+"/"+exceutioner_threads.length);

            if(turbo_match_found)break;
            final int finalI = i;
            // try {
            exceutioner_threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int actual_var_index = (emp_per_thread * finalI);
                        int emp_per_thread_intt = employee_count - actual_var_index;
                        String qry="SELECT * FROM employee_data_table WHERE data_type='"+ svars.data_type_indexes.fingerprints+"' AND employee_id ='"+mem_id+"' LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;


                        Cursor c = database.rawQuery(qry, null);
                        int h=0;
                        if (c.moveToFirst()) {
                            do {
                                percent_calculation pc = new percent_calculation(c.getCount() + "", h+ "");
                                inter.on_match_progress_changed(Integer.parseInt(pc.per_balance));
                                h++;

                                if (turbo_match_found) break;
                                Log.e("Turbo match =>", finalI+" :Matching =>" + c.getString(c.getColumnIndex("employee_id")));
                                if(svars.current_device(act)== svars.DEVICE.WALL_MOUNTED.ordinal()/*||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()*/)
                                {
                                    if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(c.getString(c.getColumnIndex("data")))) > svars.matching_acuracy) {



                                        Log.e("FPMATCH ", "OBTAINED ");


                                        inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), 3,1);
                                        turbo_match_found = true;
                                    }
                                }else{
                                    int mm_score=1000;
                                    try {

                                        Fmd ffm1 = UareUGlobal.GetImporter().ImportFmd(model, main_fmd_format, main_fmd_format);
                                        Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(c.getString(c.getColumnIndex("data")), 0), main_fmd_format, main_fmd_format);



                                        mm_score = UareUGlobal.GetEngine().Compare(ffm1, 0, ffm2, 0);
                                    }catch (UareUException EX){

                                        Log.e("Turbo match =>","Matching error ur=>"+EX.getMessage());

                                    }catch (Throwable ex){
                                        ex.printStackTrace();
                                        Log.e("Turbo match =>","Matching error th =>"+ex.getMessage());

                                    }
                                    Log.e("Turbo match =>","Matching error score=>"+mm_score);
                                    if (mm_score < svars.matching_error_margin) {

                                        Log.e("FPMATCH ", "OBTAINED ");


                                        inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), 3,1);
                                        turbo_match_found = true;
                                    }
                                }





                            } while (c.moveToNext());

                        } else {

                            Log.e("Turbo match =>", "Thread " + finalI + " has no records to match");

                        }
                        finish_count[0]++;
                        percent_calculation pc = new percent_calculation(max_thread_count + "", finish_count[0] + "");
                        inter.on_match_progress_changed(Integer.parseInt(pc.per_balance));
                        Log.e("Turbo match =>", "Thread finish : " + finish_count[0]);
                        if (max_thread_count == finish_count[0]) {
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                        }


                    }catch (Throwable ex){

                        Log.e("Main Turbo match =>", "Thread " + finalI + " has no records to match");

                    }
                }
            });
            exceutioner_threads[i].start();
            /*}catch (Throwable ex){

                Log.e("Turbo match =>","Thread generation error =>"+i+ex.getMessage());

            }*/
        }
        Log.e("Turbo match =>","Thread generation time =>"+stw.elapsed(TimeUnit.MILLISECONDS));

    }
    public void load_match_not_found_reason(final byte[] model, final matching_interface inter,final int clock_mode)
    {
        Log.e("CPU CORE COUNT :",""+svars.getCpuCores());
        if(svars.current_device(act)==svars.DEVICE.WALL_MOUNTED.ordinal()||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()) {
            FPMatch.getInstance().InitMatch(0,"http://www.fgtit.com/");
        }


        final Stopwatch stw=new Stopwatch();
        stw.start();


        Log.e("Turbo match =>","Employee loading_time =>"+stw.elapsed(TimeUnit.MILLISECONDS));



        stw.stop();
        stw.reset();
        stw.start();




        try {

            String qry_static="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND COALESCE (employee_id, '') = ''";
            String qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND COALESCE (employee_id, '') = ''";
            switch (clock_mode)
            {
                case 0:
                    qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ";

                    break;
                case 1:
                    qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id NOT NULL AND employee_id NOT IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='')";



                    break;
                case 5:



                    qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id NOT IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='')";
                    break;
                case 6:



                    qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_checking_table ect ON eit.sid=ect.employee_id WHERE ect.check_in_time IS NULL OR ect.check_in_time ='')";
                    break;
            }
            Cursor c = database.rawQuery(qry_static, null);
            if (c.moveToFirst()) {
                do {
                    if (turbo_match_found) break;
                    Log.e("Turbo match =>", " :Matching for reason =>" + c.getString(c.getColumnIndex("employee_id")));
                    if(svars.current_device(act)==svars.DEVICE.WALL_MOUNTED.ordinal()/*||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()*/)
                    {
                        if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(c.getString(c.getColumnIndex("data")))) > svars.matching_acuracy) {



                            Log.e("FPMATCH ", "Reason OBTAINED ");


                            inter.on_match_faild_reason_found(0,c.getString(c.getColumnIndex("employee_id")));
                            turbo_match_found = true;
                        }
                    }else{
                        int mm_score=1000;
                        try {

                            Fmd ffm1 = UareUGlobal.GetImporter().ImportFmd(model, main_fmd_format, main_fmd_format);
                            Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(c.getString(c.getColumnIndex("data")), 0), main_fmd_format, main_fmd_format);



                            mm_score = UareUGlobal.GetEngine().Compare(ffm1, 0, ffm2, 0);
                        }catch (UareUException EX){

                            Log.e("Turbo match =>","Matching error ur=>"+EX.getMessage());

                        }catch (Throwable ex){
                            ex.printStackTrace();
                            Log.e("Turbo match =>","Matching error th =>"+ex.getMessage());

                        }
                        Log.e("Turbo match =>","Matching error score=>"+mm_score);
                        if (mm_score <svars.matching_error_margin) {

                            Log.e("FPMATCH ", "Reason OBTAINED ");

                            inter.on_match_faild_reason_found(0,c.getString(c.getColumnIndex("employee_id")));

                            turbo_match_found = true;
                        }
                    }





                } while (c.moveToNext());
                c.close();
            } else {

                Log.e("Turbo match =>", "Thread  has no records to match");

            }
            c.close();
            c = database.rawQuery(qry, null);
            if (c.moveToFirst()) {
                do {
                    if (turbo_match_found) break;
                    Log.e("Turbo match =>", " :Matching  for reason =>" + c.getString(c.getColumnIndex("employee_id")));
                    if(svars.current_device(act)==svars.DEVICE.WALL_MOUNTED.ordinal()/*||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()*/)
                    {
                        if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(c.getString(c.getColumnIndex("data")))) > svars.matching_acuracy) {



                            Log.e("FPMATCH ", "Reason OBTAINED ");


                            inter.on_match_faild_reason_found(clock_mode==0?1:clock_mode==1?2:clock_mode==5?3:4,c.getString(c.getColumnIndex("employee_id")));

                            turbo_match_found = true;
                        }
                    }else{
                        int mm_score=1000;
                        try {

                            Fmd ffm1 = UareUGlobal.GetImporter().ImportFmd(model, main_fmd_format, main_fmd_format);
                            Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(c.getString(c.getColumnIndex("data")), 0), main_fmd_format, main_fmd_format);



                            mm_score = UareUGlobal.GetEngine().Compare(ffm1, 0, ffm2, 0);
                        }catch (UareUException EX){

                            Log.e("Turbo match =>","Matching error ur=>"+EX.getMessage());

                        }catch (Throwable ex){
                            ex.printStackTrace();
                            Log.e("Turbo match =>","Matching error th =>"+ex.getMessage());

                        }
                        Log.e("Turbo match =>","Matching error score=>"+mm_score);
                        if (mm_score <svars.matching_error_margin) {

                            Log.e("FPMATCH ", "Reason OBTAINED ");


                            inter.on_match_faild_reason_found(clock_mode==0?1:clock_mode==1?2:clock_mode==5?3:4,c.getString(c.getColumnIndex("employee_id")));
                            turbo_match_found = true;
                        }
                    }





                } while (c.moveToNext());
                c.close();
            } else {

                Log.e("Turbo match =>", "Thread  has no records to match");

            }
            c.close();



            if (!turbo_match_found) {
                inter.on_match_faild_reason_found(666,null);


            }


        }catch (Throwable ex){

            Log.e("Main Turbo match =>", "Thread has no records to match");

        }


        Log.e("Turbo match =>","Thread generation time =>"+stw.elapsed(TimeUnit.MILLISECONDS));

    }



    void kill_threads()
    {
        if(exceutioner_threads!=null)
        {
            for(Thread th :exceutioner_threads)
            {
                th.stop();
                th=null;

            }

        }
    }





     public member load_employee_from_idno(String idno)
    {




        Cursor c = database.rawQuery("SELECT * FROM member_info_table WHERE idno='"+idno+"'", null);

        if (c.moveToFirst()) {
            do {
                member emp=new member();
//                emp.sid = c.getString(c.getColumnIndex("sid"));
//                emp.idno = c.getString(c.getColumnIndex("idno"));
//                emp.full_name = c.getString(c.getColumnIndex("full_name"));






                emp.finger_prints=load_employee_data(c.getString(c.getColumnIndex("sid")),svars.data_type_indexes.fingerprints);
                emp.images=load_employee_data(c.getString(c.getColumnIndex("sid")),svars.data_type_indexes.photo);
               c.close();
                return emp;
            } while (c.moveToNext());
        }




        try {


        }catch (Exception ex){
            Log.e("Employee insert error",""+ex.getMessage());}
        return null;
    }

    public boolean can_clock_in(String emp_sid)
    {


        return !database.rawQuery("SELECT * FROM member_clock_table ect WHERE ect.employee_id='"+emp_sid+"' AND ect.clock_out_time IS NULL OR ect.clock_out_time =''",null).moveToFirst();



    }
 public boolean payment_code_exists(String payment_code)
    {


        return database.rawQuery("SELECT * FROM payment_codes_table WHERE pay_code='"+payment_code+"'",null).moveToFirst();



    }






}
