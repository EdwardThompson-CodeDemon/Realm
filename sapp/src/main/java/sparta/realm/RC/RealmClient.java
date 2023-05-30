package sparta.realm.RC;

import android.content.Context;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.util.Log;

import com.google.common.base.CharMatcher;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import sparta.realm.Realm;
import sparta.realm.RealmClientCallbackInterface;
import sparta.realm.spartautils.svars;


public class RealmClient extends SocketClient {


    public boolean keepReading = false;

    private String serverMessage;


    Context context;

    PrintWriter out;
    BufferedReader in_b;
    DataInputStream in_d;
    DataOutputStream out_d;
    InputStream in;
    String logTag = "Realm Client";

    public RealmClient(RealmClientCallbackInterface realmClientInterfaceTX) {
        super(realmClientInterfaceTX);
        this.context = Realm.context;
        rsp = new RealmClientProtocolV2(this, realmClientInterfaceTX);

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
        Boolean ok_to_read = false;
        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_ADDR);

            Log.e(logTag, "Connecting ...");
            socket = new Socket(serverAddr, SERVER_PORT);
            Log.e(logTag, "Connected");
            socket.setSoTimeout(SERVER_READTIMEOUT);//should be for blocking socs//but now i need to constantly reconnect
//            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            in = socket.getInputStream();
            out_d = new DataOutputStream(socket.getOutputStream());
//            in_d = new DataInputStream(in);
            in_d = new DataInputStream(new BufferedInputStream(in));

            Log.e(logTag, "Authenticating ...");
//            authenticate();
            rsp.Authenticate(device_code, username, password);
            ok_to_read = true;

        } catch (Exception e) {

            Log.e(logTag, "Server connection error", e);
            return;
        }


        try {
            while (keepReading) {
                if (socket.isClosed()) {
                    Log.e(logTag, "Server connection closed !");
                    break;
                }
                try{
                    realmClientInterfaceTX.on_status_changed("1");
                }catch (DeadObjectException deadObjectException){}
//                    while(socket.getInputStream().available() > 0);
                int input_size = in_d.readInt();
                int string_size = in_d.readInt();

//                    out_d.write(totalLen);
//                    out_d.write(strLen);
//                    out_d.write(message.getBytes());
                Log.e(logTag, "RX len: " + input_size);
                Log.e(logTag, "STR len: " + string_size);
                Log.e(logTag, "files len: " + (input_size - string_size));
                byte[] message = new byte[string_size];
                in_d.readFully(message, 0, message.length);
                if (input_size == string_size) {
                    rsp.OnDataReceived(message);

                } else {
                    String inputString = new String(message);
                    LinkedHashMap<String, Map.Entry<Long, String>> fileNameSize = fileNameSizeExtension(inputString, rsp.openEncoding, rsp.closeEncoding);
                    int tot = fileNameSize.size();
                    for (Map.Entry<String, Map.Entry<Long, String>> entry : fileNameSize.entrySet()) {
                        String fileName = svars.getTransactionNo() + "." + entry.getValue().getValue();
                        try (FileOutputStream outStream = new FileOutputStream(svars.current_app_config(Realm.context).appDataFolder + fileName)) {
                            long total_size = entry.getValue().getKey();
                            long total_downloaded = 0;
                            int maxBufferSize = 8 * 1024;
                            while (total_size != total_downloaded) {
                                long remaining_bytes = total_size - total_downloaded;
                                remaining_bytes = remaining_bytes > maxBufferSize ? maxBufferSize : remaining_bytes;
                                byte[] buffer = new byte[(int) remaining_bytes];
                                in_d.readFully(buffer, 0, buffer.length);
                                outStream.write(buffer);
                                total_downloaded += remaining_bytes;

                            }
                        }
                        inputString = inputString.replace(rsp.openEncoding + entry.getKey() + "|" + entry.getValue().getKey() + "|" + entry.getValue().getValue() + rsp.closeEncoding, fileName);
                    }

                    rsp.OnDataReceived(inputString.getBytes());
                }
//                    ByteStreams.readFully(in,message,0,message.length);
                Log.e(logTag, "RX OK. Processing ... ");

            }

            Log.e(logTag, "RX stopped !");

        } catch (Exception e) {
            Log.e(logTag, "RX error:", e);
        } finally {
            try {
                socket.close();
                realmClientInterfaceTX.on_status_changed("0");
            } catch (Exception e) {
            }
        }


        if (keepReading == false) {
            Log.e(logTag, "Connection aborted due to tx errors");

        } else {
            Log.e(logTag, "Connection aborted due to rx errors");

        }

        try {
            realmClientInterfaceTX.on_status_changed("0");
        } catch (DeadObjectException e) {
        } catch (RemoteException remoteException) {
            remoteException.printStackTrace();
        }
