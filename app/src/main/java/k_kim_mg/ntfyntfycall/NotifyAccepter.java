package k_kim_mg.ntfyntfycall;

import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NotifyAccepter extends Service {
    SharedPreferences sp;
    public NotifyAccepter() {
        super();
    }
/*
    @Override
    protected void onHandleIntent(Intent intent) {

    }
    */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sp = PreferenceManager.getDefaultSharedPreferences(NotifyAccepter.this);
        boolean cont = sp.getBoolean("CHKACCEPT", false);
        int ret = super.onStartCommand(intent, flags, startId);
        if (cont) {
            NotifyAcceptThread nath = new NotifyAcceptThread();
            nath.start();
        } else {
            stopSelf();
        }
        return ret;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * サーバーソケットを受け付けるスレッド
     */
    private class NotifyAcceptThread extends Thread {
        private static final String SERVICE = "NOTIFYACCEPTSOCKET";
        private BluetoothAdapter bt;
        // The local server socket
        private BluetoothServerSocket sock;
        public NotifyAcceptThread () {
            try {
                bt = BluetoothAdapter.getDefaultAdapter();
                sock = bt.listenUsingRfcommWithServiceRecord(SERVICE, PhoneReceiver.UUID_SSP);
            } catch (IOException e) {
                Log.e(SERVICE, e.getMessage(), e);
            }
        }
        public void run () {

            boolean cont = sp.getBoolean("CHKACCEPT", false);
            while (sock != null &&  cont) {
                try {
                    BluetoothSocket socket = sock.accept();
                    InputStream is = socket.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String message = br.readLine();
                    Toast.makeText(NotifyAccepter.this, message, Toast.LENGTH_LONG).show();
                    br.close();
                    isr.close();
                    is.close();
                    socket.close();

                    cont = sp.getBoolean("CHKACCEPT", false);
                } catch (IOException e) {
                    Log.e(SERVICE, e.getMessage(), e);
                    cont = false;
                }
            }

            try {
                sock.close();
            } catch (IOException e) {
                Log.e(SERVICE, e.getMessage(), e);
            }

            stopSelf();
        }
    }

}

