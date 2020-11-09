package sparta.realm.spartautils.app_control;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;

import sparta.realm.spartautils.svars;


public class SpartaApplicationErrorHandler implements Thread.UncaughtExceptionHandler {
        private Thread.UncaughtExceptionHandler defaultUEH;
        private Context cntx = null;

        public SpartaApplicationErrorHandler(Context cntx) {
            this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
            this.cntx = cntx;
        }

        public void uncaughtException(Thread t, Throwable e) {
            StackTraceElement[] arr = e.getStackTrace();
            String report = e.toString()+"\n\n";
            report += "--------- Stack trace ---------\n\n";
            for (int i=0; i<arr.length; i++) {
                report += "    "+arr[i].toString()+"\n";
            }
            report += "-------------------------------\n\n";

            // If the exception was thrown in a background thread inside
            // AsyncTask, then the actual exception can be found with getCause

            report += "--------- Cause ---------\n\n";
            Throwable cause = e.getCause();
            if(cause != null) {
                report += cause.toString() + "\n\n";
                arr = cause.getStackTrace();
                for (int i=0; i<arr.length; i++) {
                    report += "    "+arr[i].toString()+"\n";
                }
            }
            report += "-------------------------------\n\n";
try{
    String root = cntx.getExternalFilesDir(null).getAbsolutePath() + "/traces/"+ svars.gett_date();
    Log.e("TRACE_TAG", "PATH: " + root);

    File file = new File(root);
    file.mkdirs();

        File trace_file = new File(file, "trc_"+ System.currentTimeMillis());
        FileWriter writer = new FileWriter(trace_file,true);
        writer.append(report);
        writer.flush();
        writer.close();


}catch (Exception ex){}
           /* try {
                FileOutputStream trace = cntx.openFileOutput("stack.trace",
                        Context.MODE_PRIVATE);
                trace.write(report.getBytes());
                trace.close();
            } catch(IOException ioe) {
                // ...
            }*/

            defaultUEH.uncaughtException(t, e);
        }
    }

