package sparta.realm.Services;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import sparta.realm.BuildConfig;


import sparta.realm.R;
import sparta.realm.Realm;
import sparta.realm.spartamodels.dyna_data_obj;


import sparta.realm.spartautils.svars;
import sparta.realm.utils.AppConfig;


import com.realm.annotations.sync_service_description;
import com.realm.annotations.sync_status;

import static sparta.realm.Realm.realm;
import static sparta.realm.spartautils.svars.current_app_config;


public class SynchronizationManager {
    public static String logTag = "SynchronizationManager";
    static Context act;
    String st = "";
    Timer logintimer = new Timer();
    public static Timer synchronizationTimer = null;


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

    public SynchronizationManager() {

        app_config =app_config==null? current_app_config(act):app_config;

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

    public SynchronizationManager(SynchronizationStatusHandler ssi) {


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
        if (synchronizationTimer != null) {
            return;
        }
        long periodSinceLastSync = sdb.sync_period();
        synchronizationTimer = new Timer();
        synchronizationTimer.schedule(new TimerTask() {

                                          public void run() {
                                              Log.e(logTag, "Initializing sync. Period since last sync : " + sdb.sync_period() + "mins");
                                              if (sdb.sync_period() >= (svars.sync_interval_mins(act) - 1) || sdb.sync_period() < 0) {

                                                  if (svars.background_sync(act)) {
                                                      sync_now();

                                                  }

                                              }


                                          }
                                      }, (svars.sync_interval_mins(act) - periodSinceLastSync) < 0 ? 0 : (svars.sync_interval_mins(act) - periodSinceLastSync) > svars.sync_interval_mins(act) ? svars.sync_interval_mins(act) * 60000 : (svars.sync_interval_mins(act) - periodSinceLastSync) * 60000
                , svars.sync_interval_mins(act) * 60000);

    }


    public void sync_now() {
        if (sdb.sync_period() >= 1) {
            ssi.on_status_code_changed(1);
            renew_token();
            //   check_for_for_updates();

        }
    }

    static int sync_sum_counter = 0;
    static int sync_complete_counter = 0;
    static int sync_success_counter = 0;

    public static void resetSyncCounters() {

        sync_sum_counter = 0;
        sync_success_counter = 0;
        sync_complete_counter = 0;
    }

    void launch_services() {

        ssi.onSynchronizationBegun();
        resetSyncCounters();
        try {
            setupSyncActionAnn();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(logTag, "Sync error: " + e.getMessage());
        }


    }

    public void set_sync(boolean sync) {

        svars.set_shared_pref_boolean(act, svars.shared_prefs_booleans.should_sync, sync);
    }


    /**
     * Sets up synchronization based on  data from mappery of annotated data which is pre-reflected at pre build
     * No runtime reflection
     */
    void setupSyncActionAnn_() {
        Stopwatch sw = new Stopwatch();
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

    /**
     * Sets up synchronization based on  data from mappery of annotated data which is pre-reflected at pre build
     * No runtime reflection
     */
    void setupSyncActionAnn() {
        Stopwatch sw = new Stopwatch();
        sw.start();
        try {
            List<sync_service_description> sync_services = realm.getSyncDescription();
            List<sync_service_description> download_sync_services = new ArrayList<>();
            sync_sum_counter = sync_services.size();

            for (sync_service_description ssd_t : sync_services) {
                if (ssd_t == null) {
                    continue;
                }
                switch (ssd_t.servic_type) {
                    case Download:
                        download_sync_services.add(ssd_t);
                        break;
                    case Upload:
                        upload_(ssd_t);

                        break;
                    case Download_Upload:
                        upload_(ssd_t);
                        download_sync_services.add(ssd_t);
                        break;
                }
            }
            for (sync_service_description ssd_t : download_sync_services) {
                download_(ssd_t);
            }
        } catch (Exception ex) {
            Log.e(logTag, "" + ex.getMessage());
        }

        sw.stop();
        Log.e(logTag, "Ann reflection time:" + sw.elapsed(TimeUnit.MILLISECONDS) + " mills");
    }


    public static void download_(sync_service_description ssd) {
        app_config =app_config==null? current_app_config(act):app_config;

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
                                ssi.on_api_error(ssd, error_data);
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

                                JSONArray temp_ar = (JSONArray) sdb.getJsonValue(ssd.download_array_position, maindata[0]);
                                temp_ar = new JSONArray(temp_ar.toString().replace("'", "''"));
                                double den = (double) temp_ar.length();
                                if (!ssd.use_download_filter) {
                                    sdb.getDatabase().execSQL("DELETE FROM " + ssd.table_name + " WHERE sync_status ='" + sync_status.syned.ordinal() + "'");
                                }
                                Log.e(ssd.service_name + " :: RX", "IS OK " + den);
                                if (den >= 0) {
                                    boolean storage_fields_converted_ok = true;
                                    if (ssd.storage_mode_check) {
                                        Log.e(ssd.service_name, "Started storage checking ...");
                                        a:
                                        for (int i = 0; i < temp_ar.length(); i++) {
                                            JSONObject jo = temp_ar.getJSONObject(i);
                                            Iterator keys = jo.keys();
                                            List<String> key_list = new ArrayList<>();
                                            while (keys.hasNext()) {
                                                key_list.add((String) keys.next());

                                            }

                                            for (String k : realm.getFilePathFields(ssd.object_package, key_list)) {
                                                try {
                                                    String saved_file_name = DatabaseManager.save_doc(jo.getString(k));
                                                    if (saved_file_name == null) {
                                                        storage_fields_converted_ok = false;
                                                        break a;
                                                    }
                                                    jo.put(k, saved_file_name);
                                                } catch (Exception e) {
                                                    Log.e(logTag, "Base64 image error:" + e.getMessage());
                                                    storage_fields_converted_ok = false;
                                                    break a;
                                                }

                                            }
                                        }
                                        Log.e(ssd.service_name, "Done storage checking ...");

                                    }
                                    if (storage_fields_converted_ok) {
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
                            ssi.on_api_error(ssd, e.getMessage());
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


    public static void upload_(sync_service_description ssd) {
        Log.e("SYNC ::  ", "Object table :" + ssd.table_name + "\n"
                + "Service name :" + ssd.service_name + "\n"
                + "Service type :" + ssd.servic_type.name() + "\n"
                + "upload link :" + ssd.upload_link + "\n");
        app_config =app_config==null? current_app_config(act):app_config;

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
//        ArrayList<JSONObject> pending_records = Main_handler.OnAboutToUploadObjects(ssd, sdb.load_dynamic_json_records_ann(ssd, pending_records_filter));
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
                    Log.e(ssd.service_name + ":: upload::", "Started storage checking ...");

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
                    Log.e(ssd.service_name + ":: upload::", "Done storage checking ...");

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
                                        //Log.e("Response =>", "" + response.toString());
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
                                                ssi.on_api_error(ssd, response.toString());
                                                ContentValues cv = new ContentValues();
                                                cv.put("data_status", "e");

                                                sdb.database.update(table_name, cv, "_id=" + finalLid, null);
                                                ssi.on_status_changed("Update failed ...  =>" + finalLid);
                                                ssi.on_status_code_changed(666);
                                                String error = " " + upload_object.toString() + "\n" + response.toString();
                                                Log.e(ssd.service_name + ":: upload::error::", error);
                                                sdb.log_String(act, ssd.service_name + ":: upload::error::" + error + "::Upload object::" + upload_object);
                                            }
                                        } catch (Exception ex) {
                                            ssi.on_api_error(ssd, response.toString());
                                            ssi.on_status_changed("Update failed ...  =>" + finalLid);
                                            ssi.on_status_code_changed(666);
                                            String error = " " + upload_object.toString() + "\n" + ex.getMessage();
                                            Log.e(ssd.service_name + ":: upload::error::", error);
                                            sdb.log_String(act, ssd.service_name + ":: upload::error::" + error + "::Upload object::" + upload_object);
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        Log.e(ssd.service_name + ":: upload error:", ":" + anError.getErrorBody());
                                        ssi.on_api_error(ssd, anError.getErrorBody());
                                        sdb.log_String(act, ssd.service_name + ":: upload::error::" + anError.getErrorBody() + "::Upload object::" + upload_object);
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


                                            sdb.database.update(table_name, cv, "_id=" + finalLid, null);
                                            update_counter(ssd, pending_records_filter);
                                            upload_counter[0]++;
                                            if (upload_counter[0] == upload_length) {
                                                sync_sum_counter++;
                                                upload_(ssd);
                                            }
                                        } else {
                                            ssi.on_status_code_changed(666);

                                            cv.put("data_status", "e");

                                            sdb.database.update(table_name, cv, "_id=" + finalLid, null);

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
        app_config =app_config==null? current_app_config(act):app_config;

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