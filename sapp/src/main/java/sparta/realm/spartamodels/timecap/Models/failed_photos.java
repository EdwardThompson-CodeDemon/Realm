package sparta.realm.spartamodels.timecap.Models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;

import java.io.Serializable;


@DynamicClass(table_name ="failed_photos_table")
@SyncDescription(service_id = "4",service_name = "Failed images",service_type = SyncDescription.service_type.Upload,upload_link = "/Api_v1.0/Camera/AddRejectedImages",chunk_size = 1)
//@SyncDescription(service_name = "Failed images",service_type = SyncDescription.service_type.Upload,upload_link = "/Api_v1.0/Camera/AddRejectedImages",chunk_size = 1)
//@SyncDescription(service_name = "Failed images",service_type = SyncDescription.service_type.Upload,upload_link = "/Api_v1.0/Camera/AddCamVerificationApi_v1.0/Camera/AddRejectedImages",chunk_size = 1)
///Api_v1.0/Camera/AddCamVerificationApi_v1.0/Camera/AddRejectedImages
//Api_v1.0/Camera/AddVerificationImages
//Api_v1.0/Camera/GetDeviceConfig
//Api_v1.0/Camera/AddCamVerification
//Api_v1.0/Camera/GetDeviceLicense/{devicekey}
public class failed_photos  implements Serializable {

    @DynamicProperty(json_key = "is_active",column_name = "data_status")
    public String data_status = "";

    @DynamicProperty(column_name = "_id",json_key = "lid",column_data_type = "INTEGER",extra_params = "PRIMARY KEY AUTOINCREMENT")
    public String id = "";
    @DynamicProperty(json_key = "id",column_name = "sid",indexed_column = true,extra_params = "UNIQUE")
    public String sid = "";
    @DynamicProperty(column_name = "reg_time",column_data_type = "DATETIME",column_default_value = "(datetime('now','localtime'))")
    public String reg_time = "";
    @DynamicProperty(json_key = "sync_status", column_name = "sync_status")
    public String sync_status = "";

    @DynamicProperty(json_key = "biometric_details_id")
   public String image_id="";

 @DynamicProperty(json_key = "datecomparer")
   public String image_datecompairer="";

 @DynamicProperty(json_key = "device_code")
 public String device_code="";










    public failed_photos()
    {

    }

}
