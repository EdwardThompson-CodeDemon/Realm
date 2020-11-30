package sparta.realm.Services;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;


import com.fpcore.FPMatch;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Stopwatch;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.json.JSONObject;




import java.util.ArrayList;


import java.util.concurrent.TimeUnit;


import sparta.realm.Activities.RecordList;


import sparta.realm.R;
import sparta.realm.spartamodels.dependant;
import sparta.realm.spartamodels.dyna_data;
import sparta.realm.spartamodels.dyna_data_obj;
import sparta.realm.spartamodels.dynamic_property;
import sparta.realm.spartamodels.member;

import sparta.realm.spartautils.sparta_string_compairer;
import sparta.realm.spartautils.svars;





/**
 * Created by Thompsons on 01-Feb-17.
 */

public class sdbw extends DatabaseManager {


    public sdbw(Context act)
    {
        super(act);
        this.act=act;





    }




   public ArrayList<dyna_data_obj> saved_users()
    {
        ArrayList<dyna_data_obj> saved_users=new ArrayList<>();
Cursor c=database.rawQuery("SELECT * FROM user_table"/*" WHERE data_status='1'"*/,null);
if(c.moveToFirst())
{
    do{
      //  dyna_data_obj user=new dyna_data_obj(c.getString(c.getColumnIndex("id")),c.getString(c.getColumnIndex("sid")),c.getString(c.getColumnIndex("sid")),c.getString(c.getColumnIndex("sid")),c.getString(c.getColumnIndex("user_fullname")),c.getString(c.getColumnIndex("user_fullname")),c.getString(c.getColumnIndex("username")));
//        dyna_data_obj objj=new dyna_data_obj("",US.getString("id"),"","mm",US.getString("code")+"-"+US.getString("full_name") +" "+US.getString("last_name"),"",US.getString("code") );
//        objj.data_2.value=US.getBoolean("changed_password")?act.getString(R.string.active):act.getString(R.string.inactive);

        dyna_data_obj usr=new dyna_data_obj();
     usr.data.value=c.getString(c.getColumnIndex("user_fullname"));
     usr.data_2.value=c.getString(c.getColumnIndex("data_status"));
     usr.code.value=c.getString(c.getColumnIndex("username"));
     usr.sid.value=c.getString(c.getColumnIndex("sid"));


        saved_users.add(usr);

    }while (c.moveToNext());

}

        return saved_users;
    }



    public void register_member(Boolean first_record,JSONObject jempl)
    {
        if(first_record!=null&&first_record)
        {
         //   database.beginTransaction();
            Log.e("Members =>","transaction begun");

return;
        }else if(first_record!=null&&first_record==false)
        {
            Log.e("Members =>","transaction complete");

//            database.setTransactionSuccessful();
//            database.endTransaction();
            return;
        }
        try {
            try{
                String qry="DELETE FROM member_info_table WHERE (sid='"+jempl.getString("id")+"' OR sid="+jempl.getString("id")+") AND sync_status='i'";
//Log.e("Query :",""+qry);
//Log.e("JO :",""+jempl);
                database.execSQL(qry);
            }catch(Exception ex){

                Log.e("DELETING ERROR =>",""+ex.getMessage());
            }
            if(jempl.getBoolean("is_active")){

                ContentValues cv= load_object_cv_from_JSON(jempl,new member());

                Log.e("Inserted member =>"," "+database.insert("member_info_table", null, cv));

            }else{
                 }









        }catch (Exception ex){
            Log.e("insert error",""+ex.getMessage());}

        if(first_record!=null&&first_record==false)
        {
            Log.e("Dynadata ENDING =>","transaction complete");

            database.setTransactionSuccessful();
            database.endTransaction();

        }

    }



