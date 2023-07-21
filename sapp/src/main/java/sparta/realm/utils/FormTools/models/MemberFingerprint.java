package sparta.realm.utils.FormTools.models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;

import java.io.Serializable;

import sparta.realm.spartautils.svars;


@DynamicClass(table_name = "member_fingerprint")
@SyncDescription(service_name = "Member fingerprint", upload_link = "/MobiServices/SaveData/SaveFingerImages",service_type = SyncDescription.service_type.Upload,storage_mode_check = true)
public class MemberFingerprint extends Fingerprint implements Serializable {

    @DynamicProperty(json_key = "unique_code")
    public String member_transaction_no;




    public MemberFingerprint(String template_format, String fingerprint, String index, String member_transaction_no)
    {
        this.member_transaction_no=member_transaction_no;
        this.template=fingerprint;
        this.fingerprint_index=index;
        this.template_format=template_format;


        this.transaction_no= svars.getTransactionNo();

        this.sync_status= com.realm.annotations.sync_status.pending.ordinal()+"";


    }



    public MemberFingerprint() {
        this.transaction_no = svars.getTransactionNo();


        this.sync_status = com.realm.annotations.sync_status.pending.ordinal() + "";

    }

}
