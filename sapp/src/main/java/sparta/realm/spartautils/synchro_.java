package sparta.realm.spartautils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.jem.rubberpicker.RubberSeekBar;

import java.util.ArrayList;

import sparta.realm.R;
import sparta.realm.Services.SynchronizationManager;




public class synchro_ {
    ObjectAnimator sync_spinn_annimator=null;
    View sync_view;
    boolean sync_view_added =false;
    boolean sync_view_droped =false;
    ArrayList<View> launch_buttons;
    RelativeLayout main;
    Activity act;
    SynchronizationManager sb=null;
    TextView last_sync_time_txt;
    ImageView sync_icon;
   public synchro_(Activity act, RelativeLayout main, ArrayList<View> launch_buttons, TextView last_sync_time_txt, ImageView special_synchro_view)
    {
     this.act=act;
     this.main=main;
     this.last_sync_time_txt=last_sync_time_txt==null?new TextView(act):last_sync_time_txt;
     this.launch_buttons=launch_buttons;
     this.sync_icon=special_synchro_view;
     setyp_synchro();
     for(View v:launch_buttons)
     {
         v.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 animate_synch_view();
             }
         });

     }
      /*  special_synchro_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animate_synch_view();
            }
        });*/
    }
public void triger_sync()
{
    animate_synch_view();
}
    void animate_synch_view()
    {
        if(!sync_view_added)
        {
            main.addView(sync_view);
            sync_view_added =true;
            ObjectAnimator obj = ObjectAnimator.ofFloat(sync_view, "translationY", 100, 0f);
            obj.setDuration(1000);
            obj.start();
        }
        if(!sync_view_droped) {
            ObjectAnimator obj = ObjectAnimator.ofFloat(sync_view, "translationY", -180, 0f);
            obj.setDuration(1000);
            obj.start();
            ObjectAnimator obj2 = ObjectAnimator.ofFloat(sync_view, "alpha", 0, 1f);
            obj2.setDuration(1500);
            obj2.start();
            ObjectAnimator obj3 = ObjectAnimator.ofFloat(sync_icon, "rotation", 180f, 0f);
            obj3.setDuration(950);
            obj3.start();

            sync_view_droped =true;
            sync_icon.setAlpha(0.5f);
        }else {
            ObjectAnimator obj = ObjectAnimator.ofFloat(sync_view, "translationY", 0f, -180f);
            obj.setDuration(1000);
            obj.start();
            ObjectAnimator obj2 = ObjectAnimator.ofFloat(sync_view, "alpha", 1, 0f);
            obj2.setDuration(950);
            obj2.start();
            ObjectAnimator obj3 = ObjectAnimator.ofFloat(sync_icon, "rotation", 0f, 180f);
            obj3.setDuration(950);
            obj3.start();
            sync_view_droped =false;
            sync_icon.setAlpha(1f);

        }
    }
    Button sync_now;
    TextView title,class_name,absentees,percent_title_label,sync_status_txt,sync_time,min_percent_label;
    ArcProgress ARC1;
    ProgressBar sync_percent;
    CheckBox autoback_sync,global_data_sync;
    ImageView sync_config;

    RelativeLayout sync_lay;
    void setyp_synchro()
    {

        sync_view = LayoutInflater.from(act).inflate(R.layout.item_sync_view, null, false);
        sync_lay=(RelativeLayout)sync_view.findViewById(R.id.sync_lay);
        sync_now=(Button)sync_view.findViewById(R.id.sync_now);
        sync_config=(ImageView) sync_view.findViewById(R.id.sync_config);
        ARC1=(ArcProgress)sync_view.findViewById(R.id.arc_progress);

        percent_title_label=(TextView)sync_view.findViewById(R.id.percent_title_label);
        min_percent_label=(TextView)sync_view.findViewById(R.id.percent_label);
        sync_status_txt=(TextView)sync_view.findViewById(R.id.sync_status_txt);
        sync_time=(TextView)sync_view.findViewById(R.id.sync_status_val);

        sync_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_sync_config_dialog();
            }
        });
      sync_icon =sync_icon==null?(ImageView)sync_view.findViewById(R.id.sync_status_icon):sync_icon;

        sync_percent=(ProgressBar)sync_view.findViewById(R.id.sync_percent);
        autoback_sync=(CheckBox)sync_view.findViewById(R.id.autoback_sync_check);
        global_data_sync=(CheckBox)sync_view.findViewById(R.id.global_data_sync_check);
        autoback_sync.setChecked(svars.background_sync(act));
        global_data_sync.setChecked(svars.global_data_sync(act));

        autoback_sync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                svars.set_background_sync(act,b);
            }
        });

        global_data_sync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                svars.set_global_data_sync(act,b);
            }
        });

        sync_status_txt.setText("");
       // sync_time.setText("Sync time :"+svars.sync_time(act));
        sync_time.setText(((svars.sync_time(act)!=null&&svars.sync_time(act).split(" ")[0].equalsIgnoreCase(svars.gett_time().split(" ")[0]))?"Today "+svars.sync_time(act).split(" ")[1]:svars.sync_time(act)));


        sync_spinn_annimator = ObjectAnimator.ofFloat(sync_icon, "rotation", 0f,180f, 360f);
        sync_spinn_annimator.setDuration(400);
        sync_spinn_annimator.setRepeatCount(ValueAnimator.INFINITE);
        sync_spinn_annimator.start();

        sb = new SynchronizationManager(new SynchronizationManager.SynchronizationStatusHandler() {
            @Override
            public void on_status_code_changed(final int status) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        switch (status) {
                            case 0:
                                sync_now.setVisibility(View.GONE);

                                break;
                            case 1:

                                sync_now.setVisibility(View.GONE);
                                sync_lay.setBackground(act.getDrawable(R.drawable.status_back));

                                break;
                            case 2:

                                //   sync_now.setVisibility(View.GONE);
                                //  sync_lay.setBackground(getDrawable(R.drawable.status_back));

                                break;

                            case 3:
                                sync_now.setVisibility(View.VISIBLE);
                                sync_lay.setBackground(act.getDrawable(R.drawable.status_back));

                                if(sync_view_droped)
                                {
                                    animate_synch_view();
                                }

                                break;
                            case 4:
                                sync_now.setVisibility(View.VISIBLE);


                                break;

                            case 666:
                                //  sync_now.setVisibility(View.VISIBLE);
                                sync_lay.setBackground(act.getDrawable(R.drawable.status_back_error));

                                break;

                        }
//                        sync_time.setText(svars.sync_time(act));
//                        last_sync_time_txt.setText(act.getResources().getString(R.string.last_sync_time)+svars.sync_time(act));
                        sync_time.setText((svars.sync_time(act)!=null&&svars.sync_time(act).split(" ")[0].equalsIgnoreCase(svars.gett_time().split(" ")[0]))?"Today "+svars.sync_time(act).split(" ")[1]:svars.sync_time(act));
                        last_sync_time_txt.setText(act.getResources().getString(R.string.last_sync_time)+((svars.sync_time(act)!=null&&svars.sync_time(act).split(" ")[0].equalsIgnoreCase(svars.gett_time().split(" ")[0]))?"Today "+svars.sync_time(act).split(" ")[1]:svars.sync_time(act)));

                    }
                });
            }

            @Override
            public void on_status_changed(final String status) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {



                        if(status.toLowerCase().contains("failed")||status.toLowerCase().contains("fetching"))
                        {
                            sync_now.setVisibility(View.VISIBLE);
                            sync_spinn_annimator.cancel();

                        }else if(status.toLowerCase().contains("offline")) {

                            sync_now.setVisibility(View.VISIBLE);
                            sync_spinn_annimator.cancel();
                            sync_status_txt.setText(act.getString(R.string.status_prompt)+status);

                        }else{
                            sync_status_txt.setText("Status : "+status);

                        }



                        sync_time.setText((svars.sync_time(act)!=null&&svars.sync_time(act).split(" ")[0].equalsIgnoreCase(svars.gett_time().split(" ")[0]))?act.getString(R.string.today)+svars.sync_time(act).split(" ")[1]:svars.sync_time(act));
                        last_sync_time_txt.setText(act.getResources().getString(R.string.last_sync_time)+((svars.sync_time(act)!=null&&svars.sync_time(act).split(" ")[0].equalsIgnoreCase(svars.gett_time().split(" ")[0]))?act.getString(R.string.today)+svars.sync_time(act).split(" ")[1]:svars.sync_time(act)));

                    }
                });
            }

            @Override
            public void on_info_updated(final String status) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        percent_title_label.setText(status);
                    }
                }); }

            @Override
            public void on_main_percentage_changed(final int progress) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ARC1.setProgress(progress);


                    }
                });
                Runtime.getRuntime().gc();
            }

            @Override
            public void on_secondary_progress_changed(final int progress) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sync_percent.setProgress(progress);
                        min_percent_label.setText(progress+"%");
                    }
                });
            }
        });
        sb.InitialiseAutosync();
        sync_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb.sync_now();
            }
        });
    }
    String timesp(long timeSec)
    {
        ;// Json output
        int hours = (int) timeSec/ 3600;
        int temp = (int) timeSec- hours * 3600;
        int mins = temp / 60;
        temp = temp - mins * 60;
        int secs = temp;

        return hours>1?hours+ " Hrs ":hours>0?hours+ " Hr ":""+mins+" mins  ";//hh:mm:ss formatte
    }
    public void show_sync_config_dialog() {
        View aldv = LayoutInflater.from(act).inflate(R.layout.dialog_config_sync, null);
        final AlertDialog ald = new AlertDialog.Builder(act)
                .setView(aldv)
                .show();
        ald.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RubberSeekBar rsb=aldv.findViewById(R.id.rrp);
        ((TextView)aldv.findViewById(R.id.time_span)).setText(timesp(svars.sync_interval_mins(act)*60));
        rsb.setCurrentValue(svars.sync_interval_mins(act));

        rsb.setOnRubberSeekBarChangeListener(new RubberSeekBar.OnRubberSeekBarChangeListener() {
            @Override
            public void onProgressChanged(RubberSeekBar rubberSeekBar, int i, boolean b) {
                ((TextView)aldv.findViewById(R.id.time_span)).setText(timesp(i*60));
                svars.set_sync_interval_mins(act,i);
                sb.synchronizationTimer.cancel();
                sb.synchronizationTimer =null;
                sb.InitialiseAutosync();

            }

            @Override
            public void onStartTrackingTouch( RubberSeekBar rubberSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(RubberSeekBar rubberSeekBar) {

            }
        });

        (aldv.findViewById(R.id.restore_defaults)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rsb.setCurrentValue(svars.default_sync_interval_mins);
                svars.set_sync_interval_mins(act,svars.default_sync_interval_mins);
            }
        });
        (aldv.findViewById(R.id.dismiss)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ald.dismiss();
            }
        });

    }
}
