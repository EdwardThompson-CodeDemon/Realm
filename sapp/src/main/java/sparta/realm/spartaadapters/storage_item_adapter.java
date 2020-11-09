package sparta.realm.spartaadapters;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import sparta.realm.R;
import sparta.realm.spartautils.StorageUtils;


/**
 * Created by Thompsons on 08-Mar-17.
 */

public class storage_item_adapter extends BaseAdapter {
    List<StorageUtils.StorageInfo> StorageInfos;
    Activity act;
    public storage_item_adapter(Activity act, List<StorageUtils.StorageInfo> StorageInfos)
    {
        this.act=act;
        this.StorageInfos=StorageInfos;

    }
    @Override
    public int getCount() {
        return StorageInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return StorageInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView=null;
        StorageUtils.StorageInfo storinf= StorageInfos.get(position);
        convertView= LayoutInflater.from(act).inflate(R.layout.storage_item,null,false);
        TextView storage_name_label=(TextView)convertView.findViewById(R.id.storage_name_label);
        TextView used_space_label=(TextView)convertView.findViewById(R.id.used_space_label);
        TextView total_space_label=(TextView)convertView.findViewById(R.id.total_space_label);
        ProgressBar per_use=(ProgressBar)convertView.findViewById(R.id.prog);

        storage_name_label.setText(storinf.getDisplayName());
        

try{ total_space_label.setText("Total : "+storinf.getTotalMemorySize());}catch (Exception ex){}
        try{used_space_label.setText("Available : "+storinf.getAvailableMemorySize());}catch (Exception ex){}
try{
    String tot=storinf.getTotalMemorySize().replace(",","").replace("MB","").replace("GB","").trim();
        String avail=storinf.getAvailableMemorySize().replace(",","").replace("MB","").replace("GB","").trim();
        double total_gb= Double.parseDouble(tot);
        double available_gb= Double.parseDouble(avail);
        double used_gb=total_gb-available_gb;
        int gb_per=(int)((used_gb/total_gb)*100.0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            per_use.setProgress(gb_per,true);
        }else{
            per_use.setProgress(gb_per);

        }
        if(gb_per>70)
        {
            per_use.setProgressDrawable(act.getResources().getDrawable(R.drawable.progress_red));
        }
}catch (Exception ex){}
        return convertView;
    }
}
