package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import sparta.spartaannotations.DynamicClass;
import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.SyncDescription;
import sparta.spartaannotations.db_class_;

import static sparta.spartaannotations.SyncDescription.service_type.Upload;

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
public class weibridge_details extends db_class_ implements Serializable {

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
 */

    @DynamicProperty(json_key = "gross_weight", column_name = "quantity_in")
   public String quantity_in="";

  @DynamicProperty(json_key = "transaction_date", column_name = "transaction_date")
   public String transaction_date="";

    @DynamicProperty(json_key = "first_weight_time", column_name = "transaction_date")
    public String first_weight_time="";

    @DynamicProperty(json_key = "buyer_contact",column_name = "zone_Code")
    public String zone_Code="";


 @DynamicProperty(json_key = "registration_date",column_name = "registration_date")
    public String registration_date="";







    public weibridge_details()
    {




    }

}
