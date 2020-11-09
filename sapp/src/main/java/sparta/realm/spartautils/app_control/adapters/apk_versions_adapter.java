package sparta.realm.spartautils.app_control.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sparta.realm.R;
import sparta.realm.spartautils.app_control.models.apk_version;


/**
 * Created by Thompsons on 08-Mar-17.
 */

public class apk_versions_adapter extends BaseAdapter {
    ArrayList<apk_version> versions;
    Context act;
    public apk_versions_adapter(Context act, ArrayList<apk_version> versions)
    {
        this.act=act;
        this.versions=versions;

    }
    @Override
    public int getCount() {
        return versions.size();
    }

    @Override
    public Object getItem(int position) {
        return versions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView=null;
        apk_version fiel= versions.get(position);
        convertView= LayoutInflater.from(act).inflate(R.layout.version_view,null,false);
        TextView name=(TextView)convertView.findViewById(R.id.index_field);
        TextView id=(TextView)convertView.findViewById(R.id.idv);
        name.setText(act.getString(R.string.version_code_label)+fiel.code+"    "+act.getString(R.string.name_label)+fiel.name);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date ttm=null;
        try {
           ttm=formatter.parse(fiel.date);
            formatter = new SimpleDateFormat("dd-MM-yyyy");
            id.setText(act.getString(R.string.release_date_label)+formatter.format(ttm));

        } catch (ParseException e) {
            e.printStackTrace();
            id.setText(act.getString(R.string.release_date_label)+fiel.date);
        }
 if(fiel.status.equalsIgnoreCase("1"))
{
   // id.setText("Latest version  :"+fiel.date.split(" ")[0]);
id.setTextColor(Color.GREEN);
}else {
    convertView.setAlpha(0.5f);
}

        return convertView;
    }
}
