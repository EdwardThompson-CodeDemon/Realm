package sparta.realm.realmclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import sparta.realm.Realm;
import sparta.realm.RealmClientInterface;
import sparta.realm.spartautils.svars;

public class RealmClientService extends JobIntentService{
    public RealmClientService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.e("Realm Client ","onBind");
        if(main_client==null) {
            try {
                client_thread= new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Realm Client ","Starting ...");
                        main_client = new RealmClient(svars.device_code(Realm.context),"demo","demo123");
                        main_client.InitializeClient("192.168.1.107",8888,svars.device_code(Realm.context),"demo","demo123");
                        Log.e("Realm Client ","Stopped Running");
                        stopSelf();
                    }
                });
                client_thread.start();




            } catch (Throwable e) {
                Log.e("IO Exception ", "" + e.getMessage());
                e.printStackTrace();
            }
        }else {


            Log.e("Realm Client ","Already Running");


        }
        return null;
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.e("Realm Client ","Started Handling");

    }

    RealmClientInterface.Stub realmClientInterface = new RealmClientInterface.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void on_status_code_changed(int status) throws RemoteException {

        }

        @Override
        public void on_status_changed(String status) throws RemoteException {

        }

        @Override
        public void on_info_updated(String status) throws RemoteException {

        }

        @Override
        public void on_main_percentage_changed(int progress) throws RemoteException {

        }

        @Override
        public void on_secondary_progress_changed(int progress) throws RemoteException {

        }

        @Override
        public void onSynchronizationBegun() throws RemoteException {

        }

        @Override
        public void onSynchronizationCompleted() throws RemoteException {

        }


    };

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("TAG", name.getClassName() + " onServiceConnected");
            realmClientInterface = (RealmClientInterface.Stub) RealmClientInterface.Stub.asInterface(service);
        }




        @Override
        public void onServiceDisconnected(ComponentName name) {

            unbindService(serviceConnection);
        }
    };



    public static final int JOB_ID = 1;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, RealmClient.class, JOB_ID, work);
    }

    public static RealmClient main_client;
    Thread client_thread=null;
    public static final int upload=1;
    public static final int Download=2;
    public static final int Synchronize=3;
    public static final int uploadAll=4;
    public static final int DownloadAll=5;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        if(){
//
//        }
//        intent.getAction() != null
        if(intent != null && intent.getAction() != null) {
            switch (intent.getIntExtra("action", 0)) {
                case Synchronize:
                    main_client.Synchronize();
                    break;
                case DownloadAll:
                    Log.e("Realm Client ","Downloading all :");
                    main_client.downloadAll();
                    break;
                case uploadAll:
                    Log.e("Realm Client ","Uploading all :");
                    main_client.UploadAll();
                    break;
                case upload:
                    main_client.upload(Realm.realm.getHashedSyncDescriptions().get(intent.getStringExtra("service_id")));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    break;
            }
        }
        return START_STICKY;
    }



}
