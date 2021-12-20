package sparta.realm.RC;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import sparta.realm.Realm;
import sparta.realm.RealmClientCallbackInterface;


public class RealmClientJava extends SocketClient {


    public boolean keepReading = false;

    private String serverMessage;



    Context context;

    PrintWriter out;
    BufferedReader in;
    DataInputStream in_d;
    DataOutputStream out_d;
String log_tag="Realm Client :";

     public RealmClientJava(RealmClientCallbackInterface realmClientInterfaceTX) {
         super(realmClientInterfaceTX);
         this.context = Realm.context;
rsp=new RealmClientProtocol(this,realmClientInterfaceTX);

    }
//  public RealmClientJava(Context context) {
//        this.context = context;
//
//    }


    @Override
    public int InitializeSocket(String server_ip, int port, String device_code, String username, String password) {



         super.InitializeSocket(server_ip, port, device_code, username, password);
         run();
return 1;

    }

    public void stopClient() {
        keepReading = false;
    }
//synchronized int dd=0;
    public void run() {

        keepReading = true;
        Socket socket;
        Boolean ok_to_read=false;
        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_ADDR);

            Log.e(log_tag, "Connecting ..." );
             socket = new Socket(serverAddr, SERVER_PORT);
            Log.e(log_tag, "Connected");
//            socket.setSoTimeout(SERVER_READTIMEOUT);//should be for blocking socs
//            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out_d = new DataOutputStream(socket.getOutputStream());
            in_d = new DataInputStream(socket.getInputStream());
//            in_d = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            Log.e(log_tag, "Authenticating ...");
//            authenticate();
            rsp.Authenticate(device_code,username,password);
ok_to_read=true;

        } catch (Exception e) {

            Log.e(log_tag, "Server connection error",e);
            return;
        }


            try {
                while (keepReading) {
                    if (socket.isClosed()) {
                       Log.e(log_tag, "Server connection closed !");
                        break;
                    }
                    realmClientInterfaceTX.on_status_changed("1");
//                    while(socket.getInputStream().available() > 0);
                  int input_size=  in_d.readInt();
                    Log.e(log_tag, "RX len: "+input_size);
                    byte[] message = new byte[input_size];
                    in_d.readFully(message, 0, message.length); // read the message
                    rsp.OnDataReceived(message);
                }

                Log.e(log_tag, "RX stopped !");

            } catch (Exception e) {
                Log.e(log_tag, "RX error:",e);
            } finally {
                try {
                   socket.close();
                   realmClientInterfaceTX.on_status_changed("0");
               }catch (Exception e){}
            }



        if(keepReading == false){
            Log.e(log_tag, "Connection aborted due to tx errors");

        }else{
            Log.e(log_tag, "Connection aborted due to rx errors");

        }

        try {
            realmClientInterfaceTX.on_status_changed("0");
        } catch (RemoteException remoteException) {
            remoteException.printStackTrace();
        }
//        run();
    }


    <T> ArrayList<T> deserialize(String INPUT) {
        //0554954980


        return new ArrayList<T>();

    }

    void authenticate() {

//sendMessage("581816800\u001E1\u001E0\u001Edemo\u001Edemo123");
sendMessage(System.currentTimeMillis()+"\u001E1\u001E0\u001Edemo\u001Edemo123");
    }

    @Override
    public void sendData(String data) {
        super.sendData(data);
        sendMessage(data);
    }

    public void sendMessage(String message){
        Log.e(log_tag,"TX: "+message);
        byte[] len=ByteBuffer.allocate(4).putInt(message.length()).array();
        Log.e(log_tag,"TX len: "+message.length());
     try {
         out_d.write(len);
         out_d.write(message.getBytes());
     } catch (IOException e) {
         e.printStackTrace();
         Log.e(log_tag,"TX exception: "+message.length());
         keepReading =false;

//         run();
     }

    }



}
