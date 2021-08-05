package sparta.realm.spartamodels.timecap;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import java.io.Serializable;

import sparta.realm.spartautils.svars;

import static com.realm.annotations.SyncDescription.service_type.Download;


@DynamicClass(table_name = "member_data_table")
@SyncDescription(service_name = "member_data_table",chunk_size = 10000,service_type = Download,download_link = "/Biometrics/BiometricsDetails/GetFingerPrints",is_ok_position = "JO:isOkay",download_array_position = "JO:result;JO:result")
public class member_data extends db_class_ implements Serializable {


    @DynamicProperty(json_key = "biometric_type_id",column_name = "data_type")
    public String data_type="";
    @DynamicProperty(json_key = "biometric_type_details_id",column_name = "data_index")
    public String data_index="";
    @DynamicProperty(json_key = "biometric_string",column_name = "data")
    public String data="";
    @DynamicProperty(json_key = "data_format",column_name = "data_format")
    public String data_format="";
    @DynamicProperty(json_key = "data_storage_mode",column_name = "data_storage_mode")
    public String data_storage_mode="";
    @DynamicProperty(json_key = "employee_details_id",column_name = "member_id")
 public String member_id="";

//    public dynamic_property data_type=new dynamic_property("biometric_type_id","data_type",null,true);
//    public dynamic_property data_index=new dynamic_property("biometric_type_details_id","data_index",null,true);
//    public dynamic_property data=new dynamic_property("biometric_string","data",null,false);
//    public dynamic_property data_format=new dynamic_property("","data_format",null,false);
//    public dynamic_property data_storage_mode=new dynamic_property("data_storage_mode","data_storage_mode",null,true);
//    public dynamic_property member_id=new dynamic_property("employee_details_id","member_id",null,false);
//    public dynamic_property optimization_status=new dynamic_property("optimization_status","optimization_status",null,false);









    public member_data()
    {





    }

}
