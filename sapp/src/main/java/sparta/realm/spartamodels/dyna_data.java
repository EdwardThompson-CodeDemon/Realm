package sparta.realm.spartamodels;


import sparta.realm.spartautils.svars;
import sparta.spartaannotations.DynamicProperty;
import sparta.spartaannotations.db_class_;

/**
 * Created by Thompsons on 1/8/2018.
 */

public class dyna_data extends db_class_ {



    public enum operation_data_type{
        Null,
        GenderList,
        ProffessionList,
        ConsulateList,
        ZonesList,
        LocalitiesList,
        CommunesList,
        IdTypesList,
        BloodGroupsList,
        DelegatesList,
        TeintsList

    }
    @DynamicProperty(json_key = "name",column_name = "name")
    public String name="";




    @DynamicProperty(json_key = "data_type",column_name = "data_type")
    public String data_type="";

    @DynamicProperty(json_key = "parent",column_name = "parent")
    public String parent="";

    @DynamicProperty(json_key = "data",column_name = "data")
    public String data="";

    @DynamicProperty(json_key = "data_2",column_name = "data_2")
    public String data_2="";

    @DynamicProperty(json_key = "Code",column_name = "code")
    public String code="";

    public dyna_data()
    {


    }
    public dyna_data(String lid, String sid, String name, String data_type, String data, String parent, String code)
    {


        this.sid=sid;
        this.name=name;
        this.data_type=data_type;
        this.parent=parent;
        id=lid;
        this.data=data;
        this.code=code;
    }
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

//        Check if o is an instance of Complex or not
//          "null instanceof [type]" also returns false

        if (!(o instanceof dyna_data)) {
            return false;
        }



        // Compare the data members and return accordingly
        return sid.equalsIgnoreCase(((dyna_data)o).sid);
    }

}
