package sparta.realm.utils.FormTools.models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;
import com.realm.annotations.SyncDescription;

import java.io.Serializable;
import java.util.ArrayList;

import sparta.realm.spartautils.svars;


@DynamicClass(table_name = "member_fingerprints")
public class MemberFingerprints extends RealmModel implements Serializable {

    public ArrayList<MemberFingerprint> fingerprintsInput = new ArrayList<>();




    public MemberFingerprints() {
        this.transaction_no = svars.getTransactionNo();


        this.sync_status = com.realm.annotations.sync_status.pending.ordinal() + "";

    }

}
