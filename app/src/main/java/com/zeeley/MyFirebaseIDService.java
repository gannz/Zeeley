package com.zeeley;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gannu on 26-08-2016.
 */
public class MyFirebaseIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(constants.zeeley,"registration token is "+refreshedToken);
        SharedPreferences preferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES,MODE_PRIVATE);
        if(preferences.getBoolean(constants.IS_LOGGEDIN,false)){
            sendRegistrationToken(refreshedToken);
        }
    }

    private void sendRegistrationToken(String token){
        new AsyncTask<String,Void,Void>(){

            boolean isDone = false;
            @Override
            protected Void doInBackground(String... params) {
                String token=params[0];
                try{
                    URL url = new URL("http://zeeley.com/android_reg_key/?key=" + token);
                    HttpURLConnection c = (HttpURLConnection)url.openConnection();
                    c.setRequestMethod("GET");
                    c.setInstanceFollowRedirects(true);
                    if(c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        isDone  = true;
                    }

                }catch (Exception e){
                    //Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(isDone){
                    Toast.makeText(MyFirebaseIDService.this,"token sent to server",Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(MyFirebaseIDService.this,"token not sent",Toast.LENGTH_LONG).show();
            }
        }.execute(token);
    }
}
