package sparta.realm.spartamodels;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import sparta.spartaannotations.DynamicClass;
import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.SyncDescription;
import sparta.spartaannotations.db_class_;

import static sparta.spartaannotations.SyncDescription.service_type.Download;


@DynamicClass(table_name = "member_info_tablez")
@SyncDescription(service_name = "Member",service_type = Download,download_link = svars.Member_download_link_ann,upload_link = svars.Member_upload_link_ann,use_download_filter =true,chunk_size =svars.members_request_limit )
public class member_z extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "full_name", column_name = "full_name")
   public String full_name="";

    @DynamicProperty(json_key = "member_no", column_name = "member_no")
    public String member_no="";

    @DynamicProperty(json_key = "marital_status_id", column_name = "marital_status_id")
    public String marital_status="";//new dynamic_property("marital_status_id","marital_status_id",null,false);

    @DynamicProperty(json_key = "phone1", column_name = "phone1")
 public String phone="";//new dynamic_property("phone1","phone1",null,false);

    @DynamicProperty(json_key = "gender_type_id", column_name = "gender_type_id")
    public String gender_type_id="";//new dynamic_property("gender_type_id","gender_type_id",null,false);
    @DynamicProperty(json_key = "nat_id", column_name = "nat_id")
 public String nat_id="";//ew dynamic_property("nat_id","nat_id",null,false);
    @DynamicProperty(json_key = "membership_type_id", column_name = "membership_type_id")
    public String membership_type_id="";//ew dynamic_property("membership_type_id","membership_type_id",null,false);
    @DynamicProperty(json_key = "membership_sub_type_id", column_name = "membership_sub_type_id")
 public String membership_sub_type_id="";//ew dynamic_property("membership_sub_type_id","membership_sub_type_id",null,true);
    @DynamicProperty(json_key = "approval_status", column_name = "approval_status")
public String approval_status="";//ew dynamic_property("approval_status","approval_status",null,true);
    @DynamicProperty(json_key = "route_id", column_name = "route_id")
public String route_id="";//ew dynamic_property("route_id","route_id",null,true);


    @DynamicProperty(json_key = "Account_no", column_name = "account_no")
public String account_no="";//ew dynamic_property("Account_no","account_no",null,true);

    @DynamicProperty(json_key = "Branchname", column_name = "branchname")
public String branchname="";//ew dynamic_property("Branchname","branchname",null,true);

    @DynamicProperty(json_key = "bankname", column_name = "bankname")
public String bankname="";//ew dynamic_property("bankname","bankname",null,true);

    @DynamicProperty(json_key = "collection_centre_Id", column_name = "collection_centre")
    public String collection_centre="";//ew dynamic_property("collection_centre_Id","collection_centre",null,true);

    @DynamicProperty(json_key = "region_id", column_name = "region_id")
    public String region="";//ew dynamic_property("region_id","region_id",null,true);
    @DynamicProperty(json_key = "district_id", column_name = "district_id")
public String district="";//ew dynamic_property("district_id","district_id",null,true);

    @DynamicProperty(json_key = "ward_id", column_name = "ward_id")
    public String ward="";//ew dynamic_property("ward_id","ward_id",null,false);
    @DynamicProperty(json_key = "village_id", column_name = "village_id")
    public String village="";//ew dynamic_property("village_id","village_id",null,false);


    @DynamicProperty(json_key = "transacting_branch_id", column_name = "transacting_branch_id")
    public String transacting_branch_id="";//ew dynamic_property("transacting_branch_id","transacting_branch_id",null,false);
    @DynamicProperty(json_key = "type_of_storage", column_name = "type_of_storage")
    public String type_of_storage="";//ew dynamic_property("type_of_storage","type_of_storage",null,false);
    @DynamicProperty(json_key = "pluckers_groups_id", column_name = "pluckers_groups_id")
    public String pluckers_groups_id="";//ew dynamic_property("pluckers_groups_id","pluckers_groups_id",null,false);
    @DynamicProperty(json_key = "passporturl", column_name = "passporturl")
    public String passporturl="";//ew dynamic_property("passporturl","passporturl",null,false);





    @DynamicProperty(json_key = "no_of_fingerprints", column_name = "fp_count")
public String fp_count="";//ew dynamic_property("no_of_fingerprints","fp_count",null,false);
    @DynamicProperty(json_key = "no_of_documents", column_name = "img_count")
public String img_count="";//ew dynamic_property("no_of_documents","img_count",null,false);
    @DynamicProperty(json_key = "no_of_fingerimages", column_name = "fp_img_count")
public String fp_img_count="";//ew dynamic_property("no_of_fingerimages","fp_img_count",null,false);
    @DynamicProperty(json_key = "no_of_wsq_fingerimages", column_name = "fp_wsq_img_count")
public String fp_wsq_img_count="";//ew dynamic_property("no_of_wsq_fingerimages","fp_wsq_img_count",null,false);




    @DynamicProperty(json_key = "apk_version", column_name = "apk_version_creating")
public String apk_version_creating ="";//ew dynamic_property("apk_version","apk_version_creating",null,false);
    @DynamicProperty(json_key = "apk_version_saving", column_name = "apk_version_saving")
public String apk_version_saving ="";//ew dynamic_property("apk_version_saving","apk_version_saving",null,false);



    @DynamicProperty(json_key = "device_id", column_name = "device_id")
    public String device_id="";//ew dynamic_property("device_id","device_id",null,false);
    @DynamicProperty(json_key = "reg_mode", column_name = "reg_mode")
    public String reg_mode="";//ew dynamic_property("reg_mode","reg_mode",null,false);
    @DynamicProperty(json_key = "receipt_withdrawal_date", column_name = "receipt_withdrawal_date")
    public String receipt_withdrawal_date="";//ew dynamic_property("receipt_withdrawal_date","receipt_withdrawal_date",null,false);



    public String[] finger_prints=new String[10];
    public String[] finger_print_exceptions=new String[10];
 public String[] finger_print_images=new String[10];
 public String[] finger_print_WSQ_images=new String[10];
public String[] finger_print_skipping_reason=new String[10];

      public String[] images=new String[30];






  public String reg_type="0";




 public String face_count="";
    public String balance;
    public String credit_limit;


    public member_z()
    {




    }

}
