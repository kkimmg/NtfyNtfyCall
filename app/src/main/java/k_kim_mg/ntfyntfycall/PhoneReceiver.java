package k_kim_mg.ntfyntfycall;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 着信の通知を行う
 */
public class PhoneReceiver extends BroadcastReceiver {
    /** SSP */
    public static final UUID UUID_SSP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /** コンテキスト */
    private Context ctx;

    /**
     * 通知を受けた
     * @param context コンテキスト
     * @param intent インテント
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        Toast.makeText(ctx, "ON RECIEVE", Toast.LENGTH_LONG).show();
        try {
            //TelephonyManagerの生成
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //リスナーの登録
            MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
            tm.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        } catch (Exception e) {
            Log.e(e.getClass().getCanonicalName(), ":" + e.getMessage());
        }

    }

    /**
     * 電話の状態をチェックする
     */
    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String callNumber) {
            try {
                //Log.d("PhoneState Changed", ":" + state + ":" + callNumber);
                String message = "";
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:      //待ち受け（終了時）
                        Toast.makeText(ctx, "CALL_STATE_IDLE", Toast.LENGTH_LONG).show();
                        message = "IDLE.";
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:   //着信
                        Toast.makeText(ctx, "CALL_STATE_RINGING: " + callNumber, Toast.LENGTH_LONG).show();
                        message = "RINGING.(" + callNumber + ")";
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:   //通話
                        Toast.makeText(ctx, "CALL_STATE_OFFHOOK", Toast.LENGTH_LONG).show();
                        message = "OFFHOOK.";
                        break;
                }
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
                boolean send = sp.getBoolean("CHKNOTIFY", false);
                if (!send) {
                    return;
                }
                BluetoothLoop lp = new BluetoothLoop();
                lp.setMessage(message);
                lp.start();
            } catch (Exception ex) {
                Toast.makeText(ctx, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class BluetoothLoop extends Thread {
        /* 送信メッセージ */
        private  String message;

        /**
         * 送信メッセージ
         * @return 送信メッセージ
         */
        public String getMessage() {
            return (message == null ? "" : message);
        }

        /**
         * 送信メッセージ
         * @param message 送信メッセージ
         */
        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public void run () {
            try {
                BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
                try {
                    bt.cancelDiscovery();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Uri uri = Uri.parse("content://" + SendListProvider.PROVIDER_NAME + "/" + SendListProvider.TABLE_NAME);
                Cursor cursor = ctx.getContentResolver().query(uri, null, null, null, null);
                while (cursor.moveToNext()) {
                    String address = cursor.getString(cursor.getColumnIndex("DEVICEADDRESS"));
                    try {
                        System.err.println("ADDRESS=" + address);
                        BluetoothDevice device = bt.getRemoteDevice(address);
                        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID_SSP);
                        socket.connect();
                        OutputStream os = socket.getOutputStream();
                        os.write(getMessage().getBytes());
                        os.flush();
                        os.close();
                        socket.close();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}


