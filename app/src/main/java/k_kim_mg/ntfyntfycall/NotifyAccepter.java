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
        System.out.println("startservice3");
        sp = PreferenceManager.getDefaultSharedPreferences(NotifyAccepter.this);
        boolean cont = sp.getBoolean("CHKACCEPT", false);
        int ret = super.onStartCommand(intent, flags, startId);
        if (cont) {
            System.out.println("startservice4");
            NotifyAcceptThread nath = new NotifyAcceptThread();
            nath.start();
            System.out.println("startservice5");
        } else {
            System.out.println("startservice6");
            stopSelf();
            System.out.println("startservice7");
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
            System.out.println("startservice8");
            try {
                bt = BluetoothAdapter.getDefaultAdapter();
                System.out.println("startservice9");
                sock = bt.listenUsingRfcommWithServiceRecord(SERVICE, PhoneReceiver.UUID_SSP);
                System.out.println("startservice10");
            } catch (IOException e) {
                Log.e(SERVICE, e.getMessage(), e);
            }
        }
        public void run () {

            boolean cont = sp.getBoolean("CHKACCEPT", false);
            while (sock != null &&  cont) {
                try {
                    System.out.println("startservice11");
                    BluetoothSocket socket = sock.accept();
                    System.out.println("startservice12:" + socket.getRemoteDevice().getAddress());
                    InputStream is = socket.getInputStream();
                    System.out.println("startservice13");
                    InputStreamReader isr = new InputStreamReader(is);
                    System.out.println("startservice14");
                    BufferedReader br = new BufferedReader(isr);
                    System.out.println("startservice15");
                    String message = br.readLine();
                    System.out.println("startservice16");
                    Toast.makeText(NotifyAccepter.this, message, Toast.LENGTH_LONG).show();
                    System.out.println("startservice17");
                    br.close();
                    System.out.println("startservice18");
                    isr.close();
                    System.out.println("startservice19");
                    is.close();
                    System.out.println("startservice20");
                    socket.close();
                    System.out.println("startservice21");

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

