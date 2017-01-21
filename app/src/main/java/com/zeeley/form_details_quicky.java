package com.zeeley;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class form_details_quicky extends AppCompatActivity {
    ListView listView;
    int tripId;
    SharedPreferences preferences;
    Button join;
    SharedPreferences.Editor editor;
    ProgressBar progressBar;
    String Remarks;
    int adminId;
    ParticipantsAdapter adapter;
    ArrayList<grpParticpnt> particpnts;
    BroadcastReceiver receiver;
    LocalBroadcastManager broadcastManager;
    String myPk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_details_quicky);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Quicky");
        }
        TextView place, dateandtime, remarks;
        place = (TextView) findViewById(R.id.placeName);
         join= (Button) findViewById(R.id.continuebutton);
        dateandtime = (TextView) findViewById(R.id.dateandtime);
        remarks = (TextView) findViewById(R.id.remarks);
        final Trip trip = Parcels.unwrap(getIntent().getParcelableExtra(constants.Trip));
        Remarks=trip.getRemark();
        adminId=trip.getAdminId();
        tripId = trip.getTrip_id();
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        preferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
        preferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
        myPk = preferences.getString("pk", constants.DEFAULT);
        String bt = preferences.getString(String.valueOf(tripId), constants.DEFAULT);
        if (bt.equals(constants.requestSent)) {
            join.setText("Join Request sent");
            join.setClickable(false);
        } else if (bt.equals(constants.joined)) {
            join.setText("Joined");
            join.setClickable(false);
        } else if (bt.equals(constants.DEFAULT)) {
            join.setText("Join");
            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(form_details_quicky.this)
                            .title("Join this group?")
                            .positiveText("join")
                            .negativeText("cancel")
                            .autoDismiss(true)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if(!constants.isNetworkAvailable(form_details_quicky.this)){
                                        Toast.makeText(form_details_quicky.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        InterestManager.Quiky quiky = new InterestManager.Quiky();
                                        quiky.joinQuicky(String.valueOf(tripId), form_details_quicky.this);
                                    }

                                    // // TODO: 30-09-2016 save tripid in sharedpreferences when accepted
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();

                }
            });
        }
        place.setText(trip.getHeading());
        String date = CommonMethods.dateFormat.format(trip.getStartDate()) + " & " + CommonMethods.format.format(trip.getStartDate());
        dateandtime.setText(date);
        remarks.setText(trip.getRemark());
        listView = (ListView) findViewById(R.id.list);
        new task(trip.getTrip_id()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(constants.joinAction)){
                    join.setText("Join Request Sent");
                    join.setClickable(false);
                }
                else if(intent.getAction().equals(constants.deleteAction)){
                    String id=intent.getStringExtra(constants.Id);
                    for(grpParticpnt gp:particpnts){
                        if(gp.getPk().equals(id)){
                            particpnts.remove(gp);
                            if(adapter!=null){
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                    new task(trip.getTrip_id()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                }
            }
        };
        broadcastManager =LocalBroadcastManager.getInstance(form_details_quicky.this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter=new IntentFilter(constants.submitAction);
        filter.addAction(constants.deleteAction);
        broadcastManager.registerReceiver(receiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        broadcastManager.unregisterReceiver(receiver);
    }

    class task extends AsyncTask<Void, Void, Void> {
        int Id;

        public task(int id) {
            this.Id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {


            try {
                Log.d(constants.zeeley, "running participants task");
                URL url = new URL("http://zeeley.com/team_info/?team_id=&trip_id=" + Id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);

                BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                for (String s; (s = r.readLine()) != null; ) {
                    stringBuilder.append(s);
                }
                JSONArray array = new JSONArray(stringBuilder.toString());
                JSONObject parameters, fields;
                particpnts = new ArrayList<>();
                Log.d(constants.zeeley, "numb of participants " + array.length());
                for (int i = 0; i < array.length(); i++) {

                    URL u = new URL("http://zeeley.com/media/file_" + Id + ".thumbnail");
                    HttpURLConnection con = (HttpURLConnection) u.openConnection();
                    con.setRequestMethod("GET");
                    con.setInstanceFollowRedirects(true);
                    Bitmap b = BitmapFactory.decodeStream(con.getInputStream());
                    parameters = array.getJSONObject(i);
                    fields = parameters.getJSONObject("fields");
                    String name = fields.getString("first_name");
                    Log.d(constants.zeeley, "participant " + name);
                    String setting = fields.getString("priv_string");
                    String p_k = String.valueOf(parameters.getInt("pk"));
                    grpParticpnt tp = new grpParticpnt(name, p_k, setting, b);
                    particpnts.add(tp);
                }
            } catch (Exception e) {
                Log.d(constants.zeeley, " exception in running participants task " + e.getMessage());
                e.printStackTrace();
                Log.w(constants.zeeley, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            if (particpnts != null && !particpnts.isEmpty()) {
                listView.setVisibility(View.VISIBLE);
                adapter = new ParticipantsAdapter(particpnts, form_details_quicky.this);
                listView.setAdapter(adapter);
                final String[] items = {"Message", "View profile"};
                final String[] adminItems = {"Message", "View profile", "Remove from group"};

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        boolean isAdmin = false;
                        grpParticpnt gp = (grpParticpnt) listView.getItemAtPosition(position);

                        Log.d(constants.zeeley, "userId is " + myPk);
                        if (gp.getPk().equals(myPk)) {
                            return;
                        }
                        if (gp.getPk().equals(String.valueOf(adminId))) {
                            isAdmin = true;
                        }
                        Log.w(constants.zeeley, gp.getName() + " is selected");
                        new MaterialDialog.Builder(form_details_quicky.this)
                                .items(isAdmin ? adminItems : items)
                                .title(gp.getName())
                                .itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        dialog.dismiss();
                                        grpParticpnt gp = (grpParticpnt) listView.getItemAtPosition(position);
                                        dummyProfile dummy = new dummyProfile(gp.getName(), gp.getPk(), gp.getSettings(), gp.getImage());
                                        switch (which) {
                                            case 0:
                                                Intent i = new Intent(form_details_quicky.this, chatactivity.class);
                                                Bundle b = new Bundle();
                                                b.putString(constants.SOURCE, constants.FROM_REGULAR);
                                                b.putParcelable(constants.Dummy, Parcels.wrap(dummy));
                                                i.putExtra(constants.BUNDLE, b);
                                                startActivity(i);
                                                break;
                                            case 1:
                                                Intent intent = new Intent(form_details_quicky.this, userProfile.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean(constants.callDB, false);
                                                bundle.putBoolean(constants.From_onlineUser, false);
                                                bundle.putParcelable(constants.Dummy, Parcels.wrap(dummy));
                                                intent.putExtra(constants.BUNDLE, bundle);
                                                startActivity(intent);
                                                break;
                                            case 2:
                                                InterestManager.Quiky quiky = new InterestManager.Quiky();
                                                quiky.deleteUser(gp.getPk(), String.valueOf(tripId), Remarks, form_details_quicky.this);
                                        }
                                    }
                                })
                                .show();
                    }
                });
            }
            else if (particpnts!=null&&particpnts.isEmpty()){
                TextView np= (TextView) findViewById(R.id.nop);
                np.setVisibility(View.VISIBLE);
            }
        }
    }

    class ParticipantsAdapter extends BaseAdapter {
        ArrayList<grpParticpnt> particpnts;
        Context context;
        LayoutInflater inflater;

        public ParticipantsAdapter(ArrayList<grpParticpnt> particpnts, Context context) {
            this.particpnts = particpnts;
            Log.d(constants.zeeley, "listview size is " + particpnts.size());
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return particpnts.size();
        }

        @Override
        public Object getItem(int position) {
            return particpnts.get(position);
        }

        @Override
        public long getItemId(int position) {
            grpParticpnt gp = (grpParticpnt) getItem(position);
            return Long.parseLong(gp.getPk());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.grp_participant, parent, false);
                viewholder holder = new viewholder(convertView);
                convertView.setTag(holder);
            }
            viewholder holder = (viewholder) convertView.getTag();
            grpParticpnt gp = (grpParticpnt) getItem(position);
            if (gp.getPk().equals(String.valueOf(adminId))) {
                holder.admin.setVisibility(View.VISIBLE);
                holder.admin.setText("Admin");
            }
            else holder.admin.setVisibility(View.GONE);
            String name = gp.getName();
            if (gp.getPk().equals(myPk)) {
                name = "You";
            }
            holder.setData(gp.getImage(), name);
            return convertView;
        }

        class viewholder {
            TextView name, admin;
            ImageView imageView;

            public viewholder(View itemView) {
                imageView = (ImageView) itemView.findViewById(R.id.chatimage);
                name = (TextView) itemView.findViewById(R.id.chatname);
                admin = (TextView) itemView.findViewById(R.id.admin);
            }

            public void setData(Bitmap image, String name) {
                this.imageView.setImageBitmap(image);
                this.name.setText(name);
            }
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
}
