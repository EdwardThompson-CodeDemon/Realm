package sparta.realm.spartamodels;


import java.io.Serializable;
import java.util.ArrayList;

import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicProperty;


public class member extends db_class implements Serializable {

   public dynamic_property surname=new dynamic_property("first_name","surname",null,false);
   public dynamic_property other_names=new dynamic_property("last_name","other_names",null,false);
   public dynamic_property maiden_name=new dynamic_property("maiden_name","maiden_name",null,false);
   public dynamic_property nickname=new dynamic_property("nickname","nickname",null,false);
 public dynamic_property idno=new dynamic_property("identity_doc_number","idno",null,false);

 public dynamic_property dob=new dynamic_property("dob","dob",null,false);
 public dynamic_property pob=new dynamic_property("place_of_birth","pob",null,false);
 public dynamic_property phoneno=new dynamic_property("phone_no","phoneno",null,false);
 public dynamic_property gender=new dynamic_property("gender_id","gender",null,true);
public dynamic_property reg_start_time=new dynamic_property("reg_start_time","reg_start_time",null,true);
public dynamic_property reg_stop_time=new dynamic_property("reg_end_time","reg_stop_time",null,true);


public dynamic_property blood_group=new dynamic_property("blood_group_id","blood_group",null,true);

public dynamic_property profession=new dynamic_property("profession_id","profession",null,true);
public dynamic_property father_name=new dynamic_property("fathers_name","father_name",null,true);
public dynamic_property father_dob=new dynamic_property("fathers_dob","father_dob",null,true);
public dynamic_property mother_name=new dynamic_property("mothers_name","mother_name",null,true);
public dynamic_property mother_dob=new dynamic_property("mothers_dob","mother_dob",null,true);

    public dynamic_property bukina_residence=new dynamic_property("place_of_residence_burkina","bukina_residence",null,false);
    public dynamic_property local_residence=new dynamic_property("place_of_local_residence","local_residence",null,false);


    public dynamic_property height=new dynamic_property("taille","height",null,false);
    public dynamic_property complexion=new dynamic_property("teint_id","complexion",null,false);
    public dynamic_property id_type=new dynamic_property("identity_doc_type_id","id_type",null,false);
    public dynamic_property deligate_code=new dynamic_property("delegate_id","deligate_code",null,false);
    public dynamic_property enrollement_location=new dynamic_property("enrollement_location","enrollement_location",null,false);
    public dynamic_property id_authority=new dynamic_property("identity_established_by","id_authority",null,false);
    public dynamic_property contact_person_notify=new dynamic_property("contact_person_phone","contact_person_notify",null,false);
    public dynamic_property contact_person=new dynamic_property("contact_person","contact_person",null,false);
    public dynamic_property locality=new dynamic_property("locality_id","locality",null,false);
    public dynamic_property id_date=new dynamic_property("identity_established_date","id_date",null,false);
 public dynamic_property payment_date=new dynamic_property("payment_date","payment_date",null,false);
 public dynamic_property payment_code=new dynamic_property("payment_number","payment_code",null,false);
 public dynamic_property formulaire_no=new dynamic_property("form_number","formulaire_no",null,false);
 public dynamic_property other_profession=new dynamic_property("other_profession","other_profession",null,false);



public dynamic_property fp_count=new dynamic_property("no_of_fingerprints","fp_count",null,false);
public dynamic_property img_count=new dynamic_property("no_of_documents","img_count",null,false);
public dynamic_property fp_img_count=new dynamic_property("no_of_fingerimages","fp_img_count",null,false);
public dynamic_property fp_wsq_img_count=new dynamic_property("no_of_wsq_fingerimages","fp_wsq_img_count",null,false);



public dynamic_property exception_code=new dynamic_property("exception_code","exception_code",null,false);
public dynamic_property apk_version_creating =new dynamic_property("apk_version","apk_version_creating",null,false);
public dynamic_property apk_version_saving =new dynamic_property("apk_version_saving","apk_version_saving",null,false);
public dynamic_property  enrolment_type =new dynamic_property("enrolment_type_id","enrolment_type",null,false);

    public dynamic_property receipt_no=new dynamic_property("reception_no","receipt_no",null,false);
    public dynamic_property device_id=new dynamic_property("device_id","device_id",null,false);
    public dynamic_property reg_mode=new dynamic_property("reg_mode","reg_mode",null,false);
    public dynamic_property receipt_withdrawal_date=new dynamic_property("receipt_withdrawal_date","receipt_withdrawal_date",null,false);



    public String[] finger_prints=new String[10];
    public String[] finger_print_exceptions=new String[10];
 public String[] finger_print_images=new String[10];
 public String[] finger_print_WSQ_images=new String[10];
public String[] finger_print_skipping_reason=new String[10];

      public String[] images=new String[30];






  public String reg_type="0";




 public String face_count="";



    public member()
    {

        super("member_info_table");
sync_service_description sd=new sync_service_description();
sd.service_name= "Member";
sd.upload_link= svars.Member_upload_link;
//sd.download_link= svars.Member_download_link;
sd.servic_type= sync_service_description.service_type.Upload;
sd.chunk_size= svars.members_request_limit;
sd.use_download_filter= true;
sd.table_name= table_name;

        ssds=new sync_service_description[]{sd};

id.json_name="tablet_data_position";
    }

}
