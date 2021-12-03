package sparta.realm.spartamodels.timecap.Models;

import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.db_class_;


@DynamicClass(table_name = "departments_table")
//@SyncDescription(service_id = "9",service_name = "Departments",service_type = SyncDescription.service_type.Download,chunk_size = 1000,download_link = "/Configurations/Departments/GetDepartments",is_ok_position = "JO:IsOkay",download_array_position = "JO:Result;JO:Result")
public class department extends db_class_ {
// {
//                "$id": "3",
//                "id": 1,
//                "department_name": "MANAGING DIR OFC",
//                "code": null,
//                "datecomparer": 16045197195303898
//            }
//
    @DynamicProperty(json_key = "department_name")
    public String name="";
    @DynamicProperty(json_key = "code")
    public String code="";
    @DynamicProperty(json_key = "msid")
    public String msid="";
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof department)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        department c = (department) o;
        return c.msid.equalsIgnoreCase(this.msid);
    }

    public department()
    {

    }
}