//        run();
    }

    int stringCount(String input, char compChar) {
        char[] input_char = input.toCharArray();
        int cnt = 0;
        for (int i = 0; i < input_char.length; i++) {

            if (input_char[i] == compChar) {
                cnt++;
            }

        }
        return cnt;

    }

    LinkedHashMap<String, Map.Entry<Long, String>> fileNameSizeExtension(String input, char openingChar, char closingChar) {
        LinkedHashMap<String, Map.Entry<Long, String>> fileNameSize = new LinkedHashMap<>();
        int count = CharMatcher.is(openingChar).countIn(input);
        char[] input_array = input.toCharArray();
        int strpos = 0;
        for (int i = 0; i < count; i++) {
            int ic = input.indexOf(openingChar, strpos);
            strpos = ic + 1;
            StringBuilder entryBuilder = new StringBuilder();
            while (input_array[strpos] != closingChar) {
                entryBuilder.append(input_array[strpos]);
                strpos++;
            }
            String[] entry = entryBuilder.toString().split("\\|");
            fileNameSize.put(entry[0], new AbstractMap.SimpleEntry<Long, String>(Long.parseLong(entry[1]), (entry[2])));
        }


        return fileNameSize;
    }

    ArrayList<Long> fileSizes(String input, String openingChar, char closingChar) {
        ArrayList<Long> fileLengths = new ArrayList<>();
        int count = CharMatcher.is(openingChar.charAt(0)).countIn(input);
        char[] input_array = input.toCharArray();
        int strpos = 0;
        for (int i = 0; i < count; i++) {
            int ic = input.indexOf(openingChar, strpos);
            strpos = ic + 1;
            StringBuilder entryBuilder = new StringBuilder();
            while (input_array[strpos] != closingChar) {
                entryBuilder.append(input_array[strpos]);
                strpos++;
            }
            fileLengths.add(Long.parseLong(entryBuilder.toString()));
        }


        return fileLengths;
    }

    String filePlaceHolderPrefix = "╤╤";

    long[] filesSizesFromRawString(String input) {

        long[] lo = new long[0];
        int index = 0;
        while (index != -1) {
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
    public void sendData(String data) {
        super.sendData(data);
        sendData(data, 0, new ArrayList<>());
    }

    @Override
    public void sendData(String message, int total_files_size, ArrayList<File> files) {
        Log.e(logTag, "TX: " + message);
        byte[] str=message.getBytes();
        byte[] strLen = ByteBuffer.allocate(4).putInt(str.length).array();
        byte[] totalLen = ByteBuffer.allocate(4).putInt(str.length+ total_files_size).array();
        Log.e(logTag, "TX len: " + message.length());
        try {
            out_d.write(totalLen);
            out_d.write(strLen);
            out_d.write(str);
            if (total_files_size > 0 && files != null && files.size() > 0) {
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
            Log.e(logTag, "TX exception: " + message.length());
            keepReading = false;

//         run();
        }

    }

    public void sendMessage(String message, ArrayList<File> files) {
        Log.e(logTag, "TX: " + message);
        int totalFileSize = 0;
        if (files != null && files.size() > 0) {
            for (File file : files) {
                if (file.exists()) {
                    totalFileSize += file.length();
                } else {
                    //Any missing file then return
                    return;
                }
            }
        }

        byte[] strLen = ByteBuffer.allocate(4).putInt(message.length()).array();
        byte[] totalLen = ByteBuffer.allocate(4).putInt(message.length() + totalFileSize).array();
        Log.e(logTag, "TX len: " + message.length());
        try {
            out_d.write(totalLen);
            out_d.write(strLen);
            out_d.write(message.getBytes());
            if (totalFileSize > 0) {
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
            Log.e(logTag, "TX exception: " + message.length());
            keepReading = false;

//         run();
        }

    }


}
