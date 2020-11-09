package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import sparta.spartaannotations.DynamicClass;
import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.SyncDescription;
import sparta.spartaannotations.db_class_;

import static sparta.spartaannotations.SyncDescription.service_type.Download;

/*
case "JobCenters" :
				try {
					JSONObject jObj = new JSONObject(httpjobresponse);
					JSONArray jObjResArray = jObj.getJSONArray("Result");

					for (int i=0; i<jObjResArray.length(); i++){
						JSONObject json_data = jObjResArray.getJSONObject(i);

						ContentValues Centers = new ContentValues();
						Centers.put("center_id", json_data.getInt("center_id"));
						Centers.put("center_name", json_data.getString("centername"));
						Centers.put("route_id", json_data.getString("route_id"));

						String centerid = null;
						Cursor cursor1 = databaseservice.rawQuery("SELECT center_id FROM centers WHERE center_id = ?", new String[]{""+json_data.getInt("center_id")});
						cursor1.moveToFirst();
						if(!cursor1.isAfterLast()){
							do{
								centerid = cursor1.getString(0);
							}while(cursor1.moveToNext());
						}
						cursor1.close();
						if(centerid == null){
							try{
								databaseservice.insert("centers", null, Centers);
							}catch(Exception e){
								Log.i("%%%%%%%%%% ", "^^^^^^^^^^^^^^^ "+e);
							}
						}else{
							try{
								databaseservice.update("centers", Centers, "center_id = ?", new String[]{""+json_data.getInt("center_id")});
							}catch(Exception e){
								Log.i("%%%%%%%%%%%%    ", "^^^^^^^^^^^^^^^ "+e);
							}
						}
					}

				} catch (JSONException e) {
					//endService("getcenters", e.getLocalizedMessage());

				}

				break;

 */
@DynamicClass(table_name = "centers")
@SyncDescription(service_name = "JobCenters",service_type = Download,download_link = svars.Center_download_link)
public class center extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "center_id", column_name = "center_id")
   public String center_id="";

    @DynamicProperty(json_key = "centername", column_name = "center_name")
    public String center_name="";

    @DynamicProperty(json_key = "route_id", column_name = "route_id")
    public String route_id="";






    public center()
    {




    }

}
