package k_kim_mg.ntfyntfycall;

import android.bluetooth.BluetoothDevice;

/**
 * Created by kenji on 2017/01/31.
 */

public class BluetoothDeviceWrapper {
    /** BluetoothDevice */
    private BluetoothDevice bluetoothDevice;
    /** selected */
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }
}
