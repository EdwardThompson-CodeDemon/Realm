package sparta.realm.spartamodels.wc.sim;

import java.io.Serializable;

import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import static com.realm.annotations.SyncDescription.service_type.Upload;



@DynamicClass(table_name = "TBL_agent_totals")
@SyncDescription(service_name = "AgentTotals", service_type = Upload, upload_link = svars.agent_totals_upload_link)

public class AgentTotals extends db_class_ implements Serializable {

    @DynamicProperty(json_key = "user_id", column_name = "unit_of_measure_id")
    public String user_id="";

    @DynamicProperty(json_key = "collection_date", column_name = "sync_datetime")
    public String collection_date="";

    @DynamicProperty(json_key = "quantity", column_name = "net")
    public String quantity="";


    public AgentTotals() {
    }
}
