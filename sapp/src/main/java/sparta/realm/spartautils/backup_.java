package sparta.realm.spartautils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.os.EnvironmentCompat;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import sparta.realm.BuildConfig;
import sparta.realm.R;
import sparta.realm.spartaadapters.storage_item_adapter;


public class backup_ {

    View backup_view;
    boolean backup_view_added =false;
    boolean backup_view_droped =false;

    backup_progress_listener backup_i;

    RelativeLayout main;
    Activity act;
     ArrayList<View> launch_button;
    public backup_(Activity act, RelativeLayout main, ArrayList<View> launch_buttons)
    {
        this.act=act;
        this.main=main;
        this.launch_button=launch_button;
        setup_backup_view();
        for(View v:launch_buttons)
        {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    animate_backup_view();
                }
            });

        }

    }
    void animate_backup_view()
    {

        if(!backup_view_added)
        {
            main.addView(backup_view);
            backup_view_added =true;
            ObjectAnimator obj = ObjectAnimator.ofFloat(backup_view, "translationX", 100, 0f);
            obj.setDuration(1000);
            obj.start();
        }
        backup_view.bringToFront();
        if(!backup_view_droped) {
            ObjectAnimator obj = ObjectAnimator.ofFloat(backup_view, "translationX", 180, 0f);
            obj.setDuration(1000);
            obj.start();
            ObjectAnimator obj2 = ObjectAnimator.ofFloat(backup_view, "alpha", 0, 1f);
            obj2.setDuration(1500);
            obj2.start();



            backup_view_droped =true;


        }else {
            ObjectAnimator obj = ObjectAnimator.ofFloat(backup_view, "translationX", 0f, 180f);
            obj.setDuration(1000);
            obj.start();
            ObjectAnimator obj2 = ObjectAnimator.ofFloat(backup_view, "alpha", 1, 0f);
            obj2.setDuration(950);
            obj2.start();


            backup_view_droped =false;



        }
    }

    Button initialize_backup;
    TextView backup_view_title,backup_view_secondary_status,backup_view_secondary_percent_label,backup_view_last_backup_time,backup_log;
    ArcProgress backup_view_primary_backup_progress;
    ProgressBar backup_view_secondary_backup_progress;
    CheckBox backup_view_email_backup_check,backup_view_ftp_backup_check,backup_view_sd_backup_check,recrusive_backup_check;

    RelativeLayout backup_view_lay;

    void setup_backup_view() {

        backup_view = LayoutInflater.from(act).inflate(R.layout.backup_view, null, false);
        backup_view_lay = (RelativeLayout) backup_view.findViewById(R.id.backup_lay);

        initialize_backup = (Button) backup_view.findViewById(R.id.init_backup);

        backup_view_primary_backup_progress = (ArcProgress) backup_view.findViewById(R.id.arc_primary_progress);
        backup_view_secondary_backup_progress = (ProgressBar) backup_view.findViewById(R.id.secondary_backup_progress);

        backup_view_secondary_percent_label = (TextView) backup_view.findViewById(R.id.secondary_percent_val);
        backup_view_title = (TextView) backup_view.findViewById(R.id.primary_status_val);
        backup_view_secondary_status = (TextView) backup_view.findViewById(R.id.secondary_status_val);
        backup_view_last_backup_time = (TextView) backup_view.findViewById(R.id.last_backup_time_val);
        backup_log = (TextView) backup_view.findViewById(R.id.backup_log);
        backup_log.setMovementMethod(new ScrollingMovementMethod());

        backup_view_email_backup_check = (CheckBox) backup_view.findViewById(R.id.email_backup_check);
        backup_view_ftp_backup_check = (CheckBox) backup_view.findViewById(R.id.ftp_backup_check);
        backup_view_sd_backup_check = (CheckBox) backup_view.findViewById(R.id.sd_backup_check);
        recrusive_backup_check = (CheckBox) backup_view.findViewById(R.id.recrusive_backup);

        backup_view_email_backup_check.setChecked(svars.email_backup(act));
        backup_view_ftp_backup_check.setChecked(svars.ftp_backup(act));
        backup_view_sd_backup_check.setChecked(svars.sd_backup(act));
        recrusive_backup_check.setChecked(svars.recrusive_backup(act));

        backup_view_sd_backup_check.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                display_storage_diagnostics();
                return false;
            }
        });
        recrusive_backup_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                svars.set_recrusive_backupp(act,b);
            }
        });

        backup_view_email_backup_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                svars.set_email_backup(act,b);
            }
        });


        backup_view_ftp_backup_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                svars.set_ftp_backup(act,b);
            }
        });


        backup_view_sd_backup_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                svars.set_sd_backup(act,b);
            }
        });


        backup_i= new backup_progress_listener() {
            @Override
            public void on_primary_status_changed(final String status) {
                Log.e("Primary status =>",""+status);
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        backup_view_title.setText(status);
                        backup_log.setText(status+"\n"+backup_log.getText().toString());
                        backup_log.invalidate();
                        backup_view_last_backup_time.setText(svars.backup_time(act));
                        backup_view_last_backup_time.invalidate();
                    }
                });
            }

            @Override
            public void on_secondary_status_changed(final String status) {
                Log.e("Secondary status =>",""+status);
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        backup_view_secondary_status.setText(status);
                        backup_log.setText(status+"\n"+backup_log.getText().toString());

                        backup_view_secondary_status.invalidate();
                        backup_log.invalidate();
                    }
                });
            }

            @Override
            public void on_primary_progress_changed(final int progress) {
                Log.e("Primary progress =>",""+progress);
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        backup_view_primary_backup_progress.setProgress(progress);
                    }
                });
            }

            @Override
            public void on_secondary_progress_changed(final int progress) {
                Log.e("Secondary progress =>",""+progress);
                //   backup_view_secondary_backup_progress.invalidate();
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        backup_view_secondary_backup_progress.setProgress(progress);
                        backup_view_secondary_percent_label.setText(progress+"%");
                        //   Log.e("Secondary progress M =>",""+progress);
                    }
                });
            }

            @Override
            public void on_error_encountered(final String status) {
                Log.e("Error =>",""+status);
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String next = "<font color='#EE0000'>"+"<br/>"+backup_log.getText().toString()+"</font>";
                        // backup_log.setText(Html.fromHtml(status + next));
                        backup_log.setText("Error : "+status+"\n"+backup_log.getText().toString());
                        backup_log.invalidate();
                    }
                });


            }

            @Override
            public void on_backup_complete() {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initialize_backup.setEnabled(true);
                        backup_view_last_backup_time.setText(svars.backup_time(act));
                    }
                });
            }
        };
        initialize_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{backup_thread.interrupt();}catch (Exception ex){}
                backup_thread=null;
                backup_view_title.setText(act.getString(R.string.backup_title));
                backup_view_secondary_percent_label.setText("0%");
                backup_view_secondary_status.setText("");
                backup_view_secondary_backup_progress.setProgress(0);
                backup_view_primary_backup_progress.setProgress(0);
                backup_log.setText("");
                initialize_backup.setEnabled(false);
                backup_thread=new Thread(new Runnable() {
                    @Override
                    public void run() {


                        backup_app_data(act,backup_i);
                    }
                });

                backup_thread.start();

            }
        });
    }
    Thread backup_thread;
    void display_storage_diagnostics()
    {
        final View aldv= LayoutInflater.from(act).inflate(R.layout.dialog_storage_report,null);
        final GridView storage_list=(GridView)aldv.findViewById(R.id.version_list);

        final TextView log_file_size_label=(TextView)aldv.findViewById(R.id.log_file_size_label);
        final TextView database_file_size_label=(TextView)aldv.findViewById(R.id.database_file_size_label);
        final TextView backup_folder_size_label=(TextView)aldv.findViewById(R.id.backup_label_size_label);

        log_file_size_label.setText(StorageUtils.getfile_size(new File(act.getExternalFilesDir(null).getAbsolutePath() + "/logs/"+svars.Log_file_name)));
        database_file_size_label.setText(StorageUtils.getfile_size(new File(act.getExternalFilesDir(null).getAbsolutePath() + "/" +svars.DB_NAME)));
        backup_folder_size_label.setText(StorageUtils.getfolder_size(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TimeandAttendance/backups")));

        final Button dismiss=(Button) aldv.findViewById(R.id.update);



        storage_list.setAdapter(new storage_item_adapter(act, StorageUtils.getStorageList()));
        final AlertDialog ald = new AlertDialog.Builder(act)
                .setView(aldv)
                .show();
    }
    public  void  triger_backup()
    {
        animate_backup_view();
    }
    public static void backup_db(Activity act){



        File s_file = new File(act.getExternalFilesDir(null).getAbsolutePath());
        s_file.mkdirs();
        File t_file = new File(act.getExternalFilesDir(null).getAbsolutePath() + "/backups");
        t_file.mkdirs();

        try {


            File sourceLocation= new File(s_file,svars.DB_NAME);
            File targetLocation= new File(t_file, System.currentTimeMillis()+".spartadb");

            InputStream in = null;

            in = new FileInputStream(sourceLocation);

            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface backup_progress_listener{
        void on_primary_status_changed(String status);
        void on_secondary_status_changed(String status);
        void on_primary_progress_changed(int progress);
        void on_secondary_progress_changed(int progress);
        void on_error_encountered(String status);
        void on_backup_complete();



    }

    public static void backup_app_data(Activity act, final backup_progress_listener listener)
    {

        String backup_transaction_code="SPA_IDC"+ System.currentTimeMillis()+"$"+svars.device_code(act).replace("|","$")+"BU_RTA";
        listener.on_primary_status_changed(act.getString(R.string.verifying_files_to_backup));
        //   listener.on_secondary_status_changed("...");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //  File fp_images_folder = new File(Environment.getExternalStorageDirectory().toString() + "/realm", "/.UNCOMPRESSED_FP_IMGS");
        File fp_images_folder = new File(svars.current_app_config(act).file_path_employee_data);
        fp_images_folder.mkdirs();
        //   listener.on_secondary_status_changed(act.getString(R.string.image_files_ok));
        listener.on_secondary_status_changed(act.getString(R.string.employee_data_files_ok));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File db_folder = new File(act.getExternalFilesDir(null).getAbsolutePath());
        db_folder.mkdirs();
        listener.on_secondary_status_changed(act.getString(R.string.db_ok));
        listener.on_primary_progress_changed(10);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File db_backup_folder = new File(svars.current_app_config(act).file_path_db_backup);
        db_backup_folder.mkdirs();
        listener.on_secondary_status_changed(act.getString(R.string.db_backup_ok));
        listener.on_primary_progress_changed(20);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        File log_folder = new File(act.getExternalFilesDir(null).getAbsolutePath() + "/logs");
        log_folder.mkdirs();
        listener.on_secondary_status_changed(act.getString(R.string.log_file_ok));
        listener.on_primary_progress_changed(30);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        File log_files_backup_folder = new File(svars.current_app_config(act).file_path_log_backup);
        log_files_backup_folder.mkdirs();
        listener.on_secondary_status_changed(act.getString(R.string.log_backup_ok));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        File global_backup_folder = new File(svars.current_app_config(act).file_path_general_backup);
        global_backup_folder.mkdirs();
        listener.on_secondary_status_changed(act.getString(R.string.backup_location_ok));
        listener.on_primary_progress_changed(40);

        listener.on_primary_status_changed(act.getString(R.string.staging_current_database));
        listener.on_secondary_status_changed(act.getString(R.string.staging_current_database));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            File sourceLocation= new File(db_folder,svars.DB_NAME);
            File targetLocation= new File(db_backup_folder,"SPA"+ System.currentTimeMillis()+"DB_RTA");

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);


            byte[] buf = new byte[1024];
            int len;
            int per_counter = 0;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                per_counter += len;
                // final double finalPercent = Math.round(((double) per_counter / (double) c.getContentLength()) * 100);
                final double finalPercent = sparta_loc_util.round(((double) per_counter / (double) sourceLocation.length()),2)* 100;
                listener.on_secondary_progress_changed((int)finalPercent);
                // listener.on_secondary_progress_changed(Integer.parseInt((((""+finalPercent).split(".")[0])+((""+finalPercent).split(".")[1].toCharArray()[0])+((""+finalPercent).split(".")[1].toCharArray()[1]))));
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            listener.on_error_encountered(e.getMessage());
        }
        listener.on_primary_status_changed(act.getString(R.string.staging_log_files));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            File sourceLocation= new File(log_folder,svars.Log_file_name);
            File targetLocation= new File(log_files_backup_folder,"SPA"+ System.currentTimeMillis()+"LG_RTA");

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);


            byte[] buf = new byte[1024];
            int len;
            int per_counter = 0;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                per_counter += len;
                // final double finalPercent = Math.round(((double) per_counter / (double) c.getContentLength()) * 100);
                final double finalPercent = sparta_loc_util.round(((double) per_counter / (double) sourceLocation.length()),2)* 100;


                listener.on_secondary_progress_changed((int)finalPercent);



                // listener.on_secondary_progress_changed(Integer.parseInt((((""+finalPercent).split(".")[0])+((""+finalPercent).split(".")[1].toCharArray()[0])+((""+finalPercent).split(".")[1].toCharArray()[1]))));
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            listener.on_error_encountered(e.getMessage());
        }
        listener.on_primary_progress_changed(50);

        if(Thread.currentThread().isInterrupted())
        {
            return;
        }
        listener.on_primary_status_changed(act.getString(R.string.prepairing_files_to_archive));

        ArrayList<String> files_tobackup=new ArrayList<>();
        File[] dump_files = fp_images_folder.listFiles();
        if(dump_files!=null) {
            for (int i = 0; i < dump_files.length; i++) {
                files_tobackup.add(dump_files[i].getAbsolutePath());
            }
        }

        if(svars.recrusive_backup(act)){

            dump_files = db_backup_folder.listFiles();
            if(dump_files!=null) {
                for (int i = 0; i < dump_files.length; i++) {
                    files_tobackup.add(dump_files[i].getAbsolutePath());
                }
            }

        }else{
            files_tobackup.add(new File(db_folder,svars.DB_NAME).getAbsolutePath());

        }





        dump_files = log_files_backup_folder.listFiles();
        if(dump_files!=null) {
            for (int i = 0; i < dump_files.length; i++) {
                files_tobackup.add(dump_files[i].getAbsolutePath());
            }

        }
        listener.on_primary_status_changed(act.getString(R.string.creating_archive));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        zip_array(act,files_tobackup,global_backup_folder.getAbsolutePath()+"/"+backup_transaction_code+".zip",listener);
        listener.on_primary_status_changed(act.getString(R.string.archive_creation_complete));
        listener.on_primary_progress_changed(60);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        listener.on_primary_status_changed(act.getString(R.string.initializing_backup));

        if(svars.email_backup(act))
        {
            listener.on_secondary_status_changed(act.getString(R.string.email_backup_begun));

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            send_backup_mail(global_backup_folder.getAbsolutePath()+"/"+backup_transaction_code+".zip",act,listener);

        }
        if(svars.ftp_backup(act))
        {
            listener.on_secondary_status_changed(act.getString(R.string.sftp_backup_begun));

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            send_backup_ftp(global_backup_folder.getAbsolutePath()+"/"+backup_transaction_code+".zip",act,listener);
        }
        if(svars.sd_backup(act))
        {
            listener.on_secondary_status_changed(act.getString(R.string.sd_card_backup_begun));

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
// String dstPath = Environment.getExternalStorageDirectory() + File.separator + ".realm_BACKUP" + File.separator;
// String dstPath = Environment.get()+ File.separator + ".realm_BACKUP" + File.separator;
//String dstPath =getSdCardPaths(act,false).get(0)+ File.separator + "realm_BACKUP";
            // String dstPath ="storage/emulated/1"+ File.separator + "realm_BACKUP" + File.separator;

            try {
                String dstPath =act.getExternalFilesDirs(null)[1]+ File.separator + "realm_BACKUP";
                File sourceLocation= new File(global_backup_folder,backup_transaction_code+".zip");
                File targetLocation= new File(dstPath);

                targetLocation.mkdirs();


                if(!targetLocation.exists())
                {
                    if(Thread.currentThread().isInterrupted())
                    {
                        return;
                    }  listener.on_error_encountered(act.getString(R.string.sd_card_not_available));
                    listener.on_secondary_status_changed(act.getString(R.string.sd_card_not_available));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    listener.on_secondary_status_changed(act.getString(R.string.sd_card_backup_failed));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    listener.on_secondary_status_changed(act.getString(R.string.sd_card_backup_terminated));
                    return;
                }
                targetLocation= new File(targetLocation,backup_transaction_code+".zip");

                InputStream in = new FileInputStream(sourceLocation);
                OutputStream out = new FileOutputStream(targetLocation);


                byte[] buf = new byte[1024];
                int len;
                int per_counter = 0;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                    per_counter += len;
                    if(Thread.currentThread().isInterrupted())
                    {
                        return;
                    }
                    // final double finalPercent = Math.round(((double) per_counter / (double) c.getContentLength()) * 100);
                    final double finalPercent = sparta_loc_util.round(((double) per_counter / (double) sourceLocation.length()),2)* 100;


                    listener.on_secondary_progress_changed((int)finalPercent);



                    // listener.on_secondary_progress_changed(Integer.parseInt((((""+finalPercent).split(".")[0])+((""+finalPercent).split(".")[1].toCharArray()[0])+((""+finalPercent).split(".")[1].toCharArray()[1]))));
                }
                in.close();
                out.close();

                try {

                    File oldFolder = new File(dstPath,backup_transaction_code+".zip");
                    File newFolder = new File(dstPath, Calendar.getInstance().getTime().toString().split("G")[0].trim().replace(" ","_").replace(":","-")+".zip");
                    boolean success = oldFolder.renameTo(newFolder);
                    Log.e("Renaming backup =>",""+success);

                }catch (Exception ex){}

                listener.on_secondary_status_changed(act.getString(R.string.sd_card_backup_complete));
            } catch (Exception e) {
                e.printStackTrace();
                listener.on_error_encountered(e.getMessage());
                listener.on_secondary_status_changed(act.getString(R.string.sd_card_backup_failed));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ez) {
                    ez.printStackTrace();
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        listener.on_primary_progress_changed(100);
        listener.on_secondary_progress_changed(100);








        listener.on_primary_status_changed(act.getString(R.string.backup_complete));
        listener.on_secondary_status_changed(act.getString(R.string.backup_complete));
        svars.set_backup_time(act, Calendar.getInstance().getTime().toString().split("G")[0].trim());
        listener.on_backup_complete();
       /* File ff=new File(global_backup_folder.getAbsolutePath()+"/"+backup_transaction_code+".zip");
        ff.delete();*/
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    static void send_backup_mail(final String file_path, final Activity act, backup_progress_listener listener) {

        if(Thread.currentThread().isInterrupted())
        {
            return;
        }

        sparta_mail_probe smp = new sparta_mail_probe("Time and Attendance App data Backup", "Device =" + svars.device_code(act) + "\nUsername =" + svars.user_name(act) + "\nUser id=" + svars.user_id(act) + "\nM link=" + svars.current_app_config(act).ACCOUNT + "\nApp version " + svars.current_version(act));

        try {
            File file = new File(file_path);
            long fileSizeInBytes = file.length();
            long fileSizeInKB = fileSizeInBytes / 1024;
            long fileSizeInMB = fileSizeInKB / 1024;

            smp.send_attachement(file_path);


        } catch (Exception ex) {


        }
    }

    static void send_backup_ftp(String path, Activity act, backup_progress_listener listener) {
        if(Thread.currentThread().isInterrupted())
        {
            return;
        }
        Log.e("SSSSSSSSS", " UPLOADING TO SFTP NOW"+path);

        File uploadFilePath;
        Session session;
        Channel channel = null;
        ChannelSftp sftp = null;
        uploadFilePath = new File(path);
        //uploadFilePath=new File(Environment.getExternalStorageDirectory()+"/Android/data/sparta.farmercontractor/files/Output/"+filename);
        byte[] bufr = new byte[(int) uploadFilePath.length()];
        FileInputStream fis = null;
        Log.e("SSSSSSSSS", " UPLOADING TO SFTP NOW 2");
        try {
            fis = new FileInputStream(uploadFilePath);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            Log.e("LLLLLLLLLL", "dddddddddddddddd file not found");
            listener.on_error_encountered(act.getString(R.string.missing_file)+uploadFilePath.getName());
            e1.printStackTrace();

        }
        try {
            fis.read(bufr);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            listener.on_error_encountered(act.getString(R.string.missing_file)+uploadFilePath.getName());
        }


        JSch ssh = new JSch();
        int reconnection_count = 0;
        int max_reconnection_count = 10;
        while (true)
        {

            try {

//            session = ssh.getSession("Contractor", "www.cs4africa.com", 1989);
//
//
//          session.setConfig("StrictHostKeyChecking", "no");
//           session.setPassword("@contractor");


                session = ssh.getSession("Togo", "www.cs4africa.com", 1989);
                System.out.println("JSch JSch JSch Session created.");
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword("TOGO");


                session.connect();
                listener.on_secondary_status_changed(act.getString(R.string.connected_to_sftp_server));


                channel = session.openChannel("sftp");
                channel.connect();
                sftp = (ChannelSftp) channel;

                try {
                    sftp.mkdir(svars.ftp_folder(act));
                } catch (Exception ex) {
                }
                try {
                    sftp.cd(svars.ftp_folder(act));
                } catch (Exception ee) {
                }

                break;
            } catch(Exception e){
                Log.e("Upload error =>", "" + e.getMessage());
                listener.on_error_encountered(act.getString(R.string.connection_failed));
                if (reconnection_count < max_reconnection_count) {
                    reconnection_count++;
                    listener.on_secondary_status_changed(act.getString(R.string.attempting_sftp_reconnection));

                }else{
                    return;
                }

            }
        }
        listener.on_secondary_status_changed(act.getString(R.string.uploading_backup));

        ByteArrayInputStream in = new ByteArrayInputStream(bufr);
        try {
            sftp.put(in, uploadFilePath.getName(), null);
            // listener.on_secondary_status_changed("Uploading backup");
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            listener.on_error_encountered(act.getString(R.string.sftp_upload_failed)+uploadFilePath.getName());
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            listener.on_error_encountered("SFTP server disconnection failed :"+uploadFilePath.getName());

            e.printStackTrace();
        }

        if (sftp.getExitStatus() == -1) {
            System.out.println("file uploaded");
            Log.e("upload result", "succeeded");
            listener.on_secondary_status_changed(act.getString(R.string.sftp_upload_successfull));

            //toast = 1;

            //toastHandlefinish.sendEmptyMessage(0);

        } else {
            Log.e("upload failed ", "failed");
            listener.on_error_encountered(act.getString(R.string.sftp_upload_failed));

        }
    }


    public static List<String> getSdCardPaths(final Context context, final boolean includePrimaryExternalStorage)
    {
        final File[] externalCacheDirs= ContextCompat.getExternalCacheDirs(context);
        if(externalCacheDirs==null||externalCacheDirs.length==0)
            return null;
        if(externalCacheDirs.length==1)
        {
            if(externalCacheDirs[0]==null)
                return null;
            final String storageState= EnvironmentCompat.getStorageState(externalCacheDirs[0]);
            if(!Environment.MEDIA_MOUNTED.equals(storageState))
                return null;
            if(!includePrimaryExternalStorage&& Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB&& Environment.isExternalStorageEmulated())
                return null;
        }
        final List<String> result=new ArrayList<>();
        if(includePrimaryExternalStorage||externalCacheDirs.length==1)
            result.add(getRootOfInnerSdCardFolder(externalCacheDirs[0]));
        for(int i=1;i<externalCacheDirs.length;++i)
        {
            final File file=externalCacheDirs[i];
            if(file==null)
                continue;
            final String storageState= EnvironmentCompat.getStorageState(file);
            if(Environment.MEDIA_MOUNTED.equals(storageState))
                result.add(getRootOfInnerSdCardFolder(externalCacheDirs[i]));
        }
        if(result.isEmpty())
            return null;
        return result;
    }
    private static String getRootOfInnerSdCardFolder(File file)
    {
        if(file==null)
            return null;
        final long totalSpace=file.getTotalSpace();
        while(true)
        {
            final File parentFile=file.getParentFile();
            if(parentFile==null||parentFile.getTotalSpace()!=totalSpace)
                return file.getAbsolutePath();
            file=parentFile;
        }
    }

    public static void zip_array(Activity act, ArrayList<String> _files, String zipFileName, backup_progress_listener listener) {
        try {
            if(Thread.currentThread().isInterrupted())
            {
                return;
            }
            int BUFFER=1024;
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.size(); i++) {
                if(Thread.currentThread().isInterrupted())
                {
                    return;
                }   Log.e("Compression :", "Adding: " + _files.get(i));
                FileInputStream fi = new FileInputStream(_files.get(i));
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files.get(i).substring(_files.get(i).lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
                listener.on_secondary_status_changed(act.getString(R.string.archiving)+_files.get(i).substring(_files.get(i).lastIndexOf("/") + 1));
                listener.on_secondary_progress_changed((int)((((double)i)/((double)_files.size()))*100));
                // listener.on_secondary_status_changed("Archiving :"+_files.get(i).substring(_files.get(i).lastIndexOf("/") + 1));
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            listener.on_error_encountered(e.getMessage());
        }
    }

    public void zip(String[] _files, String zipFileName) {
        try {

            int BUFFER=1250*1250;
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.length; i++) {
                Log.e("Compression :", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
