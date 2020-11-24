package sparta.realm.spartamodels;

import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.db_class_;

/**
 * Created by Thompsons on 01-Feb-17.
 */
@DynamicClass(table_name = "user_table")
public class user_ extends db_class_ {
    @DynamicProperty(column_name = "user_fullname")
    public String fullname="";
    @DynamicProperty(column_name = "username")
    public String username="";
    @DynamicProperty(column_name = "password")
    public String password="";



    public user_()
    {



    }

}
