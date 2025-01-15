package sparta.realm.Services;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.common.base.Stopwatch;
import com.google.common.io.ByteStreams;
import com.realm.annotations.db_class_;
import com.realm.annotations.sync_service_description;
import com.realm.annotations.sync_status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import dalvik.system.DexFile;

import sparta.realm.R;
import sparta.realm.Realm;
import sparta.realm.spartamodels.db_class;
import sparta.realm.spartamodels.dyna_data_obj;
import sparta.realm.spartamodels.dynamic_property;
import sparta.realm.spartamodels.member_data;
import sparta.realm.spartautils.svars;
import sparta.realm.utils.AppConfig;

import static com.realm.annotations.SyncDescription.service_type.Configuration;
import static com.realm.annotations.SyncDescription.service_type.Download;
import static com.realm.annotations.SyncDescription.service_type.Download_Upload;
import static com.realm.annotations.SyncDescription.service_type.Upload;
import static sparta.realm.Realm.realm;
import static sparta.realm.spartautils.svars.current_app_config;


public class SynchronizationManager_ {
    public static String logTag = "SynchronizationManager";
    static Context act;
    String st = "";
    Timer logintimer = new Timer();
    public static Timer syncregisterdtimer = null;
    Timer sync_clock_timer = new Timer();
    Timer sync_workers_timer = new Timer();

    Drawable icondrawable;
    static DatabaseManager sdb;

