package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import static com.realm.annotations.SyncDescription.service_type.Download;

/*
AllinventoryItemTypes.put("gridcollection_id", json_data.getInt("inventory_id")); //inventory_id
						AllinventoryItemTypes.put("inventory_id", json_data.getInt("inventory_id"));
						AllinventoryItemTypes.put("inv_itemtype_id", json_data.getInt("inventoryitemtypeid"));//inventory_item_type_id
						AllinventoryItemTypes.put("rid", json_data.getInt("inventory_id"));
						AllinventoryItemTypes.put("capacity", json_data.getString("capacity"));
						AllinventoryItemTypes.put("inv_name", json_data.getString("inventory_name"));//inventory_name
						AllinventoryItemTypes.put("inventory_make_name", json_data.getString("inventory_make_name"));
						AllinventoryItemTypes.put("bp", json_data.getDouble("current_buying_price"));
						AllinventoryItemTypes.put("transportcost", json_data.optDouble("transportation_cost", 0.0));
						AllinventoryItemTypes.put("routename", json_data.getString("inventory_item_type_name_route"));
						AllinventoryItemTypes.put("inventory_make_id", json_data.getString("inventory_make_id"));
						AllinventoryItemTypes.put("make_model_id", json_data.getInt("inventory_make_model_id"));
						AllinventoryItemTypes.put("make_model_name", json_data.getString("inventory_make_model_name"));
						AllinventoryItemTypes.put("blockId", json_data.getString("blockId"));
						AllinventoryItemTypes.put("blockname", json_data.getString("blockname"));
						AllinventoryItemTypes.put("moisture", json_data.getDouble("moisture_level"));
						AllinventoryItemTypes.put("isactive", json_data.getBoolean("isactive"));
						AllinventoryItemTypes.put("reduction_weight", json_data.getDouble("reduction_weight"));
						AllinventoryItemTypes.put("weight_per_reduction", json_data.getDouble("weight_per_reduction"));

 */
@DynamicClass(table_name = "TBL_all_inv_dtls2")
//@SyncDescription(service_name = "JobAllInventory",service_type = Download,download_link = svars.Route_inventory_assignment_download_link,chunk_size =svars.excuse_request_limit )
public class route_product_assignment extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "inventory_id", column_name = "gridcollection_id")
   public String gridcollection_id="";

    @DynamicProperty(json_key = "inventory_id", column_name = "inventory_id")
    public String inventory_id="";

    @DynamicProperty(json_key = "inventory_id", column_name = "rid")
    public String rid="";

 @DynamicProperty(json_key = "capacity", column_name = "capacity")
    public String capacity="";

 @DynamicProperty(json_key = "inventory_name", column_name = "inv_name")
    public String inv_name="";

@DynamicProperty(json_key = "inventory_make_name", column_name = "inventory_make_name")
    public String inventory_make_name="";


@DynamicProperty(json_key = "current_buying_price", column_name = "bp")
    public String bp="";


@DynamicProperty(json_key = "transportation_cost", column_name = "transportcost")
    public String transportcost="";



@DynamicProperty(json_key = "inventory_item_type_name_route", column_name = "routename")
    public String routename="";


@DynamicProperty(json_key = "inventory_make_id", column_name = "inventory_make_id")
    public String inventory_make_id="";


@DynamicProperty(json_key = "inventory_make_model_id", column_name = "make_model_id")
    public String make_model_id="";


@DynamicProperty(json_key = "inventory_make_model_name", column_name = "make_model_name")
    public String make_model_name="";


@DynamicProperty(json_key = "blockId", column_name = "blockId")
    public String blockId="";

@DynamicProperty(json_key = "blockname", column_name = "blockname")
    public String blockname="";


@DynamicProperty(json_key = "moisture_level", column_name = "moisture")
    public String moisture="";


@DynamicProperty(json_key = "reduction_weight", column_name = "reduction_weight")
    public String reduction_weight="";

@DynamicProperty(json_key = "weight_per_reduction", column_name = "weight_per_reduction")
    public String weight_per_reduction="";





    public route_product_assignment()
    {




    }

}
