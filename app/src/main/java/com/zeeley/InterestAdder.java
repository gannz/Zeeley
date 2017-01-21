package com.zeeley;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class InterestAdder extends AppCompatActivity {
    final int Section = 0;
    final int normalItem = 1;
    final int title = 2;
    // boolean callSetresult=false;
    final int image = 3;

    private String from;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private ArrayList<Integer> interestList = new ArrayList<>();
    private GoogleApiClient client;
    boolean mActionModeIsActive = false;
    boolean mBackWasPressedInActionMode = false;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        mBackWasPressedInActionMode = mActionModeIsActive && event.getKeyCode() == KeyEvent.KEYCODE_BACK;
        return super.dispatchKeyEvent(event);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interest_adder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        from = getIntent().getStringExtra(constants.SOURCE);
        //callSetresult=getIntent().getBooleanExtra(constants.isChange, false);
        final ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new Adapter());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add Interests");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        // listView.setSelector(R.drawable.list);
        //setData();
        setlistData();

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
                    Toast.makeText(InterestAdder.this, interestList.size() + " items selected", Toast.LENGTH_SHORT).show();
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
                    String interest = constants.getInterstTitle((int) id);
                    Intent intent = new Intent();
                    intent.putExtra(constants.INTEREST, interest);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    //new FetchOtherUsers(InterestAdder.this,constants.getInterstTitle((int) id)).execute();
                    Intent intent = new Intent(InterestAdder.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(constants.INTEREST, constants.getInterstTitle((int) id));
                    bundle.putDouble(constants.latitude, 54.6);
                    bundle.putDouble(constants.longitude, 25.9);
                    intent.putExtra(constants.BUNDLE, bundle);
                    startActivity(intent);
                }

            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }





    class viewHolder {
        ImageView image;
        TextView textView;
      /*  public viewHolder(View view){
            image=(ImageView) view.findViewById(R.id.childimage);
            textView = (TextView) view.findViewById(R.id.childtitle);
        }*/
    }

    class Adapter extends BaseAdapter {
        LayoutInflater inflater;

        public Adapter() {
            inflater = LayoutInflater.from(InterestAdder.this);
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
                viewHolder holder = new viewHolder();
                if (getItemViewType(position) == Section) {
                    convertView = inflater.inflate(R.layout.section, parent, false);
                    holder.textView = (TextView) convertView.findViewById(R.id.list_item_section_text);
                    holder.textView.setText(list.get(position).getTitle());
                    convertView.setTag(holder);
                } else if (getItemViewType(position) == normalItem) {
                    convertView = inflater.inflate(R.layout.child, parent, false);
                    holder.textView = (TextView) convertView.findViewById(R.id.childtitle);
                    holder.textView.setText(list.get(position).getTitle());
                    holder.image = (ImageView) convertView.findViewById(R.id.childimage);
                    holder.image.setImageResource(list.get(position).getImage());
                    convertView.setTag(holder);

                }
            } else {
                viewHolder holder = (viewHolder) convertView.getTag();
                if (getItemViewType(position) == Section) {
                    holder.textView.setText(list.get(position).getTitle());
                } else {
                    holder.textView.setText(list.get(position).getTitle());
                    holder.image.setImageResource(list.get(position).getImage());
                }
            }
            return convertView;
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
                this.latitude = gps.getLatitude();
                this.longitude = gps.getLongitude();
                Toast.makeText(context, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                gps.showSettingsAlert();
            }

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
                String url_string = "http://zeeley.com/android_position/?latitude=" + latitude + "&longitude=" + longitude + "&interest=" + interest;
                URL url = new URL(url_string);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                    } else {
                        stringBuilder.append("Error code " + responseCode);
                    }
                    bufferedReader.close();
                    response = stringBuilder.toString();
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (ProtocolException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return "catch";
            }
            return response;
        }

        protected void onPostExecute(String response) {

            if (response == null) {
                progressDialog.dismiss();
                Toast.makeText(context, "Error loading data..please try again", Toast.LENGTH_SHORT).show();
                response = "THERE WAS AN ERROR";
                Log.i("INFO", response);
            }

            //       Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
            else {

                try {
                    //ToDo: check if server is sending empty array or string
                    //         Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                    //    JSONObject object = new JSONObject(response);
                    constants.userInterst = interest;
                    JSONArray jData = new JSONArray(response);
                    //      Toast.makeText(getApplicationContext(),jData.length()+"aa", Toast.LENGTH_LONG).show();
                    //  Toast.makeText(getApplicationContext(),jData.length()+"",Toast.LENGTH_LONG).show();

                    //constants.onlineUserList.clear();
                    if (!constants.USERS_LIST.isEmpty()) {
                        constants.USERS_LIST.clear();
                    }
                    for (int i = 0; i < jData.length(); i++) {
                        JSONObject jsonObject = jData.getJSONObject(i);
                        JSONObject obj = jsonObject.getJSONObject("fields");
                        int id = jsonObject.getInt("pk");
                        //onlineUser user = new onlineUser(obj.getDouble("latitude"), obj.getDouble("longitude"), obj.getString("first_name") + " " +
                               // obj.getString("last_name"), obj.getString("current_intrest"),obj.getString("priv_string"), id);//TODO: check the actual string for parsing
                        //constants.onlineUserList.add(Parcels.wrap(user));
                        //constants.USERS_LIST.add(user);
                    }
                  /*
                    ArrayList<onlineUser> list=new ArrayList<>();
                    for (Parcelable u : constants.infoData()) {
                        onlineUser user = Parcels.unwrap(u);
                        if(user.getInterest().equals(interest)){
                            list.add(user);
                        }
                    }*/

                    progressDialog.dismiss();
                    if (from.equals(constants.From_changeInterest)) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString(constants.INTEREST, interest);
                        bundle.putDouble(constants.latitude, latitude);
                        bundle.putDouble(constants.longitude, longitude);
                        intent.putExtra(constants.BUNDLE, bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Intent intent = new Intent(InterestAdder.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(constants.INTEREST, interest);
                        bundle.putDouble(constants.latitude, latitude);
                        bundle.putDouble(constants.longitude, longitude);
                        intent.putExtra(constants.BUNDLE, bundle);
                        startActivity(intent);
                    }
                    //setResult()

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
