package sparta.realm.spartaadapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sparta.realm.R;
import sparta.realm.spartamodels.dyna_data;
import sparta.realm.spartamodels.dyna_data_obj;


/**
 * Created by Thompsons on 08-Mar-17.
 */

public class dyna_data_adapter_ extends BaseAdapter {
    List<dyna_data> objs;
    Activity act;
    public dyna_data_adapter_(Activity act, List<dyna_data> objs)
    {
        this.act=act;
        this.objs=objs;

    }
    @Override
    public int getCount() {
        return objs.size();
    }

    @Override
    public Object getItem(int position) {
        return objs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView=null;
        dyna_data obj= objs.get(position);
        convertView=LayoutInflater.from(act).inflate(R.layout.dyna_obj_custom,null,false);
        TextView name=(TextView)convertView.findViewById(R.id.val);

        TextView code=(TextView)convertView.findViewById(R.id.index_field);


        if(obj.data_type!=null) {
            code.setVisibility(obj.data_type.equalsIgnoreCase(dyna_data_obj.operatio_data_type.DelegatesList.ordinal()+"") ? View.VISIBLE : View.GONE);
        }
        name.setText(obj.data);
        code.setText(obj.code);

        return convertView;
    }
}
