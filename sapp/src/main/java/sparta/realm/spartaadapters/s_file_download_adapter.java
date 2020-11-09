package sparta.realm.spartaadapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sparta.realm.R;
import sparta.realm.spartamodels.file_download;
import sparta.realm.spartaviews.sparta_progress_bar;


/**
 * Created by Thompsons on 08-Mar-17.
 */

public class s_file_download_adapter extends BaseAdapter {
    ArrayList<file_download> s_files;
    Activity act;
    public s_file_download_adapter(Activity act, ArrayList<file_download> s_files)
    {
        this.act=act;
        this.s_files=s_files;

    }
    @Override
    public int getCount() {
        return s_files.size();
    }

    @Override
    public Object getItem(int position) {
        return s_files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView=null;
        final file_download fiel= s_files.get(position);
        convertView= LayoutInflater.from(act).inflate(R.layout.item_report_download,null,false);

        TextView name=(TextView)convertView.findViewById(R.id.file_name_val);
        TextView date=(TextView)convertView.findViewById(R.id.date_val);
       final sparta_progress_bar download_progress=(sparta_progress_bar)convertView.findViewById(R.id.download_progress);
       final TextView percent_label=(TextView)convertView.findViewById(R.id.percent_label);
       final ImageView download_icon=(ImageView)convertView.findViewById(R.id.download_icon);
      // TextView percent=(TextView)convertView.findViewById(R.id.percent_label);
        final RelativeLayout progress_lay =(RelativeLayout)convertView.findViewById(R.id.progress_lay);


        name.setText(fiel.file_name);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date ttm=null;
        try {
           ttm=formatter.parse(fiel.generation_date);
            formatter = new SimpleDateFormat("dd-MM-yyyy");
            date.setText(formatter.format(ttm));

        } catch (ParseException e) {
            e.printStackTrace();
            date.setText(fiel.generation_date);
        }
fiel.setOnStatusChangeListener(new file_download.s_file_interface() {
    @Override
    public void on_download_progress(int progress) {
        download_progress.setProgress(progress);
        percent_label.setText(progress+"%");
        progress_lay.setVisibility(View.VISIBLE);
    }

    @Override
    public void on_download_error(String error) {

    }

    @Override
    public void on_download_begun() {
        progress_lay.setVisibility(View.VISIBLE);

    }

    @Override
    public void on_re_download_begun() {

    }

    @Override
    public void on_download_complete() {
        progress_lay.setVisibility(View.GONE);
        download_icon.setVisibility(View.GONE);
    }
});
        download_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fiel.begin_download();
            }
        });


        return convertView;
    }
}
