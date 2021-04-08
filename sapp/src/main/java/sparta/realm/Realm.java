package sparta.realm;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.luxand.FSDK;
import com.realm.annotations.RealmDataClass;

import sparta.realm.Services.DatabaseManager;


import sparta.realm.spartautils.app_control.SpartaApplicationErrorHandler;
import sparta.realm.spartautils.app_control.services.App_updates;
import sparta.realm.spartautils.svars;

public class Realm {

  //  public static RealmDataClass realm_;
    public static Context context;
    public static RealmDataClass realm;
    public static DatabaseManager databaseManager;

    public static void Initialize(Context cont, RealmDataClass realm_, String app_version, svars.SPARTA_APP app_config){
    context=cont;
    realm=realm_;
        svars.set_current_version(cont,app_version);
       svars.set_current_app_config(cont,app_config);
        Thread.setDefaultUncaughtExceptionHandler(new SpartaApplicationErrorHandler(Realm.context));
        String[] supportedABIS = Build.SUPPORTED_ABIS; // Return an ordered list of ABIs supported by this device.
        for (String abi:supportedABIS) {
            Log.e("ARchit", "ss " + abi);
            Toast arc = Toast.makeText(Realm.context, "Device Architecture is " + abi, Toast.LENGTH_LONG);
            // arc.show();
        }
        context.startService(new Intent(Realm.context, App_updates.class));
        try{
            databaseManager=new DatabaseManager(Realm.context);

        }catch (Exception ex){}

}
    public static void Initialize( Context cont,RealmDataClass realm_,String app_version, svars.SPARTA_APP app_config,String FSDK_KEY){
        Initialize(cont,realm_, app_version,app_config);

        if(FSDK_KEY!=null){
            try {
                int res = FSDK.ActivateLibrary(FSDK_KEY);
                FSDK.Initialize();
                FSDK.SetFaceDetectionParameters(false, false, 100);
                FSDK.SetFaceDetectionThreshold(5);

                if (res == FSDK.FSDKE_OK) {
                    Log.d("FSDK : ","Initialization OK" );

                } else {
                    Log.e("FSDK Error : ","Initialization failed" );

                }
            }
            catch (Exception e) {
                Log.e("exception ","" + e.getMessage());
            }
        }


}
}
