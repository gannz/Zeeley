package com.zeeley;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    //ArrayList<dataobject> list;
    Gson gson;
    database database = new database(this);


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //String message = remoteMessage.getNotification().getBody();

        String activityToLaunch = remoteMessage.getNotification().getTitle();
        Log.d(constants.zeeley, "message aaya hai: " + remoteMessage.getNotification().getBody());

        Map<String, String> map = remoteMessage.getData();
        if (activityToLaunch.equalsIgnoreCase("Zeeley Message")) {
            String pk = map.get("team_id");
            String isPhoto = map.get("is_photo");
            String m = remoteMessage.getNotification().getBody();
            m = m.substring(m.indexOf(':') + 2, m.length());
            m = m.trim();
            Date dat = new Date(remoteMessage.getSentTime());
            String time = CommonMethods.format.format(dat);
            String date = CommonMethods.dateFormat.format(dat);
            ChatMessage chatMessage = new ChatMessage(m, false);
            chatMessage.setTime(time);
            chatMessage.setDate(date);
            chatMessage.setIsImage(isPhoto.equalsIgnoreCase("true") ? true : false);
            //// TODO: 01-09-2016 check database if user exist else get data from server and sv in db
            if (constants.Current_Id == Integer.parseInt(pk)) {
                Intent intent = new Intent(constants.Message_Action);
                intent.putExtra(constants.New_Message, Parcels.wrap(chatMessage));
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                chats_updater.updatelist(this, null, Integer.parseInt(pk), m, false, false);
            } else {
                constants.Notification_Time = remoteMessage.getSentTime();
                chats_updater.updatelist(this, null, Integer.parseInt(pk), m, false, false);
            }
            database database = new database(this);
            chatMessage = new ChatMessage(m, false);
            chatMessage.setDate(chatactivity.createDateString(dat));
            database.addChatUser(chatMessage, pk);
        } else if (activityToLaunch.equalsIgnoreCase("Zeeley Invitation")) {

            String teamID = map.get("team_id");
            String senderID = map.get("sender_id");
            String teamName = map.get("team_name");
            Log.d(constants.zeeley, "team id is " + teamID);
            Log.d(constants.zeeley, " senderId is " + senderID);
            Log.d(constants.zeeley, "teamName is " + teamName);

            String currentInterest = remoteMessage.getNotification().getBody();
            currentInterest = currentInterest.substring(currentInterest.lastIndexOf(' ') + 1, currentInterest.length());
            Log.d(constants.zeeley, "curnt interest is " + currentInterest);
            Intent intent = new Intent(constants.acceptAction);
            Bundle bundle = new Bundle();
            bundle.putString(constants.teamId, teamID);
            bundle.putString(constants.senderId, senderID);
            bundle.putString(constants.teamName, teamName);
            bundle.putString(constants.invitaionInterest, currentInterest);
            bundle.putBoolean(constants.isFormInvitaion, false);
            bundle.putLong(constants.notificationTime, remoteMessage.getSentTime());
            intent.putExtra(constants.BUNDLE, bundle);
            sendBroadcast(intent);
            Log.d(constants.zeeley, currentInterest);

        } else if (activityToLaunch.equalsIgnoreCase("Zeeley Team Message")) {
            Log.d(constants.zeeley, " team msg receivd");
            SharedPreferences preferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
            int pkofUser = Integer.parseInt(preferences.getString(my_login1.PRIMARY_KEY, "-1"));
            String teamID = map.get("team_id");
            String id = map.get("sender_id");
            String isImage = map.get("is_photo");
            String name = remoteMessage.getNotification().getBody();
            name = name.substring(0, name.indexOf(':'));
            String message = remoteMessage.getNotification().getBody();
            message = message.substring(message.indexOf(':') + 1, message.length());
            Date date = new Date(remoteMessage.getSentTime());
            GroupChatMessage gcm = new GroupChatMessage(teamID, message, CommonMethods.format.format(date), CommonMethods.dateFormat.format(date), name, Integer.parseInt(id) == pkofUser);
            gcm.setIsImage(isImage.equalsIgnoreCase("true"));
            if (constants.Current_Id == Integer.parseInt(teamID)) {
                Intent intent = new Intent(constants.Message_Action);
                intent.putExtra(constants.New_Message, Parcels.wrap(gcm));
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                //chats_updater.updatelist(this, null, Integer.parseInt(teamID), message, false, false);
                chats_updater.updateGrpList(this,id,name,message,CommonMethods.dateFormat.format(date));
            } else {
               // chats_updater.updatelist(this, null, Integer.parseInt(teamID), message, false, false);
                chats_updater.updateGrpList(this,id,name,message,CommonMethods.dateFormat.format(date));
                msgNotifications.sendNotification(teamID, message, remoteMessage.getSentTime(), this);
            }
            database.addGroupMessage(gcm, teamID);
            //TODO: do as in the case of zeeley message
        } else if (activityToLaunch.equalsIgnoreCase("Zeeley Request")) {
            // executed only on admin
            String teamID = map.get("team_id");
            String senderID = map.get("new_memb");
            String teamName = map.get("team_name");
            String url = map.get("url");
            formInfo fi = new formInfo(url, teamID, senderID);

//  run accept task to get profile of sender and show notification
            profileObj prof = new database(this).getProfile(senderID);
            // // TODO: 03-10-2016 required teamname..ask mohan to send teamname
            if (prof != null) {
                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.default_profpic);
                new database(this).addintoNotific(prof, teamID, teamName, b, true);
                msgNotifications.sendFormNotif(this, remoteMessage.getSentTime(), teamName, prof.getName(), b, fi);
            } else {
                downldProfforForm(teamID, senderID, teamName, remoteMessage.getSentTime(), true, url);
            }

            //// TODO: 02-10-2016 save tripid string in shared preferences with value constants.joined

        } else if (activityToLaunch.equalsIgnoreCase("Zeeley Accept")) {
            // executed on sender device
            //todo load groupchat
            String id = map.get("team_id");
            String teamName = map.get("team_name");
            loadGroupChat(id, teamName);

        }  //todo there shud be a member added notification for interest invitation.
        else if (activityToLaunch.equalsIgnoreCase("Member added")) {
            // executed on all devices except sender
            //it cannot be executed because Mohan thinks this feature is useless
            String name = map.get("name");
            String memberId = map.get("id");
            String teamId = map.get("teamId");
            String teamName = map.get("teamName");
            ////  if admin then add this to groupchatscreen with data already present in profile or notifiactions table
            // if on other participants then download profile from server and display in grpchat screen
            profileObj prof = new database(this).getProfile(memberId);
            if (prof != null) {
                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.default_profpic);
                groupParticipant gp = new groupParticipant(teamId, prof.getName(), prof.getPk(), b, prof.getSettings().getSettingsString());
                new database(this).addGrpParticipant(gp);
                msgNotifications.sendMemberAddedNotific(prof.getName(), teamName, teamId, this, remoteMessage.getSentTime());
            } else {
                // download profile from server
                Intent dp = new Intent(constants.downloadProfileAction);
                Bundle b = new Bundle(2);
                b.putString(constants.teamId, teamId);
                b.putString(constants.Id, memberId);
                dp.putExtra(constants.BUNDLE, b);
                LocalBroadcastManager.getInstance(this).sendBroadcast(dp);
            }

        } else {
            Toast.makeText(this, remoteMessage.getNotification().getTitle(), Toast.LENGTH_SHORT).show();
        }
    }


    private void loadGroupChat(String id, String teamName) {
        new LoadGroupChat().execute(id, teamName);
    }

    private class LoadGroupChat extends AsyncTask<String, Void, Void> {

        String teamid, teamName;
        ArrayList<GroupChatMessage> messages;
        String pk;
        SharedPreferences preferences;
        ArrayList<groupParticipant> groupParticipants = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            preferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, MODE_PRIVATE);
            this.pk = preferences.getString("pk", constants.DEFAULT);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                teamid = params[0];
                teamName = params[1];
                //// TODO: 03-10-2016 change url
                URL url = new URL("http://zeeley.com/team_info/?team_id=" + teamid + "&trip_id=");
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
                HashMap<String, String> names = new HashMap<>();
                for (int i = 0; i < array.length(); i++) {
                    parameters = array.getJSONObject(i);
                    fields = parameters.getJSONObject("fields");
                    String name = fields.getString("first_name");
                    Log.d(constants.zeeley, name);
                    String settings = fields.getString("priv_string");
                    String p_k = String.valueOf(parameters.getInt("pk"));
                    Log.d("zeeley", p_k + " : " + name);
                    names.put(p_k, name);
                    URL u = new URL("http://zeeley.com/media/file_" + p_k + ".thumbnail");
                    HttpURLConnection con = (HttpURLConnection) u.openConnection();
                    con.setRequestMethod("GET");
                    con.setInstanceFollowRedirects(true);
                    Bitmap b = BitmapFactory.decodeStream(con.getInputStream());
                    groupParticipant gp = new groupParticipant(teamid, name, p_k, b, settings);
                    groupParticipants.add(gp);
                }

                url = new URL("http://zeeley.com/android_teamcht/?sluge=" + teamid);
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
                    Log.d("zeeley", names.get(id));
                    Date date = chatactivity.parseDate(post_time);
                    GroupChatMessage gcm = new GroupChatMessage(team_id, message,
                            CommonMethods.format.format(date), CommonMethods.dateFormat.format(date), names.get(id), Integer.parseInt(id) == Integer.parseInt(pk));
                    gcm.setIsImage(fields.getBoolean("teamc_photo"));
                    messages.add(gcm);
                    if (i == array.length() - 1) {
                        dataobject obj = new dataobject(teamName, CommonMethods.dateFormat.format(chatactivity.parseDate(post_time)),
                                message, team_id, null, true);

                        SharedPreferences.Editor editor = preferences.edit();
                        String list = preferences.getString(constants.CHATS_LIST, constants.DEFAULT);
                        ArrayList<dataobject> dataobjects = new ArrayList<>();
                        Gson gson = new Gson();
                        if (list.equals(constants.DEFAULT)) {
                            dataobjects.add(obj);
                            String h = gson.toJson(dataobjects);
                            editor.putString(constants.CHATS_LIST, h);
                            editor.apply();
                        } else {
                            dataobject[] ar = gson.fromJson(list, dataobject[].class);
                            for (dataobject u : ar) {
                                dataobjects.add(u);
                            }
                            dataobjects.add(0, obj);
                            String h = gson.toJson(dataobjects);
                            editor.putString(constants.CHATS_LIST, h);
                            editor.apply();
                        }


                        //TODO: add this to chat screen
                    }
                }
            } catch (Exception e) {
                Log.d("zeeley", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            database.createGroupChatTable(teamid);
            Log.d("zeeley", "Group chat table created");
            // Log.d("zeeley", map.get(teamid).toString());
            database.addGroupMessage(messages, teamid);
            Log.d("zeeley", "group message added");
            if (!groupParticipants.isEmpty()) {
                new database(MyFirebaseMessagingService.this).addgroupParticipants(groupParticipants);
            }
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(teamid, constants.joined);
        }
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(constants.zeeley, "executing ontaskremoved");
        if (!chats_updater.isSaved) {
            SharedPreferences sharedPreferences;
            SharedPreferences.Editor editor;
            sharedPreferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String jsonstring = gson.toJson(chats_updater.list);
            if (sharedPreferences.contains(constants.CHATS_LIST)) {
                editor.remove(constants.CHATS_LIST);
            }
            editor.putString(constants.CHATS_LIST, jsonstring);
            editor.apply();
            chats_updater.isSaved = true;
        }

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        if (!chats_updater.isSaved) {
            SharedPreferences sharedPreferences;
            SharedPreferences.Editor editor;
            sharedPreferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String jsonstring = gson.toJson(chats_updater.list);
            if (sharedPreferences.contains(constants.CHATS_LIST)) {
                editor.remove(constants.CHATS_LIST);
            }
            editor.putString(constants.CHATS_LIST, jsonstring);
            editor.apply();
            chats_updater.isSaved = true;
        }
        super.onDestroy();
    }

    public void downldProfforForm(String teamID, String senderID, String teamName, long time, boolean isformInvitation, String url) {
        new acceptTask(teamID, senderID, teamName, time, isformInvitation, url).execute();
    }

    class acceptTask extends AsyncTask<Void, Void, profileObj> {
        String teamID, senderID, teamName, url;
        long time;
        boolean isformInvitation;
        Bitmap b;

        public acceptTask(String teamID, String senderID, String teamName, long time, boolean isformInvitation, String url) {
            this.teamID = teamID;
            this.senderID = senderID;
            this.teamName = teamName;
            this.time = time;
            this.url = url;
            this.isformInvitation = isformInvitation;
        }

        @Override
        protected profileObj doInBackground(Void... params) {

            try {
                Log.d(constants.zeeley, "geting info of invitation sender");
                URL url = new URL("http://zeeley.com/android_info/?id=" + senderID);
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
                String current_intrest = object.getString("current_interest");
                String seting = object.getString("priv_string");
                settingsObj settings = new settingsObj(seting);
                String latitude = object.getString("latitude");
                String longitude = object.getString("longitude");
                URL u = new URL("http://zeeley.com/media/file_" + senderID + ".thumbnail");
                HttpURLConnection img = (HttpURLConnection) u.openConnection();
                img.setRequestMethod("GET");
                img.setInstanceFollowRedirects(true);
                b = BitmapFactory.decodeStream(img.getInputStream());
                profileObj prof = new profileObj(senderID, first_name, last_name, gender, current_intrest, latitude, longitude, age, settings);
                prof.setContactNo(contact);
                formInfo fi = new formInfo(this.url, teamID, senderID);
                msgNotifications.sendFormNotif(MyFirebaseMessagingService.this, time, teamName, prof.getName(), b, fi);
                return prof;


            } catch (Exception e) {
                Log.d(constants.zeeley, e.getMessage());
                Log.d(constants.zeeley, "eror geting info in updater profile tsk");
            }

            return null;
        }

        @Override
        protected void onPostExecute(profileObj prof) {
            if (prof != null && b != null) {
                database db = new database(MyFirebaseMessagingService.this);
                db.addintoNotific(prof, teamID, teamName, b, isformInvitation);
                db.insertintoProf(prof);
            } else if (prof != null && b == null) {
                database db = new database(MyFirebaseMessagingService.this);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_profpic);
                db.addintoNotific(prof, teamID, teamName, bitmap, isformInvitation);
                db.insertintoProf(prof);
            }

        }
    }

}