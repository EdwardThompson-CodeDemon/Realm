package sparta.realm.spartamodels.timecap.Models;

import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;


/**
 * Created by Thompsons on 1/8/2018.
 */

@DynamicClass(table_name = "fees_details_table")
@SyncDescription(service_id = "8",use_download_filter = false,service_name = "Fees details",service_type = SyncDescription.service_type.Download,chunk_size = 6000,download_link = "/Students/Fees/GetStudentFeesStatus",is_ok_position = "JO:isOkay",download_array_position = "JO:result;JA:arr")
@SyncDescription(service_id = "8",use_download_filter = false,service_name = "Fees details",service_type = SyncDescription.service_type.Upload,chunk_size = 6000,download_link = "/Students/Fees/GetStudentFeesStatus",is_ok_position = "JO:isOkay",download_array_position = "JO:result")
@SyncDescription(service_id = "8",use_download_filter = false,service_name = "Fees details",service_type = SyncDescription.service_type.Download,chunk_size = 6000,download_link = "/Students/Fees/GetStudentFeesStatus",is_ok_position = "JO:isOkay",download_array_position = "JO:result")
public class fees_details extends db_class_ {


public static final String ed=null;
@DynamicProperty(json_key = "student_id")
    public String member_id="";
@DynamicProperty(column_name = "total_amount")
    public String total_amount="";
@DynamicProperty(column_name = "payed")
    public String payed="";


    @DynamicProperty(json_key = "fee_bal_perperiod")
    public String balance="";

    @DynamicProperty(json_key = "allowed")
    public String allowed="";

    @DynamicProperty(json_key = "fees_type")
    public String fees_type="";

    @DynamicProperty(json_key = "expiry")
    public String expiry_date="";

    @DynamicProperty(json_key = "cumulative_bal")
    public String cumulative_bal="";

    @DynamicProperty(column_name = "camera_sync_status")
    public String camera_sync_status="";
    public fees_details()
    {

    }
}
