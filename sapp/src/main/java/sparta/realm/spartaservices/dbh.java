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
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
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
import sparta.realm.spartamodels.dyna_data;
import sparta.realm.spartamodels.dyna_data_obj;
import sparta.realm.spartamodels.dynamic_property;
import sparta.realm.spartamodels.member;
import sparta.realm.spartamodels.geo_fence;
import sparta.realm.spartamodels.percent_calculation;

import sparta.realm.spartamodels.sdb_model;
import sparta.realm.spartautils.Gpsprobe_r;
import sparta.realm.spartautils.app_control.SpartaApplication;
import sparta.realm.spartautils.app_control.models.sparta_app_version;
import sparta.realm.spartautils.face.face_handler;
import sparta.realm.spartautils.fp.sdks.fgtit.utils.ExtApi;
import sparta.realm.spartautils.s_bitmap_handler;
import sparta.realm.spartautils.s_cryptor;
import sparta.realm.spartautils.sparta_loc_util;
import sparta.realm.spartautils.sparta_mail_probe;
import sparta.realm.spartautils.sparta_string_compairer;
import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmDataClass;
import com.realm.annotations.sync_service_description;
import com.realm.annotations.sync_status;

import static sparta.realm.spartautils.app_control.SpartaApplication.realm;
import static sparta.realm.spartautils.fp.fp_handler_stf_usb_8_inch.main_fmd_format;



/**
 * Created by Thompsons on 01-Feb-17.
 */

public class dbh {
    static Context act;


