package sparta.realm.spartautils.app_control;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


//import sparta.realm.BuildConfig;
import sparta.realm.Realm;
import sparta.realm.Services.DatabaseManager;
import sparta.realm.spartautils.svars;
import sparta.realm.utils.AppConfig;


public class SpartaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppConfig APPP = new AppConfig("https://weightcapture.cs4africa.com/arqan", "http://ta.cs4africa.com:2222/api/AppStore/LoadApp","da","realm","/SystemAccounts/Authentication/Login/Submit",true);
        APPP.WORKING_PROFILE_MODE= AppConfig.PROFILE_MODE.GENERAL;
        AppConfig UIPA_APP = new AppConfig("http://ta.cs4africa.com:9090",
                null,
                "U.I.P.A." ,
                "MAIN CAMPUS",
                "/Authentication/Login/Submit",false

        );
        svars.set_current_device(this, svars.DEVICE.UAREU.ordinal());
//        Realm.Initialize(this,new spartaDynamics(),"REALM",APPP,"ihdBXU3FcZAawN2qayK9PG3kGz1BocN0EjHOe6hn2LhCubiwJYP7XsbIxildd0hfE9Tio36fGMdwoH4kC0HJNjzs5GbdWchRPmn5O/omstCi37+w7VNFkOgWxDhQSiDn4Apb77g0FwoNvyhVgE7lBx9DxcSnqvniTyKidXlHCak=");
//        Realm.Initialize(this,new spartaDynamics(),"0.0.1", BuildConfig.APPLICATION_ID,UIPA_APP);
//        Realm.Initialize(this,new spartaDynamics(),"0.0.1","megvii.testfacepass",UIPA_APP);
        DatabaseManager.database.execSQL("DELETE FROM tickets");
        DatabaseManager.database.execSQL("DELETE FROM ticket_verification_data");
        SharedPreferences.Editor saver =this.getSharedPreferences(sparta.realm.spartautils.svars.sharedprefsname, this.MODE_PRIVATE).edit();
        saver.putString("username", "evans");
        saver.putString("pass", "demo123");
        saver.commit();
    }

    public static Context getAppContext() {
        return Realm.context;
    }
}
