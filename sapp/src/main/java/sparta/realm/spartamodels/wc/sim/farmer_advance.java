package sparta.realm.spartamodels.wc.sim;


import java.io.Serializable;

import sparta.realm.spartautils.svars;
import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import static com.realm.annotations.SyncDescription.service_type.Download;
import static com.realm.annotations.SyncDescription.service_type.Download_Upload;
import static com.realm.annotations.SyncDescription.service_type.Upload;


/*
	int scalecounter = 0;
			Cursor cursor = databaseservice.rawQuery("SELECT _id, farmer_id, netweight,amount_requsted, center_id,delegate FROM farmer_advances WHERE status  = ? LIMIT 1", new String[]{"new"});
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				do {
					JSONObject jsonObj = new JSONObject();
					try {
						jsonObj.put("FarmerId", cursor.getString(1));
						jsonObj.put("TotalKGSCollected", Double.valueOf(cursor.getString(2))/2);
						jsonObj.put("RequestedAmount", cursor.getString(3));
						jsonObj.put("CollectionCenter", cursor.getString(4));
						jsonObj.put("Delegate", cursor.getString(5));

						money_advance_id = cursor.getInt(0);

						Log.v("LLLLLLLLLL","HHHHHHHHH capagri "+jsonObj);

						url = "https://weightcapture.cs4africa.com/ivory_capagri/Loan/mainPesa/IvoryAdvances";
						service_url = url;
						HTTPjob = "Capagri";
						Params = jsonObj.toString();
				*/
@DynamicClass(table_name = "farmer_advances")
@SyncDescription(service_name = "Capagri",service_type = Upload,upload_link = svars.Farmer_advances_upload_link)
public class farmer_advance extends db_class_ implements Serializable {



    @DynamicProperty(json_key = "FarmerId", column_name = "farmer_id")
   public String farmer_id="";

    @DynamicProperty(json_key = "TotalKGSCollected", column_name = "netweight")
    public String netweight="";

  @DynamicProperty(json_key = "RequestedAmount", column_name = "amount_requsted")
    public String amount_requsted="";

  @DynamicProperty(json_key = "CollectionCenter", column_name = "center_id")
    public String center_id="";



  @DynamicProperty(json_key = "Delegate", column_name = "delegate")
    public String delegate="";





    public farmer_advance()
    {




    }

}
