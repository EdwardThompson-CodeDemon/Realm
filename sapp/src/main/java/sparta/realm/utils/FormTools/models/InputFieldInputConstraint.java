package sparta.realm.utils.FormTools.models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;

import java.io.Serializable;
import java.util.ArrayList;


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

 @DynamicProperty(json_key = "independent_column")
    public String independent_column;

    @DynamicProperty(json_key = "operation_value")
    public String operation_value;
    @DynamicProperty(json_key = "dataset_values")
    public String dataset_values;
    public ArrayList<IndependentInputFieldVariable> orIndependentInputFieldVariables=new ArrayList<>();
    public ArrayList<IndependentInputFieldVariable> andIndependentInputFieldVariables=new ArrayList<>();
    public enum ConstraintType{
        EqualTo,//displays the inputfield only if independent inputfield variable returns true
        // for orfilter or the and filteris equal to the value set.This is a display filter
        NotEqualTo,//displays the inputfield only if independent inputfield variable returns false
        // for orfilter or the and filteris equal to the value set.This is a display filter
        ParentChild,//displays if the parent is not null
        IncludeOnly,
        Exclude,
              EQUAL_TO_INCLUDE_ONLY_ELSE_SHOW_ALL,//always displays but if input variable contitions pass includes only the dataset
        NOT_EQUAL_TO_INCLUDE_ONLY_ELSE_SHOW_ALL
    }


    public InputFieldInputConstraint(String constraint_type, String independent_input_field, String dependent_column, String operation_value,ArrayList<IndependentInputFieldVariable> orIndependentInputFieldVariables,ArrayList<IndependentInputFieldVariable> andIndependentInputFieldVariables) {
        this.constraint_type = constraint_type;
        this.independent_input_field = independent_input_field;
        this.dependent_column = dependent_column;
        this.operation_value = operation_value;
        this.orIndependentInputFieldVariables = orIndependentInputFieldVariables;
        this.andIndependentInputFieldVariables = andIndependentInputFieldVariables;
        this.sid = sid;

    }



    public InputFieldInputConstraint() {


    }

}
