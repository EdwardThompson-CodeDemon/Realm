package sparta.realm.Services;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.common.base.Stopwatch;
import com.google.common.reflect.ClassPath;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import dalvik.system.DexFile;
import sparta.realm.Activities.SpartaAppCompactActivity;
import sparta.realm.Activities.splash;
import sparta.realm.BuildConfig;


import sparta.realm.DataManagement.Models.Query;
import sparta.realm.R;
import sparta.realm.Realm;
import sparta.realm.spartamodels.db_class;
import sparta.realm.spartamodels.dyna_data;
import sparta.realm.spartamodels.dyna_data_obj;
import sparta.realm.spartamodels.dynamic_property;
import sparta.realm.spartamodels.member;

import sparta.realm.spartamodels.sdb_model;
import sparta.realm.spartautils.Gpsprobe_r;
import sparta.realm.spartautils.app_control.SpartaApplication;
import sparta.realm.spartautils.app_control.adapters.apk_versions_adapter;
import sparta.realm.spartautils.app_control.models.apk_version;
import sparta.realm.spartautils.app_control.models.sparta_app_version;
import sparta.realm.spartautils.s_bitmap_handler;
import sparta.realm.spartautils.s_cryptor;
import sparta.realm.spartautils.svars;
import sparta.realm.utils.AppConfig;

import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmDataClass;
import com.realm.annotations.sync_service_description;
import com.realm.annotations.sync_status;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static sparta.realm.Realm.realm;


/**
 * Created by Thompsons on 01-Feb-17.
 */

public class DatabaseManager {
    static Context act;


    public static sdb_model main_db = null;
    public static SQLiteDatabase database = null;
    //   public static sdbw sd;
    public static boolean loaded_db = false;
    public static String logTag = "DatabaseManager";

