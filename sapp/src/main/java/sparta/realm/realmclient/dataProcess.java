package sparta.realm.realmclient;

import com.realm.annotations.sync_service_description;

public class dataProcess {
    public String transaction_no;
    public sync_service_description ssd;
    public int transferType=0;
    public int transfer_status=0;
    public int serviceType=0;
    public String data="";
    public static int serviceTypeAuth=1;
    public static int serviceTypeIo=2;
    public static int transferTypeTx=1;
    public static int transferTypeRx=2;
    public static int transferStatusRequested=1;
    public static int transferStatusCompleted=2;
    public static int transferStatusCanceled=3;
    public dataProcess(int transferType,String transaction_no,int serviceType,sync_service_description ssd,String data){
        this.transferType=transferType;
        this.transaction_no=transaction_no;
        this.serviceType=serviceType;
        this.transferType=transferType;
        this.ssd=ssd;
        this.data=data;
    }
}
