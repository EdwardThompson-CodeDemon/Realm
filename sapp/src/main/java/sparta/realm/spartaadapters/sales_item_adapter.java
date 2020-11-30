package sparta.realm.spartaadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


import sparta.realm.R;
import sparta.realm.spartamodels.order_item;

public class sales_item_adapter extends BaseAdapter {

    Context cntxt;
    ArrayList<order_item> sales_items;

    public sales_item_adapter(Context cntx, ArrayList<order_item> sales_items)
    {
        this.cntxt=cntx;
        this.sales_items=sales_items;
    }
    @Override
    public int getCount() {
        return sales_items.size();
    }

    @Override
    public Object getItem(int position) {
        return sales_items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        convertView= LayoutInflater.from(cntxt).inflate(R.layout.item_order_detail,null);
        order_item item=sales_items.get(position);
        ((TextView) convertView.findViewById(R.id.product_name)).setText(item.product_name);
        ((TextView) convertView.findViewById(R.id.item_price)).setText("Price :"+item.price);
        ((TextView) convertView.findViewById(R.id.item_quantity)).setText("Quantity :"+item.quantity);
        ((TextView) convertView.findViewById(R.id.total_amount)).setText("Total :"+item.total_amount);
        return convertView;
    }
}
