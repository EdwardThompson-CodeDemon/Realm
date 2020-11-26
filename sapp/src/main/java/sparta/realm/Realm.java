package sparta.realm;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.luxand.FSDK;
import com.realm.annotations.RealmDataClass;

import sparta.realm.spartaservices.sdbw;
import sparta.realm.spartautils.app_control.SpartaApplicationErrorHandler;
import sparta.realm.spartautils.app_control.services.App_updates;

public class Realm {

  //  public static RealmDataClass realm_;
    public static Context context;
    public static RealmDataClass realm;

    public static void Initialize( Context cont,RealmDataClass realm_){
    context=cont;
    realm=realm_;
        Thread.setDefaultUncaughtExceptionHandler(new SpartaApplicationErrorHandler(Realm.context));
        String[] supportedABIS = Build.SUPPORTED_ABIS; // Return an ordered list of ABIs supported by this device.
        for (String abi:supportedABIS) {
            Log.e("ARchit", "ss " + abi);
            Toast arc = Toast.makeText(Realm.context, "Device Architecture is " + abi, Toast.LENGTH_LONG);
            // arc.show();
        }
        context.startService(new Intent(Realm.context, App_updates.class));

        /* If you has other classes that need context object to initialize when application is created,
         you can use the appContext here to process. */
        try {
            int res = FSDK.ActivateLibrary("ihdBXU3FcZAawN2qayK9PG3kGz1BocN0EjHOe6hn2LhCubiwJYP7XsbIxildd0hfE9Tio36fGMdwoH4kC0HJNjzs5GbdWchRPmn5O/omstCi37+w7VNFkOgWxDhQSiDn4Apb77g0FwoNvyhVgE7lBx9DxcSnqvniTyKidXlHCak=");
            FSDK.Initialize();
            FSDK.SetFaceDetectionParameters(false, false, 100);
            FSDK.SetFaceDetectionThreshold(5);

            if (res == FSDK.FSDKE_OK) {
            } else {


            }
        }
        catch (Exception e) {
            Log.e("exception ","" + e.getMessage());
        }
        try{
            sdbw sd=new sdbw(Realm.context);
            sd=null;
        }catch (Exception ex){}
}
}
