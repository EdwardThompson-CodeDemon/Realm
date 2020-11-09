package sparta.realm.spartamodels;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Thompsons on 03-Mar-17.
 */

public class db_class implements Cloneable, Serializable {
    public String table_name;
    public dynamic_property id=new dynamic_property(null,"id",null,false);
    public dynamic_property sid=new dynamic_property("id","sid",null,false);
   public dynamic_property reg_time=new dynamic_property("date_time","reg_time",null,false);
   public dynamic_property sync_status=new dynamic_property(null,"sync_status",null,false);
   public dynamic_property data_status=new dynamic_property("is_active","data_status",null,false);
   public dynamic_property transaction_no=new dynamic_property("unique_code","transaction_no",null,false);
   public dynamic_property sync_var=new dynamic_property("datecomparer","sync_var",null,false);

   public dynamic_property user_id=new dynamic_property("user_id","user_id",null,false);
   public dynamic_property data_usage_frequency=new dynamic_property(null,"data_usage_frequency",null,false);
//public sync_service_description ssd=null;
public sync_service_description[] ssds=null;
  public db_class(String table_name)
    {
this.table_name=table_name;
    }


    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    }
