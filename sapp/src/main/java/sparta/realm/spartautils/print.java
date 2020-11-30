package sparta.realm.spartautils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorSpace;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.print.sdk.Barcode;
import com.android.print.sdk.CanvasPrint;
import com.android.print.sdk.FontProperty;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.android.print.sdk.PrinterType;
import com.smartdevice.sdk.printer.BlueToothService;
import com.smartdevice.sdk.printer.PrintService;
import com.smartdevice.sdk.printer.PrinterClass;
import com.smartdevicesdk.btprinter.BluetoothService;
import com.smartdevicesdk.btprinter.ICoallBack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import sparta.realm.R;
import sparta.realm.spartautils.bluetooth.bt_device_connector;
import sparta.realm.spartautils.printing.PrintCommand;

import static com.android.print.sdk.PrinterConstants.BarcodeType.CODE128;
import static sparta.realm.spartautils.bluetooth.bt_device_connector.EXTRA_DEVICE_ADDRESS;


/**
 * Created by Thompson on 24-Oct-16.
 */
public class print {
    Activity act;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    public static String sharedprefsname ="SPARTASHAREDPREFS_idcap";
    public static String header = null, body, footer, divider, ColectionTitle = null, clerk, Supplier, centre, total, cumilative, CRLF, sign, body1, application = "WeightCAPTURE", Supplier_name,product_name;





    ListView devicelist;
    View btdialog;
    String document;
     String signature,signatures;
    ArrayList<BluetoothDevice> devicesv = new ArrayList<>();
String devicemac=null;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    private String TAG="SPS ::";

    public interface printeri{
        void on_print_begun();
        void on_print_complete();
        void on_print_error();


    }
    public static boolean checkState = true;

    public enum printer_type{
      T12,
        SFT,
     SFT2
    }
    printeri in;
    public print(Activity act, String doc)
    {
        in=(printeri)act;
        in.on_print_begun();
        this.act=act;
        devicemac="00:1B:35:13:69:40";
/*if(devicemac==null)
{
    Toast.makeText(act,"Printer not set\nNavigate to the side menu at the main menu and click on \"set printer\" to set it",Toast.LENGTH_LONG).show();
    in.on_print_complete();
    return;
}*/
        document=doc;
        //finddevices();
    }
    private PrinterInstance mPrinter;
com.smartdevicesdk.btprinter.PrintService pl;

    printer_type working_printer_type;

