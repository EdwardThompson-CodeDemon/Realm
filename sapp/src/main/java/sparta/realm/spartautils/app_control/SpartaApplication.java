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
    }

    public static Context getAppContext() {
        return Realm.context;
    }
}
