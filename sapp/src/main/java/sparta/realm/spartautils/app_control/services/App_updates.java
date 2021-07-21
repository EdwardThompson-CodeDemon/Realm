package sparta.realm.spartautils.app_control.services;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.github.lzyzsd.circleprogress.ArcProgress;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import sparta.realm.BuildConfig;
import sparta.realm.R;
import sparta.realm.Realm;
import sparta.realm.spartamodels.percent_calculation;
import sparta.realm.Services.DatabaseManager;
import sparta.realm.spartautils.app_control.models.sparta_app_version;
import sparta.realm.spartautils.svars;

import static sparta.realm.spartautils.svars.current_app_config;


public class App_updates extends Service {
   
    Timer update_check_timer=new Timer();
   
    static DatabaseManager db;
    static AlertDialog ald;





    private static NotificationManager mNotifyManager;
    private static NotificationCompat.Builder mBuilder;

    public static boolean active=false;
    private static final int NOTIFICATION_ID = 151;
    public static String UPDATE_CHECK_LINK="http://ta.cs4africa.com:2222/api/AppStore/LoadApp";
    public static String log_tag="App Update";

    public App_updates() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
static sparta_app_version w_sav;
  private static final int PERIOD = 1000*60*30;//30 mins
    //  private static final int PERIOD = 15000;// 15 seconds
    Handler check_update_handler;
    Runnable check_update_thread;
    @Override
    public void onCreate() {
        super.onCreate();
        act=this;
        db=Realm.databaseManager;
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(act);
        mBuilder.setContentTitle("APK Update")
                .setContentText("Preparing to download")
                .setSmallIcon(R.drawable.download_icon);

        if(ald==null){
            create_dialog();
        }


        AndroidNetworking.initialize(act);
        Log.e("App_updates","Creating");

       if(check_update_handler!=null){return;}
//        if(update_check_timer!=null){return;}
//        update_check_timer = new Timer();
        check_update_handler = new Handler();
        check_update_thread = new Runnable() {
            public void run() {
                check_for_app_versions();
                check_update_handler.postDelayed(this, PERIOD);
            }
        };
        check_update_thread.run();
   /*     update_check_timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                String time_now="Today "+ Calendar.getInstance().getTime().toString().split(" ")[3];


                Log.e("Checking for updates", " Now :" + time_now);
                Log.e("Checking for updates", " Last check :" + db.update_check_period());
                if(db.update_check_period()>=(svars.update_check_interval_mins-1)||db.update_check_period()<0) {
                    w_sav=db.load_latest_apk_to_install();
                    if(w_sav!=null)
                    {
                        Log.e("APK UPDATE", " Loaded  " );

                         //  create_dialog();
                            show_dialog();



                        Log.e("APK UPDATE", " Shown dialog  " +(act==null?"NULL ":" NOT NULL"));


                    }else{
                        Log.e("APK UPDATE", "Not  Loaded  " );

                    }
                    //check_for_app_versions();

                }



            }
        }, 500, 1000);*/
   //     check_for_app_versions();

    }

    private static void create_dialog() {
        View aldv= LayoutInflater.from(act).inflate(R.layout.dialog_app_update_2019,null);
        ald=new AlertDialog.Builder(act,R.style.AppTheme)
                .setView(aldv)
                .setCancelable(true)
                .create();
        final WindowManager.LayoutParams dialogWindowAttributes = ald.getWindow().getAttributes();


        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogWindowAttributes);
        lp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280, act.getResources().getDisplayMetrics());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ald.getWindow().setAttributes(lp);
        ald.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        ald.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        version_name=(TextView)aldv.findViewById(R.id.title);
        change_log=(TextView)aldv.findViewById(R.id.changelog);
        update=(Button) aldv.findViewById(R.id.update);
        change_log.setMovementMethod(new ScrollingMovementMethod());
        arc=(ArcProgress)aldv.findViewById(R.id.arc_progress) ;

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ald.dismiss();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());

                        builder.detectFileUriExposure();
                    }
                    final Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(current_app_config(Realm.context).file_path_app_downloads + "/" + w_sav.download_link.split("/")[w_sav.download_link.split("/").length - 1])), "application/vnd.android.package-archive");

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    act.startActivity(intent);
                }catch (Exception ex){
                    Log.e(log_tag,"Error installing :"+ex.getMessage());

                }

            }
        });
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e("App_updates","Starting");
    }
