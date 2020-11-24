package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;



import static com.realm.annotations.SyncDescription.service_type.Download;

/*
 gridcollection.put("start_date",c.getString(1));
                gridcollection.put("vehicle_registration_id",c.getString(4));
                gridcollection.put("session",c.getString(3));

 String query = "SELECT _id, datetime(sync_date,'unixepoch','localtime'), datetime(sync_date2,'unixepoch','localtime'), sync_date, vehicle_id from TBL_sessions where status = ? AND vehicle_id <> 0 LIMIT 1";

 */




@DynamicClass(table_name = "ActivitySessions")
@SyncDescription(service_name = "JobSessions",service_type = Download,download_link = svars.Route_download_link,chunk_size =svars.excuse_request_limit,table_filters = "type=1")
@SyncDescription(service_name = "JobSessions",service_type = Download,download_link = svars.Route_download_link,chunk_size =svars.excuse_request_limit,table_filters = "type=2")
public class sessions extends db_class_ implements Serializable {

public static int type1=0;
public static int type2=0;

    @DynamicProperty(json_key = "inventory_id", column_name = "sync_date")
   public String session="";

    @DynamicProperty(json_key = "vehicle_registration_id", column_name = "vehicle_id")
    public String vehicle_id="";

    @DynamicProperty(json_key = "start_date", column_name = "sync_date")
    public String sync_date="";





 @DynamicProperty( column_name = "type")
    public String type="";




    public sessions()
    {




    }

}
