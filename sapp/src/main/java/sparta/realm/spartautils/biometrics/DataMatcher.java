package sparta.realm.spartautils.biometrics;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Base64;
import android.util.Log;

import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.fpcore.FPMatch;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import sparta.realm.Realm;
import sparta.realm.spartamodels.percent_calculation;


import sparta.realm.spartautils.biometrics.face.face_handler;
import sparta.realm.spartautils.biometrics.fp.sdks.fgtit.utils.ExtApi;
import sparta.realm.spartautils.matching_interface;
import sparta.realm.spartautils.svars;

import static sparta.realm.Services.DatabaseManager.database;
import static sparta.realm.spartautils.biometrics.fp.fp_handler_stf_usb_8_inch.main_fmd_format;


public class DataMatcher {




    Thread[] exceutioner_threads;
    boolean turbo_match_found=false;
    public enum verification_type {
        clock_in,
        clock_out,
        verification,
        door_verification,
        weighbridge_verification,
        check_out,
        check_in

    }

    public void load_match(final byte[] model, final matching_interface inter, final int clock_mode)
    {
        Log.e("CPU CORE COUNT :",""+ svars.getCpuCores());
        if(svars.current_device()==svars.DEVICE.WALL_MOUNTED.ordinal()||svars.current_device()==svars.DEVICE.BIO_MINI.ordinal()) {
            FPMatch.getInstance().InitMatch(0,"http://www.fgtit.com/");
        }
        int employee_count_actual=0;
        Cursor cnt_c=database.rawQuery("SELECT COUNT(*) FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"'",null);
        if(cnt_c.moveToFirst()){
            do{
                employee_count_actual=cnt_c.getInt(0);
            }while(cnt_c.moveToNext());}
        final Stopwatch stw=new Stopwatch();


        stw.start();
        final int employee_count=employee_count_actual;
        // final int emp_per_thread=2500;
        //    final int max_thread_count=(employee_count+(emp_per_thread-1))/emp_per_thread;
        final int max_thread_count=1;//svars.getCpuCores();
        final int emp_per_thread=(employee_count+(max_thread_count-1))/max_thread_count;


        Log.e("Turbo match =>","Employee loading_time =>"+stw.elapsed(TimeUnit.MILLISECONDS));
        Log.e("Turbo match =>","Employee fp count =>"+employee_count);


        stw.stop();
        stw.reset();
        stw.start();
        //final int max_thread_count=employee_count/emp_per_thread;
        // (value + 14) / 15;
        Log.e("Turbo match =>","Threads to generate =>"+max_thread_count);

        exceutioner_threads= new Thread[max_thread_count];

        turbo_match_found=false;
        final int[] finish_count = {0};
        for(int i=0;i<exceutioner_threads.length;i++)
        {
//           final FPMatch fpm=new FPMatch();
//           fpm.InitMatch(0,"http://www.fgtit.com/");
            Log.e("Turbo match =>","Generating thread =>"+i+"/"+exceutioner_threads.length);

            if(turbo_match_found)break;
            final int finalI = i;
            // try {
            exceutioner_threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int actual_var_index = (emp_per_thread * finalI);
                        int emp_per_thread_intt = employee_count - actual_var_index;
                        String qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id NOT NULL LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                        switch (clock_mode)
                        {
                            case 0:
                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id NOT NULL AND employee_id NOT IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;

                                break;
                            case 1:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                            case 5:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                            case 6:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_checking_table ect ON eit.sid=ect.employee_id WHERE ect.check_in_time IS NULL OR ect.check_in_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                        }
                        Cursor c = database.rawQuery(qry, null);
                        if (c.moveToFirst()) {
                            do {
                                if (turbo_match_found) break;
                                Log.e("Turbo match =>", finalI+" :Matching =>" + c.getString(c.getColumnIndex("employee_id")));
                                if(svars.current_device()==svars.DEVICE.WALL_MOUNTED.ordinal()/*||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()*/)
                                {
//            if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(svars.fp_my_r_thumb_bt_device)) > svars.matching_acuracy) {

                                    if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(c.getString(c.getColumnIndex("data")))) > svars.matching_acuracy) {



                                        Log.e("FPMATCH ", "OBTAINED ");


                                        inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), clock_mode,1);
                                        turbo_match_found = true;
                                    }
                                }else{
                                    int mm_score=1000;
                                    try {

                                        Fmd ffm1 = UareUGlobal.GetImporter().ImportFmd(model, main_fmd_format, main_fmd_format);
                                        Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(c.getString(c.getColumnIndex("data")), 0), main_fmd_format, main_fmd_format);
                                        //   Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(svars.fp_my_r_index_wall_mat_device, 0), main_fmd_format, main_fmd_format);
                                        //  Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(svars.fp_my_r_index_8_inch, 0), main_fmd_format, main_fmd_format);



                                        mm_score = UareUGlobal.GetEngine().Compare(ffm1, 0, ffm2, 0);
                                    }catch (UareUException EX){

                                        Log.e("Turbo match =>","Matching error ur=>"+EX.getMessage());

                                    }catch (Throwable ex){
                                        ex.printStackTrace();
                                        Log.e("Turbo match =>","Matching error th =>"+ex.getMessage());

                                    }
                                    Log.e("Turbo match =>","Matching error score=>"+mm_score);
                                    if (mm_score <svars.matching_error_margin) {

                                        Log.e("FPMATCH ", "OBTAINED ");
                                        ContentValues cv =new ContentValues();
                                        cv.put("data_usage_frequency",""+System.currentTimeMillis());
                                        database.update("employee_data_table",cv,"id="+c.getInt(c.getColumnIndex("id")),null);

                                        inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), clock_mode,1);
                                        turbo_match_found = true;
                                    }
                                }





                            } while (c.moveToNext());

                        } else {

                            Log.e("Turbo match =>", "Thread " + finalI + " has no records to match");
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                            if(!turbo_match_found){load_match_not_found_reason(model,inter,clock_mode);}

                        }
                        finish_count[0]++;
                        Log.e("Turbo match =>", "Thread finish : " + finish_count[0]);
                        percent_calculation pc = new percent_calculation(max_thread_count + "", finish_count[0] + "");
                        inter.on_match_progress_changed(Integer.parseInt(pc.per_balance));
                        if (max_thread_count == finish_count[0]) {
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                            if(!turbo_match_found){load_match_not_found_reason(model,inter,clock_mode);}
                        }


                    }catch (Throwable ex){

                        Log.e("Main Turbo match =>", "Thread " + finalI + " has no records to match");

                    }
                }
            });
            exceutioner_threads[i].start();
            /*}catch (Throwable ex){

                Log.e("Turbo match =>","Thread generation error =>"+i+ex.getMessage());

            }*/
        }
        Log.e("Turbo match =>","Thread generation time =>"+stw.elapsed(TimeUnit.MILLISECONDS));

    }
    public int load_match(final byte[] model, final matching_interface inter, final boolean load_match_failed_reason)
    {
        final int clock_mode=0;
        Log.e("CPU CORE COUNT :",""+svars.getCpuCores());
        if(svars.current_device()==svars.DEVICE.WALL_MOUNTED.ordinal()||svars.current_device()==svars.DEVICE.BIO_MINI.ordinal()) {
            FPMatch.getInstance().InitMatch(0,"http://www.fgtit.com/");
        }
        int employee_count_actual=0;
        Cursor cnt_c=database.rawQuery("SELECT COUNT(*) FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"'",null);
        if(cnt_c.moveToFirst()){
            do{
                employee_count_actual=cnt_c.getInt(0);
            }while(cnt_c.moveToNext());}
        final Stopwatch stw=new Stopwatch();


        stw.start();
        final int employee_count=employee_count_actual;
        // final int emp_per_thread=2500;
        //    final int max_thread_count=(employee_count+(emp_per_thread-1))/emp_per_thread;
        final int max_thread_count=1;//svars.getCpuCores();
        final int emp_per_thread=(employee_count+(max_thread_count-1))/max_thread_count;


        Log.e("Turbo match =>","Employee loading_time =>"+stw.elapsed(TimeUnit.MILLISECONDS));
        Log.e("Turbo match =>","Employee fp count =>"+employee_count);


        stw.stop();
        stw.reset();
        stw.start();
        //final int max_thread_count=employee_count/emp_per_thread;
        // (value + 14) / 15;
        Log.e("Turbo match =>","Threads to generate =>"+max_thread_count);

        exceutioner_threads= new Thread[max_thread_count];

        turbo_match_found=false;
        final int[] finish_count = {0};
        for(int i=0;i<exceutioner_threads.length;i++)
        {
//           final FPMatch fpm=new FPMatch();
//           fpm.InitMatch(0,"http://www.fgtit.com/");
            Log.e("Turbo match =>","Generating thread =>"+i+"/"+exceutioner_threads.length);

            if(turbo_match_found)break;
            final int finalI = i;
            // try {
            exceutioner_threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int actual_var_index = (emp_per_thread * finalI);
                        int emp_per_thread_intt = employee_count - actual_var_index;
                        String qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id NOT NULL AND employee_id NOT IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                        String qry2="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;


                        Cursor c = database.rawQuery(qry2, null);
                        if (c.moveToFirst()) {
                            do {
                                if (turbo_match_found) break;
                                Log.e("Turbo match =>", finalI+" :Matching =>" + c.getString(c.getColumnIndex("employee_id")));
                                if(svars.current_device()==svars.DEVICE.WALL_MOUNTED.ordinal()/*||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()*/)
                                {
//            if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(svars.fp_my_r_thumb_bt_device)) > svars.matching_acuracy) {

                                    if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(c.getString(c.getColumnIndex("data")))) > svars.matching_acuracy) {



                                        Log.e("FPMATCH ", "OBTAINED ");


                                        inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), verification_type.clock_out.ordinal(),1);
                                        turbo_match_found = true;
                                    }
                                }else{
                                    int mm_score=1000;
                                    try {

                                        Fmd ffm1 = UareUGlobal.GetImporter().ImportFmd(model, main_fmd_format, main_fmd_format);
                                        Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(c.getString(c.getColumnIndex("data")), 0), main_fmd_format, main_fmd_format);
                                        //   Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(svars.fp_my_r_index_wall_mat_device, 0), main_fmd_format, main_fmd_format);
                                        //  Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(svars.fp_my_r_index_8_inch, 0), main_fmd_format, main_fmd_format);



                                        mm_score = UareUGlobal.GetEngine().Compare(ffm1, 0, ffm2, 0);
                                    }catch (UareUException EX){

                                        Log.e("Turbo match =>","Matching error ur=>"+EX.getMessage());

                                    }catch (Throwable ex){
                                        ex.printStackTrace();
                                        Log.e("Turbo match =>","Matching error th =>"+ex.getMessage());

                                    }
                                    Log.e("Turbo match =>","Matching error score=>"+mm_score);
                                    if (mm_score <svars.matching_error_margin) {

                                        Log.e("FPMATCH ", "OBTAINED ");
                                        ContentValues cv =new ContentValues();
                                        cv.put("data_usage_frequency",""+System.currentTimeMillis());
                                        database.update("employee_data_table",cv,"id="+c.getInt(c.getColumnIndex("id")),null);

                                        inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), verification_type.clock_out.ordinal(),1);
                                        turbo_match_found = true;
                                    }
                                }





                            } while (c.moveToNext());

                        } else {

                            Log.e("Turbo match =>", "Thread " + finalI + " has no records to match");
//                                inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
//                                if(!turbo_match_found){load_match_not_found_reason(model,inter,clock_mode);}

                        }
                        if(!turbo_match_found)
                        {
                            c = database.rawQuery(qry, null);
                            if (c.moveToFirst()) {
                                do {
                                    if (turbo_match_found) break;
                                    Log.e("Turbo match =>", finalI+" :Matching =>" + c.getString(c.getColumnIndex("employee_id")));
                                    if(svars.current_device()==svars.DEVICE.WALL_MOUNTED.ordinal()/*||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()*/)
                                    {
//            if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(svars.fp_my_r_thumb_bt_device)) > svars.matching_acuracy) {

                                        if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(c.getString(c.getColumnIndex("data")))) > svars.matching_acuracy) {



                                            Log.e("FPMATCH ", "OBTAINED ");


                                            inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), 0,1);
                                            turbo_match_found = true;
                                        }
                                    }else{
                                        int mm_score=1000;
                                        try {

                                            Fmd ffm1 = UareUGlobal.GetImporter().ImportFmd(model, main_fmd_format, main_fmd_format);
                                            Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(c.getString(c.getColumnIndex("data")), 0), main_fmd_format, main_fmd_format);
                                            //   Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(svars.fp_my_r_index_wall_mat_device, 0), main_fmd_format, main_fmd_format);
                                            //  Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(svars.fp_my_r_index_8_inch, 0), main_fmd_format, main_fmd_format);



                                            mm_score = UareUGlobal.GetEngine().Compare(ffm1, 0, ffm2, 0);
                                        }catch (UareUException EX){

                                            Log.e("Turbo match =>","Matching error ur=>"+EX.getMessage());

                                        }catch (Throwable ex){
                                            ex.printStackTrace();
                                            Log.e("Turbo match =>","Matching error th =>"+ex.getMessage());

                                        }
                                        Log.e("Turbo match =>","Matching error score=>"+mm_score);
                                        if (mm_score <svars.matching_error_margin) {

                                            Log.e("FPMATCH ", "OBTAINED ");
                                            ContentValues cv =new ContentValues();
                                            cv.put("data_usage_frequency",""+System.currentTimeMillis());
                                            database.update("employee_data_table",cv,"id="+c.getInt(c.getColumnIndex("id")),null);

                                            inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), 0,1);
                                            turbo_match_found = true;
                                        }
                                    }





                                } while (c.moveToNext());

                            } else {

                                Log.e("Turbo match =>", "Thread " + finalI + " has no records to match");
                                inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                                if(!turbo_match_found){load_match_not_found_reason(model,inter,0);}

                            }
                        }
                        finish_count[0]++;
                        Log.e("Turbo match =>", "Thread finish : " + finish_count[0]);
                        percent_calculation pc = new percent_calculation(max_thread_count + "", finish_count[0] + "");
                        inter.on_match_progress_changed(Integer.parseInt(pc.per_balance));
                        if (max_thread_count == finish_count[0]) {
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                            if(!turbo_match_found&&load_match_failed_reason){load_match_not_found_reason(model,inter,0);}
                        }


                    }catch (Throwable ex){

                        Log.e("Main Turbo match =>", "Thread " + finalI + " has no records to match");

                    }
                }
            });
            exceutioner_threads[i].start();
            /*}catch (Throwable ex){

                Log.e("Turbo match =>","Thread generation error =>"+i+ex.getMessage());

            }*/
        }
        Log.e("Turbo match =>","Thread generation time =>"+stw.elapsed(TimeUnit.MILLISECONDS));
        return clock_mode;
    }
    public boolean can_clock(boolean in, String empid)
    {
        String qry="SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id " +
                "WHERE ect.employee_id='"+empid+"' AND (ect.clock_out_time IS NULL OR ect.clock_out_time ='')";//clocking out check if record of in without out



        Cursor c = database.rawQuery(qry, null);
        if (c.moveToFirst()) {
            c.close();
            return !in;
        }else{
            return in;
        }
        //  return false;
    }
    public void load_match(final byte[] model, final matching_interface inter, final int clock_mode, final String emp_id)
    {
        Log.e("CPU CORE COUNT :",""+svars.getCpuCores());
        if(svars.current_device()==svars.DEVICE.WALL_MOUNTED.ordinal()||svars.current_device()==svars.DEVICE.BIO_MINI.ordinal()) {
            FPMatch.getInstance().InitMatch(0,"http://www.fgtit.com/");
        }
        int employee_count_actual=0;
        Cursor cnt_c=database.rawQuery("SELECT COUNT(*) FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"'",null);
        if(cnt_c.moveToFirst()){
            do{
                employee_count_actual=cnt_c.getInt(0);
            }while(cnt_c.moveToNext());}
        final Stopwatch stw=new Stopwatch();


        stw.start();
        final int employee_count=employee_count_actual;
        // final int emp_per_thread=2500;
        //    final int max_thread_count=(employee_count+(emp_per_thread-1))/emp_per_thread;
        final int max_thread_count=1;//svars.getCpuCores();
        final int emp_per_thread=(employee_count+(max_thread_count-1))/max_thread_count;


        Log.e("Turbo match =>","Employee loading_time =>"+stw.elapsed(TimeUnit.MILLISECONDS));
        Log.e("Turbo match =>","Employee fp count =>"+employee_count);


        stw.stop();
        stw.reset();
        stw.start();
        //final int max_thread_count=employee_count/emp_per_thread;
        // (value + 14) / 15;
        Log.e("Turbo match =>","Threads to generate =>"+max_thread_count);

        exceutioner_threads= new Thread[max_thread_count];

        turbo_match_found=false;
        final int[] finish_count = {0};
        for(int i=0;i<exceutioner_threads.length;i++)
        {
//           final FPMatch fpm=new FPMatch();
//           fpm.InitMatch(0,"http://www.fgtit.com/");
            Log.e("Turbo match =>","Generating thread =>"+i+"/"+exceutioner_threads.length);

            if(turbo_match_found)break;
            final int finalI = i;
            // try {
            exceutioner_threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int actual_var_index = (emp_per_thread * finalI);
                        int emp_per_thread_intt = employee_count - actual_var_index;
                        String qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id='"+emp_id+"' LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                        switch (clock_mode)
                        {
                            case 0:
                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id ='"+emp_id+"' AND employee_id NOT IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;

                                break;
                            case 1:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id ='"+emp_id+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                            case 5:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id ='"+emp_id+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                            case 6:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id ='"+emp_id+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_checking_table ect ON eit.sid=ect.employee_id WHERE ect.check_in_time IS NULL OR ect.check_in_time ='') LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                        }
                        Cursor c = database.rawQuery(qry, null);
                        if (c.moveToFirst()) {
                            do {
                                if (turbo_match_found) break;
                                Log.e("Turbo match =>", finalI+" :Matching =>" + c.getString(c.getColumnIndex("employee_id")));
                                if(svars.current_device()==svars.DEVICE.WALL_MOUNTED.ordinal()/*||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()*/)
                                {
                                    if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(c.getString(c.getColumnIndex("data")))) > svars.matching_acuracy) {



                                        Log.e("FPMATCH ", "OBTAINED ");


                                        inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), clock_mode,1);
                                        turbo_match_found = true;
                                    }
                                }else{
                                    int mm_score=1000;
                                    try {

                                        Fmd ffm1 = UareUGlobal.GetImporter().ImportFmd(model, main_fmd_format, main_fmd_format);
                                        Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(c.getString(c.getColumnIndex("data")), 0), main_fmd_format, main_fmd_format);



                                        mm_score = UareUGlobal.GetEngine().Compare(ffm1, 0, ffm2, 0);
                                    }finally {
                                        //   ex.printStackTrace();
                                        //  Log.e("Turbo match =>","Matching error th =>"+ex.getMessage());

                                    }
                                    Log.e("Turbo match =>","Matching error score=>"+mm_score);
                                    if (mm_score <svars.matching_error_margin) {

                                        Log.e("FPMATCH ", "OBTAINED ");


                                        inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), clock_mode,1);
                                        c.close();
                                        turbo_match_found = true;
                                    }
                                }





                            } while (c.moveToNext());
                            c.close();

                        } else {
                            c.close();

                            Log.e("Turbo match =>", "Thread " + finalI + " has no records to match");

                        }
                        finish_count[0]++;
                        Log.e("Turbo match =>", "Thread finish : " + finish_count[0]);
                        percent_calculation pc = new percent_calculation(max_thread_count + "", finish_count[0] + "");
                        inter.on_match_progress_changed(Integer.parseInt(pc.per_balance));
                        if (max_thread_count == finish_count[0]) {
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                            if(!turbo_match_found){load_match_not_found_reason(model,inter,clock_mode);}
                        }


                    }catch (Throwable ex){

                        Log.e("Main Turbo match =>", "Thread " + finalI + " has no records to match");

                    }
                }
            });
            exceutioner_threads[i].start();
            /*}catch (Throwable ex){

                Log.e("Turbo match =>","Thread generation error =>"+i+ex.getMessage());

            }*/
        }
        Log.e("Turbo match =>","Thread generation time =>"+stw.elapsed(TimeUnit.MILLISECONDS));

    }

    public void load_face_match(final byte[] model, final matching_interface inter, final int clock_mode, boolean student)
    {
        final String member_category=student?"1":"2";
        Log.e("CPU CORE COUNT :",""+svars.getCpuCores());
        if(svars.current_device()==svars.DEVICE.WALL_MOUNTED.ordinal()||svars.current_device()==svars.DEVICE.BIO_MINI.ordinal()) {
            FPMatch.getInstance().InitMatch(0,"http://www.fgtit.com/");
        }
        int employee_count_actual=0;
        Cursor cnt_c=database.rawQuery("SELECT COUNT(id) FROM member_data_table WHERE data_type='"+svars.data_type_indexes.photo+"'",null);
        if(cnt_c.moveToFirst()){
            do{
                employee_count_actual=cnt_c.getInt(0);
            }while(cnt_c.moveToNext());}
        final Stopwatch stw=new Stopwatch();


        stw.start();
        final int employee_count=employee_count_actual;
        // final int emp_per_thread=2500;
        //    final int max_thread_count=(employee_count+(emp_per_thread-1))/emp_per_thread;
        final int max_thread_count=1;//svars.getCpuCores();
        final int emp_per_thread=(employee_count+(max_thread_count-1))/max_thread_count;


        Log.e("Turbo match =>","Data count loading_time =>"+stw.elapsed(TimeUnit.MILLISECONDS));
        Log.e("Turbo match =>","Data count =>"+employee_count);


        stw.stop();
        stw.reset();
        stw.start();
        //final int max_thread_count=employee_count/emp_per_thread;
        // (value + 14) / 15;
        Log.e("Turbo match =>","Threads to generate =>"+max_thread_count);

        exceutioner_threads= new Thread[max_thread_count];

        turbo_match_found=false;
        final int[] finish_count = {0};
        for(int i=0;i<exceutioner_threads.length;i++)
        {
//           final FPMatch fpm=new FPMatch();
//           fpm.InitMatch(0,"http://www.fgtit.com/");
            Log.e("Turbo match =>","Generating thread =>"+i+"/"+exceutioner_threads.length);

            if(turbo_match_found)break;
            final int finalI = i;
            // try {
            exceutioner_threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int actual_var_index = (emp_per_thread * finalI);
                        int emp_per_thread_intt = employee_count - actual_var_index;
                        String qry="SELECT mdt.id,mdt.member_id,mdt.data,mdt.data_index FROM member_data_table mdt INNER JOIN member_info_table mit ON mit.sid=mdt.member_id WHERE mit.category='"+member_category+"' AND mdt.data_type='"+svars.data_type_indexes.photo+"' AND mdt.data_index='"+svars.image_indexes.croped_face+"' AND mdt.member_id NOT NULL "+/*AND mdt.data_index IN (2,3) */"ORDER BY mdt.data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                        switch (clock_mode)
                        {
                            case 0:
                                qry="SELECT * FROM member_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND member_id NOT NULL AND member_id NOT IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN member_clock_table ect ON eit.sid=ect.member_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='' AND eit.category ='"+member_category+"') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;

                                break;
                            case 1:



                                qry="SELECT * FROM member_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND member_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN member_clock_table ect ON eit.sid=ect.member_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='' AND eit.category='"+member_category+"') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                            case 5:



                                qry="SELECT * FROM member_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND member_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN member_clock_table ect ON eit.sid=ect.member_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                            case 6:



                                qry="SELECT * FROM member_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND member_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_checking_table ect ON eit.sid=ect.member_id WHERE ect.check_in_time IS NULL OR ect.check_in_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                        }
                        Cursor c = database.rawQuery(qry, null);
                        if (c.moveToFirst()) {
                            do {
                                if (turbo_match_found) break;
                                Log.e("Turbo match =>", finalI+" :Matching =>" + c.getString(c.getColumnIndex("member_id")));





                                if (face_handler.match_ok(model,svars.current_app_config(Realm.context).file_path_employee_data+c.getString(c.getColumnIndex("data"))) ) {

                                    Log.e("FACE MATCH ", "OBTAINED ");
                                    ContentValues cv =new ContentValues();
                                    cv.put("data_usage_frequency",""+System.currentTimeMillis());
                                    database.update("member_data_table",cv,"id="+c.getInt(c.getColumnIndex("id")),null);

                                    inter.on_match_found(c.getString(c.getColumnIndex("member_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), clock_mode,1);
                                    turbo_match_found = true;
                                }






                            } while (c.moveToNext());

                        } else {

                            Log.e("Turbo match =>", "Thread " + finalI + " has no records to match");
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                            if(!turbo_match_found){load_match_not_found_reason(model,inter,clock_mode);}

                        }
                        finish_count[0]++;
                        Log.e("Turbo match =>", "Thread finish : " + finish_count[0]);
                        percent_calculation pc = new percent_calculation(max_thread_count + "", finish_count[0] + "");
                        inter.on_match_progress_changed(Integer.parseInt(pc.per_balance));
                        if (max_thread_count == finish_count[0]) {
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                            if(!turbo_match_found){load_match_not_found_reason(model,inter,clock_mode);}
                        }


                    }catch (Throwable ex){

                        Log.e("Main Turbo match =>", "Thread " + finalI + " Error :"+ex);

                    }
                }
            });
            exceutioner_threads[i].start();
            /*}catch (Throwable ex){

                Log.e("Turbo match =>","Thread generation error =>"+i+ex.getMessage());

            }*/
        }
        Log.e("Turbo match =>","Thread generation time =>"+stw.elapsed(TimeUnit.MILLISECONDS));

    }
    public void load_face_match(final String model, final matching_interface inter, final int clock_mode, boolean student)
    {
        final String member_category=student?"1":"2";
        Log.e("CPU CORE COUNT :",""+svars.getCpuCores());
        if(svars.current_device()==svars.DEVICE.WALL_MOUNTED.ordinal()||svars.current_device()==svars.DEVICE.BIO_MINI.ordinal()) {
            FPMatch.getInstance().InitMatch(0,"http://www.fgtit.com/");
        }
        int employee_count_actual=0;
        Cursor cnt_c=database.rawQuery("SELECT COUNT(id) FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.photo+"'",null);
        if(cnt_c.moveToFirst()){
            do{
                employee_count_actual=cnt_c.getInt(0);
            }while(cnt_c.moveToNext());}
        final Stopwatch stw=new Stopwatch();


        stw.start();
        final int employee_count=employee_count_actual;
        // final int emp_per_thread=2500;
        //    final int max_thread_count=(employee_count+(emp_per_thread-1))/emp_per_thread;
        final int max_thread_count=1;//svars.getCpuCores();
        final int emp_per_thread=(employee_count+(max_thread_count-1))/max_thread_count;


        Log.e("Turbo match =>","Data count loading_time =>"+stw.elapsed(TimeUnit.MILLISECONDS));
        Log.e("Turbo match =>","Data count =>"+employee_count);


        stw.stop();
        stw.reset();
        stw.start();
        //final int max_thread_count=employee_count/emp_per_thread;
        // (value + 14) / 15;
        Log.e("Turbo match =>","Threads to generate =>"+max_thread_count);

        exceutioner_threads= new Thread[max_thread_count];

        turbo_match_found=false;
        final int[] finish_count = {0};
        for(int i=0;i<exceutioner_threads.length;i++)
        {
//           final FPMatch fpm=new FPMatch();
//           fpm.InitMatch(0,"http://www.fgtit.com/");
            Log.e("Turbo match =>","Generating thread =>"+i+"/"+exceutioner_threads.length);

            if(turbo_match_found)break;
            final int finalI = i;
            // try {
            exceutioner_threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int actual_var_index = (emp_per_thread * finalI);
                        int emp_per_thread_intt = employee_count - actual_var_index;
                        String qry="SELECT mdt.id,mdt.employee_id,mdt.data,mdt.data_index FROM employee_data_table mdt " +
                                "INNER JOIN employee_info_table mit ON mit.sid=mdt.employee_id " +
                                "WHERE mdt.data_type='"+svars.data_type_indexes.photo+"' " +
                                "AND mdt.data_index='"+svars.image_indexes.croped_face+"' " +
                                "AND mdt.employee_id NOT NULL "+/*AND mdt.data_index IN (2,3) */"" +
                                "ORDER BY mdt.data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                        /// qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id NOT NULL LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                        switch (clock_mode)
                        {
                            case 0:
                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND data_index='"+svars.image_indexes.croped_face+"' AND employee_id NOT NULL AND employee_id NOT IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;

                                break;
                            case 1:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND data_index='"+svars.image_indexes.croped_face+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                            case 5:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND data_index='"+svars.image_indexes.croped_face+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit " +
                                        "INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id " +
                                        "WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') " +
                                        "AND eit.sid NOT IN(SELECT employee_id FROM employee_checking_table echt WHERE  ect.check_in_time IS NULL OR ect.check_in_time ='' )" +
                                        "ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                            case 6:



                                qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND data_index='"+svars.image_indexes.croped_face+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit " +
                                        "INNER JOIN employee_checking_table ect ON eit.sid=ect.employee_id " +
                                        "WHERE ect.check_in_time IS NULL OR ect.check_in_time ='') " +
                                        "ORDER BY data_usage_frequency DESC LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;
                                break;
                        }
                        Cursor c = database.rawQuery(qry, null);
                        if (c.moveToFirst()) {
                            do {
                                if (turbo_match_found) break;
                                Log.e("Turbo match =>", finalI+" :Matching =>" + c.getString(c.getColumnIndex("member_id")));





                                if (face_handler.match_ok(model,svars.current_app_config(Realm.context).file_path_employee_data+c.getString(c.getColumnIndex("data"))) ) {

                                    Log.e("FACE MATCH ", "OBTAINED ");
                                    ContentValues cv =new ContentValues();
                                    cv.put("data_usage_frequency",""+System.currentTimeMillis());
                                    database.update("member_data_table",cv,"id="+c.getInt(c.getColumnIndex("id")),null);

                                    inter.on_match_found(c.getString(c.getColumnIndex("member_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), clock_mode,2);
                                    turbo_match_found = true;
                                }






                            } while (c.moveToNext());

                        } else {

                            Log.e("Turbo match =>", "Thread " + finalI + " has no records to match");
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                            if(!turbo_match_found){/*load_match_not_found_reason(null,inter,clock_mode);*/}

                        }
                        finish_count[0]++;
                        Log.e("Turbo match =>", "Thread finish : " + finish_count[0]);
                        percent_calculation pc = new percent_calculation(max_thread_count + "", finish_count[0] + "");
                        //    inter.on_match_progress_changed(Integer.parseInt(pc.per_balance));
                        if (max_thread_count == finish_count[0]) {
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                            if(!turbo_match_found){/*load_match_not_found_reason(null,inter,clock_mode);*/}
                        }


                    }catch (Throwable ex){

                        Log.e("Main Turbo match =>", "Thread " + finalI + " Error :"+ex);

                    }
                }
            });
            exceutioner_threads[i].start();
            /*}catch (Throwable ex){

                Log.e("Turbo match =>","Thread generation error =>"+i+ex.getMessage());

            }*/
        }
        Log.e("Turbo match =>","Thread generation time =>"+stw.elapsed(TimeUnit.MILLISECONDS));

    }

    public void load_match_unique(final byte[] model, final matching_interface inter, final String mem_id)
    {
        Log.e("CPU CORE COUNT :",""+ svars.getCpuCores());
        if(svars.current_device()== svars.DEVICE.WALL_MOUNTED.ordinal()|| svars.current_device()== svars.DEVICE.BIO_MINI.ordinal()) {
            FPMatch.getInstance().InitMatch(0,"http://www.fgtit.com/");
        }
        int employee_count_actual=0;
        Cursor cnt_c=database.rawQuery("SELECT COUNT(*) FROM employee_data_table WHERE data_type='"+ svars.data_type_indexes.fingerprints+"'",null);
        if(cnt_c.moveToFirst()){
            do{
                employee_count_actual=cnt_c.getInt(0);
            }while(cnt_c.moveToNext());}
        final Stopwatch stw=new Stopwatch();


        stw.start();
        final int employee_count=employee_count_actual;
        // final int emp_per_thread=2500;
        //    final int max_thread_count=(employee_count+(emp_per_thread-1))/emp_per_thread;
        final int max_thread_count=1;//svars.getCpuCores();
        final int emp_per_thread=(employee_count+(max_thread_count-1))/max_thread_count;


        Log.e("Turbo match =>","Employee loading_time =>"+stw.elapsed(TimeUnit.MILLISECONDS));
        Log.e("Turbo match =>","Employee fp count =>"+employee_count);


        stw.stop();
        stw.reset();
        stw.start();
        //final int max_thread_count=employee_count/emp_per_thread;
        // (value + 14) / 15;
        Log.e("Turbo match =>","Threads to generate =>"+max_thread_count);

        exceutioner_threads= new Thread[max_thread_count];

        turbo_match_found=false;
        final int[] finish_count = {0};
        for(int i=0;i<exceutioner_threads.length;i++)
        {
//           final FPMatch fpm=new FPMatch();
//           fpm.InitMatch(0,"http://www.fgtit.com/");
            Log.e("Turbo match =>","Generating thread =>"+i+"/"+exceutioner_threads.length);

            if(turbo_match_found)break;
            final int finalI = i;
            // try {
            exceutioner_threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int actual_var_index = (emp_per_thread * finalI);
                        int emp_per_thread_intt = employee_count - actual_var_index;
                        String qry="SELECT * FROM employee_data_table WHERE data_type='"+ svars.data_type_indexes.fingerprints+"' AND employee_id ='"+mem_id+"' LIMIT " + emp_per_thread + " OFFSET " + actual_var_index;


                        Cursor c = database.rawQuery(qry, null);
                        int h=0;
                        if (c.moveToFirst()) {
                            do {
                                percent_calculation pc = new percent_calculation(c.getCount() + "", h+ "");
                                inter.on_match_progress_changed(Integer.parseInt(pc.per_balance));
                                h++;

                                if (turbo_match_found) break;
                                Log.e("Turbo match =>", finalI+" :Matching =>" + c.getString(c.getColumnIndex("employee_id")));
                                if(svars.current_device()== svars.DEVICE.WALL_MOUNTED.ordinal()/*||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()*/)
                                {
                                    if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(c.getString(c.getColumnIndex("data")))) > svars.matching_acuracy) {



                                        Log.e("FPMATCH ", "OBTAINED ");


                                        inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), 3,1);
                                        turbo_match_found = true;
                                    }
                                }else{
                                    int mm_score=1000;
                                    try {

                                        Fmd ffm1 = UareUGlobal.GetImporter().ImportFmd(model, main_fmd_format, main_fmd_format);
                                        Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(c.getString(c.getColumnIndex("data")), 0), main_fmd_format, main_fmd_format);



                                        mm_score = UareUGlobal.GetEngine().Compare(ffm1, 0, ffm2, 0);
                                    }catch (UareUException EX){

                                        Log.e("Turbo match =>","Matching error ur=>"+EX.getMessage());

                                    }catch (Throwable ex){
                                        ex.printStackTrace();
                                        Log.e("Turbo match =>","Matching error th =>"+ex.getMessage());

                                    }
                                    Log.e("Turbo match =>","Matching error score=>"+mm_score);
                                    if (mm_score < svars.matching_error_margin) {

                                        Log.e("FPMATCH ", "OBTAINED ");


                                        inter.on_match_found(c.getString(c.getColumnIndex("employee_id")), c.getString(c.getColumnIndex("data_index")), "" + stw.elapsed(TimeUnit.MILLISECONDS), 3,1);
                                        turbo_match_found = true;
                                    }
                                }





                            } while (c.moveToNext());

                        } else {

                            Log.e("Turbo match =>", "Thread " + finalI + " has no records to match");

                        }
                        finish_count[0]++;
                        percent_calculation pc = new percent_calculation(max_thread_count + "", finish_count[0] + "");
                        inter.on_match_progress_changed(Integer.parseInt(pc.per_balance));
                        Log.e("Turbo match =>", "Thread finish : " + finish_count[0]);
                        if (max_thread_count == finish_count[0]) {
                            inter.on_match_complete(turbo_match_found,stw.elapsed(TimeUnit.MILLISECONDS)+"");/*kill_threads();*/
                        }


                    }catch (Throwable ex){

                        Log.e("Main Turbo match =>", "Thread " + finalI + " has no records to match");

                    }
                }
            });
            exceutioner_threads[i].start();
            /*}catch (Throwable ex){

                Log.e("Turbo match =>","Thread generation error =>"+i+ex.getMessage());

            }*/
        }
        Log.e("Turbo match =>","Thread generation time =>"+stw.elapsed(TimeUnit.MILLISECONDS));

    }
    public void load_match_not_found_reason(final byte[] model, final matching_interface inter, final int clock_mode)
    {
        Log.e("CPU CORE COUNT :",""+svars.getCpuCores());
        if(svars.current_device()==svars.DEVICE.WALL_MOUNTED.ordinal()||svars.current_device()==svars.DEVICE.BIO_MINI.ordinal()) {
            FPMatch.getInstance().InitMatch(0,"http://www.fgtit.com/");
        }


        final Stopwatch stw=new Stopwatch();
        stw.start();


        Log.e("Turbo match =>","Employee loading_time =>"+stw.elapsed(TimeUnit.MILLISECONDS));



        stw.stop();
        stw.reset();
        stw.start();




        try {

            String qry_static="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND COALESCE (employee_id, '') = ''";
            String qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND COALESCE (employee_id, '') = ''";
            switch (clock_mode)
            {
                case 0:
                    qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='') ";

                    break;
                case 1:
                    qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id NOT NULL AND employee_id NOT IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='')";



                    break;
                case 5:



                    qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id NOT IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_clocking_table ect ON eit.sid=ect.employee_id WHERE ect.clock_out_time IS NULL OR ect.clock_out_time ='')";
                    break;
                case 6:



                    qry="SELECT * FROM employee_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND employee_id IN(SELECT eit.sid FROM employee_info_table eit INNER JOIN employee_checking_table ect ON eit.sid=ect.employee_id WHERE ect.check_in_time IS NULL OR ect.check_in_time ='')";
                    break;
            }
            Cursor c = database.rawQuery(qry_static, null);
            if (c.moveToFirst()) {
                do {
                    if (turbo_match_found) break;
                    Log.e("Turbo match =>", " :Matching for reason =>" + c.getString(c.getColumnIndex("employee_id")));
                    if(svars.current_device()==svars.DEVICE.WALL_MOUNTED.ordinal()/*||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()*/)
                    {
                        if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(c.getString(c.getColumnIndex("data")))) > svars.matching_acuracy) {



                            Log.e("FPMATCH ", "Reason OBTAINED ");


                            inter.on_match_faild_reason_found(0,c.getString(c.getColumnIndex("employee_id")));
                            turbo_match_found = true;
                        }
                    }else{
                        int mm_score=1000;
                        try {

                            Fmd ffm1 = UareUGlobal.GetImporter().ImportFmd(model, main_fmd_format, main_fmd_format);
                            Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(c.getString(c.getColumnIndex("data")), 0), main_fmd_format, main_fmd_format);



                            mm_score = UareUGlobal.GetEngine().Compare(ffm1, 0, ffm2, 0);
                        }catch (UareUException EX){

                            Log.e("Turbo match =>","Matching error ur=>"+EX.getMessage());

                        }catch (Throwable ex){
                            ex.printStackTrace();
                            Log.e("Turbo match =>","Matching error th =>"+ex.getMessage());

                        }
                        Log.e("Turbo match =>","Matching error score=>"+mm_score);
                        if (mm_score <svars.matching_error_margin) {

                            Log.e("FPMATCH ", "Reason OBTAINED ");

                            inter.on_match_faild_reason_found(0,c.getString(c.getColumnIndex("employee_id")));

                            turbo_match_found = true;
                        }
                    }





                } while (c.moveToNext());
                c.close();
            } else {

                Log.e("Turbo match =>", "Thread  has no records to match");

            }
            c.close();
            c = database.rawQuery(qry, null);
            if (c.moveToFirst()) {
                do {
                    if (turbo_match_found) break;
                    Log.e("Turbo match =>", " :Matching  for reason =>" + c.getString(c.getColumnIndex("employee_id")));
                    if(svars.current_device()==svars.DEVICE.WALL_MOUNTED.ordinal()/*||svars.current_device(act)==svars.DEVICE.BIO_MINI.ordinal()*/)
                    {
                        if (FPMatch.getInstance().MatchTemplate(model, ExtApi.Base64ToBytes(c.getString(c.getColumnIndex("data")))) > svars.matching_acuracy) {



                            Log.e("FPMATCH ", "Reason OBTAINED ");


                            inter.on_match_faild_reason_found(clock_mode==0?1:clock_mode==1?2:clock_mode==5?3:4,c.getString(c.getColumnIndex("employee_id")));

                            turbo_match_found = true;
                        }
                    }else{
                        int mm_score=1000;
                        try {

                            Fmd ffm1 = UareUGlobal.GetImporter().ImportFmd(model, main_fmd_format, main_fmd_format);
                            Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(c.getString(c.getColumnIndex("data")), 0), main_fmd_format, main_fmd_format);



                            mm_score = UareUGlobal.GetEngine().Compare(ffm1, 0, ffm2, 0);
                        }catch (UareUException EX){

                            Log.e("Turbo match =>","Matching error ur=>"+EX.getMessage());

                        }catch (Throwable ex){
                            ex.printStackTrace();
                            Log.e("Turbo match =>","Matching error th =>"+ex.getMessage());

                        }
                        Log.e("Turbo match =>","Matching error score=>"+mm_score);
                        if (mm_score <svars.matching_error_margin) {

                            Log.e("FPMATCH ", "Reason OBTAINED ");


                            inter.on_match_faild_reason_found(clock_mode==0?1:clock_mode==1?2:clock_mode==5?3:4,c.getString(c.getColumnIndex("employee_id")));
                            turbo_match_found = true;
                        }
                    }





                } while (c.moveToNext());
                c.close();
            } else {

                Log.e("Turbo match =>", "Thread  has no records to match");

            }
            c.close();



            if (!turbo_match_found) {
                inter.on_match_faild_reason_found(666,null);


            }


        }catch (Throwable ex){

            Log.e("Main Turbo match =>", "Thread has no records to match");

        }


        Log.e("Turbo match =>","Thread generation time =>"+stw.elapsed(TimeUnit.MILLISECONDS));

    }



    void kill_threads()
    {
        if(exceutioner_threads!=null)
        {
            for(Thread th :exceutioner_threads)
            {
                th.stop();
                th=null;

            }

        }
    }

}
