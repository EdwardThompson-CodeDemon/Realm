package sparta.realm.RC;

import android.content.ContentValues;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.realm.annotations.SyncDescription;
import com.realm.annotations.sync_service_description;
import com.realm.annotations.sync_status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import sparta.realm.Realm;
import sparta.realm.RealmClientCallbackInterface;
import sparta.realm.Services.DatabaseManager;
import sparta.realm.spartamodels.percent_calculation;

import static sparta.realm.Realm.realm;

public class RealmClientProtocol extends RealmSocketProtocol{
    @Override
    public void OnDataReceived(byte[] remote_data) {
        super.OnDataReceived(remote_data);
        ProcessInputReceived(new String(remote_data));
    }

    @Override
    public void Authenticate(String device_code, String username, String password) {
        super.Authenticate(device_code, username, password);
        SendMessageJ(null,"1",device_code,username,password);

    }

    //    public native int InitializeClient(String server_ip,int port,String device_code,String username,String password);

    public void ProcessInputReceived(String data){


String[] data_segments=data.split(delimeter);
String transaction_no=data_segments[0];
String requestType=data_segments[1];

switch (requestType){
    case "1":

rch.onAuthenticated(this,data_segments[2].equals("1"));
        dpm.addTransaction(sparta.realm.realmclient.dataProcess.transferTypeRx,transaction_no, sparta.realm.realmclient.dataProcess.serviceTypeAuth,null,data);
        break;
 case "4":
     String datarequestinstruction=data_segments[2];
     String service_id=data_segments[3];
     switch (datarequestinstruction){
         case "1"://data received
             String ddata=data_segments[4];

             rch.onDataDownloaded(this,transaction_no,service_id,ddata);
             dpm.addTransaction(sparta.realm.realmclient.dataProcess.transferTypeRx,transaction_no, sparta.realm.realmclient.dataProcess.serviceTypeIo,null,data);

             break;
         case "2"://data uploaded

             String upload_result=data_segments[4];

             rch.onDataUploaded(this,service_id,upload_result);
             dpm.addTransaction(sparta.realm.realmclient.dataProcess.transferTypeRx,transaction_no, sparta.realm.realmclient.dataProcess.serviceTypeIo,null,data);

             break;
         case "3"://data available for download

             rch.onRequestToDownloadReceived(this,transaction_no,service_id);
             dpm.addTransaction(sparta.realm.realmclient.dataProcess.transferTypeRx,transaction_no, sparta.realm.realmclient.dataProcess.serviceTypeIo,null,data);
             break;
     }



     break;
}
data=null;
//        Log.e(rc.logTag,"Requested to download "+sr.service_name);

        calc_progress();
    }
  ////////////////////////////////////////////////JAVA///////////////////////////////////////


    class dataProcessManager{
        public ArrayList<sparta.realm.realmclient.dataProcess> all_processes=new ArrayList<>();
        void addTransaction(int transferType,String transaction_no,int serviceType,sync_service_description ssd,String data){

            sparta.realm.realmclient.dataProcess dp=new sparta.realm.realmclient.dataProcess(transferType,transaction_no,serviceType,ssd,data);
//            all_processes.add(dp);


        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        void setTransactionTransferStatus(String transactionNo,int transactionTransferStatus){

            List<sparta.realm.realmclient.dataProcess> tr =
                    all_processes.stream()
                            .filter(p-> p.transaction_no.equals((transactionNo)))
                            .collect(Collectors.toList());
            all_processes.remove(tr.get(0));
            tr.get(0).transfer_status=transactionTransferStatus;
            all_processes.add(tr.get(0));
        }
    }

    String delimeter="";
    public String device_code, username,password;
RealmClientComHandler rch=new RealmClientComHandler() {};
    public dataProcessManager dpm=new dataProcessManager();
    public RealmClientCallbackInterface realmClientInterfaceTX ;
SocketClient sc;

