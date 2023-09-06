package sparta.realm.utils.FormTools.models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;

import java.io.Serializable;
import java.util.ArrayList;

import sparta.realm.spartautils.svars;


@DynamicClass(table_name = "member_fingerprints")
@SyncDescription(service_name = "Member fingerprint", upload_link = "/MobiServices/SaveData/SaveFingerImages",service_type = SyncDescription.service_type.Upload,storage_mode_check = true)
public class MemberFingerprints extends Fingerprint implements Serializable {

    public ArrayList<MemberFingerprint> fingerprintsInput = new ArrayList<>();




    public MemberFingerprints() {
        this.transaction_no = svars.getTransactionNo();


        this.sync_status = com.realm.annotations.sync_status.pending.ordinal() + "";

    }

}
