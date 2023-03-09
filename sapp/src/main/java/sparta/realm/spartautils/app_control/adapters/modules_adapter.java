package sparta.realm.spartautils.app_control.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import sparta.realm.R;
import sparta.realm.spartautils.app_control.models.module;


/**
 * Created by Thompsons on 08-Mar-17.
 */

public class modules_adapter extends BaseAdapter {
    ArrayList<module> modules;
    Activity act;
    public modules_adapter(Activity act, ArrayList<module> modules)
    {
        this.act=act;
        this.modules=modules;

    }
    @Override
    public int getCount() {
        return modules.size();
    }

    @Override
    public Object getItem(int position) {
        return modules.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView=null;
        module fiel= modules.get(position);
        convertView= LayoutInflater.from(act).inflate(R.layout.item_module,null,false);
        TextView module_name=(TextView)convertView.findViewById(R.id.module_name);
        ImageView module_icon=(ImageView)convertView.findViewById(R.id.module_icon);
//  TextView status=(TextView)convertView.findViewById(R.id.status);

        module_name.setText(fiel.name);
        module_icon.setImageDrawable(fiel.icon);
//        module_icon.setColorFilter(Color.GRAY);




        return convertView;
    }
}
