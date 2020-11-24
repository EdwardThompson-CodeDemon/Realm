package sparta.realm.spartamodels.wc.sim;

import java.io.Serializable;

import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import static com.realm.annotations.SyncDescription.service_type.Upload;




@DynamicClass(table_name = "MC_dispatch")
@SyncDescription(service_name = "InsertCocoaDispatch", service_type = Upload, upload_link = svars.Cocoa_dispatch_session_upload_link)

public class cocoa_dispatch_session extends db_class_ implements Serializable {

    @DynamicProperty(json_key = "cocoa_dispatch_session", column_name = "session_no")
    public String session_no = "";

    @DynamicProperty(json_key = "tag_no", column_name = "tag_no")
    public String tag_no = "";


    public cocoa_dispatch_session() {


    }

}
