package sparta.realm.spartamodels;

import java.io.Serializable;

/**
 * Created by Thompsons on 5/11/2018.
 */

public class payment implements Serializable {
    public String customer_id,mode_id,mode_name,amount,ref,lid,sync_status,sync_time;
    public payment(String lid, String mode_id, String mode_name, String customer_id, String amount, String ref, String sync_status, String sync_time)
    {
        this.customer_id=customer_id;
        this.mode_id=mode_id;
        this.mode_name=mode_name;
        this.amount=amount;
        this.ref=ref;
        this.sync_status=sync_status;
        this.sync_time=sync_time;
        this.lid=lid;

    }
    public payment()
    {

    }
}
