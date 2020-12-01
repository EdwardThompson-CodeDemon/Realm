package sparta.realm;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.common.base.Stopwatch;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import sparta.realm.Activities.SpartaAppCompactActivity;

import sparta.realm.Services.SynchronizationManager;
import sparta.realm.Services.DatabaseManager;
import sparta.realm.spartautils.svars;

import com.realm.annotations.RealmDataClass;

import static sparta.realm.Realm.realm;

public class MainActivity2 extends SpartaAppCompactActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SynchronizationManager as=new SynchronizationManager(new SynchronizationManager.sync_status_interface() {
            @Override
            public void on_status_code_changed(int status) {

            }

            @Override
            public void on_status_changed(String status) {

            }

            @Override
            public void on_info_updated(String status) {

            }

            @Override
            public void on_main_percentage_changed(int progress) {

            }

            @Override
            public void on_secondary_progress_changed(int progress) {

            }
        });
      //  as.InitialiseAutosync();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                SharedPreferences.Editor saver =act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

                saver.putString("user_name", ((EditText)findViewById(R.id.username_edt)).getText().toString());
                saver.putString("username", ((EditText)findViewById(R.id.username_edt)).getText().toString());
                saver.putString("pass", ((EditText)findViewById(R.id.password_edt)).getText().toString());





                saver.commit();
                as.sync_now();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);






        String resp=response(1).toString();
        String resp2=response(1).toString();
        Log.e("Deleting :","");
        DatabaseManager.database.execSQL("DELETE FROM TBL_supplier_account");
        DatabaseManager.database.execSQL("DELETE FROM CP_TBL_supplier_account");
        Log.e("Deleted :","");

//        Log.e("gson :",""+insert_gson(resp));

        Log.e("Deleting :","");
        DatabaseManager.database.execSQL("DELETE FROM TBL_supplier_account");
        Log.e("Deleted :","");

//        Log.e("Json :",""+insert_v1(resp));

    Log.e("Deleting :","");
        DatabaseManager.database.execSQL("DELETE FROM TBL_supplier_account");
        Log.e("Deleted :","");

//        Log.e("Json 2 :",""+insert_v1_sd(resp));

        Log.e("Deleting :","");
        DatabaseManager.database.execSQL("DELETE FROM TBL_supplier_account");
        Log.e("Deleted :","");

//Log.e("gson2 :",""+insert_gson2(resp));

        Log.e("Deleting :","Deleting");
        DatabaseManager.database.execSQL("DELETE FROM TBL_supplier_account");
        Log.e("Deleted :","Deleted");

        Log.e("gson_ctm 2 :",""+insert_v2_2(resp));


        Log.e("Deleting :","Deleting");
        DatabaseManager.database.execSQL("DELETE FROM TBL_supplier_account");
        Log.e("Deleted :","Deleted");

        Log.e("gson_ctm :",""+insert_v2(resp2));

// Log.e("Deleting :","Deleting");
//        dbh.database.execSQL("DELETE FROM TBL_supplier_account");
//        Log.e("Deleted :","Deleted");

        Log.e("gson_ctm 3:",""+insert_from(resp));

        test_realm();
    }

    void test_realm(){
//        RealmDataClass rd= new spartaDynamics();
//        for (String s: rd.getDynamicClassPaths()) {
//
//
//            Log.e("Classes reflected =>", "Realm :" + s);
//        }
//
//        Log.e("Classes reflected =>", "Done " );
    }

