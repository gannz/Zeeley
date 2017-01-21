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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class form_movie extends AppCompatActivity implements View.OnClickListener,DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {
    Toolbar toolbar;
    TextView movieName,theatreName,Dates;
    EditText peopleCount,remarks;
    Button submit,skip;
    final int code=47;
    Place theatre;
    String date;
    BroadcastReceiver receiver;
    Bundle bundle;
    LocalBroadcastManager broadcastManager;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Movie");
        }
        movieName= (TextView) findViewById(R.id.movieName);
        theatreName= (TextView) findViewById(R.id.theatreName);
       // timeText= (TextView) findViewById(R.id.time);
        Dates= (TextView) findViewById(R.id.date);
        movieName.setOnClickListener(this);
        theatreName.setOnClickListener(this);
       // timeText.setOnClickListener(this);
        Dates.setOnClickListener(this);
        submit= (Button) findViewById(R.id.done);
        submit.setOnClickListener(this);
        remarks= (EditText) findViewById(R.id.remarks);
        skip= (Button) findViewById(R.id.skipbtn);
        skip.setOnClickListener(this);
        bundle=getIntent().getBundleExtra(constants.BUNDLE);
        peopleCount= (EditText) findViewById(R.id.peopleCount);
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(constants.zeeley, "onrecev cald");
                if(intent.getAction().equals(constants.submitAction)){
                    Log.d(constants.zeeley,"submit action recvd");
                    Intent intent1 = new Intent(form_movie.this, MainActivity.class);
                    intent1.putExtra(constants.BUNDLE, bundle);
                    startActivity(intent1);
                    finish();
                }
            }
        };
        broadcastManager =LocalBroadcastManager.getInstance(form_movie.this);
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
        int id=v.getId();
        switch (id){
            case R.id.skipbtn:
                Intent intent1 = new Intent(this, MainActivity.class);
                intent1.putExtra(constants.BUNDLE, bundle);
                startActivity(intent1);
                finish();
                break;
            case R.id.movieName:

                break;
            case R.id.theatreName:
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

                        Toast.makeText(form_movie.this, "Time not selected", Toast.LENGTH_SHORT).show();
                    }
                });
                tpd.show(getFragmentManager(), "Timepickerdialog");
                break;
            case R.id.date:
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
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
            case R.id.done:
                InterestManager.Movie mov=new InterestManager.Movie();
                //todo no of people not asked and date,time too
                if(!constants.isNetworkAvailable(this)){
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                else{
                    mov.createMovieShare(movieName.getText().toString(),date,remarks.getText().toString(),
                            theatreName.getText().toString(),form_movie.this);
                }

                break;

        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == code) {
            if (resultCode == RESULT_OK) {
                theatre = PlaceAutocomplete.getPlace(this, data);
                theatreName.setText(theatre.getName());

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
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String dateString = String.format("%d-%d-%d", year, monthOfYear, dayOfMonth);
        date=monthOfYear+"/"+dayOfMonth+"/"+year;
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-M-d").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Then get the day of week from the Date based on specific locale.
        String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);

        Dates.setText(dayOfMonth + " " + dayOfWeek);

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String time = hourOfDay + ":" + minute;
       // timeText.setText(time);
    }
}
