package k_kim_mg.ntfyntfycall;

import android.app.TabActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // FragmentTabHost を取得する
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
        //tabHost.setup(this, getSupportFragmentManager(), R.id.content_main);
        tabHost.setup();

        TabHost.TabSpec tabSpec1, tabSpec2;

        // TabSpec を生成する
        tabSpec1 = tabHost.newTabSpec("tab1");
        tabSpec1.setIndicator("Notify");
        tabSpec1.setContent(R.id.tab1);
        // TabHost に追加
        tabHost.addTab(tabSpec1);

        // TabSpec を生成する
        tabSpec2 = tabHost.newTabSpec("tab2");
        tabSpec2.setIndicator("Accept");
        tabSpec2.setContent(R.id.tab2);
        // TabHost に追加
        tabHost.addTab(tabSpec2);

        setUpComponents();
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void showAlert(String title, String message, String label) {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle(title).setMessage(message).setPositiveButton(label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create();
        dialog.show();

    }

    /**
     * コンポーネントの初期化
     */
    private void setUpComponents () {
        final List<BluetoothDeviceWrapper> notifyList = new ArrayList<BluetoothDeviceWrapper>();
        final List<BluetoothDeviceWrapper> acceptList = new ArrayList<BluetoothDeviceWrapper>();
        final BluetoothDeviceArrayAdapter notifyAdapter = new BluetoothDeviceArrayAdapter(this, R.layout.deviceitemview, notifyList);
        final BluetoothDeviceArrayAdapter acceptAdapter = new BluetoothDeviceArrayAdapter(this, R.layout.deviceitemview, acceptList);
        // Bluetooth
        BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
        if (bt == null) {
            Toast.makeText(this, "Bluetooth is not suppoted.", Toast.LENGTH_LONG).show();
            Uri uri1 = Uri.parse("content://" + SendListProvider.PROVIDER_NAME + "/" + SendListProvider.TABLE_NAME);
            Cursor cursor1 = getContentResolver().query(uri1, null, null, null, null);
            while (cursor1.moveToNext()) {

            }
            Uri uri2 = Uri.parse("content://" + RecvListProvider.PROVIDER_NAME + "/" + RecvListProvider.TABLE_NAME);
            Cursor cursor2 = getContentResolver().query(uri1, null, null, null, null);
            if (cursor2.moveToNext()) {
            }
            Toast.makeText(this, "end.", Toast.LENGTH_LONG).show();
        } else {
            if (!bt.isEnabled()) {
                Toast.makeText(this, "Bluetooth is not enabled.", Toast.LENGTH_LONG).show();
                Uri uri1 = Uri.parse("content://" + SendListProvider.PROVIDER_NAME + "/" + SendListProvider.TABLE_NAME);
                Cursor cursor1 = getContentResolver().query(uri1, null, null, null, null);
                while (cursor1.moveToNext()) {

                }
                Uri uri2 = Uri.parse("content://" + RecvListProvider.PROVIDER_NAME + "/" + RecvListProvider.TABLE_NAME);
                Cursor cursor2 = getContentResolver().query(uri1, null, null, null, null);
                if (cursor2.moveToNext()) {
                }
            } else {
                Set<BluetoothDevice> deviceSet = bt.getBondedDevices();
                for (BluetoothDevice device : deviceSet) {
                    BluetoothDeviceWrapper notify = new BluetoothDeviceWrapper();
                    BluetoothDeviceWrapper accept = new BluetoothDeviceWrapper();
                    notify.setBluetoothDevice(device);
                    notifyList.add(notify);
                    Uri uri1 = Uri.parse("content://" + SendListProvider.PROVIDER_NAME + "/" + SendListProvider.TABLE_NAME + "/#" + device.getAddress());
                    Cursor cursor1 = getContentResolver().query(uri1, null, null, null, null);
                    if (cursor1.moveToNext()) {
                        notify.setSelected(true);
                    }
                    accept.setBluetoothDevice(device);
                    acceptList.add(accept);
                    Uri uri2 = Uri.parse("content://" + RecvListProvider.PROVIDER_NAME + "/" + RecvListProvider.TABLE_NAME + "/#" + device.getAddress());
                    Cursor cursor2 = getContentResolver().query(uri1, null, null, null, null);
                    if (cursor2.moveToNext()) {
                        accept.setSelected(true);
                    }
                }
            }
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        // Components
        Button btnNotify = (Button) findViewById(R.id.btnNotify);
        final CheckBox chkNotify = (CheckBox) findViewById(R.id.chkNotify);
        chkNotify.setChecked(sp.getBoolean("CHKNOTIFY", false));
        final ListView lstNotifyDevices = (ListView) findViewById(R.id.lstNotifyDevices);
        lstNotifyDevices.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lstNotifyDevices.setAdapter(notifyAdapter);

        Button btnAccept = (Button) findViewById(R.id.btnAccept);
        final CheckBox chkAccept = (CheckBox) findViewById(R.id.chkAccept);
        chkAccept.setChecked(sp.getBoolean("CHKACCEPT", false));
        final ListView lstAcceptDevices = (ListView) findViewById(R.id.lstAcceptDevices);
        lstAcceptDevices.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lstAcceptDevices.setAdapter(acceptAdapter);

        btnNotify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("CHKNOTIFY", chkNotify.isChecked());
                editor.commit();

                /*int cnt = */getContentResolver().delete(SendListProvider.CONTENT_NAME, "", null);
                for (BluetoothDeviceWrapper wrapper : notifyList) {
                    if (wrapper.isSelected()) {
                        BluetoothDevice bd = wrapper.getBluetoothDevice();
                        ContentValues cv = new ContentValues();
                        cv.put("DEVICEADDRESS", bd.getAddress());
                        cv.put("DEVICENAME", bd.getName());

                        getContentResolver().insert(SendListProvider.CONTENT_NAME, cv);
                        Toast.makeText(MainActivity.this, "Send:" + bd.getAddress() + ":" + bd.getName(), Toast.LENGTH_LONG).show();
                    }
                }
                Toast.makeText(MainActivity.this, "Reflected" , Toast.LENGTH_LONG).show();

                BluetoothLoop bl = new BluetoothLoop();
                bl.start();
                Toast.makeText(MainActivity.this, "Test" , Toast.LENGTH_LONG).show();
            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("CHKACCEPT", chkAccept.isChecked());
                editor.commit();

                /*int cnt = */getContentResolver().delete(RecvListProvider.CONTENT_NAME, "", null);
                for (BluetoothDeviceWrapper wrapper : acceptList) {
                    if (wrapper.isSelected()) {
                        BluetoothDevice bd = wrapper.getBluetoothDevice();
                        ContentValues cv = new ContentValues();
                        cv.put("DEVICEADDRESS", bd.getAddress());
                        cv.put("DEVICENAME", bd.getName());

                        getContentResolver().insert(RecvListProvider.CONTENT_NAME, cv);
                        Toast.makeText(MainActivity.this, "Recv:" + bd.getAddress() + ":" + bd.getName(), Toast.LENGTH_LONG).show();
                    }
                }
                Toast.makeText(MainActivity.this, "Reflected" , Toast.LENGTH_LONG).show();

                if (chkAccept.isChecked()) {
                    System.out.println("startservice1");
                    Intent intent = new Intent(MainActivity.this, NotifyAccepter.class);
                    startService(intent);
                    System.out.println("startservice2");
                } else {
                    Intent intent = new Intent(MainActivity.this, NotifyAccepter.class);
                    stopService(intent);
                    System.out.println("stopservice");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class BluetoothLoop extends Thread {

        @Override
        public void run() {
            System.out.println("Test ->");
            try {
                System.out.println("Try ->");
                BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
                try {
                    bt.cancelDiscovery();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                Uri uri = Uri.parse("content://" + SendListProvider.PROVIDER_NAME + "/" + SendListProvider.TABLE_NAME);
                Cursor cursor = MainActivity.this.getContentResolver().query(uri, null, null, null, null);
                while (cursor.moveToNext()) {
                    String address = cursor.getString(cursor.getColumnIndex("DEVICEADDRESS"));
                    try {
                        //Toast.makeText(MainActivity.this, "Test -> " + address, Toast.LENGTH_LONG).show();
                        System.out.println("Test ->" + address);
                        BluetoothDevice device = bt.getRemoteDevice(address);
                        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(PhoneReceiver.UUID_SSP);
                        socket.connect();
                        OutputStream os = socket.getOutputStream();
                        os.write("Test".getBytes());
                        os.flush();
                        os.close();
                        socket.close();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
