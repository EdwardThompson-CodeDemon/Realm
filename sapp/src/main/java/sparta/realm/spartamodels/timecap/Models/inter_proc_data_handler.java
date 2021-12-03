package sparta.realm.spartamodels.timecap.Models;

import android.content.ContentValues;
import android.database.Cursor;

import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.db_class_;

import java.io.Serializable;

import sparta.realm.Services.DatabaseManager;

@DynamicClass(table_name = "inter_proc_data_handler")

public class inter_proc_data_handler extends db_class_ implements Serializable {





    @DynamicProperty(column_name = "person_in_view")
    public String person_in_view="";


  public static  void set_person_in_view(boolean person_in_view){
        ContentValues cv=new ContentValues();
        cv.put("person_in_view",""+person_in_view);
        cv.put("sid","1");
        DatabaseManager.database.replace("inter_proc_data_handler",null,cv);
    }
    public static boolean person_in_view(){
        Cursor c=  DatabaseManager.database.rawQuery("SELECT * FROM inter_proc_data_handler WHERE person_in_view LIKE '%true%'",null);
        if(c.moveToFirst())
        {
            c.close();
            return true;
        }else {
            c.close();
            return false;
        }
    }

}