    public DatabaseManager(Context act) {
        this.act = act;
        if (!loaded_db) {
            //setup_db_model();
            try {
                //  setup_db();
                setup_db_ann();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    public DatabaseManager(RealmDataClass realm) {
        this.act = SpartaApplication.getAppContext();

        if (!loaded_db) {
            //setup_db_model();
            try {
                //  setup_db();
                setup_db_ann();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    public SQLiteDatabase getDatabase() {
        return database;
    }


    /**
     * Sets up database by creating and adding missing tables and missing columns and indeces in their respective tables
     *
     * @return void
     * @deprecated This method is no longer used,its too cumbersum and error prone.
     * <p> Use {@link this#setup_db_ann()} instead.
     */
    @Deprecated
    static void setup_db_model() {
        sdb_model dbm = new sdb_model();
        dbm.db_name = svars.DB_NAME;
        dbm.db_path = act.getExternalFilesDir(null).getAbsolutePath() + "/" + svars.DB_NAME;
        dbm.db_password = svars.DB_PASS;

        SQLiteDatabase.loadLibs(act);


        //  SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile.getAbsolutePath(), , null);
        database = SQLiteDatabase.openOrCreateDatabase(dbm.db_path, dbm.db_password, null);

        if (svars.version_action_done(act, svars.version_action.DB_CHECK)) {
            loaded_db = true;
            return;
        }

        sdb_model.sdb_table.column id = new sdb_model.sdb_table.column(false, "id", "INTEGER");
        id.extra_params = "PRIMARY KEY AUTOINCREMENT";
        sdb_model.sdb_table.column reg_time = new sdb_model.sdb_table.column(false, "reg_time", "DATETIME", "(datetime('now','localtime'))");
        sdb_model.sdb_table.column sync_status = new sdb_model.sdb_table.column(false, "sync_status");
        sdb_model.sdb_table.column transaction_no = new sdb_model.sdb_table.column(false, "transaction_no");
        sdb_model.sdb_table.column sid = new sdb_model.sdb_table.column(true, "sid");
        sdb_model.sdb_table.column sync_var = new sdb_model.sdb_table.column(true, "sync_var");
        sdb_model.sdb_table.column data_status = new sdb_model.sdb_table.column(true, "data_status");
        sdb_model.sdb_table.column user_id = new sdb_model.sdb_table.column(false, "user_id");
        sdb_model.sdb_table.column data_usage_frequency = new sdb_model.sdb_table.column(true, "data_usage_frequency");
        sdb_model.sdb_table.column lat = new sdb_model.sdb_table.column(false, "lat");
        sdb_model.sdb_table.column lon = new sdb_model.sdb_table.column(false, "lon");

        ArrayList<sdb_model.sdb_table.column> common_columns = new ArrayList();
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
            String dpex_path = cex.getName();
            Log.e("Class path =>", "" + dpex_path);
            String package_path = dpex_path.substring(0, dpex_path.lastIndexOf('.'));
            Log.e("Package path =>", "" + package_path);
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if (info.getName().startsWith(package_path)) {
                    final Class<?> clazz = info.load();
                    Log.e("Class reflected =>", "" + clazz.getName());
                    // do something with your clazz
                }
            }
        } catch (Exception ex) {
        }
        sdb_model.sdb_table member_info_table = new sdb_model.sdb_table("member_info_table");
        member_info_table.columns.addAll(common_columns);
        member_info_table.columns.add(new sdb_model.sdb_table.column(true, "first_name"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(true, "last_name"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(true, "full_name"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(true, "idno"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "dob"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "phone_no"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "email"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "category"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "sub_category"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "department"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "nationality"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "nfc"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "kra"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "nssf"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "nhif"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "employee_no"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "gender"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "reg_start_time"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "reg_end_time"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "site_id"));
        member_info_table.columns.add(new sdb_model.sdb_table.column(false, "job_department"));

        sdb_model.sdb_table member_data_table = new sdb_model.sdb_table("member_data_table");

        member_data_table.columns.addAll(common_columns);
        member_data_table.columns.add(new sdb_model.sdb_table.column(true, "data_type"));
        member_data_table.columns.add(new sdb_model.sdb_table.column(false, "data_index"));
        member_data_table.columns.add(new sdb_model.sdb_table.column(false, "data"));
        member_data_table.columns.add(new sdb_model.sdb_table.column(false, "data_format"));
        member_data_table.columns.add(new sdb_model.sdb_table.column(false, "data_storage_mode"));
        member_data_table.columns.add(new sdb_model.sdb_table.column(true, "employee_id"));


        sdb_model.sdb_table verification_activity_table = new sdb_model.sdb_table("verification_activity_table");

        verification_activity_table.columns.addAll(common_columns);
        verification_activity_table.columns.add(new sdb_model.sdb_table.column(false, "activity_name"));
        verification_activity_table.columns.add(new sdb_model.sdb_table.column(false, "activity_result_uom"));
        verification_activity_table.columns.add(new sdb_model.sdb_table.column(false, "workers_limit"));
        verification_activity_table.columns.add(new sdb_model.sdb_table.column(false, "activity_operation_mode"));
        verification_activity_table.columns.add(new sdb_model.sdb_table.column(false, "working_field"));
        verification_activity_table.columns.add(new sdb_model.sdb_table.column(false, "working_period_from"));
        verification_activity_table.columns.add(new sdb_model.sdb_table.column(false, "working_period_to"));

        sdb_model.sdb_table employee_verification_activities_table = new sdb_model.sdb_table("employee_verification_activities_table");

        employee_verification_activities_table.columns.addAll(common_columns);
        employee_verification_activities_table.columns.add(new sdb_model.sdb_table.column(true, "activity_id"));
        employee_verification_activities_table.columns.add(new sdb_model.sdb_table.column(true, "employee_id"));
        employee_verification_activities_table.columns.add(new sdb_model.sdb_table.column(false, "result"));
        employee_verification_activities_table.columns.add(new sdb_model.sdb_table.column(false, "working_field"));
        employee_verification_activities_table.columns.add(new sdb_model.sdb_table.column(false, "result_update_time"));
        employee_verification_activities_table.columns.add(new sdb_model.sdb_table.column(true, "site_id"));

        sdb_model.sdb_table geo_fences_table = new sdb_model.sdb_table("geo_fences_table");

        geo_fences_table.columns.addAll(common_columns);
        geo_fences_table.columns.add(new sdb_model.sdb_table.column(false, "fence_type"));
        geo_fences_table.columns.add(new sdb_model.sdb_table.column(false, "parent_type"));
        geo_fences_table.columns.add(new sdb_model.sdb_table.column(true, "parent_id"));


        sdb_model.sdb_table geo_fence_points_table = new sdb_model.sdb_table("geo_fence_points_table");

        geo_fence_points_table.columns.addAll(common_columns);
        geo_fence_points_table.columns.add(new sdb_model.sdb_table.column(true, "fence_id"));
        geo_fence_points_table.columns.add(new sdb_model.sdb_table.column(false, "lat"));
        geo_fence_points_table.columns.add(new sdb_model.sdb_table.column(false, "lon"));
        geo_fence_points_table.columns.add(new sdb_model.sdb_table.column(false, "radius"));


        sdb_model.sdb_table employee_verifications_table = new sdb_model.sdb_table("employee_verifications_table");

        employee_verifications_table.columns.addAll(common_columns);
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(true, "employee_id"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(false, "verification_mode"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(false, "verification_mode_index"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(false, "verification_type"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(false, "verification_data"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(false, "lat"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(false, "lon"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(true, "verification_field"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(true, "verification_activity"));
        employee_verifications_table.columns.add(new sdb_model.sdb_table.column(true, "site_id"));


        sdb_model.sdb_table member_clock_table = new sdb_model.sdb_table("member_clock_table");

        member_clock_table.columns.addAll(common_columns);
        member_clock_table.columns.add(new sdb_model.sdb_table.column(true, "employee_id"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "clock_in_time"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "clock_in_verification_mode"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "clock_in_verification_mode_index"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "clock_in_verification_data"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "clock_in_transaction_no"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "clock_in_user_id"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "clock_in_lat"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "clock_in_lon"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "clock_out_lat"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "clock_out_lon"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "clock_out_verification_mode"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "clock_out_verification_mode_index"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "clock_out_verification_data"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "clock_out_transaction_no"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "clock_out_user_id"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "clock_out_time"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(false, "verification_field"));
        member_clock_table.columns.add(new sdb_model.sdb_table.column(true, "site_id"));

        sdb_model.sdb_table employee_checking_table = new sdb_model.sdb_table("employee_checking_table");

        employee_checking_table.columns.addAll(common_columns);
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(true, "employee_id"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "check_in_time"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "check_in_verification_mode"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "check_in_verification_mode_index"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "check_in_verification_data"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "check_in_transaction_no"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "check_in_user_id"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "check_in_lat"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "check_in_lon"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "check_out_lat"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "check_out_lon"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "check_out_verification_mode"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "check_out_verification_mode_index"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "check_out_verification_data"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "check_out_transaction_no"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "check_out_user_id"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "check_out_time"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(false, "verification_field"));
        employee_checking_table.columns.add(new sdb_model.sdb_table.column(true, "site_id"));


        sdb_model.sdb_table weighbridge_weighment_table = new sdb_model.sdb_table("weighbridge_weighment_table");

        weighbridge_weighment_table.columns.addAll(common_columns);
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(true, "employee_id"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "first_weight_verification_mode"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "first_weight_verification_mode_index"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "first_weight_verification_data"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "first_weight_transaction_no"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "first_weight_user_id"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "first_weight_lat"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "first_weight_lon"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "first_weight"));

        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "second_weight"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "second_weight_lat"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "second_weight_lon"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "second_weight_verification_mode"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "second_weight_verification_mode_index"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "second_weight_verification_data"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "second_weight_transaction_no"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "second_weight_user_id"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "second_weight_time"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "verification_field"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "site_id"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(true, "vehicle_id"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "employee_vehicle_assignment_id"));
        weighbridge_weighment_table.columns.add(new sdb_model.sdb_table.column(false, "vehicle_plate_no"));


        sdb_model.sdb_table employee_vehicle_assignment_table = new sdb_model.sdb_table("employee_vehicle_assignment_table");

        employee_vehicle_assignment_table.columns.addAll(common_columns);
        employee_vehicle_assignment_table.columns.add(new sdb_model.sdb_table.column(true, "employee_id"));
        employee_vehicle_assignment_table.columns.add(new sdb_model.sdb_table.column(true, "vehicle_id"));
        employee_vehicle_assignment_table.columns.add(new sdb_model.sdb_table.column(false, "vehicle_plate_no"));
        employee_vehicle_assignment_table.columns.add(new sdb_model.sdb_table.column(false, "vehicle_type"));


        sdb_model.sdb_table device_room_access_ids_table = new sdb_model.sdb_table("device_room_access_ids_table");

        device_room_access_ids_table.columns.addAll(common_columns);
        device_room_access_ids_table.columns.add(new sdb_model.sdb_table.column(true, "room_id"));
        device_room_access_ids_table.columns.add(new sdb_model.sdb_table.column(false, "room_index"));
        device_room_access_ids_table.columns.add(new sdb_model.sdb_table.column(false, "room_description"));


        sdb_model.sdb_table employee_room_access_table = new sdb_model.sdb_table("employee_room_access_table");

        employee_room_access_table.columns.addAll(common_columns);
        employee_room_access_table.columns.add(new sdb_model.sdb_table.column(true, "employee_id"));
        employee_room_access_table.columns.add(new sdb_model.sdb_table.column(true, "room_id"));
        employee_room_access_table.columns.add(new sdb_model.sdb_table.column(false, "room_description"));


        sdb_model.sdb_table dyna_data_table = new sdb_model.sdb_table("dyna_data_table");

        dyna_data_table.columns.addAll(common_columns);
        dyna_data_table.columns.add(new sdb_model.sdb_table.column(true, "data_type"));
        dyna_data_table.columns.add(new sdb_model.sdb_table.column(false, "data_code"));
        dyna_data_table.columns.add(new sdb_model.sdb_table.column(true, "parent"));
        dyna_data_table.columns.add(new sdb_model.sdb_table.column(false, "data"));


        sdb_model.sdb_table activity_fields_table = new sdb_model.sdb_table("activity_fields_table");

        activity_fields_table.columns.addAll(common_columns);
        activity_fields_table.columns.add(new sdb_model.sdb_table.column(false, "field_name"));
        activity_fields_table.columns.add(new sdb_model.sdb_table.column(true, "field_department_id"));


        sdb_model.sdb_table user_table = new sdb_model.sdb_table("user_table");

        user_table.columns.addAll(common_columns);
        user_table.columns.add(new sdb_model.sdb_table.column(false, "user_fullname"));
        user_table.columns.add(new sdb_model.sdb_table.column(false, "username"));
        user_table.columns.add(new sdb_model.sdb_table.column(false, "password"));


        sdb_model.sdb_table app_versions_table = new sdb_model.sdb_table("app_versions_table");

        app_versions_table.columns.addAll(common_columns);
        app_versions_table.columns.add(new sdb_model.sdb_table.column(false, "version_name"));
        app_versions_table.columns.add(new sdb_model.sdb_table.column(false, "version_code"));
        app_versions_table.columns.add(new sdb_model.sdb_table.column(false, "release_name"));
        app_versions_table.columns.add(new sdb_model.sdb_table.column(false, "release_time"));
        app_versions_table.columns.add(new sdb_model.sdb_table.column(false, "release_notes"));
        app_versions_table.columns.add(new sdb_model.sdb_table.column(false, "creation_time"));
        app_versions_table.columns.add(new sdb_model.sdb_table.column(false, "download_link"));
        app_versions_table.columns.add(new sdb_model.sdb_table.column(false, "file"));


        sdb_model.sdb_table leave_application_table = new sdb_model.sdb_table("leave_application_table");

        leave_application_table.columns.addAll(common_columns);
        leave_application_table.columns.add(new sdb_model.sdb_table.column(true, "employee_id"));
        leave_application_table.columns.add(new sdb_model.sdb_table.column(false, "leave_id"));
        leave_application_table.columns.add(new sdb_model.sdb_table.column(false, "from_time"));
        leave_application_table.columns.add(new sdb_model.sdb_table.column(false, "to_time"));
        leave_application_table.columns.add(new sdb_model.sdb_table.column(false, "note"));


        sdb_model.sdb_table employee_leave_table = new sdb_model.sdb_table("employee_leave_table");

        employee_leave_table.columns.addAll(common_columns);
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(true, "employee_id"));
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(true, "leave_id"));
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(false, "leave_name"));
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(false, "total_days"));
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(false, "accumulated_days"));
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(false, "leave_balance"));
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(false, "deduction_mode"));
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(false, "authenticate"));
        employee_leave_table.columns.add(new sdb_model.sdb_table.column(false, "minimum_prior_days"));


        sdb_model.sdb_table employee_random_calls_table = new sdb_model.sdb_table("employee_random_calls_table");

        employee_random_calls_table.columns.addAll(common_columns);
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(true, "employee_id"));
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(true, "lat_raised"));
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(false, "lon_raised"));
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(false, "time_raised"));
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(false, "time_checked"));
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(false, "lat_checked"));
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(false, "lon_checked"));
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(false, "default_reason"));
        employee_random_calls_table.columns.add(new sdb_model.sdb_table.column(false, "answered"));


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


        main_db = dbm;


        for (sdb_model.sdb_table tb : dbm.tables) {
            try {
                Cursor cursor1 = database.rawQuery("SELECT * FROM " + tb.table_name, null);
                cursor1.moveToFirst();
                if (!cursor1.isAfterLast()) {
                    do {
                        cursor1.getString(0);
                    } while (cursor1.moveToNext());
                }
                cursor1.close();
            } catch (Exception e) {
                database.execSQL(tb.create_statement());
                String crt_stt = tb.create_indexes_statement();
                if (crt_stt.length() > 1 & crt_stt.contains(";")) {

                    for (String st : crt_stt.split(";")) {
                        try {
                            Log.e("DB :", "Index statement creating =>" + st);
                            database.execSQL(st);
                            Log.e("DB :", "Index statement created =>" + st);
                        } catch (Exception ex1) {
                        }

                    }


                }
                continue;
            }

            for (sdb_model.sdb_table.column col : tb.columns) {
                try {
                    Cursor cursor1 = database.rawQuery("SELECT count(" + col.column_name + ") FROM " + tb.table_name, null);
                    cursor1.moveToFirst();
                    if (!cursor1.isAfterLast()) {
                        do {
                            cursor1.getString(0);
                        } while (cursor1.moveToNext());
                    }
                    cursor1.close();
                } catch (Exception e) {
                    database.execSQL("ALTER TABLE " + tb.table_name + " ADD COLUMN " + col.column_name + " " + col.data_type + " " + col.default_value);
                }
            }
        }
        svars.set_version_action_done(act, svars.version_action.DB_CHECK);
        Log.e("DB", "Finished DB Verification");
        main_db = null;
        loaded_db = true;

    }

    /**
     * Sets up database by creating and adding missing tables and missing columns and indeces in their respective tables
     *
     * @deprecated This method is no longer used,its slow compaired to {@link this#setup_db_ann()} which is twice as fast with annotation.
     * <p> Use {@link this#setup_db_ann()} instead.
     */
    @Deprecated()
    void setup_db() throws IOException {
        sdb_model dbm = new sdb_model();
        dbm.db_name = svars.DB_NAME;
        dbm.db_path = svars.current_app_config(act).file_path_db(act);
        //   dbm.db_path=act.getExternalFilesDir(null).getAbsolutePath()+"/"+svars.DB_NAME;
        dbm.db_password = svars.DB_PASS;

        SQLiteDatabase.loadLibs(act);


        //  SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile.getAbsolutePath(), , null);
        database = SQLiteDatabase.openOrCreateDatabase(dbm.db_path, dbm.db_password, null);
        // if(svars.version_action_done(act, svars.version_action.DB_CHECK)){  loaded_db=true;return;}

        svars.set_photo_camera_type(act, svars.image_indexes.profile_photo, 1);

        dynamic_property dpex = new dynamic_property(null, null, null);
        Class<?> cex = dpex.getClass();
        String dpex_path = cex.getName();

        String package_path = dpex_path.substring(0, dpex_path.lastIndexOf('.'));

        String codepath = act.getPackageCodePath();

        DexFile df = new DexFile(codepath);
        Stopwatch sw = new Stopwatch();
        sw.start();
        // List<String> resultList = Lists.newArrayList(Collections.list(df.entries())).stream().filter(s -> s.startsWith(package_path)).collect(Collectors.toList());
//        for (String iter :resultList) {
//            String s = iter;
//            Log.e("Classes reflected 3 =>", "" + s);
//
//        }
//        Log.e("Classes reflected 3 in :", "" + sw.elapsed(TimeUnit.MICROSECONDS));


        sdb_model.sdb_table.column id = new sdb_model.sdb_table.column(false, "id", "INTEGER");
        id.extra_params = "PRIMARY KEY AUTOINCREMENT";
        sdb_model.sdb_table.column reg_time = new sdb_model.sdb_table.column(false, "reg_time", "DATETIME", "(datetime('now','localtime'))");
        sdb_model.sdb_table.column sync_status = new sdb_model.sdb_table.column(false, "sync_status");
        sdb_model.sdb_table.column transaction_no = new sdb_model.sdb_table.column(false, "transaction_no");
        sdb_model.sdb_table.column sid = new sdb_model.sdb_table.column(true, "sid");
        sdb_model.sdb_table.column sync_var = new sdb_model.sdb_table.column(true, "sync_var");
        sdb_model.sdb_table.column data_status = new sdb_model.sdb_table.column(true, "data_status");
        sdb_model.sdb_table.column user_id = new sdb_model.sdb_table.column(false, "user_id");
        sdb_model.sdb_table.column data_usage_frequency = new sdb_model.sdb_table.column(true, "data_usage_frequency");
        sdb_model.sdb_table.column lat = new sdb_model.sdb_table.column(false, "lat");
        sdb_model.sdb_table.column lon = new sdb_model.sdb_table.column(false, "lon");

        ArrayList<sdb_model.sdb_table.column> common_columns = new ArrayList();
        common_columns.add(id);
        common_columns.add(user_id);
        common_columns.add(reg_time);
        common_columns.add(data_status);
        common_columns.add(transaction_no);
        common_columns.add(sync_status);
        common_columns.add(sid);
        common_columns.add(sync_var);
        common_columns.add(data_usage_frequency);


        for (Enumeration<String> iter = df.entries(); iter.hasMoreElements(); ) {
            String s = iter.nextElement();
            if (s.startsWith(package_path)) {
                try {
                    Class<?> clazz = Class.forName(s);


                    db_class db_tb_temp = new db_class("");
                    if (db_tb_temp.getClass().isAssignableFrom(clazz) && !db_tb_temp.getClass().getName().equalsIgnoreCase(clazz.getName())) {
                        Log.e("Classes reflected =>", "DB :" + s);
                        sdb_model.sdb_table db_tb = table_from_dyna_property_class(clazz);
                        if (db_tb == null) {
                            continue;
                        }
                        db_tb.columns.addAll(common_columns);
                        //   dbm.tables.add(db_tb);
                        try {
                            Cursor cursor1 = database.rawQuery("SELECT * FROM " + db_tb.table_name, null);
                            cursor1.moveToFirst();
                            if (!cursor1.isAfterLast()) {
                                do {
                                    cursor1.getString(0);
                                } while (cursor1.moveToNext());
                            }
                            cursor1.close();
                        } catch (Exception e) {
                            database.execSQL(db_tb.create_statement());
                            String crt_stt = db_tb.create_indexes_statement();
                            if (crt_stt.length() > 1 & crt_stt.contains(";")) {

                                for (String st : crt_stt.split(";")) {
                                    try {
                                        Log.e("DB :", "Index statement creating =>" + st);
                                        database.execSQL(st);
                                        Log.e("DB :", "Index statement created =>" + st);
                                    } catch (Exception ex1) {
                                    }

                                }


                            }
                            continue;
                        }

                        for (sdb_model.sdb_table.column col : db_tb.columns) {
                            try {
                                Cursor cursor1 = database.rawQuery("SELECT count(" + col.column_name + ") FROM " + db_tb.table_name, null);
                                cursor1.moveToFirst();
                                if (!cursor1.isAfterLast()) {
                                    do {
                                        cursor1.getString(0);
                                    } while (cursor1.moveToNext());
                                }
                                cursor1.close();
                            } catch (Exception e) {
                                database.execSQL("ALTER TABLE " + db_tb.table_name + " ADD COLUMN " + col.column_name + " " + col.data_type + " " + col.default_value);
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
        Log.e("DB", "Finished DB Verification");
        main_db = null;
        loaded_db = true;

    }

    /**
     * Sets up database by creating and adding missing tables and missing columns and indices in their respective tables from mappery of annotated data which is pre-reflected at pre build
     */
    void setup_db_ann() {


        sdb_model dbm = new sdb_model();
        AppConfig appconfig = svars.current_app_config(act);
        dbm.db_name = appconfig.DB_NAME;
        dbm.db_path = appconfig.file_path_db();
        dbm.db_password = appconfig.DB_PASS;

        SQLiteDatabase.loadLibs(act);

        File par = new File(new File(dbm.db_path).getParent());
        if (!par.exists()) {
            par.mkdirs();
        }
        Log.e(logTag, "DB Path:" + dbm.db_path);

        database = SQLiteDatabase.openOrCreateDatabase(new File(dbm.db_path), dbm.db_password, null);

        // if(svars.version_action_done(act, svars.version_action.DB_CHECK)){  loaded_db=true;return;}

        svars.set_photo_camera_type(act, svars.image_indexes.profile_photo, 1);


        Stopwatch sw = new Stopwatch();
        sw.start();


        for (String s : realm.getDynamicClassPaths()) {


            Log.e("Classes reflected =>", "Ann :" + s);

            String table_name = realm.getPackageTable(s);
            try {
                Cursor cursor1 = database.rawQuery("SELECT * FROM " + table_name, null);
//                cursor1.moveToFirst();
//                if (!cursor1.isAfterLast()) {
//                    do {
//                        cursor1.getString(0);
//                    } while (cursor1.moveToNext());
//                }
                cursor1.close();
            } catch (Exception e) {
                database.execSQL(realm.getTableCreateSttment(table_name, false));
                database.execSQL(realm.getTableCreateSttment(table_name, true));
                String crt_stt = realm.getTableCreateIndexSttment(table_name);
                if (crt_stt.length() > 1 & crt_stt.contains(";")) {

                    for (String st : crt_stt.split(";")) {
                        try {
                            Log.e("DB :", "Index statement creating =>" + st);
                            database.execSQL(st);
                            Log.e("DB :", "Index statement created =>" + st);
                        } catch (Exception ex1) {
                        }

                    }


                }
                continue;
            }

            for (Map.Entry<String, String> col : realm.getTableColumns(table_name).entrySet()) {
                try {
                    Cursor cursor1 = database.rawQuery("SELECT count(" + col.getKey() + ") FROM " + table_name, null);
//                    cursor1.moveToFirst();
//                    if (!cursor1.isAfterLast()) {
//                        do {
//                            cursor1.getString(0);
//                        } while (cursor1.moveToNext());
//                    }
                    cursor1.close();
                } catch (Exception e) {
                    database.execSQL("ALTER TABLE " + table_name + " ADD COLUMN " + col.getKey());
//                                database.execSQL("ALTER TABLE "+db_tb.table_name+" ADD COLUMN " + col.getKey() + " "+col.data_type+" "+col.default_value);
                }
            }


            for (Map.Entry<String, String> col : realm.getTableColumns(table_name).entrySet()) {
                try {
                    Cursor cursor1 = database.rawQuery("SELECT count(" + col.getKey() + ") FROM CP_" + table_name, null);
//                    cursor1.moveToFirst();
//                    if (!cursor1.isAfterLast()) {
//                        do {
//                            cursor1.getString(0);
//                        } while (cursor1.moveToNext());
//                    }
                    cursor1.close();
                } catch (Exception e) {
                    database.execSQL("ALTER TABLE CP_" + table_name + " ADD COLUMN " + col.getKey());
//                                database.execSQL("ALTER TABLE "+db_tb.table_name+" ADD COLUMN " + col.getKey() + " "+col.data_type+" "+col.default_value);
                }
            }


        }

        Log.e("Classes reflected :", "Ann :" + sw.elapsed(TimeUnit.MICROSECONDS));

        svars.set_version_action_done(act, svars.version_action.DB_CHECK);
        Log.e("DB", "Finished DB Verification");
        main_db = null;
        loaded_db = true;

    }
/*
I thot of using an interface ,dint work
 */


    sdb_model.sdb_table table_from_dyna_property_class(Class<?> main_class) {

        Object main_obj = null;
        sdb_model.sdb_table tabl = null;//new sdb_model.sdb_table();
        try {
            main_obj = main_class.newInstance();
        } catch (IllegalAccessException e) {
            return null;

        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        try {
            Field ff = main_class.getField("table_name");
            ff.setAccessible(true);
            try {
                String table_name_ = (String) ff.get(main_class.newInstance());
                Log.e("TABLE CLASS ", "TABLE :" + table_name_);

                tabl = new sdb_model.sdb_table(table_name_);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            // e.printStackTrace();
        }
        if (tabl == null || tabl.table_name == null || tabl.table_name.length() < 1) {
            return null;
        }
//Field[] fields =concatenate(main_class.getSuperclass().getFields(),main_class.getDeclaredFields());
        for (Field field : main_class.getDeclaredFields()) {
            field.setAccessible(true); // if you want to modify private fields
            if (field.getType() == dynamic_property.class) {
                try {

                    Log.e("DP CLASS ", "" + field.getName());
                    Class<?> clazz = field.get(main_obj).getClass();
                    Field dyna_column_name_field = clazz.getDeclaredField("column_name");
                    Field dyna_index_field = clazz.getDeclaredField("index");
                    dyna_column_name_field.setAccessible(true);
//                  sdb_model.sdb_table.column col=new sdb_model.sdb_table.column(true,(String) dyna_column_name_field.get(field.get(main_obj)));

                    tabl.columns.add(new sdb_model.sdb_table.column((boolean) dyna_index_field.get(field.get(main_obj)), (String) dyna_column_name_field.get(field.get(main_obj))));


                } catch (Exception ex) {
                    Log.e("REFLECTION ERROR =>", "" + ex.getMessage());
                }
            } else {
//                try {
//                    Log.e("CLASS ", field.getName()+ " - " + field.getType());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }
        return tabl;
    }

    sdb_model.sdb_table table_from_dyna_property_class_ann(Class<?> main_class) {

        Object main_obj = null;
        sdb_model.sdb_table tabl = null;//new sdb_model.sdb_table();
        try {
            main_obj = main_class.newInstance();
        } catch (IllegalAccessException e) {
            return null;

        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        try {
            Field ff = main_class.getField("table_name");
            ff.setAccessible(true);
            try {
                String table_name_ = (String) ff.get(main_class.newInstance());
                Log.e("TABLE CLASS ", "TABLE :" + table_name_);

                tabl = new sdb_model.sdb_table(table_name_);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            // e.printStackTrace();
        }
        if (tabl == null || tabl.table_name == null || tabl.table_name.length() < 1) {
            return null;
        }
//Field[] fields =concatenate(main_class.getSuperclass().getFields(),main_class.getDeclaredFields());
        for (Field field : main_class.getDeclaredFields()) {
            field.setAccessible(true); // if you want to modify private fields
            if (field.getType() == dynamic_property.class) {
                try {

                    Log.e("DP CLASS ", "" + field.getName());
                    Class<?> clazz = field.get(main_obj).getClass();
                    Field dyna_column_name_field = clazz.getDeclaredField("column_name");
                    Field dyna_index_field = clazz.getDeclaredField("index");
                    dyna_column_name_field.setAccessible(true);
//                  sdb_model.sdb_table.column col=new sdb_model.sdb_table.column(true,(String) dyna_column_name_field.get(field.get(main_obj)));

                    tabl.columns.add(new sdb_model.sdb_table.column((boolean) dyna_index_field.get(field.get(main_obj)), (String) dyna_column_name_field.get(field.get(main_obj))));


                } catch (Exception ex) {
                    Log.e("REFLECTION ERROR =>", "" + ex.getMessage());
                }
            } else {
//                try {
//                    Log.e("CLASS ", field.getName()+ " - " + field.getType());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }
        return tabl;
    }


/////////////////////////////////////////////UPDATE //////////////////////////

    public long update_check_period() {
        //Date date = svars.sparta_EA_calendar().getTime();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
        String dttt = format1.format(date);


        if (svars.update_check_time(act) == null) {
            return svars.regsyncinterval_mins;
        }
        Date time1 = null;
        try {
            try {
                time1 = new SimpleDateFormat("HH:mm:ss").parse(format1.format(date));
            } catch (Exception ex) {
                Log.e("Time Error =>", ex.getMessage());
            }
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);
            Date time2 = null;
            try {
                Log.e("DATE 2 =>", " t " + date.getTime() + " Time=>" + svars.update_check_time(act).split(" ")[1]);
                time2 = new SimpleDateFormat("HH:mm:ss").parse(svars.update_check_time(act).split(" ")[1]);
            } catch (Exception ex) {
                Log.e("Time Error =>", ex.getMessage());
            }
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            long diffference = Calendar.getInstance().getTimeInMillis() - calendar2.getTimeInMillis();
            // return (int)Math.round((double)diffference/60000);
            int diff_1 = (int) ((diffference / (1000 * 60)) % 60);
            return diff_1 + (((int) ((diffference / (1000 * 60 * 60)) % 24)) * 60);
        } catch (Exception ex) {
            return svars.regsyncinterval_mins;
        }


    }

    public ArrayList<sparta_app_version> load_undownloaded_apks(String[] downloading) {


        ArrayList<sparta_app_version> versions = new ArrayList<>();


        Cursor c = database.rawQuery("SELECT * FROM app_versions_table WHERE data_status IS NULL AND version_name > '" + svars.current_version() + "' AND sid NOT IN (" + conccat_sql_string(downloading) + ")", null);

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

    public sparta_app_version load_latest_apk_to_install() {

        Cursor c = database.rawQuery("SELECT * FROM app_versions_table WHERE data_status IS NOT NULL AND version_name > '" + svars.current_version() + "' ORDER BY sid DESC LIMIT 1", null);

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

            if (!version_exists(json_sav.getString("Version_id"))) {
                ContentValues cv = new ContentValues();

                cv.put("sid", json_sav.getString("Version_id"));
                cv.put("version_name", json_sav.getString("Version_name"));
                cv.put("release_notes", json_sav.getString("Release_notes"));
                cv.put("release_name", json_sav.getString("Release_name"));
                cv.put("creation_time", json_sav.getString("creation_time"));
                cv.put("download_link", json_sav.getString("Landing_page_url"));
//cv.put("sid",appcateg_j.getString("icon"));


                Log.e("Apk version ", "About to insert ");

                //;
                Log.e("Apk version ", "Inserted " + database.insert("app_versions_table", null, cv));

            } else {

                Log.e("Apk version ", "App exists ");

            }
        } catch (Exception exx) {
            Log.e("Version insert error :", " " + exx.getMessage());
        }

    }

    public void update_downloaded_versions(String sid, String file_path) {
        try {


            ContentValues cv = new ContentValues();

            cv.put("data_status", "d");
            cv.put("local_path", file_path);


            Log.e("Apk version ", "About to update ");

            //;
            Log.e("Apk version ", "Inserted " + database.update("app_versions_table", cv, "sid='" + sid + "'", null));


        } catch (Exception exx) {
            Log.e("Version update error :", " " + exx.getMessage());
        }

    }

    boolean version_exists(String sid) {
        return database.rawQuery("SELECT _id FROM app_versions_table WHERE sid='" + sid + "'", null).moveToFirst();
    }

//////////////////////////////////////////////////////////////////////////////


    public member load_employee(String sid) {


        try {

            Cursor c = database.rawQuery("SELECT * FROM member_info_table WHERE sid='" + sid + "'", null);

            if (c.moveToFirst()) {
                do {
                    member emp = (member) load_object_from_Cursor(c, new member());
                    c.close();
                    return emp;
                } while (c.moveToNext());
            }

            c.close();
        } catch (Exception ex) {

        }
        return null;
    }


///////////////////////////////USER///////////////////////////////////////////

    public String validate_credentials(String username, String pass) {
        String name = null;
        Cursor c = database.rawQuery("SELECT * FROM user_table WHERE username=\"" + username + "\" AND password=\"" + pass + "\"", null);

        if (c.moveToFirst()) {
            do {

                name = c.getString(c.getColumnIndex("user_fullname"));
                SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

                saver.putString("user_name", c.getString(c.getColumnIndex("user_fullname")));
                saver.putString("username", c.getString(c.getColumnIndex("username")));
                saver.putString("pass", c.getString(c.getColumnIndex("password")));
                saver.putString("user_id", c.getString(c.getColumnIndex("sid")));

                saver.commit();
                return name == null ? username : name;

            } while (c.moveToNext());
        }
        c.close();
        return name;
    }


    public void save_user(String username, String password, String user_id, String user_name, String user_type_id) {

        /*
          {"$id":"1","Result":{"$id":"2","IsOkay":true,"Message":"login successfull","Result":{"$id":"3","user_id":18,"username":"00076","password":null,"full_name":"KOUASSI","account_id":1,"user_type_id":3,"status":1}},"ModuleName":"Login Module"}

         */
        ContentValues cv = new ContentValues();

        try {

            try {

                SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

                saver.putString("user_name", user_name);
                saver.putString("username", username);
                saver.putString("pass", password);
                saver.putString("user_id", user_id);
                saver.putString("user_type", user_type_id);


                saver.commit();
            } catch (Exception ex) {

            }
            // cv.put("account_id",jo.getString("account_id"));
            cv.put("sid", user_id);
            cv.put("username", username);
            cv.put("password", password);


            //   database.execSQL("DELETE FROM user_table WHERE sid='"+user_id+"'");
            database.execSQL("DELETE FROM user_table");
            database.insert("user_table", null, cv);
            //   validate_credentials(username,password);

        } catch (Exception e) {
            Log.e("User saving error =>", "" + e.getMessage());

            e.printStackTrace();
        }

    }


    public void register_user(String users_name, String username, String password, String user_id, String activity_status) {

        /*
          {"$id":"1","Result":{"$id":"2","IsOkay":true,"Message":"login successfull","Result":{"$id":"3","user_id":18,"username":"00076","password":null,"full_name":"KOUASSI","account_id":1,"user_type_id":3,"status":1}},"ModuleName":"Login Module"}

         */
        Log.e("Password  :", "" + password);
        ContentValues cv = new ContentValues();

        try {


            // cv.put("account_id",jo.getString("account_id"));
            cv.put("sid", user_id);
            cv.put("username", username);
            cv.put("user_fullname", users_name);
            cv.put("password", password);
            cv.put("data_status", activity_status);


            // database.execSQL("DELETE FROM user_table");
            Log.e("User saving ", "" + database.insert("user_table", null, cv));


        } catch (Exception e) {
            Log.e("User saving error =>", "" + e.getMessage());

            e.printStackTrace();
        }

    }

    public void logout_user() {
        ContentValues cv = new ContentValues();

        try {

            try {

                SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();
                saver.putString("user_name", "");
                saver.putString("user_type", "");
                saver.putString("username", "");
                saver.putString("pass", "");
                saver.putString("user_id", "");
                saver.putString("zone", "");


                saver.commit();
            } catch (Exception ex) {

            }


            database.execSQL("DELETE FROM user_table");

        } catch (Exception e) {
            Log.e("User logout error =>", "" + e.getMessage());

            e.printStackTrace();
        }

    }


//////////////////////////////////////////////////////////////////////////////////////////////////


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

    public ArrayList<Object> load_dynamic_records(Object obj, String[] table_filters) {
        ArrayList<Object> objs = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM " + ((db_class) obj).table_name + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)), null);


        if (c.moveToFirst()) {
            do {


                objs.add(load_object_from_Cursor(c, obj));
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();


        return objs;
    }

    public ArrayList<Object> load_dynamic_records(Object obj, int limit, String[] table_filters) {
        ArrayList<Object> objs = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM " + ((db_class) obj).table_name + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + " ORDER BY data_status DESC LIMIT " + limit, null);


        if (c.moveToFirst()) {
            do {


                objs.add(load_object_from_Cursor(c, obj));
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();


        return objs;
    }

    public ArrayList<Object> load_dynamic_records(sync_service_description ssd, String[] table_filters) {
        ArrayList<Object> objs = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM " + ssd.table_name + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + " ORDER BY data_status DESC LIMIT " + ssd.chunk_size, null);


        if (c.moveToFirst()) {
            do {


                try {
                    objs.add(load_object_from_Cursor(c, Class.forName(ssd.object_package).newInstance()));
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


    ////////////////////////////////////////ANN///////////////////////////////////////////////////////////////////
    public <RM> int getRecordCount(Class<RM> realm_model, @Nullable String... params) {
        String table_name = realm.getPackageTable(realm_model.getName());
        return Integer.parseInt(get_record_count(table_name, params));
    }

    public <RM> ArrayList<RM> loadObjectArray(Class<RM> realm_model, String[] columns, String[] table_filters, String[] order_filters, boolean order_asc, int limit, int offset) {
        ArrayList<RM> objs = new ArrayList<RM>();
        String table_name = realm.getPackageTable(realm_model.getName());
        String qry = "SELECT " + (columns == null ? "*" : concatString(",", columns)) + " FROM " + table_name + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + (order_filters == null ? "" : " ORDER BY " + concatString(",", order_filters) + " " + (order_asc ? "ASC" : "DESC")) + (limit <= 0 ? "" : " LIMIT " + limit + (offset <= 0 ? "" : " OFFSET " + offset));
        Cursor c = database.rawQuery(qry, null);


        if (c.moveToFirst()) {
            do {


                try {

                    objs.add((RM) realm.getObjectFromCursor(c, realm_model.getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();


        return objs;
    }


    public static HashMap<Integer, Integer> pagerEventMap = new HashMap<>();

    public <RM> ArrayList<RM> loadObjectArray(Class<RM> realm_model, int pagerEventId, int searchIndex, String[] columns, String[] table_filters, String[] order_filters, boolean order_asc, int limit, int offset) {
        ArrayList<RM> objs = new ArrayList<RM>();
        String table_name = realm.getPackageTable(realm_model.getName());
        String qry = "SELECT " + (columns == null ? "*" : concatString(",", columns)) + " FROM " + table_name + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + (order_filters == null ? "" : " ORDER BY " + concatString(",", order_filters) + " " + (order_asc ? "ASC" : "DESC")) + (limit <= 0 ? "" : " LIMIT " + limit + (offset <= 0 ? "" : " OFFSET " + offset));
        Cursor c = database.rawQuery(qry, null);


        if (c.moveToFirst()) {
            do {
                objs.add((RM) realm.getObjectFromCursor(c, realm_model.getName()));
            } while (c.moveToNext() && pagerEventMap.get(pagerEventId) == searchIndex);
        }
        c.close();


        return pagerEventMap.get(pagerEventId) == searchIndex ? objs : null;
//        return currentActiveIndex(pagerEventId)==searchIndex?objs:null;
    }

    public <RM> ArrayList<RM> loadObjectArray(Class<RM> realm_model, String rawquery) {
        ArrayList<RM> objs = new ArrayList<RM>();
//        String table_name=realm.getPackageTable(realm_model.getName());
        String qry = rawquery;//"SELECT "+(columns==null?"*":concatString(",",columns))+" FROM "+table_name+(table_filters==null?"":" "+conccat_sql_filters(table_filters))+(order_filters==null?"":" ORDER BY "+concatString(",",order_filters)+" "+(order_asc?"ASC":"DESC"))+(limit<=0?"":" LIMIT "+limit+(offset<=0?"": " OFFSET "+offset));
        Cursor c = database.rawQuery(qry, null);


        if (c.moveToFirst()) {
            do {


                try {

                    objs.add((RM) realm.getObjectFromCursor(c, realm_model.getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } while (c.moveToNext());
        }
        c.close();


        return objs;
    }

    public <RM> ArrayList<RM> loadObjectArray(Class<RM> realm_model, Query query) {
        return loadObjectArray(realm_model, query.columns, query.table_filters, query.order_filters, query.order_asc, query.limit, query.offset);

    }

    public <RM> ArrayList<RM> loadObjectArray(Class<RM> realm_model, int pagerEventId, int searchIndex, Query query) {
        return loadObjectArray(realm_model, pagerEventId, searchIndex, query.columns, query.table_filters, query.order_filters, query.order_asc, query.limit, query.offset);

    }

    public <RM> RM loadObject(Class<RM> realm_model, Query query) {
//        Query q=new Query().setColumns("").setLimit(9).setLimit(0);
        ArrayList<RM> res = loadObjectArray(realm_model, query.columns, query.table_filters, query.order_filters, query.order_asc, 1, 0);
        return res.size() > 0 ? res.get(0) : null;

    }

    public <RM> RM loadObject(Class<RM> realm_model, String rawquery) {
        ArrayList<RM> res = loadObjectArray(realm_model, rawquery);
        if (res.size() > 0) {
            Log.e(logTag, "Array size :" + res.size());
        }
        return res.size() > 0 ? res.get(0) : null;

    }

    public <RM> boolean insertObject(RM realm_model) {
        String table_name = realm.getPackageTable(realm_model.getClass().getName());

        return database.insert(table_name, null, (ContentValues) realm.getContentValuesFromObject(realm_model)) > 0;

    }

    public Object getJsonValue(String pos, JSONObject jo) {
        Object json = jo;
        if (!pos.contains(":")) {
            return null;
        }
        if (!pos.contains(";")) {
            pos += ";";
        }
        for (String s : pos.split(";")) {
            if (s.length() > 0) {
                try {
//            if(json instanceof JSONObject){
                    if (s.split(":")[0].equalsIgnoreCase("JO")) {
                        json = new JSONTokener(((JSONObject) json).opt(s.split(":")[1]).toString()).nextValue();

                    } else if (s.split(":")[0].equalsIgnoreCase("JA")) {

//                if (json instanceof JSONArray){

                        json = ((JSONArray) json).get(Integer.parseInt(s.split(":")[1]));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        return json;
    }


    /*
     *
     * Loads obects from cursor
     *
     *
     */
    public ArrayList<Object> load_dynamic_records_ann(sync_service_description ssd, String[] table_filters) {
        ArrayList<Object> objs = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM " + ssd.table_name + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + " ORDER BY data_status DESC LIMIT " + ssd.chunk_size, null);


        if (c.moveToFirst()) {
            do {


                try {

                    objs.add(realm.getObjectFromCursor(c, ssd.object_package));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();


        return objs;
    }


    public ArrayList<JSONObject> load_dynamic_json_records_ann(sync_service_description ssd, String[] table_filters) {
        ArrayList<JSONObject> objs = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM " + ssd.table_name + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + " ORDER BY data_status DESC LIMIT " + ssd.chunk_size, null);


        if (c.moveToFirst()) {
            do {


                try {

                    objs.add(realm.getJsonFromCursor(c, ssd.object_package));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();


        return objs;
    }

    public JSONArray loadJsonArray_Ann(sync_service_description ssd, String[] table_filters) {
        JSONArray objs = new JSONArray();

        Cursor c = database.rawQuery("SELECT * FROM " + ssd.table_name + (table_filters == null ? "" : " " + conccat_sql_filters(table_filters)) + " ORDER BY data_status DESC LIMIT " + ssd.chunk_size, null);


        if (c.moveToFirst()) {
            do {


                try {

                    objs.put(realm.getJsonFromCursor(c, ssd.object_package));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // objs.add(load_object_from_Cursor(c,deepClone(obj)));


            } while (c.moveToNext());
        }
        c.close();


        return objs;
    }


    public void register_object(Boolean first_record, JSONObject j_obj, Object primary_obj, String service_name) {

        if (first_record != null && first_record) {
            database.beginTransaction();
            Log.e(service_name + "::Insertion =>", "transaction begun");

            return;
        } else if (first_record != null && first_record == false) {
            Log.e(service_name + "::Insertion =>", "transaction complete");

            database.setTransactionSuccessful();
            database.endTransaction();
            return;
        }
        try {
            try {
                String qry = "DELETE FROM " + ((db_class) primary_obj).table_name + " WHERE (sid='" + j_obj.getString(((db_class) primary_obj).sid.json_name) + "' OR sid=" + j_obj.getString(((db_class) primary_obj).sid.json_name) + ") AND sync_status='i'";
//Log.e("Query :",""+qry);
//Log.e("JO :",""+jempl);
                database.execSQL(qry);
            } catch (Exception ex) {

                Log.e("DELETING ERROR =>", "" + ex.getMessage());
            }
            String is_active_key = ((db_class) primary_obj).data_status.json_name;
            if ((j_obj.has(is_active_key) && j_obj.getBoolean(((db_class) primary_obj).data_status.json_name)) || !j_obj.has(is_active_key)) {

                ContentValues cv = load_object_cv_from_JSON(j_obj, primary_obj);

                Log.e(service_name + ":: Insert result =>", " " + database.insert(((db_class) primary_obj).table_name, null, cv));

            } else {
            }


        } catch (Exception ex) {
            Log.e("insert error", "" + ex.getMessage());
        }


    }

    public void register_object_auto_ann(Boolean first_record, JSONObject j_obj, sync_service_description ssd) {

        if (first_record != null && first_record) {
            database.beginTransaction();
            Log.e(ssd.service_name + "::Insertion =>", "transaction begun");

            return;
        } else if (first_record != null && first_record == false) {
            Log.e(ssd.service_name + "::Insertion =>", "transaction complete");

            database.setTransactionSuccessful();
            database.endTransaction();
            return;
        }
        try {
            try {
                if (ssd.use_download_filter) {
                    database.execSQL(realm.getDeleteRecordSttment(ssd.table_name, j_obj.getString("id")));
                }

            } catch (Exception ex) {

                Log.e("DELETING ERROR =>", "" + ex.getMessage());
            }
            if (realm.jsonHasActiveKey(j_obj)) {

                ContentValues cv = (ContentValues) realm.getContentValuesFromJson(j_obj, ssd.table_name);
                cv.put("sync_status", sync_status.syned.ordinal());

                Log.e(ssd.service_name + ":: Insert result =>", " " + database.insert(ssd.table_name, null, cv));
                if (ssd.service_name.equalsIgnoreCase("JobAllInventory")) {
                    Log.e("Timming error :", ssd.service_name + "::" + cv.toString());
                }
            }


        } catch (Exception ex) {
            Log.e("insert error", "" + ex.getMessage());
        }


    }

    public ArrayList<dyna_data> load_dyna_data_annot(dyna_data_obj.operatio_data_type op_dyna_type, String parent) {
        String dyna_type = op_dyna_type.ordinal() + "";
        ArrayList<dyna_data> objs = new ArrayList<>();

        // Cursor c=database.rawQuery("SELECT * FROM dyna_data_table WHERE data_type='"+dyna_type+"'"+(parent==null?" LIMIT 1000":" AND parent='"+parent+"' LIMIT 1000"),null);
        Cursor c;
        if (dyna_type.equalsIgnoreCase("orgs") && parent != null && parent.equalsIgnoreCase("11")) {
            c = database.rawQuery("SELECT * FROM dyna_data_table WHERE (data_code LIKE 'UPR%' OR data_code LIKE 'UPU%') AND data_type='" + dyna_type + "'", null);

        } else {

            c = database.rawQuery("SELECT * FROM dyna_data_table WHERE data_type='" + dyna_type + "'" + (parent == null ? "" : " AND parent='" + parent + "'"), null);

        }

        if (c.moveToFirst()) {
            do {


                //  dyna_data obj=new dyna_data(c.getString(c.getColumnIndex("id")),c.getString(c.getColumnIndex("sid")),"",c.getString(c.getColumnIndex("data_type")),c.getString(c.getColumnIndex("data")),"",c.getString(c.getColumnIndex("id")));
                dyna_data obj = (dyna_data) load_object_from_Cursor_annot(c, new dyna_data());


                objs.add(obj);

            } while (c.moveToNext());
        }
        c.close();
        return objs;
    }

    public ArrayList<dyna_data_obj> load_dyna_data_from_parent_of_parent(String dyna_type, String parent_type, String parent_parent) {
        ArrayList<dyna_data_obj> objs = new ArrayList<>();


        Cursor c;

        c = database.rawQuery("SELECT * FROM dyna_data_table WHERE data_type='" + dyna_type + "' AND parent IN (SELECT sid FROM dyna_data_table WHERE data_type='" + parent_type + "' AND parent ='" + parent_parent + "')", null);

        if (c.moveToFirst()) {
            do {
//public dyna_data_obj(String lid,String sid, String name, String data_type, String data, String parent)

//                dyna_data_obj obj=new dyna_data_obj(c.getInt(c.getColumnIndex("id"))+"",c.getString(c.getColumnIndex("sid")),c.getString(c.getColumnIndex("data")),c.getString(c.getColumnIndex("data_type")),c.getString(c.getColumnIndex("data")),c.getString(c.getColumnIndex("parent")),c.getString(c.getColumnIndex("data_code")));
//
//              try{obj.code=c.getString(c.getColumnIndex("data_code"));}catch (Exception ex){}
//              try{obj.data_2=c.getString(c.getColumnIndex("data_2"));}catch (Exception ex){}
                dyna_data_obj obj = (dyna_data_obj) load_object_from_Cursor(c, new dyna_data_obj());

                objs.add(obj);

            } while (c.moveToNext());
        }
        c.close();
        return objs;
    }


    public void register_dynadata(Boolean first_record, JSONObject dyna_obj, String dyna_type, String parent) {

        if (first_record != null && first_record) {
            database.beginTransaction();
            Log.e("Dynadata Starting =>", "transaction begun");


        }

        try {


            ContentValues cv = new ContentValues();


            cv.put("sid", dyna_obj.getString("Id"));
            cv.put("data_type", dyna_type);
            cv.put("data", dyna_obj.getString("Name"));
            try {
                cv.put("code", dyna_obj.getString("Code").equalsIgnoreCase("null") ? null : dyna_obj.getString("Code"));
            } catch (Exception ex) {
            }

            try {
                cv.put("parent", dyna_obj.getString("MotherId"));
            } catch (Exception ex) {

            }
            try {
                cv.put("parent", dyna_obj.getString("CommuneId"));
            } catch (Exception ex) {

            }
            // cv.put("data_code",dyna_obj.getString("Code"));


            database.insert("dyna_data_table", null, cv);
            Log.d("Dynadata inserted =>", "" + dyna_obj.toString());


        } catch (Exception ex) {
            Log.e("Dynadata insert error", "" + ex.getMessage());
        }
        if (first_record != null && first_record == false) {
            Log.e("Dynadata ENDING =>", "transaction complete");

            database.setTransactionSuccessful();
            database.endTransaction();

        }
    }


    public ArrayList<dyna_data_obj> load_dyna_data(String dyna_type, String parent) {
        ArrayList<dyna_data_obj> objs = new ArrayList<>();

        // Cursor c=database.rawQuery("SELECT * FROM dyna_data_table WHERE data_type='"+dyna_type+"'"+(parent==null?" LIMIT 1000":" AND parent='"+parent+"' LIMIT 1000"),null);
        Cursor c;
        if (dyna_type.equalsIgnoreCase("orgs") && parent != null && parent.equalsIgnoreCase("11")) {
            c = database.rawQuery("SELECT * FROM dyna_data_table WHERE (data_code LIKE 'UPR%' OR data_code LIKE 'UPU%') AND data_type='" + dyna_type + "'", null);

        } else {
            c = database.rawQuery("SELECT * FROM dyna_data_table WHERE data_type='" + dyna_type + "'" + (parent == null ? "" : " AND parent='" + parent + "'"), null);

        }

        if (c.moveToFirst()) {
            do {


                dyna_data_obj obj = (dyna_data_obj) load_object_from_Cursor(c, new dyna_data_obj());


                objs.add(obj);

            } while (c.moveToNext());
        }
        c.close();
        return objs;
    }

    public ArrayList<dyna_data_obj> load_dyna_data(dyna_data_obj.operatio_data_type op_dyna_type, String parent) {
        String dyna_type = op_dyna_type.ordinal() + "";
        ArrayList<dyna_data_obj> objs = new ArrayList<>();

        // Cursor c=database.rawQuery("SELECT * FROM dyna_data_table WHERE data_type='"+dyna_type+"'"+(parent==null?" LIMIT 1000":" AND parent='"+parent+"' LIMIT 1000"),null);
        Cursor c;
        if (dyna_type.equalsIgnoreCase("orgs") && parent != null && parent.equalsIgnoreCase("11")) {
            c = database.rawQuery("SELECT * FROM dyna_data_table WHERE (data_code LIKE 'UPR%' OR data_code LIKE 'UPU%') AND data_type='" + dyna_type + "'", null);

        } else {
            c = database.rawQuery("SELECT * FROM dyna_data_table WHERE data_type='" + dyna_type + "'" + (parent == null ? "" : " AND parent='" + parent + "'"), null);

        }

        if (c.moveToFirst()) {
            do {


                dyna_data_obj obj = (dyna_data_obj) load_object_from_Cursor(c, new dyna_data_obj());


                objs.add(obj);

            } while (c.moveToNext());
        }
        c.close();
        return objs;
    }

    public interface data_loading_interface {
        void onDataLoaded(ArrayList x);

        void onDataLoading(int percent, ArrayList x);

    }


    public String load_dynadata_name(String sid, String data_type) {
        Cursor c = database.rawQuery("SELECT data FROM dyna_data_table WHERE data_type='" + data_type + "' AND sid='" + sid + "'", null);

        if (c.moveToFirst()) {
            do {
                String res = c.getString(c.getColumnIndex("data"));
                c.close();
                return res;

            } while (c.moveToNext());
        }
        c.close();
        return act.getString(R.string.unavailable_field);
    }

    public String load_dyna_data_name(String sid, dyna_data_obj.operatio_data_type ot) {
        try {
            Cursor c = database.rawQuery("SELECT data FROM dyna_data_table WHERE data_type='" + ot.ordinal() + "' AND sid='" + sid + "'", null);


            if (c.moveToFirst()) {
                do {

                    return c.getString(c.getColumnIndex("data"));

                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception ex) {
        }
        return act.getString(R.string.unavailable_field);
    }


    public JSONObject load_JSON_from_object(Object obj) {


        //employee mem=new employee();
        JSONObject jo = new JSONObject();

        Field[] fieldz = concatenate(obj.getClass().getDeclaredFields(), obj.getClass().getSuperclass().getDeclaredFields());
        for (Field field : fieldz) {
            field.setAccessible(true); // if you want to modify private fields
            if (field.getType() == dynamic_property.class) {
                try {

                    //  Log.e("DB Field ", "" + field.getName());
                    Class<?> clazz = field.get(obj).getClass();
                    Field dyna_value_field = clazz.getDeclaredField("value");
                    Field dyna_json_name_field = clazz.getDeclaredField("json_name");
                    Field dyna_storage_mode_field = clazz.getDeclaredField("storage_mode");

                    dyna_json_name_field.setAccessible(true);
                    dyna_value_field.setAccessible(true);
                    dyna_storage_mode_field.setAccessible(true);

                    String j_key = (String) dyna_json_name_field.get(field.get(obj));

                    if (j_key != null) {
                        int storage_mode = (int) dyna_storage_mode_field.get(field.get(obj));
                        if (storage_mode == 2) {
                            String data = get_saved_doc_base64((String) dyna_value_field.get(field.get(obj)));
                            if (data == error_return) {
                                Log.e("DATA ERROR =>", "  :: " + data);

                                //   cv.put("data_status","e");
                                jo.put(j_key, data);
                            } else {
                                jo.put(j_key, data);

                            }

                        } else {
                            jo.put(j_key, (String) dyna_value_field.get(field.get(obj)));
                        }
                    }


                } catch (Exception ex) {
                    Log.e("REFLECTION ERROR =>", "load_JSON_from_object :: " + ex.getMessage());
                }
            } else {
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


        member mem = new member();


        Field[] fieldz = concatenate(mem.getClass().getDeclaredFields(), mem.getClass().getSuperclass().getDeclaredFields());
        for (Field field : fieldz) {
            field.setAccessible(true); // if you want to modify private fields
            if (field.getType() == dynamic_property.class) {
                try {

                    //  Log.e("DB Field ", "" + field.getName());
                    Class<?> clazz = field.get(mem).getClass();
                    Field dyna_value_field = clazz.getDeclaredField("value");
                    Field dyna_json_name_field = clazz.getDeclaredField("json_name");
                    dyna_json_name_field.setAccessible(true);
                    dyna_value_field.setAccessible(true);
                    String j_key = (String) dyna_json_name_field.get(field.get(mem));
                    if (j.has(j_key)) {
                        dyna_value_field.set(field.get(mem), j.getString(j_key));
                    }
                    //     dynamic_property dp=(dynamic_property) field.getClass();
                } catch (Exception ex) {
                    Log.e("REFLECTION ERROR =>", "" + ex.getMessage());
                }
            } else {
                try {
                    //   Log.e("CLASS ", field.getName()+ " - " + field.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return mem;

    }


    public Object load_object_from_Cursor(Cursor c, Object mem) {

        Field[] fieldz = concatenate(mem.getClass().getDeclaredFields(), mem.getClass().getSuperclass().getDeclaredFields());
        for (Field field : fieldz) {

            // for (Field field : mem.getClass().getDeclaredFields()) {
            field.setAccessible(true); // if you want to modify private fields
            if (field.getType() == dynamic_property.class) {
                try {

                    //  Log.e("DB Field ", "" + field.getName());
                    Class<?> clazz = field.get(mem).getClass();
                    Field dyna_value_field = clazz.getDeclaredField("value");
                    Field dyna_db_name_field = clazz.getDeclaredField("column_name");
                    dyna_db_name_field.setAccessible(true);
                    dyna_value_field.setAccessible(true);
                    String c_key = (String) dyna_db_name_field.get(field.get(mem));
                    if (c_key != null && c_key.length() > 1 && c.getColumnIndex(c_key) != -1) {
                        dyna_value_field.set(field.get(mem), c.getString(c.getColumnIndex(c_key)));
                    }

                } catch (Exception ex) {
                    Log.e("REFLECTION ERROR =>", "load_object_from_Cursor " + ex.getMessage());
                }
            } else {
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

    public Object load_object_from_Cursor_annot(Cursor c, Object mem) {

        Field[] fieldz = concatenate(mem.getClass().getDeclaredFields(), mem.getClass().getSuperclass().getDeclaredFields());
        for (Field field : fieldz) {
            field.setAccessible(true); // if you want to modify private fields
            DynamicProperty v = field.getAnnotation(DynamicProperty.class);
            if (v == null || v.column_name() == null || (v.column_name().length() < 1) || (c.getColumnIndex(v.column_name()) == -1)) {
                continue;
            }
            try {
                field.set(mem, c.getString(c.getColumnIndex(v.column_name())));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                Log.e("REFLECTION ERROR =>", "load_object_from_Cursor " + e.getMessage());
            }

            //  Log.e("SANOT",""+field.getAnnotation(DynamicProperty.class).db_column_name());


        }
        return mem;

    }

    public ContentValues load_object_cv_from_JSON(JSONObject j, Object obj) {

        ContentValues cv = new ContentValues();


        Field[] fieldz = concatenate(obj.getClass().getDeclaredFields(), obj.getClass().getSuperclass().getDeclaredFields());
        for (Field field : fieldz) {

            // for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true); // if you want to modify private fields
            if (field.getType() == dynamic_property.class) {
                try {

                    // Log.e("DB Field ", "" + field.getName());
                    Class<?> clazz = field.get(obj).getClass();
                    Field dyna_column_name_field = clazz.getDeclaredField("column_name");
                    Field dyna_json_name_field = clazz.getDeclaredField("json_name");
                    Field dyna_storage_mode_field = clazz.getDeclaredField("storage_mode");
                    dyna_json_name_field.setAccessible(true);
                    dyna_column_name_field.setAccessible(true);
                    dyna_storage_mode_field.setAccessible(true);
                    String j_key = (String) dyna_json_name_field.get(field.get(obj));
                    if (j.has(j_key)) {
                        int storage_mode = (int) dyna_storage_mode_field.get(field.get(obj));
                        if (storage_mode == 2) {
                            String data = save_doc(j.getString(j_key));
                            if (data == error_return) {

                                cv.put("data_status", "e");
                                cv.put((String) dyna_column_name_field.get(field.get(obj)), data);
                            } else {
                                cv.put((String) dyna_column_name_field.get(field.get(obj)), data);

                            }

                        } else {
                            cv.put((String) dyna_column_name_field.get(field.get(obj)), j.getString(j_key));
                        }
                    }


                    field = null;
                } catch (Exception ex) {
                    Log.e("REFLECTION ERROR =>", "" + ex.getMessage());
                }
            } else {


            }
        }
        cv.put("sync_status", "i");
        return cv;

    }

    public ContentValues load_cv_from_object(Object obj) {

        ContentValues cv = new ContentValues();


        Field[] fieldz = concatenate(obj.getClass().getDeclaredFields(), obj.getClass().getSuperclass().getDeclaredFields());
        for (Field field : fieldz) {

            // for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true); // if you want to modify private fields
            if (field.getType() == dynamic_property.class) {
                try {

                    // Log.e("DB Field ", "" + field.getName());
                    Class<?> clazz = field.get(obj).getClass();
                    Field dyna_column_name_field = clazz.getDeclaredField("column_name");
                    Field dyna_value_field = clazz.getDeclaredField("value");


                    dyna_column_name_field.setAccessible(true);
                    dyna_value_field.setAccessible(true);
                    String cv_key = (String) dyna_column_name_field.get(field.get(obj));
                    String cv_value = (String) dyna_value_field.get(field.get(obj));
                    if (cv_key != null && cv_key.length() > 0 && cv_value != null && cv_value.length() > 0) {

                        cv.put(cv_key, cv_value);
                    }


                } catch (Exception ex) {
                    Log.e("REFLECTION ERROR =>", "" + ex.getMessage());
                }
            } else {


            }
        }
        cv.put("sync_status", "p");
        return cv;

    }


    public String greatest_sync_var(String table_name, @Nullable String... filters) {
        try {
            String[] flts = new String[filters.length + 1];
            flts[0] = "sync_var NOT NULL";
            for (int i = 0; i < filters.length; i++) {
                flts[i + 1] = filters[i];
            }
            String qry = "SELECT CAST(sync_var AS INTEGER) FROM " + table_name + (filters == null ? "" : " " + conccat_sql_filters(flts)) + " ORDER BY CAST(sync_var AS INTEGER) DESC LIMIT 1";
            Cursor c = database.rawQuery(qry, null);

            if (c.moveToFirst()) {
                do {

                    String res = c.getString(0);
                    c.close();
                    return res;
                } while (c.moveToNext());
            }
            c.close();
            return "0";
        } catch (Exception e) {
            Log.e("DatabaseManager", "" + e.getMessage());
            return null;
        }
    }

    public String get_record_count(String table_name, @Nullable String... filters) {

        String qry = "SELECT COUNT(*) FROM " + table_name + (filters == null ? "" : " " + conccat_sql_filters(filters));
        //  Log.e("QRY :",""+qry);
        Cursor c = database.rawQuery(qry, null);

        if (c.moveToFirst()) {
            do {
                String res = c.getString(0);
                c.close();
                return res;

            } while (c.moveToNext());
        }
        c.close();
        return "0";
    }


    public String record_count(String table_name) {


        Cursor c = database.rawQuery("SELECT COUNT(*) FROM " + table_name, null);

        if (c.moveToFirst()) {
            do {

                String res = c.getString(0);
                c.close();
                return res;

            } while (c.moveToNext());
        }
        c.close();
        return "0";
    }

    public long sync_period() {
        //Date date = svars.sparta_EA_calendar().getTime();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
        String dttt = format1.format(date);


        if (svars.sync_time(act) == null) {
            return svars.sync_interval_mins(act);
        }
        Date time1 = null;
        try {
            try {
                time1 = new SimpleDateFormat("HH:mm:ss").parse(format1.format(date));
            } catch (Exception ex) {
                Log.e("Time Error =>", ex.getMessage());
            }
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);
            Date time2 = null;
            try {
                Log.e("DATE 2 =>", " t " + date.getTime() + " Time=>" + svars.sync_time(act).split(" ")[1]);
                time2 = new SimpleDateFormat("HH:mm:ss").parse(svars.sync_time(act).split(" ")[1]);
            } catch (Exception ex) {
                Log.e("Time Error =>", ex.getMessage());
            }
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            long diffference = Calendar.getInstance().getTimeInMillis() - calendar2.getTimeInMillis();
            // return (int)Math.round((double)diffference/60000);
            int diff_1 = (int) ((diffference / (1000 * 60)) % 60);
            return diff_1 + (((int) ((diffference / (1000 * 60 * 60)) % 24)) * 60);
        } catch (Exception ex) {
            return svars.sync_interval_mins(act);
        }


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String conccat_sql_filters(String[] str_to_join) {
        String result = "";
        for (int i = 0; i < str_to_join.length; i++) {
            result = result + (i == 0 ? "WHERE " : " AND ") + str_to_join[i];
        }
        return result;

    }


    public static String conccat_sql_string(String[] str_to_join) {
        String result = "";
        for (int i = 0; i < str_to_join.length; i++) {
            result = result + (i == 0 ? "" : ",") + "'" + str_to_join[i] + "'";
        }
        return result;

    }

    public static String concatString(String delimeter, String[] params) {
        String result = "";
        for (int i = 0; i < params.length; i++) {
            result = result + (i == 0 ? "" : delimeter) + "" + params[i] + "";
        }
        return result;

    }

    public static String concatRealmClientString(String delimeter, String[] params) {
        String result = "";
        for (int i = 0; i < params.length; i++) {
            result = result + (i == 0 ? "" : delimeter) + "" + params[i] + "";
        }
        return result;

    }


    public String conccat_sql_string(String[] str_to_join, ArrayList<String> str_to_join2) {
        String result = "";
        for (int i = 0; i < str_to_join.length; i++) {
            result = result + (i == 0 ? "" : ",") + "'" + str_to_join[i] + "'";
        }
        for (int i = 0; i < str_to_join2.size(); i++) {
            result = result + (result.length() < 0 ? "" : ",") + "'" + str_to_join2.get(i) + "'";
        }
        return result;

    }

    public static String conccat_sql_string(ArrayList<String> str_to_join2) {
        String result = "";


        for (int i = 0; i < str_to_join2.size(); i++) {
            result = result + (result.length() < 1 ? "" : ",") + "'" + str_to_join2.get(i) + "'";
        }
        return result;

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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static byte[] get_file_data(String data_name) {


        try {


            File file = new File(data_name);

            return org.apache.commons.io.FileUtils.readFileToByteArray(file);


        } catch (Exception ex) {
            Log.e("Data file retreival :", "Failed " + ex.getMessage());

        }


        return null;
    }

    public String save_doc_us(String base64_bytes) {
        byte[] file_bytes = Base64.decode(base64_bytes, 0);

        String img_name = "TA_DAT" + System.currentTimeMillis() + "JPG_IDC.JPG";
        //  String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOutputStream = null;
        //  File file = new File(path + "/TimeAndAttendance/.RAW_EMPLOYEE_DATA/");
        File file = new File(svars.current_app_config(act).file_path_employee_data);
        if (!file.exists()) {
            Log.e("Creating data dir=>", "" + String.valueOf(file.mkdirs()));
        }
        //  file = new File(path + "/TimeAndAttendance/.RAW_EMPLOYEE_DATA/", img_name);
        file = new File(svars.current_app_config(act).file_path_employee_data, img_name);

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

    public static String error_return = "!!!";


    public static byte[] appEncryptionKey(String password) {
//         password = "password";

        /* Store these things on disk used to derive key later: */
        int iterationCount = 1000;
        int saltLength = 32; // bytes; should be the same size   as the output (256 / 8 = 32)
        int keyLength = 256; // 256-bits for AES-256, 128-bits for AES-128, etc
        byte[] salt = new byte[saltLength];

        /* When first creating the key, obtain a salt with this: */
        SecureRandom random = new SecureRandom();

        random.nextBytes(salt);

        /* Use this to derive the key from the password: */
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
        SecretKeyFactory keyFactory = null;
        try {
            keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] keyBytes = new byte[0];
        try {
            keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        return key.getEncoded();
    }

    public static byte[] encryptBytes(byte[] key, byte[] fileData) throws Exception {

        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(fileData);

        return encrypted;
    }

    public static byte[] decryptBytes(byte[] key, byte[] fileData) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] decrypted = cipher.doFinal(fileData);

        return decrypted;
    }

    public static String saveAppFileFromBase64(String base64_bytes) {
        byte[] outdata = Base64.decode(base64_bytes, Base64.DEFAULT);
        base64_bytes = null;

        return saveAppFileFromBytes(outdata);

    }

    public static String saveAppFileFromBytes(byte[] file_bytes) {
        try {
            return saveAppFileBytes(file_bytes, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String saveFileBytes(byte[] file_bytes, boolean encrypt, String full_folder_path) throws Exception {

        return encrypt ? saveFileBytes(encryptBytes(appEncryptionKey(""), file_bytes), full_folder_path) :
                saveFileBytes(file_bytes, full_folder_path);
    }

    public static String saveAppFileBytes(byte[] file_bytes, boolean encrypt) throws Exception {

        return encrypt ? saveFileBytes(encryptBytes(appEncryptionKey(""), file_bytes), new File(svars.current_app_config(Realm.context).file_path_employee_data).getAbsolutePath()) :
                saveFileBytes(file_bytes, new File(svars.current_app_config(Realm.context).file_path_employee_data).getAbsolutePath());
    }

    public static String saveFileBytes(byte[] outdata, String full_folder_path) {

        String img_name = "R" + System.currentTimeMillis() + "DT.DAT";

        File file = new File(full_folder_path);
        if (!file.exists()) {
            Log.e(logTag, "Creating data dir: " + (file.mkdirs() ? "Successfully created" : "Failed to create !"));
        }
        file = new File(svars.current_app_config(Realm.context).file_path_employee_data, img_name);
        try (RandomAccessFile randomFile = new RandomAccessFile(file.getAbsolutePath(), "rw")) {
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.write(outdata, 0, outdata.length);
            return img_name;
        } catch (IOException ex) {

        }
        return null;
    }

    public static String retrieveFileBase64(String file_path) {

        return Base64.encodeToString(retrieveFileBytes(file_path), Base64.DEFAULT);
    }

    public static String retrieveAppFileBase64(String file_name) {

        try {
            return retrieveAppFileBase64(file_name, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String retrieveAppFileBase64(String file_name, boolean encrypted) throws Exception {

        return encrypted ? Base64.encodeToString(decryptBytes(appEncryptionKey("222222"), retrieveFileBytes(new File(svars.current_app_config(act).file_path_employee_data, file_name).getAbsolutePath())), Base64.DEFAULT) :
                Base64.encodeToString(retrieveFileBytes(new File(svars.current_app_config(act).file_path_employee_data, file_name).getAbsolutePath()), Base64.DEFAULT);
    }

    public static byte[] retrieveFileBytes(String full_file_path) {
        try {
            File file = new File(full_file_path);
            return org.apache.commons.io.FileUtils.readFileToByteArray(file);

        } catch (Exception ex) {
            Log.e("Data file retreival :", " " + ex.getMessage());

        }
        return null;
    }


    public static String save_doc(String base64_bytes) {
        try {
            base64_bytes = base64_bytes.replace("\\n", "").replace("\\", "");
//            Bitmap bmp = s_bitmap_handler.getImage();
            String img_name = "RE_DAT" + System.currentTimeMillis() + "_IMG.JPG";

            File file = new File(svars.current_app_config(Realm.context).file_path_employee_data);
            if (!file.exists()) {
                Log.e(logTag, "Creating data dir: " + (file.mkdirs() ? "Successfully created" : "Failed to create !"));
            }
            file = new File(svars.current_app_config(Realm.context).file_path_employee_data, img_name);

            try (OutputStream fOutputStream = new FileOutputStream(file)) {


                fOutputStream.write(Base64.decode(base64_bytes, 0));
            } catch (FileNotFoundException e) {
                e.printStackTrace();

                return error_return;
            } catch (IOException e) {
                e.printStackTrace();

                return error_return;
            }
            return img_name;
//            return SpartaAppCompactActivity.save_app_image(bmp);

        } catch (Exception ex) {


        }


        return base64_bytes;
    }

    public static String save_doc_(String base64_bytes) {
        try {
            base64_bytes = base64_bytes.replace("\\n", "").replace("\\", "");
            Bitmap bmp = s_bitmap_handler.getImage(Base64.decode(base64_bytes, 0));

            return SpartaAppCompactActivity.save_app_image(bmp);

        } catch (Exception ex) {
            String img_name = "RE_DAT" + System.currentTimeMillis() + "_IMG.JPG";

            File file = new File(svars.current_app_config(Realm.context).file_path_employee_data);
            if (!file.exists()) {
                Log.e(logTag, "Creating data dir: " + (file.mkdirs() ? "Successfully created" : "Failed to create !"));
            }
            file = new File(svars.current_app_config(Realm.context).file_path_employee_data, img_name);
            byte[] file_bytes = Base64.decode(base64_bytes, 0);
            OutputStream fOutputStream = null;

            Log.e("Creating file =>", "" + String.valueOf(file.getAbsolutePath()));
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

        }


        return base64_bytes;
    }

    public static String get_saved_doc_base64(String data_name) {
        String res = "";
        try {
//            res = Base64.encodeToString(s_bitmap_handler.getBytes(BitmapFactory.decodeFile(new File(svars.current_app_config(act).file_path_employee_data, data_name).getAbsolutePath())), 0);
            res = Base64.encodeToString(org.apache.commons.io.FileUtils.readFileToByteArray(new File(svars.current_app_config(act).file_path_employee_data, data_name)), 0);
            return res;
        } catch (Exception ex) {
            Log.e("Data file retreival :", " " + ex.getMessage());

        }


        return res;
    }


    public static Gpsprobe_r gps;


    public static void log_event(Context act, String data) {

        gps = gps == null ? new Gpsprobe_r(act) : gps;
        String prefix = svars.sparta_EA_calendar().getTime().toString() + "   :   " + gps.getLatitude() + "," + gps.getLongitude() + "     =>";

        //   String prefix=svars.sparta_EA_calendar().getTime().toString()+"     =>";
//        String root = act.getExternalFilesDir(null).getAbsolutePath() + "/logs";
        String root = svars.current_app_config(act).file_path_logs;
        Log.e(logTag, "PATH: " + root);

        File file = new File(root);
        file.mkdirs();
        try {
            File gpxfile = new File(file, svars.getCurrentDateOfMonth() + "" + svars.Log_file_name);
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(svars.APP_OPERATION_MODE == svars.OPERATION_MODE.DEV ? prefix + data + "\n" : s_cryptor.encrypt(prefix + data + "\n"));
            writer.flush();
            writer.close();
        } catch (Exception ex) {

        }

    }

    public static void log_String(Context act, String data) {
        gps = gps == null ? new Gpsprobe_r(act) : gps;
        String prefix = svars.sparta_EA_calendar().getTime().toString() + "   :   " + gps.getLatitude() + "," + gps.getLongitude() + "     =>";

        //  String prefix=svars.sparta_EA_calendar().getTime().toString()+"     =>";
//        String root = act.getExternalFilesDir(null).getAbsolutePath() + "/logs";
        String root = svars.current_app_config(Realm.context).file_path_logs;
        Log.e(logTag, "Logging: " + root);

        File file = new File(root);
        file.mkdirs();
        try {
            File gpxfile = new File(file, svars.current_app_config(Realm.context).verbose_app_log + svars.getCurrentDateOfMonth());
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(svars.APP_OPERATION_MODE != svars.OPERATION_MODE.DEV ? prefix + data + "\n" : prefix + data + "\n");
            writer.flush();
            writer.close();
        } catch (Exception ex) {

        }

    }


    public static void log_String(String data) {
        gps = gps == null ? new Gpsprobe_r(Realm.context) : gps;
        String prefix = svars.sparta_EA_calendar().getTime().toString() + "   :   " + gps.getLatitude() + "," + gps.getLongitude() + "     =>";
        String root = svars.current_app_config(Realm.context).file_path_logs;
        Log.e(logTag, "PATH: " + root);

        File file = new File(root);
        file.mkdirs();
        try {
            File gpxfile = new File(file, svars.current_app_config(Realm.context).verbose_app_log + svars.getCurrentDateOfMonth());
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(svars.APP_OPERATION_MODE != svars.OPERATION_MODE.DEV ? prefix + data + "\n" : prefix + data + "\n");
            writer.flush();
            writer.close();
        } catch (Exception ex) {

        }

    }


    private static void addApkToInstallSession(Context context, Uri uri, PackageInstaller.Session session) {
        Log.i("TAG", "addApkToInstallSession " + uri);
        // It's recommended to pass the file size to openWrite(). Otherwise installation may fail
        // if the disk is almost full.
        try {
            OutputStream packageInSession = session.openWrite("package", 0, -1);
            InputStream input;
//            Uri uri = Uri.parse(filename);
            input = context.getContentResolver().openInputStream(uri);

            if (input != null) {
                Log.i("TAG", "input.available: " + input.available());
                byte[] buffer = new byte[16384];
                int n;
                while ((n = input.read(buffer)) >= 0) {
                    packageInSession.write(buffer, 0, n);
                }
            } else {
                Log.i("TAG", "addApkToInstallSession failed");
                throw new IOException("addApkToInstallSession");
            }
            packageInSession.close();  //need to close this stream
            input.close();             //need to close this stream
        } catch (Exception e) {
            Log.i("TAG", "addApkToInstallSession failed2 " + e.toString());
        }
    }

    private void addApkToInstallSession(String assetName, PackageInstaller.Session session)
            throws IOException {
        // It's recommended to pass the file size to openWrite(). Otherwise installation may fail
        // if the disk is almost full.
        try (OutputStream packageInSession = session.openWrite("package", 0, -1);
             InputStream is = act.getAssets().open(assetName)) {
            byte[] buffer = new byte[16384];
            int n;
            while ((n = is.read(buffer)) >= 0) {
                packageInSession.write(buffer, 0, n);
            }
        }
    }

    private static final String PACKAGE_INSTALLED_ACTION =
            "com.example.android.apis.content.SESSION_API_PACKAGE_INSTALLED";

    public static void install(Uri uri) {
        PackageInstaller.Session session = null;
        try {
            PackageInstaller packageInstaller = act.getPackageManager().getPackageInstaller();
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                    PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            int sessionId = packageInstaller.createSession(params);
            session = packageInstaller.openSession(sessionId);
            addApkToInstallSession(act, uri, session);
            // Create an install status receiver.
            Context context = act;
            Intent intent = new Intent(context, splash.class);
            intent.setAction(PACKAGE_INSTALLED_ACTION);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            IntentSender statusReceiver = pendingIntent.getIntentSender();
            // Commit the session (this will start the installation workflow).
            session.commit(statusReceiver);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't install package", e);
        } catch (RuntimeException e) {
            if (session != null) {
                session.abandon();
            }
            throw e;
        }

    }

    public static void quick_update(Activity act) {

        try {
            final View aldv = LayoutInflater.from(act).inflate(R.layout.dialog_app_update, null);
            final ProgressBar pb = (ProgressBar) aldv.findViewById(R.id.prog);
            final ProgressBar loading_bar = (ProgressBar) aldv.findViewById(R.id.loading_bar);
            final GridView version_list = (GridView) aldv.findViewById(R.id.version_list);

            final TextView version = (TextView) aldv.findViewById(R.id.current_version);
            final TextView update_date = (TextView) aldv.findViewById(R.id.update_date);
            final TextView per = (TextView) aldv.findViewById(R.id.prog_per);
            final Button update = (Button) aldv.findViewById(R.id.update);

            final RelativeLayout progres_layout = (RelativeLayout) aldv.findViewById(R.id.progress_layout);
            final AlertDialog[] ald = new AlertDialog[1];
            final boolean[] loaded_ver = {false};
            ArrayList<apk_version> versions = new ArrayList<>();
            final ArrayList<apk_version> finalVersions = versions;
            final String[] apk_name = {null};
            try {
                ald[0] = new AlertDialog.Builder(act)
                        .setView(aldv)
                        .show();
                ald[0].getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progres_layout.setVisibility(View.GONE);

                update_date.setText("Update check date : " + svars.version_check_time(act));
                version.setText("Current Version : " + BuildConfig.VERSION_NAME);

                version_list.setAdapter(new apk_versions_adapter(act, finalVersions));
                version_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        apk_name[0] = finalVersions.get(position).path;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            Thread thread = new Thread() {
                public void run() {
                    Looper.prepare();
                    final JSONObject[] maindata = new JSONObject[1];

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do your web calls here

                            if (svars.isInternetAvailable()) {
                                String data = "";
                                String error_data = "";

                                HttpURLConnection httpURLConnection = null;
                                try {

                                    httpURLConnection = (HttpURLConnection) new URL("http://ta.cs4africa.com/SPARTA/linga/services/get_version.php").openConnection();
                                    httpURLConnection.setRequestMethod("POST");
                                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                                    // httpURLConnection.setRequestProperty("Authorization", "Bearer" + svars.Service_token(act));

                                    httpURLConnection.setDoOutput(true);


                     /*   DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                        wr.writeBytes(params[1].toString());
                        wr.flush();
                        wr.close();*/

                                    int status = httpURLConnection.getResponseCode();
                                    Log.e("version POST RX", " status=> " + status);

                                    try {
                                        InputStream in = httpURLConnection.getInputStream();
                                        InputStreamReader inputStreamReader = new InputStreamReader(in);

                                        int inputStreamData = inputStreamReader.read();
                                        while (inputStreamData != -1) {
                                            char current = (char) inputStreamData;
                                            inputStreamData = inputStreamReader.read();
                                            data += current;
                                        }
                                        maindata[0] = new JSONObject(data);

                                        for (int i = 0; i < maindata[0].getJSONArray("versions").length(); i++) {
                                            JSONObject jj = maindata[0].getJSONArray("versions").getJSONObject(i);
                                            finalVersions.add(new apk_version(jj.getString("id"), jj.getString("version_name"), jj.getString("version_code"), jj.getString("date"), jj.getString("path"), jj.getString("status")));
                                            if (jj.getString("status").equalsIgnoreCase("1")) {
                                                if (!BuildConfig.VERSION_NAME.equalsIgnoreCase(jj.getString("version_name")) | BuildConfig.VERSION_CODE != Integer.parseInt(jj.getString("version_code"))) {
                                                    act.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            version.setText("Current Version : " + BuildConfig.VERSION_NAME + " Out of date");
                                                            version.setTextColor(Color.RED);
                                                            version_list.setAdapter(new apk_versions_adapter(act, finalVersions));
                                                            // Toast.makeText(act, "App out of date", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                } else {
                                                    act.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            version.setText("Current Version : " + BuildConfig.VERSION_NAME + " Latest");
                                                            version.setTextColor(Color.GREEN);
                                                            // Toast.makeText(act, "App out of date", Toast.LENGTH_LONG).show();
                                                            version_list.setAdapter(new apk_versions_adapter(act, finalVersions));
                                                        }
                                                    });
                                                }
                                            }

                                        }
                                        loaded_ver[0] = true;
                                        act.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                loading_bar.setVisibility(View.GONE);

                                            }
                                        });


                                    } catch (Exception exx) {
                                        InputStream error = httpURLConnection.getErrorStream();
                                        InputStreamReader inputStreamReader2 = new InputStreamReader(error);

                                        int inputStreamData2 = inputStreamReader2.read();
                                        while (inputStreamData2 != -1) {
                                            char current = (char) inputStreamData2;
                                            inputStreamData2 = inputStreamReader2.read();
                                            error_data += current;
                                        }
                                        Log.e("version POST RX", "error => " + error_data);

                                    }


                                } catch (Exception e) {
                                    Log.e("version POST TX", "External error => " + e.getMessage());
                                    e.printStackTrace();
                                } finally {
                                    if (httpURLConnection != null) {
                                        httpURLConnection.disconnect();
                                    }
                                }
                            } else {

                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(act, "Internet not available \n Unable to update application...", Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                            handler.removeCallbacks(this);
                            Looper.myLooper().quit();
                        }
                    }, 2000);

                    Looper.loop();
                }
            };
            thread.start();


            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (apk_name[0] != null) {
                        update.setVisibility(View.GONE);
                        ald[0].setCancelable(false);
                        Thread thread = new Thread() {
                            public void run() {
                                Looper.prepare();

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            URL url = new URL("http://ta.cs4africa.com/SPARTA/linga/apks/" + apk_name[0]);
                                            HttpURLConnection c = (HttpURLConnection) url.openConnection();
                                            c.setRequestMethod("GET");
                                            c.setDoOutput(true);
                                            c.connect();
                                            act.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progres_layout.setVisibility(View.VISIBLE);
                                                    version_list.setVisibility(View.GONE);

                                                }
                                            });

                                            String PATH = act.getExternalFilesDir(null).getAbsolutePath() + "/apks";
                                            Log.e("LOG_TAG", "PATH: " + PATH);

                                            File file = new File(PATH);
                                            file.mkdirs();
                                            final File outputFile = new File(file, apk_name[0]);
                                            FileOutputStream fos = new FileOutputStream(outputFile);
                                            InputStream is = c.getInputStream();

                                            byte[] buffer = new byte[4096];
                                            int len1 = 0;
                                            int per_counter = 0;

                                            while ((len1 = is.read(buffer)) != -1) {
                                                fos.write(buffer, 0, len1);
                                                per_counter += len1;
                                                final double finalPercent = Math.round(((double) per_counter / (double) c.getContentLength()) * 100);
                                                Log.e("Download ", "per: " + finalPercent + "%");
                                                act.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        per.setText("Downloading :" + finalPercent + "%");
                                                        pb.setProgress((int) finalPercent);
                                                    }
                                                });
                                            }

                                            fos.close();
                                            is.close();
                                            act.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    ald[0].setCancelable(true);
                                                    per.setText("Installing ...");
                                                    pb.setIndeterminate(true);
                                                    Toast.makeText(act, " Apk downloaded successfully", Toast.LENGTH_LONG).show();
                                                    install(Uri.fromFile(outputFile));
                                                    per.setText("Installed");
                                                   /* final Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(Uri.fromFile(outputFile), "application/vnd.android.package-archive");

                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    act.startActivity(intent);*/
                                                    String compdate = "";

                                                    Cursor c = database.rawQuery("SELECT CURRENT_TIMESTAMP", null);
                                                    if (c.moveToFirst()) {
                                                        do {
                                                            Log.e("nowdate =>", " " + c.getString(0));
                                                            svars.version_check_time(act, c.getString(0));
                                                        } while (c.moveToNext());
                                                    }
                                                    c.close();

                                                }
                                            });
                                        } catch (Exception ex) {
                                            Log.e("download error ", "" + ex.getMessage());
                                            ex.printStackTrace();
                                            act.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    ald[0].setCancelable(true);

                                                }
                                            });
                                        }
                                    }
                                }, 2000);

                                Looper.loop();
                            }
                        };
                        thread.start();
                    }
                }

            });


        } catch (Exception e) {
            Log.e("download error ", "" + e.getMessage());
            e.printStackTrace();


        }


    }


}
