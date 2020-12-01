package sparta.realm.spartautils.app_control;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.luxand.FSDK;
import com.realm.annotations.RealmDataClass;



import sparta.realm.Realm;
import sparta.realm.spartautils.svars;


public class SpartaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        Realm.context = getApplicationContext();
       // realm=realm==null?new spartaDynamics():realm;

        svars.SPARTA_APP APPP = new svars.SPARTA_APP("https://weightcapture.cs4africa.com/arqan", "http://ta.cs4africa.com:2222/api/AppStore/LoadApp","da","realm");

        APPP.WORKING_PROFILE_MODE= svars.SPARTA_APP.PROFILE_MODE.GENERAL;


      //  Realm.Initialize(this,new spartaDynamics(),"REALM",APPP);
    }

    public static Context getAppContext() {
        return Realm.context;
    }
}
