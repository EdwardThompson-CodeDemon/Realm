package sparta.realm.spartamodels.timecap.Models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import java.io.Serializable;


@DynamicClass(table_name ="verification_data_table")
@SyncDescription(service_id = "6",service_name = "Verification images",service_type = SyncDescription.service_type.Upload,upload_link = "/Api_v1.0/Camera/AddVerificationImages",chunk_size = 1,storage_mode_check = true)
///Api_v1.0/Camera/AddCamVerificationApi_v1.0/Camera/AddRejectedImages
//Api_v1.0/Camera/AddVerificationImages
//Api_v1.0/Camera/GetDeviceConfig
//Api_v1.0/Camera/AddCamVerification
//Api_v1.0/Camera/GetDeviceLicense/{devicekey}//{
//        "personId":"",
//        "deviceKey":"",
//        "transaction_no":"1622647519819",
//        "imgBase64":"base64string"
//        }

public class verification_data extends db_class_ implements Serializable {


  @DynamicProperty(column_name = "transaction_no",json_key = "transaction_no")
    public String transaction_no="";

    @DynamicProperty(json_key = "imgBase64",storage_mode = DynamicProperty.storage_mode.FilePath)
 public String data="";

    @DynamicProperty(json_key = "personId")
 public String member_id="";

    @DynamicProperty(json_key = "deviceKey",column_name = "device_code")
    public String device_code2="";










    public verification_data()
    {

    }

}
