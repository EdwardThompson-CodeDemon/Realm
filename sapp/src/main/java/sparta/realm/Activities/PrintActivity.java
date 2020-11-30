package sparta.realm.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sparta.realm.R;
import sparta.realm.spartamodels.order;
import sparta.realm.spartamodels.order_item;
import sparta.realm.spartautils.bluetooth.bt_device_connector;
import sparta.realm.spartautils.printing.fiscal_printer_ui;
import sparta.realm.spartautils.svars;


public class PrintActivity extends AppCompatActivity {

    order sale;
    String print_type="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        try{
            print_type=(getIntent().getStringExtra("p_type"));
        }catch (Exception ex){
            getIntent().putExtra("Error","Print type error");
            setResult(RESULT_CANCELED,getIntent());
            finish();
            return;
        }

        if(print_type.equalsIgnoreCase("f")||print_type.equalsIgnoreCase("n"))
        {
            try{
                sale= sale_from_json(new JSONObject(getIntent().getStringExtra("sale")));
                if(sale==null)
                {
                    getIntent().putExtra("Error","Sale object error");
                    setResult(RESULT_CANCELED,getIntent());
                    finish();
                    return;
                }
            }catch (Exception ex){


                getIntent().putExtra("Error","Sale object error");
                setResult(RESULT_CANCELED,getIntent());
                finish();
                return;
            }

        }








        if(print_type.equalsIgnoreCase("f"))
        {
            fiscal_printer_ui.start(svars.bt_device_address(this, bt_device_connector.bt_device_type.printer),this,sale ,getIntent().getIntExtra("position",0),true);
            fiscal_printer_ui.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    finish();
                }
            });

        }else if(print_type.equalsIgnoreCase("n"))
        {
            fiscal_printer_ui.start(svars.bt_device_address(this, bt_device_connector.bt_device_type.printer),this,sale ,getIntent().getIntExtra("position",0),false);
            fiscal_printer_ui.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    finish();
                }
            });

        }else if(getIntent().getStringExtra("p_type").equalsIgnoreCase("z"))
        {
            fiscal_printer_ui.performZReport_ken(svars.bt_device_address(this, bt_device_connector.bt_device_type.printer),this);
            fiscal_printer_ui.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    finish();
                }
            });

        }else if(getIntent().getStringExtra("p_type").equalsIgnoreCase("c"))
        {
            fiscal_printer_ui.cancel_receipt(svars.bt_device_address(this, bt_device_connector.bt_device_type.printer),this);
            fiscal_printer_ui.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    finish();
                }
            });

        }else {

            getIntent().putExtra("Error","Print type error");
            setResult(RESULT_CANCELED,getIntent());
            finish();
            return;
        }


    }




    order sale_from_json(JSONObject jos)
    {
        order sale = new order();

        try {
            sale.customer_name=jos.getString("customerName");
            sale.order_no=jos.getString("id");
            sale.total_amount=jos.getString("grossReceipt");
            sale.receipt_status= svars.is_used(this,jos.getString("id"))?"1":"0";

            JSONArray response_ARR = jos.getJSONArray("items");


            Log.e("Sales arr:", "" + response_ARR.toString());


            for (int i = 0; i < response_ARR.length(); i++) {
                try {
                    JSONObject jo = response_ARR.getJSONObject(i);
                    order_item sale_it = new order_item();
                    sale_it.sale_id = sale.order_no;
                    sale_it.product_name = jo.getString("MenuName");
                    sale_it.price = jo.getString("MenuPriceStr");
                    sale_it.quantity = jo.getString("MenuQuantity");
                    sale_it.total_amount = jo.getString("MenuAmountStr");
                    sale.sales_items.add(sale_it);

                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }


            }

        }catch (Exception ex){return null;}
return sale;
    }
}