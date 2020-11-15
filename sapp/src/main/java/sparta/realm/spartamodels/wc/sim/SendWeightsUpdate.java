package sparta.realm.spartamodels.wc.sim;

import java.io.Serializable;

import sparta.spartaannotations.DynamicClass;
import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.SyncDescription;
import sparta.spartaannotations.db_class_;

import static sparta.spartaannotations.SyncDescription.service_type.Upload;

/**
 * Created by Ngeru_Sk
 */

//@DynamicClass(table_name = "jobUpdateWeighBridge")
//@SyncDescription(service_name = "jobCocoaWeightBridgeCollections", service_type = Upload, upload_link = svars.send_weights_update_link)

public class SendWeightsUpdate extends db_class_ implements Serializable {

    @DynamicProperty(json_key = "tag_no", column_name = "tag_no")
    public String tag_no="";

    @DynamicProperty(json_key = "comment", column_name = "dispatch_session")
    public String weighbridge_id="";

    @DynamicProperty(json_key = "cocoa_buyer_id", column_name = "buyer")
    public String cocoa_buyer_id="";

    @DynamicProperty(json_key = "user_id", column_name = "")
    public String user_id="";

    @DynamicProperty(json_key = "dispatch_date", column_name = "dispatch_date")
    public String dispatch_date="";

    @DynamicProperty(json_key = "weighbridge_id", column_name = "weighbridge_id")
    public String weighbridge_id2="";


    public SendWeightsUpdate() {
    }
}
