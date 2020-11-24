package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import static com.realm.annotations.SyncDescription.service_type.Download;

/*
inventoryItem.put("rid", json_data.getInt("inventory_id"));
						inventoryItem.put("active", 1);//---------------
						inventoryItem.put("blockId", json_data.getString("blockId"));
						inventoryItem.put("blockname", json_data.getString("blockname"));
						inventoryItem.put("name", json_data.getString("inventory_name")+"  ["+json_data.getString("inventory_item_type_name_route")+"]");//---------------


						inventoryItem1.put("name", json_data.getString("inventory_name")+"  ["+json_data.getString("inventory_item_type_name_route")+"]");//---------------
						inventoryItem1.put("rid", json_data.getInt("inventory_id"));
						inventoryItem1.put("blockId", json_data.getString("blockId"));
						inventoryItem1.put("blockname", json_data.getString("blockname"));

 */
@DynamicClass(table_name = "routes")
//@SyncDescription(service_name = "JobRoutes",service_type = Download,download_link = svars.Route_download_link,chunk_size =svars.excuse_request_limit )
public class route extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "inventory_id", column_name = "rid")
   public String rid="";

    @DynamicProperty(json_key = "blockId", column_name = "blockId")
    public String blockId="";

    @DynamicProperty(json_key = "blockname", column_name = "blockname")
    public String blockname="";

 @DynamicProperty(json_key = "inventory_name", column_name = "name")
    public String name="";



 @DynamicProperty( column_name = "active")
    public String active="";




    public route()
    {




    }

}
