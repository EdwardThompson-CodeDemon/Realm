package sparta.realm.spartamodels.timecap.Models;

import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

@DynamicClass(table_name ="configuration_table")
//@SyncDescription(service_name = "Config",service_type = SyncDescription.service_type.Download,download_link = "/Api_v1.0/Camera/GetDeviceConfig",use_download_filter = false,is_ok_position = "JO:isOkay",download_array_position = "JO:result")//uipa
//@SyncDescription(service_id = "7",service_name = "Config",service_type = SyncDescription.service_type.Download,download_link = "/Api_v1.0/Camera/GetDeviceConfig",use_download_filter = false,is_ok_position = "JO:IsOkay",download_array_position = "JO:Result")
@SyncDescription(service_id = "7",service_name = "Config",service_type = SyncDescription.service_type.Download,download_link = "/Api_v1.0/Camera/GetDeviceConfig",use_download_filter = false,is_ok_position = "JO:isOkay",download_array_position = "JO:result")
public class configuration extends RealmModel {
/*

///Api_v1.0/Camera/AddCamVerificationApi_v1.0/Camera/AddRejectedImages
//Api_v1.0/Camera/AddVerificationImages
//Api_v1.0/Camera/GetDeviceConfig
//Api_v1.0/Camera/AddCamVerification
//Api_v1.0/Camera/GetDeviceLicense/{devicekey}
{
    "$id": "1",
    "isOkay": true,
    "message": "Details Found",
    "result": {
        "$id": "2",
        "id": 1,
        "searchScore": "0",
        "livenessScore": "0",
        "tempThreshold": "0",
        "allowVisitors": false,
        "status": true,
        "datecomparer": 1,
        "sync_datetime": "2021-06-29T17:33:33"
    }
}
 */
///Api_v1.0/Camera/GetDeviceConfig



          @DynamicProperty(json_key = "searchScore")
          public String matchingScore="75.5";


          @DynamicProperty(json_key = "tempThreshold")
          public String temperature_threshold="1000";
//          public String temperature_threshold="35.6";
        @DynamicProperty(json_key = "tempOffset")
          public String temperature_offset="-2";
          @DynamicProperty(json_key = "livenessScore")
          public String livelinessThreshold="0.5";
          @DynamicProperty(json_key = "allowVisitors")
          public String allowVisitors="false";
}