JSONObject response(int cnt)
{
    JSONObject jo=new JSONObject();
    try {

        jo = new JSONObject("{\"$id\":\"1\",\"IsOkay\":true,\"Message\":\"Details Found\"}\n");

        JSONArray result = new JSONArray();
    for (int i = 0; i < cnt; i++) {
//    {"$id":"2","id":614,"member_id":308,"acc_id":614,"active":true,"acc_name":"Payments Account","datecomparer":15990436450000000}
        JSONObject jo_in = new JSONObject();
        jo_in.put("id", 200 + i);
        jo_in.put("member_id", 10 + i);
        jo_in.put("acc_id", 614 + i);
        jo_in.put("acc_name", "acc_name" + i);
        jo_in.put("datecomparer", "1234567890" + i);
       result.put(jo_in);

    }jo.put("Result",result);

}catch(Exception ex){}
        return jo;
}
long insert_v1(String jos)
{
    Stopwatch stw=new Stopwatch();
    stw.start();
    try {
        JSONObject job = new JSONObject(jos);
        JSONArray array=job.getJSONArray("Result");
        for(int i=0;i<array.length();i++){
            JSONObject jo=array.getJSONObject(i);
            ContentValues cv=new ContentValues();
            cv.put("sid",jo.getString("id"));
            cv.put("member_id",jo.getString("member_id"));
            cv.put("acc_id",jo.getString("acc_id"));
            cv.put("acc_name",jo.getString("acc_name"));
            cv.put("sync_var",jo.getString("datecomparer"));


            DatabaseManager.database.insert("TBL_supplier_account",null,cv);

        }

    }catch(Exception ex){
        Log.e("Error :",""+ex.getMessage());

    }
return stw.elapsed(TimeUnit.MILLISECONDS);
}
long insert_v1_sd(String jos)
{
    Stopwatch stw=new Stopwatch();
    stw.start();
    try {
        JSONObject job = new JSONObject(jos);
        JSONArray array=job.getJSONArray("Result");
        for(int i=0;i<array.length();i++){
            JSONObject jo=array.getJSONObject(i);



            DatabaseManager.database.insert("TBL_supplier_account",null, (ContentValues) realm.getContentValuesFromJson(jo,"TBL_supplier_account"));

        }

    }catch(Exception ex){
        Log.e("Error :",""+ex.getMessage());

    }
return stw.elapsed(TimeUnit.MILLISECONDS);
}
long insert_gson(String jos)
{
    Stopwatch stw=new Stopwatch();
    stw.start();
    try {
        JsonObject job = new JsonParser().parse(jos).getAsJsonObject();

        JsonArray array=job.getAsJsonArray("Result");
        for(int i=0;i<array.size();i++){
            JsonObject jo= (JsonObject) array.get(i);
            ContentValues cv=new ContentValues();
            cv.put("sid",jo.get("id").getAsString());
            cv.put("member_id",jo.get("member_id").getAsString());
            cv.put("acc_id",jo.get("acc_id").getAsString());
            cv.put("acc_name",jo.get("acc_name").getAsString());
            cv.put("sync_var",jo.get("datecomparer").getAsString());


            DatabaseManager.database.insert("TBL_supplier_account",null,cv);

        }

    }catch(Exception ex){
        Log.e("Error :",""+ex.getMessage());

    }
return stw.elapsed(TimeUnit.MILLISECONDS);
}
long insert_gson2(String jos)
{
    Stopwatch stw=new Stopwatch();
    stw.start();
    try {
        JsonObject job = new JsonParser().parse(jos).getAsJsonObject();

        JsonArray array=job.getAsJsonArray("Result");
        for(int i=0;i<array.size();i++){
            JsonObject jo= (JsonObject) array.get(i);
            ContentValues cv=new ContentValues();
            cv.put("sid", String.valueOf(jo.get("id")));
            cv.put("member_id", String.valueOf(jo.get("member_id")));
            cv.put("acc_id", String.valueOf(jo.get("acc_id")));
            cv.put("acc_name", String.valueOf(jo.get("acc_name")));
            cv.put("sync_var", String.valueOf(jo.get("datecomparer")));


            DatabaseManager.database.insert("TBL_supplier_account",null,cv);

        }

    }catch(Exception ex){
        Log.e("Error :",""+ex.getMessage());

    }
return stw.elapsed(TimeUnit.MILLISECONDS);
}
    long  insert_v2(String jos)
    {
        Stopwatch stw=new Stopwatch();
        stw.start();
        try {
            JsonObject job = new JsonParser().parse(jos).getAsJsonObject();

            JsonArray array=job.getAsJsonArray("Result");
              int ar_sz=array.size();
            int max=ar_sz<=500?1:ar_sz%500>0?(ar_sz/500)+1:(ar_sz/500);
            Log.e("Max :",""+max);
            Log.e("Arr :",""+ar_sz);
            DatabaseManager.database.beginTransaction();
            for(int m=0;m<max;m++)
            {
                StringBuffer sb=new StringBuffer();
                sb.append("INSERT INTO TBL_supplier_account(sid,member_id,acc_id,acc_name,sync_status,sync_var) ");

                for (int s=0;s<(((m*500)+500)<=ar_sz?500:ar_sz-(m*500));s++)
                {
                    int i=(m*500)+s;
                   // Log.e("Iterating :","Round :"+m+" Position "+s+" Array possition :"+i);


                        JsonObject jo= (JsonObject) array.get(i);


                        sb.append(s==0?"SELECT "+String.valueOf(jo.get("id"))+" AS sid,"+
                                String.valueOf(jo.get("member_id"))+" AS member_id,"+
                                String.valueOf(jo.get("acc_id"))+" AS acc_id,"+
                                String.valueOf(jo.get("acc_name"))+" AS acc_name, 2 AS sync_status,"+
                                String.valueOf(jo.get("datecomparer"))+" AS sync_var":
                                " UNION SELECT "+String.valueOf(jo.get("id"))+","+String.valueOf(jo.get("member_id"))+
                                        ","+String.valueOf(jo.get("acc_id"))+","+
                                        String.valueOf(jo.get("acc_name"))+",2,"+
                                        String.valueOf(jo.get("datecomparer"))+"");





                }
             //   Log.e("INSERT QUERY :",sb.toString());
             //   dbh.log_String(act,"QRY v1 :"+sb.toString());
                DatabaseManager.database.execSQL(sb.toString());
              }
            DatabaseManager.database.setTransactionSuccessful();
            DatabaseManager.database.endTransaction();

//            for(int i=0;i<ar_sz;i++){
//                JsonObject jo= (JsonObject) array.get(i);
//            /*
//            INSERT INTO 'centers' ('rid','bp')
//          SELECT 'data1' AS 'rid', '00' AS 'bp'
//UNION  SELECT 'data2', '01'
//UNION  SELECT 'data3', '02'
//UNION  SELECT 'data4', '03'
//             */
//                sb.append(i==0?"SELECT "+String.valueOf(jo.get("id"))+" AS sid,"+
//                        String.valueOf(jo.get("member_id"))+" AS member_id,"+
//                        String.valueOf(jo.get("acc_id"))+" AS acc_id,"+
//                        String.valueOf(jo.get("acc_name"))+" AS acc_name,"+
//                        String.valueOf(jo.get("datecomparer"))+" AS sync_var":
//                        " UNION SELECT "+String.valueOf(jo.get("id"))+","+String.valueOf(jo.get("member_id"))+
//                                ","+String.valueOf(jo.get("acc_id"))+","+String.valueOf(jo.get("acc_name"))+","+String.valueOf(jo.get("datecomparer"))+"");
//
//
//                //           sb.append("("+String.valueOf(jo.get("id"))+","+String.valueOf(jo.get("member_id"))+
////                    ","+String.valueOf(jo.get("acc_id"))+","+String.valueOf(jo.get("acc_name"))+","+String.valueOf(jo.get("datecomparer"))+");");
//
//
//
//            }
//            Log.e("INSERT QUERY :",sb.toString());
//            dbh.database.beginTransaction();
//            dbh.database.execSQL(sb.toString());
//            dbh.database.setTransactionSuccessful();
//            dbh.database.endTransaction();

        }catch(Exception ex){
            Log.e("Error :",""+ex.getMessage());

        }
        return stw.elapsed(TimeUnit.MILLISECONDS);
    }
  long  insert_v2_2(String jos)
    {
        Stopwatch stw=new Stopwatch();
        stw.start();
        int compound_limit=500;
        try {
            JSONObject job = new JSONObject(jos);

            JSONArray array=job.getJSONArray("Result");
              int ar_sz=array.length();
            int max=ar_sz<=compound_limit?1:ar_sz%compound_limit>0?(ar_sz/compound_limit)+1:(ar_sz/compound_limit);
            Log.e("Max :",""+max);
            Log.e("Arr :",""+ar_sz);
         //   dbh.database.beginTransaction();
            for(int m=0;m<max;m++)
            {
                StringBuffer sb=new StringBuffer();
                sb.append("INSERT INTO TBL_supplier_account(sid,member_id,acc_id,acc_name,sync_var) ");

                for (int s=0;s<(((m*compound_limit)+compound_limit)<=ar_sz?compound_limit:ar_sz-(m*compound_limit));s++)
                {
                    int i=(m*compound_limit)+s;
                   // Log.e("Iterating :","Round :"+m+" Position "+s+" Array possition :"+i);


                        JSONObject jo= array.getJSONObject(i);


                        sb.append(s==0?"SELECT "+(jo.getString("id"))+" AS sid,"+
                               (jo.getString("member_id"))+" AS member_id,"+
                                (jo.getString("acc_id"))+" AS acc_id,'"+
                               (jo.getString("acc_name"))+"' AS acc_name,'"+
                               (jo.getString("datecomparer"))+"' AS sync_var":
                                " UNION SELECT "+(jo.getString("id"))+","+
                                        (jo.getString("member_id"))+","+
                                        (jo.getString("acc_id"))+",'"+
                                        (jo.getString("acc_name"))+"','"+
                                        (jo.getString("datecomparer"))+"'");





                }
              // Log.e("INSERT QUERY :",sb.toString());
              //  dbh.log_String(act,"QRY :"+sb.toString());
                DatabaseManager.database.execSQL(sb.toString());
              }
          //  dbh.database.setTransactionSuccessful();
           // dbh.database.endTransaction();

//            for(int i=0;i<ar_sz;i++){
//                JsonObject jo= (JsonObject) array.get(i);
//            /*
//            INSERT INTO 'centers' ('rid','bp')
//          SELECT 'data1' AS 'rid', '00' AS 'bp'
//UNION  SELECT 'data2', '01'
//UNION  SELECT 'data3', '02'
//UNION  SELECT 'data4', '03'
//             */
//                sb.append(i==0?"SELECT "+String.valueOf(jo.get("id"))+" AS sid,"+
//                        String.valueOf(jo.get("member_id"))+" AS member_id,"+
//                        String.valueOf(jo.get("acc_id"))+" AS acc_id,"+
//                        String.valueOf(jo.get("acc_name"))+" AS acc_name,"+
//                        String.valueOf(jo.get("datecomparer"))+" AS sync_var":
//                        " UNION SELECT "+String.valueOf(jo.get("id"))+","+String.valueOf(jo.get("member_id"))+
//                                ","+String.valueOf(jo.get("acc_id"))+","+String.valueOf(jo.get("acc_name"))+","+String.valueOf(jo.get("datecomparer"))+"");
//
//
//                //           sb.append("("+String.valueOf(jo.get("id"))+","+String.valueOf(jo.get("member_id"))+
////                    ","+String.valueOf(jo.get("acc_id"))+","+String.valueOf(jo.get("acc_name"))+","+String.valueOf(jo.get("datecomparer"))+");");
//
//
//
//            }
//            Log.e("INSERT QUERY :",sb.toString());
//            dbh.database.beginTransaction();
//            dbh.database.execSQL(sb.toString());
//            dbh.database.setTransactionSuccessful();
//            dbh.database.endTransaction();

        }catch(Exception ex){
            Log.e("Error :",""+ex.getMessage());

        }
        return stw.elapsed(TimeUnit.MILLISECONDS);
    }
  String[][] insert_sttz(JSONArray array)
    {
         Stopwatch stw=new Stopwatch();
        stw.start();
        int compound_limit=500;
        int ar_sz=array.length();
        int max=ar_sz<=compound_limit?1:ar_sz%compound_limit>0?(ar_sz/compound_limit)+1:(ar_sz/compound_limit);
        String[] qryz=new String[max];
     //   String[] sidz=new String[ar_sz];
        StringBuffer sb_sid=new StringBuffer("(");
        try {


            Log.e("Max :",""+max);
            Log.e("Arr :",""+ar_sz);
         //   dbh.database.beginTransaction();
            for(int m=0;m<max;m++)
            {
                StringBuffer sb=new StringBuffer();
                sb.append("REPLACE INTO TBL_supplier_account(sid,member_id,acc_id,acc_name,sync_status,sync_var) ");
int m2=m*compound_limit;
int l2=ar_sz-(m2);
int det=(((m2)+compound_limit)<=ar_sz?compound_limit:l2);
                for (int s=0;s<det;s++)
                {
                    int i=m2+s;
                   // Log.e("Iterating :","Round :"+m+" Position "+s+" Array possition :"+i);


                        JSONObject jo= array.getJSONObject(i);

//sidz[i]=jo.getString("id");
                    sb_sid.append(i==0?jo.getString("id"):","+jo.getString("id"));

                    sb.append(s==0?"SELECT "+(jo.getString("id"))+" AS sid,"+
                               (jo.getString("member_id"))+" AS member_id,"+
                                (jo.getString("acc_id"))+" AS acc_id,'"+
                               (jo.getString("acc_name"))+"' AS acc_name, 1 AS sync_status,'"+
                               (jo.getString("datecomparer"))+"' AS sync_var":
                                " UNION SELECT "+(jo.getString("id"))+","+
                                        (jo.getString("member_id"))+","+
                                        (jo.getString("acc_id"))+",'"+
                                        (jo.getString("acc_name"))+"',1,'"+
                                        (jo.getString("datecomparer"))+"'");





                }
                qryz[m]=sb.toString();


              }
            sb_sid.append(")");


        }catch(Exception ex){
            Log.e("Error :",""+ex.getMessage());

        }
        return new String[][]{new String[]{sb_sid.toString()},qryz};
    }

  long  insert_from(String jos)
    {
        Stopwatch stw=new Stopwatch();
        stw.start();
        try {
            JSONObject job = new JSONObject(jos);

            JSONArray array=job.getJSONArray("Result");
             String[][] ins=insert_sttz(array);
             String sidz_qry=ins[0][0];
              String[] qryz=ins[1];
             long tr_time=stw.elapsed(TimeUnit.MILLISECONDS);

                        DatabaseManager.database.beginTransaction();


                      DatabaseManager.database.execSQL("INSERT INTO CP_TBL_supplier_account SELECT * FROM TBL_supplier_account WHERE sid in "+sidz_qry+" AND sync_status=2");



            for (int i=0;i<qryz.length;i++)
            {
                DatabaseManager.database.execSQL(qryz[i]);
            }

            DatabaseManager.database.execSQL("REPLACE INTO TBL_supplier_account SELECT * FROM CP_TBL_supplier_account");
            DatabaseManager.database.execSQL("DELETE FROM CP_TBL_supplier_account");
            DatabaseManager.database.setTransactionSuccessful();
            DatabaseManager.database.endTransaction();

            Log.e("Exec time ",""+(stw.elapsed(TimeUnit.MILLISECONDS)-tr_time));


        }catch(Exception ex){
            Log.e("Error :",""+ex.getMessage());

        }
        return stw.elapsed(TimeUnit.MILLISECONDS);
    }
 long  insert_v3(String jos)
    {
        Stopwatch stw=new Stopwatch();
        stw.start();
        try {
            JsonObject job = new JsonParser().parse(jos).getAsJsonObject();

            JsonArray array=job.getAsJsonArray("Result");
              int ar_sz=array.size();
            int max=ar_sz<=500?1:ar_sz%500>0?(ar_sz/500)+1:(ar_sz/500);
            Log.e("Max :",""+max);
            Log.e("Arr :",""+ar_sz);
            DatabaseManager.database.beginTransaction();
            for(int m=0;m<max;m++)
            {

                for (int s=0;s<(((m*500)+500)<=ar_sz?500:ar_sz-(m*500));s++)
                {
                    int i=(m*500)+s;
                   // Log.e("Iterating :","Round :"+m+" Position "+s+" Array possition :"+i);


                        JsonObject jo= (JsonObject) array.get(i);
                    StringBuffer sb=new StringBuffer();
                    sb.append("INSERT INTO TBL_supplier_account(sid,member_id,acc_id,acc_name,sync_var) VALUES ");


//                        sb.append(s==0?"SELECT "+String.valueOf(jo.get("id"))+" AS sid,"+
//                                String.valueOf(jo.get("member_id"))+" AS member_id,"+
//                                String.valueOf(jo.get("acc_id"))+" AS acc_id,"+
//                                String.valueOf(jo.get("acc_name"))+" AS acc_name,"+
//                                String.valueOf(jo.get("datecomparer"))+" AS sync_var":
//                                " UNION SELECT "+String.valueOf(jo.get("id"))+","+String.valueOf(jo.get("member_id"))+
//                                        ","+String.valueOf(jo.get("acc_id"))+","+String.valueOf(jo.get("acc_name"))+","+String.valueOf(jo.get("datecomparer"))+"");


                    sb.append("("+String.valueOf(jo.get("id"))+","+String.valueOf(jo.get("member_id"))+
                    ","+String.valueOf(jo.get("acc_id"))+","+String.valueOf(jo.get("acc_name"))+","+String.valueOf(jo.get("datecomparer"))+")");

                  //  Log.e("INSERT QUERY :",sb.toString());
                    DatabaseManager.database.execSQL(sb.toString());

                }

              }
            DatabaseManager.database.setTransactionSuccessful();
            DatabaseManager.database.endTransaction();

//            for(int i=0;i<ar_sz;i++){
//                JsonObject jo= (JsonObject) array.get(i);
//            /*
//            INSERT INTO 'centers' ('rid','bp')
//          SELECT 'data1' AS 'rid', '00' AS 'bp'
//UNION  SELECT 'data2', '01'
//UNION  SELECT 'data3', '02'
//UNION  SELECT 'data4', '03'
//             */
//                sb.append(i==0?"SELECT "+String.valueOf(jo.get("id"))+" AS sid,"+
//                        String.valueOf(jo.get("member_id"))+" AS member_id,"+
//                        String.valueOf(jo.get("acc_id"))+" AS acc_id,"+
//                        String.valueOf(jo.get("acc_name"))+" AS acc_name,"+
//                        String.valueOf(jo.get("datecomparer"))+" AS sync_var":
//                        " UNION SELECT "+String.valueOf(jo.get("id"))+","+String.valueOf(jo.get("member_id"))+
//                                ","+String.valueOf(jo.get("acc_id"))+","+String.valueOf(jo.get("acc_name"))+","+String.valueOf(jo.get("datecomparer"))+"");
//
//
//                //           sb.append("("+String.valueOf(jo.get("id"))+","+String.valueOf(jo.get("member_id"))+
////                    ","+String.valueOf(jo.get("acc_id"))+","+String.valueOf(jo.get("acc_name"))+","+String.valueOf(jo.get("datecomparer"))+");");
//
//
//
//            }
//            Log.e("INSERT QUERY :",sb.toString());
//            dbh.database.beginTransaction();
//            dbh.database.execSQL(sb.toString());
//            dbh.database.setTransactionSuccessful();
//            dbh.database.endTransaction();

        }catch(Exception ex){
            Log.e("Error :",""+ex.getMessage());

        }
        return stw.elapsed(TimeUnit.MILLISECONDS);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}