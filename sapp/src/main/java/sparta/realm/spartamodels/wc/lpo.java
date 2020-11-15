package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import sparta.spartaannotations.DynamicClass;
import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.SyncDescription;
import sparta.spartaannotations.db_class_;

import static sparta.spartaannotations.SyncDescription.service_type.Download;
import static sparta.spartaannotations.SyncDescription.service_type.Upload;

/*
case "InsertLPO":

				try {
					JSONObject jobjRes = new JSONObject(httpjobresponse);
					String Message = jobjRes.getString("Message");
					Log.v("KKKKKKK",collection_id+" KKKKKKKKKKKKK 0000 -- == MESSAGE "+Message);
					if(Message.matches("Added Successfully")){
						String whereClause = "_id = ?";
						ContentValues cv = new ContentValues();
						cv.put("status", 1);
						databaseservice.update("TBL_inventory", cv, whereClause, new String[]{collection_id});
					}else if(Message.matches("The receipt already exists")){
						String whereClause = "_id = ?";
						ContentValues cv = new ContentValues();
						cv.put("status", 1);
						databaseservice.update("TBL_inventory", cv, whereClause, new String[]{collection_id});
					}

					if (Message.equalsIgnoreCase("The receipt already exists")){
						String whereClause = "_id = ?";
						ContentValues cv = new ContentValues();
						cv.put("status", 1);
						databaseservice.update("TBL_inventory", cv, whereClause, new String[]{collection_id});
					}


					LPO_Object();


				} catch (final JSONException e1) {
					// TODO Auto-generated catch block
					endService("JobInventory",e1.getLocalizedMessage());
				}
				break;

 */
@DynamicClass(table_name = "TBL_inventory")
@SyncDescription(service_name = "InsertLPO",service_type = Upload,download_link = svars.Lpo_upload_link )
public class lpo extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "id", column_name = "id")
   public String id="";

    @DynamicProperty(json_key = "buyer_name", column_name = "buyer_name")
    public String buyer_name="";

    @DynamicProperty(json_key = "buyer_contact",column_name = "buyer_contact")
    public String buyer_contact="";


 @DynamicProperty(json_key = "registration_date",column_name = "registration_date")
    public String registration_date="";







    public lpo()
    {




    }

}
