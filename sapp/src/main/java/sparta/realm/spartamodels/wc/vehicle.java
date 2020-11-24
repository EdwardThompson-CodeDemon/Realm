package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import static com.realm.annotations.SyncDescription.service_type.Download;

/*
case "JobGetVehicles":
				JSONObject jObj_ = null;
				try {
					jObj_ = new JSONObject(httpjobresponse);
					JSONObject jObjResult = jObj_.getJSONObject("Result");
					JSONArray jObjResArray = jObjResult.getJSONArray("Result");
					for (int i=0; i<jObjResArray.length(); i++) {
						JSONObject json_data = jObjResArray.getJSONObject(i);
						ContentValues val = new ContentValues();
						val.put("member_id", String.valueOf(json_data.getInt("id")));
						val.put("member_no", json_data.getString("vehicle_no"));
						val.put("sub_account_id", 1);
						val.put("full_name", json_data.getString("vehicle_no"));
						val.put("reg_date", json_data.getString("datecomparer"));
						val.put("nat_id", json_data.getString("vehicle_no"));

						val.put("marital_status_id", 1);
						val.put("phone1", "07000000");

						String member_no = null;
						Cursor cursor1 = databaseservice.rawQuery("SELECT member_id FROM TBL_vehicles WHERE member_id = ?", new String[]{""+json_data.getInt("id")});
						cursor1.moveToFirst();
						if(!cursor1.isAfterLast()){
							do{
								member_no = cursor1.getString(0);
							}while(cursor1.moveToNext());

						}
						cursor1.close();


						if(member_no == null){
							try{
								databaseservice.insert("TBL_vehicles", null, val);
							}catch(Exception e){
								Log.i("%%%%%%%%%%   ", "^^^^^^^^^^^^^^^ "+e);
							}
						}else{
							try{
								databaseservice.update("TBL_vehicles", val, "member_id = ?", new String[]{""+json_data.getInt("id")});
							}catch(Exception e){
								Log.i("%%%%%%%%%%% ", "^^^^^^^^^^^^^^^ "+e);
							}
						}
					}

				} catch (JSONException e) {
					//endService("GetVehicles", e.getLocalizedMessage());

				}

 */
@DynamicClass(table_name = "TBL_vehicles")
@SyncDescription(service_name = "JobGetVehicles",service_type = Download,download_link = svars.Vehicle_download_link )
public class vehicle extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "id", column_name = "member_id")
   public String member_id="";

    @DynamicProperty(json_key = "vehicle_no", column_name = "member_no")
    public String member_no="";

    @DynamicProperty(json_key = "vehicle_no",column_name = "full_name")
    public String full_name="";


 @DynamicProperty(json_key = "datecomparer",column_name = "reg_date")
    public String reg_date="";


 @DynamicProperty(json_key = "vehicle_no",column_name = "nat_id")
    public String nat_id="";

@DynamicProperty(column_default_value = "1",column_name = "marital_status_id")
    public String marital_status_id="";

@DynamicProperty(column_default_value = "07000000",column_name = "phone1")
    public String phone1="";




    public vehicle()
    {




    }

}
