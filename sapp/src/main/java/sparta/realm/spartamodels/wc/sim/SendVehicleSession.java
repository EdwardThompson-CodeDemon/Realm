package sparta.realm.spartamodels.wc.sim;

import java.io.Serializable;

import sparta.realm.spartautils.svars;
import sparta.spartaannotations.DynamicClass;
import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.SyncDescription;
import sparta.spartaannotations.db_class_;

import static sparta.spartaannotations.SyncDescription.service_type.Upload;

/**
 * Created by Ngeru_Sk
 */

//@DynamicClass(table_name = "coopdispatch_session")
//@SyncDescription(service_name = "InsertWeighBridgeDispatchSession",service_type = Upload,upload_link = svars.Center_download_link)

public class SendVehicleSession extends db_class_ implements Serializable {


    @DynamicProperty(json_key = "vehicle_registration_id", column_name = "vehicle_id")
    public String vehicle_registration_id="";

    @DynamicProperty(json_key = "session", column_name = "session_no")
    public String session="";

    @DynamicProperty(json_key = "driver_name", column_name = "")
    public String driver_name="";

    @DynamicProperty(json_key = "driver_telephone", column_name = "")
    public String driver_telephone="";




    public SendVehicleSession()
    {

    }
}
