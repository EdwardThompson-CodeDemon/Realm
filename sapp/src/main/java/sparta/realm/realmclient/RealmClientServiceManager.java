package sparta.realm.realmclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import sparta.realm.R;
import sparta.realm.Realm;
import sparta.realm.RealmClientCallbackInterface;
import sparta.realm.RealmClientInterface;
import sparta.realm.spartautils.svars;

import static android.content.Context.BIND_AUTO_CREATE;

public class RealmClientServiceManager {
    RealmClientInterface realmClientInterface ;

    RealmClientCallbackInterface realmClientInterfaceRX;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("TAG", name.getClassName() + " onServiceConnected");
            realmClientInterface =  RealmClientInterface.Stub.asInterface(service);
            try {
                realmClientInterface.registerCallback(realmClientInterfaceRX);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            realmClientInterface.InitializeClient(server_ip,port, svars.device_code(Realm.context),username,password);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            Realm.context.unbindService(serviceConnection);
        }
    };

    public String server_ip;
    public int port;
    public  String device_code;
    public  String username;
    public  String password;
    public  RealmClientServiceManager(RealmClientCallbackInterface.Stub realmClientInterfaceRX,String server_ip, int port, String username, String password){

        this.realmClientInterfaceRX=realmClientInterfaceRX;
        this.server_ip=server_ip;
        this.port=port;
        this.username=username;
        this.password=password;


        Intent rci= null;
        try {
//            rci = new Intent(Realm.context,Class.forName("sparta.realm.RealmClientInterface"));
            rci = new Intent("sparta.realm.RealmClientInterface");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Realm.context.bindService(rci,serviceConnection,BIND_AUTO_CREATE);
        Realm.context.bindService(convertImplicitIntentToExplicitIntent(rci, Realm.context),serviceConnection,BIND_AUTO_CREATE);
    }
    public void registerCallback(RealmClientCallbackInterface.Stub realmClientInterfaceRX)
    {
        this.realmClientInterfaceRX=realmClientInterfaceRX;
        try {
            realmClientInterface.registerCallback(realmClientInterfaceRX);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }
public void upload(String service_id){
    try {
        realmClientInterface.upload(service_id);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void download(String service_id){
    try {
        realmClientInterface.download(service_id);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void uploadAll(){
    try {
        realmClientInterface.uploadAll();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void downloadAll(){
    try {
        realmClientInterface.downloadAll();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
 public void Synchronize(){
     try {
         realmClientInterface.synchronize();
     } catch (Exception e) {
         e.printStackTrace();
     }
}

    public static Intent convertImplicitIntentToExplicitIntent(Intent implicitIntent, Context context) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryIntentServices(implicitIntent, 0);

        if (resolveInfoList == null || resolveInfoList.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfoList.get(0);
        ComponentName component = new ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

}
