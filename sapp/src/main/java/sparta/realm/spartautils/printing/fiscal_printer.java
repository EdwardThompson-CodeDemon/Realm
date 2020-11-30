package sparta.realm.spartautils.printing;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.datecs.fiscalprinter.FiscalPrinterException;
import com.datecs.fiscalprinter.FiscalResponse;
import com.datecs.fiscalprinter.ken.FMP10KEN;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

import sparta.realm.spartamodels.order;
import sparta.realm.spartamodels.order_item;
import sparta.realm.spartautils.sparta_loc_util;
import sparta.realm.spartautils.svars;


public class fiscal_printer {
    public static FMP10KEN mFMP;

    private static BluetoothAdapter mBtAdapter;
    private static BluetoothSocket mBtSocket;
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
static {


 //   start(svars.bt_device_address(SpartaApplication.getAppContext(), bt_device_connector.bt_device_type.printer));
}
    private static void print_receipt(order ord)
    {
        if(svars.is_used(cntext,ord.order_no))
        {
            print_non_fisc(ord);


        }else{

            print_Fiscal_Order(ord);

        }
    }
    private static void print_Fiscal_Order(order ord) {



//        mFMP.openFiscalCheckWithDefaultValues();
//        mFMP.command54Variant0Version0("    * SIMPLE FISCAL TEXT *");
//        mFMP.sellThisWithQuantity("ITEM 1\nsecond line", "B", "0.99", "1");
//        mFMP.sellThisWithQuantity("ITEM 2", "B", "1.99", "1");
//        mFMP.sellThisWithQuantity("ITEM 3", "B", "2.99", "1");
//        mFMP.command51Variant0Version0();
//        mFMP.command51Variant0Version1("-99.00");
//        mFMP.totalInCash();
//        mFMP.closeFiscalCheck();
        try {
            // mFMP.command48Variant0Version0("1", "0000", "1");
            mFMP.openFiscalCheckWithDefaultValues();
           // sd.database.execSQL("UPDATE orders SET receipt_status=\"printed\" WHERE id="+ord.lid);
            mFMP.command54Variant0Version0("Sale : "+ord.order_no);
            for(order_item pp:ord.sales_items)
            {
              /*  mFMP.sellThisWithQuantity(pp.name, "A",String.format("%.2f",Double.parseDouble(pp.selling_price)/Double.parseDouble(pp.quantity)),pp.quantity);
                mFMP.command51Variant0Version0();
                mFMP.command51Variant0Version1("-0.00");
                mFMP.totalInCash();
                mFMP.closeFiscalCheck();*/
                mFMP.sellThisWithQuantity(pp.product_name, "A", sparta_loc_util.round(Double.parseDouble(pp.price),2)+"",sparta_loc_util.round(Double.parseDouble(pp.quantity),2)+"");

                /*mFMP.command49Variant0Version14("ITEM 1", "second line", "B", "10.00", "1.000");
                mFMP.command49Variant1Version14("ITEM 1", "second line", "B", "10.00", "1.000", "3.00");
                mFMP.command49Variant2Version14("ITEM 1", "second line", "B", "10.00", "1.000", "-50.00");*/
            }
            mFMP.command54Variant0Version0("ORG");
            mFMP.command51Variant0Version1("-0.00");
            // mFMP.command49Variant0Version14("ITEM 1", "second line", "B", "10.00", "1.000");
            mFMP.command51Variant0Version0();
            mFMP.totalInCash();
            mFMP.closeFiscalCheck();
          //  sd.database.execSQL("UPDATE orders SET receipt_status=\"printed\" WHERE id="+ord.lid);
            // disconnect();

svars.add_used_code(cntext,ord.order_no);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    static void print_non_fisc(final order ord)
    {
        try {

            mFMP.command38Variant0Version0();

//            for (int i = 0; i<ord.payments.size(); i++){
//                if(ord.payments.get(i).mode_id.equalsIgnoreCase(svars.mpesa_payment_mode_id)){
//                    mFMP.command42Variant0Version0("MPESA - "+ord.payments.get(i).amount);
//                }else if(ord.payments.get(i).mode_id.equalsIgnoreCase(svars.cheque_payment_mode_id)){
//                    mFMP.command42Variant0Version0("CHEQUE - "+ord.payments.get(i).amount);
//                }else if(ord.payments.get(i).mode_id.equalsIgnoreCase(svars.cash_payment_mode_id)){
//                    mFMP.command42Variant0Version0("CASH - "+ord.payments.get(i).amount);
//                }
//            }
            mFMP.command42Variant0Version0("CASH - "+ord.total_amount);




            for (order_item p : ord.sales_items) {
                String itemName = p.product_name;
                Double amt = Double.parseDouble(p.price);
                Double qty =Double.parseDouble(p.quantity);

                if (qty>0){
                    mFMP.command42Variant0Version0(itemName);
                    mFMP.command42Variant0Version0(String.valueOf(amt) +" x "+ String.valueOf(qty));
                    //mFMP.sellThisWithQuantity(itemName, "A", String.valueOf(amt), String.valueOf(qty));
                }
            }

            mFMP.command39Variant0Version0();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void performZReport_ken() {
        invokeHelper(new MethodInvoker() {
            @Override
            public void invoke() throws IOException {
                FiscalResponse response = mFMP.command120Variant1Version0();
                StringBuffer buffer = new StringBuffer();

                if (response.get("errorCode").equals("P")) {
                    do {
                        buffer.append(response.get("journalLineText") + "\r\n");
                        response = mFMP.command120Variant1Version1();
                    } while (response.get("errorCode").equals("P"));

                    // Calculate SHA hash
                    MessageDigest md = null;
                    try {
                        md = MessageDigest.getInstance("SHA-1");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    byte[] hash = md.digest(buffer.toString().getBytes("Cp1252"));

                    // Erase journal
                    String sha1 = byteArrayToHexString(hash, 0, hash.length);
                    mFMP.command120Variant2Version0(sha1);
                    // Print journal
                    mFMP.command69Variant0Version0("0");


                }
            }
        });
    }

    private static final String byteArrayToHexString(byte[] data, int offset, int length) {
        final char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] buf = new char[length * 2];
        int offs = 0;

        for (int i = 0; i < length; i++) {
            buf[offs++] = hex[(data[offset + i] >> 4) & 0xf];
            buf[offs++] = hex[(data[offset + i] >> 0) & 0xf];
        }

        return new String(buf, 0, offs);
    }
    private interface MethodInvoker {
        public void invoke() throws IOException;
    }


    static Snackbar dialog;
    static Context cntext;
   public static View main_lay=null;
    private static void invokeHelper(final MethodInvoker invoker) {
        new Handler(cntext.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                dialog = Snackbar
                        .make(main_lay, ".", Snackbar.LENGTH_LONG);


                dialog.setText("Loading ...");


                dialog.show();
            }
        });


        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    invoker.invoke();
                    disconnect();

                } catch (FiscalPrinterException e) { // Fiscal printer error
                    e.printStackTrace();
                    postToast("FiscalPrinterException: " + e.getMessage());
                } catch (IOException e) { //Communication error
                    e.printStackTrace();
                    postToast("IOException: " + e.getMessage());
                    disconnect();
                    selectDevice();
                } catch (Exception e) { // Critical exception
                    e.printStackTrace();
                    postToast("Exception: " + e.getMessage());
                    disconnect();
                    selectDevice();
                } finally {
                    new Handler(cntext.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                }
            }
        });
        t.start();
    }
    private static void selectDevice() {
        // connect(svars.bt_printer_address(act));
    }
    public void start(final String address, final ArrayList<order> orderss) {
        invokeHelper(new MethodInvoker() {
            @Override
            public void invoke() throws IOException {
                if(mBtAdapter==null){ mBtAdapter = BluetoothAdapter.getDefaultAdapter();
                }
                final BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
                final BluetoothSocket socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                socket.connect();

                mBtSocket = socket;
                final InputStream in = socket.getInputStream();
                final OutputStream out = socket.getOutputStream();
                mFMP = new FMP10KEN(in, out);
                postToast("Connected");
                if(orderss==null)
                {
                    Log.e("Z report =>"," starting ...");
                    performZReport_ken();
                    Log.e("Z report =>","done ");
                }else{
                    for(order ordd:orderss)
                    {
                        print_receipt(ordd);
                    }

                }

            }
        });
    }
 public static void start(final String address,Context con,order sale) {
        cntext=con;
     if(mBtAdapter==null){ mBtAdapter = BluetoothAdapter.getDefaultAdapter();
     }
        invokeHelper(new MethodInvoker() {
            @Override
            public void invoke() throws IOException {
            try {
                final BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
                final BluetoothSocket socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                socket.connect();
                postToast("Connected ..");

                mBtSocket = socket;
                final InputStream in = socket.getInputStream();
                final OutputStream out = socket.getOutputStream();
                mFMP = new FMP10KEN(in, out);
                postToast("Printing ...");
                print_receipt(sale);
                postToast("Printing done");

            }catch (Exception ex){Log.e("Error :",""+ex.getMessage());ex.printStackTrace();
                postToast("Printer is Offline");}



            }
        });
    }

    private static void postToast(final String text) {
        new Handler(cntext.getMainLooper()).post(() -> dialog.setText( text).show());
    }
    public static synchronized void disconnect() {
        if (mFMP != null) {
            mFMP.close();
        }

        if (mBtSocket != null) {
            try {
                mBtSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
