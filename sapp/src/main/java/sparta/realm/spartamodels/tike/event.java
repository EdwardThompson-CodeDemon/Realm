package sparta.realm.spartamodels.tike;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import java.io.Serializable;

import sparta.realm.spartautils.svars;

import static com.realm.annotations.SyncDescription.service_type.Download;

/*

 */
@DynamicClass(table_name = "events")
@SyncDescription(service_id="1",service_name = "Events",service_type = Download,download_link = svars.Center_download_link)
public class event extends db_class_ implements Serializable {


    @DynamicProperty(json_key = "event_name", column_name = "event_name")
   public String event_name="";

    @DynamicProperty(json_key = "start_time", column_name = "start_time")
    public String start_time="";

    @DynamicProperty(json_key = "stop_time", column_name = "stop_time")
    public String stop_time="";






    public event()
    {




    }

}
