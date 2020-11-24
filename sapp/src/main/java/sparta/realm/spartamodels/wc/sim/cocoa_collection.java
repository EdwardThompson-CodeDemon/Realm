package sparta.realm.spartamodels.wc.sim;

import java.io.Serializable;
import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import static com.realm.annotations.SyncDescription.service_type.Download;


@DynamicClass(table_name = "field_collections")
@SyncDescription(service_name = "jobCocoaFieldCollections", service_type = Download, download_link = svars.Cocoa_collection_download_link)
public class cocoa_collection extends db_class_ implements Serializable {


    @DynamicProperty(json_key = "user_id", column_name = "user_id")
    public String user_id="";

    @DynamicProperty(json_key = "supplier_id", column_name = "supplier_id")
    public String supplier_id="";

    @DynamicProperty(json_key = "collection_date", column_name = "collection_date")
    public String collection_date="";

    @DynamicProperty(json_key = "tag_no", column_name = "tag_no")
    public String tag_no="";

    @DynamicProperty(json_key = "quantity", column_name = "quantity")
    public String member_id="quantity";

    @DynamicProperty(json_key = "datecomparer", column_name = "datecomparer")
    public String datecomparer="";

    @DynamicProperty(json_key = "cocoa_dispatch_session", column_name = "cocoa_dispatch_session")
    public String cocoa_dispatch_session="";

    @DynamicProperty(json_key = "vehicle_registration_id", column_name = "vehicle_registration_id")
    public String vehicle_registration_id="";

    @DynamicProperty(json_key = "id", column_name = "id")
    public String id="";

    @DynamicProperty(json_key = "CentreId", column_name = "CentreId")
    public String CentreId="";


    public cocoa_collection()  {
    }
}
