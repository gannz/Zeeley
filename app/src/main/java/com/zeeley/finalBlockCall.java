package com.zeeley;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by gannu on 10-11-2016.
 */
public class finalBlockCall {
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    static ProgressDialog progressDialog;

    public static void block(final ArrayList<String> finalStates, final ArrayList<Integer> finalIndices, final Context context,
                             final dataobject user
    ) {
        // final indices value 1 means that is blockd
        Log.d(constants.zeeley, "insid blck of fbc");
        final String pk = user.getId();
        final String name = user.getName();
        sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Blocking/Unblocking");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Boolean value) {
                super.onPostExecute(value);
                settingsObj so = user.getSettings();
                Log.d(constants.zeeley, "bfr in pst exec fbc..blk" + so.canInvite + " " + so.canViewProfile + " " + so.canMessage + " " + so.canAccessLocation + " ");
                if (value) {
                    ArrayList<String> blocks = new ArrayList<String>();
                    boolean caninvite = false, canviewprofile = false, canmessage = false, canaccesslocation = false;
                    for (int i = 0; i < 4; i++) {

                        if (finalIndices.get(i) == 1) {
                            switch (i) {
                                case 0:
                                    blocks.add("Invitation");
                                    Log.d(constants.zeeley, "invitatn aded to blocks");
                                    user.getSettings().setCanInvite(false);
                                    Log.d(constants.zeeley, "user cant invite");
                                    break;
                                case 1:
                                    blocks.add("Viewing profile");
                                    Log.d(constants.zeeley, "prof aded to blocks");
                                    user.getSettings().setCanViewProfile(false);
                                    Log.d(constants.zeeley, "user cant viewprofile");
                                    break;
                                case 2:
                                    blocks.add("Messaging");
                                    Log.d(constants.zeeley, "msg aded to blocks");
                                    user.getSettings().setCanMessage(false);
                                    Log.d(constants.zeeley, "user cant msg");
                                    break;
                                case 3:
                                    blocks.add("Accessing location");
                                    Log.d(constants.zeeley, "loctn aded to blocks");
                                    user.getSettings().setCanAccessLocation(false);
                                    Log.d(constants.zeeley, "user cant acesslocation");
                                    break;
                            }
                        } else {
                            switch (i) {
                                case 0:
                                    user.getSettings().setCanInvite(true);
                                    caninvite = true;
                                    Log.d(constants.zeeley, "user can invite");
                                    break;
                                case 1:
                                    user.getSettings().setCanViewProfile(true);
                                    canviewprofile = true;
                                    Log.d(constants.zeeley, "user can viewprofile");
                                    break;
                                case 2:
                                    user.getSettings().setCanMessage(true);
                                    canmessage = true;
                                    Log.d(constants.zeeley, "user can msg");
                                    break;
                                case 3:
                                    user.getSettings().setCanAccessLocation(true);
                                    canaccesslocation = true;
                                    Log.d(constants.zeeley, "user can acesslocation");
                                    break;
                            }

                        /*if (finalStates.get(i).equals("changed")) {
                            switch (i) {
                                case 0:
                                    if (finalIndices.get(i) == 1) {
                                        blocks.add("Invitation");
                                        user.getSettings().setCanInvite(false);
                                        Log.d(constants.zeeley, "changing user set to cant invite");
                                        break;
                                    } else user.getSettings().setCanInvite(true);
                                    Log.d(constants.zeeley, "changing user set to can invite");
                                    break;
                                case 1:
                                    if (finalIndices.get(i) == 1) {
                                        blocks.add("Viewing profile");
                                        user.getSettings().setCanViewProfile(false);
                                        Log.d(constants.zeeley, " changing user set to cant view prof");
                                        break;
                                    } else user.getSettings().setCanViewProfile(true);
                                    Log.d(constants.zeeley, "changing user set to can view prof");
                                    break;
                                case 2:
                                    if (finalIndices.get(i) == 1) {
                                        blocks.add("Messaging");
                                        user.getSettings().setCanMessage(false);
                                        Log.d(constants.zeeley, "changing user set to cant msg");
                                        break;
                                    } else user.getSettings().setCanMessage(true);
                                    Log.d(constants.zeeley, "changing user set to can msg");
                                    break;
                                case 3:
                                    if (finalIndices.get(i) == 1) {
                                        blocks.add("Accessing location");
                                        user.getSettings().setCanAccessLocation(false);
                                        Log.d(constants.zeeley, "changing user set to cant aces locatn");
                                        break;
                                    } else user.getSettings().setCanAccessLocation(true);
                                    Log.d(constants.zeeley, "changing user set to can aces locatn");
                                    break;

                            }
                        }*/
                        }

                    }
                    settingsObj s = user.getSettings();
                    Log.d(constants.zeeley,"aftr in pst exec fbc..blk"+ s.canInvite + " " + s.canViewProfile + " " + s.canMessage + " " + s.canAccessLocation + " ");


                   /* String chatlist = sharedPreferences.getString(constants.CHATS_LIST, constants.DEFAULT);
                    Gson gson = new Gson();
                    dataobject[] objs = gson.fromJson(chatlist, dataobject[].class);
                    ArrayList<dataobject> users = new ArrayList<>(Arrays.asList(objs));
                    for (int i = 0; i < users.size(); i++) {

                        if (user.getId().equals(users.get(i).getId())) {
                            Log.d(constants.zeeley,"bfr removing"+users.get(i).getName());
                            users.remove(i);
                            Log.d(constants.zeeley, "aftr removing" + users.get(i).getName());
                            users.add(i, user);
                            Log.d(constants.zeeley, "aftr adng" + users.get(i).getName());
                            break;
                        }
                    }
                    editor = sharedPreferences.edit();
                    editor.remove(constants.CHATS_LIST);
                    editor.putString(constants.CHATS_LIST, gson.toJson(users));
                    editor.apply();

                    Log.d(constants.zeeley, "chats list of shard pref is updatd");
                    display(context,user.getId());*/
                    chats_updater.updateBlocks(context, caninvite, canviewprofile, canmessage, user.getId(), canaccesslocation);
                    dbBlockdItem dbi = new dbBlockdItem(user.getName(), constants.getImage(Integer.valueOf(user.getId()), context), Integer.parseInt(user.getId()),
                            finalIndices.get(0) == 0, finalIndices.get(1) == 0, finalIndices.get(2) == 0, finalIndices.get(3) == 0);
                    database db = new database(context);
                    db.insertBlockMembr(dbi);
                    /* String list = sharedPreferences.getString(constants.BLOCKED_CONTACTS, constants.DEFAULT);


                    if (list.equals(constants.DEFAULT)) {
                        blockedItem item = new blockedItem(user.getName(), constants.getImage(Integer.valueOf(user.getId()), context), Integer.parseInt(user.getId()));
                        item.setBlockedFrom(blocks);
                        ArrayList<blockedItem> items = new ArrayList<blockedItem>(1);
                        items.add(item);
                        editor = sharedPreferences.edit();
                        editor.putString(constants.BLOCKED_CONTACTS, gson.toJson(items));
                        editor.apply();
                        Log.d(constants.zeeley, " no blokd contacts bfr so aded new user");
                    } else {
                        ArrayList<blockedItem> blockedItems = new ArrayList<blockedItem>();
                        blockedItem[] items = gson.fromJson(list, blockedItem[].class);
                        blockedItems.addAll(Arrays.asList(items));
                        boolean found = false;
                        for (int i = 0; i < blockedItems.size() - 1; i++) {
                            if (user.getId().equals(String.valueOf(blockedItems.get(i)))) {
                                found = true;
                                blockedItem blocked = blockedItems.get(i);
                                blockedItems.remove(i);
                                blocked.setBlockedFrom(blocks);
                                blockedItems.add(0, blocked);
                                Log.d(constants.zeeley, "found blockd user ");
                                break;
                            }
                        }
                        if (!found) {
                            blockedItem item1 = new blockedItem(name, constants.getImage(Integer.valueOf(user.getId()), context), Integer.valueOf(pk));
                            item1.setBlockedFrom(blocks);
                            blockedItems.add(0, item1);
                            Log.d(constants.zeeley, " blockd user not found ");
                        }
                        editor = sharedPreferences.edit();
                        editor.putString(constants.BLOCKED_CONTACTS, gson.toJson(blockedItems));
                        editor.apply();
                        Log.d(constants.zeeley, "blockd list updated ");
                    }*/
                    progressDialog.dismiss();
                    Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                String locationString = "", profileString = "", MsgString = "", InviteString = "";
                for (int i = 0; i < 4; i++) {
                    if (finalStates.get(i).equals("changed")) {
                        switch (i) {
                            case 3:
                                if (finalIndices.get(i) == 1) {
                                    locationString = "block_location=" + pk + "&unblock_location=";
                                    Log.d(constants.zeeley, "location blockd");
                                } else {
                                    locationString = "block_location=&unblock_location=" + pk;
                                    Log.d(constants.zeeley, "location unblockd");
                                }
                                break;
                            case 1:
                                if (finalIndices.get(i) == 1) {
                                    profileString = "&block_prof=" + pk + "&unblock_prof=";
                                    Log.d(constants.zeeley, "profile blockd");
                                } else {
                                    profileString = "&block_prof=&unblock_prof=" + pk;
                                    Log.d(constants.zeeley, "profile unblockd");
                                }
                                break;
                            case 2:
                                if (finalIndices.get(i) == 1) {
                                    MsgString = "&block_msg=" + pk + "&unblock_msg=";
                                    Log.d(constants.zeeley, "msg blockd");
                                } else {
                                    MsgString = "&block_msg=&unblock_msg=" + pk;
                                    Log.d(constants.zeeley, "msg unblockd");
                                }
                                break;
                            case 0:
                                if (finalIndices.get(i) == 1) {
                                    InviteString = "&block_invite=" + pk + "&unblock_invite=";
                                    Log.d(constants.zeeley, "invite blockd");
                                } else {
                                    InviteString = "&block_invite=&unblock_invite=" + pk;
                                    Log.d(constants.zeeley, "invite unblockd");
                                }
                                break;

                        }
                    } else if (finalStates.get(i).equals("unchanged")) {
                        switch (i) {
                            case 3:
                                locationString = "block_location=&unblock_location=";
                                Log.d(constants.zeeley, "location unchanged");

                                break;
                            case 1:
                                profileString = "&block_prof=&unblock_prof=";
                                Log.d(constants.zeeley, "profile unchanged");

                                break;
                            case 2:
                                MsgString = "&block_msg=&unblock_msg=";
                                Log.d(constants.zeeley, "msg unchanged");

                                break;
                            case 0:
                                InviteString = "&block_invite=&unblock_invite=";
                                Log.d(constants.zeeley, "invite unchanged");

                                break;

                        }
                    }
                }
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
        }.execute();
    }

