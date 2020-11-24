package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import static com.realm.annotations.SyncDescription.service_type.Upload;

/*
String query = "SELECT transaction_date, quantity_in, quantity_out,zone_Code,contact_type, net_weight, id, contact_type, session,user_id, transaction_no, separator, center_id, field_user_id, receiving_session, truck_id,transaction_type from stock_transactions where status IS NULL AND transaction_type != ? LIMIT 1";
		Cursor c = null;
		c = databaseservice.rawQuery(query, new String[]{"Firewood Items"});
		c.moveToFirst();
		if (!c.isAfterLast()) {
			do {
				weight_bridge_id = c.getString(6);
				try {

					//useridselect
					dataretriever dat = new dataretriever();
					dat.getmaxuserid();

					String  center_id = "";
					Cursor membersCursor = LoginActivity.database.rawQuery("SELECT center_id FROM centers WHERE active = ?", new String[]{"1"});
					membersCursor.moveToFirst();
					if(!membersCursor.isAfterLast()) {
						do {
							center_id = membersCursor.getString(0);
						} while (membersCursor.moveToNext());
					}
					membersCursor.close();

					obj.put("gross_weight", c.getDouble(1));
					obj.put("transaction_date", c.getString(0));
					obj.put("first_weight_time", c.getString(0));
					obj.put("Transporter_id",c.getInt(3));
					obj.put("user_id",dat.user_id);
					obj.put("receipt_no",c.getString(10));
					obj.put("session_no", c.getString(8));
					obj.put("net_weight", c.getString(5));
					obj.put("user_weighbridge_id", c.getString(9));
					obj.put("user_field_id", c.getString(13));
					obj.put("inventory_item_id", 1);
					obj.put("route_id", 1);
					obj.put("transacting_branch_id", 1);
					obj.put("tare_weight", c.getDouble(2));
					obj.put("net_weight", c.getDouble(1));
					obj.put("centre_id", center_id);
					obj.put("vehicle_registration_id", c.getString(15));
					obj.put("delivery_session", c.getString(14));
                    obj.put("outer_number", c.getString(16));


					JSONArray grid = new JSONArray();

					Cursor c_b = LoginActivity.database.rawQuery("SELECT tag_no, separator, user_id FROM Weighbridgetags WHERE weighbridge_id = ?", new String[]{c.getString(6)});
					c_b.moveToFirst();
					if (!c_b.isAfterLast()) {
						do {
							JSONObject j = new JSONObject();
							j.put("tag_no",c_b.getString(0));
							j.put("comment","");
							j.put("cocoa_buyer_id",1);
							j.put("user_id",c_b.getString(2));
							j.put("dispatch_date", c.getString(0));
							j.put("consignment_session", c_b.getString(1));
							j.put("outer_number", c.getString(16));
							grid.put(j);
						}while (c_b.moveToNext());
					}
					c_b.close();
					obj.put("no_of_bags", grid.length());
					obj.put("driver_name", "Driver");

					obj.put("CollectionTagList",grid);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} while (c.moveToNext());
		} c.close();
		return obj;

 */
@DynamicClass(table_name = "stock_transactions")
@SyncDescription(service_name = "InsertWeightBridge",service_type = Upload,download_link = svars.Weighbridge_add_upload_link)
@SyncDescription(service_name = "InsertWeightBridge",service_type = Upload,download_link = svars.Weighbridge_dispatch_upload_link)
public class stock_transaction extends db_class_ implements Serializable {

    String query = "SELECT transaction_date," +
            " quantity_in," +
            " quantity_out," +
            "zone_Code," +
            "contact_type," +
            " net_weight," +
            " id," +
            " contact_type, session,user_id, transaction_no, separator, center_id, field_user_id, receiving_session, truck_id,transaction_type from stock_transactions where status IS NULL AND transaction_type != ? LIMIT 1";
/*
obj.put("gross_weight", c.getDouble(1));
					obj.put("transaction_date", c.getString(0));
					obj.put("first_weight_time", c.getString(0));
					obj.put("Transporter_id",c.getInt(3));
					//obj.put("user_id",dat.user_id);
					obj.put("receipt_no",c.getString(10));
					obj.put("session_no", c.getString(8));
					obj.put("net_weight", c.getString(5));
					obj.put("user_weighbridge_id", c.getString(9));
					obj.put("user_field_id", c.getString(13));
					obj.put("inventory_item_id", 1);
					obj.put("route_id", 1);
					obj.put("transacting_branch_id", 1);
					obj.put("tare_weight", c.getDouble(2));
					obj.put("net_weight", c.getDouble(1));
					obj.put("centre_id", center_id);
					obj.put("vehicle_registration_id", c.getString(15));
					obj.put("delivery_session", c.getString(14));
                    obj.put("outer_number", c.getString(16));
 */

    @DynamicProperty(json_key = "outer_number", column_name = "transaction_type")
   public String transaction_type="";



    @DynamicProperty(json_key = "delivery_session", column_name = "receiving_session")
   public String receiving_session="";


 @DynamicProperty(json_key = "vehicle_registration_id", column_name = "truck_id")
   public String truck_id="";



 @DynamicProperty(json_key = "centre_id", column_name = "centre_id")
   public String centre_id="";




    @DynamicProperty(json_key = "net_weight", column_name = "quantity_in")
   public String quantity_in="";



    @DynamicProperty(json_key = "tare_weight", column_name = "quantity_out")
   public String quantity_out="";


    @DynamicProperty(json_key = "transacting_branch_id", column_name = "transacting_branch_id",column_default_value = "1")
   public String transacting_branch_id="";

   @DynamicProperty(json_key = "route_id", column_name = "route_id",column_default_value = "1")
   public String route_id="";

    @DynamicProperty(json_key = "inventory_item_id", column_name = "inventory_item_id",column_default_value = "1")
   public String inventory_item_id="";

   @DynamicProperty(json_key = "gross_weight", column_name = "quantity_in")
   public String gross_weight="";

  @DynamicProperty(json_key = "transaction_date", column_name = "transaction_date")
   public String transaction_date="";

    @DynamicProperty(json_key = "first_weight_time", column_name = "transaction_date")
    public String first_weight_time="";

    @DynamicProperty(json_key = "Transporter_id",column_name = "zone_Code")
    public String zone_Code="";


 @DynamicProperty(json_key = "receipt_no",column_name = "transaction_no")
    public String receipt_no="";

    @DynamicProperty(json_key = "session_no",column_name = "session")
    public String session="";

 @DynamicProperty(json_key = "net_weight",column_name = "net_weight")
    public String net_weight="";


 @DynamicProperty(json_key = "user_weighbridge_id",column_name = "user_id")
    public String user_weighbridge_id="";



@DynamicProperty(json_key = "user_field_id",column_name = "field_user_id")
    public String field_user_id="";






    public stock_transaction()
    {




    }

}
