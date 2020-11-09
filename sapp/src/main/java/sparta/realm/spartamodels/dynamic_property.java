package sparta.realm.spartamodels;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class dynamic_property implements Serializable {
    public String json_name="";
    public String column_name="";
    public String default_value="";
    public String value="";
    public boolean index=false;
    public int storage_mode=1;

    public dynamic_property(String json_name,String column_name,String value,boolean index,String default_value){
        this.json_name=json_name;
        this.column_name=column_name;
        this.value=value;
        this.default_value=default_value;
        this.index=index;
    }

    public dynamic_property(String json_name,String column_name,String value){
        this.json_name=json_name;
        this.column_name=column_name;
        this.value=value;

    }

    public dynamic_property(String json_name,String column_name,String value,boolean index){
        this.json_name=json_name;
        this.column_name=column_name;
        this.value=value;
        this.index=index;

    }

 public dynamic_property(String json_name,String column_name,String value,boolean index,int storage_mode){
        this.json_name=json_name;
        this.column_name=column_name;
        this.value=value;
        this.index=index;
        this.storage_mode=storage_mode;

    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

//        Check if o is an instance of Complex or not
//          "null instanceof [type]" also returns false

        if (!(o instanceof dynamic_property)) {
            return false;
        }



        // Compare the data members and return accordingly
        return value.equalsIgnoreCase(((dynamic_property)o).value);
    }

    @NonNull
    @Override
    public String toString() {
        return value;
    }
}
