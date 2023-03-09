package sparta.realm.RC;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import sparta.realm.Realm;
import sparta.realm.RealmClientCallbackInterface;


public class RealmClient extends SocketClient {


    public boolean keepReading = false;

    private String serverMessage;



    Context context;

    PrintWriter out;
    BufferedReader in_b;
    DataInputStream in_d;
    DataOutputStream out_d;
    InputStream in;
String log_tag="Realm Client :";

     public RealmClient(RealmClientCallbackInterface realmClientInterfaceTX) {
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
            socket.setSoTimeout(SERVER_READTIMEOUT);//should be for blocking socs//but now i need to constantly reconnect
//            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            in=socket.getInputStream();
            out_d = new DataOutputStream(socket.getOutputStream());
//            in_d = new DataInputStream(in);
            in_d = new DataInputStream(new BufferedInputStream(in));

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
                  int string_size=  in_d.readInt();

                    Log.e(log_tag, "RX len: "+input_size);
                    Log.e(log_tag, "STR len: "+string_size);
                    Log.e(log_tag, "files len: "+(input_size-string_size));
                    byte[] message = new byte[input_size];
                    in_d.readFully(message, 0, message.length);
                    if(input_size==string_size)
                    {
                        rsp.OnDataReceived(message);

                    }else{


                    }
//                    ByteStreams.readFully(in,message,0,message.length);
                    Log.e(log_tag, "RX OK. Processing ... ");

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
    //╤╤1,100000
    String filePlaceHolderPrefix="╤╤";
long[] filesSizesFromRawString(String input)
{

    long[] lo=new long[0];
    int index = 0;
    while(index != -1){
        index = input.indexOf(filePlaceHolderPrefix, index);
        if (index != -1) {
//            indexes.add(index);
            index++;
        }
    }

    return lo;


}

    <T> ArrayList<T> deserialize(String INPUT) {
        //0554954980


        return new ArrayList<T>();

    }

    void authenticate() {

    }


 @Override
    public void sendData(String data,ArrayList<File> files) {
        super.sendData(data);
//        sendMessage(data);
    }

    public void sendMessage(String message,int total_files_size,ArrayList<File> files){
        Log.e(log_tag,"TX: "+message);
        byte[] strLen=ByteBuffer.allocate(4).putInt(message.length()).array();
        byte[] totalLen=ByteBuffer.allocate(4).putInt(message.length()+total_files_size).array();
        Log.e(log_tag,"TX len: "+message.length());
     try {
         out_d.write(totalLen);
         out_d.write(strLen);
         out_d.write(message.getBytes());
         if (total_files_size>0&&files!=null&&files.size()>0) {
             for (File file : files) {
                 try (
                         FileInputStream in = new FileInputStream(file);
                         java.nio.channels.FileLock lock = new RandomAccessFile(file, "rw").getChannel().lock();
                         BufferedInputStream fileReader = new BufferedInputStream(in)
                 ) {
                     byte[] buffer = new byte[4096];

                     int bytesRead = fileReader.read(buffer, 0, buffer.length);
                     while (bytesRead > 0) {
                         out_d.write(buffer, 0, bytesRead);
                         bytesRead = fileReader.read(buffer, 0, buffer.length);
                     }
                 }
//             try(BufferedInputStream fileReader=new BufferedInputStream(new FileInputStream(file))){
//                 byte[] buffer = new byte[4096];
//
//                 int bytesRead = fileReader.read(buffer, 0, buffer.length);
//                 while(bytesRead > 0) {
//                     out_d.write(buffer, 0, bytesRead);
//                     bytesRead = fileReader.read(buffer, 0, buffer.length);
//                 }
//
//             }

             }
         }
     } catch (IOException e) {
         e.printStackTrace();
         Log.e(log_tag,"TX exception: "+message.length());
         keepReading =false;

//         run();
     }

    }

  public void sendMessage(String message,ArrayList<File> files){
        Log.e(log_tag,"TX: "+message);
        int totalFileSize=0;
      if (files!=null&&files.size()>0) {
          for (File file : files) {
              if(file.exists()){
                  totalFileSize+=file.length();
              }else{
                  //Any missing file then return
                  return;
              }
          }
          }

          byte[] strLen=ByteBuffer.allocate(4).putInt(message.length()).array();
        byte[] totalLen=ByteBuffer.allocate(4).putInt(message.length()+totalFileSize).array();
        Log.e(log_tag,"TX len: "+message.length());
     try {
         out_d.write(totalLen);
         out_d.write(strLen);
         out_d.write(message.getBytes());
         if (totalFileSize>0) {
             for (File file : files) {
                 try (
                         FileInputStream in = new FileInputStream(file);
                         java.nio.channels.FileLock lock = new RandomAccessFile(file, "rw").getChannel().lock();
                         BufferedInputStream fileReader = new BufferedInputStream(in)
                 ) {
                     byte[] buffer = new byte[4096];

                     int bytesRead = fileReader.read(buffer, 0, buffer.length);
                     while (bytesRead > 0) {
                         out_d.write(buffer, 0, bytesRead);
                         bytesRead = fileReader.read(buffer, 0, buffer.length);
                     }
                     lock.release();
                 }
//             try(BufferedInputStream fileReader=new BufferedInputStream(new FileInputStream(file))){
//                 byte[] buffer = new byte[4096];
//
//                 int bytesRead = fileReader.read(buffer, 0, buffer.length);
//                 while(bytesRead > 0) {
//                     out_d.write(buffer, 0, bytesRead);
//                     bytesRead = fileReader.read(buffer, 0, buffer.length);
//                 }
//
//             }

             }
         }
     } catch (IOException e) {
         e.printStackTrace();
         Log.e(log_tag,"TX exception: "+message.length());
         keepReading =false;

//         run();
     }

    }



}
