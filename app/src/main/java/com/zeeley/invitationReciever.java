package com.zeeley;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class invitationReciever extends BroadcastReceiver {
    public invitationReciever() {
    }

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d(constants.zeeley, "Invitation receiver onreceive called");

        if (intent.getAction().equals(constants.Accept)){
            Log.d(constants.zeeley, "trigered by acept invitation");
            Log.d(constants.zeeley, "executing");
            inviteInfo invite = Parcels.unwrap(intent.getParcelableExtra(constants.INVITATION));
            if (invite != null) {
                Log.d(constants.zeeley, " info values " + invite.getCurrentInterest());
                Log.d(constants.zeeley, " info values " + invite.getSenderID());
                Log.d(constants.zeeley, " info values " + invite.getTeamID());
                Log.d(constants.zeeley, " info values " + invite.getTeamName());
            }

            Log.d(constants.zeeley, "inviteinfo retreived");
            SharedPreferences preferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
            String pk = preferences.getString(my_login1.PRIMARY_KEY, "-1");

            Intent accept = new Intent(context, invitationService.class);
            Bundle b = new Bundle();
            b.putString(constants.Interest_Id, pk);
            b.putParcelable(constants.INVITATION, Parcels.wrap(invite));
            accept.putExtra(constants.BUNDLE, b);
            Log.d(constants.zeeley, "starting Service");
            context.startService(accept);
            Log.d(constants.zeeley, " Service started");
        }

        else if (intent.getAction().equals(constants.Reject))
            Log.d(constants.zeeley, "trigered by reject invitation");

        else if (intent.getAction().equals(constants.acceptAction)){
            Log.d(constants.zeeley,"received aceptaction intent in receiver");
            Bundle bundle=intent.getBundleExtra(constants.BUNDLE);
            Intent intent1=new Intent(context,invitationService.class);
            intent1.putExtra(constants.BUNDLE,bundle);
            intent1.setAction(constants.acceptAction);
            context.startService(intent1);
        }
        else if (intent.getAction().equals(constants.downloadProfileAction)){
            Intent is=new Intent(context,invitationService.class);
            Bundle bundle=intent.getBundleExtra(constants.BUNDLE);
            is.putExtra(constants.BUNDLE,bundle);
            context.startService(is);
        }

    }


}
