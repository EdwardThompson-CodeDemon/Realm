package sparta.realm.spartamodels.timecap.Models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import java.io.Serializable;


@DynamicClass(table_name ="member_data_table")
@SyncDescription(service_id = "3",service_name = "Images",service_type = SyncDescription.service_type.Download,download_link = "/Api_v1.0/Mobile/GetImagesRecords",chunk_size = 10,is_ok_position = "JO:isOkay",download_array_position = "JO:result;JO:result",storage_mode_check = true)//uipa
//@SyncDescription(service_id = "3",service_name = "Images",service_type = SyncDescription.service_type.Download,download_link = "/Biometrics/BiometricsDetails/GetCameraImages",chunk_size = 100,is_ok_position = "JO:isOkay",download_array_position = "JO:result",storage_mode_check = true)//uipa
//@SyncDescription(service_id = "2",service_name = "Images",service_type = SyncDescription.service_type.Download,download_link = "/Api_v1.0/Camera/GetImagesToCameraPerDepartments",use_download_filter = true,chunk_size = 5000,is_ok_position = "JO:IsOkay",download_array_position = "JO:Result;JO:Result")
//@SyncDescription(service_name = "Member images",service_type = SyncDescription.service_type.Upload,upload_link = "/Biometrics/BiometricsDetails/Add",table_filters = {"data_status IS NULL "," data_format NOT NULL"},chunk_size = 1)
//@SyncDescription(service_name = "Member images",service_type = SyncDescription.service_type.Upload,upload_link = "/Employee/BiometricDetails/Add",table_filters = {"data_status IS NULL "," data_format NOT NULL"},chunk_size = 1)
@SyncDescription(service_id = "2",service_name = "Member images",service_type = SyncDescription.service_type.Upload,upload_link = "/Api_v1.0/Camera/AddCamImageTwo",chunk_size = 1,storage_mode_check = true)
//@SyncDescription(service_name = "Member images",service_type = SyncDescription.service_type.Download,download_link = "/Biometrics/BiometricsDetails/GetCameraImages",use_download_filter = true,chunk_size = 1,is_ok_position = "JO:isOkay",download_array_position = "JO:result")
//@SyncDescription(service_name = "Member images",service_type = SyncDescription.service_type.Download,download_link = "/Biometrics/BiometricsDetails/GetImages",use_download_filter = true,chunk_size = 1,is_ok_position = "JO:isOkay",download_array_position = "JO:result")

public class member_data extends db_class_ implements Serializable {///Api_v1.0/Camera/AddCamImageTwo

//    @DynamicProperty(json_key = "personId")//uipa
    @DynamicProperty(json_key = "employee_details_id")//uipa download
//    @DynamicProperty(json_key = "member_id")//dole
    public String member_id="";

    @DynamicProperty(json_key = "msid")
   public String msid="";



    @DynamicProperty(json_key = "biometric_type_id")
   public String data_type="";

    @DynamicProperty(json_key = "biometric_type_details_id")
    public String data_index="";

    @DynamicProperty(column_name = "transaction_no",json_key = "transaction_no")
    public String transaction_no="";



     @DynamicProperty(column_name = "data",json_key = "biometric_string",storage_mode = DynamicProperty.storage_mode.FilePath)
//    @DynamicProperty(json_key = "imgBase64",storage_mode = DynamicProperty.storage_mode.FilePath)
    public String data="";

    @DynamicProperty(json_key = "biometric_type_id")
 public String data_format="";


    @DynamicProperty(json_key = "data_storage_mode")
 public String data_storage_mode="";




    @DynamicProperty(json_key = "optimization_status")
 public String optimization_status="";

    @DynamicProperty(column_name = "camera_sync_status")
    public String camera_sync_status="";

   @DynamicProperty(json_key = "url_path")
    public String url="";


    @DynamicProperty(column_name = "download_status",column_default_value = "1")
    public String download_status="";

  @DynamicProperty(json_key = "face_token")
    public String face_token="";








    public member_data()
    {


    }

}
