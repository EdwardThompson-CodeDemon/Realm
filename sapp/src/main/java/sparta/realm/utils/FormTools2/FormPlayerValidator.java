package sparta.realm.utils.FormTools2;



//import static com.realm.apps.test.weighbridgedemo.utils.FormTools2.FormPlayer.getField;
//import static com.realm.apps.test.weighbridgedemo.utils.FormTools2.FormPlayer.getInputField_;

import static sparta.realm.utils.FormTools2.FormPlayer.getField;
import static sparta.realm.utils.FormTools2.FormPlayer.getInputField_;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sparta.realm.DataManagement.Models.Query;
import sparta.realm.Realm;
import sparta.realm.utils.FormTools.models.IndependentInputFieldVariable;
import sparta.realm.utils.FormTools.models.InputField;
import sparta.realm.utils.FormTools.models.InputFieldInputConstraint;
import sparta.realm.utils.FormTools.models.InputFieldInputConstraintProcessingResult;
import sparta.realm.utils.FormTools.models.InputGroup;

public class FormPlayerValidator {

    public static Boolean checkOrConditions(List<IndependentInputFieldVariable> variables, ArrayList<InputGroup> inputGroups, boolean shouldMatch) {
        Boolean result = null;
        for (IndependentInputFieldVariable variable : variables) {
            String value = getInputField_(variable.independent_input_field, inputGroups).input;
            if ((shouldMatch && value.equals(variable.operation_value)) || (!shouldMatch && !value.equals(variable.operation_value))) {
                return true;
            }
            if (result == null) {
                result = false;
            }
        }
        return result;
    }

    public static Boolean checkAndConditions(List<IndependentInputFieldVariable> variables, ArrayList<InputGroup> inputGroups, boolean shouldMatch) {
        Boolean result = null;
        for (IndependentInputFieldVariable variable : variables) {
            String value = getInputField_(variable.independent_input_field, inputGroups).input;
            if (value == null || (shouldMatch && !value.equals(variable.operation_value)) || (!shouldMatch && value.equals(variable.operation_value))) {
                return false;
            }
            if (result == null) {
                result = true;
            }
        }
        return result;
    }

    public static boolean constraintExists(ArrayList<InputFieldInputConstraint> inputFieldInputConstraints, InputFieldInputConstraint.ConstraintType constraintType) {
        for (InputFieldInputConstraint inputFieldInputConstraint : inputFieldInputConstraints) {
            if (Objects.requireNonNull(InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) == constraintType) {
                return true;

            }
        }

        return false;
    }

