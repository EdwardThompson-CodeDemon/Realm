package sparta.realm.spartautils.app_control.models;

/**
 * Created by Thompsons on 1/8/2018.
 */

public class apk_version {

    public String code,name,sid,date,path,status;
    public apk_version(String sid, String name, String code, String date, String path, String status)
    {
        this.sid=sid;
        this.name=name;
        this.code=code;
        this.date=date;
        this.path=path;
        this.status=status;
    }
}
