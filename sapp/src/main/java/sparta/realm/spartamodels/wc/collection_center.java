package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import static com.realm.annotations.SyncDescription.service_type.Download;

/*
case "JobUserCollectionCenters":
				try {

					JSONObject jObj = new JSONObject(httpjobresponse);
					JSONObject jObjResult = jObj.getJSONObject("Result");
					JSONArray jObjResArray = jObjResult.getJSONArray("Result");
					Log.v("lLLLLLLLLLL","OOOOOOOOOOOOOO " +jObjResArray.toString());

					for (int i=0; i<jObjResArray.length(); i++){
						JSONObject json_data = jObjResArray.getJSONObject(i);
						ContentValues ActiveRoutes = new ContentValues();
						ContentValues ActiveRoutes1 = new ContentValues();
						ActiveRoutes.put("id", json_data.getString("id"));
						ActiveRoutes1.put("user_id", json_data.getString("user_id"));
						ActiveRoutes.put("collection_center_id", json_data.getString("inventory_item_types_accessories_id"));
						ActiveRoutes.put("price", json_data.getString("price"));
						ActiveRoutes.put("sync_datetime", json_data.getString("synch_date"));
						String existuseercenter = null;
						Cursor cursor1 = databaseservice.rawQuery("SELECT id FROM delegates_centers WHERE id = ?", new String[]{""+json_data.getInt("id")});
						cursor1.moveToFirst();
						if(!cursor1.isAfterLast()){
							do{
								existuseercenter = cursor1.getString(0);
							}while(cursor1.moveToNext());

						}
						cursor1.close();

						if(existuseercenter == null){
							try{
								databaseservice.insert("delegates_centers", null, ActiveRoutes);

							}catch(Exception e){
								Log.i("%%%%%%%%%%%%% ", "^^^^^^^^^^^^^^^ "+e);

							}
						}else{
							try{
								databaseservice.update("delegates_centers", ActiveRoutes1, "id = ?", new String[]{""+json_data.getString("id")});
							}catch(Exception e){


							}
						}

					}

				} catch (JSONException e) {

				}

 */
@DynamicClass(table_name = "delegates_centers")
@SyncDescription(service_name = "JobUserCollectionCenters",service_type = Download,download_link = svars.Collection_center_download_link,chunk_size =svars.excuse_request_limit )
public class collection_center extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "id", column_name = "id")
   public String id="";

 @DynamicProperty(json_key = "user_id", column_name = "user_id")
   public String user_id="";

    @DynamicProperty(json_key = "collection_center_id", column_name = "collection_center_id")
    public String collection_center_id="";

    @DynamicProperty(json_key = "price", column_name = "price")
    public String price="";

 @DynamicProperty(json_key = "synch_date", column_name = "sync_var")
    public String sync_datetime="";




    public collection_center()
    {




    }

}
