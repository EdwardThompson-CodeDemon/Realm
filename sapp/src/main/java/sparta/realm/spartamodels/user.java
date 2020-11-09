package sparta.realm.spartamodels;

/**
 * Created by Thompsons on 01-Feb-17.
 */

public class user extends db_class {
    public dynamic_property fullname=new dynamic_property("fulluname","user_fullname",null,false);
    public dynamic_property username=new dynamic_property("fulluname","username",null,false);
    public dynamic_property password=new dynamic_property("fulluname","password",null,false);



    public user()
    {
       super("user_table");



    }

}
