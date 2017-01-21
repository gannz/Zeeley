package com.zeeley;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class notifications extends AppCompatActivity {
    public static ArrayList<notificObj> list;
    private myAdapter adapter;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Notifications");
        }

        final ListView listView = (ListView) findViewById(R.id.list);
        TextView textView = (TextView) findViewById(R.id.textView);
        cursor = new database(this).getReadableDatabase().query(database.notific_table, new String[]{database.COLUMN_ID, database.COLUMN_FIRST_NAME, database.Settings, database.TeamName, database.TeamId,
                database.COLUMN_LAST_NAME, database.COLUMN_PROF_PIC, database.COLUMN_CURRENT_INTEREST, database.UserId, database.Latitude, database.isFormInvitation, database.Longitude}, null, null, null, null, database.ID + " DESC");
        if(cursor.getCount()!=0){
            listView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        }
        adapter = new myAdapter(this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(adapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pk = view.getId();
                Cursor cursor = new database(notifications.this).getReadableDatabase().query(database.notific_table, new String[]{database.Settings, database.Longitude
                        , database.Latitude, database.COLUMN_FIRST_NAME, database.TeamName, database.TeamId, database.COLUMN_LAST_NAME, database.COLUMN_PROF_PIC, database.COLUMN_GENDER, database.COLUMN_AGE, database.COLUMN_CURRENT_INTEREST}, database.UserId + "=" + pk, null, null, null, null);
                cursor.moveToFirst();
                String name = cursor.getString(cursor.getColumnIndex(database.COLUMN_FIRST_NAME)) + " " + cursor.getString(cursor.getColumnIndex(database.COLUMN_LAST_NAME));
                byte[] b = cursor.getBlob(cursor.getColumnIndex(database.COLUMN_PROF_PIC));
                Bitmap img = BitmapFactory.decodeByteArray(b, 0, b.length);
                settingsObj setings = new settingsObj(cursor.getString(cursor.getColumnIndex(database.Settings)));
                onlineUser user = new onlineUser(Double.valueOf(cursor.getString(cursor.getColumnIndex(database.Latitude))), Double.valueOf(cursor.getString(cursor.getColumnIndex(database.Latitude))), name,
                        cursor.getString(cursor.getColumnIndex(database.COLUMN_CURRENT_INTEREST)), setings, pk, img);
                cursor.close();
                Intent intent = new Intent(notifications.this, userProfile.class);
                Bundle bundle = new Bundle(2);
                bundle.putBoolean(constants.From_onlineUser, true);
                bundle.putParcelable(constants.USER, Parcels.wrap(user));
                intent.putExtra(constants.BUNDLE, bundle);
                startActivity(intent);
            }
        });
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle(listView.getCheckedItemCount() + " items selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.notific_menu, menu);
                return true;

            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                final long[] ids = listView.getCheckedItemIds();
                if (item.getItemId() == R.id.delete) {
                    new MaterialDialog.Builder(notifications.this)
                            .title("Delete " + listView.getCheckedItemCount() + " items?")
                            .positiveText("ok")
                            .negativeText("cancel")
                            .autoDismiss(true)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    SQLiteDatabase sqLiteDatabase = new database(notifications.this).getWritableDatabase();
                                    for (long i : ids) {

                                        sqLiteDatabase.delete(database.notific_table, database.ID + "=" + i, null);

                                    }
                                    Cursor cursor = new database(notifications.this).getReadableDatabase().query(database.notific_table, new String[]{database.COLUMN_ID, database.COLUMN_FIRST_NAME, database.Settings, database.TeamName, database.TeamId,
                                            database.COLUMN_LAST_NAME, database.COLUMN_PROF_PIC, database.COLUMN_CURRENT_INTEREST, database.isFormInvitation, database.UserId, database.Latitude, database.Longitude}, null, null, null, null, database.ID + " DESC");
                                    adapter.changeCursor(cursor);
                                    sqLiteDatabase.close();
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
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
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
    protected void onDestroy() {
        cursor.close();
        super.onDestroy();
    }

    class myAdapter extends CursorAdapter {
        Context context;
        LayoutInflater inflater;
        Cursor cursor;
        SQLiteDatabase sqLiteDatabase;

        public myAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            this.context = context;
            cursor = c;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = inflater.inflate(R.layout.notific_row, parent, false);
            viewholder holder = new viewholder(view);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(final View view, final Context context, final Cursor cursor) {
            viewholder holder = (viewholder) view.getTag();
            byte[] img = cursor.getBlob(cursor.getColumnIndex(database.COLUMN_PROF_PIC));
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            holder.image.setImageBitmap(bitmap);
            final String name = cursor.getString(cursor.getColumnIndex(database.COLUMN_FIRST_NAME)) + " " + cursor.getString(cursor.getColumnIndex(database.COLUMN_LAST_NAME));
            holder.name.setText(name);
            final boolean isForm = cursor.getInt(cursor.getColumnIndex(database.isFormInvitation)) == 1;
            if (isForm) {
                holder.interst.setText("wants to join");
                holder.distance.setText(cursor.getString(cursor.getColumnIndex(database.TeamName)));
            } else {
                String interest = cursor.getString(cursor.getColumnIndex(database.COLUMN_CURRENT_INTEREST));
                holder.interst.setText(interest);
                String distance = constants.getDistance(Double.valueOf(cursor.getString(cursor.getColumnIndex(database.Latitude))),
                        Double.valueOf(cursor.getString(cursor.getColumnIndex(database.Longitude))), cursor.getString(cursor.getColumnIndex(database.Settings)));
                holder.distance.setText(distance);
            }

            final int id = cursor.getInt(cursor.getColumnIndex(database.UserId));
            final String teamId = cursor.getString(cursor.getColumnIndex(database.TeamId));
            final String teamName = cursor.getString(cursor.getColumnIndex(database.TeamName));

            view.setId(id);
            holder.okImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String interest = cursor.getString(cursor.getColumnIndex(database.COLUMN_CURRENT_INTEREST));
                    String title;
                    if (isForm)
                        title = "Add " + name + " to group";
                    else title = "Accept Invitation?";
                    new MaterialDialog.Builder(context)
                            .title(title)
                            .positiveText("ok")
                            .negativeText("cancel")
                            .autoDismiss(true)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    if (isForm) {
                                        join();
                                    } else {
                                        SharedPreferences preferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
                                        String pk = preferences.getString(my_login1.PRIMARY_KEY, "-1");
                                        acceptInvite(interest, pk, String.valueOf(id), teamId, teamName);
                                    }
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
            holder.cancelImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(context)
                            .title("Reject Invitation" + "?")
                            .positiveText("reject")
                            .negativeText("cancel")
                            .autoDismiss(true)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    sqLiteDatabase = new database(context).getWritableDatabase();
                                    sqLiteDatabase.delete(database.notific_table, database.ID + "=" + cursor.getInt(cursor.getColumnIndex(database.ID)), null);
                                    Cursor cursor = new database(notifications.this).getReadableDatabase().query(database.notific_table, new String[]{database.COLUMN_ID, database.COLUMN_FIRST_NAME, database.Settings, database.TeamName, database.TeamId,
                                            database.COLUMN_LAST_NAME, database.COLUMN_PROF_PIC, database.COLUMN_CURRENT_INTEREST, database.UserId, database.isFormInvitation, database.Latitude, database.Longitude}, null, null, null, null, database.ID + " DESC");
                                    myAdapter.this.changeCursor(cursor);
                                    sqLiteDatabase.close();
                                    Toast.makeText(context, "Invitation rejected ", Toast.LENGTH_SHORT).show();
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

    }

    public class viewholder {
        public ImageView image, okImage, cancelImage;
        public TextView name, interst, distance;


        public viewholder(View itemView) {
            image = (ImageView) itemView.findViewById(R.id.chatimag);
            okImage = (ImageView) itemView.findViewById(R.id.accepted);
            cancelImage = (ImageView) itemView.findViewById(R.id.rejected);
            name = (TextView) itemView.findViewById(R.id.name);
            interst = (TextView) itemView.findViewById(R.id.interest);
            distance = (TextView) itemView.findViewById(R.id.distance);

        }
    }


    public void acceptInvite(String currentInterest, String pk_user, String pk_sender, final String teamID, final String teamName) {
        if(!constants.isNetworkAvailable(this)){
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
      
        new AsyncTask<String, Void, Void>() {
            boolean done = false;

            @Override
            protected Void doInBackground(String... params) {
                try {
                    URL url = new URL("http://zeeley.com/accept/?q=Invite." + params[0]
                            + "." + params[1] + "." + params[2] + "." + params[3]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setInstanceFollowRedirects(true);
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                        Log.d("zeeley", "Invite accepted");

                        done = true;
                    } else {
                        Log.d("zeeley", "Problem in accepting invitation");
                    }
                } catch (Exception e) {
                    Log.d("zeeley", e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (done) {
                    Toast.makeText(notifications.this, "Invitation accepted", Toast.LENGTH_SHORT).show();
                    new LoadGroupChat().execute(teamID, teamName);
                }
            }
        }.execute(currentInterest, pk_user, pk_sender, teamID, teamName);

    }

    public void join() {

    }

    private class LoadGroupChat extends AsyncTask<String, Void, Void> {
        SharedPreferences preferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
        String pk = preferences.getString(my_login1.PRIMARY_KEY, "-1");
        ArrayList<String> teamID = new ArrayList<>();
        dataobject obj;
        ArrayList<grpParticpnt> grpParticpnts = new ArrayList<>();
        ArrayList<GroupChatMessage> messages = null;
        String team, teamName;
        HashMap<String, Bitmap> images = new HashMap<>();
        ArrayList<String> pks = new ArrayList<>();

        @Override
        protected Void doInBackground(String... params) {
            team = params[0];
            teamName = params[1];

            try {

                HashMap<String, String> names = new HashMap<>();

                URL url = new URL("http://zeeley.com/team_info/?team_id=" + team+"&trip_id");
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
                String lastName = null;
                if (array.length() == 0) {
                    obj = new dataobject(teamName, " ",
                            " ", team, null, true);
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        parameters = array.getJSONObject(i);
                        fields = parameters.getJSONObject("fields");
                        String name = fields.getString("first_name");
                        String settings = fields.getString("priv_string");
                        String p_k = parameters.getInt("pk") + "";
                        pks.add(p_k);
                        names.put(p_k, name);
                        grpParticpnt gp = new grpParticpnt(name, p_k, settings);
                        grpParticpnts.add(gp);
                        if (i == array.length() - 1) lastName = name;
                    }
                    for (String id : pks) {
                        URL u = new URL("http://zeeley.com/media/file_" + id + ".thumbnail");
                        HttpURLConnection con = (HttpURLConnection) u.openConnection();
                        con.setRequestMethod("GET");
                        con.setInstanceFollowRedirects(true);
                        Bitmap b = BitmapFactory.decodeStream(con.getInputStream());
                        images.put(id, b);
                    }
                    obj = new dataobject(teamName, " ",
                            " ", team, null, true);
                }
                url = new URL("http://zeeley.com/android_teamcht/?sluge=" + team);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);
                r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                stringBuilder = new StringBuilder();
                for (String s; (s = r.readLine()) != null; )
                    stringBuilder.append(s);
                array = new JSONArray(stringBuilder.toString());
                JSONObject parameter;
                messages = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    parameter = array.getJSONObject(i);
                    fields = parameter.getJSONObject("fields");
                    String post_time = fields.getString("post_time");
                    String id = fields.getInt("teamc_id") + "";
                    String message = fields.getString("teamc_message");
                    String team_id = fields.getInt("teamc_group") + "";
                    Date date=chatactivity.parseDate(post_time);
                    GroupChatMessage gcm = new GroupChatMessage(team_id, message, CommonMethods.format.format(date)
                            ,CommonMethods.dateFormat.format(date), names.get(id), Integer.parseInt(id) == Integer.parseInt(pk));
                    messages.add(gcm);
                    if (i == array.length() - 1) {
                        obj = new dataobject(teamName, CommonMethods.dateFormat.format(chatactivity.parseDate(post_time)),
                                lastName + " : " + message, team_id, null, true);

                    }
                }


            } catch (Exception e) {
                obj = null;
                Log.d("zeeley", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (messages != null) {
                Log.d("zeeley", "msgs not null");
                new database(notifications.this).createGroupChatTable(team);
                Log.d("zeeley", "Group chat table created");
                new database(notifications.this).addGroupMessage(messages, team);
                Log.d("zeeley", "group message added");
                new database(notifications.this).addGrpMembers(team, grpParticpnts, images);
            }
            if (obj != null) {

                Toast.makeText(notifications.this, "Invitation accepted ", Toast.LENGTH_SHORT).show();
                SQLiteDatabase sqLiteDatabase = new database(notifications.this).getWritableDatabase();
                sqLiteDatabase.delete(database.notific_table, database.ID + "=" + cursor.getInt(cursor.getColumnIndex(database.ID)), null);
                Cursor cursor = new database(notifications.this).getReadableDatabase().query(database.notific_table, new String[]{database.COLUMN_ID, database.COLUMN_FIRST_NAME, database.Settings, database.TeamName, database.TeamId,
                        database.COLUMN_LAST_NAME, database.COLUMN_PROF_PIC, database.COLUMN_CURRENT_INTEREST, database.UserId, database.Latitude, database.Longitude}, null, null, null, null, database.ID + " DESC");
                adapter.changeCursor(cursor);
                sqLiteDatabase.close();
                SharedPreferences preferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor;
                String list = preferences.getString(constants.CHATS_LIST, constants.DEFAULT);
                ArrayList<dataobject> objs = new ArrayList<>();
                Gson gson = new Gson();
                if (list.equals(constants.DEFAULT)) {
                    objs.add(obj);
                    String json = gson.toJson(objs);
                    editor = preferences.edit();
                    editor.putString(constants.CHATS_LIST, json);
                    editor.apply();
                } else {
                    dataobject[] dataobjects = gson.fromJson(list, dataobject[].class);
                    objs.add(0, obj);
                    objs.addAll(Arrays.asList(dataobjects));
                    editor = preferences.edit();
                    editor.putString(constants.CHATS_LIST, gson.toJson(objs));
                    editor.apply();
                }
                Intent i = new Intent(notifications.this, groupChatScreen.class);
                Bundle b = new Bundle();
                dummyGroupProfile groupProfile = new dummyGroupProfile(R.id.userimage, teamName, team);
                b.putString(constants.SOURCE, constants.FROM_REGULAR);
                b.putParcelable(constants.Dummy, Parcels.wrap(groupProfile));
                i.putExtra(constants.BUNDLE, b);
                startActivity(i);
                finish();
            }


        }
    }
}
