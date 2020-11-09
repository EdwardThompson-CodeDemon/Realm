package sparta.realm.spartautils.app_control.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class sparta_app implements Serializable {
    public String app_name,developer_team,creation_time,assembly_name;
    public ArrayList<String> categories_str;
    public ArrayList<sparta_app_category> categories;
    public String app_id;
    public String icon_url,app_about;
    public boolean is_installed;
    public String installed_version;
public ArrayList<sparta_app_version> released_versions;
    public Bitmap get_logo() {
            return null;
    }
}