boolean first_done=false;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
          Log.e("App_updates","Start trigger");
          if(!first_done){first_done=true;return START_STICKY;}
        db=db==null?Realm.databaseManager:db;
        if(mNotifyManager==null){
            mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(act);

        }

    //    check_for_app_versions();
        if(!downloading&&w_sav==null){w_sav=db.load_latest_apk_to_install();}

        if(w_sav!=null)
        {
            Log.e("APK UPDATE", " Loaded  " );

            //  create_dialog();
            show_dialog();



            Log.e("APK UPDATE", " Shown dialog  " +(act==null?"NULL ":" NOT NULL"));


        }else{
            Toast.makeText(act,"No versions available ",Toast.LENGTH_LONG).show();
            Log.e("APK UPDATE", "Not  Loaded  " );
          if(!checking_for_updates){
              check_for_app_versions();
          }

        }


        return START_STICKY;
    }
public static void intialize_download_check_proceedure()
{
    String[] savss=new String[savs.size()];
    for(int i=0;i<savss.length;i++)
    {
        savss[i]=savs.get(i).version_id;

    }

   savs.addAll(db.load_undownloaded_apks(savss));
    if(savs.size()>0&&!downloading)
    {
        Log.e("APK UPDATE", " Downloading " );
        download();
        Log.e("APK UPDATE", " Downloaded " );

    }else if(savs.size()==0&&!downloading){
        Log.e("APK UPDATE", " Loading downloaded app " );

    }


}
   static SDownloadInterface main_inter=new SDownloadInterface() {
        @Override
        public void on_status_changed(int status, String message) {

        }

        @Override
        public void on_download_progress(int progress) {



            mBuilder.setProgress(100, progress, progress==0?true:false);
            mBuilder.setContentText("Downloading "+progress+"%");
            mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
            Log.e("downloading :", " "+progress);
arc.setProgress(progress);
update.setText("Downloading .. "+progress+"%");
update.setEnabled(false);

        }

        @Override
        public void on_download_error(String error) {

        }

        @Override
        public void on_download_begun() {

        }

        @Override
        public void on_re_download_begun() {

        }

        @Override
        public void on_download_complete() {
            mBuilder.setProgress(100, 100, true);
            mBuilder.setContentText("Download complete");
            mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());

            update.setText("Update");
            update.setEnabled(true);


        }
    };
