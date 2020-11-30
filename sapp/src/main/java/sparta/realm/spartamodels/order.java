package sparta.realm.spartamodels;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import java.io.Serializable;
import java.util.ArrayList;

@DynamicClass(table_name = "orders")
@SyncDescription()
public class order extends db_class_ implements Serializable {

@DynamicProperty(json_key = "customer_name")
   public String customer_name="";

    @DynamicProperty(json_key = "sale_time")
    public String sale_time="";//

    @DynamicProperty(json_key = "order_no")
    public String order_no="";
    @DynamicProperty(json_key = "total_amount")
    public String total_amount="";

   // @DynamicProperty(json_key = "order_items")
    public ArrayList<order_item> sales_items=new ArrayList<>();


    @DynamicProperty(json_key = "order_type")
    public String  order_type ="";




    @DynamicProperty(json_key = "apk_version_creating")
    public String apk_version_creating ="";
    @DynamicProperty(json_key = "apk_version_saving")
    public String apk_version_saving ="";


     public dynamic_property device_id=new dynamic_property("device_id","device_id",null,false);
 public String receipt_status="0";


    public order()
    {



    }

}