    public static void display(Context context, String id) {
        // SharedPreferences sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
        String chatlist = sharedPreferences.getString(constants.CHATS_LIST, constants.DEFAULT);
        Gson gson = new Gson();
        dataobject[] objs = gson.fromJson(chatlist, dataobject[].class);
        ArrayList<dataobject> users = new ArrayList<>(Arrays.asList(objs));
        for (int i = 0; i < users.size(); i++) {
            dataobject user = users.get(i);
            if (user.getId().equals(id)) {
                settingsObj so = user.getSettings();
                Log.d(constants.zeeley, so.canInvite + " " + so.canViewProfile + " " + so.canMessage + " " + so.canAccessLocation + " ");
                break;
            }
        }
        editor = sharedPreferences.edit();
        editor.remove(constants.CHATS_LIST);
        editor.putString(constants.CHATS_LIST, gson.toJson(users));
        editor.apply();
    }

    public static void blockOnlineUser(final ArrayList<String> finalStates, final ArrayList<Integer> finalIndices, final Context context,
                                       final onlineUser user
    ) {

        final String pk = String.valueOf(user.getId());
        final String name = user.getName();
        sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Blocking/Unblocking");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }


            @Override
            protected Boolean doInBackground(Void... params) {
                String locationString = "", profileString = "", MsgString = "", InviteString = "";
                for (int i = 0; i < 4; i++) {
                    if (finalStates.get(i).equals("changed")) {
                        switch (i) {
                            case 3:
                                if (finalIndices.get(i) == 1) {
                                    locationString = "block_location=" + pk + "&unblock_location=";
                                    Log.d(constants.zeeley, "location blockd");
                                } else {
                                    locationString = "block_location=&unblock_location=" + pk;
                                    Log.d(constants.zeeley, "location unblockd");
                                }
                                break;
                            case 1:
                                if (finalIndices.get(i) == 1) {
                                    profileString = "&block_prof=" + pk + "&unblock_prof=";
                                    Log.d(constants.zeeley, "profile blockd");
                                } else {
                                    profileString = "&block_prof=&unblock_prof=" + pk;
                                    Log.d(constants.zeeley, "profile unblockd");
                                }
                                break;
                            case 2:
                                if (finalIndices.get(i) == 1) {
                                    MsgString = "&block_msg=" + pk + "&unblock_msg=";
                                    Log.d(constants.zeeley, "msg blockd");
                                } else {
                                    MsgString = "&block_msg=&unblock_msg=" + pk;
                                    Log.d(constants.zeeley, "msg unblockd");
                                }
                                break;
                            case 0:
                                if (finalIndices.get(i) == 1) {
                                    InviteString = "&block_invite=" + pk + "&unblock_invite=";
                                    Log.d(constants.zeeley, "invite blockd");
                                } else {
                                    InviteString = "&block_invite=&unblock_invite=" + pk;
                                    Log.d(constants.zeeley, "invite unblockd");
                                }
                                break;

                        }
                    } else if (finalStates.get(i).equals("unchanged")) {
                        switch (i) {
                            case 3:
                                locationString = "block_location=&unblock_location=";
                                Log.d(constants.zeeley, "location unchanged");

                                break;
                            case 1:
                                profileString = "&block_prof=&unblock_prof=";
                                Log.d(constants.zeeley, "profile unchanged");

                                break;
                            case 2:
                                MsgString = "&block_msg=&unblock_msg=";
                                Log.d(constants.zeeley, "msg unchanged");

                                break;
                            case 0:
                                InviteString = "&block_invite=&unblock_invite=";
                                Log.d(constants.zeeley, "invite unchanged");

                                break;

                        }
                    }
                }
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
            protected void onPostExecute(Boolean value) {
                super.onPostExecute(value);

                if (value) {
                    ArrayList<String> blocks = new ArrayList<String>();

                    for (int i = 0; i < 4; i++) {

                        if (finalIndices.get(i) == 1) {
                            switch (i) {
                                case 0:
                                    blocks.add("Invitation");
                                    Log.d(constants.zeeley, "invitatn aded to blocks");
                                    user.getSettings().setCanInvite(false);
                                    Log.d(constants.zeeley, "user cant invite");
                                    break;
                                case 1:
                                    blocks.add("Viewing profile");
                                    Log.d(constants.zeeley, "prof aded to blocks");
                                    user.getSettings().setCanViewProfile(false);
                                    Log.d(constants.zeeley, "user cant viewprofile");
                                    break;
                                case 2:
                                    blocks.add("Messaging");
                                    Log.d(constants.zeeley, "msg aded to blocks");
                                    user.getSettings().setCanMessage(false);
                                    Log.d(constants.zeeley, "user cant msg");
                                    break;
                                case 3:
                                    blocks.add("Accessing location");
                                    Log.d(constants.zeeley, "loctn aded to blocks");
                                    user.getSettings().setCanAccessLocation(false);
                                    Log.d(constants.zeeley, "user cant acesslocation");
                                    break;
                            }
                        } else {
                            switch (i) {
                                case 0:
                                    user.getSettings().setCanInvite(true);
                                    Log.d(constants.zeeley, "user can invite");
                                    break;
                                case 1:
                                    user.getSettings().setCanViewProfile(true);
                                    Log.d(constants.zeeley, "user can viewprofile");
                                    break;
                                case 2:
                                    user.getSettings().setCanMessage(true);
                                    Log.d(constants.zeeley, "user can msg");
                                    break;
                                case 3:
                                    user.getSettings().setCanAccessLocation(true);
                                    Log.d(constants.zeeley, "user can acesslocation");
                                    break;
                            }
                        }

                    }
                    dbBlockdItem dbi = new dbBlockdItem(user.getName(), constants.getImage((int) user.getId(), context), (int) user.getId(),
                            finalIndices.get(0) == 0, finalIndices.get(1) == 0, finalIndices.get(2) == 0, finalIndices.get(3) == 0);
                    database db = new database(context);
                    db.insertBlockMembr(dbi);

                  /*  Gson gson = new Gson();
                    String list = sharedPreferences.getString(constants.BLOCKED_CONTACTS, constants.DEFAULT);


                    if (list.equals(constants.DEFAULT)) {
                        blockedItem item = new blockedItem(user.getName(), constants.getImage((int)user.getId(), context),(int)user.getId());
                        item.setBlockedFrom(blocks);
                        ArrayList<blockedItem> items = new ArrayList<blockedItem>(1);
                        items.add(item);
                        editor = sharedPreferences.edit();
                        editor.putString(constants.BLOCKED_CONTACTS, gson.toJson(items));
                        editor.apply();
                        Log.d(constants.zeeley, " no blokd contacts bfr so aded new user");
                    } else {
                        ArrayList<blockedItem> blockedItems = new ArrayList<blockedItem>();
                        blockedItem[] items = gson.fromJson(list, blockedItem[].class);
                        blockedItems.addAll(Arrays.asList(items));
                        boolean found = false;
                        for (int i = 0; i < blockedItems.size() - 1; i++) {
                            if (user.getId()==blockedItems.get(i).getId()) {
                                found = true;
                                blockedItem blocked = blockedItems.get(i);
                                blockedItems.remove(i);
                                blocked.setBlockedFrom(blocks);
                                blockedItems.add(0, blocked);
                                Log.d(constants.zeeley, "found blockd user ");
                                break;
                            }
                        }
                        if (!found) {
                            blockedItem item1 = new blockedItem(name, constants.getImage((int)user.getId(), context), Integer.valueOf(pk));
                            item1.setBlockedFrom(blocks);
                            blockedItems.add(0, item1);
                            Log.d(constants.zeeley, " blockd user not found ");
                        }
                        editor = sharedPreferences.edit();
                        editor.putString(constants.BLOCKED_CONTACTS, gson.toJson(blockedItems));
                        editor.apply();
                        Log.d(constants.zeeley, "blockd list updated ");
                    }
                    progressDialog.dismiss();*/
                    Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }


        }.execute();
    }

}
