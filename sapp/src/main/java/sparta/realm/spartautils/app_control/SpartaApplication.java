package sparta.realm.spartautils.app_control;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.luxand.FSDK;
import com.realm.annotations.RealmDataClass;

import sparta.realm.Dynamics.spartaDynamics;
import sparta.realm.Realm;
import sparta.realm.spartaservices.sdbw;
import sparta.realm.spartautils.app_control.services.App_updates;

public class SpartaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.context = getApplicationContext();
       // realm=realm==null?new spartaDynamics():realm;

        Realm.Initialize(this,new spartaDynamics());
    }

    public static Context getAppContext() {
        return Realm.context;
    }
}
