package com.zeeley;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, info.locateMarker {

    FloatingActionsMenu fabMenu;
    public static chats c = null;
    FrameLayout frameLayout;
    info i;
    GoogleMap mMap;
    HashMap<String, Integer> icons;
    final int interestRequestCode = 852;
    Double lat;
    Double lon;
    String userInterest;
    SectionsPagerAdapter mSectionsPagerAdapter;
    int Grid_Request = 85;
    ViewPager mViewPager;
    private final int showInterest = 74;
    TabLayout tabLayout;
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoInterest;
    ArrayList<Marker> markerArrayList;
    private TextView infoAge;
    private TextView infoDistance;
    private Button infoButton;
    private ImageView infoImage;
    private boolean isLocate = false;
    private OnInfoWindowElemTouchListener infoButtonListener;
    MapWrapperLayout mapWrapperLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
        nMgr.cancel(constants.notificationId);
        constants.notifications.clear();
        icons = new HashMap<>();
        Bundle bundle = getIntent().getBundleExtra(constants.BUNDLE);
        String source = bundle.getString(constants.SOURCE);
        if (source != null && source.equals(constants.fromNotification)) {
            if (constants.USERS_LIST.isEmpty()) {
                database db = new database(this);
                SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.query(database.onlineUsersTable, new String[]{database.Longitude, database.Latitude, database.Name
                        , database.COLUMN_CURRENT_INTEREST, database.COLUMN_PROF_PIC, database.Settings, database.COLUMN_PRIMARY_KEY}, null, null, null, null, null);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        String name = cursor.getString(cursor.getColumnIndex(database.Name));
                        String latitude = cursor.getString(cursor.getColumnIndex(database.Latitude));
                        String longitude = cursor.getString(cursor.getColumnIndex(database.Longitude));
                        String interest = cursor.getString(cursor.getColumnIndex(database.COLUMN_CURRENT_INTEREST));
                        String id = cursor.getString(cursor.getColumnIndex(database.COLUMN_PRIMARY_KEY));
                        String setings = cursor.getString(cursor.getColumnIndex(database.Settings));
                        byte[] b = cursor.getBlob(cursor.getColumnIndex(database.COLUMN_PROF_PIC));
                        onlineUser user = new onlineUser(Double.valueOf(latitude), Double.valueOf(longitude), name, interest, new settingsObj(setings),
                                Long.valueOf(id), BitmapFactory.decodeByteArray(b, 0, b.length));
                        constants.USERS_LIST.add(user);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                sqLiteDatabase.close();
            }
            SharedPreferences sharedPreferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, MODE_PRIVATE);
            if (sharedPreferences.contains(constants.Latitude)) {
                String lati = sharedPreferences.getString(constants.latitude, null);
                if (lati != null)
                    lat = Double.parseDouble(lati);
                else
                    lat = null;
            } else lat = null;
            if (sharedPreferences.contains(constants.Longitude)) {
                String longi = sharedPreferences.getString(constants.longitude, null);
                if (longi != null)
                    lon = Double.parseDouble(longi);
                else lon = null;
            } else lon = null;
            if (sharedPreferences.contains(constants.INTEREST)) {
                userInterest = sharedPreferences.getString(constants.INTEREST, null);
            }
        } else if (source != null && source.equals(constants.fromInterestList)) {
            userInterest = bundle.getString(constants.INTEREST);
            lat = bundle.getDouble(constants.latitude);
            lon = bundle.getDouble(constants.longitude);
        }

        markerArrayList = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fabbyMenu);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
        infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.infowindow, null);
        infoTitle = (TextView) infoWindow.findViewById(R.id.name);
        infoAge = (TextView) infoWindow.findViewById(R.id.age);
        infoDistance = (TextView) infoWindow.findViewById(R.id.distance);
        infoInterest = (TextView) infoWindow.findViewById(R.id.interest);
        infoButton = (Button) infoWindow.findViewById(R.id.chat);
        infoImage = (ImageView) infoWindow.findViewById(R.id.image);
        infoButtonListener = new OnInfoWindowElemTouchListener(infoButton) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                onlineUser user = (onlineUser) marker.getTag();
                Intent intent = new Intent(MainActivity.this, chatactivity.class);
                Bundle b = new Bundle();
                b.putString(constants.SOURCE, constants.FROM_REGULAR);
                b.putParcelable(constants.USER, Parcels.wrap(user));
                intent.putExtra(constants.BUNDLE, b);
                startActivity(intent);
            }
        };
        this.infoButton.setOnTouchListener(infoButtonListener);

        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frameLayout.getBackground().setAlpha(240);
                frameLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        fabMenu.collapse();
                        return true;
                    }
                });
            }

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });
        FloatingActionButton notific = (FloatingActionButton) findViewById(R.id.notification);
        notific.setOnClickListener(this);
        FloatingActionButton interest = (FloatingActionButton) findViewById(R.id.interest);
        interest.setOnClickListener(this);
        FloatingActionButton Allinterest = (FloatingActionButton) findViewById(R.id.all_interests);
        Allinterest.setOnClickListener(this);
        // remove below 3 lines
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0) {
                    if (isLocate) {
                        for (Marker marker : markerArrayList) {
                            marker.setVisible(true);
                        }
                        isLocate = false;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setIcons();
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.map) {
            fabMenu.collapse();
            if (mMap != null) {
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                else if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE)
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            } else Toast.makeText(this, "Map is not available", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.interest) {
            fabMenu.collapse();
            Intent intent = new Intent(MainActivity.this, Interst_List.class);
            intent.putExtra(constants.SOURCE, constants.From_showInterest);
            startActivityForResult(intent, showInterest);
        } else if (id == R.id.all_interests) {
            fabMenu.collapse();
            if(!constants.isNetworkAvailable(this)){
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
            else{
                userInterest = constants.All_Interests;
                new FetchOtherUsers(this, "all", false).execute();
            }

        } else if (id == R.id.notification) {
            fabMenu.collapse();
            Intent intent = new Intent(MainActivity.this, notifications.class);
            startActivity(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void setIcons() {
        icons.put("basketball", R.drawable.basketball);
        icons.put("beer", R.drawable.beer);
        //icons.put("biryani", R.drawable.biryani);
        //icons.put("chating", R.drawable.chating);
        icons.put("cricket", R.drawable.cricket);
        //icons.put("dating", R.drawable.dating);
        icons.put("drummer", R.drawable.drummer);
        icons.put("electricguitar", R.drawable.elec_guitar);
        icons.put("football", R.drawable.football);
        icons.put("guitar", R.drawable.guitar);
        icons.put("gym", R.drawable.gym);
        icons.put("burger", R.drawable.hamburger);
        icons.put("movie", R.drawable.movie);
        icons.put("noodles", R.drawable.noodles);
        icons.put("pizza", R.drawable.pizza);
        icons.put("porridge", R.drawable.porridge);
        icons.put("taxi", R.drawable.taxi);
        icons.put("tennis", R.drawable.tennis);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent intent1 = new Intent(this, settings.class);
                startActivity(intent1);
                break;
            case R.id.newgroup:
                Intent ng = new Intent(this, newgroup.class);
                startActivity(ng);
            case R.id.travel:
                Intent tr = new Intent(this, form_travel.class);
                startActivity(tr);
                break;
            case R.id.cab:
                Intent c = new Intent(this, form_cab.class);
                startActivity(c);
                break;
            case R.id.movie:
                Intent m = new Intent(this, form_movie.class);
                startActivity(m);
                break;
            case R.id.restaurant:
                Intent r = new Intent(this, form_restaurant.class);
                startActivity(r);
                break;
            case R.id.game:
                Intent g = new Intent(this, form_game.class);
                startActivity(g);
                break;
            case R.id.quicky:
                Intent q = new Intent(this, form_quicky.class);
                startActivity(q);
                break;
            case R.id.profile:
                Intent i = new Intent(this, MyProfile.class);
                startActivity(i);
                break;
           /* case R.id.help:
                Log.d(constants.zeeley, "executing dumy");
                inviteInfo invite = new inviteInfo("60", "8", "dummy grp", "testing ");
                msgNotifications.sendInvitationNotif(this, System.currentTimeMillis(), invite);
                Log.d(constants.zeeley, " dumy executed ");
                break;*/
            case R.id.interest:
                Intent intent = new Intent(this, mygridActivity.class);
                //intent.putExtra(constants.SOURCE,constants.From_changeInterest);
                startActivityForResult(intent, Grid_Request);
                //  startActivity(intent);

        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == interestRequestCode) {
            Bundle bundle = data.getBundleExtra(constants.BUNDLE);
            lat = bundle.getDouble(constants.latitude);
            lon = bundle.getDouble(constants.longitude);
            mSectionsPagerAdapter.notifyDataSetChanged();
        }
        if (resultCode == RESULT_OK && requestCode == Grid_Request) {
            Log.d(constants.zeeley, " onactivity reslt cald");
            int id = data.getIntExtra(constants.Interest_Id, -1);
            if (!constants.isNetworkAvailable(this)) {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            } else {
                new FetchOtherUsers(this, constants.getInterstTitle(id), true).execute();
            }

        }
        if (resultCode == RESULT_OK && requestCode == showInterest) {
            Log.d(constants.zeeley, "onactivy result cald ");
            mSectionsPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void locateMarkerOnMap(String id) {
        isLocate = true;
        LatLng latLng = null;
        for (Marker marker : markerArrayList) {
            onlineUser user = (onlineUser) marker.getTag();
            if (user != null && String.valueOf(user.getId()).equals(id)) {
                latLng = new LatLng(user.getLatitude(), user.getLongitude());
                marker.setVisible(true);
                break;
            } else {
                marker.setVisible(false);
            }
        }
        if (latLng != null) {
            CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        }
        mViewPager.setCurrentItem(0);

    }

  /*  @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isLocate){
            for(Marker marker:markerArrayList){
                marker.setVisible(true);
            }
            isLocate=false;
        }
    }*/

    public class SectionsPagerAdapter extends FragmentPagerAdapter implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {
        Marker Mymarker;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    com.google.android.gms.maps.SupportMapFragment mapFragment = new com.google.android.gms.maps.SupportMapFragment();

                    mapFragment.getMapAsync(this);

                    return mapFragment;

                case 1:
                    i = info.getInstance(constants.getinfoData(userInterest), null, constants.FROM_REGULAR);
                    return i;

                case 2:
                    return chats.getInstance(constants.FROM_REGULAR, null);


                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "MAP";
                case 1:
                    return "Online Users";
                case 2:
                    return "Chats";
            }
            return null;
        }

        public Bitmap getCircularBitmap(Bitmap bitmap) {
            Bitmap output;

            if (bitmap.getWidth() > bitmap.getHeight()) {
                output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            } else {
                output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            float r = 0;

            if (bitmap.getWidth() > bitmap.getHeight()) {
                r = bitmap.getHeight() / 2;
            } else {
                r = bitmap.getWidth() / 2;
            }

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            //canvas.drawOval(,paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;


        }

        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mapWrapperLayout.init(mMap, getPixelsFromDp(MainActivity.this, 39 + 20));
            mMap.setInfoWindowAdapter(this);
            mMap.setOnInfoWindowClickListener(this);
            markerArrayList.clear();
            LatLng myLocation = null;
            if (lat != null && lon != null) {
                myLocation = new LatLng(lat, lon);
                Mymarker = mMap.addMarker(new MarkerOptions().position(myLocation).title("Your location"));
                Mymarker.setTag(null);
                markerArrayList.add(Mymarker);
            }

            if (userInterest.equals(constants.All_Interests)) {

                for (onlineUser user : constants.getUsersList()) {
                    // Bitmap bitmap=getCircularBitmap(user.getImage());

                    if (user.getSettings().isLocationAccessible()) {
                        LatLng posi = new LatLng(user.getLatitude(), user.getLongitude());
                        Marker marker = mMap.addMarker(new MarkerOptions().position(posi));
                        marker.setTag(user);
                        markerArrayList.add(marker);
                    }
                }
            } else {
                for (int i = 0; i < constants.getUsersList().size(); i++) {
                    onlineUser user = constants.getUsersList().get(i);
                    if (user.getInterest().toLowerCase().equals(userInterest.toLowerCase())) {
                        if (user.getSettings().isLocationAccessible()) {
                            LatLng posi = new LatLng(user.getLatitude(), user.getLongitude());
                            Marker marker = mMap.addMarker(new MarkerOptions().position(posi));
                            marker.setTag(user);
                            markerArrayList.add(marker);
                        }
                    }

                }
            }
            if (myLocation != null) {
                CameraUpdate center =
                        CameraUpdateFactory.newLatLng(myLocation);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

                mMap.moveCamera(center);
                mMap.animateCamera(zoom);
            }


        }

        @Override
        public View getInfoWindow(Marker marker) {
            if (marker.getTag() == null)
                return null;

            onlineUser user = (onlineUser) marker.getTag();
            infoTitle.setText(user.getName());
            Log.d(constants.zeeley, " seting name on window " + user.getName());
            infoImage.setImageResource(R.drawable.userimg);
            infoAge.setText(constants.DEFAULT);
            infoDistance.setText(constants.DEFAULT);
            infoInterest.setText(user.getInterest());
            return infoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        @Override
        public void onInfoWindowClick(Marker marker) {
            onlineUser user = (onlineUser) marker.getTag();
            Intent intent = new Intent(MainActivity.this, userProfile.class);
            Bundle bundle = new Bundle(2);
            bundle.putBoolean(constants.From_onlineUser, true);
            bundle.putParcelable(constants.USER, Parcels.wrap(user));
            intent.putExtra(constants.BUNDLE, bundle);
            startActivity(intent);
        }
    }

    private class FetchOtherUsers extends AsyncTask<Void, Void, String> {

        private double latitude, longitude;
        ProgressDialog progressDialog;
        Context context;
        GPSTracker gps;
        String interest;
        boolean fromChangeInterst;

        public FetchOtherUsers(Context context, String interest, boolean fromChangeInterest) {
            this.context = context;
            this.fromChangeInterst = fromChangeInterest;
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
        }

        private void gpsOff() {
            gps.showSettingsAlert();

        }

        protected void onPreExecute() {
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            if (fromChangeInterst)
            progressDialog.setMessage("Changing Interest to " + interest);
            else
                progressDialog.setMessage("Fetching all online users");
            progressDialog.show();
            progressDialog.setCancelable(false);
            // progressBar.setVisibility(View.VISIBLE);
            // responseView.setText("");
        }

        protected String doInBackground(Void... urls) {

            String response = "";

            try {
                Log.e("zeeley", "inside doinbackgrnd");
                String url_string = "http://zeeley.com/android_position?latitude=" + String.valueOf(latitude) + "&longitude=" +
                        String.valueOf(longitude) + "&interest=" + interest;
                URL url = new URL(url_string);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

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
                        constants.USERS_LIST.add(user);
                    }

                } else {
                    Log.e("zeeley", "couldnt conect to server");
                    stringBuilder.append("Error code " + responseCode);
                    response = stringBuilder.toString();
                }
                bufferedReader.close();

            } catch (Exception e) {

                Log.e("zeeley", e.getMessage());

                return e.getMessage();
            }
            return response;
        }

        protected void onPostExecute(String response) {
            progressDialog.dismiss();
            try {
                Log.e("zeeley", "inside post");
                JSONArray array = new JSONArray(response);
                if (array.length() == 0) {

                    Toast.makeText(context, "No people found!", Toast.LENGTH_SHORT).show();
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    //Log.i("INFO", response);
                } else {

                    mSectionsPagerAdapter.notifyDataSetChanged();
                }


            } catch (Exception e) {
                Log.e("zeeley", "parsing error in post");

                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
            }
        }


    }
}
