package sparta.realm.spartaadapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sparta.realm.R;
import sparta.realm.spartamodels.dyna_data_obj;


/**
 * Created by Thompsons on 08-Mar-17.
 */

public class dyna_data_adapter extends BaseAdapter {
    List<dyna_data_obj> objs;
    Activity act;
    public dyna_data_adapter(Activity act, List<dyna_data_obj> objs)
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
        dyna_data_obj obj= objs.get(position);
        convertView=LayoutInflater.from(act).inflate(R.layout.dyna_obj_custom,null,false);
        TextView name=(TextView)convertView.findViewById(R.id.val);

        TextView code=(TextView)convertView.findViewById(R.id.index_field);
       // ImageView data_icon = (ImageView) convertView.findViewById(R.id.imageView1);

if(obj.data_type.value!=null) {
    code.setVisibility(obj.data_type.value.equalsIgnoreCase(dyna_data_obj.operatio_data_type.DelegatesList.ordinal()+"") ? View.VISIBLE : View.GONE);
}
name.setText(obj.data.value);
code.setText(obj.code.value);
     //   id.setText(obj.data_type.value.equalsIgnoreCase("mc")?(position+1)+".":obj.code.value);



        return convertView;
    }
}
