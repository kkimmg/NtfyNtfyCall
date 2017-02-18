package k_kim_mg.ntfyntfycall;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kenji on 2017/01/30.
 */

public class BluetoothDeviceArrayAdapter extends ArrayAdapter<BluetoothDeviceWrapper> {
    private LayoutInflater inflater;

    public BluetoothDeviceArrayAdapter (Context context, int textViewResourceId, List<BluetoothDeviceWrapper> objects) {
        super(context, textViewResourceId, objects);
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BluetoothDeviceWrapper item = (BluetoothDeviceWrapper)getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.deviceitemview, null);
        }
        TextView txtDeviceName = (TextView)convertView.findViewById(R.id.txtDeviceName);
        txtDeviceName.setText(item.getBluetoothDevice().getName());

        TextView txtDeviceAddress = (TextView)convertView.findViewById(R.id.txtDeviceAddress);
        txtDeviceAddress.setText(item.getBluetoothDevice().getAddress());

        CheckBox chkSelected = (CheckBox)convertView.findViewById(R.id.chkSelected);
        chkSelected.setChecked(item.isSelected());
        chkSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                item.setSelected(b);
            }

        } );

        return convertView;
    }
}
