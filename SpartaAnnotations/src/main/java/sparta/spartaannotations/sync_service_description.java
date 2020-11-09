package sparta.spartaannotations;

import java.io.Serializable;


/**
 * Created by Thompsons on 03-Mar-17.
 */

public class sync_service_description implements Serializable, Cloneable {

    public String service_name,upload_link,download_link,table_name,object_package;
    public String[] table_filters;
    public boolean use_download_filter=false;
   public int chunk_size=5000;
   public SyncDescription.service_type servic_type;


public sync_service_description ()
{

}
public sync_service_description (String service_name, String upload_link, String download_link, String table_name, String[] table_filters, boolean use_download_filter, int chunk_size, SyncDescription.service_type servic_type)
{
    this.service_name=service_name;
    this.upload_link=upload_link;
    this.download_link=download_link;
    this.table_name=table_name;
    this.table_filters=table_filters;
    this.use_download_filter=use_download_filter;
    this.chunk_size=chunk_size;
    this.table_filters=table_filters;
    this.servic_type=servic_type;

}
    public sync_service_description clone() throws
            CloneNotSupportedException
    {
        return ((sync_service_description)super.clone());
    }



    }