    public print(printer_type pt, final Activity act, String doc, String signature)
    {
        PrintService.isMain = true;
         checkState = true;
working_printer_type=pt;


        in=(printeri)act;
        in.on_print_begun();
        this.act=act;
        document=doc;
        this.signature=signature;
        this.signatures=signatures;
        devicemac="00:1B:35:13:69:40";
        devicemac=svars.bt_printer_address(act);
if(devicemac==null)
{
    new bt_device_connector(act,bt_device_connector.bt_device_type.printer).show(new bt_device_connector.device_selection_handler() {


        @Override
        public void on_device_paired_and_selected(BluetoothDevice device) {

        }

        @Override
        public void on_device_slected(BluetoothDevice device) {

        }

        @Override
        public void on_device_paired(BluetoothDevice device) {

        }
    });
    return;
}
if(pt== printer_type.T12){

    BluetoothDevice dev=saved_dev();
    if(dev==null)return;
    mPrinter = new PrinterInstance(act, dev, mHandler);
    mPrinter.openConnection();

    printCustomImage();
}else if(pt== printer_type.SFT) {
    PrintService.PrinterInit(0, act, mhandler, new Handler());
    if (!PrintService.pl().IsOpen()) {
        PrintService.pl().open(act);
    }
    new Thread(new Runnable() {
        @Override
        public void run() {
            PrintService.pl().connect(new bt_device_connector(act, bt_device_connector.bt_device_type.printer).set_device(bt_device_connector.bt_device_type.printer).getAddress());
//            act.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try{
//                        print();
//                    }catch (Exception ex){}
//
//                }
//            });
        }
    }).start();
    tv_update.start();

}else if(pt== printer_type.SFT2)
{
    pl = new com.smartdevicesdk.btprinter.PrintService(act);
    pl.setOnPrinterStatus(new ICoallBack() {
        @Override
        public void onPrinterStatus(int s, String text) {
            switch (s) {
                case BluetoothService.STATE_CONNECTED:
                    // 连接成功蜂鸣器发声
                 //   pl.write(PrintCommand.set_Buzzer(2, 1));
                    if(qr_to_print==null){
                        print();
                    }else {
                        print(qr_to_print);
                    }
                    //  pl.getMacAddress();


                    break;
                case BluetoothService.STATE_CONNECTING:


                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                    // 未连接


                    break;
                case BluetoothService.READ_DATA:
                    // 打印机返回数据

                    break;
            }
            Log.e(TAG, "---"+text);
            ShowMsg(text);
        }
    });
    Intent intent = new Intent();
    intent.putExtra(EXTRA_DEVICE_ADDRESS, new bt_device_connector(act, bt_device_connector.bt_device_type.printer).set_device(bt_device_connector.bt_device_type.printer).getAddress());
    pl.connectDevice(intent, true);


}



}
String qr_to_print=null;
  public print(printer_type pt, final Activity act, String doc, String signature, String qr)
    {
        qr_to_print=qr;
        PrintService.isMain = true;

         checkState = true;
working_printer_type=pt;


        in=(printeri)act;
        in.on_print_begun();
        this.act=act;
        document=doc;
        this.signature=signature;
        this.signatures=signatures;
        devicemac="00:1B:35:13:69:40";
        devicemac=svars.bt_device_address(act, bt_device_connector.bt_device_type.printer);
if(devicemac==null)
{
    new bt_device_connector(act,bt_device_connector.bt_device_type.printer).show(new bt_device_connector.device_selection_handler() {
        @Override
        public void on_device_paired_and_selected(BluetoothDevice device) {

        }

        @Override
        public void on_device_slected(BluetoothDevice device) {

        }

        @Override
        public void on_device_paired(BluetoothDevice device) {

        }
    });
    return;
}
if(pt== printer_type.T12){

    BluetoothDevice dev=saved_dev();
    if(dev==null)return;
    mPrinter = new PrinterInstance(act, dev, mHandler);
    mPrinter.openConnection();

    printCustomImage();
}else if(pt== printer_type.SFT) {
    PrintService.PrinterInit(0, act, mhandler, new Handler());
    if (!PrintService.pl().IsOpen()) {
        PrintService.pl().open(act);
    }
    new Thread(new Runnable() {
        @Override
        public void run() {
try{            PrintService.pl().connect(new bt_device_connector(act, bt_device_connector.bt_device_type.printer).set_device(bt_device_connector.bt_device_type.printer).getAddress());}catch (Exception ex){
act.runOnUiThread(new Runnable() {
    @Override
    public void run() {
        Toast.makeText(act, act.getString(R.string.unable_to_connect_to_printer), Toast.LENGTH_LONG).show();
        new bt_device_connector(act,bt_device_connector.bt_device_type.printer).show(new bt_device_connector.device_selection_handler() {
            @Override
            public void on_device_paired_and_selected(BluetoothDevice device) {
                PrintService.pl().connect(new bt_device_connector(act, bt_device_connector.bt_device_type.printer).set_device(bt_device_connector.bt_device_type.printer).getAddress());
            }

            @Override
            public void on_device_slected(BluetoothDevice device) {

            }

            @Override
            public void on_device_paired(BluetoothDevice device) {

            }
        });
    }
});

}
//            act.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try{
//                        print();
//                    }catch (Exception ex){}
//
//                }
//            });
        }
    }).start();
    tv_update.start();

}else if(pt== printer_type.SFT2)
{
    pl = new com.smartdevicesdk.btprinter.PrintService(act);
    pl.setOnPrinterStatus(new ICoallBack() {
        @Override
        public void onPrinterStatus(int s, String text) {
            switch (s) {
                case BluetoothService.STATE_CONNECTED:
                    // 连接成功蜂鸣器发声
                    //   pl.write(PrintCommand.set_Buzzer(2, 1));
                    if(qr_to_print==null){
                        print();
                    }else {
                        print(qr_to_print);
                    }
                    //  pl.getMacAddress();

                    conected=true;
                    break;
                case BluetoothService.STATE_CONNECTING:


                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                    // 未连接


                    break;
                case BluetoothService.READ_DATA:
                    // 打印机返回数据

                    break;
            }
            Log.e(TAG, "---"+text);
             if(text.equalsIgnoreCase("connecting")){
                ShowMsg(act.getString(R.string.connecting));

            }else if(text.startsWith("connected_to")){
                ShowMsg(text.replace("connected_to",act.getString(R.string.connected_to)));

            }
         }
    });
    BluetoothDevice btd= new bt_device_connector(act, bt_device_connector.bt_device_type.printer).set_device(bt_device_connector.bt_device_type.printer);
    if(btd!=null)
    {
        try{
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, new bt_device_connector(act, bt_device_connector.bt_device_type.printer).set_device(bt_device_connector.bt_device_type.printer).getAddress());
            pl.connectDevice(intent, true);
        }catch (Exception ex){}
    }else{
        try{
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act, act.getString(R.string.unable_to_connect_to_printer), Toast.LENGTH_LONG).show();
                    new bt_device_connector(act,bt_device_connector.bt_device_type.printer).show(new bt_device_connector.device_selection_handler() {
                        @Override
                        public void on_device_paired_and_selected(BluetoothDevice device) {
                            try{
                                Intent intent = new Intent();
                                intent.putExtra(EXTRA_DEVICE_ADDRESS, new bt_device_connector(act, bt_device_connector.bt_device_type.printer).set_device(bt_device_connector.bt_device_type.printer).getAddress());
                                pl.connectDevice(intent, true);
                            }catch (Exception ex){}
                        }

                        @Override
                        public void on_device_slected(BluetoothDevice device) {

                        }

                        @Override
                        public void on_device_paired(BluetoothDevice device) {

                        }
                    });
                }
            });

        }catch (Exception ex){}

    }



}




    }
