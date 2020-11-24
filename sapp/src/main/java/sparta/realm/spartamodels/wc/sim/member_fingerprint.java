package sparta.realm.spartamodels.wc.sim;
import java.io.Serializable;

import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import static com.realm.annotations.SyncDescription.service_type.Download;
import static com.realm.annotations.SyncDescription.service_type.Upload;


@DynamicClass(table_name = "finger_prints")
@SyncDescription(service_name = "JobgetFingerPrints", service_type = Download, download_link = svars.Fingerprint_downloading_link,use_download_filter = false)
@SyncDescription(service_name = "InsertFingerPrints", service_type = Upload, upload_link = svars.Fingerprint_uploading_link)

public class member_fingerprint extends db_class_ implements Serializable {

    @DynamicProperty(json_key = "characters", column_name = "finger_print")
    public String characters="";

    @DynamicProperty(json_key = "human_body_id", column_name = "finger_id")
    public String human_body_id="";

    @DynamicProperty(json_key = "", column_name = "status")
    public String datecomparer="";

    @DynamicProperty(json_key = "", column_name = "member_no")
    public String member_no="";

    @DynamicProperty(json_key = "member_id", column_name = "member_id")
    public String member_id="";





    public member_fingerprint()
    {

    }
}
