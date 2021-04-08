package sparta.realm.spartautils.general_interfaces;

import android.bluetooth.BluetoothDevice;

public interface bluetooth_interface extends data_interface{



        void on_device_error(BluetoothDevice device, String error, String error2);


}