public  boolean conected=false;

    Handler mhandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    Log.e(TAG, "readBuf:" + readBuf[0]);
                    if (readBuf[0] == 0x13) {
                        PrintService.isFUll = true;
                        ShowMsg(act.getResources().getString(
                                R.string.str_printer_state)
                                + ":"
                                + act.getResources().getString(
                                R.string.str_printer_bufferfull));
                    } else if (readBuf[0] == 0x11) {
                        PrintService.isFUll = false;
                        ShowMsg(act.getResources().getString(
                                R.string.str_printer_state)
                                + ":"
                                + act.getResources().getString(
                                R.string.str_printer_buffernull));
                    } else if (readBuf[0] == 0x08) {
                        ShowMsg(act.getResources().getString(
                                R.string.str_printer_state)
                                + ":"
                                + act.getResources().getString(
                                R.string.str_printer_nopaper));
                    } else if (readBuf[0] == 0x01) {
                        // ShowMsg(act.getResources().getString(R.string.str_printer_state)+":"+act.getResources().getString(R.string.str_printer_printing));
                    } else if (readBuf[0] == 0x04) {
                        ShowMsg(act.getResources().getString(
                                R.string.str_printer_state)
                                + ":"
                                + act.getResources().getString(
                                R.string.str_printer_hightemperature));
                    } else if (readBuf[0] == 0x02) {
                        ShowMsg(act.getResources().getString(
                                R.string.str_printer_state)
                                + ":"
                                + act.getResources().getString(
                                R.string.str_printer_lowpower));
                    } else {
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        if (readMessage.contains("800"))// 80mm paper
                        {
                            PrintService.imageWidth = 72;
                            Toast.makeText(act, "80mm",
                                    Toast.LENGTH_SHORT).show();
                        } else if (readMessage.contains("580"))// 58mm paper
                        {
                            PrintService.imageWidth = 48;
                            Toast.makeText(act, "58mm",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case MESSAGE_STATE_CHANGE:// 蓝牙连接状
                    switch (msg.arg1) {
                        case PrinterClass.STATE_CONNECTED:// 已经连接
                            break;
                        case PrinterClass.STATE_CONNECTING:// 正在连接
                            Toast.makeText(act,
                                    "CONNECTING", Toast.LENGTH_SHORT).show();
                            break;
                        case PrinterClass.STATE_LISTEN:
                        case PrinterClass.STATE_NONE:
                            break;
                        case PrinterClass.SUCCESS_CONNECT:
                            PrintService.pl().write(new byte[] { 0x1b, 0x2b });// 检测打印机型号
                            Toast.makeText(act,
                                    "SUCCESS_CONNECT", Toast.LENGTH_SHORT).show();
                          if(qr_to_print==null){
                              print();
                          }else {
                              print(qr_to_print);
                          }
                            break;
                        case PrinterClass.FAILED_CONNECT:
                            Toast.makeText(act,
                                    "FAILED_CONNECT", Toast.LENGTH_SHORT).show();
                            if (BlueToothService.autoConnect == 3) {
                                /*startActivity(new Intent(MainActivity.this,
                                        DeviceListActivity.class));*/
                            } else {
                                BlueToothService.reconnect();
                            }
                            break;
                        case PrinterClass.LOSE_CONNECT:
                            Toast.makeText(act, "LOSE_CONNECT",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private void ShowMsg(String msg) {
        Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
    }
    Thread tv_update = new Thread() {
        public void run() {
            while (true) {
                if (checkState) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                   
                            // TODO Auto-generated method stub
                            if (PrintService.pl() != null) {
                                if (PrintService.pl().getState() == PrinterClass.STATE_CONNECTED) {
                                    /*
                                    textView_state
                                            .setText(act.getResources()
                                                    .getString(
                                                            R.string.str_connected));*/
                                } else if (PrintService.pl().getState() == PrinterClass.STATE_CONNECTING) {
                                   /* textView_state
                                            .setText(act.getResources()
                                                    .getString(
                                                            R.string.str_connecting));*/
                                } else if (PrintService.pl().getState() == PrinterClass.LOSE_CONNECT
                                        || PrintService.pl().getState() == PrinterClass.FAILED_CONNECT) {
                                    checkState = false;
                                    /* textView_state.setText(act.getResources()
                                            .getString(
                                                    R.string.str_disconnected));
                                   Intent intent = new Intent();
                                    intent.setClass(MainActivity.this,
                                            PrintSettingActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(intent);*/
                                } else {
                                    /*textView_state.setText(act.getResources()
                                            .getString(
                                                    R.string.str_disconnected));*/
                                }
                            }
                      
                            
                }
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:

                    Toast.makeText(act, "Impression...",
                            Toast.LENGTH_SHORT).show();
                    /*mPrinter.openConnection();
                    printCustomImage();*/
                    break;
                case PrinterConstants.Connect.FAILED:

                    Toast.makeText(act, "connect failed...",
                            Toast.LENGTH_SHORT).show();
                    break;
                case PrinterConstants.Connect.CLOSED:

                    Toast.makeText(act, "connect close...", Toast.LENGTH_SHORT)
                            .show();
                    break;
                default:
                    break;
            }


        }

    };

    public void printCustomImage() {
        mPrinter.init();
        // TODO Auto-generated method stub

        CanvasPrint cp = new CanvasPrint();
        cp.init(PrinterType.TIII);


        cp.setUseSplit(true);
        cp.setTextAlignRight(false);
        FontProperty fp = new FontProperty();
        fp.setFont(false, false, false, false, 27, null);

       // cp.drawImage(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(act.getResources(),R.drawable.capture_icon), 170, 120, false));
        cp.drawImage(0,0,s_bitmap_handler.toGrayscale (Bitmap.createScaledBitmap(BitmapFactory.decodeResource(act.getResources(), R.drawable.logo), 130, 130, false)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTargetDensity = 200;
        options.inDensity = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            options.inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.ACES);
        }
//        cp.drawImage(240,0, s_bitmap_handler.toGrayscale(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(svars.current_app_config(Realm.context).file_path_employee_data+svars.working_employee.images[svars.image_indexes.profile_photo],options), 120, 165, false)));
        cp.drawImage(200,0, (Bitmap.createScaledBitmap(BitmapFactory.decodeResource(act.getResources(), R.drawable.logo), 165, 130, false)));


        //  mPrinter.printText(document+"\n");

     //   mPrinter.printImage(cp.getCanvasImage());

        cp.setFontProperty(fp);
        fp.setFont(false, false, false, false, 20, null);
        cp.setFontProperty(fp);
        cp.drawText("RECEPISSE CARTE CONSULAIRE");

        for(String doo:document.split("\n"))
        {
            Log.e("Print pos :",""+  cp.getCurrentPointY());
            cp.drawText(doo.trim());
        }


        Log.e("Print pos :","i"+  cp.getCurrentPointY());
        cp.drawImage( s_bitmap_handler.toGrayscale(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(svars.current_app_config(act).file_path_employee_data+svars.working_member.images[svars.image_indexes.signature]), 170, 100, false)));
        Log.e("Print pos :","ii"+  cp.getCurrentPointY());
        mPrinter.printImage(cp.getCanvasImage());
     cp= new CanvasPrint();
        cp.init(PrinterType.TIII);


        cp.setUseSplit(true);
        cp.setTextAlignRight(false);
        cp.setFontProperty(fp);


       // cp.drawImage(0,0, s_bitmap_handler.toGrayscale(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(svars.current_app_config(Realm.context).file_path_employee_data+svars.working_employee.images[svars.image_indexes.signature]), 120, 100, true)));
       // cp.drawText("----------------------------------------------------------");
        //  cp.drawText("--------------------------GUID------------------------");
       // cp.drawText(".");
        cp.drawText("Toute erreur de saisie non signalée ne sera pas prise en compte après production de la carte\n.\n");
        cp.drawText(".");


        //cp.drawText(document.trim());
       // cp.drawImage(Bitmap.createScaledBitmap(s_bitmap_handler.getImage(Base64.decode(signature,0)), 170, 120, false));

        //mPrinter.printText("Print Custom Image:\n");


            mPrinter.printImage(cp.getCanvasImage());
       // Barcode bc=new Barcode(PrinterConstants.BarcodeType.CODABAR,0,76,5,svars.working_employee.transaction_no.value);
       // Barcode bc=new Barcode(PrinterConstants.BarcodeType.CODABAR,2,150,2,svars.working_employee.formulaire_no.value);
        Barcode bc=  new Barcode(CODE128, 2, 150, 2, svars.working_member.formulaire_no.value);


        mPrinter.printBarCode(bc);

        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        in.on_print_complete();

    }
BluetoothDevice saved_dev()
{
    if(devicemac==null) {
       return null;
    }else {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Log.e("Bluetooth class =>", "No bluetooth adapter available");


            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                act.startActivityForResult(enableBluetooth, 0);
                return null;
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            boolean found=false;
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    Log.e("Printer =>", "device : " + device.getName());

                    if (device.getAddress().toString().equalsIgnoreCase(devicemac)) {
                        Log.e("Bluetooth class =>", "Bluetooth device found.");
                        return device;

                    }

                }




            }else {
               return null;

            }


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    return null;
}


     public void openBT() {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            Log.e("Bluetooth class =>", "Bluetooth Opened");
            beginListenForData();



        } catch (Exception e) {
           Log.e("Open bt error =>", "" + e.getMessage());
        }
    }
    public void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Toast.makeText(act.getApplicationContext(), "incoming " + data, Toast.LENGTH_LONG).show();

                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  public void print(){
        if(working_printer_type== printer_type.T12)
        {
            try {



                // the text typed by the user
                mmOutputStream.write(document.getBytes());
                in.on_print_complete();

                // tell the user data were sent
                Toast.makeText(act.getApplicationContext(), "Impression ", Toast.LENGTH_LONG).show();
//act.finish();
                try {
                    act.finish();
                }catch (Exception ex){}
                try {
                    // worker_reg2.finish_all();
                }catch (Exception ex){}
                closeBT();

                set_bt_printer_address(act,mmDevice.getAddress().toString());

            } catch (Exception e) {
                Log.e("Send data bt error =>", "" + e.getMessage());
            }
        }else if(working_printer_type== printer_type.SFT)
        {
            try {
                Bitmap mBitmap = BitmapFactory.decodeResource(act.getResources(),
                        R.drawable.capturelogo);

                PrintService.pl().write(
                        PrintService.pl().CMD_FONTSIZE_NORMAL);
                Bitmap myBitmap = PrintService.resizeImage(mBitmap, 360, 60);
                PrintService.pl().printImage2(myBitmap);

                PrintService.pl().write(
                        PrintService.pl().CMD_ALIGN_LEFT);

                PrintService.pl().write(
                        PrintService.pl().CMD_FONTSIZE_NORMAL);

                PrintService.pl().printText(document + "\r\n\r\n");
                byte[] btStr = null;
                btStr = document.getBytes();
                //获取文字数据长度
                int msgSize = btStr.length;

                //初始化发送数据十六进制数组大小，指令+文字数据长度
                byte[] btcmd = new byte[4 + msgSize];
                btcmd[0] = 0x1F;
                btcmd[1] = 0x11;
                btcmd[2] = (byte) (msgSize >>> 8);
                btcmd[3] = (byte) (msgSize & 0xff);

                //合并数组
                System.arraycopy(btStr, 0, btcmd, 4, btStr.length);

                //转换十进制
                String sendString = new String(btcmd);

                //发送
                PrintService.pl().printText(sendString);
            } catch (Exception e) {
                Log.e("Send data bt error =>", "" + e.getMessage());
            }
            }else if(working_printer_type== printer_type.SFT2)
        {
            pl.write(PrintCommand.set_CodePage(4));   //select codepage
            pl.printUnicode_1F30("Côte d'Ivoire Côte d'IvoireCôte d'IvoireCôte d'IvoireCôte d'IvoireCôte d'Ivoire");

        }

    }
   public void print(final String qr){
        if(working_printer_type== printer_type.T12)
        {
            try {

                printCustomImage();


                // the text typed by the user
            //    mmOutputStream.write(document.getBytes());
                in.on_print_complete();

                // tell the user data were sent
                Toast.makeText(act.getApplicationContext(), "Impression ", Toast.LENGTH_LONG).show();
//act.finish();
                try {
           //         act.finish();
                }catch (Exception ex){}
                try {
                    // worker_reg2.finish_all();
                }catch (Exception ex){}
               // closeBT();

                set_bt_printer_address(act,mmDevice.getAddress().toString());

            } catch (Exception e) {
                Log.e("Send data bt error =>", "" + e.getMessage());
            }
        }else if(working_printer_type== printer_type.SFT)
        {
            try {
                Bitmap mBitmap = BitmapFactory.decodeResource(act.getResources(),
                        R.drawable.capturelogo);

                PrintService.pl().write(
                        PrintService.pl().CMD_FONTSIZE_NORMAL);
                Bitmap myBitmap = PrintService.resizeImage(mBitmap, 360, 60);
                PrintService.pl().printImage2(myBitmap);

                PrintService.pl().write(
                        PrintService.pl().CMD_ALIGN_LEFT);

                PrintService.pl().write(
                        PrintService.pl().CMD_FONTSIZE_NORMAL);

                PrintService.pl().printText(document + "\r\n\r\n");

                PrintService.pl().write(
                        PrintService.pl().CMD_ALIGN_MIDDLE);
                PrintService.pl().printText("GUUID ↓" + "\r\n\r\n");

                byte[] btStr = null;
                btStr = qr.getBytes();
                //获取文字数据长度
                int msgSize = btStr.length;

                //初始化发送数据十六进制数组大小，指令+文字数据长度
                byte[] btcmd = new byte[4 + msgSize];
                btcmd[0] = 0x1F;
                btcmd[1] = 0x11;
                btcmd[2] = (byte) (msgSize >>> 8);
                btcmd[3] = (byte) (msgSize & 0xff);

                //合并数组
                System.arraycopy(btStr, 0, btcmd, 4, btStr.length);

                //转换十进制
                String sendString = new String(btcmd);

                //发送
                PrintService.pl().printText(sendString);
                PrintService.pl().printText("\n\n\n\r\n");

            } catch (Exception e) {
                Log.e("Send data bt error =>", "" + e.getMessage());
            }
            }else if(working_printer_type== printer_type.SFT2)
        {
            if(!pl.isConnected()) {
                BluetoothDevice btd= new bt_device_connector(act, bt_device_connector.bt_device_type.printer).set_device(bt_device_connector.bt_device_type.printer);
                if(btd!=null)
                {
                    try{
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_DEVICE_ADDRESS, new bt_device_connector(act, bt_device_connector.bt_device_type.printer).set_device(bt_device_connector.bt_device_type.printer).getAddress());
                        pl.connectDevice(intent, true);
                    }catch (Exception ex){}
                }else{
                    try{
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(act, act.getString(R.string.unable_to_connect_to_printer), Toast.LENGTH_LONG).show();
                                new bt_device_connector(act,bt_device_connector.bt_device_type.printer).show(new bt_device_connector.device_selection_handler() {
                                    @Override
                                    public void on_device_paired_and_selected(BluetoothDevice device) {
                                        try{
                                            Intent intent = new Intent();
                                            intent.putExtra(EXTRA_DEVICE_ADDRESS, new bt_device_connector(act, bt_device_connector.bt_device_type.printer).set_device(bt_device_connector.bt_device_type.printer).getAddress());
                                            pl.connectDevice(intent, true);
                                        }catch (Exception ex){}
                                    }

                                    @Override
                                    public void on_device_slected(BluetoothDevice device) {

                                    }

                                    @Override
                                    public void on_device_paired(BluetoothDevice device) {

                                    }
                                });
                            }
                        });

                    }catch (Exception ex){}

                }
                return;
            }
            pl.setPrint_Language("PC863",4);
            pl.write(PrintCommand.set_CodePage(4));   //select codepage

            pl.write(PrintCommand.set_Align(0));

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.e("Doc",""+document);
            Bitmap mBitmap = BitmapFactory.decodeResource(act.getResources(),
                    R.drawable.capturelogo);

            pl.printGrayImage(mBitmap);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                for(String s:document.split("\n")) {
                    pl.printUnicode_1F30(s);
                    pl.printText("\n");
                }
            }catch (Exception ex){

             }
new Thread(new Runnable() {
                @Override
                public void run() {
                    pl.write(PrintCommand.set_QrCode_TopSpace(0));
                    pl.printQrCode(qr);
                    pl.printText("\n\n\n\n");

                    //  pl.write(new byte[] { 0x0c });

                }
            }).start();
        }

    }

//  public static byte[] set_CodePage(int n)
//  {
//      if(n<0||n>40)
//      {
//return null;
//      }
////CMD.ESc
//
//  }
  public void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
          //  in.on_print_complete();

          //  Toast.makeText(act, "Bluetooth Closed", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }

        return data;
    }
    public static String bt_printer_address(Activity act)
    {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("bt_printer_address",null);

    }
    public static void set_bt_printer_address(Activity act, String macaddress)
    {

        SharedPreferences.Editor saver =act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("bt_printer_address",macaddress);

        saver.commit();

    }

}
