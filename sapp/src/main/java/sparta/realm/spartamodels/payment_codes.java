package sparta.realm.spartamodels;


import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicProperty;

/**
 * Created by Thompsons on 1/8/2018.
 */

public class payment_codes extends db_class{





   // @DynamicProperty(json_name = "pay_code",db_column_name = "pay_code" )
    //public String pay_code="";
    public dynamic_property pay_code=new dynamic_property("pay_code","pay_code","");
   // @DynamicProperty(json_name = "code_status",db_column_name = "code_status")
    public dynamic_property code_status=new dynamic_property("code_status","code_status","");

   // @DynamicProperty(json_name = "date_time",db_column_name = "date_time")
    public dynamic_property date_time=new dynamic_property("date_time","date_time","");


    public payment_codes()
    {
        super("payment_codes_table");
        sync_service_description sd=new sync_service_description();
        sd.service_name= "Payment codes";

        sd.download_link= svars.payment_code_download_link;
        sd.servic_type= sync_service_description.service_type.Download;

        sd.use_download_filter= false;


        sd.table_name= table_name;
        ssds=new sync_service_description[]{sd};

    }
    public payment_codes(String lid, String sid, String pay_code, String code_status, String date_time)
    {
        super("payment_codes_table");
        this.sid.value=sid;
        this.pay_code.value=pay_code;
        this.code_status.value=code_status;
        this.date_time.value=date_time;
        id.value=lid;


    }



}
