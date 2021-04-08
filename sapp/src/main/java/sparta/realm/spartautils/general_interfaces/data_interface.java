package sparta.realm.spartautils.general_interfaces;

import android.bluetooth.BluetoothDevice;

public interface data_interface {



        default void on_data_received(int device, String data){

        }
    default void on_device_connection_failed(BluetoothDevice device){

    }
    default void on_data_sent(BluetoothDevice device, String data){

    }
    default void on_data_sent(BluetoothDevice device, byte[] data){

    }
    default   void on_data_parsed(BluetoothDevice device, String data){

        }
        void on_device_connection_changed(boolean connected, BluetoothDevice device);
        void on_device_reonnected(BluetoothDevice device);
        void on_device_error(BluetoothDevice device, String error);


}
