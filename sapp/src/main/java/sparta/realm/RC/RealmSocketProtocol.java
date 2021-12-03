package sparta.realm.RC;

import com.realm.annotations.sync_service_description;

import sparta.realm.RealmClientCallbackInterface;
import sparta.realm.realmclient.RealmClient;

public class RealmSocketProtocol implements SocketInterface{

    public String device_code, username,password;

   public RealmClientCallbackInterface realmClientInterfaceTX ;


    public RealmSocketProtocol(RealmClientCallbackInterface realmClientInterfaceTX ){
        this.realmClientInterfaceTX=realmClientInterfaceTX;
    }

    public void registerCallBack( RealmClientCallbackInterface realmClientInterfaceTX ){
        this.realmClientInterfaceTX=realmClientInterfaceTX;
    }

    public void downloadAll (){


    }

    public void uploadAll (){


    }


  public void Synchronize (){


    }


  public void download (String tx_transaction_no, sync_service_description ssd){


    }


  public void upload (sync_service_description ssd){


    }

 public void uploadArray (sync_service_description ssd){


    }


    @Override
    public void OnDataReceived(byte[] remote_data) {

    }



}
