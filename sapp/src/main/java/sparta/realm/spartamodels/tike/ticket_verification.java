package sparta.realm.spartamodels.tike;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import java.io.Serializable;

import sparta.realm.spartautils.svars;

import static com.realm.annotations.SyncDescription.service_type.Download;
import static com.realm.annotations.SyncDescription.service_type.Upload;

/*

 */
@DynamicClass(table_name = "ticket_verifications")
@SyncDescription(service_id="3",service_name = "Ticket verification",service_type = Upload,download_link = svars.Center_download_link)
public class ticket_verification extends db_class_ implements Serializable {


    @DynamicProperty(json_key = "qr_code", column_name = "qr_code")
   public String qr_code="";

    @DynamicProperty(json_key = "ticket_id", column_name = "ticket_id")
    public String ticket_id="";

    @DynamicProperty(json_key = "device_code", column_name = "device_code")
    public String device_code="";

    @DynamicProperty(json_key = "verification_time", column_name = "verification_time")
    public String verification_time="";

    @DynamicProperty(json_key = "verification_mode", column_name = "verification_mode")
    public String verification_mode="";//either qr or nfc

//json object qr_code,ticket_id,device_code,verification_time,verification_mode







    public ticket_verification()
    {




    }

}
