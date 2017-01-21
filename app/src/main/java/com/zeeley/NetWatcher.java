package com.zeeley;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetWatcher extends BroadcastReceiver {
    public NetWatcher() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(constants.zeeley,"netwatcher onreceive called");
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            if (info.isConnected()) {
                //start service
                Log.d(constants.zeeley,"starting mesaging service");
                //Intent i = new Intent(context, MyFirebaseMessagingService.class);
                //context.startService(intent);
            }
            else {
                //stop service
               // Intent i = new Intent(context, MyFirebaseMessagingService.class);
                //context.stopService(intent);
            }
        }
    }
}