  public void register_employee_dependant(JSONObject jempl)
    {

        try {
            try{

                database.execSQL("DELETE FROM employee_dependants_table WHERE sid=\""+jempl.getString("id")+"\" AND sync_status='i'");
            }catch(Exception ex){

                Log.e("DELETING ERROR =>",""+ex.getMessage());
            }
            if(!jempl.getBoolean("isactive")){return;}


            ContentValues cv = load_employee_dependant_from_JSON(jempl);


            Log.e("Inserted  =>"," "+database.insert("employee_dependants_table", null, cv));



        }catch (Exception ex){
            Log.e("Employee insert error",""+ex.getMessage());}

    }
    public ArrayList<member> load_all_employees(String search_tearm, int tracking_index, int offset, int limit, final String category)
    {
        Stopwatch stw=new Stopwatch();
        stw.start();
        final ArrayList<member> objs=new ArrayList<>();
        if(RecordList.search_counter!=tracking_index){Log.e("STW =>","Returning ...");return objs;}

        Cursor c =null;
        String limit_stt="LIMIT " + limit + " OFFSET " + offset;
        String qry="";
        if(search_tearm==null ||search_tearm.length()<1) {


           qry="SELECT EIT.sid,EIT.fullname,EIT.idno," +
                   "COUNT(CASE WHEN EDT.data_type='"+svars.data_type_indexes.fingerprints+"' THEN 1 END) AS fp_count," +
                   "COUNT(CASE WHEN (EDT.data_type='"+svars.data_type_indexes.photo+"' AND EDT.data_index='"+svars.image_indexes.profile_photo+"') THEN 1 END) AS img_count, " +
                    "COUNT(CASE WHEN (EDT.data_type='"+svars.data_type_indexes.photo+"' AND EDT.data_index='"+svars.image_indexes.profile_photo+"') THEN 1 END) AS face_count " +
                   "FROM member_info_table EIT " +
                   "LEFT OUTER JOIN member_data_table EDT ON EIT.sid=EDT.member_id " +
                   "WHERE category='"+category+"' "+
                   "GROUP BY EIT.id "+limit_stt;
        }else {
            //c = database.rawQuery("SELECT sid,fullname,idno,category FROM employee_info_table WHERE (UPPER(fullname) LIKE '%" + search_tearm + "%' OR UPPER(idno) LIKE '%" + search_tearm + "%')  LIMIT " + limit + " OFFSET " + offset, null);
//            qry="SELECT EIT.sid,EIT.fullname,EIT.idno,EIT.category,\n" +
//                    "(SELECT COUNT(id) FROM member_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"' AND parent_id=EIT.sid) AS fp_count, \n" +
//                    "(SELECT COUNT(id) FROM member_data_table WHERE data_type='"+svars.data_type_indexes.photo+"' AND parent_id=EIT.sid) AS img_count \n" +
//                    "FROM member_info_table EIT WHERE (UPPER(EIT.fullname) LIKE '%" + search_tearm + "%' OR UPPER(EIT.idno) LIKE '%" + search_tearm + "%') "+limit_stt;
            qry="SELECT EIT.sid,EIT.fullname,EIT.idno," +
                    "COUNT(CASE WHEN EDT.data_type='"+svars.data_type_indexes.fingerprints+"' THEN 1 END) AS fp_count," +
                    "COUNT(CASE WHEN EDT.data_type='"+svars.data_type_indexes.photo+"' THEN 1 END) AS img_count " +
                    "FROM member_info_table EIT " +
                    "LEFT OUTER JOIN member_data_table EDT ON EIT.sid=EDT.member_id " +
                  "WHERE (UPPER(EIT.fullname) LIKE '%" + search_tearm + "%' OR UPPER(EIT.idno) LIKE '%" + search_tearm + "%') AND EIT.category='"+category+"'"+
            "GROUP BY EIT.id "+limit_stt;

        }
        c=database.rawQuery(qry,null);
        Log.e("STW =>",": Raw querry : Current :"+tracking_index+"    Main :"+RecordList.search_counter+" : "+stw.elapsed(TimeUnit.MILLISECONDS));
        Log.e("STW =>",": Raw querry : "+qry);
        if(RecordList.search_counter!=tracking_index){Log.e("STW =>","Returning ...");return objs;}
        stw.reset();
        stw.start();
        if (c.moveToFirst()&&RecordList.search_counter==tracking_index) {
            do {
                //   if(Employee_list.search_counter!=tracking_index){break;}

//                employee emp=new employee();
//                emp.sid = c.getString(c.getColumnIndex("sid"));
//                emp.idno = c.getString(c.getColumnIndex("idno"));
//                emp.full_name = c.getString(c.getColumnIndex("full_name"));
//                if(for_list)
//                {
//                    emp.finger_prints=load_employee_data(c.getString(c.getColumnIndex("sid")),svars.data_type_indexes.fingerprints);
//                    emp.images=load_employee_data(c.getString(c.getColumnIndex("sid")),svars.data_type_indexes.photo);
//                }else{
//                    /**/
//                    emp.finger_prints=null;
//                    emp.images=null;
//                    emp.finger_print_images=null;
//                }
//member mem=load_member_from_Cursor(c);
                member mem= (member) load_object_from_Cursor(c,new member());
                mem.fp_count.value=c.getString(c.getColumnIndex("fp_count"));
mem.img_count.value=c.getString(c.getColumnIndex("img_count"));
mem.face_count=c.getString(c.getColumnIndex("face_count"));
                objs.add(mem);


            } while (c.moveToNext()&&RecordList.search_counter==tracking_index);
        }else{Log.e("STW =>","Returning ...");}
        Log.e("STW =>",": Array loading : Current"+tracking_index+"    Main :"+RecordList.search_counter+" : "+stw.elapsed(TimeUnit.MILLISECONDS));


        c.close();
        c=null;




        return objs;
    }
    public ArrayList<member> load_all_employees_without_fp(String search_tearm, int tracking_index)
    {
        ArrayList<member> objs=new ArrayList<>();

        Cursor c =null;// database.rawQuery("SELECT * FROM employee_info_table WHERE sid NOT IN(SELECT employee_id FROM member_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"') AND (UPPER(full_name) LIKE '%"+search_tearm+"%' OR UPPER(idno) LIKE '%"+search_tearm+"%') LIMIT 50", null);
        if(search_tearm==null ||search_tearm.length()<1)
        {
            c = database.rawQuery("SELECT * FROM employee_info_table WHERE sid NOT IN(SELECT employee_id FROM member_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"')", null);

        }else{
            c = database.rawQuery("SELECT * FROM employee_info_table WHERE sid NOT IN(SELECT employee_id FROM member_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"') AND (UPPER(full_name) LIKE '%"+search_tearm+"%' OR UPPER(idno) LIKE '%"+search_tearm+"%') LIMIT 100", null);

        }
        //
        if (c.moveToFirst()&&RecordList.search_counter==tracking_index) {
            do {
                //  if(Employee_list.search_counter!=tracking_index){break;}
//                member emp=load_member_from_Cursor(c);
                member emp= (member) load_object_from_Cursor(c,new member());

                // emp.finger_prints=load_employee_data(c.getString(c.getColumnIndex("sid")),svars.data_type_indexes.fingerprints);
                //  emp.images=load_employee_data(c.getString(c.getColumnIndex("sid")),svars.data_type_indexes.photo);
                objs.add(emp);
            } while (c.moveToNext()&&RecordList.search_counter==tracking_index);
        }




        c.close();
        c=null;

        return objs;
    }

    public ArrayList<member> load_all_employees(String search_tearm, int offset, int limit)
    {
        ArrayList<member> objs=new ArrayList<>();

        Cursor c =null;
        if(search_tearm==null ||search_tearm.length()<1) {
            c = database.rawQuery("SELECT id,sid,fullname,idno,category FROM member_info_table LIMIT " + limit + " OFFSET " + offset, null);
        }else {
            c = database.rawQuery("SELECT id,sid,fullname,idno,category FROM member_info_table WHERE (UPPER(fullname) LIKE '%" + search_tearm + "%' OR UPPER(idno) LIKE '%" + search_tearm + "%') LIMIT " + limit + " OFFSET " + offset, null);

        }
        if (c.moveToFirst()) {
            do {
//                member emp=load_member_from_Cursor(c);
                member emp= (member) load_object_from_Cursor(c,new member());

objs.add(emp);
            } while (c.moveToNext());
        }



        try {


        }catch (Exception ex){
            Log.e("Employee insert error",""+ex.getMessage());}
return objs;
    }
public ArrayList<member> load_pending_employees()
    {
        ArrayList<member> objs=new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM member_info_table WHERE sync_status='p'", null);


        if (c.moveToFirst()) {
            do {

objs.add(load_employee_from_cursor(c,true));
            } while (c.moveToNext());
        }
c.close();



return objs;
    }



 public ArrayList<dependant> load_pending_dependants()
    {
        ArrayList<dependant> objs=new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM employee_dependants_table WHERE sync_status='p' LIMIT 1", null);


        if (c.moveToFirst()) {
            do {

objs.add(load_dependant_from_cursor(c));
            } while (c.moveToNext());
        }
c.close();



return objs;
    }



   public ArrayList<member> load_all_employees_without_fp(String search_tearm)
    {
        ArrayList<member> objs=new ArrayList<>();

        Cursor c =null;// database.rawQuery("SELECT * FROM member_info_table WHERE sid NOT IN(SELECT employee_id FROM member_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"') AND (UPPER(full_name) LIKE '%"+search_tearm+"%' OR UPPER(idno) LIKE '%"+search_tearm+"%') LIMIT 50", null);
        if(search_tearm==null ||search_tearm.length()<1)
 {
     c = database.rawQuery("SELECT * FROM member_info_table WHERE sid NOT IN(SELECT employee_id FROM member_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"')", null);

 }else{
            c = database.rawQuery("SELECT * FROM member_info_table WHERE sid NOT IN(SELECT employee_id FROM member_data_table WHERE data_type='"+svars.data_type_indexes.fingerprints+"') AND (UPPER(full_name) LIKE '%"+search_tearm+"%' OR UPPER(idno) LIKE '%"+search_tearm+"%')", null);

        }
        //
        if (c.moveToFirst()) {
            do {
                member emp=new member();
//                emp.sid = c.getString(c.getColumnIndex("sid"));
//                emp.idno = c.getString(c.getColumnIndex("idno"));


               // emp.finger_prints=load_employee_data(c.getString(c.getColumnIndex("sid")),svars.data_type_indexes.fingerprints);
              //  emp.images=load_employee_data(c.getString(c.getColumnIndex("sid")),svars.data_type_indexes.photo);
objs.add(emp);
            } while (c.moveToNext());
        }

/*{,"employee_no":"2","worker_name":"TEST 1","nat_id":"5092906","reg_date":"2018-06-05T00:00:00","dob":null,"phone_no":"254704201171","email":"","marital_status_id":3,"gender_id":3,"tribe_id":1,"isactive":true,"employee_sub_category_id":2,"employee_category_id":2,"passport_url":null,"account_branch_id":8,"department_id":4,"user_id":0,"is_allsite":null,"nssf":"2007891443","nhif":"","kra_pin":"","datecomparer":1495113280,"employee_category":null,"employee_sub_category":null,"gender":null,"department":null,"marital_status":null,"account_branches":null}*/
/*
member_info_table.columns.add(new sdb_model.sdb_table.column("first_name"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("last_name"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("full_name"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("idno"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("dob"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("phone_no"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("email"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("category"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("sub_category"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("nationality"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("nfc"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("kra"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("nssf"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("nhif"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("employee_no"));
        member_info_table.columns.add(new sdb_model.sdb_table.column("gender"));



 */


        try {


        }catch (Exception ex){
            Log.e("Employee insert error",""+ex.getMessage());}
return objs;
    }
    public String[] load_employee_data(String sid, int data_type) {

        String raw_qry="SELECT * FROM member_data_table WHERE member_id ='"+sid+"' AND data_type='"+data_type+"'";
        Cursor c = database.rawQuery(raw_qry, null);
        String[] loaded_employee_data=new String[30];
        if (c.moveToFirst()) {
            do {
                loaded_employee_data[Integer.parseInt(c.getString(c.getColumnIndex("data_index")))]=c.getString(c.getColumnIndex("data"));

            } while (c.moveToNext());
        }
        c.close();

        return loaded_employee_data;
    }

 private String[] load_employee_data_from_transaction_no(String transaction_no, int data_type) {

        String raw_qry="SELECT * FROM member_data_table WHERE transaction_no ='"+transaction_no+"' AND data_type='"+data_type+"'";
        Cursor c = database.rawQuery(raw_qry, null);
        String[] loaded_employee_data=new String[30];
        if (c.moveToFirst()) {
            do {
                loaded_employee_data[Integer.parseInt(c.getString(c.getColumnIndex("data_index")))]=c.getString(c.getColumnIndex("data"));

            } while (c.moveToNext());
        }
        c.close();

        return loaded_employee_data;
    }

    public void populate_dummy_data()
    {
        int pref_count=0;
  int commune_count=0;
  int locality_count=0;

  database.execSQL("DELETE FROM dyna_data_table WHERE data_type IN('rg','prf','cmn','lcl','jc')");
        Log.e("Data build","Begun");
database.beginTransaction();

        for(int i=0;i<5;i++)
        {

            try {
                ContentValues cv = new ContentValues();
                cv.put("sid", i+"");
                cv.put("data_type", "rg");
              //  cv.put("parent", parent);
                cv.put("data", "REGION "+i);
                database.insert("dyna_data_table", null, cv);

                for(int j=0;j<10;j++)
                {
                    pref_count++;
                    cv = new ContentValues();
                    cv.put("sid", pref_count+"");
                    cv.put("data_type", "prf");
                      cv.put("parent", ""+i);
                    cv.put("data", "REGION "+i+" Pref"+pref_count);
                    database.insert("dyna_data_table", null, cv);

                    for(int k=0;k<10;k++)
                    {
                        commune_count++;
                        cv = new ContentValues();
                        cv.put("sid", commune_count+"");
                        cv.put("data_type", "cmn");
                        cv.put("parent", ""+pref_count);
                        cv.put("data", "REGION "+i+" Pref "+pref_count+" COMMUNE "+commune_count);
                        database.insert("dyna_data_table", null, cv);
                        for(int l=0;l<10;l++)
                        {
                            locality_count++;
                            cv = new ContentValues();
                            cv.put("sid", locality_count+"");
                            cv.put("data_type", "lcl");
                            cv.put("parent", ""+commune_count);
                            cv.put("data", "REGION "+i+" Pref "+pref_count+" COMMUNE "+commune_count+" LOCAL "+locality_count);
                            database.insert("dyna_data_table", null, cv);
                        }

                    }

                }
                Log.e("Data build","Region built "+i);
            }catch (Exception ex){

                Toast.makeText(act,"Region data has failed to be built ", Toast.LENGTH_LONG).show();

            }


          }


        for(int i=0;i<10;i++) {

            try {
                ContentValues cv = new ContentValues();
                cv.put("sid", i + "");
                cv.put("data_type", "jc");

               cv.put("data", "JOB CATEGORY " + i);
                database.insert("dyna_data_table", null, cv);
                Log.e("Data build","Job category built "+i);


            } catch (Exception ex) {
            }
        }


  for(int i=0;i<10;i++) {

            try {
                ContentValues cv = new ContentValues();
                cv.put("sid", i + "");
                cv.put("data_type", "jc");

               cv.put("data", "JOB CATEGORY " + i);
                database.insert("dyna_data_table", null, cv);
            } catch (Exception ex) {
            }
        }

        database.setTransactionSuccessful();
        database.endTransaction();
                Toast.makeText(act,"Data has been built ", Toast.LENGTH_LONG).show();

    }


    public ArrayList<member> load_searched_employee(String search_term)
    {
        ArrayList<member> objs=new ArrayList<>();

        Cursor c=database.rawQuery("SELECT * FROM employee_info WHERE employee_no LIKE '%"+search_term+"%' LIMIT 5",null);

        if(c.moveToFirst())
        {
            do{

                member emp=new member();





                objs.add(emp);

            }while (c.moveToNext());
        }
        return objs;
    }



    dependant load_dependant_from_cursor(Cursor c)
    {
        dependant d = new dependant();



        d.lid=c.getInt(c.getColumnIndex("id"))+"";
        d.dependant_type=c.getString(c.getColumnIndex("dependant_type"));
        d.surname=c.getString(c.getColumnIndex("surname"));
        d.other_names=c.getString(c.getColumnIndex("other_names"));
        d.gender=c.getString(c.getColumnIndex("gender"));
        d.dob=c.getString(c.getColumnIndex("dob"));
        d.birth_cert_photo=c.getString(c.getColumnIndex("birth_cert_pic"));
        d.passport_photo=c.getString(c.getColumnIndex("passport_pic"));
        d.parent_transaction_no=c.getString(c.getColumnIndex("parent_transaction_no"));
        d.transaction_no=c.getString(c.getColumnIndex("transaction_no"));
         d.user_id=c.getString(c.getColumnIndex("user_id"));
         d.reg_time=c.getString(c.getColumnIndex("reg_time"));

        return d;
    }
  member load_employee_from_cursor(Cursor c, boolean full_data)
    {
        member emp = new member();







        if(!full_data){return emp;}



        return emp;
    }

    ContentValues load_employee_dependant_from_JSON(JSONObject j)
    {
        ContentValues cv=new ContentValues();
        try{
            /*

                 postData[0].put("dependant_type", "" + pending_record.dependant_type);
                    postData[0].put("first_name", "" + pending_record.surname);
                    postData[0].put("last_name", "" + pending_record.other_names);
                     postData[0].put("gender", "" + pending_record.gender);

                    postData[0].put("dob", "" + pending_record.dob);
                     postData[0].put("parent_transaction_no", "" + pending_record.parent_transaction_no);
                    postData[0].put("transaction_no", "" + pending_record.transaction_no);
                        postData[0].put("user_id",""+pending_record.user_id);
                    postData[0].put("unique_code", "" + transaction_no);
                    postData[0].put("passport_photo", get_saved_doc_base64(  pending_record.passport_photo));
                    postData[0].put("birth_cert_photo", get_saved_doc_base64( pending_record.birth_cert_photo));



             */
        cv.put("sid",j.getString("id"));
        cv.put("dependant_type",j.getString("dependant_type"));
        cv.put("surname",j.getString("first_name"));
        cv.put("other_names",j.getString("last_name"));
        cv.put("gender",j.getString("gender"));
        cv.put("dob",j.getString("dob").length()>3?j.getString("dob").split("T")[0]:"");
        cv.put("birth_cert_pic",save_doc(j.getString("birth_cert_photo")));
        cv.put("passport_pic",save_doc(j.getString("passport_photo")));
        cv.put("parent_transaction_no",j.getString("parent_transaction_no"));
        cv.put("transaction_no",j.getString("transaction_no"));
        cv.put("user_id",j.getString("user_id"));
        cv.put("sync_status","i");
        cv.put("sync_var",j.getString("datecomparer"));
        }catch (Exception ex){
Log.e("JSON ERROR =>","Dependants "+ex.getMessage());
        }
        
        return cv;
        
    }

    ArrayList<dependant> load_dependants_from_transaction_no(String transaction_no)
    {
        ArrayList<dependant> dependants=new ArrayList<>();
        Cursor c=database.rawQuery("SELECT * FROM employee_dependants_table WHERE parent_transaction_no='"+transaction_no+"'",null);
        if(c.moveToFirst())
        {
            do{
                dependant d=new dependant();
                d.dependant_type=c.getString(c.getColumnIndex("dependant_type"));
                d.surname=c.getString(c.getColumnIndex("surname"));
                d.other_names=c.getString(c.getColumnIndex("other_names"));
                d.gender=c.getString(c.getColumnIndex("gender"));
                d.dob=c.getString(c.getColumnIndex("dob"));
                d.birth_cert_photo=c.getString(c.getColumnIndex("birth_cert_pic"));
                d.passport_photo=c.getString(c.getColumnIndex("passport_pic"));
                d.transaction_no=c.getString(c.getColumnIndex("transaction_no"));
                d.parent_transaction_no=c.getString(c.getColumnIndex("parent_transaction_no"));
                d.user_id=c.getString(c.getColumnIndex("user_id"));
                dependants.add(d);
            }while (c.moveToNext());
        }
        c.close();

        return dependants;
    }




}