boolean checking_for_updates=false;
    void check_for_app_versions() {
        checking_for_updates=true;
        final JSONObject postData=new JSONObject();
       String[] app_package_name =new String[]{svars.current_app_name(act)};
        try{
            postData.put("RequestTime", Calendar.getInstance().getTime().toString());
            postData.put("RequestData", new JSONArray(app_package_name));
            Log.e("Post data",""+postData.toString());
        }catch (Exception ex){
            return;

        }
        final JSONObject[] maindata = {new JSONObject()};

        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();

                final Handler handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        Log.e("version POST", "Handler => " + message.toString());
                        //intialize_download_check_proceedure();

//show_dialog();
                        return false;
                    }
                });
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        String data = "";
                        String error_data = "";

                        HttpURLConnection httpURLConnection = null;
                        try {

                            httpURLConnection = (HttpURLConnection) new URL(current_app_config(Realm.context).APP_CONTROLL_MAIN_LINK).openConnection();
                            httpURLConnection.setRequestMethod("POST");
                            httpURLConnection.setRequestProperty("Content-Type", "application/json");
                            // httpURLConnection.setRequestProperty("Authorization", "Bearer" + svars.Service_token(act));

                            httpURLConnection.setDoOutput(true);


                            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                            wr.writeBytes(postData.toString());
                            wr.flush();
                            wr.close();

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
                                    Log.e("version POST RX", "  " + data);

                                    maindata[0] = new JSONObject(data);

                                    if (maindata[0].getInt("StatusCode") == 1) {


                                        JSONObject app_j = maindata[0].getJSONObject("Response");
                                       // Log.e("Apk  "," "+app_j.toString());

                                        JSONArray app_versions = app_j.getJSONArray("application_versions");

                                             for (int l = 0; l < app_versions.length(); l++) {
                                                JSONObject appcateg_j = app_versions.getJSONObject(l);
                                             //    Log.e("Apk version ","before "+appcateg_j.toString());
                                                 db.save_versions(appcateg_j);
                                              //   Log.e("Apk version ","after "+appcateg_j.toString());

                                             }
                                        String time_now=svars.gett_time();
                                             svars.set_update_check_time(act,time_now);

                                        intialize_download_check_proceedure();
                                        checking_for_updates=false;


                                    } else {
                                        checking_for_updates=false;

                                        Log.e("Load apps4_categs =>", "NO SUCCESS");
                                    }


                                } catch (Exception exx) {
                                    intialize_download_check_proceedure();
                                    handler.dispatchMessage(new Message());
                                    InputStream error = httpURLConnection.getErrorStream();
                                    InputStreamReader inputStreamReader2 = new InputStreamReader(error);

                                    int inputStreamData2 = inputStreamReader2.read();
                                    while (inputStreamData2 != -1) {
                                        char current = (char) inputStreamData2;
                                        inputStreamData2 = inputStreamReader2.read();
                                        error_data += current;
                                    }
                                    Log.e("version POST RX", "error => " + exx);
   Log.e("version POST RX", "error => " + error_data);
                                    checking_for_updates=false;

                                }


                            } catch (Exception e) {


                            handler.dispatchMessage(new Message());

                            intialize_download_check_proceedure();
                            Log.e("version POST TX", "External error => " + e.getMessage());
                                e.printStackTrace();
                            checking_for_updates=false;
                        } finally {
                                if (httpURLConnection != null) {
                                    httpURLConnection.disconnect();
                                }
                            checking_for_updates=false;
                        }

//
                        handler.removeCallbacks(this);
                        Looper.myLooper().quit();
                    }
                }, 500);

                Looper.loop();
            }
        };
        thread.start();








    }

    static TextView change_log;
    static TextView version_name;
    static Button update;
    static ArcProgress arc;
