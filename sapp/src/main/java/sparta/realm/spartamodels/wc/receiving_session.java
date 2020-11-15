package sparta.realm.spartamodels.wc;

import java.io.Serializable;


import sparta.realm.spartautils.svars;
import sparta.spartaannotations.DynamicClass;
import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.SyncDescription;
import sparta.spartaannotations.db_class_;



import static sparta.spartaannotations.SyncDescription.service_type.Upload;


@DynamicClass(table_name = "dispatch_session")
@SyncDescription(service_name = "InsertReceivingSession", service_type = Upload, download_link = svars.receiving_session_upload_link)

public class receiving_session extends db_class_ implements Serializable {

    @DynamicProperty(json_key = "vehicle_registration_id", column_name = "vehicle_id")
    public String vehicle_registration_id="";

    @DynamicProperty(json_key = "session", column_name = "session_no")
    public String session="";

    @DynamicProperty(json_key = "driver_name", column_name = "driver_name")
    public String driver_name="";

    @DynamicProperty(json_key = "driver_telephone", column_name = "driver_telephone")
    public String driver_telephone="";

    public receiving_session() {
    }
}
