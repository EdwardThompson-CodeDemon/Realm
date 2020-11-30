package sparta.realm.spartamodels;


import com.realm.annotations.DynamicProperty;
import com.realm.annotations.db_class_;

import java.io.Serializable;





public class order_item extends db_class_ implements Serializable {


    @DynamicProperty(json_key = "sale_id")
   public String sale_id="";
    @DynamicProperty(json_key = "product_name")
    public String product_name="";
    @DynamicProperty(json_key = "price")
   public String price="";//new dynamic_property("price","price",null,false);
    @DynamicProperty(json_key = "quantity")
   public String quantity="";//new dynamic_property("quantity","quantity",null,false);
    @DynamicProperty(json_key = "total_amount")
   public String total_amount="";//new dynamic_property("total_amount","total_amount",null,false);







    public order_item()
    {





    }

}