public static void show_dialog()
{
if(w_sav==null){
    version_name.setText(svars.current_version(act));
    change_log.setText("latest version");

}else {
    version_name.setText(w_sav.release_name+"\n"+w_sav.version_name);
    change_log.setText(w_sav.release_notes);

}

ald.show();

}
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("App_updates","Destroying");
    }



    public static class download_install_status{

        public static int downloading=1;
        public static int downloaded_not_installed=4;
        public static int qued=3;
        public static int installing=2;
        public static int installed=5;

        int status=0;

        public download_install_status(sparta_app_version sav)
        {
            for (int i=0;i<savs.size();i++)
            {
                sparta_app_version s=savs.get(i);
                if(s.version_id.equalsIgnoreCase(sav.version_id))
                {
                    status=i==0?downloading:qued;

                }
            }


        }
        public int getCode() { return status; }

        public static String getMessge(int status) {
            //this.status=status;
            switch (status)
            {
                case 1:

                    return "Downloading";


                case 2:

                    return "Installing";


                case 3:
                    return "Qued";

                case 4:
                    return "Downloaded not installed";


                case 5:
                    return "Installed";

                default:
                    return "Unable to determine";



            }

        }
        @Override
        public String toString() {
            switch (status)
            {
                case 1:

                    return "Downloading";


                case 2:

                    return "Insatalling";


                case 3:
                    return "Qued";

                case 4:
                    return "Downloaded not installed";

                default:
                    return "Unable to determine";



            }

        }
    }
    public enum EventAction {
        SDCARD_MOUNTED(25, "External SDCard was mounted");
        private final int code;
        private final String message;
        private EventAction(int code, String message) {
            this.code = code;
            this.message = message;
        }
        @Override
        public String toString() { return message; }
        public int getCode() { return code; }
    }
    public interface SDownloadInterface{


        void on_status_changed(int status, String message);
        void on_download_progress(int progress);
        void on_download_error(String error);
        void on_download_begun();
        void on_re_download_begun();
        void on_download_complete();



    }
    //public static SDownloadInterface main_inter;
    public static SDownloadInterface host_inter;
    public static ArrayList<sparta_app_version> savs=new ArrayList<>();
    static int re_download_count=0;
    static int max_redounload_count=10;
    static boolean downloading=false;
    static Boolean re_download_on_failed=true;










    public static Context act;
    public static boolean isMyServiceRunning(Context act) {
        Class<?> serviceClass=App_updates.class;

        ActivityManager manager;
        manager = (ActivityManager) act.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
static int cur_per=-1;
    public static void download()
    {
        if(savs.size()==0)
        {
            Log.e("download initializing ", " Nothing to download");
downloading=false;

            return;
        }

        mBuilder.setContentTitle("App Update")
                .setContentText("Attempting download")
                .setSmallIcon(R.drawable.logo);
        final sparta_app_version sav=savs.get(0);
        w_sav=sav;
        Log.e("download initializing ", " "+sav.download_link);
       /*Thread thread = new Thread() {
            public void run() {
                Looper.prepare();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
 */
     /*   try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        try {
           // Log.e("download initializing ", " xxx");
            final File file = new File(current_app_config(Realm.context).file_path_app_downloads);
                            file.mkdirs();
            File d_path=new File(current_app_config(Realm.context).file_path_app_downloads);
            if (!d_path.exists()){

                Log.e("Creating directory :",d_path.mkdir()+"") ;
            }
                            AndroidNetworking.download(sav.download_link, current_app_config(Realm.context).file_path_app_downloads,sav.download_link.split("/")[sav.download_link.split("/").length-1])
                                    .setTag("downloadTest")
                                    .setPriority(Priority.HIGH)
                                    .build()
                                    .setDownloadProgressListener(new DownloadProgressListener() {
                                        @Override
                                        public void onProgress(long bytesDownloaded, long totalBytes) {
if(!downloading)
{
    downloading=true;
//    show_dialog();
}
                                            percent_calculation per=new percent_calculation(totalBytes+"",""+bytesDownloaded);



                                             if(Integer.parseInt(per.per_balance)!=cur_per){
                                                 main_inter.on_download_progress(Integer.parseInt(per.per_balance));
                                                 main_inter.on_status_changed(download_install_status.downloading, download_install_status.getMessge(download_install_status.downloading));
cur_per= Integer.parseInt(per.per_balance);
                                             }

                                        }
                                    })
                                    .startDownload(new DownloadListener() {
                                        @Override
                                        public void onDownloadComplete() {
                                            downloading=false;
                                            main_inter.on_download_complete();
                                            mBuilder.setProgress(100, 100, true);

                                            db.update_downloaded_versions(sav.version_id,current_app_config(Realm.context).file_path_app_downloads+sav.download_link.split("/")[sav.download_link.split("/").length-1] );
                                            main_inter.on_status_changed(download_install_status.installing, download_install_status.getMessge(download_install_status.installing));
                                            sav.local_path= current_app_config(Realm.context).file_path_app_downloads+sav.download_link.split("/")[sav.download_link.split("/").length-1];
                                            w_sav=sav;
                                            re_download_count=0;
                                            //downloaded=true;
                                            savs.remove(sav);

                                            Toast.makeText(act, " Apk downloaded successfully", Toast.LENGTH_LONG).show();
                                            show_dialog();

                                            main_inter.on_status_changed(download_install_status.installed, download_install_status.getMessge(download_install_status.installed));
                                            download();

                                        }
                                        @Override
                                        public void onError(ANError error) {
                                            Log.e("download error ", "[" +re_download_count+"]"+ error.getErrorBody());
                                            main_inter.on_download_error(error.getErrorBody());
                                            if(re_download_on_failed&re_download_count<max_redounload_count)
                                            {
                                                main_inter.on_re_download_begun();
                                                re_download_count++;
                                                download();
                                            }else if(re_download_on_failed&re_download_count>=max_redounload_count)
                                            {
                                                re_download_count=0;
                                                savs.remove(sav);
                                                downloading=false;
                                                download();
                                                w_sav= null;


                                            }
                                        }
                                    });


            Log.e("download initialized ", " xxx");



        } catch (final Exception ex) {

                            Log.e("download error ", "[" +re_download_count+"]"+ ex.getMessage());
                            ex.printStackTrace();

                        }/*
                    }
                }, 2000);

                Looper.loop();
            }
        };
        thread.start();
*/
    }
}