    public RealmClientProtocol(SocketClient sc,RealmClientCallbackInterface realmClientInterfaceTX ){
        super(realmClientInterfaceTX);
        this.realmClientInterfaceTX=realmClientInterfaceTX;
this.sc=sc;

    }
   public void registerCallBack( RealmClientCallbackInterface realmClientInterfaceTX ){
        this.realmClientInterfaceTX=realmClientInterfaceTX;


    }


public static String logTag="Realm client Java";
  public   int io_operations_counter=0;
    public int io_operation_complete_counter=0;


    interface  RealmClientComHandler{

        default void onRequestToDownloadReceived(RealmClientProtocol rc, String tx_transaction_no, String service_id){
             sync_service_description sr= Realm.realm.getHashedSyncDescriptions().get(service_id);//should include service id to get one syncdesc
          if(sr==null){
              Log.e(rc.logTag,"RX prompt on an unimplemented service "+service_id);

          }
            Log.e(rc.logTag,"RX prompt on "+sr.service_name);

            if(sr.service_id!=null&&!sr.service_id.equals("null")) {
                try {
                    rc.realmClientInterfaceTX.on_info_updated(sr.service_name +" data available");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                rc.download(tx_transaction_no,sr);

            } else {
              Log.e(rc.logTag,"Service implementation not available locally");
          }
        }
        default void onAuthenticated(RealmClientProtocol rc, boolean authentication_status){
            try {
                rc.realmClientInterfaceTX.on_info_updated(authentication_status?"Authenticated":"Authentication failed");
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if(authentication_status) {
                Log.e(logTag,"Authenticated  Synchronizing");
                rc.Synchronize();

            }else{
                Log.e(logTag,"Authentication failed");

            }

        }

        default void onDataDownloaded(RealmClientProtocol rc, String transaction_no, String service_id, String data){
//            Log.e(rc.logTag,"Data been downloaded "+data);

            sync_service_description ssd= realm.getHashedSyncDescriptions().get(service_id);
if(ssd==null) {

}else {
try {


//    JSONArray temp_ar = (JSONArray) SynchronizationManager.getJsonValue(ssd.download_array_position, new JSONObject(data));
    Log.e(RealmClientProtocol.logTag,"Converting "+ssd.service_name+" JSON");
    JSONArray temp_ar = new JSONArray(data);
    data=null;
//    LinkedHashSet<E> hashSet = new LinkedHashSet<E>();
   if(ssd.storage_mode_check){
        Log.e(RealmClientProtocol.logTag,"Started storage checking ...");

       for (int i =0;i<temp_ar.length();i++) {
           JSONObject jo = temp_ar.getJSONObject(i);
           Iterator keys = jo.keys();
           List<String> key_list = new ArrayList<>();
           while (keys.hasNext()) {
               key_list.add((String) keys.next());

           }


//        Log.e(RealmClient.logTag,"Keys to save to file  "+realm.getFilePathFields(ssd.object_package,key_list));
           for(String k:realm.getFilePathFields(ssd.object_package,key_list)) {
               try {
                   jo.put(k, DatabaseManager.save_doc(jo.getString(k)));
               }catch (Exception e){
                   Log.e(RealmClientProtocol.logTag,"Base64 image error:"+e.getMessage());

               }

           }
       }
       Log.e(RealmClientProtocol.logTag,"Done storage checking ...");

   }

        double den = (double) temp_ar.length();
     if (!ssd.use_download_filter) {
        DatabaseManager.database.execSQL("DELETE FROM " + ssd.table_name + " WHERE sync_status ='" + sync_status.syned.ordinal() + "'");
    }

//    temp_ar=new JSONArray(rc.realmClientInterfaceTX.OnDownloadedObjects(service_id,temp_ar.toString()));
    temp_ar = new JSONArray(temp_ar.toString().replace("'", "''"));
    Log.e(ssd.service_name + " :: RX", "Inserting " + den);
    if (den > 0) {
        synchronized (DatabaseManager.database) {
            String[][] ins = realm.getInsertStatementsFromJson(temp_ar, ssd.object_package);
            String sidz = ins[0][0];
            String sidz_inactive = ins[0][1];
            String[] qryz = ins[1];
            int q_length = qryz.length;
            temp_ar = null;

            // ssi.on_status_changed("Synchronizing " + ssd.service_name);

            DatabaseManager.database.beginTransaction();
            DatabaseManager.database.execSQL("INSERT INTO CP_" + ssd.table_name + " SELECT * FROM " + ssd.table_name + " WHERE sid in " + sidz + " AND sync_status=" + sync_status.pending.ordinal() + "");

            for (int i = 0; i < q_length; i++) {
                DatabaseManager.database.execSQL(qryz[i]);
                double num = (double) i + 1;
                double per = (num / q_length) * 100.0;
                try {
                    rc.realmClientInterfaceTX.on_secondary_progress_changed((int)per);
                }catch (Exception e){}
                    //  ssi.on_secondary_progress_changed((int) per);

//                  ssi.on_info_updated(ssd.service_name + " :" + num + "/" + q_length + "    Local data :" + Integer.parseInt(new DatabaseManager(Realm.context).get_record_count(ssd.table_name, ssd.table_filters)));
            }

            DatabaseManager.database.execSQL("DELETE FROM " + ssd.table_name + " WHERE data_status='false'");
//                                         DatabaseManager.database.execSQL("DELETE FROM " + ssd.table_name + " WHERE sid IN("+sidz_inactive+")AND sync_status<>" + sync_status.pending.ordinal());
//                                         DatabaseManager.database.execSQL("DELETE FROM " + ssd.table_name + " WHERE sid IN("+DatabaseManager.conccat_sql_string(sidz_inactive)+")AND sync_status<>" + sync_status.pending.ordinal());
            DatabaseManager.database.execSQL("REPLACE INTO " + ssd.table_name + " SELECT * FROM CP_" + ssd.table_name + "");
            DatabaseManager.database.execSQL("DELETE FROM CP_" + ssd.table_name + "");
            DatabaseManager.database.setTransactionSuccessful();
            DatabaseManager.database.endTransaction();

        }
    }
    if(den>=ssd.chunk_size){
        try {
            rc.realmClientInterfaceTX.on_info_updated("Downloaded "+ssd.service_name+". Re-downloading next batch");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        rc.download(transaction_no,ssd);
    }else {
        try {
            rc.realmClientInterfaceTX.on_info_updated("Synchronized");
            rc.realmClientInterfaceTX.onServiceSynchronizationCompleted(ssd.service_id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    Log.e(ssd.service_name + "RX", "Inserted " + den);

}catch (Exception ex) {
//    Log.e(rc.logTag,"Data insert error "+ex);
    DatabaseManager.log_String("Data insert error "+ex);

}
}

            rc.io_operation_complete_counter++;
        }
         default void _onDataUploaded(RealmClientProtocol rc, String service_id, String data){

             sync_service_description ssd= realm.getHashedSyncDescriptions().get(service_id);
             if(ssd==null) {

             }else {
                 try {
                     JSONObject res=new JSONObject(data);
                     ContentValues cv=new ContentValues();
                     cv.put("sync_var",res.getString("id"));
                     cv.put("sync_status",sync_status.syned.ordinal());
                     Log.e(rc.logTag, "Updated :"+DatabaseManager.database.update(ssd.table_name,cv,"transaction_no='"+res.getString("transaction_no")+"'",null));


                 } catch (Exception ex) {
                     Log.e(rc.logTag, "Error updating  :"+ex.getMessage());

                 }
                 try {
                     rc.realmClientInterfaceTX.on_info_updated("Uploaded: "+ssd.service_name);
                 } catch (RemoteException e) {
                     e.printStackTrace();
                 }

                     rc.upload(ssd);

             }

             rc.io_operation_complete_counter++;
                 }
         default void onDataUploaded(RealmClientProtocol rc, String service_id, String data){
             rc.updateUploadedData(service_id,data);

                 }
        default void onConnected(int service_id){ }
    }
     void updateUploadedData_(String service_id,String data){
         sync_service_description ssd= realm.getHashedSyncDescriptions().get(service_id);
         if(ssd==null) {

         }else {
             try {
                 JSONArray arr=new JSONArray(data);
                 for(int i=0;i<arr.length();i++){
                     JSONObject res=arr.getJSONObject(i);
                     ContentValues cv=new ContentValues();
                     cv.put("sync_var",res.getString("id"));
                     cv.put("sync_status",sync_status.syned.ordinal());
                     Log.e(logTag, "Updated :"+DatabaseManager.database.update(ssd.table_name,cv,"transaction_no='"+res.getString("transaction_no")+"'",null));

                 }


             } catch (Exception ex) {
                 Log.e(logTag, "Error updating  :"+ex.getMessage());

             }
             try {
                 realmClientInterfaceTX.on_info_updated("Uploaded "+ssd.service_name);
             } catch (RemoteException e) {
                 e.printStackTrace();
             }

             upload(ssd);

         }

         io_operation_complete_counter++;
     }
   void updateUploadedData(String service_id,String data){

       io_operation_complete_counter++;
       sync_service_description ssd= realm.getHashedSyncDescriptions().get(service_id);
        if(ssd==null) {

        }else {
            try {
                JSONArray arr=new JSONArray(data);
                if(arr.length()>0) {
                    Log.e(logTag, "Updating "+arr.length());
                    StringBuilder sbqry = new StringBuilder();
                    sbqry.append("WITH RealmClientResult(transaction_no, sid, sync_status) AS (VALUES");
                    String[] sids=new String[arr.length()];
                    for (int i = 0; i < arr.length(); i++) {
                        sbqry.append((i != 0) ? "," : "");
                        JSONObject res = arr.getJSONObject(i);
                        sbqry.append("('");
                        sbqry.append(res.getString("transaction_no"));
                        sbqry.append("','");
                        sbqry.append(res.getString("id"));
                        sbqry.append("',");
                        sbqry.append(sync_status.syned.ordinal());
                        sbqry.append(")");
                        sids[i]=res.getString("id");
                    }
                    sbqry.append(") UPDATE " + ssd.table_name + " SET " +
                            "  sid = (SELECT sid FROM RealmClientResult WHERE " + ssd.table_name + ".transaction_no = RealmClientResult.transaction_no)," +
                            "  sync_status = (SELECT sync_status FROM RealmClientResult WHERE " + ssd.table_name + ".transaction_no = RealmClientResult.transaction_no)\n" +
                            "WHERE transaction_no IN (SELECT transaction_no FROM RealmClientResult)");
//                    DatabaseManager.database.execSQL("DELETE FROM "+ssd.table_name+" WHERE sync_status='"+sync_status.syned.ordinal()+"' AND transaction_no IN ("+DatabaseManager.conccat_sql_string(transactions)+")");
                    DatabaseManager.database.execSQL("DELETE FROM "+ssd.table_name+" WHERE sid IN ("+DatabaseManager.conccat_sql_string(sids)+")");
                    DatabaseManager.database.execSQL(sbqry.toString());
                    Log.e(logTag, "Updated OK "+arr.length());
                }



            } catch (Exception ex) {
                Log.e(logTag, "Error updating bulk :"+ex.getMessage());
            try {
                JSONArray arr = new JSONArray(data);
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        JSONObject res = arr.getJSONObject(i);
                    ContentValues cv = new ContentValues();
                    cv.put("sid", res.getString("id"));
                    cv.put("sync_status", ""+sync_status.syned.ordinal());
                    int update_result=DatabaseManager.database.update(ssd.table_name, cv, "transaction_no='" + res.getString("transaction_no") + "'", null);
                    Log.e(logTag, "Updated :" + update_result);

                }catch (Exception ex2){
                        Log.e(logTag, "Error updating single :"+ex2.getMessage());

                }
                }
            }catch (Exception ex2){
                Log.e(logTag, "Error updating single :"+ex2.getMessage());

            }
            }
            try {
                realmClientInterfaceTX.on_info_updated("Uploaded "+ssd.service_name);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            upload(ssd);

        }

    }

    public void downloadAll (){
        for (Map.Entry<String, sync_service_description> e : Realm.realm.getHashedSyncDescriptions().entrySet()) {
           if( e.getValue().service_id!=null&&!e.getValue().service_id.equals("null"))
           {
               if(e.getValue().servic_type== SyncDescription.service_type.Download) {
                   Log.e(logTag, "Downloading :" + e.getValue().service_name);
                   download(null, e.getValue());
               }
           }else
               {
               Log.e(logTag,"Service implementation not available locally");
               }

        }
    }
    public void uploadAll (){
        for (Map.Entry<String, sync_service_description> e : Realm.realm.getHashedSyncDescriptions().entrySet()) {
            if( e.getValue().service_id!=null&&!e.getValue().service_id.equals("null"))
            {
                if(e.getValue().servic_type== SyncDescription.service_type.Upload){
                    Log.e(logTag,"Uploading  :"+ e.getValue().service_name);
                    upload( e.getValue());
                }

            }else
            {
                Log.e(logTag,"Service implementation not available locally");
            }

        }
    }
  public void Synchronize (){
      io_operations_counter=0;
      io_operation_complete_counter=0;
                         Log.e(logTag,"Synchronizing ...");

      try {
          realmClientInterfaceTX.on_info_updated("Syncing ");
      } catch (RemoteException e) {
          e.printStackTrace();
      }
        for (Map.Entry<String, sync_service_description> e : Realm.realm.getHashedSyncDescriptions().entrySet()) {
           if( e.getValue().service_id!=null&&!e.getValue().service_id.equals("null"))
           {
               if(e.getValue().servic_type== SyncDescription.service_type.Download){
//                   Log.e(logTag,"Downloading :"+ e.getValue().service_name);
                   download(null, e.getValue());
                }else if(e.getValue().servic_type== SyncDescription.service_type.Upload){
//                   Log.e(logTag,"Uploading  :"+ e.getValue().service_name);
                   upload( e.getValue());
               }

           }else
               {
               Log.e(logTag,"Service implementation not available locally");
               }

        }
    }
   public void download (String tx_transaction_no,sync_service_description ssd){

       try {
           realmClientInterfaceTX.on_info_updated("Downloading "+ssd.service_name);
       } catch (RemoteException e) {
           e.printStackTrace();
       }
       io_operations_counter++;
       Log.e(logTag,"Downloading "+ssd.service_name+"  "+io_operations_counter+"/"+io_operation_complete_counter);
//       calc_progress();
       try {
           String o=realmClientInterfaceTX.OnAboutToDownloadObjects(ssd.service_id);
           SendMessageJ(tx_transaction_no,"4","2",ssd.service_id+"",Realm.databaseManager.greatest_sync_var(ssd.table_name),""+ssd.chunk_size,o);
           dpm.addTransaction(sparta.realm.realmclient.dataProcess.transferTypeTx,tx_transaction_no, sparta.realm.realmclient.dataProcess.serviceTypeIo,ssd,(tx_transaction_no==null?"R"+System.currentTimeMillis()+"S":tx_transaction_no)+delimeter+DatabaseManager.concatRealmClientString(delimeter,new String[]{tx_transaction_no,"4",ssd.service_id+"","2",Realm.databaseManager.greatest_sync_var(ssd.table_name),""+ssd.chunk_size,o}));
       } catch (RemoteException e) {
           e.printStackTrace();
       }

    }
       public void uploadSingle (sync_service_description ssd){
        try {
            realmClientInterfaceTX.on_info_updated("Uploading "+ssd.service_name);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        String transaction_no="R"+System.currentTimeMillis()+"S";
        String [] pending_records_filter=ssd.table_filters==null?new String[1]:new String[ssd.table_filters.length+1];
        if(ssd.table_filters!=null)
        {
            System.arraycopy(ssd.table_filters,0,pending_records_filter,0,ssd.table_filters.length);

        }
        pending_records_filter[pending_records_filter.length-1]="sync_status='"+ sync_status.pending.ordinal()+"'";

        ArrayList<JSONObject> pending_records= Realm.databaseManager.load_dynamic_json_records_ann(ssd,pending_records_filter);


        for(JSONObject jo:pending_records){

            try {
                jo.put("filters",new JSONArray(ssd.uploadConstrainFields));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            io_operations_counter++;
            SendMessageJ(transaction_no,"4","1",ssd.service_id+"",jo.toString());

        }
//        dpm.addTransaction(dataProcess.transferTypeTx,transaction_no,dataProcess.serviceTypeIo,ssd,transaction_no+""+DatabaseManager.concatRealmClientString(delimeter,new String[]{tx_transation_no,"4",ssd.service_id+"","2",Realm.databaseManager.greatest_sync_var(ssd.table_name),""+ssd.chunk_size}));


    }
      public void upload (sync_service_description ssd) {
      uploadArray(ssd);
      }
      public void uploadArray (sync_service_description ssd){
        try {
            realmClientInterfaceTX.on_info_updated("Uploading "+ssd.service_name);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        String transaction_no="R"+System.currentTimeMillis()+"S";
        String [] pending_records_filter=ssd.table_filters==null?new String[1]:new String[ssd.table_filters.length+1];
        if(ssd.table_filters!=null)
        {
            System.arraycopy(ssd.table_filters,0,pending_records_filter,0,ssd.table_filters.length);

        }
        pending_records_filter[pending_records_filter.length-1]="sync_status='"+ sync_status.pending.ordinal()+"'";

        ArrayList<JSONObject> pending_records= Realm.databaseManager.load_dynamic_json_records_ann(ssd,pending_records_filter);
JSONArray arr=new JSONArray();

        for(JSONObject jo:pending_records){

            try {
                jo.put("filters",new JSONArray(ssd.uploadConstrainFields));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            io_operations_counter++;

            arr.put(jo);
        }
          if(ssd.storage_mode_check) {
              Log.e(RealmClientProtocol.logTag, "Started storage checking ...");

              for (int i = 0; i < pending_records.size(); i++) {
                  JSONObject jo = pending_records.get(i);
                  Iterator keys = jo.keys();
                  List<String> key_list = new ArrayList<>();
                  while (keys.hasNext()) {
                      key_list.add((String) keys.next());

                  }


//        Log.e(RealmClient.logTag,"Keys to save to file  "+realm.getFilePathFields(ssd.object_package,key_list));
                  for (String k : realm.getFilePathFields(ssd.object_package, key_list)) {
                      try {
                          jo.put(k, DatabaseManager.get_saved_doc_base64(jo.getString(k)));
                      } catch (Exception e) {
                          Log.e(RealmClientProtocol.logTag, "Base64 image error:" + e.getMessage());

                      }

                  }
              }
          }
        if(arr.length()>0){
            SendMessageJ(transaction_no,"4","1",ssd.service_id+"",arr.toString());
        }else{
            try {
                realmClientInterfaceTX.on_info_updated("Synchronized");
                realmClientInterfaceTX.onServiceSynchronizationCompleted(ssd.service_id);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
//        dpm.addTransaction(dataProcess.transferTypeTx,transaction_no,dataProcess.serviceTypeIo,ssd,transaction_no+""+DatabaseManager.concatRealmClientString(delimeter,new String[]{tx_transation_no,"4",ssd.service_id+"","2",Realm.databaseManager.greatest_sync_var(ssd.table_name),""+ssd.chunk_size}));


    }


    public void SendMessageJ (String tx_transation_no,String... data){
        sc.sendData((tx_transation_no==null?"R"+System.currentTimeMillis()+"S":tx_transation_no)+delimeter+DatabaseManager.concatRealmClientString(delimeter,data));
        calc_progress();
//Log.e(logTag,"TX :"+((tx_transation_no==null?"R"+System.currentTimeMillis()+"S":tx_transation_no)+delimeter+DatabaseManager.concatRealmClientString(delimeter,data)));
    }
public void calc_progress(){
    percent_calculation pc = new percent_calculation(io_operations_counter + "", io_operation_complete_counter + "");
    Log.e(logTag,"Progress: "+io_operation_complete_counter+" / "+ io_operations_counter);
    try {
        realmClientInterfaceTX.on_main_percentage_changed(Integer.parseInt(pc.per_balance));
        if(Integer.parseInt(pc.per_balance)==100){
            realmClientInterfaceTX.on_info_updated("Sync complete");
            realmClientInterfaceTX.onSynchronizationCompleted();
        }
    } catch (RemoteException e) {
        e.printStackTrace();
    }
}

}
