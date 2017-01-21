package com.zeeley;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class splashActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        sharedPreferences=getSharedPreferences(constants.MY_SHAREDPREFERENCES,MODE_PRIVATE);
        editor=sharedPreferences.edit();
        if(!sharedPreferences.contains(constants.IS_LOGGEDIN)){
            editor.putBoolean(constants.IS_LOGGEDIN,false);
        }
        if(!sharedPreferences.contains(constants.DntShowCB)){
            editor.putBoolean(constants.DntShowCB,false);
        }
        editor.apply();
        task t = new task();
        t.execute();


    }
 class task extends AsyncTask<Void,Void,Void>{

     @Override
     protected Void doInBackground(Void... params) {
         // if logged in..wait for 2 seconds return launch_mainactivity
         //else wait for 2 sec and display login screen
         try {
             Thread.sleep(2000);
             // check if logged in and decide if login or mainactivity shud be shown
         } catch (InterruptedException e) {
             e.printStackTrace();
         }

         SharedPreferences sharedPreferences=getSharedPreferences(constants.MY_SHAREDPREFERENCES,MODE_PRIVATE);
         if(sharedPreferences.getBoolean(constants.DntShowCB,false))
         {
             Log.d(constants.zeeley,"dntshwcb is checkd");
             if(sharedPreferences.getBoolean(constants.IS_LOGGEDIN,false)){
                 Intent intent=new Intent(splashActivity.this,Interst_List.class);
                 intent.putExtra(constants.SOURCE, constants.From_chooseInterest);
                 startActivity(intent);
                finish();
             }
             else {
                 Intent intent=new Intent(splashActivity.this,my_login1.class);
                 startActivity(intent);
                 finish();
             }
         }
         else {
             Log.d(constants.zeeley,"dntshwcb is not checkd");
             Intent intent=new Intent(splashActivity.this,Howitworks.class);
             startActivity(intent);
             finish();
         }

         return null;
     }
 }

}
