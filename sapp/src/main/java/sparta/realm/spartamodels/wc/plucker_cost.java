package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import sparta.spartaannotations.DynamicClass;
import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.SyncDescription;
import sparta.spartaannotations.db_class_;

import static sparta.spartaannotations.SyncDescription.service_type.Download;

/*
case "JobPluckersCost":

				try {
					JSONObject jObj = new JSONObject(httpjobresponse);
					JSONObject jObjRe = jObj.getJSONObject("Result");
					JSONArray jObjResArray = jObjRe.getJSONArray("Result");

					for (int i=0; i<jObjResArray.length(); i++){

						JSONObject json_data = jObjResArray.getJSONObject(i);
						ContentValues ses = new ContentValues();
						ses.put("type_id", json_data.getString("id"));
						ses.put("name", json_data.getString("name"));
						Log.v("HHHHHHH", "PLUCKER TRYPE "+json_data.getString("name"));
						ses.put("sync_datetime", json_data.getString("sync_datetime"));

						String sessionid = null;
						Cursor cursor1 = databaseservice.rawQuery("SELECT type_id FROM TBL_plucker_types WHERE type_id = ?", new String[]{""+json_data.getString("id")});
						cursor1.moveToFirst();
						if(!cursor1.isAfterLast()){
							do{
								sessionid = cursor1.getString(0);
							}while(cursor1.moveToNext());

						}
						cursor1.close();

						if(sessionid == null){
							try{
								databaseservice.insert("TBL_plucker_types", null, ses);
							}catch(Exception e){
								Log.i("%%%%%%%%%%%%%%%%", "^^^^^^^^^^^^^^^ "+e);
							}
						}else{
							try{
								Log.i("%%%%%%%%%%%%%% ", sessionid+"^^^^^^^^^^^^^^^ updating id "+json_data.getString("id"));
								databaseservice.update("TBL_plucker_types", ses, " type_id = ? ", new String[]{sessionid});
							}catch(Exception e){
								Log.i("%%%%%%%% ", "^^^^^^^^^^^^^^^ "+e);

							}
						}
					}
				}
				catch (JSONException e) {
					//sendFingerPrints();
					//endService("Plucker Cost", e.getLocalizedMessage());
				}
				break;

 */
@DynamicClass(table_name = "TBL_plucker_types")
@SyncDescription(service_name = "JobPluckersCost",service_type = Download,download_link = svars.Plucker_cost_download_link)
public class plucker_cost extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "id", column_name = "type_id")
   public String type_id="";

    @DynamicProperty(json_key = "name", column_name = "name")
    public String name="";

    @DynamicProperty(json_key = "sync_datetime", column_name = "sync_datetime")
    public String sync_datetime="";






    public plucker_cost()
    {




    }

}
