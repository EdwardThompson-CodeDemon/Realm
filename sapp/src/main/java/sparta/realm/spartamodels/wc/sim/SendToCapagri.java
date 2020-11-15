package sparta.realm.spartamodels.wc.sim;

import java.io.Serializable;

import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.db_class_;

/**
 * Created by Ngeru_Sk
 */

//@DynamicClass(table_name = "farmer_advances")
//@SyncDescription(service_name = "Capagri", service_type = Upload, upload_link = svars.send_capagri_link)


public class SendToCapagri extends db_class_ implements Serializable {
    @DynamicProperty(json_key = "FarmerId", column_name = "farmer_id")
    public String FarmerId="";

    @DynamicProperty(json_key = "TotalKGSCollected", column_name = "netweight")
    public String TotalKGSCollected="";

    @DynamicProperty(json_key = "RequestedAmount", column_name = "amount_requsted")
    public String RequestedAmount="";

    @DynamicProperty(json_key = "CollectionCenter", column_name = "center_id")
    public String CollectionCenter="";

    @DynamicProperty(json_key = "Delegate", column_name = "delegate")
    public String Delegate="";

    public SendToCapagri() {
    }
}
