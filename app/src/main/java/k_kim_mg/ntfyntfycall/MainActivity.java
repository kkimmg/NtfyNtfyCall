package k_kim_mg.ntfyntfycall;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

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
        setSupportActionBar(toolbar);

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
        final List<BluetoothDevice> notifyList = new ArrayList<BluetoothDevice>();
        final List<BluetoothDevice> acceptList = new ArrayList<BluetoothDevice>();
        final ArrayAdapter<String> notifyAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item);
        final ArrayAdapter<String> acceptAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item);
        List<Integer> notifySelect = new ArrayList<Integer>();
        List<Integer> acceptSelect = new ArrayList<Integer>();
        // Bluetooth
        BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
        if (bt == null) {
            showAlert("Exit", "Bluetooth is not suppoted.", "OK");
            finish();
        }
        if (!bt.isEnabled()) {
            showAlert("Exit", "Bluetooth is not enabled.", "OK");
            finish();
        }
        Set<BluetoothDevice> deviceSet = bt.getBondedDevices();
        int i = 0;
        for (BluetoothDevice device : deviceSet) {
            notifyList.add(device);
            notifyAdapter.add(device.getName());
            Uri uri1 = Uri.parse("content://" + SendListProvider.PROVIDER_NAME + "/" + SendListProvider.TABLE_NAME + "/" + device.getAddress());
            Cursor cursor1 = getContentResolver().query(uri1, null, null, null, null);
            if (cursor1.moveToNext()) {
                notifySelect.add(i);
            }
            acceptList.add(device);
            acceptAdapter.add(device.getName());
            Uri uri2 = Uri.parse("content://" + SendListProvider.PROVIDER_NAME + "/" + SendListProvider.TABLE_NAME + "/" + device.getAddress());
            Cursor cursor2 = getContentResolver().query(uri1, null, null, null, null);
            if (cursor1.moveToNext()) {
                acceptSelect.add(i);
            }

            i++;
        }

        // Components
        Button btnNotify = (Button) findViewById(R.id.btnNotify);
        final CheckBox chkNotify = (CheckBox) findViewById(R.id.chkNotify);
        final ListView lstNotifyDevices = (ListView) findViewById(R.id.lstNotifyDevices);
        lstNotifyDevices.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lstNotifyDevices.setAdapter(notifyAdapter);
        for (int j: notifySelect) {
            lstNotifyDevices.setItemChecked(j, true);
        }

        Button btnAccept = (Button) findViewById(R.id.btnAccept);
        final CheckBox chkAccept = (CheckBox) findViewById(R.id.chkAccept);
        final ListView lstAcceptDevices = (ListView) findViewById(R.id.lstAcceptDevices);
        lstAcceptDevices.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lstAcceptDevices.setAdapter(notifyAdapter);
        for (int j: acceptSelect) {
            lstAcceptDevices.setItemChecked(j, true);
        }

        btnNotify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("CHKNOTIFY", chkNotify.isChecked());
                editor.commit();

                /*int cnt = */getContentResolver().delete(SendListProvider.CONTENT_NAME, "", null);
                SparseBooleanArray sba = lstNotifyDevices.getCheckedItemPositions();
                for (int i = 0; i < sba.size(); i++) {
                    BluetoothDevice bd = notifyList.get(i);
                    ContentValues cv = new ContentValues();
                    cv.put("DEVICEADRESS", bd.getAddress());
                    cv.put("DEVICENAME", bd.getName());

                    getContentResolver().insert(RecvListProvider.CONTENT_NAME, cv);
                }
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
                SparseBooleanArray sba = lstAcceptDevices.getCheckedItemPositions();
                for (int i = 0; i < sba.size(); i++) {
                    BluetoothDevice bd = acceptList.get(i);
                    ContentValues cv = new ContentValues();
                    cv.put("DEVICEADRESS", bd.getAddress());
                    cv.put("DEVICENAME", bd.getName());

                    getContentResolver().insert(RecvListProvider.CONTENT_NAME, cv);
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
}
