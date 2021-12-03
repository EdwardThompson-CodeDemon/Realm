package sparta.realm.spartamodels.timecap.Models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import java.io.Serializable;


@DynamicClass(table_name ="verifications_table")
///Api_v1.0/Camera/AddCamVerificationApi_v1.0/Camera/AddRejectedImages
//Api_v1.0/Camera/AddVerificationImages
//Api_v1.0/Camera/GetDeviceConfig
//Api_v1.0/Camera/AddCamVerification
//Api_v1.0/Camera/GetDeviceLicense/{devicekey}
//@SyncDescription(service_id = "5",service_name = "Verification",service_type = SyncDescription.service_type.Upload,upload_link = "/Api_v1.0/Camera/AddTemp",table_filters = {"data_status IS NULL "},chunk_size = 2000)
@SyncDescription(service_id = "5",service_name = "Verification",service_type = SyncDescription.service_type.Upload,upload_link = "/Api_v1.0/Camera/AddCamVerification",table_filters = {"data_status IS NULL "},chunk_size = 2000)



public class verification extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "deviceKey",column_name = "device_code")
    public String device_code2="";

  @DynamicProperty(json_key = "personId")
    public String member_id="";


  @DynamicProperty(json_key = "time")
    public String epoch_time="";

    @DynamicProperty(column_name = "transaction_no",json_key = "transaction_no")
    public String transaction_no="";

 @DynamicProperty(json_key = "type",column_default_value = "'face_0'")
    public String verification_type="";


 @DynamicProperty(column_name = "device_ip"/*,json_key = "ip"*/)
    public String device_ip="";

    @DynamicProperty(json_key = "imgType",column_default_value = "'0'")
    public String image_type="";


    @DynamicProperty(json_key = "temperature")
   public String temperature="";

    @DynamicProperty(json_key = "path",column_default_value = "'Not Valid'")
   public String path="";

    @DynamicProperty(json_key = "allowed")
 public String allowed="";



 @DynamicProperty(json_key = "searchScore")
 public String match_score="";


@DynamicProperty(json_key = "livenessScore")
 public String liveliness="";

    @DynamicProperty(json_key = "fees_allowed")
    public String fees_allowed="";










    public verification()
    {


    }

}
