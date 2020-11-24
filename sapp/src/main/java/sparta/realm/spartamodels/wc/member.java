package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import static com.realm.annotations.SyncDescription.service_type.Download;
import static com.realm.annotations.SyncDescription.service_type.Download_Upload;
import static com.realm.annotations.SyncDescription.service_type.Upload;

//			String query = "SELECT _id,full_name, phone1,nat_id,email,branch_id,branch,acc_no,acc_name,bank_details_id,membership_type_id,route, member_no, member_id, photo_name FROM TBL_members WHERE  status='pending'  LIMIT 1";


//        memberList.put("full_name", c.getString(1));
//				memberList.put("member_no", c.getString(12));
//				memberList.put("phone1", c.getString(2));
//				memberList.put("nat_id", c.getString(3));
//				memberList.put("email", c.getString(4));
//				memberList.put("branch_id", c.getString(5));
//				memberList.put("Is_active","True");
//				memberList.put("route_id",c.getString(11));
//
//
//				memberList.put("bank_details_id", ""+1);
//				memberList.put("acc_no", c.getString(7));
//				memberList.put("account_name", c.getString(8));
//				memberList.put("branch_name", c.getString(6));
//				memberList.put("membership_type_id", c.getString(10));
//				memberList.put("collection_centre_Id", ""+col_center);
//				memberList.put("photo_name",  c.getString(14));
@DynamicClass(table_name = "TBL_members")
@SyncDescription(service_name = "Member",service_type = Download,download_link = svars.Member_download_link )
@SyncDescription(service_name = "Member",service_type = Upload,upload_link = svars.Member_upload_link )
public class member extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "full_name", column_name = "full_name")
   public String full_name="";

    @DynamicProperty(json_key = "phone1", column_name = "phone1")
    public String phone="";


    @DynamicProperty(json_key = "nat_id", column_name = "nat_id")
    public String nat_id="";

  @DynamicProperty(json_key = "email", column_name = "email")
    public String email="";

    @DynamicProperty(json_key = "branch_id", column_name = "branch_id")
    public String branch_id="";

 @DynamicProperty(json_key = "branch_name", column_name = "branch")
    public String branch="";


    @DynamicProperty(json_key = "member_no", column_name = "member_no")
    public String member_no="";


    @DynamicProperty(json_key = "route_id", column_name = "route")
    public String route_id="";


    @DynamicProperty(json_key = "membership_type_id", column_name = "membership_type_id")
    public String membership_type_id="";


    @DynamicProperty(json_key = "acc_no", column_name = "acc_no")
    public String account_no="";

  @DynamicProperty(json_key = "account_name", column_name = "acc_name")
    public String account_name="";


    @DynamicProperty(json_key = "bankname", column_name = "bankname")
    public String bankname="";


    @DynamicProperty(json_key = "bank_details_id", column_name = "bank_details_id")
    public String bank_details_id="";

    @DynamicProperty(json_key = "collection_centre_Id", column_name = "collection_centre")
    public String collection_centre="";

 @DynamicProperty(json_key = "photo_name", column_name = "photo_name")
    public String photo_name="";







    public member()
    {




    }

}