    static {
        act = Realm.context;
        sdb = Realm.databaseManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            builder.detectFileUriExposure();
        }
        AndroidNetworking.initialize(act);
        ssi = new SynchronizationStatusHandler() {
            @Override
            public void on_status_code_changed(int status) {

            }

            @Override
            public void on_status_changed(String status) {

            }

            @Override
            public void on_info_updated(String status) {

            }

            @Override
            public void on_main_percentage_changed(int progress) {

            }

            @Override
            public void on_secondary_progress_changed(int progress) {

            }

        };
    }

    public SynchronizationManager_() {

        app_config = current_app_config(act);

    }


    public interface SynchronizationHandler {
        default Boolean OnRequestToSync() {
            return true;
        }

        default ArrayList<JSONObject> OnAboutToUploadObjects(sync_service_description ssd, ArrayList<JSONObject> objects) {
            return objects;
        }

        default JSONObject OnUploadingObject(sync_service_description ssd, JSONObject object) {
            return object;
        }

        default JSONObject OnUploadedObject(sync_service_description ssd, JSONObject object, JSONObject response) {

            return response;
        }

        default ANError OnUploadedObjectError(sync_service_description ssd, JSONObject object, ANError error) {
            return error;
        }

        //        default Boolean OnAboutToDownload(sync_service_description ssd, JSONObject filter) {
//            return true;
//        }
        default JSONObject OnAboutToDownload(sync_service_description ssd, JSONObject filter) {
            return filter;
        }

        default JSONObject OnDownloadedObject(sync_service_description ssd, JSONObject object, JSONObject response) {

            return response;
        }

        default JSONObject OnAuthenticated(String token, JSONObject response) {

            return response;
        }


    }

    static SynchronizationHandler Main_handler = new SynchronizationHandler() {
    };

    public interface SynchronizationStatusHandler {
        default void on_status_code_changed(int status) {
        }

        ;

        default void on_api_error(sync_service_description ssd, String error) {
        }

        ;

        void on_status_changed(String status);

        void on_info_updated(String status);

        void on_main_percentage_changed(int progress);

        void on_secondary_progress_changed(int progress);

        default void onSynchronizationBegun() {
        }

        ;

        default void onSynchronizationCompleted(sync_service_description ssd) {
        }

        ;

        default void onSynchronizationCompleted() {
        }

        ;
    }

    static SynchronizationStatusHandler ssi;
    static AppConfig app_config;

    public SynchronizationManager_(SynchronizationStatusHandler ssi) {


        this.ssi = ssi;
        app_config = current_app_config(act);


    }

    public void OverrideSynchronization(SynchronizationHandler synchronizationHandler) {
        Main_handler = synchronizationHandler;
    }

    public void setSynchronizationStatusHandler(SynchronizationStatusHandler synchronizationstatushandler) {
        ssi = synchronizationstatushandler;
    }

    public void InitialiseAutosync() {
        if (syncregisterdtimer != null) {
            return;
        }
        syncregisterdtimer = new Timer();
        syncregisterdtimer.schedule(new TimerTask() {

            public void run() {
                String time_now = "Today " + Calendar.getInstance().getTime().toString().split(" ")[3];
                // svars.set_sync_time(act,time_now);
                Log.e("Initializing sync", " time =>" + time_now);
                Log.e("Initializing sync", " PERIOD =>" + sdb.sync_period());
                if (sdb.sync_period() >= (svars.sync_interval_mins(act) - 1) || sdb.sync_period() < 0) {

                    if (svars.background_sync(act)) {
                        sync_now();

                    }

                }


            }
        }, 1000, svars.sync_interval_mins(act) * 60000);

    }


    public void sync_now() {
        if (sdb.sync_period() >= 1) {
            ssi.on_status_code_changed(1);
            // 3
            //load_users();
            renew_token();
            /**/



            /**/
            //   check_for_for_updates();


        }
    }

    static int sync_sum_counter = 0;
    static int sync_complete_counter = 0;
    static int sync_success_counter = 0;

    void launch_services() {
        //sync_unregistered_employees(act);
        //  sync_unregistered_employee_fingerprints(act);
        ssi.onSynchronizationBegun();
        sync_sum_counter = 0;
        sync_success_counter = 0;
        sync_complete_counter = 0;
        try {
            //  setup_sync_action();
            // setup_sync_action_ann_0();
            setup_sync_action_ann();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SYNC Error :", "" + e.getMessage());
        }


    }

    public void set_sync(boolean sync) {

        svars.set_shared_pref_boolean(act, svars.shared_prefs_booleans.should_sync, sync);
    }

    /**
     * Sets up synchronization based on ? extends db_class classes with dynamic_property types defined
     *
     * @deprecated This method is no longer used,its slow compaired to {@link this#setup_sync_action_ann()} also uses previous heavy dynamicproperty classes and runtime reflection .
     * <p> Use {@link this#setup_sync_action_ann()} instead.
     */
    @Deprecated()
    void setup_sync_action() throws IOException {


        HashMap<sync_service_description, Object> service_descriptions = new HashMap<>();
        dynamic_property dpex = new dynamic_property(null, null, null);
        Class<?> cex = dpex.getClass();
        String dpex_path = cex.getName();

        String package_path = dpex_path.substring(0, dpex_path.lastIndexOf('.'));

        String codepath = act.getPackageCodePath();

        DexFile df = new DexFile(codepath);
        Stopwatch sw =  Stopwatch.createUnstarted();
        sw.start();

        for (Enumeration<String> iter = df.entries(); iter.hasMoreElements(); ) {
            String s = iter.nextElement();
            if (s.startsWith(package_path)) {
                try {
                    Class<?> clazz = Class.forName(s);


                    db_class db_tb_temp = new db_class("");
                    if (db_tb_temp.getClass().isAssignableFrom(clazz) && !db_tb_temp.getClass().getName().equalsIgnoreCase(clazz.getName())) {
                        try {
                            Field ssd_ = clazz.getSuperclass().getDeclaredField("ssds");
                            if (ssd_.get(clazz.newInstance()) != null) {

                                sync_service_description[] ssds = (sync_service_description[]) ssd_.get(clazz.newInstance());
                                // sync_service_description ssd_t = (sync_service_description) ssd_.get(clazz.newInstance());
                                for (sync_service_description ssd_t : ssds) {
                                    if (ssd_t == null) {
                                        continue;
                                    }
                                    if (ssd_t.servic_type == Download_Upload) {
                                        ssd_t.servic_type = Download;
                                        service_descriptions.put(ssd_t, clazz.newInstance());
                                        sync_service_description ssd_t2 = ssd_t.clone();

                                        ssd_t2.servic_type = Upload;
                                        service_descriptions.put(ssd_t2, clazz.newInstance());

                                    } else {
                                        service_descriptions.put(ssd_t, clazz.newInstance());
                                    }
                                }

                            } else {
                                continue;


                            }

                        } catch (Exception ex) {
                            continue;
                        }
                        Log.e("Classes reflected =>", "SERVICE :" + s);

                    }
                } catch (Exception EX) {
                }
            }
        }

        for (Object o : service_descriptions.keySet()) {
            sync_service_description ssd_t = ((sync_service_description) o);
//            Log.e("SYNC ::  ","Object table :"+((db_class)service_descriptions.get(o)).table_name+"\n"
//            +"Service name :"+ssd_t.service_name+"\n"
//            +"Service type :"+ssd_t.servic_type.name()+"\n"
//            +"upload link :"+ssd_t.upload_link+"\n"
//            +"download link :"+ssd_t.download_link+"\n");

            if (ssd_t.servic_type == Download) {
                download(ssd_t, service_descriptions.get(o));

            } else if (ssd_t.servic_type == Upload) {
                upload(ssd_t, service_descriptions.get(o));
                // download((sync_service_description) ssd_.get(clazz.newInstance()),clazz.newInstance());

            } else if (ssd_t.servic_type == Configuration) {
                if (svars.global_data_sync(act)) {
                    download_configuration(ssd_t);

                }


            }
        }
        sw.stop();
        Log.e("Reflection raw :", "" + sw.elapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * Sets up synchronization based on annotations with dynamicProperty
     *
     * @deprecated This method is no longer used,its slow compaired to {@link this#setup_sync_action_ann()} which is almost twice as fast with annotation.
     * This method still uses runtime reflection
     * <p> Use {@link this#setup_sync_action_ann()} instead.
     */
    @Deprecated()
    void setup_sync_action_ann_0() {

        HashMap<sync_service_description, Object> service_descriptions = new HashMap<>();
        Stopwatch sw =  Stopwatch.createUnstarted();
        sw.start();
        try {


            for (sync_service_description ssd_t : realm.getSyncDescription()) {
                if (ssd_t == null) {
                    continue;
                }
                Class<?> clazz = Class.forName(ssd_t.object_package);
                if (ssd_t.servic_type == Download_Upload) {
                    ssd_t.servic_type = Download;
                    service_descriptions.put(ssd_t, clazz.newInstance());
                    sync_service_description ssd_t2 = ssd_t.clone();

                    ssd_t2.servic_type = Upload;
                    service_descriptions.put(ssd_t2, clazz.newInstance());

                } else {
                    service_descriptions.put(ssd_t, clazz.newInstance());
                }
            }


        } catch (Exception ex) {

        }

        for (Object o : service_descriptions.keySet()) {
            sync_service_description ssd_t = ((sync_service_description) o);


            if (ssd_t.servic_type == Download) {
                download_(ssd_t);

            } else if (ssd_t.servic_type == Upload) {
                upload_(ssd_t);


            } else if (ssd_t.servic_type == Configuration) {
                if (svars.global_data_sync(act)) {
                    download_configuration(ssd_t);

                }


            }
        }

        sw.stop();
        Log.e("Reflection ann :", "" + sw.elapsed(TimeUnit.MILLISECONDS));
    }


    /**
     * Sets up synchronization based on  data from mappery of annotated data which is pre-reflected at pre build
     * No runtime reflection
     */
    void setup_sync_action_ann() {
        Stopwatch sw =  Stopwatch.createUnstarted();
        sw.start();
        try {
            List<sync_service_description> sync_services = realm.getSyncDescription();
            sync_sum_counter = sync_services.size();

            for (sync_service_description ssd_t : sync_services) {
                if (ssd_t == null) {
                    continue;
                }
                switch (ssd_t.servic_type) {
                    case Download:
                        download_(ssd_t);

                        break;
                    case Upload:
                        upload_(ssd_t);

                        break;
                    case Download_Upload:
                        //  sync_service_description ssd_t2 = ssd_t.clone();

                        download_(ssd_t);
                        // ssd_t2.servic_type = Upload;

                        //  upload_(ssd_t);

                        break;
                    case Configuration:
                        if (svars.global_data_sync(act)) {
                            //  download_configuration_ann(ssd_t);

                        }
                        break;
                }


            }
        } catch (Exception ex) {
            Log.e(logTag, "" + ex.getMessage());
        }

        sw.stop();
        Log.e("Reflection ann 2:", "" + sw.elapsed(TimeUnit.MILLISECONDS));
    }

    void download(sync_service_description ssd, Object obj_class) {

        Log.e("SYNC ::  ", "Object table :" + ssd.table_name + "\n"
                + "Service name :" + ssd.service_name + "\n"
                + "Service type :" + ssd.servic_type.name() + "\n"
                + "download link :" + ssd.download_link + "\n");
        sync_sum_counter++;
        ssi.on_status_code_changed(2);
        ssi.on_status_changed("Synchronizing " + ssd.service_name + " ↓");
        JSONObject filter_object = ssd.use_download_filter ? generate_filter(ssd.table_name, ssd.chunk_size, ssd.table_filters) : new JSONObject();
        new AsyncTask() {
            JSONObject maindata;

            @Override
            protected Object doInBackground(Object[] params) {
                String data = "";
                String error_data = "";

                HttpURLConnection httpURLConnection = null;
                try {

                    httpURLConnection = (HttpURLConnection) new URL(params[0].toString()).openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Authorization", svars.Service_token(act));

                    httpURLConnection.setDoOutput(true);

                    if (ssd.use_download_filter) {
                        //  httpURLConnection.setDoOutput(true);
                        DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                        wr.writeBytes(params[1].toString());
                        wr.flush();
                        wr.close();  /**/
                    }
                    int status = httpURLConnection.getResponseCode();
                    Log.e(ssd.service_name + " :: RX", " status=> " + status);
                    // ssi.on_status_changed("Synchronizing");

                    try {
                        InputStream in = httpURLConnection.getInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(in);


                        data = new String(ByteStreams.toByteArray(in));


                        Log.e(ssd.service_name + " :: RX =>", " " + data);


                    } catch (Exception exx) {
                        InputStream error = httpURLConnection.getErrorStream();
                        InputStreamReader inputStreamReader2 = new InputStreamReader(error);

                        int inputStreamData2 = inputStreamReader2.read();
                        while (inputStreamData2 != -1) {
                            char current = (char) inputStreamData2;
                            inputStreamData2 = inputStreamReader2.read();
                            error_data += current;
                        }
                        Log.e(ssd.service_name + " :: TX", "error => " + error_data);

                    }
                    maindata = new JSONObject(data);
                    data = null;
                    //  ssi.on_main_percentage_changed(0);

                    if (maindata.getBoolean("IsOkay")) {


                        JSONArray temp_ar;
                        if (ssd.service_name.equalsIgnoreCase("Member fingerprints")) {
                            temp_ar = maindata.getJSONObject("result").getJSONArray("result");
                            // ((member_data)obj_class).data.storage_mode=2;
                        } else if (ssd.service_name.equalsIgnoreCase("Member images")) {
                            //   temp_ar = maindata.getJSONObject("result").getJSONArray("result");
                            temp_ar = maindata.getJSONArray("Result");
                            ((member_data) obj_class).data.storage_mode = 2;
                        } else {
                            temp_ar = maindata.getJSONObject("Result").getJSONArray("Result");
                        }

                        double den = (double) temp_ar.length();
                        sdb.register_object(true, null, null, null);
                        for (int i = 0; i < temp_ar.length(); i++) {
                            ssi.on_status_changed("Synchronizing " + ssd.service_name);
                            JSONObject jj = temp_ar.getJSONObject(i);
                            // sd.register_member(i==0?true:i==temp_ar.length()?false:null,jj);
                            if (ssd.service_name.equalsIgnoreCase("Fees details")) {
                                jj.put("fees_type", "1");
                            }
                            sdb.register_object(null, jj, obj_class, ssd.service_name);

                            double num = (double) i + 1;
                            double per = (num / den) * 100.0;
                            ssi.on_secondary_progress_changed((int) per);
                            //ssi.on_info_updated("Members :"+num+"/"+den+"    Total local members :"+sdb.employee_count());
                            ssi.on_info_updated(ssd.service_name + " :" + num + "/" + den + "    Local data :" + Integer.parseInt(sdb.get_record_count(ssd.table_name, ssd.table_filters)));
                            jj = null;
                            //2832581/01

                        }
                        sdb.register_object(false, null, null, null);


                        sync_complete_counter++;
                        sync_success_counter++;
                        double denm = (double) sync_sum_counter;
                        ssi.on_status_changed("Synchronized " + ssd.service_name);

                        double num = (double) sync_complete_counter;
                        double per = (num / denm) * 100.0;
                        ssi.on_main_percentage_changed((int) per);


                        if (temp_ar.length() == ssd.chunk_size) {
                            download(ssd, obj_class);


                        } else {
                            if (per == 100.0) {
                                ssi.on_main_percentage_changed(100);
                                ssi.on_status_changed("Synchronization complete");
                                ssi.on_secondary_progress_changed(100);
                                ssi.on_main_percentage_changed(100);
                                ssi.on_info_updated("Synchronization complete");
                                ssi.on_status_code_changed(3);
                            }

                        }
                        maindata = null;
                        temp_ar = null;
                    }

                } catch (Exception e) {
                    Log.e(ssd.service_name + ":: TX", " error => " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }

                return data;
            }


        }.execute(ssd.download_link, filter_object);

    }

    public static void download(sync_service_description ssd) {

        Log.e("SYNC ::  ", "Object table :" + ssd.table_name + "\n"
                + "Service name :" + ssd.service_name + "\n"
                + "Service type :" + ssd.servic_type.name() + "\n"
                + "download link :" + ssd.download_link + "\n");
        sync_sum_counter++;
        ssi.on_status_code_changed(2);
        ssi.on_status_changed("Synchronizing " + ssd.service_name + " ↓");
        JSONObject filter_object = ssd.use_download_filter ? generate_filter(ssd.table_name, ssd.chunk_size, ssd.table_filters) : new JSONObject();
        new AsyncTask() {
            JSONObject maindata;

            @Override
            protected Object doInBackground(Object[] params) {
                String data = "";
                String error_data = "";

                HttpURLConnection httpURLConnection = null;
                try {

                    httpURLConnection = (HttpURLConnection) new URL(params[0].toString()).openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Authorization", svars.Service_token(act));

                    httpURLConnection.setDoOutput(true);

                    if (ssd.use_download_filter) {
                        //  httpURLConnection.setDoOutput(true);
                        DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                        wr.writeBytes(params[1].toString());
                        wr.flush();
                        wr.close();  /**/
                    }
                    int status = httpURLConnection.getResponseCode();
                    Log.e(ssd.service_name + " :: RX", " status=> " + status);
                    // ssi.on_status_changed("Synchronizing");

                    try {
                        InputStream in = httpURLConnection.getInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(in);


                        data = new String(ByteStreams.toByteArray(in));


                        Log.e(ssd.service_name + " :: RX =>", " " + data);


                    } catch (Exception exx) {
                        InputStream error = httpURLConnection.getErrorStream();
                        InputStreamReader inputStreamReader2 = new InputStreamReader(error);

                        int inputStreamData2 = inputStreamReader2.read();
                        while (inputStreamData2 != -1) {
                            char current = (char) inputStreamData2;
                            inputStreamData2 = inputStreamReader2.read();
                            error_data += current;
                        }
                        Log.e(ssd.service_name + " :: TX", "error => " + error_data);

                    }
                    maindata = new JSONObject(data);
                    data = null;
                    //  ssi.on_main_percentage_changed(0);

                    if (maindata.getBoolean("IsOkay")) {


                        JSONArray temp_ar;
                        Object json = new JSONTokener(maindata.opt("Result").toString()).nextValue();
                        temp_ar = json instanceof JSONArray ? (JSONArray) json : ((JSONObject) json).getJSONArray("Result");

                       /* if(ssd.service_name.equalsIgnoreCase("Member fingerprints")){
                            temp_ar = maindata.getJSONObject("result").getJSONArray("result");
                            // ((member_data)obj_class).data.storage_mode=2;
                        }else if(ssd.service_name.equalsIgnoreCase("Member images")){
                            //   temp_ar = maindata.getJSONObject("result").getJSONArray("result");
                            temp_ar = maindata.getJSONArray("Result");
                            // ((member_data)obj_class).data.storage_mode=2;
                        }else{
                            if(ssd.use_download_filter){
                                temp_ar = maindata.getJSONObject("Result").getJSONArray("Result");

                            }else{
                                temp_ar = maindata.getJSONArray("Result");
                            }
                        }*/

                        double den = (double) temp_ar.length();
                        sdb.register_object_auto_ann(true, null, ssd);
                        if (!ssd.use_download_filter) {
                            sdb.getDatabase().execSQL("DELETE FROM " + ssd.table_name + " WHERE sync_status ='" + sync_status.syned.ordinal() + "'");
                        }
                        Log.e(ssd.service_name + " :: TX", "ISOK ");
                        for (int i = 0; i < temp_ar.length(); i++) {
                            ssi.on_status_changed("Synchronizing " + ssd.service_name);
                            JSONObject jj = temp_ar.getJSONObject(i);
                            // sd.register_member(i==0?true:i==temp_ar.length()?false:null,jj);
                            if (ssd.service_name.equalsIgnoreCase("Fees details")) {
                                jj.put("fees_type", "1");
                            }
                            sdb.register_object_auto_ann(null, jj, ssd);

                            double num = (double) i + 1;
                            double per = (num / den) * 100.0;
                            ssi.on_secondary_progress_changed((int) per);
                            //ssi.on_info_updated("Members :"+num+"/"+den+"    Total local members :"+sdb.employee_count());
                            ssi.on_info_updated(ssd.service_name + " :" + num + "/" + den + "    Local data :" + Integer.parseInt(sdb.get_record_count(ssd.table_name, ssd.table_filters)));
                            jj = null;
                            //2832581/01

                        }
                        sdb.register_object_auto_ann(false, null, ssd);


                        sync_complete_counter++;
                        sync_success_counter++;
                        double denm = (double) sync_sum_counter;
                        ssi.on_status_changed("Synchronized " + ssd.service_name);

                        double num = (double) sync_complete_counter;
                        double per = (num / denm) * 100.0;
                        ssi.on_main_percentage_changed((int) per);


                        if (temp_ar.length() >= ssd.chunk_size && ssd.use_download_filter) {
                            download_(ssd);


                        } else {
                            if (per == 100.0) {
                                ssi.on_main_percentage_changed(100);
                                ssi.on_status_changed("Synchronization complete");
                                ssi.on_secondary_progress_changed(100);
                                ssi.on_main_percentage_changed(100);
                                ssi.on_info_updated("Synchronization complete");
                                ssi.on_status_code_changed(3);
                            }

                        }
                        maindata = null;
                        temp_ar = null;
                    }

                } catch (Exception e) {
                    Log.e(ssd.service_name + ":: TX", " error => " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }

                return data;
            }


        }.execute(app_config.APP_MAINLINK + ssd.download_link, filter_object);

    }

    /**
     * Uses MulitiThread with single insertion (conversion :Json to CV)
     *
     * @param ssd
     */
    public static void download__(sync_service_description ssd) {

        Log.e("SYNC ::  ", "Object table :" + ssd.table_name + "\n"
                + "Service name :" + ssd.service_name + "\n"
                + "Service type :" + ssd.servic_type.name() + "\n"
                + "download link :" + ssd.download_link + "\n");
        sync_sum_counter++;
        ssi.on_status_code_changed(2);
        ssi.on_status_changed("Synchronizing " + ssd.service_name + " ↓");
        JSONObject filter_object = ssd.use_download_filter ? generate_filter(ssd.table_name, ssd.chunk_size, ssd.table_filters) : new JSONObject();
        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                final JSONObject[] maindata = new JSONObject[1];

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        String data = "";
                        String error_data = "";

                        HttpURLConnection httpURLConnection = null;
                        try {

                            httpURLConnection = (HttpURLConnection) new URL(app_config.APP_MAINLINK + ssd.download_link).openConnection();
                            httpURLConnection.setRequestMethod("POST");
                            httpURLConnection.setRequestProperty("Content-Type", "application/json");
                            httpURLConnection.setRequestProperty("Authorization", svars.Service_token(act));

                            httpURLConnection.setDoOutput(true);

                            if (ssd.use_download_filter) {
                                //  httpURLConnection.setDoOutput(true);
                                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                                wr.writeBytes(filter_object.toString());
                                wr.flush();
                                wr.close();  /**/
                            }
                            int status = httpURLConnection.getResponseCode();
                            Log.e(ssd.service_name + " :: RX", " status=> " + status);
                            // ssi.on_status_changed("Synchronizing");

                            try {
                                InputStream in = httpURLConnection.getInputStream();
                                InputStreamReader inputStreamReader = new InputStreamReader(in);


                                data = new String(ByteStreams.toByteArray(in));


                                Log.e(ssd.service_name + " :: RX =>", " " + data);


                            } catch (Exception exx) {
                                InputStream error = httpURLConnection.getErrorStream();
                                InputStreamReader inputStreamReader2 = new InputStreamReader(error);

                                int inputStreamData2 = inputStreamReader2.read();
                                while (inputStreamData2 != -1) {
                                    char current = (char) inputStreamData2;
                                    inputStreamData2 = inputStreamReader2.read();
                                    error_data += current;
                                }
                                Log.e(ssd.service_name + " :: TX", "error => " + error_data);

                            }
                            maindata[0] = new JSONObject(data);
                            data = null;
                            //  ssi.on_main_percentage_changed(0);

                            if (maindata[0].getBoolean("IsOkay")) {


                                JSONArray temp_ar;
                                Object json = new JSONTokener(maindata[0].opt("Result").toString()).nextValue();
                                temp_ar = json instanceof JSONArray ? (JSONArray) json : ((JSONObject) json).getJSONArray("Result");

                       /* if(ssd.service_name.equalsIgnoreCase("Member fingerprints")){
                            temp_ar = maindata.getJSONObject("result").getJSONArray("result");
                            // ((member_data)obj_class).data.storage_mode=2;
                        }else if(ssd.service_name.equalsIgnoreCase("Member images")){
                            //   temp_ar = maindata.getJSONObject("result").getJSONArray("result");
                            temp_ar = maindata.getJSONArray("Result");
                            // ((member_data)obj_class).data.storage_mode=2;
                        }else{
                            if(ssd.use_download_filter){
                                temp_ar = maindata.getJSONObject("Result").getJSONArray("Result");

                            }else{
                                temp_ar = maindata.getJSONArray("Result");
                            }
                        }*/

                                double den = (double) temp_ar.length();
                                sdb.register_object_auto_ann(true, null, ssd);
                                if (!ssd.use_download_filter) {
                                    sdb.getDatabase().execSQL("DELETE FROM " + ssd.table_name + " WHERE sync_status ='" + sync_status.syned.ordinal() + "'");
                                }
                                Log.e(ssd.service_name + " :: TX", "ISOK ");
                                for (int i = 0; i < temp_ar.length(); i++) {
                                    ssi.on_status_changed("Synchronizing " + ssd.service_name);
                                    JSONObject jj = temp_ar.getJSONObject(i);


                                    sdb.register_object_auto_ann(null, jj, ssd);

                                    double num = (double) i + 1;
                                    double per = (num / den) * 100.0;
                                    ssi.on_secondary_progress_changed((int) per);
                                    //ssi.on_info_updated("Members :"+num+"/"+den+"    Total local members :"+sdb.employee_count());
                                    ssi.on_info_updated(ssd.service_name + " :" + num + "/" + den + "    Local data :" + Integer.parseInt(sdb.get_record_count(ssd.table_name, ssd.table_filters)));
                                    jj = null;
                                    //2832581/01

                                }
                                sdb.register_object_auto_ann(false, null, ssd);


                                sync_complete_counter++;
                                sync_success_counter++;
                                double denm = (double) sync_sum_counter;
                                ssi.on_status_changed("Synchronized " + ssd.service_name);

                                double num = (double) sync_complete_counter;
                                double per = (num / denm) * 100.0;
                                ssi.on_main_percentage_changed((int) per);


                                if (temp_ar.length() >= ssd.chunk_size && ssd.use_download_filter) {
                                    download__(ssd);


                                } else {
                                    if (per == 100.0) {
                                        ssi.on_main_percentage_changed(100);
                                        ssi.on_status_changed("Synchronization complete");
                                        ssi.on_secondary_progress_changed(100);
                                        ssi.on_main_percentage_changed(100);
                                        ssi.on_info_updated("Synchronization complete");
                                        ssi.on_status_code_changed(3);
                                    }

                                }
                                maindata[0] = null;
                                temp_ar = null;
                            }

                        } catch (Exception e) {
                            Log.e(ssd.service_name + ":: TX", " error => " + e.getMessage());
                            e.printStackTrace();
                        } finally {
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }
                        }

                        handler.removeCallbacks(this);
                        Looper.myLooper().quit();
                    }
                }, 500);

                Looper.loop();
            }
        };
        thread.start();


        // }.execute();

    }

    public static void download_(sync_service_description ssd) {

        Log.e("SYNC ::  ", "\nObject table :" + ssd.table_name + "\n"
                + "Service name :" + ssd.service_name + "\n"
                + "Service type :" + ssd.servic_type.name() + "\n"
                + "download link :" + ssd.download_link + "\n");
        //   sync_sum_counter++;
        ssi.on_status_code_changed(2);
        ssi.on_status_changed("Synchronizing " + ssd.service_name + " ↓");
        final JSONObject[] filter_object = {ssd.use_download_filter ? generate_filter(ssd.table_name, ssd.chunk_size, ssd.table_filters) : new JSONObject()};
        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                final JSONObject[] maindata = new JSONObject[1];

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

//                        if(!Main_handler.OnAboutToDownload(ssd, filter_object[0]))
//                        {
                        filter_object[0] = Main_handler.OnAboutToDownload(ssd, filter_object[0]);
                        if (filter_object[0] == null) {
                            sync_complete_counter++;
                            sync_success_counter++;
                            double denm = (double) sync_sum_counter;
                            ssi.on_status_changed("Synchronized " + ssd.service_name);

                            double num = (double) sync_complete_counter;
                            double per = (num / denm) * 100.0;
                            ssi.on_main_percentage_changed((int) per);


                            return;
                        }

                        String data = "";
                        String error_data = "";

                        HttpURLConnection httpURLConnection = null;
                        try {

                            httpURLConnection = (HttpURLConnection) new URL(app_config.APP_MAINLINK + ssd.download_link).openConnection();
                            httpURLConnection.setRequestMethod("POST");
                            httpURLConnection.setRequestProperty("Content-Type", "application/json");
                            httpURLConnection.setRequestProperty("Authorization", svars.Service_token(act));

                            httpURLConnection.setDoOutput(true);

                            if (ssd.use_download_filter) {
                                //  httpURLConnection.setDoOutput(true);
                                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                                wr.writeBytes(filter_object[0].toString());
                                wr.flush();
                                wr.close();  /**/
                                Log.e(ssd.service_name + " :: TX =>", " " + filter_object[0].toString());

                            }
                            int status = httpURLConnection.getResponseCode();
                            Log.e(ssd.service_name + " :: RX", " status=> " + status);
                            // ssi.on_status_changed("Synchronizing");

                            try {
                                InputStream in = httpURLConnection.getInputStream();
                                // InputStreamReader inputStreamReader = new InputStreamReader(in);


                                data = new String(ByteStreams.toByteArray(in));


                                Log.e(ssd.service_name + " :: RX =>", " " + data);


                            } catch (Exception exx) {
                                InputStream error = httpURLConnection.getErrorStream();
                                InputStreamReader inputStreamReader2 = new InputStreamReader(error);

                                int inputStreamData2 = inputStreamReader2.read();
                                while (inputStreamData2 != -1) {
                                    char current = (char) inputStreamData2;
                                    inputStreamData2 = inputStreamReader2.read();
                                    error_data += current;
                                }
                                Log.e(ssd.service_name + " :: TX", "error => " + error_data);
                                ssi.on_api_error(ssd,error_data);
                            }
//                            maindata[0] = new JSONObject(data);
                            maindata[0] = Main_handler.OnDownloadedObject(ssd, filter_object[0], new JSONObject(data));
                            if (maindata[0] == null) {

                                sync_complete_counter++;
                                sync_success_counter++;
                                double denm = (double) sync_sum_counter;
                                ssi.on_status_changed("Synchronized " + ssd.service_name);

                                double num = (double) sync_complete_counter;
                                double per = (num / denm) * 100.0;
                                ssi.on_main_percentage_changed((int) per);
                                if (per == 100.0) {
                                    ssi.on_main_percentage_changed(100);
                                    ssi.on_status_changed("Synchronization complete");
                                    ssi.on_secondary_progress_changed(100);
                                    ssi.on_main_percentage_changed(100);
                                    ssi.on_info_updated("Synchronization complete");
                                    ssi.on_status_code_changed(3);
                                    ssi.onSynchronizationCompleted(ssd);
                                    ssi.onSynchronizationCompleted();
                                } else {
                                    ssi.onSynchronizationCompleted(ssd);

                                }
                                return;
                            }
                            data = null;
                            //  ssi.on_main_percentage_changed(0);

                            if (ssd.is_ok_position == null || (boolean) sdb.getJsonValue(ssd.is_ok_position, maindata[0])) {
//  if (maindata[0].getBoolean(app_config.SYNC_USE_CAPS?"IsOkay":"isOkay")) {


                                JSONArray temp_ar = (JSONArray) sdb.getJsonValue(ssd.download_array_position, maindata[0]);
//                                Object json = new JSONTokener(maindata[0].opt(app_config.SYNC_USE_CAPS?"Result":"result").toString()).nextValue();
//                                temp_ar = json instanceof JSONArray ? (JSONArray) json : ((JSONObject) json).getJSONArray(app_config.SYNC_USE_CAPS?"Result":"result");
                                temp_ar = new JSONArray(temp_ar.toString().replace("'", "''"));

                       /* if(ssd.service_name.equalsIgnoreCase("Member fingerprints")){
                            temp_ar = maindata.getJSONObject("result").getJSONArray("result");
                            // ((member_data)obj_class).data.storage_mode=2;
                        }else if(ssd.service_name.equalsIgnoreCase("Member images")){
                            //   temp_ar = maindata.getJSONObject("result").getJSONArray("result");
                            temp_ar = maindata.getJSONArray("Result");
                            // ((member_data)obj_class).data.storage_mode=2;
                        }else{
                            if(ssd.use_download_filter){
                                temp_ar = maindata.getJSONObject("Result").getJSONArray("Result");

                            }else{
                                temp_ar = maindata.getJSONArray("Result");
                            }
                        }*/

                                double den = (double) temp_ar.length();
                                // sdb.register_object_auto_ann(true,null,ssd);
                                if (!ssd.use_download_filter) {
                                    sdb.getDatabase().execSQL("DELETE FROM " + ssd.table_name + " WHERE sync_status ='" + sync_status.syned.ordinal() + "'");
                                }
                                Log.e(ssd.service_name + " :: RX", "IS OK " + den);
                                if (den >= 0) {
                                    if (ssd.storage_mode_check) {
                                        Log.e(logTag, "Started storage checking ...");

                                        for (int i = 0; i < temp_ar.length(); i++) {
                                            JSONObject jo = temp_ar.getJSONObject(i);
                                            Iterator keys = jo.keys();
                                            List<String> key_list = new ArrayList<>();
                                            while (keys.hasNext()) {
                                                key_list.add((String) keys.next());

                                            }

                                            for (String k : realm.getFilePathFields(ssd.object_package, key_list)) {
                                                try {
                                                    jo.put(k, DatabaseManager.save_doc(jo.getString(k)));
                                                } catch (Exception e) {
                                                    Log.e(logTag, "Base64 image error:" + e.getMessage());

                                                }

                                            }
                                        }
                                        Log.e(logTag, "Done storage checking ...");

                                    }
                                    synchronized (this) {
                                        String[][] ins = realm.getInsertStatementsFromJson(temp_ar, ssd.object_package);
                                        String sidz = ins[0][0];
                                        String sidz_inactive = ins[0][1];
                                        String[] qryz = ins[1];
                                        int q_length = qryz.length;
                                        temp_ar = null;
                                        //  while(dbh.database.inTransaction()){Log.e("Waiting .. ","In transaction ");}
                                        ssi.on_status_changed("Synchronizing " + ssd.service_name);

                                        DatabaseManager.database.beginTransaction();
                                        DatabaseManager.database.execSQL("INSERT INTO CP_" + ssd.table_name + " SELECT * FROM " + ssd.table_name + " WHERE sid in " + sidz + " AND sync_status=" + sync_status.pending.ordinal() + "");

                                        for (int i = 0; i < q_length; i++) {
                                            DatabaseManager.database.execSQL(qryz[i]);
                                            double num = (double) i + 1;
                                            double per = (num / q_length) * 100.0;
                                            ssi.on_secondary_progress_changed((int) per);
                                            //ssi.on_info_updated("Members :"+num+"/"+den+"    Total local members :"+sdb.employee_count());
                                            ssi.on_info_updated(ssd.service_name + " :" + num + "/" + q_length + "    Local data :" + Integer.parseInt(sdb.get_record_count(ssd.table_name, ssd.table_filters)));
                                        }

                                        DatabaseManager.database.execSQL("DELETE FROM " + ssd.table_name + " WHERE data_status='false'");
//                                         DatabaseManager.database.execSQL("DELETE FROM " + ssd.table_name + " WHERE sid IN("+sidz_inactive+")AND sync_status<>" + sync_status.pending.ordinal());
//                                         DatabaseManager.database.execSQL("DELETE FROM " + ssd.table_name + " WHERE sid IN("+DatabaseManager.conccat_sql_string(sidz_inactive)+")AND sync_status<>" + sync_status.pending.ordinal());
                                        DatabaseManager.database.execSQL("REPLACE INTO " + ssd.table_name + " SELECT * FROM CP_" + ssd.table_name + "");
                                        DatabaseManager.database.execSQL("DELETE FROM CP_" + ssd.table_name + "");
                                        DatabaseManager.database.setTransactionSuccessful();
                                        DatabaseManager.database.endTransaction();
                                    }
                                }

                                Log.e(ssd.service_name + " :: RX", " DONE ");


                                //   sdb.register_object_auto_ann(false,null,ssd);


                                sync_complete_counter++;
                                sync_success_counter++;
                                double denm = (double) sync_sum_counter;
                                ssi.on_status_changed("Synchronized " + ssd.service_name);

                                double num = (double) sync_complete_counter;
                                double per = (num / denm) * 100.0;
                                ssi.on_main_percentage_changed((int) per);


                                if (den >= ssd.chunk_size && ssd.use_download_filter) {
                                    sync_sum_counter++;
                                    download_(ssd);


                                } else {
                                    if (per == 100.0) {
                                        ssi.on_main_percentage_changed(100);
                                        ssi.on_status_changed("Synchronization complete");
                                        ssi.on_secondary_progress_changed(100);
                                        ssi.on_main_percentage_changed(100);
                                        ssi.on_info_updated("Synchronization complete");
                                        ssi.on_status_code_changed(3);
                                        ssi.onSynchronizationCompleted(ssd);
                                        ssi.onSynchronizationCompleted();
                                    } else {
                                        ssi.onSynchronizationCompleted(ssd);

                                    }

                                }
                                maindata[0] = null;
                                temp_ar = null;
                            }

                        } catch (Exception e) {
                            Log.e(ssd.service_name + ":: TX", " error => " + e.getMessage());
                            ssi.on_api_error(ssd,e.getMessage());
                            e.printStackTrace();
                        } finally {
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }
                        }

                        handler.removeCallbacks(this);
                        Looper.myLooper().quit();
                    }
                }, 50);

                Looper.loop();
            }
        };
        thread.start();


        // }.execute();

    }

    void download_configuration(sync_service_description ssd) {
        Log.e("SYNC ::  ", "Object table :" + ssd.table_name + "\n"
                + "Service name :" + ssd.service_name + "\n"
                + "Service type :" + ssd.servic_type.name() + "\n"
                + "download link :" + ssd.download_link + "\n");

        //  sync_sum_counter++;
        ssi.on_status_code_changed(2);
        ssi.on_status_changed(act.getResources().getString(R.string.synchronizing) + ssd.service_name + " ↓");
        JSONObject filter_object = ssd.use_download_filter ? generate_filter(ssd.table_name, ssd.chunk_size, ssd.table_filters) : new JSONObject();
        new AsyncTask() {
            JSONObject maindata;

            @Override
            protected Object doInBackground(Object[] params) {
                String data = "";
                String error_data = "";

                HttpURLConnection httpURLConnection = null;
                try {

                    httpURLConnection = (HttpURLConnection) new URL(params[0].toString()).openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Authorization", svars.Service_token(act));

                    httpURLConnection.setDoOutput(true);

                    if (ssd.use_download_filter) {
                        //  httpURLConnection.setDoOutput(true);
                        DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                        wr.writeBytes(params[1].toString());
                        wr.flush();
                        wr.close();  /**/
                    }
                    int status = httpURLConnection.getResponseCode();
                    Log.e(ssd.service_name + " :: RX", " status=> " + status);
                    // ssi.on_status_changed("Synchronizing");

                    try {
                        InputStream in = httpURLConnection.getInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(in);


                        data = new String(ByteStreams.toByteArray(in));
                        byte[] buffer = new byte[1024];
                        int len1 = 0;

                        // IOUtils.toString(inputStream, "UTF-8");
                        while ((len1 = in.read(buffer)) != -1) {
                            String curr = new String(buffer, "UTF-8");
                            data += curr;
                            Log.e(" POST RX =>", " " + curr);
                        }
                        Log.e(ssd.service_name + " :: RX =>", " " + data.substring(data.length() - 100000));


                    } catch (Exception exx) {
                        InputStream error = httpURLConnection.getErrorStream();
                        InputStreamReader inputStreamReader2 = new InputStreamReader(error);

                        int inputStreamData2 = inputStreamReader2.read();
                        while (inputStreamData2 != -1) {
                            char current = (char) inputStreamData2;
                            inputStreamData2 = inputStreamReader2.read();
                            error_data += current;
                        }
                        Log.e(ssd.service_name + " :: TX", "error => " + error_data);

                    }
                    maindata = new JSONObject(data);
                    data = null;
                    //  ssi.on_main_percentage_changed(0);

                    if (maindata.getBoolean("IsOkay")) {


                        sdb.database.execSQL("DELETE FROM dyna_data_table");
                        sdb.register_object(true, null, null, null);


                        for (dyna_data_obj.operatio_data_type od : dyna_data_obj.operatio_data_type.values()) {
                            if (od.name().equalsIgnoreCase(dyna_data_obj.operatio_data_type.Null.name())) {
                                continue;
                            }

                            try {
                                JSONArray objects_array = maindata.getJSONObject("Result").getJSONArray(od.name());
                                Log.e(ssd.service_name + " :: RX =>", " " + objects_array.toString());

                                for (int i = 0; i < objects_array.length(); i++) {
                                    double den = (double) objects_array.length();
                                    ssi.on_status_changed(act.getResources().getString(R.string.synchronizing) + od.name());
                                    JSONObject jj = objects_array.getJSONObject(i);
                                    sdb.register_dynadata(null, jj, od.ordinal() + "", null);
                                    double num = (double) i + 1;
                                    double per = (num / den) * 100.0;
                                    ssi.on_secondary_progress_changed((int) per);
                                    ssi.on_info_updated(jj.getString("Name") + " :" + num + "/" + den + "    " + act.getString(R.string.progress) + per + "%");

                                }
                                maindata.getJSONObject("Result").remove(od.name());
                                Log.e(ssd.service_name + " :: RX =>", " " + maindata.getJSONObject("Result").toString());

                            } catch (Exception ex) {
                                Log.e(ssd.service_name + " :: RX", "error => " + ex);

                            }
                        }


                        maindata = null;


                        sdb.register_object(false, null, "contb", null);


                        maindata = null;
                        sync_complete_counter++;
                        sync_success_counter++;
                        double denm = (double) sync_sum_counter;
                        ssi.on_status_changed(act.getString(R.string.syncd_global_data));

                        double num = (double) sync_complete_counter;
                        double per = (num / denm) * 100.0;
                        ssi.on_main_percentage_changed((int) per);
                        ssi.on_status_code_changed(4);


                        String time_now = svars.gett_time();

                        svars.set_sync_time(act, time_now);


                    }

                } catch (Exception e) {
                    Log.e(ssd.service_name + ":: TX", " error => " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }

                return data;
            }


        }.execute(ssd.download_link, filter_object);

    }

    void download_configuration_ann(sync_service_description ssd) {
        Log.e("SYNC ::  ", "Object table :" + ssd.table_name + "\n"
                + "Service name :" + ssd.service_name + "\n"
                + "Service type :" + ssd.servic_type.name() + "\n"
                + "download link :" + ssd.download_link + "\n");

        sync_sum_counter++;
        ssi.on_status_code_changed(2);
        ssi.on_status_changed(act.getResources().getString(R.string.synchronizing) + ssd.service_name + " ↓");
        JSONObject filter_object = ssd.use_download_filter ? generate_filter(ssd.table_name, ssd.chunk_size, ssd.table_filters) : new JSONObject();
        new AsyncTask() {
            JSONObject maindata;

            @Override
            protected Object doInBackground(Object[] params) {
                String data = "";
                String error_data = "";

                HttpURLConnection httpURLConnection = null;
                try {

                    httpURLConnection = (HttpURLConnection) new URL(params[0].toString()).openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Authorization", svars.Service_token(act));

                    httpURLConnection.setDoOutput(true);

                    if (ssd.use_download_filter) {
                        //  httpURLConnection.setDoOutput(true);
                        DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                        wr.writeBytes(params[1].toString());
                        wr.flush();
                        wr.close();  /**/
                    }
                    int status = httpURLConnection.getResponseCode();
                    Log.e(ssd.service_name + " :: RX", " status=> " + status);
                    // ssi.on_status_changed("Synchronizing");

                    try {
                        InputStream in = httpURLConnection.getInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(in);


                        data = new String(ByteStreams.toByteArray(in));
                        byte[] buffer = new byte[1024];
                        int len1 = 0;

                        // IOUtils.toString(inputStream, "UTF-8");
                        while ((len1 = in.read(buffer)) != -1) {
                            String curr = new String(buffer, "UTF-8");
                            data += curr;
                            Log.e(" POST RX =>", " " + curr);
                        }
                        Log.e(ssd.service_name + " :: RX =>", " " + data.substring(data.length() - 100000));


                    } catch (Exception exx) {
                        InputStream error = httpURLConnection.getErrorStream();
                        InputStreamReader inputStreamReader2 = new InputStreamReader(error);

                        int inputStreamData2 = inputStreamReader2.read();
                        while (inputStreamData2 != -1) {
                            char current = (char) inputStreamData2;
                            inputStreamData2 = inputStreamReader2.read();
                            error_data += current;
                        }
                        Log.e(ssd.service_name + " :: TX", "error => " + error_data);

                    }
                    maindata = new JSONObject(data);
                    data = null;
                    //  ssi.on_main_percentage_changed(0);

                    if (maindata.getBoolean("IsOkay")) {


                        sdb.database.execSQL("DELETE FROM dyna_data_table");
                        sdb.register_object(true, null, null, null);


                        for (dyna_data_obj.operatio_data_type od : dyna_data_obj.operatio_data_type.values()) {
                            if (od.name().equalsIgnoreCase(dyna_data_obj.operatio_data_type.Null.name())) {
                                continue;
                            }

                            try {
                                JSONArray objects_array = maindata.getJSONObject("Result").getJSONArray(od.name());
                                Log.e(ssd.service_name + " :: RX =>", " " + objects_array.toString());

                                for (int i = 0; i < objects_array.length(); i++) {
                                    double den = (double) objects_array.length();
                                    ssi.on_status_changed(act.getResources().getString(R.string.synchronizing) + od.name());
                                    JSONObject jj = objects_array.getJSONObject(i);
                                    sdb.register_dynadata(null, jj, od.ordinal() + "", null);
                                    double num = (double) i + 1;
                                    double per = (num / den) * 100.0;
                                    ssi.on_secondary_progress_changed((int) per);
                                    ssi.on_info_updated(jj.getString("Name") + " :" + num + "/" + den + "    " + act.getString(R.string.progress) + per + "%");

                                }
                                maindata.getJSONObject("Result").remove(od.name());
                                Log.e(ssd.service_name + " :: RX =>", " " + maindata.getJSONObject("Result").toString());

                            } catch (Exception ex) {
                                Log.e(ssd.service_name + " :: RX", "error => " + ex);

                            }
                        }


                        maindata = null;


                        sdb.register_object(false, null, "contb", null);


                        maindata = null;
                        sync_complete_counter++;
                        sync_success_counter++;
                        double denm = (double) sync_sum_counter;
                        ssi.on_status_changed(act.getString(R.string.syncd_global_data));

                        double num = (double) sync_complete_counter;
                        double per = (num / denm) * 100.0;
                        ssi.on_main_percentage_changed((int) per);
                        ssi.on_status_code_changed(4);


                        String time_now = svars.gett_time();

                        svars.set_sync_time(act, time_now);


                    }

                } catch (Exception e) {
                    Log.e(ssd.service_name + ":: TX", " error => " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }

                return data;
            }


        }.execute(app_config.APP_MAINLINK + ssd.download_link, filter_object);

    }

    @Deprecated
    public static void upload(sync_service_description ssd, Object obj_class) {
        Log.e("SYNC ::  ", "Object table :" + ssd.table_name + "\n"
                + "Service name :" + ssd.service_name + "\n"
                + "Service type :" + ssd.servic_type.name() + "\n"
                + "upload link :" + ssd.upload_link + "\n");

        sync_sum_counter++;
        ssi.on_status_code_changed(2);
        //  ssi.on_status_changed("Sparta sync");
        double denm2 = (double) sync_sum_counter;
        ssi.on_status_changed(act.getResources().getString(R.string.synchronizing) + ssd.service_name + " ↑");

        double num2 = (double) sync_complete_counter;
        double per2 = (num2 / denm2) * 100.0;
        ssi.on_main_percentage_changed((int) per2);

        String[] pending_records_filter = ssd.table_filters == null ? new String[1] : new String[ssd.table_filters.length + 1];
        if (ssd.table_filters != null) {
            System.arraycopy(ssd.table_filters, 0, pending_records_filter, 0, ssd.table_filters.length);

        }
        pending_records_filter[pending_records_filter.length - 1] = "sync_status='p'";
        // ArrayList<Object> pending_records=sdb.load_dynamic_records(obj_class,pending_records_filter);
        ArrayList<Object> pending_records = sdb.load_dynamic_records(ssd, pending_records_filter);

        final int[] upload_counter = {0};
        final int upload_length = pending_records.size();
        String table_name = ssd.table_name;
        if (pending_records.size() < 1) {
            Log.e(ssd.service_name + ":: upload::", "No records");
            sync_complete_counter++;
            sync_success_counter++;
            Log.e("sync", "Sync counter " + sync_complete_counter);
            double denm = (double) sync_sum_counter;
            ssi.on_status_changed("Synchronized local " + ssd.service_name);

            double num = (double) sync_complete_counter;
            double per = (num / denm) * 100.0;
            ssi.on_main_percentage_changed((int) per);
            if (per == 100.0) {
                ssi.on_main_percentage_changed(100);
                ssi.on_status_changed(act.getResources().getString(R.string.synchronization_complete));
                ssi.on_secondary_progress_changed(100);
                ssi.on_main_percentage_changed(100);
                ssi.on_info_updated(act.getResources().getString(R.string.synchronization_complete));
                ssi.on_status_code_changed(3);
            }
        } else {

            for (Object obj : pending_records) {

                if (ssd.service_name.equalsIgnoreCase("Member images") || ssd.service_name.equalsIgnoreCase("Member FP Images")) {


                    // ((member_data)obj).data.storage_mode=2;
                }
                if (ssd.service_name.equalsIgnoreCase("Member")) {


                }
                JSONObject upload_object = sdb.load_JSON_from_object(obj);
                Log.e(ssd.service_name + ":: upload::", " " + upload_object.toString());
                String lid = ((db_class_) obj).id;
                new AsyncTask() {
                    JSONObject maindata;

                    @Override
                    protected Object doInBackground(Object[] params) {

                        ssi.on_status_code_changed(2);
                        ssi.on_status_changed(act.getResources().getString(R.string.synchronizing) + ssd.service_name);

                        AndroidNetworking.post(ssd.upload_link)
                                .addHeaders("Authorization", svars.Service_token(act))
                                .addHeaders("content-type", "application/json")
                                .addHeaders("cache-control", "no-cache")
                                .addJSONObjectBody(upload_object)
                                .setTag(this)
                                .setPriority(Priority.MEDIUM)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.e("Response =>", "" + response.toString());

                                        try {


                                            if (response.getBoolean("IsOkay")) {


                                                ContentValues cv = new ContentValues();

                                                cv.put("sync_status", "i");
                                                cv.put("sid", response.getJSONObject("Result").getString("id"));

                                                sdb.database.update(table_name, cv, "id=" + lid, null);


                                                sync_complete_counter++;
                                                sync_success_counter++;
                                                Log.e(ssd.service_name + " ::", "Sync counter" + sync_complete_counter);
                                                double denm = (double) sync_sum_counter;
                                                int pending_data_count = Integer.parseInt(sdb.get_record_count(table_name, pending_records_filter));


                                                ssi.on_status_changed(pending_data_count == 0 ? act.getResources().getString(R.string.synchronized_local) + ssd.service_name : act.getResources().getString(R.string.synchronizing) + ssd.service_name + act.getResources().getString(R.string.pending_data) + pending_data_count);

                                                double num = (double) sync_complete_counter;
                                                double per = (num / denm) * 100.0;
                                                ssi.on_main_percentage_changed((int) per);
                                                if (per == 100.0) {
                                                    ssi.on_main_percentage_changed(100);
                                                    ssi.on_status_changed(act.getResources().getString(R.string.synchronization_complete));
                                                    ssi.on_secondary_progress_changed(100);
                                                    ssi.on_main_percentage_changed(100);
                                                    ssi.on_info_updated(act.getResources().getString(R.string.synchronization_complete));
                                                    ssi.on_status_code_changed(3);
                                                }
                                                upload_counter[0]++;
                                                if (upload_counter[0] == upload_length) {
                                                    upload(ssd, obj_class);
                                                }


                                            } else {
                                                ssi.on_status_changed("Update failed ...  =>" + lid);
                                                ssi.on_status_code_changed(666);
                                                String error = " " + upload_object.toString() + "\n" + response.toString();
                                                Log.e(ssd.service_name + ":: upload::error::", error);
                                                DatabaseManager.log_String(act, ssd.service_name + ":: upload::error::" + error);
                                            }
                                        } catch (Exception ex) {
                                            ssi.on_status_changed("Update failed ...  =>" + lid);
                                            ssi.on_status_code_changed(666);
                                            String error = " " + upload_object.toString() + "\n" + ex.getMessage();
                                            Log.e(ssd.service_name + ":: upload::error::", error);
                                            DatabaseManager.log_String(act, ssd.service_name + ":: upload::error::" + error);
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        Log.e("Response error=>", ssd.service_name + ":: upload =>" + anError.getErrorBody());
                                        ssi.on_status_code_changed(666);
                                        ContentValues cv = new ContentValues();

                                        cv.put("data_status", "e");

                                        sdb.database.update(table_name, cv, "id=" + lid, null);
                                        if (anError.getErrorBody().contains("Record Exist")) {
                                            ssi.on_status_changed("Synchronizing");


                                            cv = new ContentValues();
                                            cv.put("sync_status", "i");


                                            sdb.database.update(table_name, cv, "id=" + lid, null);

                                        }

                                    }
                                });


                        return "";
                    }


                }.execute();


            }
        }


    }


    public static void upload_(sync_service_description ssd) {
        Log.e("SYNC ::  ", "Object table :" + ssd.table_name + "\n"
                + "Service name :" + ssd.service_name + "\n"
                + "Service type :" + ssd.servic_type.name() + "\n"
                + "upload link :" + ssd.upload_link + "\n");

        //   sync_sum_counter++;
        ssi.on_status_code_changed(2);
        //  ssi.on_status_changed("Sparta sync");
        double denm2 = (double) sync_sum_counter;
        ssi.on_status_changed(act.getResources().getString(R.string.synchronizing) + ssd.service_name + " ↑");

        double num2 = (double) sync_complete_counter;
        double per2 = (num2 / denm2) * 100.0;
        ssi.on_main_percentage_changed((int) per2);

        String[] pending_records_filter = ssd.table_filters == null ? new String[1] : new String[ssd.table_filters.length + 1];
        if (ssd.table_filters != null) {
            System.arraycopy(ssd.table_filters, 0, pending_records_filter, 0, ssd.table_filters.length);

        }
        pending_records_filter[pending_records_filter.length - 1] = "sync_status='" + sync_status.pending.ordinal() + "'";
        // ArrayList<Object> pending_records=sdb.load_dynamic_records(obj_class,pending_records_filter);
        ArrayList<JSONObject> pending_records = Main_handler.OnAboutToUploadObjects(ssd, sdb.load_dynamic_json_records_ann(ssd, pending_records_filter));
        final int[] upload_counter = {0};
        final int upload_length = pending_records.size();
        String table_name = ssd.table_name;
        if (pending_records.size() < 1) {
            Log.e(ssd.service_name + ":: upload::", "No records");
            sync_complete_counter++;
            sync_success_counter++;
            Log.e("sync", "Sync counter " + sync_complete_counter);
            double denm = (double) sync_sum_counter;
            ssi.on_status_changed("Synchronized local " + ssd.service_name);

            double num = (double) sync_complete_counter;
            double per = (num / denm) * 100.0;
            ssi.on_main_percentage_changed((int) per);
            ssi.onSynchronizationCompleted(ssd);

            if (per == 100.0) {
                ssi.on_main_percentage_changed(100);
                ssi.on_status_changed(act.getResources().getString(R.string.synchronization_complete));
                ssi.on_secondary_progress_changed(100);
                ssi.on_main_percentage_changed(100);
                ssi.on_info_updated(act.getResources().getString(R.string.synchronization_complete));
                ssi.on_status_code_changed(3);
                ssi.onSynchronizationCompleted();

            }
        } else {

            for (JSONObject obj : pending_records) {
                obj = Main_handler.OnUploadingObject(ssd, obj);
                if (obj == null) {
                    continue;
                }
                if (ssd.storage_mode_check) {
                    Log.e(logTag, "Started storage checking ...");

                    Iterator keys = obj.keys();
                    List<String> key_list = new ArrayList<>();
                    while (keys.hasNext()) {
                        key_list.add((String) keys.next());

                    }


                    for (String k : realm.getFilePathFields(ssd.object_package, key_list)) {
                        try {
                            obj.put(k, DatabaseManager.get_saved_doc_base64(obj.getString(k)));
                        } catch (Exception e) {
                            Log.e(logTag, "Base64 conversion error:" + e.getMessage());

                        }


                    }
                }

                JSONObject upload_object = obj;
//                JSONObject upload_object = sdb.load_JSON_from_object(obj);
                Log.e(ssd.service_name + ":: upload::", " " + upload_object.toString());
                String lid = null;
                try {
                    lid = upload_object.getString("lid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String finalLid = lid;
                new AsyncTask() {
                    JSONObject maindata;

                    @Override
                    protected Object doInBackground(Object[] params) {

                        ssi.on_status_code_changed(2);
                        ssi.on_status_changed(act.getResources().getString(R.string.synchronizing) + ssd.service_name);

                        AndroidNetworking.post(app_config.APP_MAINLINK + ssd.upload_link)
                                .addHeaders("Authorization", svars.Service_token(act))
                                .addHeaders("content-type", "application/json")
                                .addHeaders("cache-control", "no-cache")
                                .addJSONObjectBody(upload_object)
                                .setTag(this)
                                .setPriority(Priority.MEDIUM)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.e("Response =>", "" + response.toString());
                                        Log.e(ssd.service_name + ":: upload response ::", " " + response.toString());

                                        response = Main_handler.OnUploadedObject(ssd, upload_object, response);
                                        if (response == null) {
                                            update_counter(ssd, pending_records_filter);
                                            upload_counter[0]++;
                                            if (upload_counter[0] == upload_length) {
                                                sync_sum_counter++;
                                                upload_(ssd);
                                            }
                                            return;
                                        }
                                        try {


                                            if (response.getBoolean(app_config.SYNC_USE_CAPS ? "IsOkay" : "isOkay")) {


                                                ContentValues cv = new ContentValues();

                                                cv.put("sync_status", sync_status.syned.ordinal());
                                                cv.put("sid", response.getJSONObject(app_config.SYNC_USE_CAPS ? "Result" : "result").getString("id"));
                                                sdb.database.rawExecSQL("DELETE FROM " + table_name + " WHERE sid='" + cv.get("sid") + "' AND _id<>" + finalLid);
                                                sdb.database.update(table_name, cv, "_id=" + finalLid, null);


                                                update_counter(ssd, pending_records_filter);
                                                upload_counter[0]++;
                                                if (upload_counter[0] == upload_length) {
                                                    sync_sum_counter++;
                                                    upload_(ssd);
                                                }

                                            } else {
                                                ssi.on_api_error(ssd,response.toString());
                                                ContentValues cv = new ContentValues();
                                                cv.put("data_status", "e");

                                                sdb.database.update(table_name, cv, "id=" + finalLid, null);
                                                ssi.on_status_changed("Update failed ...  =>" + finalLid);
                                                ssi.on_status_code_changed(666);
                                                String error = " " + upload_object.toString() + "\n" + response.toString();
                                                Log.e(ssd.service_name + ":: upload::error::", error);
                                                sdb.log_String(act, ssd.service_name + ":: upload::error::" + error);
                                            }
                                        } catch (Exception ex) {
                                            ssi.on_api_error(ssd,response.toString());
                                            ssi.on_status_changed("Update failed ...  =>" + finalLid);
                                            ssi.on_status_code_changed(666);
                                            String error = " " + upload_object.toString() + "\n" + ex.getMessage();
                                            Log.e(ssd.service_name + ":: upload::error::", error);
                                            sdb.log_String(act, ssd.service_name + ":: upload::error::" + error);
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        Log.e("Response error=>", ssd.service_name + ":: upload =>" + anError.getErrorBody());
                                        ssi.on_api_error(ssd,anError.getErrorBody());
                                        sdb.log_String(act, ssd.service_name + ":: upload::error::" + anError.getErrorBody());
                                        anError = Main_handler.OnUploadedObjectError(ssd, upload_object, anError);
                                        if (anError == null) {
                                            update_counter(ssd, pending_records_filter);
                                            upload_counter[0]++;
                                            if (upload_counter[0] == upload_length) {
                                                sync_sum_counter++;
                                                upload_(ssd);
                                            }
                                            return;
                                        }

                                        ContentValues cv = new ContentValues();
                                        if (anError.getErrorBody().contains("Record Exist")) {
                                            ssi.on_status_changed("Synchronizing");


                                            cv = new ContentValues();
                                            cv.put("sync_status", sync_status.syned.ordinal());


                                            sdb.database.update(table_name, cv, "id=" + finalLid, null);
                                            update_counter(ssd, pending_records_filter);
                                            upload_counter[0]++;
                                            if (upload_counter[0] == upload_length) {
                                                sync_sum_counter++;
                                                upload_(ssd);
                                            }
                                        } else {
                                            ssi.on_status_code_changed(666);

                                            cv.put("data_status", "e");

                                            sdb.database.update(table_name, cv, "id=" + finalLid, null);

                                        }

                                    }
                                });


                        return "";
                    }


                }.execute();


            }
        }


    }

    static void update_counter(sync_service_description ssd, String[] pending_records_filter) {
        sync_complete_counter++;
        sync_success_counter++;
        Log.e(ssd.service_name + " ::", "Sync counter" + sync_complete_counter);
        double denm = (double) sync_sum_counter;
        int pending_data_count = Integer.parseInt(sdb.get_record_count(ssd.table_name, pending_records_filter));


        ssi.on_status_changed(pending_data_count == 0 ? act.getResources().getString(R.string.synchronized_local) + ssd.service_name : act.getResources().getString(R.string.synchronizing) + ssd.service_name + act.getResources().getString(R.string.pending_data) + pending_data_count);

        double num = (double) sync_complete_counter;
        double per = (num / denm) * 100.0;
        ssi.on_main_percentage_changed((int) per);
        if (per == 100.0) {
            ssi.on_main_percentage_changed(100);
            ssi.on_status_changed(act.getResources().getString(R.string.synchronization_complete));
            ssi.on_secondary_progress_changed(100);
            ssi.on_main_percentage_changed(100);
            ssi.on_info_updated(act.getResources().getString(R.string.synchronization_complete));
            ssi.on_status_code_changed(3);
        }

    }

    public static void upload_2(sync_service_description ssd) {
        Log.e("SYNC ::  ", "Object table :" + ssd.table_name + "\n"
                + "Service name :" + ssd.service_name + "\n"
                + "Service type :" + ssd.servic_type.name() + "\n"
                + "upload link :" + ssd.upload_link + "\n");

        sync_sum_counter++;
        ssi.on_status_code_changed(2);
        //  ssi.on_status_changed("Sparta sync");
        double denm2 = (double) sync_sum_counter;
        ssi.on_status_changed(act.getResources().getString(R.string.synchronizing) + ssd.service_name + " ↑");

        double num2 = (double) sync_complete_counter;
        double per2 = (num2 / denm2) * 100.0;
        ssi.on_main_percentage_changed((int) per2);

        String[] pending_records_filter = ssd.table_filters == null ? new String[1] : new String[ssd.table_filters.length + 1];
        if (ssd.table_filters != null) {
            System.arraycopy(ssd.table_filters, 0, pending_records_filter, 0, ssd.table_filters.length);

        }
        pending_records_filter[pending_records_filter.length - 1] = "sync_status='" + sync_status.pending.ordinal() + "'";
        // ArrayList<Object> pending_records=sdb.load_dynamic_records(obj_class,pending_records_filter);
        ArrayList<Object> pending_records = sdb.load_dynamic_records_ann(ssd, pending_records_filter);

        final int[] upload_counter = {0};
        final int upload_length = pending_records.size();
        String table_name = ssd.table_name;
        if (pending_records.size() < 1) {
            Log.e(ssd.service_name + ":: upload::", "No records");
            sync_complete_counter++;
            sync_success_counter++;
            Log.e("sync", "Sync counter " + sync_complete_counter);
            double denm = (double) sync_sum_counter;
            ssi.on_status_changed("Synchronized local " + ssd.service_name);

            double num = (double) sync_complete_counter;
            double per = (num / denm) * 100.0;
            ssi.on_main_percentage_changed((int) per);
            if (per == 100.0) {
                ssi.on_main_percentage_changed(100);
                ssi.on_status_changed(act.getResources().getString(R.string.synchronization_complete));
                ssi.on_secondary_progress_changed(100);
                ssi.on_main_percentage_changed(100);
                ssi.on_info_updated(act.getResources().getString(R.string.synchronization_complete));
                ssi.on_status_code_changed(3);
            }
        } else {

            for (Object obj : pending_records) {

                if (ssd.service_name.equalsIgnoreCase("Member images") || ssd.service_name.equalsIgnoreCase("Member FP Images")) {


                    // ((member_data)obj).data.storage_mode=2;
                }
                if (ssd.service_name.equalsIgnoreCase("Member")) {


                }
                JSONObject upload_object = sdb.load_JSON_from_object(obj);
                Log.e(ssd.service_name + ":: upload::", " " + upload_object.toString());
                String lid = ((db_class_) obj).id;
                new AsyncTask() {
                    JSONObject maindata;

                    @Override
                    protected Object doInBackground(Object[] params) {

                        ssi.on_status_code_changed(2);
                        ssi.on_status_changed(act.getResources().getString(R.string.synchronizing) + ssd.service_name);

                        AndroidNetworking.post(app_config.APP_MAINLINK + ssd.upload_link)
                                .addHeaders("Authorization", svars.Service_token(act))
                                .addHeaders("content-type", "application/json")
                                .addHeaders("cache-control", "no-cache")
                                .addJSONObjectBody(upload_object)
                                .setTag(this)
                                .setPriority(Priority.MEDIUM)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.e("Response =>", "" + response.toString());

                                        try {


                                            if (response.getBoolean("IsOkay")) {


                                                ContentValues cv = new ContentValues();

                                                cv.put("sync_status", sync_status.syned.ordinal());
                                                cv.put("sid", response.getJSONObject("Result").getString("id"));

                                                sdb.database.update(table_name, cv, "id=" + lid, null);


                                                sync_complete_counter++;
                                                sync_success_counter++;
                                                Log.e(ssd.service_name + " ::", "Sync counter" + sync_complete_counter);
                                                double denm = (double) sync_sum_counter;
                                                int pending_data_count = Integer.parseInt(new DatabaseManager(act).get_record_count(table_name, pending_records_filter));


                                                ssi.on_status_changed(pending_data_count == 0 ? act.getResources().getString(R.string.synchronized_local) + ssd.service_name : act.getResources().getString(R.string.synchronizing) + ssd.service_name + act.getResources().getString(R.string.pending_data) + pending_data_count);

                                                double num = (double) sync_complete_counter;
                                                double per = (num / denm) * 100.0;
                                                ssi.on_main_percentage_changed((int) per);
                                                if (per == 100.0) {
                                                    ssi.on_main_percentage_changed(100);
                                                    ssi.on_status_changed(act.getResources().getString(R.string.synchronization_complete));
                                                    ssi.on_secondary_progress_changed(100);
                                                    ssi.on_main_percentage_changed(100);
                                                    ssi.on_info_updated(act.getResources().getString(R.string.synchronization_complete));
                                                    ssi.on_status_code_changed(3);
                                                }
                                                upload_counter[0]++;
                                                if (upload_counter[0] == upload_length) {
                                                    upload_(ssd);
                                                }


                                            } else {
                                                ssi.on_status_changed("Update failed ...  =>" + lid);
                                                ssi.on_status_code_changed(666);
                                                String error = " " + upload_object.toString() + "\n" + response.toString();
                                                Log.e(ssd.service_name + ":: upload::error::", error);
                                                DatabaseManager.log_String(act, ssd.service_name + ":: upload::error::" + error);
                                            }
                                        } catch (Exception ex) {
                                            ssi.on_status_changed("Update failed ...  =>" + lid);
                                            ssi.on_status_code_changed(666);
                                            String error = " " + upload_object.toString() + "\n" + ex.getMessage();
                                            Log.e(ssd.service_name + ":: upload::error::", error);
                                            DatabaseManager.log_String(act, ssd.service_name + ":: upload::error::" + error);
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        Log.e("Response error=>", ssd.service_name + ":: upload =>" + anError.getErrorBody());
                                        ssi.on_status_code_changed(666);
                                        ContentValues cv = new ContentValues();

                                        cv.put("data_status", "e");

                                        sdb.database.update(table_name, cv, "id=" + lid, null);
                                        if (anError.getErrorBody().contains("Record Exist")) {
                                            ssi.on_status_changed("Synchronizing");

                                            DatabaseManager sd = new DatabaseManager(act);
                                            cv = new ContentValues();
                                            cv.put("sync_status", sync_status.syned.ordinal());


                                            sdb.database.update(table_name, cv, "id=" + lid, null);

                                        }

                                    }
                                });


                        return "";
                    }


                }.execute();


            }
        }


    }

    static JSONObject generate_filter(String table_name, int chunk_size, String[] table_filters) {
        JSONObject postData = new JSONObject();
        JSONObject filter = new JSONObject();
        JSONArray filters = new JSONArray();


        JSONObject fiteritem = new JSONObject();
        try {
            fiteritem.put("field", "datecomparer");

            fiteritem.put("operator", "gt");
            fiteritem.put("value", "" + sdb.greatest_sync_var(table_name, table_filters));
            //   fiteritem.put("value","0");

            filters.put(fiteritem);

            filter.put("filters", filters);
            filter.put("logic", "AND");
            postData.put("filter", filter);

            postData.put("take", chunk_size);
            postData.put("skip", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;

    }

    public static Object getJsonValue(String pos, JSONObject jo) {
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

    void renew_token() {
        ssi.on_status_changed(act.getString(R.string.authenticating));
        final JSONObject JO = new JSONObject();

        JSONObject user = new JSONObject();
        try {
            SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);


            user.put("PassWord", prefs.getString("pass", ""));
            user.put("UserName", prefs.getString("username", ""));
            user.put("Branch", app_config.ACCOUNT_BRANCH);
            user.put("AccountName", app_config.ACCOUNT);
            user.put("Language", "English");


            JO.put("IsRenewalPasswordRequest", "false");
            JO.put("CurrentUser", user);
        } catch (Exception ex) {
        }
        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                final JSONObject[] maindata = {new JSONObject()};

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        if (svars.isInternetAvailable()) {
                            String response = "";
                            String error_data = "";
                            HttpURLConnection httpURLConnection = null;

                            try {
                                Log.e("JSON ST PG =>", "" + svars.login_url);
                                Log.e("LOGIN TX =>", "" + JO.toString());
//                                httpURLConnection = (HttpURLConnection) new URL(current_app_config(act).APP_MAINLINK+"/SystemAccounts/Authentication/Login/Submit").openConnection();
                                httpURLConnection = (HttpURLConnection) new URL(app_config.APP_MAINLINK + app_config.AUTHENTICATION_URL).openConnection();
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                                httpURLConnection.setDoOutput(true);

                                Log.e("LOGIN TX =>", "Connected");
//ByteStreams.copy(JO.toString().getBytes(),httpURLConnection.getOutputStream());
                                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                                wr.write(JO.toString().getBytes());
                                wr.flush();
                                wr.close();
                                int status = httpURLConnection.getResponseCode();
                                Log.e("LOGIN POST RX", " status=> " + status);


                                try {
                                    InputStream in = httpURLConnection.getInputStream();
                                    response = new String(ByteStreams.toByteArray(in));
                                    Log.e("LOGIN POST RX", " => " + response);

                                    maindata[0] = Main_handler.OnAuthenticated(httpURLConnection.getHeaderField("authorization"), new JSONObject(response));
//                                    maindata[0] = new JSONObject(response);
                                    JSONObject RESULT = maindata[0].getJSONObject(app_config.SYNC_USE_CAPS ? "Result" : "result");


                                    if (RESULT.getBoolean(app_config.SYNC_USE_CAPS ? "IsOkay" : "isOkay")) {

                                        svars.set_Service_token(act, httpURLConnection.getHeaderField("authorization"));
                                        ssi.on_status_changed(act.getString(R.string.authenticated));
                                        ssi.on_status_code_changed(1);

                                        launch_services();


                                    } else {
                                        ssi.on_status_changed(act.getString(R.string.authentication_error));
                                        ssi.on_status_code_changed(666);
                                        ssi.on_status_code_changed(4);
                                        sdb.database.execSQL("DELETE FROM user_table WHERE sid ='" + svars.user_id(act) + "'");

                                        //   sdb.logout_user();
                                        android.os.Process.killProcess(android.os.Process.myPid());

                                    }
                                    Log.e("JSON REC =>", "" + response);


                                    Log.e("JSON TOKEN =>", "" + httpURLConnection.getHeaderField("authorization"));
                                } catch (Exception ex) {
                                    InputStream error = httpURLConnection.getErrorStream();
                                    InputStreamReader inputStreamReader2 = new InputStreamReader(error);

                                    int inputStreamData2 = inputStreamReader2.read();
                                    while (inputStreamData2 != -1) {
                                        char current = (char) inputStreamData2;
                                        inputStreamData2 = inputStreamReader2.read();
                                        error_data += current;
                                    }
                                    ssi.on_status_changed(act.getString(R.string.authentication_error));
                                    ssi.on_status_code_changed(666);
                                    ssi.on_status_code_changed(4);

                                    Log.e("REG POST TX error =>", " " + error_data);
                                    Log.e("JSON POST TX error =>", " " + error_data);
                                    //JSONObject jo = new JSONObject(error_data);
                                }


                            } catch (final Exception e) {

                                Log.e("Creds enquiry error", "" + e.getMessage());
                                ssi.on_status_changed(act.getString(R.string.authentication_error));
                                ssi.on_status_code_changed(666);
                                ssi.on_status_code_changed(4);
                            }
                        } else {


                            ssi.on_status_changed(act.getString(R.string.internet_connection_error));
                            ssi.on_status_code_changed(4);
                            ssi.on_status_code_changed(666);

                        }
                    }
                }, 10);

                Looper.loop();
            }
        };
        thread.start();


    }


    void renew_token_() {
        ssi.on_status_changed(act.getString(R.string.authenticating));
        final JSONObject JO = new JSONObject();

        JSONObject user = new JSONObject();
        try {
            SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);


            user.put("PassWord", prefs.getString("pass", ""));
            user.put("UserName", prefs.getString("username", ""));
            user.put("Branch", current_app_config(Realm.context).ACCOUNT_BRANCH);
            user.put("AccountName", current_app_config(Realm.context).ACCOUNT);
            user.put("Language", "English");


            JO.put("IsRenewalPasswordRequest", "false");
            JO.put("CurrentUser", user);
        } catch (Exception ex) {
        }
        new AsyncTask() {
            JSONObject maindata;

            @Override
            protected Object doInBackground(Object[] params) {
                if (svars.isInternetAvailable()) {
                    String response = "";
                    String error_data = "";
                    HttpURLConnection httpURLConnection = null;

                    try {
                        Log.e("JSON ST PG =>", "" + params[0].toString());
                        Log.e("LOGIN TX =>", "" + JO.toString());
                        httpURLConnection = (HttpURLConnection) new URL(params[0].toString()).openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");

                        httpURLConnection.setDoOutput(true);

                        Log.e("LOGIN TX =>", "Connected");

                        DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                        wr.writeBytes(JO.toString());
                        wr.flush();
                        wr.close();
                        int status = httpURLConnection.getResponseCode();
                        Log.e("LOGIN POST RX", " status=> " + status);


                        try {
                            InputStream in = httpURLConnection.getInputStream();
                            response = new String(ByteStreams.toByteArray(in));
                            Log.e("LOGIN POST RX", " => " + response);

                            maindata = new JSONObject(response);
                            JSONObject RESULT = maindata.getJSONObject("Result");


                            if (RESULT.getBoolean("IsOkay")) {

                                svars.set_Service_token(act, httpURLConnection.getHeaderField("authorization"));
                                ssi.on_status_changed(act.getString(R.string.authenticated));
                                ssi.on_status_code_changed(1);

                                launch_services();


                            } else {
                                ssi.on_status_changed(act.getString(R.string.authentication_error));
                                ssi.on_status_code_changed(666);
                                ssi.on_status_code_changed(4);
                                sdb.database.execSQL("DELETE FROM user_table WHERE sid ='" + svars.user_id(act) + "'");

                                sdb.logout_user();
                                android.os.Process.killProcess(android.os.Process.myPid());

                            }
                            Log.e("JSON REC =>", "" + response);


                            Log.e("JSON TOKEN =>", "" + httpURLConnection.getHeaderField("authorization"));
                        } catch (Exception ex) {
                            InputStream error = httpURLConnection.getErrorStream();
                            InputStreamReader inputStreamReader2 = new InputStreamReader(error);

                            int inputStreamData2 = inputStreamReader2.read();
                            while (inputStreamData2 != -1) {
                                char current = (char) inputStreamData2;
                                inputStreamData2 = inputStreamReader2.read();
                                error_data += current;
                            }
                            ssi.on_status_changed(act.getString(R.string.authentication_error));
                            ssi.on_status_code_changed(666);
                            ssi.on_status_code_changed(4);

                            Log.e("REG POST TX error =>", " " + error_data);
                            Log.e("JSON POST TX error =>", " " + error_data);
                            //JSONObject jo = new JSONObject(error_data);
                        }


                    } catch (final Exception e) {

                        Log.e("Creds enquiry error", "" + e.getMessage());
                        ssi.on_status_changed(act.getString(R.string.authentication_error));
                        ssi.on_status_code_changed(666);
                        ssi.on_status_code_changed(4);
                    }
                } else {
//                    act.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(act, act.getString(R.string.unable_to_do_back_sync), Toast.LENGTH_LONG).show();
//                            pd.setMessage(act.getString(R.string.internet_connection_error));
//                          //  pd.show();
//                        }
//                    });
                    ssi.on_status_changed(act.getString(R.string.internet_connection_error));
                    ssi.on_status_code_changed(4);
                    ssi.on_status_code_changed(666);

                }


                return maindata;
            }


        }.execute(svars.login_url);
    }

    void load_users() {

        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                final JSONObject[] maindata = {new JSONObject()};

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // Do your web calls here

                        if (svars.isInternetAvailable()) {
                            String response = "";
                            String error_data = "";
                            HttpURLConnection httpURLConnection = null;

                            try {
                                Log.e("JSON ST PG =>", "" + svars.user_request_url + svars.device_code(act));

                                httpURLConnection = (HttpURLConnection) new URL(svars.user_request_url + svars.device_code(act)).openConnection();
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                                httpURLConnection.setDoOutput(true);


                                try {
                                    InputStream in = httpURLConnection.getInputStream();
                                    InputStreamReader inputStreamReader = new InputStreamReader(in);

                                    int inputStreamData = inputStreamReader.read();

                                    while (inputStreamData != -1) {
                                        char current = (char) inputStreamData;
                                        inputStreamData = inputStreamReader.read();
                                        response += current;
                                    }
                                    Log.e("JSON REC =>", "" + response);
                                    svars.set_Service_token(act, httpURLConnection.getHeaderField("authorization"));

                                } catch (Exception ex) {
                                    InputStream error = httpURLConnection.getErrorStream();
                                    InputStreamReader inputStreamReader2 = new InputStreamReader(error);

                                    int inputStreamData2 = inputStreamReader2.read();
                                    while (inputStreamData2 != -1) {
                                        char current = (char) inputStreamData2;
                                        inputStreamData2 = inputStreamReader2.read();
                                        error_data += current;
                                    }

                                    //    Log.e("REG POST TX error =>", " " + error_data);
                                    Log.e("JSON POST TX error =>", " strm :" + error_data);
                                    JSONObject jo = new JSONObject(error_data);
                                }

 /*

        {"$id":"1","Result":{"$id":"2","IsOkay":true,"Message":"Login Successfull","Result":{"$id":"3","employees":[],"logs_tbl":[],"lpo_details":[],"member_visit":[],"permissions":[],"sent_sms":[],"sub_accounts":null,"system_transaction":[{"$id":"4","id":3278,"transaction_date":"2018-02-26T14:16:19.3451725+03:00","transaction_no":"LOGIN20182261416193451","transaction_types_id":1,"user_id":1,"initiating_company_module_id":47,"advance_applications":[],"api_payment_transaction_summary":[],"company_modules":{"$id":"5","module":{"$id":"6","company_modules":[{"$ref":"5"}],"company_modules1":[{"$ref":"5"}],"control_set":[],"control_set1":[],"module_id":34,"module_name":"Login Module","is_default":true,"url":"NA"},"sub_accounts":null,"module1":{"$ref":"6"},"sub_accounts1":null,"company_transaction_stages":[],"company_transaction_stages1":[],"system_transaction_details":[],"system_transaction":[{"$ref":"4"}],"id":47,"module_id":34,"account_id":1},"farmers_input_orders":[],"input_assignment_tobranches":[],"inventories":[],"lpo_details":[],"lpo_details1":[],"member_account_details":[],"member_nfc":[],"members":[],"sent_sms":[],"supplier_input_member":[],"system_transaction_details":[],"transaction_types":{"$id":"7","system_transaction":[{"$ref":"4"}],"id":1,"type":"Insert"},"user":{"$ref":"3"},"user_code":[{"$id":"8","id":9091,"user_id":1,"secret_code":"5bewBeM2lpQ=","ip_address":"197.248.17.30","was_login_successfull":true,"sync_datetime":"2018-02-26T14:16:19.3383367+03:00","system_transaction_id":3278,"system_transaction":{"$ref":"4"},"user":{"$ref":"3"}}],"wetcu_stores":[]}],"system_transaction_details":[],"task_assignment":[],"user_branches":[],"user_code":[{"$ref":"8"}],"user_languages":[],"user_pay_modes":[],"user_system_transaction_rights":[],"weighbridges":[],"weighbridges1":[],"worker_task":[],"id":1,"username_edt":"demo","password":"9adrT0M0dQY=","account_id":1,"status_id":true,"email_address":"test@gmail.com","phone_number":"0717385272","use_sms":false,"use_mail":false,"use_code":false,"password_duration":31,"user_image_path":"/Images/UserProfile/ProfilePictures/1_eric_Koala.jpg"}},"ModuleName":"Login Module"}


          */
                                maindata[0] = new JSONObject(response);


                                if (maindata[0].getBoolean("IsOkay")) {
                                    final JSONObject RESULT = maindata[0].getJSONArray("Result").getJSONObject(0);
                                    final JSONArray USERS = RESULT.getJSONArray("UsersList");
                                    //  final JSONArray AgenceList=RESULT.getJSONArray("AgenceList");
                                    final JSONArray DeviceDetails = RESULT.getJSONArray("DeviceDetails");
                                    Log.e("dev details :", "" + DeviceDetails.toString());
                                    try {
                                        svars.set_device_id(act, DeviceDetails.getJSONObject(0).getString("id"));
                                        svars.set_device_name(act, DeviceDetails.getJSONObject(0).getString("tablet_code"));
                                        //svars.set_consula(act, DeviceDetails.getJSONObject(0).getString("consulate_code"));
                                        svars.set_site_name(act, DeviceDetails.getJSONObject(0).getString("device_name"));
                                    } catch (Exception ex) {
                                    }


                                    sdb.database.execSQL("DELETE FROM user_table");


                                    for (int i = 0; i < USERS.length(); i++) {
                                        JSONObject US = USERS.getJSONObject(i);
                                        //  dyna_data_obj objj=new dyna_data_obj("",US.getString("id"),"","mm",US.getString("code")+"-"+US.getString("full_name") +" "+US.getString("last_name"),"",US.getString("code") );
                                        //   objj.data_2.value=US.getBoolean("changed_password")?act.getString(R.string.active):act.getString(R.string.inactive);

                                        //  sdb.register_user(US.getString("code")+"-"+US.getString("full_name") +" "+US.getString("last_name"),US.getString("code"), US.getString("password"),US.getString("id"),US.getBoolean("changed_password")?"1":"0");


                                    }


                                } else {


                                }
                            } catch (final Exception e) {


                                Log.e("Creds enquiry error", "" + e.getMessage());
                            }
                        } else {


                        }

                        handler.removeCallbacks(this);
                        Looper.myLooper().quit();
                    }
                }, 2000);

                Looper.loop();
            }
        };
        thread.start();
    }

    void check_for_for_updates() {

        new AsyncTask() {
            JSONObject maindata;

            @Override
            protected Object doInBackground(Object[] params) {
                String data = "";
                String error_data = "";

                HttpURLConnection httpURLConnection = null;
                try {

                    httpURLConnection = (HttpURLConnection) new URL(params[0].toString()).openConnection();
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
                        maindata = new JSONObject(data);
                        data = null;
                        String time_now = svars.gett_time();

                        svars.set_sync_time(act, time_now);

                        Cursor c = new DatabaseManager(act).database.rawQuery("SELECT CURRENT_TIMESTAMP", null);
                        if (c.moveToFirst()) {
                            do {
                                Log.e("nowdate =>", " " + c.getString(0));
                                svars.version_check_time(act, c.getString(0));
                            } while (c.moveToNext());
                        }
                        for (int i = 0; i < maindata.getJSONArray("versions").length(); i++) {
                            JSONObject jj = maindata.getJSONArray("versions").getJSONObject(i);

                            if (jj.getString("status").equalsIgnoreCase("1")) {
                                if (!svars.current_version().equalsIgnoreCase(jj.getString("version_name")) /*| BuildConfig.VERSION_CODE != Integer.parseInt(jj.getString("version_code"))*/) {
//                                            act.runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    Toast.makeText(act, act.getString(R.string.app_name)+" is out of date", Toast.LENGTH_LONG).show();
//                                                    downld();
//                                                    svars.uptodate(act, false);
//
//                                                }
//                                            });
                                } else {
                                    svars.uptodate(act, true);

                                }
                            }
                        }


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
                        //   ssi.on_status_code_changed(3);

                    }


                } catch (Exception e) {
                    Log.e("version POST TX", "External error => " + e.getMessage());
                    e.printStackTrace();
                    // ssi.on_status_code_changed(3);
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }

                return data;
            }


        }.execute(current_app_config(Realm.context).APP_CONTROLL_MAIN_LINK);

    }


}