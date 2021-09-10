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
@DynamicClass(table_name = "tickets")
//@SyncDescription(service_id="2",service_name = "Tickets",service_type = Download,chunk_size = 50000)
//@SyncDescription(service_id="7",service_name = "Tickets",service_type = Upload)
public class ticket extends db_class_ implements Serializable {


    @DynamicProperty(json_key = "qr_code", column_name = "qr_code")
   public String qr_code="";

    @DynamicProperty(json_key = "event_id", column_name = "event_id")
    public String event_id="";

    @DynamicProperty(json_key = "seat_range_id")
    public String seat_range_id="";

    @DynamicProperty(json_key = "ticket_all_events")
    public String seat_no="";

    @DynamicProperty(json_key = "seat_no")
    public String ticket_all_events="";

    @DynamicProperty(json_key = "device_code")
    public String device_code="";

    @DynamicProperty(json_key = "ticket_price_id")
    public String ticket_price_id="";

    @DynamicProperty(json_key = "transaction_no")
    public String transaction_no="";
    public ticket()
    {

    }

}
