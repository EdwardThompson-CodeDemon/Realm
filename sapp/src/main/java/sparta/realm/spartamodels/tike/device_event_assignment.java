package sparta.realm.spartamodels.tike;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import java.io.Serializable;

import static com.realm.annotations.SyncDescription.service_type.Download;

/*

 */
@DynamicClass(table_name = "device_event_assignment")
@SyncDescription(service_id="5",service_name = "Device Events",service_type = Download)
public class device_event_assignment extends db_class_ implements Serializable {


    @DynamicProperty(json_key = "device_code", column_name = "device_code")
   public String device_code="";

    @DynamicProperty(json_key = "event_id", column_name = "event_id")
    public String event_id="";


    public device_event_assignment()
    {

    }

}
