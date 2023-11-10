package sparta.realm.utils.FormTools.views;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sparta.realm.Adapters.GeneralDataAdapterListener;
import sparta.realm.Adapters.GeneralDataAdapterView;
import sparta.realm.DataManagement.Models.Query;
import sparta.realm.R;
import sparta.realm.Realm;

import sparta.realm.utils.FormTools.FormPlayer;
import sparta.realm.utils.FormTools.models.InputField;
import sparta.realm.utils.FormTools.models.InputFieldInputConstraintProcessingResult;
import sparta.realm.utils.FormTools.models.InputGroup;
import sparta.realm.utils.FormTools.models.SelectionData;

public class FormInputDisplayView extends GeneralDataAdapterView<InputField, GeneralDataAdapterListener<InputField>> {

    TextView title;
    TextView value;

    FormInputDisplayView(View itemView) {
        super(itemView);
    }


    @Override
    public void onBind(InputField inputField) {
        InputFieldInputConstraintProcessingResult inputFieldInputConstraintProcessingResult = null;
        if (inputField.inputFieldInputConstraints != null && inputField.inputFieldInputConstraints.size() > 0) {
            inputFieldInputConstraintProcessingResult = FormPlayer.getInputFieldInputConstraintProcessingResult(inputField.inputFieldInputConstraints, items);
            if (!inputFieldInputConstraintProcessingResult.field_active) {
//                inputField.input = null;
//                inputField.inputValid = true;
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.height = 0;
                itemView.setLayoutParams(params);
                return;
            } else {
                if (inputField.input_type.equals(InputField.InputType.ValueOnly.ordinal() + "")) {
//                    inputField.inputValid = true;
                    ViewGroup.LayoutParams params = itemView.getLayoutParams();
                    params.height = 0;
                    itemView.setLayoutParams(params);
                    return;
                } else {
                    ViewGroup.LayoutParams params = itemView.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    itemView.setLayoutParams(params);
//                    inputField.inputValid = false;
                }

            }
        } else {
            if (inputField.input_type.equals(InputField.InputType.ValueOnly.ordinal() + "")) {
//                inputField.inputValid = true;
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.height = 0;
                itemView.setLayoutParams(params);
                return;
            } else {
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                itemView.setLayoutParams(params);
            }


        }

        title.setText(inputField.title);
        if (inputField.input_type.equals(InputGroup.InputType.Selection.ordinal() + "")) {

            try {
                value.setText(((SelectionData) Realm.databaseManager.loadObject(Class.forName(inputField.dataset), new Query().setTableFilters("sid=?").setQueryParams(inputField.input))).name);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        } else {
            value.setText(inputField.input);

        }

    }

    public FormInputDisplayView() {
        super(LayoutInflater.from(Realm.context).inflate(R.layout.item_form_input_display, null, true), null);
        title = itemView.findViewById(R.id.title);
        value = itemView.findViewById(R.id.value);
    }


}
