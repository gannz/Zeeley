package com.zeeley;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class Interst_List extends AppCompatActivity {
    final int Section = 0;
    final int normalItem = 1;
    final int title = 2;
    // boolean callSetresult=false;
    final int image = 3;

    private String from;

    private ArrayList<Integer> interestList = new ArrayList<>();
    boolean mActionModeIsActive = false;
    boolean mBackWasPressedInActionMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interest_adder);
        Button skip = (Button) findViewById(R.id.skipbtn);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Interst_List.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(constants.SOURCE, constants.fromNotification);
                intent.putExtra(constants.BUNDLE, bundle);
                startActivity(intent);
                finish();
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        from = getIntent().getStringExtra(constants.SOURCE);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);

        //callSetresult=getIntent().getBooleanExtra(constants.isChange, false);
        final ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new Adapter());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (from.equals(constants.From_chooseInterest)) {
                actionBar.setTitle("Choose Interest");
                Log.d(constants.zeeley, "chose intrst title set");
            } else if (from.equals(constants.From_addInterest)) {
                layout.setVisibility(View.GONE);
                actionBar.setTitle("Add Interests");
            } else if (from.equals(constants.From_showInterest)) {
                layout.setVisibility(View.GONE);
                actionBar.setTitle("Show Interest");
            }
        }
        setlistData();
        if (from.equals(constants.From_chooseInterest) || from.equals(constants.From_showInterest)) {
            listView.setLongClickable(false);
        }
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle(listView.getCheckedItemCount() + " items selected");
                if (checked) {
                    interestList.add((int) id);
                } else {
                    interestList.remove((int) id);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                mActionModeIsActive = true;

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionModeIsActive = false;

                if (!mBackWasPressedInActionMode) {
                    Toast.makeText(Interst_List.this, interestList.size() + " items selected", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putIntegerArrayListExtra(constants.INTEREST_LISTS, interestList);
                    setResult(RESULT_OK, intent);
                    finish();

                }

                mBackWasPressedInActionMode = false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (from.equals(constants.From_addInterest)) {
                    Intent intent = new Intent();
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    list.add((int) id);
                    intent.putIntegerArrayListExtra(constants.INTEREST_LISTS, list);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                //constants.userInterst = constants.getInterstTitle((int) id);
                else if (from.equals(constants.From_showInterest)) {
                    Log.d(constants.zeeley," from showinterest ");
                    if (!constants.isNetworkAvailable(Interst_List.this)) {
                        Toast.makeText(Interst_List.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    } else {
                        GPSTracker gps = new GPSTracker(Interst_List.this);
                        String lat, lon;
                        String interest = constants.getInterstTitle((int) id);
                        if (gps.canGetLocation()) {

                            lat = String.valueOf(gps.getLatitude());
                            lon = String.valueOf(gps.getLongitude());
                            constants.MY_LATITUDE = gps.getLatitude();
                            constants.MY_LONGITUDE = gps.getLongitude();
                            SharedPreferences sharedPreferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(constants.Latitude, lat);
                            editor.putString(constants.Longitude,lon);
                            editor.apply();
                            viewNearByPeople(interest,lat,lon,Interst_List.this );
                        } else {
                            gps.showSettingsAlert();
                        }

                    }

                } else {
                   /* Intent intent = new Intent(Interst_List.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(constants.INTEREST, "Pizza");
                    bundle.putDouble(constants.latitude, 30.5);
                    bundle.putDouble(constants.longitude,45.6 );
                    intent.putExtra(constants.BUNDLE, bundle);
                    startActivity(intent);*/
                    if (!constants.isNetworkAvailable(Interst_List.this)) {
                        Toast.makeText(Interst_List.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    } else
                        new FetchOtherUsers(Interst_List.this, constants.getInterstTitle((int) id)).execute();
                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    class Adapter extends BaseAdapter {
        LayoutInflater inflater;

        public Adapter() {
            inflater = LayoutInflater.from(Interst_List.this);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            return list.get(position).isSection ? Section : normalItem;

        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isEnabled(int position) {
            return !list.get(position).isSection;
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            item i = (item) getItem(position);
            return i.getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                if (getItemViewType(position) == Section) {
                    convertView = inflater.inflate(R.layout.section, parent, false);
                    sectionHolder holder = new sectionHolder(convertView);
                    convertView.setTag(holder);
                } else if (getItemViewType(position) == normalItem) {
                    convertView = inflater.inflate(R.layout.child, parent, false);
                    childHolder holder = new childHolder(convertView);
                    convertView.setTag(holder);

                }
            }

            if (convertView.getTag() != null) {
                if (getItemViewType(position) == Section) {
                    sectionHolder holder = (sectionHolder) convertView.getTag();
                    holder.setData(list.get(position).getTitle());
                } else if (getItemViewType(position) == normalItem) {
                    childHolder holder = (childHolder) convertView.getTag();
                    holder.setData(list.get(position).getImage(), list.get(position).getTitle());
                }
            }

            return convertView;
        }

        class sectionHolder {
            TextView textView;

            public sectionHolder(View view) {
                textView = (TextView) view.findViewById(R.id.list_item_section_text);
            }

            public void setData(String title) {
                textView.setText(title);
            }

        }

        class childHolder {
            ImageView image;
            TextView textView;

            childHolder(View view) {
                image = (ImageView) view.findViewById(R.id.childimage);
                textView = (TextView) view.findViewById(R.id.childtitle);
            }

            public void setData(int image, String title) {
                textView.setText(title);
                this.image.setImageResource(image);
            }
        }
    }

    ArrayList<item> list = new ArrayList<>();

    public void setData() {
        item food = new item("Food", true, -1);
        list.add(food);
        item pizza = new item("Food", false, -1);
        pizza.setImage(R.drawable.pizza);
        list.add(pizza);
        item hamburger = new item("hamburger", false, -1);
        hamburger.setImage(R.drawable.hamburger);
        list.add(hamburger);
        item biryani = new item("biryani", false, -1);
        biryani.setImage(R.drawable.biryani);
        list.add(biryani);
        item roll = new item("roll", false, -1);
        roll.setImage(R.drawable.wrap);
        list.add(roll);
        item outdoor = new item("Outdoor", true, -1);
        list.add(outdoor);
        item cricket = new item("cricket", false, -1);
        cricket.setImage(R.drawable.cricket);
        list.add(cricket);
        item football = new item("football", false, -1);
        football.setImage(R.drawable.football);
        list.add(football);
        item basketball = new item("Food", false, -1);
        hamburger.setImage(R.drawable.hamburger);
        list.add(hamburger);
        item tennis = new item("tennis", false, -1);
        tennis.setImage(R.drawable.tennis);
        list.add(tennis);
        item company = new item("Find Company for", true, -1);
        list.add(company);
        item movie = new item("movie", false, -1);
        movie.setImage(R.drawable.movie);
        list.add(movie);
        item restaurant = new item("Food", false, -1);
        hamburger.setImage(R.drawable.hamburger);
        list.add(hamburger);
        item study = new item("study", false, -1);
        hamburger.setImage(R.drawable.study);
        list.add(study);
        item rockband = new item("Rockband", true, -1);
        list.add(rockband);
        item singer = new item("singer", false, -1);
        hamburger.setImage(R.drawable.singer);
        list.add(hamburger);
        item drummer = new item("drummer", false, -1);
        drummer.setImage(R.drawable.hamburger);
        list.add(drummer);
        item guitar = new item("Guitar", false, -1);
        guitar.setImage(R.drawable.guitar);
        list.add(guitar);
        item travel = new item("Travel", true, -1);
        list.add(travel);
        item taxi = new item("taxi", false, -1);
        taxi.setImage(R.drawable.taxi);
        list.add(taxi);
        item auto = new item("auto", false, -1);
        auto.setImage(R.drawable.auto);
        list.add(auto);

    }

    private void setlistData() {
        String sectionTitles[] = {"Food", "Outdoor", "RockBand", "Find company for", "Travel", "Dating", "Chating"};
        int items[] = {4, 5, 3, 3, 2, 1, 1};
        int k = 1;
        for (int i = 0; i < 7; i++) {
            item section = new item(sectionTitles[i], true, -1);
            list.add(section);
            for (int j = 1; j <= items[i]; j++) {
                item itemObj = new item(constants.getInterstTitle(k), false, k);
                itemObj.setImage(constants.getInterestImage(k));
                list.add(itemObj);
                k++;
            }
        }
    }


    private class FetchOtherUsers extends AsyncTask<Void, Void, String> {

        private double latitude, longitude;
        ProgressDialog progressDialog;
        Context context;
        GPSTracker gps;
        String interest;

        public FetchOtherUsers(Context context, String interest) {
            this.context = context;
            this.interest = interest;
            progressDialog = new ProgressDialog(context);
            gps = new GPSTracker(context);

            if (gps.canGetLocation()) {
                gpsOn();
            } else {
                gpsOff();
            }

        }

        private void gpsOn() {
            this.latitude = gps.getLatitude();
            this.longitude = gps.getLongitude();
            constants.MY_LATITUDE = gps.getLatitude();
            constants.MY_LONGITUDE = gps.getLongitude();
            SharedPreferences sharedPreferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(constants.Latitude, String.valueOf(latitude));
            editor.putString(constants.Longitude, String.valueOf(longitude));
            editor.putString(constants.INTEREST, interest);
            editor.apply();
        }

        private void gpsOff() {
            gps.showSettingsAlert();

        }

        protected void onPreExecute() {
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Loading");
            progressDialog.show();
            progressDialog.setCancelable(false);
            // progressBar.setVisibility(View.VISIBLE);
            // responseView.setText("");
        }

        protected String doInBackground(Void... urls) {

            String response = "";

            try {
                String url_string = "http://zeeley.com/android_position?latitude=" + String.valueOf(latitude) + "&longitude=" +
                        String.valueOf(longitude) + "&interest=" + interest;

                try {
                    URL url = new URL(url_string);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setInstanceFollowRedirects(true);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        response = stringBuilder.toString();
                        constants.userInterst = interest;
                        JSONArray jData = new JSONArray(response);
                        if (!constants.USERS_LIST.isEmpty()) {
                            constants.USERS_LIST.clear();
                        }
                        for (int i = 0; i < jData.length(); i++) {
                            JSONObject jsonObject = jData.getJSONObject(i);
                            JSONObject obj = jsonObject.getJSONObject("fields");
                            String id = jsonObject.getString("pk");
                            URL u = new URL("http://zeeley.com/media/file_" + id + ".thumbnail");
                            HttpURLConnection img = (HttpURLConnection) u.openConnection();
                            img.setRequestMethod("GET");
                            img.setInstanceFollowRedirects(true);
                            Bitmap b = BitmapFactory.decodeStream(img.getInputStream());


                            //TODO: remov the image from constructor
                            onlineUser user = new onlineUser(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")), obj.getString("first_name") + " " +
                                    obj.getString("last_name"), obj.getString("current_intrest"),
                                    new settingsObj(obj.getString("priv_string")), Long.parseLong(id), b);

                            Log.d(constants.zeeley, user.getFirst_Name());
                            constants.USERS_LIST.add(user);
                        }
                        Log.d(constants.zeeley, " size is " + constants.USERS_LIST.size());

                    } else {
                        stringBuilder.append("Error code " + responseCode);
                        response = stringBuilder.toString();
                    }
                    bufferedReader.close();

                } catch (MalformedURLException e1) {
                    Log.e("ERROR", e1.getMessage(), e1);
                    e1.printStackTrace();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (ProtocolException e1) {
                    e1.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();

                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return "catch";
            }
            return response;
        }

        protected void onPostExecute(String response) {

            try {
                JSONArray array = new JSONArray(response);
                if (array.length() == 0) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "No people found!", Toast.LENGTH_SHORT).show();
                    //Log.i("INFO", response);
                }
                progressDialog.dismiss();
                if (!constants.USERS_LIST.isEmpty()) {
                    new database(Interst_List.this).insertOnlineUsers(constants.USERS_LIST);

                }
               /* if (from.equals(constants.From_changeInterest)) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString(constants.INTEREST, interest);
                    bundle.putDouble(constants.latitude, latitude);
                    bundle.putDouble(constants.longitude, longitude);
                    intent.putExtra(constants.BUNDLE, bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {*/
                switch (interest) {
                    case "Restaurant":
                        Intent i = new Intent(context, form_restaurant.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(constants.SOURCE, constants.fromInterestList);
                        bundle.putString(constants.INTEREST, interest);
                        bundle.putDouble(constants.latitude, latitude);
                        bundle.putDouble(constants.longitude, longitude);
                        i.putExtra(constants.BUNDLE, bundle);
                        context.startActivity(i);
                        finish();
                        break;
                    case "Film":
                        Intent f = new Intent(context, form_movie.class);
                        Bundle bf = new Bundle();
                        bf.putString(constants.SOURCE, constants.fromInterestList);
                        bf.putString(constants.INTEREST, interest);
                        bf.putDouble(constants.latitude, latitude);
                        bf.putDouble(constants.longitude, longitude);
                        f.putExtra(constants.BUNDLE, bf);
                        context.startActivity(f);
                        finish();
                        break;
                    case "Taxi":
                        Intent t = new Intent(context, form_cab.class);
                        Bundle bt = new Bundle();
                        bt.putString(constants.SOURCE, constants.fromInterestList);
                        bt.putString(constants.INTEREST, interest);
                        bt.putDouble(constants.latitude, latitude);
                        bt.putDouble(constants.longitude, longitude);
                        t.putExtra(constants.BUNDLE, bt);
                        context.startActivity(t);
                        finish();
                        break;
                    case "Game":
                        Intent g = new Intent(context, form_game.class);
                        Bundle bg = new Bundle();
                        bg.putString(constants.SOURCE, constants.fromInterestList);
                        bg.putString(constants.INTEREST, interest);
                        bg.putDouble(constants.latitude, latitude);
                        bg.putDouble(constants.longitude, longitude);
                        g.putExtra(constants.BUNDLE, bg);
                        context.startActivity(g);
                        finish();
                        break;
                    case "Travel":
                        Intent tr = new Intent(context, form_travel.class);
                        Bundle btr = new Bundle();
                        btr.putString(constants.SOURCE, constants.fromInterestList);
                        btr.putString(constants.INTEREST, interest);
                        btr.putDouble(constants.latitude, latitude);
                        btr.putDouble(constants.longitude, longitude);
                        tr.putExtra(constants.BUNDLE, btr);
                        context.startActivity(tr);
                        finish();
                        break;
                    case "Quicky":
                        Intent q = new Intent(context, form_quicky.class);
                        Bundle bq = new Bundle();
                        bq.putString(constants.SOURCE, constants.fromInterestList);
                        bq.putString(constants.INTEREST, interest);
                        bq.putDouble(constants.latitude, latitude);
                        bq.putDouble(constants.longitude, longitude);
                        q.putExtra(constants.BUNDLE, bq);
                        context.startActivity(q);
                        finish();
                        break;
                    default:
                        Intent intent = new Intent(context, MainActivity.class);
                        Bundle bd = new Bundle();
                        bd.putString(constants.SOURCE, constants.fromInterestList);
                        bd.putString(constants.INTEREST, interest);
                        bd.putDouble(constants.latitude, latitude);
                        bd.putDouble(constants.longitude, longitude);
                        intent.putExtra(constants.BUNDLE, bd);
                        context.startActivity(intent);
                        finish();
                }
               /* Intent intent = new Intent(context, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(constants.SOURCE, constants.fromInterestList);
                bundle.putString(constants.INTEREST, interest);
                bundle.putDouble(constants.latitude, latitude);
                bundle.putDouble(constants.longitude, longitude);
                intent.putExtra(constants.BUNDLE, bundle);
                context.startActivity(intent);*/


            } catch (Exception e) {
                progressDialog.dismiss();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


    }


    public void viewNearByPeople(String interest, String lat, String lon, final Context context) {
        Log.d(constants.zeeley," exec view nearby ppl ");
        new AsyncTask<String, Void, Void>() {
            boolean success = false;
            ProgressDialog progressDialog = new ProgressDialog(context);

            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setIndeterminate(true);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Loading");
                progressDialog.show();
                progressDialog.setCancelable(false);
            }

            public Void doInBackground(String... params) {
                try {
                    URL url = new URL("http://www.zeeley.com/android_view_interest/"
                            + "?interest=" + params[0]
                            + "&latitude=" + params[1]
                            + "&longitude=" + params[2]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setInstanceFollowRedirects(true);
                    BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        for (String s; (s = r.readLine()) != null; ) {
                            sb.append(s);
                        }
                        JSONArray array = new JSONArray(sb.toString());
                        constants.USERS_LIST.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            JSONObject obj = jsonObject.getJSONObject("fields");
                            String id = jsonObject.getString("pk");
                            URL u = new URL("http://zeeley.com/media/file_" + id + ".thumbnail");
                            HttpURLConnection img = (HttpURLConnection) u.openConnection();
                            img.setRequestMethod("GET");
                            img.setInstanceFollowRedirects(true);
                            Bitmap b = BitmapFactory.decodeStream(img.getInputStream());


                            //TODO: remov the image from constructor
                            onlineUser user = new onlineUser(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")), obj.getString("first_name") + " " +
                                    obj.getString("last_name"), obj.getString("current_intrest"),
                                    new settingsObj(obj.getString("priv_string")), Long.parseLong(id), b);

                            Log.d(constants.zeeley, user.getFirst_Name());
                            constants.USERS_LIST.add(user);
                        }
                        success = true;
                        Log.d(constants.zeeley, "sucs in dbg");
                    }
                } catch (Exception e) {
                    Log.d(constants.zeeley, "excep while geting shw interst users " + e.getMessage());
                    Log.d(constants.zeeley, e.getMessage());
                    success = false;
                }

                return null;
            }

            public void onPostExecute(Void aVoid) {
                if (success) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }.execute(interest, lat, lon);
    }

    public void changeInterest(String interest, String lat, String lon) {
        new AsyncTask<String, Void, Void>() {
            public Void doInBackground(String... params) {
                try {
                    URL url = new URL("http://www.zeeley.com/android_position/"
                            + "?interest=" + params[0]
                            + "&latitude=" + params[1]
                            + "&longitude=" + params[2]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setInstanceFollowRedirects(true);

                    BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    for (String s; (s = r.readLine()) != null; ) {
                        sb.append(s);
                    }

                    JSONArray array = new JSONArray(sb.toString());


                } catch (Exception e) {
                    Log.d("Zeeley", e.getMessage());
                }
                return null;
            }

            public void onPostExecute(Void aVoid) {

            }
        }.execute(interest, lat, lon);
    }
}


