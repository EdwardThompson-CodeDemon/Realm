package sparta.realm.spartamodels;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import sparta.realm.spartautils.sparta_loc_util;


public class file_download {
public String local_file_path,external_file_path,file_name,user_id,generation_date;
Activity act;
Boolean re_download_on_failed=true;
public file_download(Activity act, s_file_interface intt)
{
    main_inter=intt;
    this.act=act;
}
public file_download(Activity act, String user_id, String external_file_path, String file_name, String generation_date)
{


    this.act=act;
    this.user_id=user_id;
    this.external_file_path=external_file_path;
    this.file_name=file_name;
    this.generation_date=generation_date;
   // begin_download();

}



public interface s_file_interface{
    void on_download_progress(int progress);
    void on_download_error(String error);
    void on_download_begun();
 void on_re_download_begun();
void on_download_complete();



}
s_file_interface main_inter;
int re_download_count=0;
int max_redounload_count=4;
boolean downloaded=false;

public void begin_download_()
{

    downloaded=false;

        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            URL url = new URL(external_file_path);
                            HttpURLConnection c = (HttpURLConnection) url.openConnection();
                            c.setRequestMethod("GET");
                            c.setDoOutput(true);
                            c.setDoInput(true);
                            c.connect();
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   main_inter.on_download_begun();

                                }
                            });


                            String path = Environment.getExternalStorageDirectory().toString();
                            final File file = new File(path + "/realm/SUPERVISOR_REPORTS/"+user_id+"/");
                            file.mkdirs();
                            final File outputFile = new File(file, file_name);
                            FileOutputStream fos = new FileOutputStream(outputFile);

                            InputStream is = c.getInputStream();

                            int maxBufferSize = 128 * 1024;

                            byte[] buffer = new byte[maxBufferSize];
                            // byte[] buffer = new byte[65536];
                            //   byte[] buffer = new byte[1073741824];
                            // byte[] buffer = new byte[32768];
                            // byte[] buffer = new byte[4096];
                            int len1 = 0;
                            int per_counter = 0;

                            while ((len1 = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, len1);
                                per_counter += len1;
                                // final double finalPercent = Math.round(((double) per_counter / (double) c.getContentLength()) * 100);
                                final double finalPercent = sparta_loc_util.round(((double) per_counter / (double) c.getContentLength()),2)* 100;
                                // Log.e("Download ", "per: " + finalPercent + "%");
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        main_inter.on_download_progress((int)finalPercent);
                                    }
                                });
                            }

                            fos.close();
                            is.close();
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
main_inter.on_download_complete();
re_download_count=0;
                                    local_file_path=file.getAbsolutePath();
                                    downloaded=true;

                                }
                            });
                        } catch (final Exception ex) {

                            Log.e("download error ", "[" +re_download_count+"]"+ ex.getMessage());
                            ex.printStackTrace();
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                  main_inter.on_download_error(ex.getMessage());
if(re_download_on_failed&re_download_count<max_redounload_count)
{
    main_inter.on_re_download_begun();
    re_download_count++;
    begin_download();
}

                                }
                            });
                        }
                    }
                }, 2000);

                Looper.loop();
            }
        };
        thread.start();

}
public void begin_download()
{

    downloaded=false;

        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            URL url = new URL(external_file_path);


                            String path = Environment.getExternalStorageDirectory().toString();
                            final File file = new File(path + "/realm/SUPERVISOR_REPORTS/"+user_id+"/");
                            file.mkdirs();

                            AndroidNetworking.download(external_file_path,path + "/realm/SUPERVISOR_REPORTS/"+user_id+"/",file_name)
                                    .setTag("downloadTest")
                                    .setPriority(Priority.MEDIUM)
                                    .build()
                                    .setDownloadProgressListener(new DownloadProgressListener() {
                                        @Override
                                        public void onProgress(long bytesDownloaded, long totalBytes) {
                                            // do anything with progress
                                            percent_calculation per=new percent_calculation(totalBytes+"",""+bytesDownloaded);
                                            main_inter.on_download_progress(Integer.parseInt(per.per_balance));
                                        }
                                    })
                                    .startDownload(new DownloadListener() {
                                        @Override
                                        public void onDownloadComplete() {
                                            main_inter.on_download_complete();
                                            re_download_count=0;
                                            downloaded=true;
                                            local_file_path=file.getAbsolutePath();     }
                                        @Override
                                        public void onError(ANError error) {
                                            Log.e("download error ", "[" +re_download_count+"]"+ error.getErrorBody());
                                            main_inter.on_download_error(error.getErrorBody());
                                            if(re_download_on_failed&re_download_count<max_redounload_count)
                                            {
                                                main_inter.on_re_download_begun();
                                                re_download_count++;
                                                begin_download();
                                            }
                                        }
                                    });

                                    local_file_path=file.getAbsolutePath();



                        } catch (final Exception ex) {

                            Log.e("download error ", "[" +re_download_count+"]"+ ex.getMessage());
                            ex.printStackTrace();
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {



                                }
                            });
                        }
                    }
                }, 2000);

                Looper.loop();
            }
        };
        thread.start();

}

public void setOnStatusChangeListener(s_file_interface intt)
{

    main_inter=intt;
}

}
