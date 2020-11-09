package sparta.realm.spartaadapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import sparta.realm.R;
import sparta.realm.spartamodels.fingerprint_to_capture;


/**
 * Created by Thompsons on 08-Mar-17.
 */

public class fp_to_capture_adapter extends BaseAdapter {
    List<fingerprint_to_capture> objs;
    Activity act;
    public fp_to_capture_adapter(Activity act, List<fingerprint_to_capture> objs)
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
        fingerprint_to_capture obj= objs.get(position);
        convertView= LayoutInflater.from(act).inflate(R.layout.item_fingerprint,null,false);
       TextView name=(TextView)convertView.findViewById(R.id.name);

        CircleImageView data_icon = (CircleImageView) convertView.findViewById(R.id.title_icon1);
if(obj.skipped)
{
    convertView.setBackground(act.getDrawable(R.drawable.accent_button_inactive));
}else{
    convertView.setBackground(act.getDrawable(R.drawable.accent_button));

}
        data_icon.setImageDrawable(act.getDrawable(obj.drawable_resource));
convertView.findViewById(R.id.fp_check_pic).setVisibility(obj.data==null||obj.data.length()<10?View.GONE:View.VISIBLE);
convertView.findViewById(R.id.loading_bar).setVisibility(obj.capturing?View.VISIBLE:View.GONE);

          name.setText(obj.name);



        return convertView;
    }
}
