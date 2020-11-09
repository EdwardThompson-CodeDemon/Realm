package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import sparta.spartaannotations.DynamicClass;
import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.SyncDescription;
import sparta.spartaannotations.db_class_;

import static sparta.spartaannotations.SyncDescription.service_type.Download;

/*
case "JobSystemUsers":

				try {
					JSONObject jObj = new JSONObject(httpjobresponse);
					Log.v("KKKKKKKKKKKK ","MMMMMMMMMM "+jObj);
					JSONObject jObjRe = jObj.getJSONObject("Result");
					JSONArray jObjResArray = jObjRe.getJSONArray("Result");

					for (int i=0; i<jObjResArray.length(); i++){

						JSONObject json_data = jObjResArray.getJSONObject(i);
						ContentValues ses = new ContentValues();
						ses.put("_id", json_data.getString("id"));
						ses.put("username", json_data.getString("username"));
						Log.v("HHHHHHH", "USERNAME "+json_data.getString("username"));
						ses.put("phone_number", json_data.getString("phone_number"));
						ses.put("account_id", json_data.getString("account_id"));


						String sessionid = null;
						Cursor cursor1 = databaseservice.rawQuery("SELECT _id FROM route_users WHERE _id = ?", new String[]{""+json_data.getString("id")});
						cursor1.moveToFirst();
						if(!cursor1.isAfterLast()){
							do{
								sessionid = cursor1.getString(0);
							}while(cursor1.moveToNext());

						}
						cursor1.close();

						if(sessionid == null){
							try{

								databaseservice.insert("route_users", null, ses);

							}catch(Exception e){
								Log.i("%%%%%%%%%%%%%%%%", "^^^^^^^^^^^^^^^ "+e);

							}
						}else{
							try{
								Log.i("%%%%%%%%%%%%%% ", sessionid+"^^^^^^^^^^^^^^^ updating id "+json_data.getString("id"));

								databaseservice.update("route_users", ses, " _id = ? ", new String[]{sessionid});
							}catch(Exception e){
								Log.i("%%%%%%%% ", "^^^^^^^^^^^^^^^ "+e);

							}
						}
					}


				}
				catch (JSONException e) {
				}


				break;

 */
@DynamicClass(table_name = "route_users")
@SyncDescription(service_name = "JobSystemUsers",service_type = Download,download_link = svars.User_download_link )
public class user extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "id", column_name = "_id")
   public String _id="";

    @DynamicProperty(json_key = "username", column_name = "username")
    public String username="";

    @DynamicProperty(json_key = "phone_number", column_name = "phone_number")
    public String phone_number="";

    @DynamicProperty(json_key = "account_id", column_name = "account_id")
    public String account_id="";




    public user()
    {




    }

}
