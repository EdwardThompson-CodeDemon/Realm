package sparta.realm.Activities;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import sparta.realm.spartautils.bluetooth.ble_probe;
import sparta.realm.spartautils.bluetooth.bt_device_connector;
import sparta.realm.spartautils.bluetooth.bt_probe;
import sparta.realm.spartautils.svars;


public class SpartaAppCompactFingerPrintGateActivity extends SpartaAppCompactFingerPrintActivity implements bt_probe.data_interface {

    ble_probe gate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

gate=new ble_probe(act, svars.bt_device_address(act, bt_device_connector.bt_device_type.lb_access_point),svars.gate_service_characteristic);
ble_probe.setup_bt((bt_probe.data_interface) act);
    }

    public void open_gate()
    {
        gate.send_gate_command(true);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                gate.send_gate_command(false);

            }
        },1000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        gate.Pause();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gate.Destroy();
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {


        super.onResume();
gate.Resume();
    }


    @Override
    public void on_data_received(BluetoothDevice device, String data) {

    }

    @Override
    public void on_device_connection_failed(BluetoothDevice device) {

    }

    @Override
    public void on_data_sent(BluetoothDevice device, String data) {

    }

    @Override
    public void on_data_sent(BluetoothDevice device, byte[] data) {
        
    }

    @Override
    public void on_data_parsed(BluetoothDevice device, String data) {

    }

    @Override
    public void on_device_connection_changed(boolean connected, BluetoothDevice device) {

    }

    @Override
    public void on_device_reonnected(BluetoothDevice device) {

    }

    @Override
    public void on_device_error(BluetoothDevice device, String error) {

    }
}
