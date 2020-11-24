package sparta.realm.spartamodels.wc;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import static com.realm.annotations.SyncDescription.service_type.Download;

/*
SupplierAccounts.put("company_name", jMembersacc.getString("company_name"));
					SupplierAccounts.put("address", jMembersacc.getString("postal_adress"));
					SupplierAccounts.put("mobile", jMembersacc.getString("phone_1"));
					SupplierAccounts.put("phone", jMembersacc.getString("phone_1"));
					SupplierAccounts.put("email", jMembersacc.getString("email"));

					int as = 0;
					String[] WhereArgs1 = {"1"};
					Cursor cursor1 = databaseservice.rawQuery("SELECT id FROM company_details WHERE company_id = ?", WhereArgs1);
					cursor1.moveToFirst();
					if(!cursor1.isAfterLast()){
						do{
							as = cursor1.getInt(0);
						}while(cursor1.moveToNext());

					}
					cursor1.close();

					if(as > 1){

						Log.v("OOOOOO","PPPPPPPPPPPP UPDATING COMPANY DETAILS ");
						try{
							databaseservice.update("company_details", SupplierAccounts, null, null);
						}catch(Exception ep){
							ep.printStackTrace();
						}


					}else{
						Log.v("OOOOOO","PPPPPPPPPPPP INSERTING COMPANY DETAILS ");
						try{
							databaseservice.insert("company_details", null, SupplierAccounts);
						}catch(Exception ep){
							Log.v("LLLLLLLLLL","KKKKKKKKKKK "+ep.getLocalizedMessage());
							ep.printStackTrace();

						}

					}
 */
@DynamicClass(table_name = "company_details")
@SyncDescription(service_name = "JobGetCompanyDetails",service_type = Download,download_link = svars.Company_details_download_link,chunk_size = 1,use_download_filter = false)
public class company_details extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "company_name", column_name = "company_name")
   public String company_name="";

    @DynamicProperty(json_key = "postal_adress", column_name = "address")
    public String address="";

    @DynamicProperty(json_key = "phone_1", column_name = "mobile")
    public String mobile="";

 @DynamicProperty(json_key = "phone_1", column_name = "phone")
    public String phone="";


@DynamicProperty(json_key = "email", column_name = "email")
    public String email="";




    public company_details()
    {




    }

}
