package sparta.realm.spartamodels;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import sparta.spartaannotations.DynamicClass;
import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.SyncDescription;
import sparta.spartaannotations.db_class_;

@DynamicClass(table_name = "member_data_table")
@SyncDescription(service_name = "Member fingerprints",service_type = SyncDescription.service_type.Upload,upload_link = svars.Fingerprint_uploading_link_ann,table_filters ={"data_type='"+ svars.data_type_indexes.fingerprints+"'"},use_download_filter =true,chunk_size =svars.fingerprints_request_limit )
@SyncDescription(service_name = "Member images",service_type = SyncDescription.service_type.Upload,upload_link = svars.Image_uploading_link_ann,table_filters ={"data_type='"+ svars.data_type_indexes.photo+"'"},chunk_size =svars.images_request_limit )
@SyncDescription(service_name = "Member FP Images",service_type = SyncDescription.service_type.Upload,upload_link = svars.Fingerprint_image_uploading_link_ann,table_filters ={"data_type='"+ svars.data_type_indexes.fingerprint_images_wsq+"'"},chunk_size =svars.images_request_limit )
@SyncDescription(service_name = "Member Excuses",service_type = SyncDescription.service_type.Upload,upload_link = svars.Fingerprint_excuses_uploading_link_ann,table_filters ={"data_type='"+ svars.data_type_indexes.fingerprint_skipping_reason+"'"},chunk_size =svars.excuse_request_limit )
public class member_data_ann extends db_class_ implements Serializable {

    @DynamicProperty(json_key = "biometric_type_id", column_name = "data_type")
    public String data_type="";

    @DynamicProperty(json_key = "index_no", column_name = "data_index")
 public String data_index="";

    @DynamicProperty(json_key = "image_name", column_name = "data")
 public String data="";

    @DynamicProperty(json_key = "data_format", column_name = "data_format")
 public String data_format="";

    @DynamicProperty(json_key = "data_storage_mode", column_name = "data_storage_mode", indexed_column = true)
 public String data_storage_mode="";

    @DynamicProperty(json_key = "employee_details_id", column_name = "member_id")
    public String member_id="";










    public member_data_ann()
    {


    }

}
