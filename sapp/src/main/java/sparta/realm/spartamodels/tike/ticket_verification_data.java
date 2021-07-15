package sparta.realm.spartamodels.tike;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import java.io.Serializable;

import sparta.realm.spartautils.svars;

import static com.realm.annotations.SyncDescription.service_type.Upload;

/*

 */
@DynamicClass(table_name = "ticket_verification_data")
@SyncDescription(service_id="4",service_name = "Ticket verification data",service_type = Upload)
public class ticket_verification_data extends db_class_ implements Serializable {


    @DynamicProperty(json_key = "ticket_verification_transaction_no")
   public String ticket_verification_transaction_no="";

    @DynamicProperty(json_key = "verification_image", column_name = "verification_image")
    public String verification_image="";

       @DynamicProperty(json_key = "temperature", column_name = "temperature")
    public String temperature="";

  @DynamicProperty(json_key = "gender", column_name = "gender")
    public String gender="";


 @DynamicProperty(json_key = "age", column_name = "age")
    public String age="";


    //json object ticket_verification_transaction_no  verification_image temperature gender age









    public ticket_verification_data()
    {




    }

}
