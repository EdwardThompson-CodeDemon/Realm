package sparta.realm.spartautils.app_control.models;

import android.graphics.drawable.Drawable;

public class module {


    public String code,name;
    public boolean active=false;
    public Drawable icon;

    public module()
    {

    }

        public module(String code, String name, boolean active)
    {

       this.name=name;
        this.code=code;
 this.active=active;





    }
}
