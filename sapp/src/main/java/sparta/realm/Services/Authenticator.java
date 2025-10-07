package sparta.realm.Services;




import static sparta.realm.spartautils.svars.baseUrl;
import static sparta.realm.spartautils.svars.current_app_config;
import static sparta.realm.spartautils.svars.current_app_name;
import static sparta.realm.spartautils.svars.setBaseUrl;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.realm.annotations.sync_service_description;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sparta.realm.Realm;

import sparta.realm.spartautils.svars;
import sparta.realm.utils.RequestManager;

public class Authenticator {
    RequestManager authenticationManager;
static String logTag="Authenticator";
    public interface AuthenticationCallback {
        default void onUrlRequestDone(boolean successful) {

        }

        default void onLicenceCheckDone(boolean successful) {

        }

        default void onUsersRequested(boolean successful) {

        }
    }

    AuthenticationCallback authenticationCallback;

    public Authenticator(AuthenticationCallback authenticationCallback) {

        this.authenticationCallback = authenticationCallback;
    }
    String clientName()
    {
        ArrayList<String> uareu_devices=new ArrayList<>();
        ArrayList<String>t801_devices=new ArrayList<>();
        ArrayList<String>famoco_devices=new ArrayList<>();
        uareu_devices.add("SF807N");
        uareu_devices.add("F807");
        uareu_devices.add("FP-08");
        uareu_devices.add("SF-08");
        uareu_devices.add("SF-807");
        uareu_devices.add("S807");
        uareu_devices.add("ax6737_65_n");
        uareu_devices.add("SF-807N");
        t801_devices.add("SEEA900");
        t801_devices.add("SPC_08101");
        famoco_devices.add("FX205");
        famoco_devices.add("FX205");
        famoco_devices.add("FP200,1");
        famoco_devices.add("FP200");
        if(uareu_devices.contains(Build.MODEL))return "CMU-TABLETS";
//        if(famoco_devices.contains(Build.MODEL))return "CNAM-FAMOCO";

        return "CNAM-FAMOCO";
    }
    public void checkUrl() {
        authenticationManager = authenticationManager == null ? new RequestManager() : authenticationManager;
        JSONObject postData = new JSONObject();


        try {
            postData.put("client_name", current_app_config(Realm.context).CLIENT_NAME);
            postData.put("package_name", "" + current_app_name());

            Log.e(logTag, "URL REQUEST TX request  =>" + postData);


        } catch (Exception ex) {
            Log.e(logTag, "URL TX ERROR=> " + ex.getMessage());
            ex.printStackTrace();
        }
        authenticationManager.requestPost(current_app_config(Realm.context).BASE_URL_REQUEST_LINK, "JO:IsOkay", postData, new RequestManager.RequestCallback() {
            @Override
            public void onApiConnectionFailed() {

                authenticationCallback.onUrlRequestDone(false);
                Log.e("URL TX: ", "onApiConnectionFailed");

            }

            @Override
            public void OnRequestSuccessfully(JSONObject response) {
                Log.e("URL RX:OnRequestSuccessfully:  ", response.toString());
                try {
                    String base_url=response.getJSONArray("Result").getJSONObject(0).getString("backend_url");

                    if(baseUrl()==null){
                        setBaseUrl(base_url);


                    } else if (baseUrl().equalsIgnoreCase(base_url)) {
                        setBaseUrl(base_url);

                    }else{
                        setBaseUrl(base_url);
                        clearAllData();

                    }
                    Log.e(logTag, " Base url set  =>" + baseUrl());
                } catch (Exception e) {

                }
                authenticationCallback.onUrlRequestDone(true);
                checkLicence();


            }
void clearAllData(){

    for (sync_service_description ssd:Realm.realm.getSyncDescription()){
        Realm.databaseManager.executeQuery("delete from "+ssd.table_name);
    }
}
            @Override
            public void OnRequestFailed() {
                Log.e("URL TX: ", "OnRequestFailed");
                authenticationCallback.onUrlRequestDone(false);


            }


        });
    }

    void checkLicence() {
        authenticationManager = authenticationManager == null ? new RequestManager() : authenticationManager;
        JSONObject postData = new JSONObject();


        try {
            String deviceUniqueIdentifier = null;
            TelephonyManager tm = (TelephonyManager) Realm.context.getSystemService(Context.TELEPHONY_SERVICE);
            if (null != tm) {
                if (ActivityCompat.checkSelfPermission(Realm.context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

//                   ActivityCompat.requestPermissions(Realm.context,
//                           new String[]{Manifest.permission.READ_PHONE_STATE},
//                           1);
//                   deviceUniqueIdentifier = "Permission not granted";
                }
                try {
                    deviceUniqueIdentifier = tm.getDeviceId();
                    postData.put("device_imei", deviceUniqueIdentifier);
                    postData.put("device_imei_2", tm.getDeviceId(1));

                } catch (Exception ex) {
                }
            }

            postData.put("device_name", "" + Build.DEVICE);
            postData.put("code", "" + svars.device_code(Realm.context));
            postData.put("model_name", Build.MODEL);
            postData.put("serial_number", Build.SERIAL);
            postData.put("manufacturer", Build.MANUFACTURER);
            postData.put("board_name", Build.BOARD);
            postData.put("brand_name", Build.BRAND);
            Log.e(logTag, "License request  =>" + postData);


        } catch (Exception ex) {
            Log.e("LSC TX ERROR=>", " " + ex.getMessage());
        }
        authenticationManager.requestPost(current_app_config(Realm.context).LICENCE_VALIDATION_URL, "JO:IsOkay", postData, new RequestManager.RequestCallback() {
            @Override
            public void onApiConnectionFailed() {
                authenticationCallback.onLicenceCheckDone(false);

            }

            @Override
            public void OnRequestSuccessfully(JSONObject response) {
                authenticationCallback.onLicenceCheckDone(true);
//                loadUsers();
            }

            @Override
            public void OnRequestFailed() {
                authenticationCallback.onLicenceCheckDone(false);


            }


        });
    }



}
