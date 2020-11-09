package sparta.realm.spartamodels;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;




/**
 * Created by Thompsons on 03-Mar-17.
 */

public class sync_service_description implements Serializable, Cloneable {

    public String service_name,upload_link,download_link,table_name;
    public String[] table_filters;
    public boolean use_download_filter=false;
   public int chunk_size=5000;
   public  service_type servic_type;
      public enum service_type{
    Null,
    Upload,
    Download,
   Download_Upload,
    Configuration
}
    public sync_service_description clone() throws
            CloneNotSupportedException
    {
        return ((sync_service_description)super.clone());
    }



    }
