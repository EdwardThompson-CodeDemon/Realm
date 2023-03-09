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
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import sparta.realm.Realm;

import static android.os.Build.VERSION.SDK_INT;

public class PermissionManagement {

    public static final int MULTIPLE_PERMISSIONS = 10;


    public static void requestAllPermissions(Activity context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                checkAndRequestPermissions(context, info.requestedPermissions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


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


    public static boolean checkAndRequestAllPermissions(Activity context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                return checkAndRequestPermissions(context, info.requestedPermissions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

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

    public static boolean checkAndRequestPermissions(Activity context, String[] permissions) {
        int result;
        boolean ok = true;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (listPermissionsNeeded.contains(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {

            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    listPermissionsNeeded.remove(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }
            } else {
                //below android 11=======
                listPermissionsNeeded.remove(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            }
        }
        if (listPermissionsNeeded.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            if (!Settings.canDrawOverlays(context)) {
                openOverlaySettings(context);
                ok = false;
            } else {
                listPermissionsNeeded.remove(Manifest.permission.SYSTEM_ALERT_WINDOW);


            }

        }
        if (listPermissionsNeeded.contains(Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
            listPermissionsNeeded.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES);

        }
 if (listPermissionsNeeded.contains("com.mediatek.permission.CTA_ENABLE_BT")) {
            listPermissionsNeeded.remove("com.mediatek.permission.CTA_ENABLE_BT");

        }

        if (listPermissionsNeeded.isEmpty() || (listPermissionsNeeded.size() == 1 & listPermissionsNeeded.get(0).equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION))) {

        } else {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            ok = false;
        }


        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {


            //  openOverlaySettings();
        }


        return ok;
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
        if (listPermissionsNeeded.contains(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {

            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    listPermissionsNeeded.remove(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
                }
            } else {
                listPermissionsNeeded.remove(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            }
        }
        if (listPermissionsNeeded.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            if (!Settings.canDrawOverlays(context)) {
                ok = false;
            } else {
                listPermissionsNeeded.remove(Manifest.permission.SYSTEM_ALERT_WINDOW);
            }
        }
        if (listPermissionsNeeded.contains(Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
            listPermissionsNeeded.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES);
        }

        if (listPermissionsNeeded.isEmpty() || (listPermissionsNeeded.size() == 1 & listPermissionsNeeded.get(0).equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION))) {

        } else {
            ok = false;
        }


        return ok;
    }


}
