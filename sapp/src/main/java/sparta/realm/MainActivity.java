package sparta.realm;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.digitalpersona.uareu.Fmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import sparta.realm.Activities.SpartaAppCompactActivity;
import sparta.realm.Activities.SpartaAppCompactFingerPrintActivity;
import sparta.realm.Services.DatabaseManager;
import sparta.realm.Services.SynchronizationManager;
import sparta.realm.spartamodels.tike.ticket;
import sparta.realm.spartamodels.timecap.member_data;
import sparta.realm.spartautils.biometrics.DataMatcher;
import sparta.realm.spartautils.matching_interface;

public class MainActivity extends SpartaAppCompactActivity {
DataMatcher dm=new DataMatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SynchronizationManager sm=new SynchronizationManager();
        sm.InitialiseAutosync();
        sm.sync_now();
        Log.e(logTag,"fp count :"+ Realm.databaseManager.get_record_count("member_data_table"));
        ArrayList<ticket> tickets=Realm.databaseManager.loadObjectArray(ticket.class,null,null,null,true,1000,10);
        ArrayList<member_data> fpz=Realm.databaseManager.loadObjectArray(member_data.class, new String[]{"sid","data"},null,null,true,1000,10);

        int limit=250;

       int offset=0;
        String limit_stt="LIMIT " + limit + " OFFSET " + offset;
        Cursor c = DatabaseManager.database.query("SELECT sid,data FROM member_data_table "+limit_stt);
        int result_count=c.getCount();
        int i=0;
        int fp_count=0;
while (result_count>=limit) {
    LinkedHashMap <String,Fmd> fpId_fmd=new LinkedHashMap <>();
    if (c.moveToFirst()) {
        do {
            fpId_fmd.put(c.getString(0),dm.base_64_tofmd(c.getString(1)));

        } while (c.moveToNext());

    }

    fp_count+=fpId_fmd.size();
    result_count=c.getCount();
    f_main_map.put(i,fpId_fmd);
            i++;
    limit_stt="LIMIT " + limit + " OFFSET " + i*limit;
    c = DatabaseManager.database.query("SELECT sid,data FROM member_data_table "+limit_stt);
}
        c.close();
Log.e(logTag,"Data load complete fpcount:"+i);


    }
    HashMap<Integer,  LinkedHashMap<String, Fmd>> f_main_map=new HashMap<>();
//    LinkedHashMap<String, Fmd> dmmms;
    String logTag="FPTest";
    Integer match_com=0;
    matching_interface mint=new matching_interface() {
        @Override
        public void on_match_complete(boolean match_found, String mils) {
             synchronized (match_com){
                match_com++;
            }
            Log.e(logTag, "Match complete:" + match_com);

        }

        @Override
        public void on_match_found(String employee_id, String data_index, String match_time, int v_type, int verrification_mode) {

        }

        @Override
        public void on_finger_match_found(String fp_id, int score, String match_time) {
            Log.e(logTag, "Match found:" + fp_id + " in mills: " + match_time);

        }

        @Override
        public void on_match_progress_changed(int progress) {

        }

        @Override
        public void on_match_faild_reason_found(int reason, String employee_id) {

        }
    };
//    @Override
//    public void on_result_obtained(String capt_result) {
//        super.on_result_obtained(capt_result);
//        Log.e("FP matching test","fp:"+ capt_result);//f_main_map
//        Log.e("FP matching test","fp count:"+ f_main_map.size());//f_main_map
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for(LinkedHashMap<String, Fmd> dmmms:f_main_map.values()) {
//
//                    dm.load_match_uareu(Base64.decode(capt_result, 0), mint,dmmms);
//                }
//                }
//            }).start();
//
//    }
}
