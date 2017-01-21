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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class form_quicky extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    Toolbar toolbar;
    TextView placeName, timeText;
    EditText Remarks;
    Button submit,skip;
    final int code = 47;
    RadioGroup radioGroup;
    String date, time;
    Bundle bundle;
    BroadcastReceiver receiver;
    LocalBroadcastManager broadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quicky);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Quicky");
        }
        placeName = (TextView) findViewById(R.id.placeName);
        placeName.setOnClickListener(this);
        timeText = (TextView) findViewById(R.id.time);
        timeText.setOnClickListener(this);
        Remarks = (EditText) findViewById(R.id.remarks);
        submit = (Button) findViewById(R.id.done);
        skip= (Button) findViewById(R.id.skipbtn);
        skip.setOnClickListener(this);
        bundle=getIntent().getBundleExtra(constants.BUNDLE);
        submit.setOnClickListener(this);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(constants.zeeley, "onrecev cald");
                if(intent.getAction().equals(constants.submitAction)){
                    Log.d(constants.zeeley,"submit action recvd");
                    Intent intent1 = new Intent(form_quicky.this, MainActivity.class);
                    intent1.putExtra(constants.BUNDLE, bundle);
                    startActivity(intent1);
                    finish();
                }
            }
        };
        broadcastManager =LocalBroadcastManager.getInstance(form_quicky.this);
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
        int id = v.getId();
        switch (id) {
            case R.id.skipbtn:
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra(constants.BUNDLE, bundle);
                startActivity(i);
                finish();
                break;
            case R.id.placeName:
                Intent intent = null;
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, code);
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

                        Toast.makeText(form_quicky.this, "Time not selected", Toast.LENGTH_SHORT).show();
                    }
                });
                tpd.show(getFragmentManager(), "Timepickerdialog");
                break;
            case R.id.done:
                Calendar c = Calendar.getInstance();
                int dat = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);
                if (radioGroup.getCheckedRadioButtonId() == R.id.tomo) {
                    dat = dat + 1;
                }
                date = month + "/" + dat + "/" + year;
                if(!constants.isNetworkAvailable(this)){
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                else{
                    InterestManager.Quiky quiky = new InterestManager.Quiky();
                    quiky.createQuicky(placeName.getText().toString(), date, time, Remarks.getText().toString(), form_quicky.this);
                }

                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == code) {
            if (resultCode == RESULT_OK) {
                Place quicky = PlaceAutocomplete.getPlace(this, data);
                placeName.setText(quicky.getName());
                // place.setFocusable(false);
                //Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                // TODO: Handle the error.
                //Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        time = hourOfDay + ":" + minute;
        timeText.setText(time);
    }
}
