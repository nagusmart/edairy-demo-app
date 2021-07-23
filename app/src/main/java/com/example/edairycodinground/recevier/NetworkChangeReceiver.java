package com.example.edairycodinground.recevier;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.edairycodinground.util.NetworkUtil;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtil.getConnectivityStatusString(context);
        Log.e("Sulod sa network reciever", "Sulod sa network reciever");
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            Intent localIntent = new Intent("CHECK_CONNECTION");
            if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                localIntent.putExtra("NETWORK_STATUS",false);
            } else {
                localIntent.putExtra("NETWORK_STATUS",true);
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        }
    }
}
