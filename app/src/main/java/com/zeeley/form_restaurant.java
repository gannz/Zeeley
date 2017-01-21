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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class form_restaurant extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    TextView RestaurantName, Dates;//timeText;
    EditText Remarks;
    Button submit, skip;
    Toolbar toolbar;
    RadioGroup radioGroup;
    final int code = 47;
    Place Restaurant;
    String date;
    Bundle bundle;
    BroadcastReceiver receiver;
    LocalBroadcastManager broadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Restaurant");
        }
        RestaurantName = (TextView) findViewById(R.id.restaurantName);
        RestaurantName.setOnClickListener(this);
        Dates = (TextView) findViewById(R.id.dates);
        Dates.setOnClickListener(this);
        // timeText = (TextView) findViewById(R.id.time);
        // timeText.setOnClickListener(this);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        submit = (Button) findViewById(R.id.done);
        skip = (Button) findViewById(R.id.skipbtn);
        skip.setOnClickListener(this);
        bundle = getIntent().getBundleExtra(constants.BUNDLE);
        submit.setOnClickListener(this);
        Remarks = (EditText) findViewById(R.id.remarks);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(constants.zeeley, "onrecev cald");
                if (intent.getAction().equals(constants.submitAction)) {
                    Log.d(constants.zeeley, "submit action recvd");
                    Intent intent1 = new Intent(form_restaurant.this, MainActivity.class);
                    intent1.putExtra(constants.BUNDLE, bundle);
                    startActivity(intent1);
                    finish();
                }
            }
        };
        broadcastManager = LocalBroadcastManager.getInstance(form_restaurant.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(constants.submitAction);

        broadcastManager.registerReceiver(receiver, filter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
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
                Intent intent1 = new Intent(this, MainActivity.class);
                intent1.putExtra(constants.BUNDLE, bundle);
                startActivity(intent1);
                finish();
                break;
            case R.id.restaurantName:
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
            case R.id.dates:
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

                        Toast.makeText(form_restaurant.this, "Time not selected", Toast.LENGTH_SHORT).show();
                    }
                });
                tpd.show(getFragmentManager(), "Timepickerdialog");
                break;
            case R.id.done:
                boolean isveg = true;
                if (radioGroup.getCheckedRadioButtonId() == R.id.nonveg) {
                    isveg = false;
                }
                if (!constants.isNetworkAvailable(this)) {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else {
                    InterestManager.Restaurant restaurant = new InterestManager.Restaurant();
                    restaurant.createRestaurantEvent(RestaurantName.getText().toString(), date, Remarks.getText().toString(), isveg, form_restaurant.this);
                }

                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == code) {
            if (resultCode == RESULT_OK) {
                Restaurant = PlaceAutocomplete.getPlace(this, data);
                RestaurantName.setText(Restaurant.getName());
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
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String dateString = String.format("%d-%d-%d", year, monthOfYear, dayOfMonth);
        date = monthOfYear + "/" + dayOfMonth + "/" + year;
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
