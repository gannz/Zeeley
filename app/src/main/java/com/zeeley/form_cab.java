package com.zeeley;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class form_cab extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView startPoint, destination, start_time, start_Date;
    EditText pplcount, Remarks;
    Button done,skip;
    int endPlace = 41;
    int startPlace = 85;
    Double stLat, stLon, endLat, endLong;
    Place placeStart, placeEnd;
    String selectedDate,selectedTime;
    private GoogleApiClient mGoogleApiClient;
    BroadcastReceiver receiver;
    LocalBroadcastManager broadcastManager;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Cab Form");
        }
        startPoint = (TextView) findViewById(R.id.placeName);
        destination = (TextView) findViewById(R.id.destination);
        start_time = (TextView) findViewById(R.id.time);
        bundle=getIntent().getBundleExtra(constants.BUNDLE);
        skip= (Button) findViewById(R.id.skipbtn);
        skip.setOnClickListener(this);
        start_Date = (TextView) findViewById(R.id.selectDates);
        pplcount = (EditText) findViewById(R.id.peopleCount);
        Remarks = (EditText) findViewById(R.id.remarks);
        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(this);
        start_Date.setOnClickListener(this);
        startPoint.setOnClickListener(this);
        destination.setOnClickListener(this);
        start_time.setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(constants.zeeley,"onrecev cald");
                if(intent.getAction().equals(constants.submitAction)){
                    Log.d(constants.zeeley,"submit action recvd");
                    Intent intent1 = new Intent(form_cab.this, MainActivity.class);
                    intent1.putExtra(constants.BUNDLE, bundle);
                    startActivity(intent1);
                    finish();
                }
            }
        };
       broadcastManager =LocalBroadcastManager.getInstance(form_cab.this);
    }

    public void selectDates() {
        Calendar now = Calendar.getInstance();

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
    protected void onResume() {
        super.onResume();
        IntentFilter filter=new IntentFilter(constants.submitAction);
        broadcastManager.registerReceiver(receiver,filter);
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
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(constants.BUNDLE, bundle);
                startActivity(intent);
                finish();
                break;

            case R.id.placeName:
                launchGogle(false);
                break;
            case R.id.destination:
                launchGogle(true);
                break;

            case R.id.selectDates:
                Calendar now = Calendar.getInstance();
                com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.vibrate(true);
                dpd.dismissOnPause(true);
                Calendar[] dates = new Calendar[5];
                for (int i = 0; i <= 4; i++) {
                    Calendar date = Calendar.getInstance();
                    date.add(Calendar.DAY_OF_MONTH, i);
                    dates[i] = date;
                }
                dpd.setSelectableDays(dates);
                dpd.show(getFragmentManager(), "Datepickerdialog");
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

                        Toast.makeText(form_cab.this, "Time not selected", Toast.LENGTH_SHORT).show();
                    }
                });
                tpd.show(getFragmentManager(), "Timepickerdialog");
                break;
            case R.id.done:
                // end dates are not sent to server...
                if(!constants.isNetworkAvailable(this)){
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                else{
                    InterestManager.CabSharing cab = new InterestManager.CabSharing();
                    cab.createCabSharing(startPoint.getText().toString(),destination.getText().toString(),stLat.toString(),stLon.toString(),
                            endLat.toString(),endLong.toString(),pplcount.getText().toString(),Remarks.getText().toString(),selectedDate,selectedTime,form_cab.this);

                }


                break;
        }
    }

    public void launchGogle(boolean fromDestination) {
        int code;
        if (fromDestination)
            code = endPlace;
        else
            code = startPlace;

        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, code);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Services not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == startPlace) {
            if (resultCode == RESULT_OK) {
                placeStart = PlaceAutocomplete.getPlace(this, data);
                stLat = placeStart.getLatLng().latitude;
                stLon = placeStart.getLatLng().longitude;
                startPoint.setText(placeStart.getName());
               Log.d(constants.zeeley,"start place is "+placeStart.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.w(constants.zeeley, "place autocomplete error " + status.getStatusMessage());
                Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                // TODO: Handle the error.
                //Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.d(constants.zeeley, "result canceled");
            }
        } else if (requestCode == endPlace) {
            if (resultCode == RESULT_OK) {
                placeEnd = PlaceAutocomplete.getPlace(this, data);
                endLat = placeEnd.getLatLng().latitude;
                endLong = placeEnd.getLatLng().longitude;
                destination.setText(placeEnd.getName());
                Log.d(constants.zeeley, "end place is " + placeEnd.getName());
                // place.setFocusable(false);
                //Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.w(constants.zeeley, "place autocomplete error in endPlace " + status.getStatusMessage());
                Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                // TODO: Handle the error.
                //Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.d(constants.zeeley, "result canceled in endplce");
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("zeeley", connectionResult.getErrorMessage());
        //Toast.makeText(this, "connection failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String dateString = String.format("%d-%d-%d", year, monthOfYear, dayOfMonth);
        selectedDate=monthOfYear+"/"+dayOfMonth+"/"+year;
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-M-d").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Then get the day of week from the Date based on specific locale.
        String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);

        start_Date.setText(dayOfMonth + " " + dayOfWeek);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        selectedTime = hourOfDay + ":" + minute;
        start_time.setText(selectedTime);
    }
}
