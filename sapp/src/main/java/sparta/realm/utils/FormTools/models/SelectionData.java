package sparta.realm.utils.FormTools.models;


import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;

import java.io.Serializable;

import sparta.realm.spartamodels.dyna_data_obj;


public class SelectionData extends RealmModel implements Serializable {



    @DynamicProperty(json_key = "name")
    public String name;

    @DynamicProperty(json_key = "parent")
    public String parent;

    @DynamicProperty(json_key = "code")
    public String code;


    public SelectionData() {


    }

    public SelectionData(String sid, String name) {
        this.sid = sid;
this.name=name;

    }


    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }

//        Check if o is an instance of Complex or not
//          "null instanceof [type]" also returns false

        if (!(o instanceof dyna_data_obj)) {
            return false;
        }


        // Compare the data members and return accordingly
        return sid.equalsIgnoreCase(((dyna_data_obj) o).sid.value);
    }
}
