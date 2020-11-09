package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import sparta.spartaannotations.DynamicClass;
import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.SyncDescription;
import sparta.spartaannotations.db_class_;

import static sparta.spartaannotations.SyncDescription.service_type.Download;

/*
case "JobPluckersMachineAssignemt":
				try {

					JSONObject jObj = new JSONObject(httpjobresponse);
					Log.v("KKKKKKKKKKKK ","MMMMMMMMMM "+jObj);
					JSONObject jObjRe = jObj.getJSONObject("Result");
					JSONArray jObjResArray = jObjRe.getJSONArray("Result");

					for (int i=0; i<jObjResArray.length(); i++){

						JSONObject json_data = jObjResArray.getJSONObject(i);
						ContentValues ses = new ContentValues();
						ses.put("id", json_data.getString("id"));
						ses.put("plucker_mode_id", json_data.getString("plucker_mode_id"));
						ses.put("plucker_id", json_data.getString("plucker_id"));
						//ses.put("plucker_id", json_data.getString("plucker_id"));

						String sessionid = null;
						Cursor cursor1 = databaseservice.rawQuery("SELECT _id FROM plucker_machine_assignment WHERE _id = ?", new String[]{""+json_data.getString("id")});
						cursor1.moveToFirst();
						if(!cursor1.isAfterLast()){
							do{
								sessionid = cursor1.getString(0);
							}while(cursor1.moveToNext());
						}
						cursor1.close();

						if(sessionid == null){
							try{
								databaseservice.insert("plucker_machine_assignment", null, ses);
							}catch(Exception e){
								Log.i("%%%%%%%%%%%%%%%%", "^^^^^^^^^^^^^^^ "+e);
							}
						}else{
							try{
								Log.i("%%%%%%%%%%%%%% ", sessionid+"^^^^^^^^^^^^^^^ updating id "+json_data.getString("id"));
								databaseservice.update("plucker_machine_assignment", ses, " _id = ? ", new String[]{sessionid});
							}catch(Exception e){
								Log.i("%%%%%%%% ", "^^^^^^^^^^^^^^^ "+e);
							}
						}
					}

					Log.v("LLLLLLLLLLLLLLL","KKKKKKKKKKKKK RECORD SPEED ");
					//fetch vehicles

				}
				catch (JSONException e) {
					Log.v("KKKKKKKKK","JJJJJJJJJJJJJJJ "+e.getLocalizedMessage());

					//endService("MachinePlucker", e.getLocalizedMessage());

				}


				break;

 */
@DynamicClass(table_name = "plucker_machine_assignment")
@SyncDescription(service_name = "JobPluckersMachineAssignemt",service_type = Download,download_link = svars.Plucker_machine_assignment_download_link )
public class plucker_machine_assignment extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "id", column_name = "id")
   public String id="";

    @DynamicProperty(json_key = "plucker_mode_id", column_name = "plucker_mode_id")
    public String plucker_mode_id="";

    @DynamicProperty(json_key = "plucker_id", column_name = "plucker_id")
    public String plucker_id="";



    public plucker_machine_assignment()
    {




    }

}
