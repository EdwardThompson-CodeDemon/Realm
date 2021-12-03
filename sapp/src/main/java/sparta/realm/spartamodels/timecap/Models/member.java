package sparta.realm.spartamodels.timecap.Models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import java.io.Serializable;


@DynamicClass(table_name = "member_info_table")
//@SyncDescription(service_id = "1",/*download_column_filter={"department"},*/service_name = "Member",service_type = SyncDescription.service_type.Download,download_link = "/Api_v1.0/Camera/GetCameraEmployeesRecords",download_array_position = "JO:Result;JO:Result",use_download_filter = true,chunk_size = 5000)
//@SyncDescription(service_name = "Member",service_type = SyncDescription.service_type.Download,download_link = "/Employees/Details/GetMobileRecords",use_download_filter = true,is_ok_position = "JO:isOkay",download_array_position = "JO:result",chunk_size = 5000)
//@SyncDescription(service_id = "1",service_name = "Member",service_type = SyncDescription.service_type.Download,download_link = "/Students/Details/GetMobileRecords",use_download_filter = true,is_ok_position = "JO:isOkay",download_array_position = "JO:result",chunk_size = 10000)
@SyncDescription(service_id = "1",service_name = "Member",service_type = SyncDescription.service_type.Download,download_link = "/Api_v1.0/Mobile/GetMobileRecords",use_download_filter = true,is_ok_position = "JO:isOkay",download_array_position = "JO:result;JO:result",chunk_size = 10000)
public class member extends db_class_ implements Serializable {

//@DynamicProperty(json_key = "worker_name")//dole
@DynamicProperty(json_key = "full_name")//uipa
   public String fullname="";


//    @DynamicProperty(json_key = "employee_no")
    @DynamicProperty(json_key = "member_no")
 public String idno="";

    @DynamicProperty(json_key = "phone1")
 public String phoneno="";



    @DynamicProperty(json_key = "email")
 public String email="";


  @DynamicProperty(json_key = "department")
 public String department="";



    @DynamicProperty(json_key = "msid")
    public String msid="";




    public member()
    {


    }

}
