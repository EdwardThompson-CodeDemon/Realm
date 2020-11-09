package sparta.realm.spartamodels;


import java.io.Serializable;

public class dependant implements Serializable {


 public String surname,other_names,gender,dob,dependant_type,passport_photo,birth_cert_photo;

public boolean data_valid=false;
    public String transaction_no;
    public String user_id;
    public String parent_transaction_no;
    public String lid;
    public String reg_time;

    public dependant()
    {

    }

}
