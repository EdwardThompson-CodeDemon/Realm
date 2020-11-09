package sparta.realm.spartamodels;

import sparta.spartaannotations.DynamicClass;
import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.SyncDescription;
import sparta.spartaannotations.db_class_;

@DynamicClass(table_name = "table_test")
//@SyncDescription(service_type = SyncDescription.service_type.Download,download_link = "",upload_link = "",chunk_size =0 )
public class testmodel extends db_class_ {
    @DynamicProperty(column_name = "id", json_key = "id_j", indexed_column = true)
    public int ids =0;




}
