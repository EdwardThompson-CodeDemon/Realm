package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import sparta.spartaannotations.DynamicClass;
import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.SyncDescription;
import sparta.spartaannotations.db_class_;

import static sparta.spartaannotations.SyncDescription.service_type.Download;

/*
case "JobPluckerGroups":
				try {
					JSONObject jObj = new JSONObject(httpjobresponse);
					//JSONArray jObjResArray = jObj.getJSONArray("Result");

					JSONObject jObj1 = jObj.getJSONObject("Result");
					JSONArray jObjResArray = jObj1.getJSONArray("Result");

					Log.v("KKKKKKKKK", "JobPluckerGroups "+jObjResArray);

					for (int i=0; i<jObjResArray.length(); i++){
						JSONObject json_data = jObjResArray.getJSONObject(i);
						ContentValues Centers = new ContentValues();
						Centers.put("group_id", json_data.getInt("id"));
						Centers.put("group_no", json_data.getString("group_no"));
						Centers.put("name", json_data.getString("name"));
						Centers.put("tel_no", json_data.getString("tel_no"));
						Centers.put("registration_date", json_data.getString("registration_date"));

						String group_id = null;
						Cursor cursor1 = databaseservice.rawQuery("SELECT group_id FROM plucker_groups WHERE group_id = ?", new String[]{""+json_data.getInt("id")});
						cursor1.moveToFirst();
						if(!cursor1.isAfterLast()){
							do{
								group_id = cursor1.getString(0);
							}while(cursor1.moveToNext());

						}
						cursor1.close();


						if(group_id == null){
							try{
								databaseservice.insert("plucker_groups", null, Centers);
							}catch(Exception e){
								Log.i("%%%%%%%%%% ", "^^^^^^^^^^^^^^^ "+e);
							}
						}else{
							try{
								databaseservice.update("plucker_groups", Centers, "group_id = ?", new String[]{""+json_data.getInt("id")});
							}catch(Exception e){
								Log.i("%%%%%%%%%%%%    ", "^^^^^^^^^^^^^^^ "+e);
							}
						}
					}


					//fetchFingerPrints(0,1000);

				} catch (JSONException e) {

					Log.v("LLLLLLLLL","EEERRRRRRROOOOOOORRRRRr No plucker groups");
					//fetchFingerPrints(0,1000);
					//endService("PluckerGroups", e.getLocalizedMessage());


				}

				break;

 */
@DynamicClass(table_name = "plucker_groups")
@SyncDescription(service_name = "JobPluckerGroups",service_type = Download,download_link = svars.Block_download_link )
public class block extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "id", column_name = "group_id")
   public int group_id=0;

    @DynamicProperty(json_key = "group_no", column_name = "group_no")
    public String group_no="";

    @DynamicProperty(json_key = "name", column_name = "name")
    public String name="";

 @DynamicProperty(json_key = "tel_no", column_name = "tel_no")
    public String tel_no="";


 @DynamicProperty(json_key = "registration_date", column_name = "registration_date")
    public String registration_date="";




    public block()
    {




    }

}
