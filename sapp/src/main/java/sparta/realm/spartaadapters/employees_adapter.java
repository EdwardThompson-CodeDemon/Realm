package sparta.realm.spartaadapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import sparta.realm.R;
import sparta.realm.spartamodels.member;
import sparta.realm.spartautils.svars;


/**
 * Created by Thompsons on 08-Mar-17.
 */

public class employees_adapter extends BaseAdapter {
    List<member> objs;
    Activity act;
    public employees_adapter(Activity act, List<member> objs)
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
//if(convertView!=null){return convertView;}
        member obj= objs.get(position);
        convertView= LayoutInflater.from(act).inflate(R.layout.item_member,null,false);
       // TextView name=(TextView)convertView.findViewById(R.id.employee_name);

        //TextView id=(TextView)convertView.findViewById(R.id.idno);
        ((TextView)convertView.findViewById(R.id.employee_name)).setText(obj.surname.value);
        ((TextView)convertView.findViewById(R.id.idno)).setText(obj.idno.value);
        if(svars.current_device()==svars.DEVICE.BIO_MINI.ordinal()) {
            /*CircleImageView data_icon = (CircleImageView) convertView.findViewById(R.id.title_icon1);

data_icon.setImageDrawable(null);*/
              // data_icon.setVisibility(View.GONE);

        }
        //  name.setText(obj.full_name);
  //      id.setText(obj.idno);



        return convertView;
    }
}
