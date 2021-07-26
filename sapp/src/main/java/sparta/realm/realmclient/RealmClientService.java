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

import sparta.realm.RealmClientCallbackInterface;
import sparta.realm.RealmClientInterface;
import sparta.realm.spartautils.svars;

public class RealmClientService extends JobIntentService{
    public RealmClientService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.e("Realm Client ","onBind");

        return realmClientInterface;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e("Realm Client ","onReBind");
        super.onRebind(intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.e("Realm Client ","Started Handling");

    }
    public RealmClientCallbackInterface realmClientInterfaceTX ;
    public static String log_tag="Realm Client Service";

    RealmClientInterface.Stub realmClientInterface = new RealmClientInterface.Stub() {


        @Override
        public void registerCallback(RealmClientCallbackInterface cb) throws RemoteException {
            realmClientInterfaceTX=cb;
            realmClientInterfaceTX.on_info_updated("Sync registered");
            if(main_client!=null){
                main_client.registerCallBack(realmClientInterfaceTX);
            }
        }

        @Override
        public void unregisterCallback(RealmClientCallbackInterface cb) throws RemoteException {

        }

        @Override
        public int InitializeClient(String server_ip, int port, String device_code, String username, String password) throws RemoteException {
          Log.e(log_tag,"Initializing client");
            start_service(server_ip,port,device_code,username,password);


            return 0;
        }

        @Override
        public void upload(String service_id) throws RemoteException {
            main_client.upload(Realm.realm.getHashedSyncDescriptions().get(service_id));

        }

        @Override
        public void download(String service_id) throws RemoteException {
main_client.download(null,Realm.realm.getHashedSyncDescriptions().get(service_id));
        }

        @Override
        public void downloadAll() throws RemoteException {
            Log.e(log_tag,"Downloading all interf");
            main_client.downloadAll();

        }

        @Override
        public void uploadAll() throws RemoteException {
            main_client.uploadAll();

        }

        @Override
        public void synchronize() throws RemoteException {
main_client.Synchronize();
        }


    };

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("TAG", name.getClassName() + " onServiceConnected");
            realmClientInterface = (RealmClientInterface.Stub) RealmClientInterface.Stub.asInterface(service);
//            try {
//                realmClientInterface.on_info_updated("test info update");
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
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

    public void start_service(String server_ip,int port,String devicecode,String username,String password){
        if(main_client==null) {
            try {
//                client_thread= new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                while (true){
                    Log.e("Realm Client ","Starting ...");
                    main_client = new RealmClient(realmClientInterfaceTX);
                    main_client.InitializeClient(server_ip,port,devicecode,username,password);
                    Log.e(log_tag,"Stopping Run");
                    stopSelf();
                    main_client=null;
                    Log.e(log_tag,"Stopped Running");

//                }


//                    }
//                });
//                client_thread.start();




            } catch (Throwable e) {
                Log.e("IO Exception ", "" + e.getMessage());
                e.printStackTrace();
            }
        }else {


            Log.e("Realm Client ","Already Running");


        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


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
                    main_client.uploadAll();
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
