package sparta.realm.spartamodels.wc.sim;

import java.io.Serializable;

import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import static com.realm.annotations.SyncDescription.service_type.Upload;


@DynamicClass(table_name = "coopdispatch_session")
@SyncDescription(service_name = "InsertWeighBridgeDispatchSession",service_type = Upload,upload_link = svars.Vehicle_dispatch_session_upload_link)

public class vehicle_dispatch_session extends db_class_ implements Serializable {


    @DynamicProperty(json_key = "vehicle_registration_id", column_name = "vehicle_id")
    public String vehicle_registration_id="";

    @DynamicProperty(json_key = "session", column_name = "session_no")
    public String session="";

    @DynamicProperty(json_key = "driver_name", column_name = "driver_name")
    public String driver_name="";

    @DynamicProperty(json_key = "driver_telephone", column_name = "driver_telephone")
    public String driver_telephone="";




    public vehicle_dispatch_session()
    {

    }
}
