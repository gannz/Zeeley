package com.zeeley;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class form_game extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    Toolbar toolbar;
    Spinner s;
    String gameName,ip,remarks,date,time;
    TextView timeText;
    EditText Remarks,ipAddress;
    RadioGroup radioGroup;
    Button submit,skip;
    BroadcastReceiver receiver;
    LocalBroadcastManager broadcastManager;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lan_gaming);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Lan Gaming");
        }
        s = (Spinner) findViewById(R.id.spinner);
        submit= (Button) findViewById(R.id.done);
        submit.setOnClickListener(this);
        Remarks= (EditText) findViewById(R.id.remarks);
        ipAddress= (EditText) findViewById(R.id.ipaddress);
        timeText= (TextView) findViewById(R.id.time);
        skip= (Button) findViewById(R.id.skipbtn);
        skip.setOnClickListener(this);
        bundle=getIntent().getBundleExtra(constants.BUNDLE);
        timeText.setOnClickListener(this);
        radioGroup= (RadioGroup) findViewById(R.id.radiogroup);

        final String select[] = {"Counter Strike: 1.6", "Counter Strike: Source", "Counter Strike: Global Offensive(GO)", "Need For Speed: Rivals",
                "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, select);

        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gameName = select[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gameName = select[0];
            }
        });

        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(constants.zeeley, "onrecev cald");
                if(intent.getAction().equals(constants.submitAction)){
                    Log.d(constants.zeeley,"submit action recvd");
                    Intent intent1 = new Intent(form_game.this, MainActivity.class);
                    intent1.putExtra(constants.BUNDLE, bundle);
                    startActivity(intent1);
                    finish();
                }
            }
        };
        broadcastManager =LocalBroadcastManager.getInstance(form_game.this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter=new IntentFilter(constants.submitAction);

        broadcastManager.registerReceiver(receiver,filter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause() {
        super.onPause();
        broadcastManager.unregisterReceiver(receiver);
    }
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.skipbtn:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(constants.BUNDLE, bundle);
                startActivity(intent);
                finish();
                break;
            case R.id.time:
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        this,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true);
                tpd.vibrate(true);
                tpd.dismissOnPause(true);
                tpd.enableSeconds(false);
                tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                        Toast.makeText(form_game.this, "Time not selected", Toast.LENGTH_SHORT).show();
                    }
                });
                tpd.show(getFragmentManager(), "Timepickerdialog");
                break;


            case R.id.done:
                Calendar c=Calendar.getInstance();
                int dat=c.get(Calendar.DAY_OF_MONTH);
                int month=c.get(Calendar.MONTH);
                int year=c.get(Calendar.YEAR);
                if(radioGroup.getCheckedRadioButtonId()==R.id.tomo){
                    dat=dat+1;
                }
                date=month+"/"+dat+"/"+year;
                //// TODO: 14-09-2016 timeformat to send to server is undnown
                if(!constants.isNetworkAvailable(this)){
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                else{
                    InterestManager.PCGame pcGame=new InterestManager.PCGame();
                    pcGame.createPCGameSharing(gameName,date,time,ipAddress.getText().toString(),Remarks.getText().toString(),form_game.this);

                }

                break;
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
         time = hourOfDay + ":" + minute;
        timeText.setText(time);
    }
}
