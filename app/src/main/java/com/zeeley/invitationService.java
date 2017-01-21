package com.zeeley;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

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

public class invitationService extends Service {
    public invitationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(constants.zeeley, "servivve strt");
        if (intent.getAction().equals(constants.acceptAction)) {
            Log.d(constants.zeeley,"receivd aceptacttion intent in service");
            Bundle bundle = intent.getBundleExtra(constants.BUNDLE);
            String teamName = bundle.getString(constants.teamName);
            String teamId = bundle.getString(constants.teamId);
            String senderId = bundle.getString(constants.senderId);
            String interest = bundle.getString(constants.invitaionInterest);
            Long time = bundle.getLong(constants.notificationTime);
            Boolean isForm = bundle.getBoolean(constants.isFormInvitaion);
            new acceptTask(teamId, senderId, teamName, interest, time, isForm).execute();
        }
        else if (intent.getAction().equals(constants.downloadProfileAction)){
            Bundle bundle=intent.getBundleExtra(constants.BUNDLE);
            new downProfTask(bundle.getString(constants.Id),bundle.getString(constants.teamId)).execute();
        }
        else if (intent.getAction().equals(constants.acceptFormAction)){
            formInfo fi=Parcels.unwrap(intent.getParcelableExtra(constants.INVITATION));
            executeURL("http://zeeley.com/" + fi.getUrl() + "_accept/?trip_id=" + fi.getTeamId() + "&memb_id=" + fi.getSenderId());
        }
        else if(intent.getAction().equals(constants.rejectFormAction)){
           Log.w(constants.zeeley,"form join request rejected...cald in invitatn service");
        }
        else {
            Bundle bundle = intent.getBundleExtra(constants.BUNDLE);
            String pk = bundle.getString(constants.Interest_Id);
            inviteInfo invite = Parcels.unwrap(bundle.getParcelable(constants.INVITATION));
            if (invite == null)
                Log.d(constants.zeeley, " invite is null");
            else
                new acceptInvite(invite.getTeamName()).execute(invite.getCurrentInterest(), pk, invite.getSenderID(), invite.getTeamID());
        }
        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    class acceptInvite extends AsyncTask<String, Void, Void> {
        boolean done = false;
        String teamName, teamId;

        acceptInvite(String teamName) {
            this.teamName = teamName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(constants.zeeley, "inside aceptinvite postexecute");
        }

        @Override
        protected Void doInBackground(String... params) {
            teamId = params[3];
            Log.d(constants.zeeley, "accepting invite...");
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
                Toast.makeText(invitationService.this, "Invitation accepted", Toast.LENGTH_SHORT).show();
                 new LoadGroupChat().execute(teamId, teamName);
            }
        }

    }

    public void executeURL(String url) {
        new executeUrl().execute(url);
    }

    private class LoadGroupChat extends AsyncTask<String, Void, Void> {
        SharedPreferences preferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
        String pk = preferences.getString(my_login1.PRIMARY_KEY, "-1");
        ArrayList<GroupChatMessage> messages = null;
        dataobject obj;
        String team, teamName;
        ArrayList<grpParticpnt> grpParticpnts = new ArrayList<>();
        HashMap<String, Bitmap> images = new HashMap<>();
        ArrayList<String> pks = new ArrayList<>();

        @Override
        protected Void doInBackground(String... params) {
            team = params[0];
            teamName = params[1];
            try {
                Log.d("zeeley", "grp chat bg");
                HashMap<String, String> names = new HashMap<>();

                URL url = new URL("http://zeeley.com/team_info/?team_id=" + team);
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
                //String lastName = null;
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

                    }
                    for (String id : pks) {
                        URL u = new URL("http://zeeley.com/media/file_" + id + ".thumbnail");
                        HttpURLConnection con = (HttpURLConnection) u.openConnection();
                        con.setRequestMethod("GET");
                        con.setInstanceFollowRedirects(true);
                        Bitmap b = BitmapFactory.decodeStream(con.getInputStream());
                        images.put(id, b);
                    }
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
                                names.get(id) + " : " + message, team_id, null, true);

                    }
                }


            } catch (Exception e) {
                obj = null;
                Log.w("zeeley", "exception while loading grpchat");
                Log.w("zeeley", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("zeeley", "in post execute");
            if (messages != null) {
                new database(invitationService.this).createGroupChatTable(team);
                Log.d("zeeley", "Group chat table created");
                new database(invitationService.this).addGroupMessage(messages, team);
                Log.d("zeeley", "group message added");
                new database(invitationService.this).addGrpMembers(team, grpParticpnts, images);
                Log.d("zeeley", "grp members aded to db");
            }
            if (obj != null) {
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
                   /* String os = gson.toJson(obj);
                    editor.putString(constants.Group_Obj, os);*/
                    editor.apply();
                    //chats_updater.updateGrpList(invitationService.this);
                } else {
                    dataobject[] dataobjects = gson.fromJson(list, dataobject[].class);
                    objs.add(0, obj);
                    objs.addAll(Arrays.asList(dataobjects));
                    editor = preferences.edit();
                    editor.putString(constants.CHATS_LIST, gson.toJson(objs));
                   /* String os = gson.toJson(obj);
                    editor.putString(constants.Group_Obj, os);*/
                    editor.apply();
                   // chats_updater.updateGrpList(invitationService.this);
                    Log.d(constants.zeeley, "updating chatslist");
                }
                Intent i = new Intent(invitationService.this, groupChatScreen.class);
                Bundle b = new Bundle();
                dummyGroupProfile groupProfile = new dummyGroupProfile(R.id.userimage, teamName, team);
                b.putString(constants.SOURCE, constants.FROM_REGULAR);
                b.putParcelable(constants.Dummy, Parcels.wrap(groupProfile));
                i.putExtra(constants.BUNDLE, b);
                startActivity(i);
            }
        }

    }

    class acceptTask extends AsyncTask<Void, Void, profileObj> {
        String teamID, senderID, teamName, currentInterest;
        long time;
        boolean isformInvitation;
        Bitmap b;

        public acceptTask(String teamID, String senderID, String teamName, String currentInterest, long time, boolean isformInvitation) {
            this.teamID = teamID;
            this.senderID = senderID;
            this.teamName = teamName;
            this.currentInterest = currentInterest;
            this.time = time;
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
                inviteInfo invite = new inviteInfo(teamID, senderID, teamName, currentInterest);
                msgNotifications.sendInvitationNotif(invitationService.this, time, invite);
                return prof;


            } catch (Exception e) {
                Log.d(constants.zeeley, e.getMessage());
                Log.d(constants.zeeley, "eror geting info in updater profile tsk");
            }

            return null;
        }

        @Override
        protected void onPostExecute(profileObj prof) {
            Log.d(constants.zeeley,"inside acepttask onpostexect");
            if (prof != null && b != null) {
                Log.d(constants.zeeley,"inside acepttask onpostexect if statment");
                database db = new database(invitationService.this);
                db.addintoNotific(prof, teamID, teamName, b, isformInvitation);
                db.insertintoProf(prof);
            } else if (prof != null && b == null) {
                Log.d(constants.zeeley,"inside acepttask onpostexect else if statment");
                database db = new database(invitationService.this);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_profpic);
                db.addintoNotific(prof, teamID, teamName, bitmap, isformInvitation);
                db.insertintoProf(prof);
            }

        }
    }

    class downProfTask extends AsyncTask<Void,Void,profileObj>{
        String senderID,teamId;

        public downProfTask(String senderID, String teamId) {
            this.senderID = senderID;
            this.teamId = teamId;
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
                Bitmap b = BitmapFactory.decodeStream(img.getInputStream());
                profileObj prof = new profileObj(senderID, first_name, last_name, gender, current_intrest, latitude, longitude, age, settings);
                prof.setContactNo(contact);
                return prof;


            } catch (Exception e) {
                Log.d(constants.zeeley, e.getMessage());
                Log.d(constants.zeeley, "eror geting info in updater profile tsk");
            }

            return null;
        }

        @Override
        protected void onPostExecute(profileObj prof) {
            if(prof!=null){
                //// TODO: 02-10-2016 send notification 
               // msgNotifications.sendMemberAddedNotific(prof.getName(),teamName,teamId,this,remoteMessage.getSentTime());
                new database(invitationService.this).insertintoProf(prof);
                Bitmap b=BitmapFactory.decodeResource(getResources(),R.drawable.default_profpic);
                groupParticipant gp=new groupParticipant(teamId,prof.getName(),prof.getPk(),b,prof.getSettings().getSettingsString());
                new database(invitationService.this).addGrpParticipant(gp);
            }

        }
    }

    private class executeUrl extends AsyncTask<String, Void, Void> {

        boolean isDone = false;

        @Override
        protected void onPostExecute(Void aVoid) {
            if (isDone) {
                Log.d(constants.zeeley, "URL CALLED SUCCESSFULLY");
            } else
                Log.d(constants.zeeley, "URL call failed");
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);

                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    isDone = true;
                }
            } catch (Exception e) {
                Log.d(constants.zeeley, e.getMessage());
            }

            return null;
        }
    }
}