    public static Boolean checkOrConditions(InputFieldInputConstraint inputFieldInputConstraint, ArrayList<InputGroup> inputGroups, boolean shouldMatch) {

      if(inputFieldInputConstraint.orIndependentInputFieldVariables.isEmpty()){return null;}
        for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.orIndependentInputFieldVariables) {
            InputField independentInputField = getInputField_(independentInputFieldVariable.independent_input_field, inputGroups);
            String independentValue = independentInputField.input;

            if (independentInputFieldVariable.independent_input_field_column != null && independentValue != null) {
                try {
                    independentValue = getField(Realm.databaseManager.loadObject(Class.forName(independentInputField.dataset), new Query().setTableFilters("sid=?").setQueryParams(independentInputField.input)), independentInputFieldVariable.independent_input_field_column);
                } catch (Exception e) {
                    independentValue = null;
                }

            }
            if (independentValue.equals(independentInputFieldVariable.operation_value) && shouldMatch) {
                return true;
            } else if (independentValue.equals(independentInputFieldVariable.operation_value) && !shouldMatch) {
                return false;

            }
        }
        return !shouldMatch;
    }

    public static Boolean checkAndConditions(InputFieldInputConstraint inputFieldInputConstraint, ArrayList<InputGroup> inputGroups, boolean shouldMatch) {

        if(inputFieldInputConstraint.andIndependentInputFieldVariables.isEmpty()){return null;}

        for (IndependentInputFieldVariable independentInputFieldVariable : inputFieldInputConstraint.andIndependentInputFieldVariables) {
            InputField independentInputField = getInputField_(independentInputFieldVariable.independent_input_field, inputGroups);
            String independentValue = independentInputField.input;

            if (independentInputFieldVariable.independent_input_field_column != null && independentValue != null) {
                try {
                    independentValue = getField(Realm.databaseManager.loadObject(Class.forName(independentInputField.dataset), new Query().setTableFilters("sid=?").setQueryParams(independentInputField.input)), independentInputFieldVariable.independent_input_field_column);
                } catch (Exception e) {
                    independentValue = null;

                }

            }

            if (independentValue == null
                    ||(!independentValue.equals(independentInputFieldVariable.operation_value)&&shouldMatch)
                    ||(independentValue.equals(independentInputFieldVariable.operation_value)&&!shouldMatch)) {
                return false;
            }
        }
        return true;
    }

    public static InputFieldInputConstraintProcessingResult processEqualToConstraint(ArrayList<InputFieldInputConstraint> inputFieldInputConstraints, ArrayList<InputGroup> inputGroups, InputFieldInputConstraintProcessingResult inputFieldInputConstraintProcessingResult) {

        for (InputFieldInputConstraint inputFieldInputConstraint : inputFieldInputConstraints) {
            if (Objects.requireNonNull(InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) == InputFieldInputConstraint.ConstraintType.EqualTo) {
                Boolean orFilterOk = checkOrConditions(inputFieldInputConstraint, inputGroups, true);
                if (Boolean.TRUE.equals(orFilterOk)) {
                    inputFieldInputConstraintProcessingResult.field_active = true;
                    return inputFieldInputConstraintProcessingResult;
                }
                Boolean andFilterOk = checkAndConditions(inputFieldInputConstraint, inputGroups, true);

                if (Boolean.TRUE.equals(andFilterOk)) {
                    inputFieldInputConstraintProcessingResult.field_active = true;
                    return inputFieldInputConstraintProcessingResult;
                }

            }

        }

        inputFieldInputConstraintProcessingResult.field_active = false;
        return inputFieldInputConstraintProcessingResult;

    }
  public static InputFieldInputConstraintProcessingResult processNotEqualToConstraint(ArrayList<InputFieldInputConstraint> inputFieldInputConstraints, ArrayList<InputGroup> inputGroups, InputFieldInputConstraintProcessingResult inputFieldInputConstraintProcessingResult) {

        for (InputFieldInputConstraint inputFieldInputConstraint : inputFieldInputConstraints) {
            if (Objects.requireNonNull(InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) == InputFieldInputConstraint.ConstraintType.NotEqualTo) {
                Boolean orFilterOk = checkOrConditions(inputFieldInputConstraint, inputGroups, false);
                if (Boolean.FALSE.equals(orFilterOk)) {
                    inputFieldInputConstraintProcessingResult.field_active = true;
                    return inputFieldInputConstraintProcessingResult;
                }
                Boolean andFilterOk = checkAndConditions(inputFieldInputConstraint, inputGroups, false);

                if (Boolean.TRUE.equals(andFilterOk)) {
                    inputFieldInputConstraintProcessingResult.field_active = true;
                    return inputFieldInputConstraintProcessingResult;
                }

            }

        }

        inputFieldInputConstraintProcessingResult.field_active = false;
        return inputFieldInputConstraintProcessingResult;

    }
      public static InputFieldInputConstraintProcessingResult processParentChildConstraint(ArrayList<InputFieldInputConstraint> inputFieldInputConstraints, ArrayList<InputGroup> inputGroups, InputFieldInputConstraintProcessingResult inputFieldInputConstraintProcessingResult) {

          for (InputFieldInputConstraint inputFieldInputConstraint : inputFieldInputConstraints) {
              if (Objects.requireNonNull(InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) == InputFieldInputConstraint.ConstraintType.ParentChild) {
                  InputField independentInputField = getInputField_(inputFieldInputConstraint.independent_input_field, inputGroups);
//                    String independentInput = getField(registeringObject, independentInputField.object_field_name);
                  String independentInput = independentInputField.input;
                  if (inputFieldInputConstraint.independent_column != null && independentInput != null) {
                      try {
                          independentInput = getField(Realm.databaseManager.loadObject(Class.forName(independentInputField.dataset), new Query().setTableFilters("sid=?").setQueryParams(independentInputField.input)), inputFieldInputConstraint.independent_column);
                      } catch (Exception e) {
                          independentInput = null;
//                            throw new RuntimeException(e);
                      }
                  }
                  if (independentInput == null) {
                      inputFieldInputConstraintProcessingResult.field_active = false;
                      return inputFieldInputConstraintProcessingResult;
                  } else {
                      inputFieldInputConstraintProcessingResult.tableFilters.add(inputFieldInputConstraint.dependent_column + "='" + independentInput + "'");
                  }
              }

          }
          return inputFieldInputConstraintProcessingResult;
      }

    public static InputFieldInputConstraintProcessingResult processIncludeOnlyConstraint(ArrayList<InputFieldInputConstraint> inputFieldInputConstraints, ArrayList<InputGroup> inputGroups, InputFieldInputConstraintProcessingResult inputFieldInputConstraintProcessingResult) {

        for (InputFieldInputConstraint inputFieldInputConstraint : inputFieldInputConstraints) {
            if (Objects.requireNonNull(InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) == InputFieldInputConstraint.ConstraintType.IncludeOnly) {
                Boolean orFilterOk = checkOrConditions(inputFieldInputConstraint, inputGroups, true);
                if (Boolean.TRUE.equals(orFilterOk)) {
                    inputFieldInputConstraintProcessingResult.tableFilters.add("sid IN (" + inputFieldInputConstraint.dataset_values + ")");
                    inputFieldInputConstraintProcessingResult.field_active = true;
                    return inputFieldInputConstraintProcessingResult;
                }
                Boolean andFilterOk = checkAndConditions(inputFieldInputConstraint, inputGroups, true);

                if (Boolean.TRUE.equals(andFilterOk)) {
                    inputFieldInputConstraintProcessingResult.field_active = true;
                    inputFieldInputConstraintProcessingResult.tableFilters.add("sid IN (" + inputFieldInputConstraint.dataset_values + ")");
                    return inputFieldInputConstraintProcessingResult;
                }
            }


        }

        inputFieldInputConstraintProcessingResult.field_active = false;

        return inputFieldInputConstraintProcessingResult;
    }
    public static InputFieldInputConstraintProcessingResult processExcludeConstraint(ArrayList<InputFieldInputConstraint> inputFieldInputConstraints, ArrayList<InputGroup> inputGroups, InputFieldInputConstraintProcessingResult inputFieldInputConstraintProcessingResult) {

        for (InputFieldInputConstraint inputFieldInputConstraint : inputFieldInputConstraints) {
            if (Objects.requireNonNull(InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) == InputFieldInputConstraint.ConstraintType.Exclude) {
                Boolean orFilterOk = checkOrConditions(inputFieldInputConstraint, inputGroups, true);
                if (Boolean.TRUE.equals(orFilterOk)) {
                    inputFieldInputConstraintProcessingResult.tableFilters.add("sid NOT IN (" + inputFieldInputConstraint.dataset_values + ")");
                    inputFieldInputConstraintProcessingResult.field_active = true;
//                    return inputFieldInputConstraintProcessingResult;
                }
                Boolean andFilterOk = checkAndConditions(inputFieldInputConstraint, inputGroups, true);

                if (Boolean.TRUE.equals(andFilterOk)) {
                    inputFieldInputConstraintProcessingResult.field_active = true;
                    inputFieldInputConstraintProcessingResult.tableFilters.add("sid NOT IN (" + inputFieldInputConstraint.dataset_values + ")");
//                    return inputFieldInputConstraintProcessingResult;
                }
            }


        }

//        inputFieldInputConstraintProcessingResult.field_active = false;

        return inputFieldInputConstraintProcessingResult;
    }
 public static InputFieldInputConstraintProcessingResult processEqualToIncludeOnlyElseShowAllConstraint(ArrayList<InputFieldInputConstraint> inputFieldInputConstraints, ArrayList<InputGroup> inputGroups, InputFieldInputConstraintProcessingResult inputFieldInputConstraintProcessingResult) {

        for (InputFieldInputConstraint inputFieldInputConstraint : inputFieldInputConstraints) {
            if (Objects.requireNonNull(InputFieldInputConstraint.ConstraintType.values()[Integer.parseInt(inputFieldInputConstraint.constraint_type)]) == InputFieldInputConstraint.ConstraintType.EQUAL_TO_INCLUDE_ONLY_ELSE_SHOW_ALL) {
                Boolean orFilterOk = checkOrConditions(inputFieldInputConstraint, inputGroups, true);
                if (Boolean.TRUE.equals(orFilterOk)) {
                    inputFieldInputConstraintProcessingResult.tableFilters.add("sid IN (" + inputFieldInputConstraint.dataset_values + ")");
                    inputFieldInputConstraintProcessingResult.field_active = true;
                    return inputFieldInputConstraintProcessingResult;
                }
                Boolean andFilterOk = checkAndConditions(inputFieldInputConstraint, inputGroups, true);

                if (Boolean.TRUE.equals(andFilterOk)) {
                    inputFieldInputConstraintProcessingResult.field_active = true;
                    inputFieldInputConstraintProcessingResult.tableFilters.add("sid IN (" + inputFieldInputConstraint.dataset_values + ")");
                    return inputFieldInputConstraintProcessingResult;
                }
            }


        }


        return inputFieldInputConstraintProcessingResult;
    }

    public static boolean processEqualToConstraint(InputFieldInputConstraint constraint, ArrayList<InputGroup> inputGroups, InputFieldInputConstraintProcessingResult result) {
        Boolean orFilterOk = checkOrConditions(constraint.orIndependentInputFieldVariables, inputGroups, true);
        Boolean andFilterOk = checkAndConditions(constraint.andIndependentInputFieldVariables, inputGroups, true);
        if (Boolean.TRUE.equals(andFilterOk) || Boolean.FALSE.equals(orFilterOk)) {
            result.tableFilters.add("sid IN (" + constraint.dataset_values + ")");
            result.field_active = true;
            return true;
        }
        return false;
    }

    public static boolean processNotEqualToConstraint(InputFieldInputConstraint constraint, ArrayList<InputGroup> inputGroups, InputFieldInputConstraintProcessingResult result) {
        Boolean orFilterOk = checkOrConditions(constraint.orIndependentInputFieldVariables, inputGroups, false);
        Boolean andFilterOk = checkAndConditions(constraint.andIndependentInputFieldVariables, inputGroups, true);
        if (Boolean.FALSE.equals(andFilterOk)) {
            result.tableFilters.add("sid IN (" + constraint.dataset_values + ")");
            result.field_active = true;
            return true;
        }
        return false;
    }

    private boolean processExcludeConstraint(InputFieldInputConstraint constraint, ArrayList<InputGroup> inputGroups) {
        Boolean orFilterOk = checkOrConditions(constraint.orIndependentInputFieldVariables, inputGroups, false);
        Boolean andFilterOk = checkAndConditions(constraint.andIndependentInputFieldVariables, inputGroups, true);
        return (orFilterOk == null || orFilterOk) || (andFilterOk != null && andFilterOk);
    }
}
