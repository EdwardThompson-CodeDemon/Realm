package sparta.realm.spartamodels;

import android.util.Log;

import java.io.Serializable;

import sparta.realm.spartautils.app_control.models.sparta_app_credentials;

/**
 * Created by Thompsons on 1/8/2018.
 */

public class apk_version extends db_class implements Serializable {
    public dynamic_property app_id=new dynamic_property("app_id","app_id",null,true);
    public dynamic_property branch_id=new dynamic_property("branch_id","branch_id",null,true);
    public dynamic_property release_date=new dynamic_property("release_date","release_date",null,false);
    public dynamic_property release_name=new dynamic_property("release_name","release_name",null,false);
    public dynamic_property version_code=new dynamic_property("version_code","version_code",null,true);
    public dynamic_property version_name=new dynamic_property("version_name","version_name",null,false);
    public dynamic_property version_type=new dynamic_property("version_type","version_type",null,false);
    public dynamic_property download_link=new dynamic_property("download_link","download_link",null,false);
    public dynamic_property ratings=new dynamic_property("ratings","ratings",null,false);
    public dynamic_property web_application_link=new dynamic_property("web_application_link","web_application_link",null,false);
    public dynamic_property release_notes=new dynamic_property("release_notes","release_notes",null,false);
    public dynamic_property icon=new dynamic_property("icon","icon",null,false);
    public dynamic_property version_id=new dynamic_property("version_id","version_id",null,false);
    public dynamic_property local_path=new dynamic_property("local_path","local_path",null,false);
    public dynamic_property creation_time=new dynamic_property("creation_time","creation_time",null,false);





    //;

   public sparta_app_credentials default_web_credentials,default_app_credentials;




    public apk_version()
    {
        super("app_versions_table");


    }
}
