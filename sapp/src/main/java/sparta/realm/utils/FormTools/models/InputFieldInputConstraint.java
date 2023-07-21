package sparta.realm.utils.FormTools.models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;

import java.io.Serializable;


@DynamicClass(table_name = "input_field_constraints")
public class InputFieldInputConstraint extends RealmModel implements Serializable {



    @DynamicProperty(json_key = "input_field")
    public String input_field;

    @DynamicProperty(json_key = "constraint_type")//equal to,not equal to,parent_child
    public String constraint_type;

    @DynamicProperty(json_key = "independent_input_field")
    public String independent_input_field;
    @DynamicProperty(json_key = "dependent_column")
    public String dependent_column;

    @DynamicProperty(json_key = "operation_value")
    public String operation_value;

    public enum ConstraintType{
        EqualTo,
        NotEqualTo,
        ParentChild
    }


    public InputFieldInputConstraint(String constraint_type, String independent_input_field, String dependent_column, String operation_value) {
        this.constraint_type = constraint_type;
        this.independent_input_field = independent_input_field;
        this.dependent_column = dependent_column;
        this.operation_value = operation_value;
        this.sid = sid;

    }



    public InputFieldInputConstraint() {


    }

}
