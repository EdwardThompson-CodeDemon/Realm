package sparta.realm.utils;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;


import java.util.ArrayList;
import java.util.List;

import sparta.realm.R;
import sparta.realm.spartaadapters.dyna_data_adapter;
import sparta.realm.spartaadapters.dyna_data_adapter_;
import sparta.realm.spartamodels.dyna_data;
import sparta.realm.spartamodels.dyna_data_obj;
import sparta.realm.spartautils.svars;

public class InputValidation {

    public static boolean isNumeric(String str) {
        if (str == null || str.length() == 0) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean containsNumeric(String str) {
        for (char cct : str.toCharArray()) {

            try {
                Double.parseDouble(cct + "");
                return true;
            } catch (NumberFormatException e) {

            }
        }
        return false;

    }

    protected void set_text_error(EditText edt, String error) {
        edt.setError(error);
        edt.setBackground(edt.getContext().getDrawable(R.drawable.textback_error));
        edt.requestFocus();


        Toast.makeText(edt.getContext(), error, Toast.LENGTH_LONG).show();

    }


    protected boolean set_conditional_input_error(boolean valid, View edt, String error, String input, int min_length) {
        if (input == null || input.length() < min_length) {
            try {
                if (edt.getClass().isInstance(new AppCompatEditText(edt.getContext()))) {
                    ((AppCompatEditText) edt).setError(error);

                }
                edt.setBackground(edt.getContext().getDrawable(R.drawable.textback_error));
                edt.requestFocus();
            } catch (Exception ex) {
            }
            if (error != null) {
                Toast.makeText(edt.getContext(), error, Toast.LENGTH_LONG).show();

            }
            valid = false;
            return valid;
        } else {
            if (edt.getClass().isInstance(new AppCompatEditText(edt.getContext()))) {
                edt.setBackground(edt.getContext().getResources().getDrawable(R.drawable.textback));
                ((AppCompatEditText) edt).setError(null);

            }

        }

        return valid;
    }

    void clear_all(Activity act, Class<?> mm) {
        View aldv = LayoutInflater.from(act).inflate(R.layout.dialog_confirm_clear_all, null);
        final AlertDialog ald = new AlertDialog.Builder(act)
                .setView(aldv)
                .show();
        aldv.findViewById(R.id.clear_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ald.dismiss();

                svars.set_working_employee(act, null);
                svars.working_member = null;
                Intent reg_intent = new Intent(act, mm);
                reg_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
                act.startActivity(reg_intent);

                act.finish();
            }
        });

        aldv.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ald.dismiss();
            }
        });

    }



    @Deprecated
    protected void populate_spiner(final Spinner spn, final ArrayList<dyna_data_obj> original_list, String sid) {
        for (int i = 0; i < original_list.size(); i++) {
            if (original_list.get(i).sid.value.equalsIgnoreCase(sid)) {
                final int finalI = i;
                spn.post(new Runnable() {
                    @Override
                    public void run() {
                        spn.setSelection(finalI, true);

                    }
                });

            }
        }
    }

    @Deprecated
    protected void populate_spiner_(final Spinner spn, final ArrayList<dyna_data> original_list, String sid) {
        for (int i = 0; i < original_list.size(); i++) {
            if (original_list.get(i).sid.equalsIgnoreCase(sid)) {
                final int finalI = i;
                spn.post(new Runnable() {
                    @Override
                    public void run() {
                        spn.setSelection(finalI, true);

                    }
                });

            }
        }
    }
}
