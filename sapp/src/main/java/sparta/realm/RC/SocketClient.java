package sparta.realm.RC;

import com.realm.annotations.sync_service_description;

import sparta.realm.RealmClientCallbackInterface;

public class SocketClient {

    public String SERVER_ADDR = "192.168.0.105";
    public int SERVER_PORT = 8889;
//    public int SERVER_READTIMEOUT = 5000;
    public int SERVER_READTIMEOUT = 10000;
    public String device_code, username,password;

   public RealmClientCallbackInterface realmClientInterfaceTX;
   public RealmSocketProtocol rsp;

    public SocketClient(RealmClientCallbackInterface realmClientInterfaceTX){
        this.realmClientInterfaceTX=realmClientInterfaceTX;
    }
    public void registerCallBack( RealmClientCallbackInterface realmClientInterfaceTX ){
        this.realmClientInterfaceTX=realmClientInterfaceTX;


    }
    public void sendData(String data){

    }
   public int InitializeSocket(String server_ip,int port,String device_code,String username,String password){
       SERVER_ADDR=server_ip;
       SERVER_PORT=port;
       this.device_code=device_code;
       this.username=username;
       this.password=password;
        return 0;
    }
 public void sendData(byte[] data){

    }


    public void downloadAll (){

rsp.downloadAll();
    }

    public void uploadAll (){
rsp.uploadAll();

    }


    public void Synchronize (){

rsp.Synchronize();
    }


    public void download (String tx_transaction_no, sync_service_description ssd){
        rsp.download(tx_transaction_no,ssd);

    }


    public void upload (sync_service_description ssd){

        rsp.upload(ssd);
    }

    public void uploadArray (sync_service_description ssd){

        rsp.uploadArray(ssd);
    }
}
