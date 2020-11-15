package sparta.realm.spartamodels.wc.sim;

import java.io.Serializable;

import sparta.realm.spartautils.svars;
import sparta.spartaannotations.DynamicClass;
import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.SyncDescription;
import sparta.spartaannotations.db_class_;

import static sparta.spartaannotations.SyncDescription.service_type.Upload;



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
