package com.zeeley;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

public class userProfile extends AppCompatActivity {
    onlineUser user;
    dummyProfile dummy;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    boolean fromOnlineUser,callFinish=false;
    profileObj prof;
    TextView interest, gender, age, location, phno, dist;
    LinearLayout formsLayout;
    String na = "N/A", Id;
    ImageView profpic;
    ListView formsListView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        formsLayout = (LinearLayout) findViewById(R.id.formlayout);
        formsListView = (ListView) findViewById(R.id.list);
        formsListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        Bundle bundle = getIntent().getBundleExtra(constants.BUNDLE);
        fromOnlineUser = bundle.getBoolean(constants.From_onlineUser);
        if(bundle.containsKey(constants.callFinish)){
            callFinish=true;
        }
        profpic = (ImageView) findViewById(R.id.image);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        interest = (TextView) findViewById(R.id.interest);
        gender = (TextView) findViewById(R.id.gender);
        age = (TextView) findViewById(R.id.age);
        phno = (TextView) findViewById(R.id.phone_no);
        dist = (TextView) findViewById(R.id.distance);
        location = (TextView) findViewById(R.id.location);

        dynamicToolbarColor();
        toolbarTextAppernce();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        ActionBar actionBar = getSupportActionBar();

        if (fromOnlineUser) {
            user = Parcels.unwrap(bundle.getParcelable(constants.USER));
            Id = String.valueOf(user.getId());
            collapsingToolbarLayout.setTitle(user.getName());
            setData(user);
            database db = new database(userProfile.this);
            profileObj obj = new profileObj(String.valueOf(user.getId()), user.getFirst_Name(), user.getLastName(), user.getGender(), user.getInterest(),
                    String.valueOf(user.getLatitude()), String.valueOf(user.getLongitude()), 21, user.getSettings());
            db.insertintoProf(obj);
            if (actionBar != null) {
                actionBar.setTitle(user.getName());
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        } else {

            dummy = Parcels.unwrap(bundle.getParcelable(constants.Dummy));
            Id = dummy.getId();
            collapsingToolbarLayout.setTitle(dummy.getName());

            if (bundle.getBoolean(constants.callDB, true))
                setDatafromDatabase(dummy.getId());
            else setDummyData(dummy);
            if (isNetworkAvailable()) {
                new profiletask(dummy.getId()).execute();
            }
            if (actionBar != null) {
                actionBar.setTitle(dummy.getName());
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        if (isNetworkAvailable()) {
            new loadProfileTeams().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Id);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callFinish){
                    finish();
                    return;
                }
                Intent i = new Intent(userProfile.this, chatactivity.class);
                Bundle b = new Bundle();
                b.putString(constants.SOURCE, constants.FROM_REGULAR);
                if (fromOnlineUser) {
                    dummyProfile dummy = new dummyProfile(user.getName(), String.valueOf(user.getId())
                            , user.getSettings().getSettingsString(), user.getImage());
                    b.putParcelable(constants.Dummy, Parcels.wrap(dummy));
                    i.putExtra(constants.BUNDLE, b);
                    startActivity(i);
                } else {
                    b.putParcelable(constants.Dummy, Parcels.wrap(dummy));
                    i.putExtra(constants.BUNDLE, b);
                    startActivity(i);
                }
            }
        });
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


    public void setDatafromDatabase(String pk) {
        database db = new database(userProfile.this);
        profileObj user = db.getProfile(pk);
        if (user != null) {
            settingsObj setings = user.getSettings();
            if (setings.canViewProfile)
                profpic.setImageResource(R.drawable.userimg);
            else profpic.setImageResource(R.drawable.default_profpic);
            if (setings.canAccessLocation)
                location.setText(user.getLocation());
            else location.setText(na);

            //todo dowmload pic
            //profpic.setImageResource(user.getImage());
            gender.setText(user.getGender());
            dist.setText(user.getDistance());

            age.setText(user.getAge());
            interest.setText(user.getCurrentInterest());
            phno.setText(user.getContactNo());
        } else setDummyData(dummy);

    }

    public void setDummyData(dummyProfile user) {
        settingsObj settings = new settingsObj(user.getSettings());
        if (settings.canViewProfile)
            profpic.setImageBitmap(user.getImage());
        else profpic.setImageResource(R.drawable.default_profpic);
        gender.setText(na);
        dist.setText(na);
        location.setText(na);
        age.setText(na);
        interest.setText(na);
        phno.setText(na);

    }

    public void setData(onlineUser user) {

        profpic.setImageBitmap(user.getImage());
        gender.setText(user.getGender());
        dist.setText(user.getDistance());
        location.setText(user.getLocation());
        age.setText(user.getAge());
        interest.setText(user.getInterest());
        phno.setText(user.getPhonenumber());

    }

    public void setDatafromNet(profileObj user) {
        settingsObj setings = user.getSettings();
        if (setings.canViewProfile)
            profpic.setImageResource(R.drawable.userimg);
        else profpic.setImageResource(R.drawable.default_profpic);
        if (setings.canAccessLocation)
            location.setText(user.getLocation());
        else location.setText(na);

        //todo dowmload pic
        //profpic.setImageResource(user.getImage());
        gender.setText(user.getGender());
        dist.setText(user.getDistance());
        age.setText(user.getAge());
        interest.setText(user.getCurrentInterest());
        phno.setText(user.getContactNo());

    }

    public void goback(View v) {
        finish();
    }

    public void showProfilePic(View v) {

        Intent i = new Intent(this, profile_photo.class);
        i.putExtra("pic", user.getImage());
        startActivity(i);

    }

    private void dynamicToolbarColor() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.userimg);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {

            @Override
            public void onGenerated(Palette palette) {
                collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(ContextCompat.getColor(userProfile.this, R.color.colorPrimary)));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getMutedColor(ContextCompat.getColor(userProfile.this, R.color.colorPrimaryDark)));
            }
        });
    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private class profiletask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        String pk;

        public profiletask(String pk) {
            this.pk = pk;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(userProfile.this);
            progressDialog.setMessage("Updating..");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            if (prof != null) {
                setDatafromNet(prof);
            } else setDatafromDatabase(pk);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL url = new URL("http://zeeley.com/android_info/?id=" + pk);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);
                StringBuilder s = new StringBuilder();

                BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                for (String sb; (sb = r.readLine()) != null; ) {
                    s.append(sb);
                }
                r.close();

                JSONObject object = new JSONObject(s.toString());
                String first_name = object.getString("first_name");
                String last_name = object.getString("last_name");
                String gender = object.getBoolean("gender") ? "Male" : "Female";
                int age = object.getInt("age");
                String contact = object.getString("contact");
                String current_intrest = object.getString("current_intrest");
                String seting = object.getString("priv_string");
                settingsObj setObj = new settingsObj(seting);
                String latitude = object.getString("latitude");
                String longitude = object.getString("longitude");
                prof = new profileObj(pk, first_name, last_name, gender, current_intrest, latitude, longitude, age, setObj);
                database db = new database(userProfile.this);
                // todo insert or update dataabase
                db.insertintoProf(prof);

            } catch (Exception e) {

            }

            return null;
        }


    }

    class loadProfileTeams extends AsyncTask<String, Void, Void> {

        private String teamID;
        ArrayList<Trip> trips = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
            Log.d(constants.zeeley, "pre exec complt");
        }

        @Override
        protected Void doInBackground(String... params) {
            teamID = params[0];
            Log.d(constants.zeeley, "inside doinnbg");
            try {
                Log.d(constants.zeeley, "loading teams");
                URL url = new URL("http://zeeley.com/android_trips/?id=" + teamID);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    for (String s; (s = r.readLine()) != null; ) {
                        sb.append(s);
                    }

                    JSONArray array = new JSONArray(sb.toString());
                    JSONObject parameters, fields;
                    trips = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        parameters = array.getJSONObject(i);
                        fields = parameters.getJSONObject("fields");
                        String trip_city_from = fields.getString("trip_city_from");
                        Log.w(constants.zeeley,"trip mode isNull "+fields.isNull("trip_mode"));
                        String trip_mode = fields.getString("trip_mode");
                        String remark = fields.getString("remark");
                        String trip_city = fields.getString("trip_city");
                        int admin_id=fields.getInt("trip_admin");
                        String trip_to = fields.getString("trip_to");
                        String trip_name = fields.getString("trip_name");
                        String trip_from = fields.getString("trip_from");
                        int trip_id = parameters.getInt("pk");

                        if (trip_city_from.equalsIgnoreCase("Quicky")) {
                            Log.d(constants.zeeley,"quicky found");
                            Trip trip = new Trip("Quicky", trip_name, remark, trip_city, trip_id, trip_from, trip_to,admin_id);
                            // tripname is destination..
                            trips.add(trip);
                        } else if (trip_city_from.equalsIgnoreCase("Cafe")) {
                            Log.d(constants.zeeley,"cafe found");
                            Trip trip = new Trip("Restaurant", trip_name, remark, trip_city, trip_id, trip_from, trip_to,admin_id);
                            // trip name is restaurant name...trip_id for geting members..tripfrm and to are dates parse tripfrm
                            trips.add(trip);
                        } else if (trip_city_from.equalsIgnoreCase("Gaming")) {
                            Log.d(constants.zeeley,"game found");
                            Trip trip = new Trip("Gaming", trip_name, remark, trip_city, trip_id, trip_from, trip_to,admin_id);
// tripcity is server address..
                            trips.add(trip);
                        } else if (trip_city_from.equalsIgnoreCase("Film")) {
                            Log.d(constants.zeeley,"film found");
                            Trip trip = new Trip("Film", trip_name, remark, trip_city, trip_id, trip_from, trip_to,admin_id);
                            //tripcity is theatre name..
                            trips.add(trip);
                        } else {
                            if (trip_mode.equalsIgnoreCase("Cab")) {
                                Log.d(constants.zeeley,"cab found");
                                Trip trip = new Trip("Cab", trip_name, remark, trip_city, trip_id, trip_from, trip_to,admin_id);
                                // trip name..team name..trip city destination..trip_city_from starting point..trip_from time..
                                trip.setTrip_city_from(trip_city_from);
                                trips.add(trip);
                            } else if (trip_mode.equals("null")) {
                                Log.d(constants.zeeley,"travel found");
                                Trip trip = new Trip("Cotravelling", trip_name, remark, trip_city, trip_id, trip_from, trip_to,admin_id);
                                trip.setTrip_city_from(trip_city_from);
                                //
                                trips.add(trip);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                Log.d("zeeley", e.getMessage());
                Log.d(constants.zeeley, "excep loading trips");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            Log.d(constants.zeeley, "in pst exect");
            if (trips != null && !trips.isEmpty()) {
                Log.d(constants.zeeley, "trips not empty with size= " + trips.size());
                formsLayout.setVisibility(View.VISIBLE);
                formsListView.setVisibility(View.VISIBLE);
                formAdapter adapter = new formAdapter(trips, userProfile.this);
                formsListView.setAdapter(adapter);
                formsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Trip trip = (Trip) formsListView.getItemAtPosition(position);
                        Log.d(constants.zeeley,trip.getTripType()+" selected");
                        String type = trip.getTripType();
                        switch (type) {
                            case "Quicky":
                                Intent i = new Intent(userProfile.this, form_details_quicky.class);
                                i.putExtra(constants.Trip, Parcels.wrap(trip));
                                startActivity(i);
                                break;
                            case "Restaurant":
                                Intent r = new Intent(userProfile.this, form_details_restaurant.class);
                                r.putExtra(constants.Trip, Parcels.wrap(trip));
                                startActivity(r);
                                break;
                            case "Gaming":
                                Intent game = new Intent(userProfile.this, form_details_game.class);
                                game.putExtra(constants.Trip, Parcels.wrap(trip));
                                startActivity(game);
                                break;
                            case "Film":
                                Intent film = new Intent(userProfile.this, form_details_movie.class);
                                film.putExtra(constants.Trip, Parcels.wrap(trip));
                                startActivity(film);
                                break;
                            case "Cab":
                                Intent cab = new Intent(userProfile.this, form_details_cab.class);
                                cab.putExtra(constants.Trip, Parcels.wrap(trip));
                                startActivity(cab);
                                break;
                            case "Cotravelling":
                                Intent travel = new Intent(userProfile.this, form_details_travel.class);
                                travel.putExtra(constants.Trip, Parcels.wrap(trip));
                                startActivity(travel);
                                break;

                        }
                    }
                });
                //TODO: add trips arraylist to tripsAdapter
            } else {
                Log.d(constants.zeeley, "trips  empty");
            }
        }
    }

    class formAdapter extends BaseAdapter {

        ArrayList<Trip> trips;
        LayoutInflater inflater;

        public formAdapter(ArrayList<Trip> list, Context context) {
            this.trips = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return trips.size();
        }

        @Override
        public Object getItem(int position) {
            return trips.get(position);
        }

        @Override
        public long getItemId(int position) {
            Trip trip = (Trip) getItem(position);

            return Long.valueOf(trip.getTrip_id());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.submitd_form, parent, false);
                viewholder holder = new viewholder(convertView);
                convertView.setTag(holder);
            }
            viewholder holder = (viewholder) convertView.getTag();
            Trip trip = (Trip) getItem(position);

            if (trip.getTripType().equals("Quicky")) {
                holder.setData("Quicky", trip.getTrip_city_from() + " - " + trip.getTrip_city());
            } else if (trip.getTripType().equals("Cab")) {
                holder.setData("Cab Sharing", trip.getTrip_city_from() + " - " + trip.getTrip_city());
            } else if (trip.getTripType().equals("Cafe")) {
                holder.setData("Restaurant", trip.getHeading());
            } else if (trip.getTripType().equals("Film")) {
                holder.setData("Movie", trip.getHeading());
            } else if (trip.getTripType().equals("Gaming")) {
                holder.setData("Gaming", trip.getHeading());
            } else if (trip.getTripType().equals("Cotravelling")) {
                holder.setData("Travel", trip.getTrip_city_from() + " - " + trip.getTrip_city());
            }

            return convertView;
        }

        class viewholder {
            TextView formType, formName;

            public viewholder(View view) {
                formName = (TextView) view.findViewById(R.id.name);
                formType = (TextView) view.findViewById(R.id.formType);
            }

            public void setData(String formtype, String name) {
                this.formName.setText(name);
                this.formType.setText(formtype);
            }
        }
    }
}
