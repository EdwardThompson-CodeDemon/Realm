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

//@DynamicClass(table_name = "MC_dispatch")
//@SyncDescription(service_name = "InsertCocoaDispatch", service_type = Upload, upload_link = svars.Update_dispatch_link)

public class UpdateDispatchCollection extends db_class_ implements Serializable {

    @DynamicProperty(json_key = "cocoa_dispatch_session", column_name = "session_no")
    public String cocoa_dispatch_session = "";

    @DynamicProperty(json_key = "tag_no", column_name = "tag_no")
    public String tag_no = "";


    public UpdateDispatchCollection() {


    }

}
