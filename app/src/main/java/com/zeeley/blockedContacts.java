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
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class blockedContacts extends AppCompatActivity {

    ArrayList<blockedItem> datasource;
    ListView listView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    myadapter adapter;
    boolean updateData = false;
    Gson gson;
    int blockRequestcode = 25;
    database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blocked_contacts);
        datasource = new ArrayList<>();
        db = new database(this);
        ArrayList<blockedItem> list = db.getBlockMbrs();
        if (list != null)
            datasource.addAll(list);
        sharedPreferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Blocked List");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(blockedContacts.this, forward.class);
                Bundle bundle = new Bundle();
                bundle.putString(constants.SOURCE, constants.From_blockedList);
                intent.putExtra(constants.BUNDLE, bundle);
                startActivityForResult(intent, blockRequestcode);
            }
        });
        TextView textView = (TextView) findViewById(R.id.text);
        listView = (ListView) findViewById(R.id.list);
        if (datasource.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        adapter = new myadapter(this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                blockedItem item = (blockedItem) listView.getItemAtPosition(position);
                new MaterialDialog.Builder(blockedContacts.this)
                        .title(item.getName() + " blocked from")
                        .items(item.getBlockedFrom())
                        .autoDismiss(true)
                        .show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final blockedItem item = (blockedItem) listView.getItemAtPosition(position);
                final ArrayList<String> menu = item.getBlockedFrom();
                new MaterialDialog.Builder(blockedContacts.this)
                        .title("Unblock " + item.getName() + " from")
                        .items(item.getBlockedFrom())
                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                /**
                                 * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected check box to actually be selected.
                                 * See the limited multi choice dialog example in the sample project for details.
                                 **/
                                dialog.setSelectedIndices(which);
                                return true;
                            }
                        })
                        .positiveText("ok")
                        .negativeText("cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                boolean invite = false, msg = false, prof = false, location = false;
                                Integer[] i = dialog.getSelectedIndices();

                                if (i.length > 0) {
                                    for (Integer k : i) {
                                        String block = menu.get(k);
                                        switch (block) {
                                            case "Invitation":
                                                invite = true;
                                                break;
                                            case "Viewing profile":
                                                prof = true;
                                                break;
                                            case "Messaging":
                                                msg = true;
                                                break;
                                            case "Accessing location":
                                                location = true;
                                                break;
                                        }

                                    }
                                    task t = new task(invite, msg, prof, location, item.getId());
                                    t.execute();
                                   /* updateData = true;
                                    if (i.length == item.getBlockedFrom().size()) {
                                        datasource.remove(datasource.size() - 1 - position);
                                        adapter.notifyDataSetChanged();
                                    } else if (i.length < item.getBlockedFrom().size()) {
                                        for (int j : i) {
                                            item.getBlockedFrom().remove(j);
                                        }
                                    }*/
                                }
                            }
                        })
                        .alwaysCallMultiChoiceCallback()
                        .show();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == blockRequestcode) {
            Log.d(constants.zeeley, "updating list as activity finishd");
            datasource.clear();
            ArrayList<blockedItem> list = db.getBlockMbrs();
            if (list != null)
                datasource.addAll(list);
            adapter.notifyDataSetChanged();
           /* String list = sharedPreferences.getString(constants.BLOCKED_CONTACTS, constants.DEFAULT);
            if (!list.equals(constants.DEFAULT)) {
                gson = new Gson();
                blockedItem[] items = gson.fromJson(list, blockedItem[].class);
                datasource.clear();
                datasource.addAll(Arrays.asList(items));
                adapter.notifyDataSetChanged();
            }*/


        }

    }

    /* @Override
     protected void onPause() {
         super.onPause();
         if (updateData) {
             editor = sharedPreferences.edit();
             editor.remove(constants.BLOCKED_CONTACTS);
             editor.putString(constants.BLOCKED_CONTACTS, gson.toJson(datasource));
             editor.apply();
             updateData = false;
         }
     }
 */
    class myadapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;

        @Override
        public int getCount() {
            return datasource.size();
        }

        @Override
        public Object getItem(int position) {
            return datasource.get(position);

        }

        public myadapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);


        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            if (view == null) {
                view = inflater.inflate(R.layout.blocked_row, parent, false);
                viewholder holder = new viewholder(view);
                view.setTag(holder);
            }
            viewholder holder = (viewholder) view.getTag();
            blockedItem item = (blockedItem) getItem(position);
            holder.setData(item.getName(), item.getImage());
            return view;
        }

        class viewholder {
            TextView name;
            ImageView image;

            public viewholder(View view) {
                name = (TextView) view.findViewById(R.id.chatname);
                image = (ImageView) view.findViewById(R.id.chatimage);
            }

            public void setData(String name, Bitmap image) {
                this.name.setText(name);
                this.image.setImageBitmap(image);
            }
        }
    }


    class task extends AsyncTask<Void, Void, Boolean> {
        boolean invite, msg, prof, location;
        int id;
        ProgressDialog progressDialog;

        public task(boolean invite, boolean msg, boolean prof, boolean location, int id) {
            this.invite = invite;
            this.msg = msg;
            this.prof = prof;
            this.location = location;
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(blockedContacts.this);
            progressDialog.setMessage("Unblocking");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String locationString = "", profileString = "", MsgString = "", InviteString = "";
            if (invite)
                InviteString = "&block_invite=&unblock_invite=" + id;
            else
                InviteString = "&block_invite=&unblock_invite=";
            if (msg)
                MsgString = "&block_msg=&unblock_msg=" + id;
            else
                MsgString = "&block_msg=&unblock_msg=";
            if (location)
                locationString = "block_location=&unblock_location=" + id;
            else
                locationString = "block_location=&unblock_location=";
            if (prof)
                profileString = "&block_prof=&unblock_prof=" + id;
            else
                profileString = "&block_prof=&unblock_prof=";
            String urls = "http://zeeley.com:8523/android_privacy/?" +
                    locationString +
                    profileString +
                    MsgString +
                    InviteString +
                    "&hide_map_all=&hide_dist_all=";
            Log.d(constants.zeeley, "url is" + urls);
            URL url = null;
            try {
                url = new URL(urls);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.d(constants.zeeley, "success");
                    return true;
                }
            } catch (Exception e) {
                Log.d("zeeley", e.getMessage());
                Log.d(constants.zeeley, "ecep ocrd " + e.getMessage());
                return false;
            }

            return false;

        }

        @Override
        protected void onPostExecute(Boolean aVoid) {

            if (aVoid) {
                boolean canaccesslocation = true, caninvite = true, canmessage = true, canviewprofile = true;
                SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
                Cursor cursor = sqLiteDatabase.query(database.BLOCK_TABLE, new String[]{database.block_invitation, database.block_location,
                                database.block_messaging, database.block_photo, database.Name, database.UserId, database.COLUMN_PROF_PIC},
                        database.UserId + "=" + id, null, null, null, null);
                Log.d(constants.zeeley, "cursor size is " + cursor.getCount());
                if (cursor.getCount() == 1) {
                    cursor.moveToFirst();
                    if (!invite) {
                        int inv = cursor.getInt(cursor.getColumnIndex(database.block_invitation));
                        if (inv == 1)
                            caninvite = false;
                    }
                    if (!msg) {
                        int inv = cursor.getInt(cursor.getColumnIndex(database.block_messaging));
                        if (inv == 1)
                            canmessage = false;
                    }
                    if (!location) {
                        int inv = cursor.getInt(cursor.getColumnIndex(database.block_location));
                        if (inv == 1)
                            canaccesslocation = false;
                    }
                    if (!prof) {
                        int inv = cursor.getInt(cursor.getColumnIndex(database.block_photo));
                        if (inv == 1)
                            canviewprofile = false;
                    }
                    String name = cursor.getString(cursor.getColumnIndex(database.Name));
                    byte[] bi = cursor.getBlob(cursor.getColumnIndex(database.COLUMN_PROF_PIC));
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bi, 0, bi.length);
                    dbBlockdItem dbi = new dbBlockdItem(name, bitmap, id, caninvite, canviewprofile, canmessage, canaccesslocation);
                    cursor.close();
                    sqLiteDatabase.close();
                    db.insertBlockMembr(dbi);
                    ArrayList<blockedItem> list = db.getBlockMbrs();
                    if (list != null) {
                        Log.d(constants.zeeley,"list no null updatng adaptr");
                        datasource.clear();
                        datasource.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                    chats_updater.updateBlocks(blockedContacts.this,caninvite,canviewprofile,canmessage,String.valueOf(id),canaccesslocation);
                   /* String chatlist = sharedPreferences.getString(constants.CHATS_LIST, constants.DEFAULT);
                    Gson gson = new Gson();
                    dataobject[] objs = gson.fromJson(chatlist, dataobject[].class);
                    ArrayList<dataobject> users = new ArrayList<>(Arrays.asList(objs));
                    boolean found = false;
                    for (dataobject user : users) {
                        if (user.getId().equals(String.valueOf(id))) {
                            found = true;
                            user.getSettings().setCanAccessLocation(canaccesslocation);
                            user.getSettings().setCanMessage(canmessage);
                            user.getSettings().setCanInvite(caninvite);
                            user.getSettings().setCanViewProfile(canviewprofile);
                            break;
                        }
                    }
                    if (found) {
                        editor = sharedPreferences.edit();
                        editor.remove(constants.CHATS_LIST);
                        editor.putString(constants.CHATS_LIST, gson.toJson(users));
                        editor.apply();
                    }*/
                }
            }
            progressDialog.dismiss();
        }
    }
}
