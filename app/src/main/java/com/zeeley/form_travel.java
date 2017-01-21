package com.zeeley;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

public class form_travel extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    EditText pplcount, Remarks;// remarks;
    TextView /*startDate, endDate,*/ selectDates, place, destination;
    int endPlace = 41;
    int startPlace = 85;
    Button done,skip;
    Double stLat, stLon, endLat, endLong;
    //LinearLayout layout;
    Place searchPlace;
    String date;
    Bundle bundle;
    private GoogleApiClient mGoogleApiClient;
    BroadcastReceiver receiver;
    LocalBroadcastManager broadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Travel Form");
        }
        pplcount = (EditText) findViewById(R.id.peopleCount);
        // remarks = (EditText) findViewById(R.id.remarks);
        // layout = (LinearLayout) findViewById(R.id.remarksLayout);
        place = (TextView) findViewById(R.id.placeName);
        destination = (TextView) findViewById(R.id.destination);
        destination.setOnClickListener(this);
        place.setOnClickListener(this);
        skip= (Button) findViewById(R.id.skipbtn);
        skip.setOnClickListener(this);
        bundle=getIntent().getBundleExtra(constants.BUNDLE);
        //startDate = (TextView) findViewById(R.id.startDate);
        //startDate.setOnClickListener(this);
        selectDates = (TextView) findViewById(R.id.selectDates);
        selectDates.setOnClickListener(this);
        //endDate = (TextView) findViewById(R.id.endDate);
        //endDate.setOnClickListener(this);
        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(this);
        Remarks = (EditText) findViewById(R.id.remarks);
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
                    Intent intent1 = new Intent(form_travel.this, MainActivity.class);
                    intent1.putExtra(constants.BUNDLE, bundle);
                    startActivity(intent1);
                    finish();
                }
            }
        };
        broadcastManager =LocalBroadcastManager.getInstance(form_travel.this);
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
    protected void onStart() {
        super.onStart();
       /*if( mGoogleApiClient != null )
            mGoogleApiClient.connect();*/
    }

    @Override
    protected void onStop() {
       /* if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }*/
        super.onStop();
    }

    public void selectDates() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear++;
                date = monthOfYear + "/" + dayOfMonth + "/" + year;

                selectDates.setText(date);
            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dpd.vibrate(true);
        dpd.dismissOnPause(true);
        Calendar[] dates = new Calendar[5];
        for (int i = 0; i <= 30; i++) {
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DAY_OF_MONTH, i);
            dates[i] = date;
        }
        dpd.setSelectableDays(dates);

        dpd.show(getFragmentManager(), "Datepickerdialog");

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
                selectDates();
                break;

            case R.id.done:
                // end dates are not sent to server...
                if(!constants.isNetworkAvailable(this)){
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                else{
                    InterestManager.Cotravelling cotravelling = new InterestManager.Cotravelling();
                    cotravelling.createCotravelEvent(place.getText().toString(), destination.getText().toString(), stLat.toString(),
                            stLon.toString(), endLat.toString(), endLong.toString(), pplcount.getText().toString(), Remarks.getText().toString(),
                            date, form_travel.this);
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
                searchPlace = PlaceAutocomplete.getPlace(this, data);
                stLat = searchPlace.getLatLng().latitude;
                stLon = searchPlace.getLatLng().longitude;
                place.setText(searchPlace.getName());
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
        } else if (requestCode == endPlace) {
            if (resultCode == RESULT_OK) {
                searchPlace = PlaceAutocomplete.getPlace(this, data);
                endLat = searchPlace.getLatLng().latitude;
                endLong = searchPlace.getLatLng().longitude;
                destination.setText(searchPlace.getName());
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
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("zeeley", connectionResult.getErrorMessage());
        Toast.makeText(this, "connection failed", Toast.LENGTH_SHORT).show();
    }

}
