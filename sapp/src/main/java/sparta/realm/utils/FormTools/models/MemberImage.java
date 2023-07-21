package sparta.realm.utils.FormTools.models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;
import com.realm.annotations.SyncDescription;

import java.io.Serializable;

import sparta.realm.spartautils.svars;


@DynamicClass(table_name = "member_image_table")
@SyncDescription(service_name = "Member image", upload_link = "/MobiServices/SaveData/SaveImages",service_type = SyncDescription.service_type.Upload,storage_mode_check = true)
public class MemberImage extends RealmModel implements Serializable {

//7194276
    @DynamicProperty(json_key = "index_no")
    public String image_index;

    @DynamicProperty(json_key = "biometric_type_id")
    public String data_type;
    @DynamicProperty(json_key = "unique_code")
    public String member_transaction_no;

    @DynamicProperty(json_key = "image_source")
    public String image_source;
    @DynamicProperty(json_key = "compression_format")
    public String compression_format;
    @DynamicProperty(json_key = "compression_percent")
    public String compression_percent;


    @DynamicProperty(json_key = "image_name", storage_mode = DynamicProperty.storage_mode.FilePath)
    public String image;

    @DynamicProperty(json_key = "msid")
    public String msid = "";

    public enum ImageIndecies {
        None,
        idFront ,
        birthCert,
        marriageDocument,
        profile_photo,
        student_card,
        signature,
        removed,
        professionDocument,
        parents_doc_pic,
        contributor_receipt_pic,
        payer_id_pic,
        bulleting_doc_pic,
        signe_pic,
        justif_pic,
        receipt_pic,
        idBack,
        payer_id_pic_back,
        otherDocument

    }


    public MemberImage() {


    }

 public MemberImage(String image) {
     this.image = image;
     this.transaction_no = svars.getTransactionNo();
        this.data_type = svars.data_type_indexes.photo + "";
        this.sync_status = com.realm.annotations.sync_status.pending.ordinal() + "";

    }

}
