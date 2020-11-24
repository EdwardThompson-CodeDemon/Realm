package sparta.realm.spartamodels.wc.sim;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import static com.realm.annotations.SyncDescription.service_type.Download;
import static com.realm.annotations.SyncDescription.service_type.Download_Upload;


/*case "jobCocoaWeightBridgeCollections":

				try {

					JSONObject jObj = new JSONObject(httpjobresponse);
					JSONObject jObjResult = jObj.getJSONObject("Result");
					JSONArray jcollections = jObjResult.getJSONArray("Result");
					int num_of_recs = jObjResult.getInt("DataSetCount");

					Log.v("KKKKKKKKKK","KKKKKKKKKKKK WEIGHBRIDGE COLLECTIONS FETCHING "+jcollections);

					for (int xx = 0; xx<jcollections.length(); xx++){

						JSONObject objcol = jcollections.getJSONObject(xx);


						ContentValues conf = new ContentValues();
						conf.put("id",objcol.getString("id"));
						conf.put("tag_no",objcol.getString("tag_no"));
						conf.put("weighbridge_id",objcol.getString("weighbridge_id"));


						int col_id_exist = 0;
						Cursor cursor = LoginActivity.database.rawQuery("SELECT id FROM weighbridgepulltags WHERE id = ?", new String[]{objcol.getString("id")});
						cursor.moveToFirst();
						if(!cursor.isAfterLast()) {
							do {
								col_id_exist = cursor.getInt(0);
							} while (cursor.moveToNext());
						}
						cursor.close();

						if (col_id_exist == 0){
							//insert
							databaseservice.insert("weighbridgepulltags", null, conf);
						}
						else{
							//update
							String whereClause = "id = ?";
							databaseservice.update("weighbridgepulltags", conf, whereClause, new String[]{objcol.getString("id")});
						}
					}


					//checkAPKUpdate();



				} catch (JSONException e) {

					//endService("jobCocoaWeightBridgeCollections", e.getLocalizedMessage());

				}


				break;
				*/
@DynamicClass(table_name = "weighbridgepulltags")
@SyncDescription(service_name = "jobCocoaWeightBridgeCollections",service_type = Download_Upload,download_link = svars.Weighbridge_tag_download_link,upload_link = svars.Weighbridge_tag_upload_link )
public class weighbridge_tag extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "tag_no", column_name = "tag_no")
   public String tag_no="";

    @DynamicProperty(json_key = "weighbridge_id", column_name = "weighbridge_id")
    public String weighbridge_id="";

  @DynamicProperty(json_key = "cocoa_buyer_id", column_name = "buyer")
    public String buyer="";

  @DynamicProperty(json_key = "dispatch_date", column_name = "dispatch_date")
    public String dispatch_date="";



  @DynamicProperty(json_key = "dispatch_session", column_name = "dispatch_session")
    public String dispatch_session="";





  @DynamicProperty(json_key = "bag_weight", column_name = "bag_weight")
    public String bag_weight="";





    public weighbridge_tag()
    {




    }

}
