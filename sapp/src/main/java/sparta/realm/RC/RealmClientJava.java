package sparta.realm.RC;

import android.content.Context;
import android.util.Log;

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


    public boolean mRun = false;

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
        mRun = false;
    }

    public void run() {

        mRun = true;

        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_ADDR);

            Log.e(log_tag, "Connecting... " );

            Boolean connected_to_server = false;
            Socket socket = new Socket(serverAddr, SERVER_PORT);


            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out_d = new DataOutputStream(socket.getOutputStream());
                in_d = new DataInputStream(socket.getInputStream());

                Log.e(log_tag, "Connected");
                 authenticate();

                while (mRun) {
                    if (socket.isClosed()) {
                        mRun = false;
                        Log.e(log_tag, " Session Disconnected");
                        continue;
                    }
//                    serverMessage = in.();
                  int input_size=  in_d.readInt();
                    Log.e(log_tag, " Input size "+input_size);
                    byte[] message = new byte[input_size];
                    in_d.readFully(message, 0, message.length); // read the message
                    rsp.OnDataReceived(message);
//                    serverMessage = new String(message);
//                    Log.e(log_tag, " Input "+serverMessage);
//                    serverMessage = null;

                }

                Log.e(log_tag, " Session closed by server");

            } catch (Exception e) {


                Log.e("TCP", "S: Error", e);
                mRun = false;
            } finally {
                if (connected_to_server == false) {
//                    communicationHandler.onConnectionToServer(connected_to_server);

                }
                socket.close();
            }
            if (connected_to_server == false) {
//                communicationHandler.onConnectionToServer(connected_to_server);

            }

        } catch (Exception e) {

            mRun = false;
            Log.e(log_tag, "Connection Error\nAborting connection", e);

        }

    }


    <T> ArrayList<T> deserialize(String INPUT) {
        //0554954980


        return new ArrayList<T>();

    }

    void authenticate() {

sendMessage("581816800\u001E1\u001E0\u001Edemo\u001Edemo123");
    }

    @Override
    public void sendData(String data) {
        super.sendData(data);
        sendMessage(data);
    }

    public void sendMessage(String message){
        Log.e(log_tag,"TX : "+message);
        byte[] len=ByteBuffer.allocate(4).putInt(message.length()).array();
        Log.e(log_tag,"TX : "+message.length());
     try {
         out_d.write(len);
         out_d.write(message.getBytes());
     } catch (IOException e) {
         e.printStackTrace();
     }

    }



}
