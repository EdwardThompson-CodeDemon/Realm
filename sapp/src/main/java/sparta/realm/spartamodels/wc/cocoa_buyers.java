package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import static com.realm.annotations.SyncDescription.service_type.Download;

/*
case "JobCocoaBuyers":
				try {

					JSONObject jObj = new JSONObject(httpjobresponse);
					JSONObject jObjResult = jObj.getJSONObject("Result");
					JSONArray jcollections = jObjResult.getJSONArray("Result");
					int num_of_recs = jObjResult.getInt("DataSetCount");

					Log.v("KKKKKKKKKK","KKKKKKKKKKKK COCOA BUYERS "+jcollections);


					for (int xx = 0; xx<jcollections.length(); xx++){

						JSONObject objcol = jcollections.getJSONObject(xx);


						ContentValues conf = new ContentValues();
						conf.put("id",objcol.getString("id"));
						conf.put("buyer_name",objcol.getString("buyer_name"));
						conf.put("buyer_contact",objcol.getString("buyer_contact"));
						conf.put("registration_date",objcol.getString("registration_date"));


						int col_id_exist = 0;
						Cursor cursor = LoginActivity.database.rawQuery("SELECT id FROM cccoa_buyers WHERE id = ?", new String[]{objcol.getString("id")});
						cursor.moveToFirst();
						if(!cursor.isAfterLast()) {
							do {
								col_id_exist = cursor.getInt(0);
							} while (cursor.moveToNext());
						}
						cursor.close();

						if (col_id_exist == 0){
							//insert
							databaseservice.insert("cccoa_buyers", null, conf);
						}
						else{
							//update
							String whereClause = "id = ?";
							databaseservice.update("cccoa_buyers", conf, whereClause, new String[]{objcol.getString("id")});
						}
					}




				} catch (JSONException e) {

					//endService("JobCocoaBuyers", e.getLocalizedMessage());
				}


				break;
 */
@DynamicClass(table_name = "coccoa_buyers")
@SyncDescription(service_name = "JobCocoaBuyers",service_type = Download,download_link = svars.Cocoa_buyers_download_link )
public class cocoa_buyers extends db_class_ implements Serializable {

//    {"$id":"6","id":1,"buyer_name":"Cargill","buyer_contact":"892922","user_id":0,"datecomparer":16051913830732754,"registration_date":"0001-01-01T00:00:00"}
//
//    @DynamicProperty(json_key = "id", column_name = "id")
//   public String id="";

    @DynamicProperty(json_key = "buyer_name", column_name = "buyer_name")
    public String buyer_name="";

    @DynamicProperty(json_key = "buyer_contact",column_name = "buyer_contact")
    public String buyer_contact="";


 @DynamicProperty(json_key = "registration_date",column_name = "registration_date")
    public String registration_date="";







    public cocoa_buyers()
    {




    }

}
