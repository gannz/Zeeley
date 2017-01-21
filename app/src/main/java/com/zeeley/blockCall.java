package com.zeeley;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by gannu on 13-09-2016.
 */

public class blockCall {
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    static ProgressDialog progressDialog;

    public static void block(final boolean canInvite, final boolean canViewProfile, final boolean canMessage, final boolean canAccessLocation, final Context context,
                             final dataobject user, final ArrayList<Integer> list
    ) {


        Log.d(constants.zeeley, " inside blck ");
        final String pk = user.getId();
        final String name = user.getName();
        sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Blocking");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Boolean value) {
                super.onPostExecute(value);
                if (value) {

                    if (!canInvite) {
                        list.set(0, 1);
                        user.getSettings().setCanInvite(false);
                        Log.d(constants.zeeley, " setng user cant invivte ");
                    }
                    if (!canViewProfile) {
                        list.set(1, 1);
                        user.getSettings().setCanViewProfile(false);
                        Log.d(constants.zeeley, " setng user cant view prof  ");
                    }
                    if (!canMessage) {
                        list.set(2, 1);
                        user.getSettings().setCanMessage(false);
                        Log.d(constants.zeeley, "  setng user cant msg ");
                    }
                    if (!canAccessLocation) {
                        list.set(3, 1);
                        user.getSettings().setCanAccessLocation(false);
                        Log.d(constants.zeeley, " setng user cant acs locatn  ");
                    }


                    ArrayList<String> blocks = new ArrayList<String>();
                    for (int i = 0; i < 4; i++) {
                        if (list.get(i) == 1) {
                            switch (i) {
                                case 0:
                                    blocks.add("Invitation");
                                    Log.d(constants.zeeley, "invit aded to blcks");
                                    break;
                                case 1:
                                    blocks.add("Viewing profile");
                                    Log.d(constants.zeeley, "prof aded to blcks");
                                    break;
                                case 2:
                                    blocks.add("Messaging");
                                    Log.d(constants.zeeley, " msg aded to blcks");
                                    break;
                                case 3:
                                    blocks.add("Accessing location");
                                    Log.d(constants.zeeley, "locat aded to blcks ");
                                    break;

                            }
                        }

                    }

                   /* String chatlist = sharedPreferences.getString(constants.CHATS_LIST, constants.DEFAULT);
                    Gson gson = new Gson();
                    dataobject[] objs = gson.fromJson(chatlist, dataobject[].class);
                    ArrayList<dataobject> users = new ArrayList<>(Arrays.asList(objs));
                   /* for (int i = 0; i < users.size(); i++) {
                        if (user.getId().equals(users.get(i).getId())) {
                            users.remove(i);
                            users.add(user);
                            break;
                        }
                    }
                    editor = sharedPreferences.edit();
                    editor.remove(constants.CHATS_LIST);
                    editor.putString(constants.CHATS_LIST, gson.toJson(users));
                    editor.apply();*/
                    chats_updater.updateBlocks(context,list.get(0) == 0,list.get(1) == 0,list.get(2) == 0,pk,list.get(3) == 0);
                    Log.d(constants.zeeley, "chats list of pref updatd ");
                    dbBlockdItem dbi = new dbBlockdItem(user.getName(), constants.getImage(Integer.valueOf(user.getId()), context), Integer.parseInt(user.getId()),
                            list.get(0) == 0, list.get(1) == 0, list.get(2) == 0, list.get(3) == 0);
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
                        Log.d(constants.zeeley, "no blokd user bfr so aded new blcd user");
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
                                Log.d(constants.zeeley, "found user in block list nd updtd its blocks ");
                                break;
                            }
                        }
                        if (!found) {
                            blockedItem item1 = new blockedItem(name, constants.getImage(Integer.valueOf(user.getId()), context), Integer.valueOf(pk));
                            item1.setBlockedFrom(blocks);
                            blockedItems.add(0, item1);
                            Log.d(constants.zeeley, "dint find user in blck list so aded it");
                        }
                        editor = sharedPreferences.edit();
                        editor.putString(constants.BLOCKED_CONTACTS, gson.toJson(blockedItems));
                        editor.apply();
                        Log.d(constants.zeeley, "updating blockd list in preferences ");
                    }*/
                    progressDialog.dismiss();
                    Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(constants.finishUrself);
                    LocalBroadcastManager manager=LocalBroadcastManager.getInstance(context);
                    manager.sendBroadcast(intent);
                    Log.d(constants.zeeley,"intent sent");
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                String locationString = "", profileString = "", MsgString = "", InviteString = "";
                if (canAccessLocation)
                    locationString = "block_location=&unblock_location=";
                else locationString = "block_location=" + pk + "&unblock_location=";
                if (canInvite)
                    InviteString = "&block_invite=&unblock_invite=";
                else InviteString = "&block_invite=" + pk + "&unblock_invite=";
                if (canMessage)
                    MsgString = "&block_msg=&unblock_msg=";
                else MsgString = "&block_msg=" + pk + "&unblock_msg=";
                if (canViewProfile)
                    profileString = "&block_prof=&unblock_prof=";
                else profileString = "&block_prof=" + pk + "&unblock_prof=";


                String urls = "http://zeeley.com/android_privacy/?" +
                        locationString +
                        profileString +
                        MsgString +
                        InviteString+ "&hide_map_all=&hide_dist_all=";
                Log.d(constants.zeeley, " url is " + urls);
                URL url = null;
                try {
                    url = new URL(urls);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setInstanceFollowRedirects(true);
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Log.d(constants.zeeley, "sucs");
                        return true;
                    }
                } catch (Exception e) {
                    Log.d(constants.zeeley, "excep in block dbg");
                    Log.d("zeeley", e.getMessage());
                    return false;
                }

                return false;
            }
        }.execute();

    }

    public static void blockOnlineUser(final boolean canInvite, final boolean canViewProfile, final boolean canMessage, final boolean canAccessLocation, final Context context,
                                       final onlineUser user, final ArrayList<Integer> list
    ) {

        final String pk = String.valueOf(user.getId());
        final String name = user.getName();
        sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Blocking");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Boolean value) {
                super.onPostExecute(value);
                if (value) {

                    if (!canInvite) {
                        list.set(0, 1);
                        user.getSettings().setCanInvite(false);
                        Log.d(constants.zeeley, " setng user cant invivte ");
                    }
                    if (!canViewProfile) {
                        list.set(1, 1);
                        user.getSettings().setCanViewProfile(false);
                        Log.d(constants.zeeley, " setng user cant view prof  ");
                    }
                    if (!canMessage) {
                        list.set(2, 1);
                        user.getSettings().setCanMessage(false);
                        Log.d(constants.zeeley, "  setng user cant msg ");
                    }
                    if (!canAccessLocation) {
                        list.set(3, 1);
                        user.getSettings().setCanAccessLocation(false);
                        Log.d(constants.zeeley, " setng user cant acs locatn  ");
                    }


                    ArrayList<String> blocks = new ArrayList<String>();
                    for (int i = 0; i < 4; i++) {
                        if (list.get(i) == 1) {
                            switch (i) {
                                case 0:
                                    blocks.add("Invitation");
                                    Log.d(constants.zeeley, "invit aded to blcks");
                                    break;
                                case 1:
                                    blocks.add("Viewing profile");
                                    Log.d(constants.zeeley, "prof aded to blcks");
                                    break;
                                case 2:
                                    blocks.add("Messaging");
                                    Log.d(constants.zeeley, " msg aded to blcks");
                                    break;
                                case 3:
                                    blocks.add("Accessing location");
                                    Log.d(constants.zeeley, "locat aded to blcks ");
                                    break;

                            }
                        }

                    }
                    dbBlockdItem dbi = new dbBlockdItem(user.getName(), constants.getImage((int) user.getId(), context), (int) user.getId(),
                            list.get(0) == 0, list.get(1) == 0, list.get(2) == 0, list.get(3) == 0);
                    database db = new database(context);
                    db.insertBlockMembr(dbi);
                   /* Gson gson = new Gson();
                    String list = sharedPreferences.getString(constants.BLOCKED_CONTACTS, constants.DEFAULT);
                    if (list.equals(constants.DEFAULT)) {
                        blockedItem item = new blockedItem(user.getName(), user.getImage(), Integer.parseInt(String.valueOf(user.getId())));
                        item.setBlockedFrom(blocks);
                        ArrayList<blockedItem> items = new ArrayList<blockedItem>(1);
                        items.add(item);
                        editor = sharedPreferences.edit();
                        editor.putString(constants.BLOCKED_CONTACTS, gson.toJson(items));
                        editor.apply();
                        Log.d(constants.zeeley, "no blokd user bfr so aded new blcd user");
                    } else {
                        ArrayList<blockedItem> blockedItems = new ArrayList<blockedItem>();
                        blockedItem[] items = gson.fromJson(list, blockedItem[].class);
                        blockedItems.addAll(Arrays.asList(items));
                        boolean found = false;
                        for (int i = 0; i < blockedItems.size() - 1; i++) {
                            if (user.getId() == blockedItems.get(i).getId()) {
                                found = true;
                                blockedItem blocked = blockedItems.get(i);
                                blockedItems.remove(i);
                                blocked.setBlockedFrom(blocks);
                                blockedItems.add(0, blocked);
                                Log.d(constants.zeeley, "found user in block list nd updtd its blocks ");
                                break;
                            }
                        }
                        if (!found) {
                            blockedItem item1 = new blockedItem(name, user.getImage(), Integer.valueOf(pk));
                            item1.setBlockedFrom(blocks);
                            blockedItems.add(0, item1);
                            Log.d(constants.zeeley, "dint find user in blck list so aded it");
                        }
                        editor = sharedPreferences.edit();
                        editor.putString(constants.BLOCKED_CONTACTS, gson.toJson(blockedItems));
                        editor.apply();
                        Log.d(constants.zeeley, "updating blockd list in preferences ");
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
                if (canAccessLocation)
                    locationString = "block_location=&unblock_location=";
                else locationString = "block_location=" + pk + "&unblock_location=";
                if (canInvite)
                    InviteString = "&block_invite=&unblock_invite=";
                else InviteString = "&block_invite=" + pk + "&unblock_invite=";
                if (canMessage)
                    MsgString = "&block_msg=&unblock_msg=";
                else MsgString = "&block_msg=" + pk + "&unblock_msg=";
                if (canViewProfile)
                    profileString = "&block_prof=&unblock_prof=";
                else profileString = "&block_prof=" + pk + "&unblock_prof=";


                String urls = "http://zeeley.com/android_privacy/?" +
                        locationString +
                        profileString +
                        MsgString +
                        InviteString+ "&hide_map_all=&hide_dist_all=";
                Log.d(constants.zeeley, urls);
                URL url = null;
                try {
                    url = new URL(urls);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setInstanceFollowRedirects(true);
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                        return true;
                    }
                } catch (Exception e) {
                    Log.d("zeeley", e.getMessage());
                    return false;
                }

                return false;

            }
        }.execute();
    }

}

