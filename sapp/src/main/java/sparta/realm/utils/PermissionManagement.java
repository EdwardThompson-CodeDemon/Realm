package sparta.realm.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import sparta.realm.Realm;

public class PermissionManagement {

    public static final int MULTIPLE_PERMISSIONS = 10;


    public static void requestAllPermissions(Activity context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                checkPermissions(context, info.requestedPermissions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static boolean checkAllPermissions(Activity context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                return checkPermissions(context, info.requestedPermissions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    public static boolean checkPermissions(Activity context, String[] permissions) {
        int result;
        boolean ok = true;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (listPermissionsNeeded.isEmpty() || (listPermissionsNeeded.size() == 1 & listPermissionsNeeded.get(0).equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION))) {

        } else {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            ok = false;
        }
//        if (!listPermissionsNeeded.isEmpty() && (listPermissionsNeeded.size()!=1&listPermissionsNeeded.get(0).equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION))) {
//
//        }


        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {


            //  openOverlaySettings();
        }
        if (!Settings.canDrawOverlays(context)) {
            openOverlaySettings(context);
            ok = false;
        }

        return ok;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void openOverlaySettings(Activity activity) {
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity.getPackageName()));
        try {
            activity.startActivityForResult(intent, 6);
        } catch (ActivityNotFoundException e) {
            Log.e("Drawers permission :", e.getMessage());
        }
    }
}
