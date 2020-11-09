package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import sparta.spartaannotations.DynamicClass;
import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.SyncDescription;
import sparta.spartaannotations.db_class_;

import static sparta.spartaannotations.SyncDescription.service_type.Download;

/*
case "JobRoutes" :
				try {

					JSONObject jObj = new JSONObject(httpjobresponse);
					JSONObject jObjResult = jObj.getJSONObject("Result");
					JSONArray jObjResArray = jObjResult.getJSONArray("Result");
					for (int i=0; i<jObjResArray.length(); i++){
						JSONObject json_data = jObjResArray.getJSONObject(i);
						ContentValues ActiveRoutes = new ContentValues();
						ContentValues ActiveRoutes1 = new ContentValues();
						ActiveRoutes.put("sync_datetime", json_data.getString("id"));
						ActiveRoutes1.put("sync_datetime", json_data.getString("id"));
						ActiveRoutes.put("active", 0);
						ActiveRoutes.put("code", json_data.getString("name"));
						ActiveRoutes1.put("code", json_data.getString("name"));
						//Log.v("XXXXXXXXXXXXX","XXXXXXXXXXX ROUTE NAME "+json_data.getString("id")+"--"+json_data.getString("name"));

						String routeid = null;
						Cursor cursor1 = databaseservice.rawQuery("SELECT sync_datetime FROM TBL_active_route WHERE sync_datetime = ?", new String[]{""+json_data.getInt("id")});
						cursor1.moveToFirst();
						if(!cursor1.isAfterLast()){
							do{
								routeid = cursor1.getString(0);
							}while(cursor1.moveToNext());

						}
						cursor1.close();

						if(routeid == null){
							try{
								databaseservice.insert("TBL_active_route", null, ActiveRoutes);

							}catch(Exception e){
								Log.i("%%%%%%%%%%%%% ", "^^^^^^^^^^^^^^^ "+e);

							}
						}else{
							try{
								databaseservice.update("TBL_active_route", ActiveRoutes1, "sync_datetime = ?", new String[]{""+json_data.getString("id")});
							}catch(Exception e){
								Log.i("%%%%%%%%", "^^^^^^^^^^^^^^^ "+e);

							}
						}

					}

				} catch (JSONException e) {

					//endService("jobRoutes", e.getLocalizedMessage());

				}

				break;

 */
@DynamicClass(table_name = "TBL_active_route")
@SyncDescription(service_name = "JobRoutes",service_type = Download,download_link = svars.Route_download_link )
public class route_block extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "name", column_name = "code")
   public String code="";

    @DynamicProperty(json_key = "collection_center_id", column_name = "collection_center_id")
    public String collection_center_id="";

    @DynamicProperty(json_key = "price", column_name = "price")
    public String price="";

 @DynamicProperty(json_key = "synch_date", column_name = "sync_var")
    public String sync_datetime="";




    public route_block()
    {




    }

}
