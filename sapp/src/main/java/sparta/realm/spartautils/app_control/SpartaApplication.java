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
import sparta.realm.spartamodels.RealmDynamics.spartaDynamics;
import sparta.realm.spartautils.svars;


public class SpartaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        Realm.context = getApplicationContext();
       // realm=realm==null?new spartaDynamics():realm;

        svars.SPARTA_APP APPP = new svars.SPARTA_APP("https://weightcapture.cs4africa.com/arqan", "http://ta.cs4africa.com:2222/api/AppStore/LoadApp","da","realm","/SystemAccounts/Authentication/Login/Submit",true);

        APPP.WORKING_PROFILE_MODE= svars.SPARTA_APP.PROFILE_MODE.GENERAL;


//        Realm.Initialize(this,new spartaDynamics(),"REALM",APPP,"ihdBXU3FcZAawN2qayK9PG3kGz1BocN0EjHOe6hn2LhCubiwJYP7XsbIxildd0hfE9Tio36fGMdwoH4kC0HJNjzs5GbdWchRPmn5O/omstCi37+w7VNFkOgWxDhQSiDn4Apb77g0FwoNvyhVgE7lBx9DxcSnqvniTyKidXlHCak=");
        Realm.Initialize(this,new spartaDynamics(),"0.0.1","megvii.testfacepass",APPP);
        Realm.databaseManager.database.execSQL("DELETE FROM app_versions_table");
    }

    public static Context getAppContext() {
        return Realm.context;
    }
}