    public static sdb_model main_db=null;
    public static SQLiteDatabase database=null;
    public static sdbw sd;
    public static boolean loaded_db=false;
    public dbh(Context act)
    {
        this.act=act;
        if(!loaded_db)
        {
            //setup_db_model();
            try {
                //  setup_db();
                setup_db_ann();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }



    }
    public dbh(RealmDataClass realm)
    {
        this.act= SpartaApplication.getAppContext();

        if(!loaded_db)
        {
            //setup_db_model();
            try {
                //  setup_db();
                setup_db_ann();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }



    }
    public  SQLiteDatabase getDatabase(){
        return database;
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






    /**
     * Sets up database by creating and adding missing tables and missing columns and indeces in their respective tables
     * @deprecated
     * This method is no longer used,its too cumbersum and error prone.
     * <p> Use {@link this#setup_db_ann()} instead.
     *
     *
     * @return void
     */
    @Deprecated
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

    /**
     * Sets up database by creating and adding missing tables and missing columns and indeces in their respective tables
     * @deprecated
     * This method is no longer used,its slow compaired to {@link this#setup_db_ann()} which is twice as fast with annotation.
     * <p> Use {@link this#setup_db_ann()} instead.
     *
     *
     *
     */
    @Deprecated()
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

        Log.e("Classes reflected :", "raw :" + sw.elapsed(TimeUnit.MICROSECONDS));

        svars.set_version_action_done(act, svars.version_action.DB_CHECK);
        Log.e("DB","Finished DB Verification");
        main_db=null;
        loaded_db=true;

    }

    /**
     *Sets up database by creating and adding missing tables and missing columns and indices in their respective tables from mappery of annotated data which is pre-reflected at pre build
     */
    void setup_db_ann()  {


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



        Stopwatch sw=new Stopwatch();
        sw.start();






        for (String s: realm.getDynamicClassPaths()) {


            Log.e("Classes reflected =>", "Ann :" + s);

            String table_name=realm.getPackageTable(s);
            try {
                Cursor cursor1 = database.rawQuery("SELECT * FROM "+table_name, null);
                cursor1.moveToFirst();
                if (!cursor1.isAfterLast()) {
                    do {
                        cursor1.getString(0);
                    } while (cursor1.moveToNext());
                }
                cursor1.close();
            } catch (Exception e) {
                database.execSQL(realm.getTableCreateSttment(table_name,false));
                database.execSQL(realm.getTableCreateSttment(table_name,true));
                String crt_stt=realm.getTableCreateIndexSttment(table_name);
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

            for (Map.Entry<String, String> col : realm.getTableColumns(table_name).entrySet()) {
                try {
                    Cursor cursor1 = database.rawQuery("SELECT count(" + col.getKey() + ") FROM "+table_name, null);
                    cursor1.moveToFirst();
                    if (!cursor1.isAfterLast()) {
                        do {
                            cursor1.getString(0);
                        } while (cursor1.moveToNext());
                    }
                    cursor1.close();
                } catch (Exception e) {
                    database.execSQL("ALTER TABLE "+table_name+" ADD COLUMN " + col.getKey() );
//                                database.execSQL("ALTER TABLE "+db_tb.table_name+" ADD COLUMN " + col.getKey() + " "+col.data_type+" "+col.default_value);
                }
            }








        }

        Log.e("Classes reflected :", "Ann :" + sw.elapsed(TimeUnit.MICROSECONDS));

        svars.set_version_action_done(act, svars.version_action.DB_CHECK);
        Log.e("DB","Finished DB Verification");
        main_db=null;
        loaded_db=true;

    }
/*
I thot of useng an interface
 */
    interface  anna_db_setup_process{

    }
     public void setup_db_annwise(SQLiteDatabase DB,sdb_model dbm)  {


//        sdb_model dbm=new sdb_model();
        dbm.db_name=svars.DB_NAME;
        dbm.db_path=svars.WORKING_APP.file_path_db(act);
        //   dbm.db_path=act.getExternalFilesDir(null).getAbsolutePath()+"/"+svars.DB_NAME;
        dbm.db_password=svars.DB_PASS;

        SQLiteDatabase.loadLibs(act);



        //  SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile.getAbsolutePath(), , null);
        database = SQLiteDatabase.openOrCreateDatabase(dbm.db_path, dbm.db_password, null);
        // if(svars.version_action_done(act, svars.version_action.DB_CHECK)){  loaded_db=true;return;}

        svars.set_photo_camera_type(act,svars.image_indexes.profile_photo,1);



        Stopwatch sw=new Stopwatch();
        sw.start();






        for (String s: realm.getDynamicClassPaths()) {


            Log.e("Classes reflected =>", "Ann :" + s);

            String table_name=realm.getPackageTable(s);
            try {
                Cursor cursor1 = database.rawQuery("SELECT * FROM "+table_name, null);
                cursor1.moveToFirst();
                if (!cursor1.isAfterLast()) {
                    do {
                        cursor1.getString(0);
                    } while (cursor1.moveToNext());
                }
                cursor1.close();
            } catch (Exception e) {
                database.execSQL(realm.getTableCreateSttment(table_name,false));
                database.execSQL(realm.getTableCreateSttment(table_name,true));
                String crt_stt=realm.getTableCreateIndexSttment(table_name);
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

            for (Map.Entry<String, String> col : realm.getTableColumns(table_name).entrySet()) {
                try {
                    Cursor cursor1 = database.rawQuery("SELECT count(" + col.getKey() + ") FROM "+table_name, null);
                    cursor1.moveToFirst();
                    if (!cursor1.isAfterLast()) {
                        do {
                            cursor1.getString(0);
                        } while (cursor1.moveToNext());
                    }
                    cursor1.close();
                } catch (Exception e) {
                    database.execSQL("ALTER TABLE "+table_name+" ADD COLUMN " + col.getKey() );
//                                database.execSQL("ALTER TABLE "+db_tb.table_name+" ADD COLUMN " + col.getKey() + " "+col.data_type+" "+col.default_value);
                }
            }








        }

        Log.e("Classes reflected :", "Ann :" + sw.elapsed(TimeUnit.MICROSECONDS));

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
    sdb_model.sdb_table table_from_dyna_property_class_ann(Class<?> main_class)
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

    public ArrayList<Object> load_dynamic_records(sync_service_description ssd, String[] table_filters)
    {
        ArrayList<Object> objs=new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM "+ssd.table_name+(table_filters==null?"":" "+conccat_sql_filters(table_filters))+" ORDER BY data_status DESC LIMIT "+ssd.chunk_size, null);


        if (c.moveToFirst()) {
            do {


                try {
                    objs.add(load_object_from_Cursor(c,Class.forName(ssd.object_package).newInstance()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();



        return objs;
    }

    /*
     *
     * Loads obects from cursor
     *
     *
     */
    public ArrayList<Object> load_dynamic_records_ann(sync_service_description ssd, String[] table_filters)
    {
        ArrayList<Object> objs=new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM "+ssd.table_name+(table_filters==null?"":" "+conccat_sql_filters(table_filters))+" ORDER BY data_status DESC LIMIT "+ssd.chunk_size, null);


        if (c.moveToFirst()) {
            do {


                try {

                    objs.add(realm.getObjectFromCursor(c,ssd.object_package));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();



        return objs;
    }


    public ArrayList<JSONObject> load_dynamic_json_records_ann(sync_service_description ssd, String[] table_filters)
    {
        ArrayList<JSONObject> objs=new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM "+ssd.table_name+(table_filters==null?"":" "+conccat_sql_filters(table_filters))+" ORDER BY data_status DESC LIMIT "+ssd.chunk_size, null);


        if (c.moveToFirst()) {
            do {


                try {

                    objs.add(realm.getJsonFromCursor(c,ssd.object_package));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();



        return objs;
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

    public void register_object_auto_ann(Boolean first_record,JSONObject j_obj,sync_service_description ssd)
    {

        if(first_record!=null&&first_record)
        {
            database.beginTransaction();
            Log.e(ssd.service_name+"::Insertion =>","transaction begun");

            return;
        }else if(first_record!=null&&first_record==false)
        {
            Log.e(ssd.service_name+"::Insertion =>","transaction complete");

            database.setTransactionSuccessful();
            database.endTransaction();
            return;
        }
        try {
            try{
                if(ssd.use_download_filter)
                {
                    database.execSQL(realm.getDeleteRecordSttment(ssd.table_name,j_obj.getString("id")));
                }

            }catch(Exception ex){

                Log.e("DELETING ERROR =>",""+ex.getMessage());
            }
            if(realm.jsonHasActiveKey(j_obj))
            {

                ContentValues cv= (ContentValues)realm.getContentValuesFromJson(j_obj,ssd.table_name);
                cv.put("sync_status", sync_status.syned.ordinal());

                Log.e(ssd.service_name+":: Insert result =>"," "+database.insert(ssd.table_name, null, cv));
if(ssd.service_name.equalsIgnoreCase("JobAllInventory"))
{
    Log.e("Timming error :",ssd.service_name+"::"+cv.toString());
}
            }





        }catch (Exception ex){
            Log.e("insert error",""+ex.getMessage());}




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

    public   interface  data_loading_interface{
        void onDataLoaded(ArrayList x);
        void onDataLoading(int percent,ArrayList x);

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










    public boolean payment_code_exists(String payment_code)
    {


        return database.rawQuery("SELECT * FROM payment_codes_table WHERE pay_code='"+payment_code+"'",null).moveToFirst();



    }






}
