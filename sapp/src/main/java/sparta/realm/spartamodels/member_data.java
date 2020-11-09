package sparta.realm.spartamodels;


import java.io.Serializable;

import sparta.realm.spartautils.svars;


public class member_data extends db_class implements Serializable {


   public dynamic_property data_type=new dynamic_property("biometric_type_id","data_type",null,true);
 public dynamic_property data_index=new dynamic_property("index_no","data_index",null,true);
 public dynamic_property data=new dynamic_property("image_name","data",null,false);
 public dynamic_property data_format=new dynamic_property("","data_format",null,false);
 public dynamic_property data_storage_mode=new dynamic_property("data_storage_mode","data_storage_mode",null,true);
 public dynamic_property member_id=new dynamic_property("employee_details_id","member_id",null,false);










    public member_data()
    {
        super("member_data_table");
        sync_service_description sd=new sync_service_description();
        sd.service_name= "Member fingerprints";
        sd.table_filters=new String[]{"data_type='"+ svars.data_type_indexes.fingerprints+"'"};
        sd.upload_link= svars.Fingerprint_uploading_link;
      //  sd.download_link= svars.Fingerprint_downloading_link;
        sd.servic_type= sync_service_description.service_type.Upload;
        sd.chunk_size= svars.fingerprints_request_limit;
        sd.use_download_filter= true;
        sd.table_name= table_name;


 sync_service_description sd2=new sync_service_description();
        sd2.service_name= "Member images";
        sd2.table_filters=new String[]{"data_type='"+svars.data_type_indexes.photo+"'"};
        sd2.upload_link= svars.Image_uploading_link;
       // sd2.download_link= svars.Image_downloading_link;
        sd2.servic_type= sync_service_description.service_type.Upload;
        sd2.chunk_size= svars.images_request_limit;
        sd2.use_download_filter= true;
        sd2.table_name= table_name;


 sync_service_description sd3=new sync_service_description();
        sd3.service_name= "Member FP Images";
        sd3.table_filters=new String[]{"data_type='"+svars.data_type_indexes.fingerprint_images_wsq+"'"};
        sd3.upload_link= svars.Fingerprint_image_uploading_link;
       // sd2.download_link= svars.Image_downloading_link;
        sd3.servic_type= sync_service_description.service_type.Upload;
        sd3.chunk_size= svars.images_request_limit;
        sd3.use_download_filter= true;
        sd3.table_name= table_name;


sync_service_description sd4=new sync_service_description();
        sd4.service_name= "Member Excuses";
        sd4.table_filters=new String[]{"data_type='"+svars.data_type_indexes.fingerprint_skipping_reason+"'"};
        sd4.upload_link= svars.Fingerprint_excuses_uploading_link;
       // sd2.download_link= svars.Image_downloading_link;
        sd4.servic_type= sync_service_description.service_type.Upload;
        sd4.chunk_size= svars.images_request_limit;
        sd4.use_download_filter= true;
        sd4.table_name= table_name;



        ssds=new sync_service_description[]{sd,sd2,sd3,sd4};

    }

}
