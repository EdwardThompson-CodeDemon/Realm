package sparta.realm.realmclient;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.realm.annotations.RealmDataClass;
import com.realm.annotations.sync_service_description;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import sparta.realm.Realm;
import sparta.realm.Services.DatabaseManager;
import sparta.realm.spartamodels.member;

public class RealmClient {

    static {
        System.loadLibrary("RealmSocket");
    }
    public native String stringFromJNI();
    public native int addFromJNI(int a,int b);
    public void MessageReceived(String data){
Log.e("Rx Java :",data);
//SendMessageJ(null,"412010000");
//SendMessageJ(null,"4","1","2","0","10000");
String[] data_segments=data.split(delimeter);
String transaction_no=data_segments[0];
String requestType=data_segments[1];
switch (requestType){
    case "1":

rch.onAuthenticated(this,data_segments[2].equals("1"));
        dpm.addTransaction(dataProcess.transferTypeRx,transaction_no,dataProcess.serviceTypeAuth,null,data);
        break;
 case "4":


     rch.onRequestToDownloadReceived(this,transaction_no,data_segments[1]);
     dpm.addTransaction(dataProcess.transferTypeRx,transaction_no,dataProcess.serviceTypeIo,null,data);

     break;
}
data=null;
    }
    public native int SendMessage(String data);
    public native int InitializeClient(String server_ip,int port,String device_code,String username,String password);
////////////////////////////////////////////////JAVA///////////////////////////////////////


    class dataProcessManager{
        public ArrayList<dataProcess> all_processes=new ArrayList<>();
        void addTransaction(int transferType,String transaction_no,int serviceType,sync_service_description ssd,String data){

            dataProcess dp=new dataProcess(transferType,transaction_no,serviceType,ssd,data);
            all_processes.add(dp);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        void setTransactionTransferStatus(String transactionNo,int transactionTransferStatus){

            List<dataProcess> tr =
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


    public RealmClient (String device_code,String username,String password){
        this.device_code=device_code;
        this.username=username;
        this.password=password;


    }
 public RealmClient (String device_code,String username,String password,RealmClientComHandler rch){
        this.device_code=device_code;
        this.username=username;
        this.password=password;
        this.rch=rch;


    }

String logTag="Realm client Java";



    interface  RealmClientComHandler{

        default void onRequestToDownloadReceived(RealmClient rc,String tx_transaction_no,String service_id){
        sync_service_description sr= Realm.realm.getHashedSyncDescriptions().get(service_id);//should include service id to get one syncdesc
          if(sr.service_id!=null&&!sr.service_id.equals("null"))  rc.download(tx_transaction_no,sr); else Log.e(rc.logTag,"Service implementation ot available locally");
        }
        default void onAuthenticated(RealmClient rc, boolean authentication_status){
            if(authentication_status) {
                Log.e("Auth :","Authenticated \n Downloading all");
                rc.downloadAll();

            }else{
                Log.e("Auth :","Authentication failed");

            }

        }
        default void onDataDownloaded(int service_id){ }
         default void onDataUploaded(int LIST_OF_transaction_no_and_sid_or_rsid){ }
        default void onConnected(int service_id){ }
    }

    public void downloadAll (){
        for (Map.Entry<String, sync_service_description> e : Realm.realm.getHashedSyncDescriptions().entrySet()) {
           if( e.getValue().service_id!=null&&!e.getValue().service_id.equals("null")) download(null, e.getValue()); else Log.e(logTag,"Service implementation not available locally");
        }
    }
   public void download (String tx_transation_no,sync_service_description ssd){
        SendMessageJ(tx_transation_no,"4",ssd.service_id+"","2",Realm.databaseManager.greatest_sync_var(ssd.table_name),""+ssd.chunk_size);
dpm.addTransaction(dataProcess.transferTypeTx,tx_transation_no,dataProcess.serviceTypeIo,ssd,(tx_transation_no==null?"R"+System.currentTimeMillis()+"S":tx_transation_no)+""+DatabaseManager.concatRealmClientString(delimeter,new String[]{tx_transation_no,"4",ssd.service_id+"","2",Realm.databaseManager.greatest_sync_var(ssd.table_name),""+ssd.chunk_size}));
    }
    public void SendMessageJ (String tx_transation_no,String... data){
        SendMessage((tx_transation_no==null?"R"+System.currentTimeMillis()+"S":tx_transation_no)+""+DatabaseManager.concatRealmClientString(delimeter,data));

    }

    public void upload (int service_id,String data){
        this.device_code=device_code;
        this.username=username;
        this.password=password;

    }


}
