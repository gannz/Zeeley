package com.zeeley;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class chats_updater {
    public static List<dataobject> list = null;
    public static boolean isChanged = false;
    public static boolean isSaved = true;
    static LocalBroadcastManager localBroadcastManager;

    public static synchronized dataobject updatelist(Context context, dummyProfile user, int id,
                                                     String message, boolean fromMe, boolean isGroup) {
        isChanged = true;
        isSaved = false;

        if (list == null) {
            list = new ArrayList<>();
            SharedPreferences sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
            if (sharedPreferences.contains(constants.CHATS_LIST)) {
                String jsonchats = sharedPreferences.getString(constants.CHATS_LIST, null);
                if (jsonchats != null) {
                    Gson gson = new Gson();
                    dataobject[] dataobjects = gson.fromJson(jsonchats, dataobject[].class);
                    for (dataobject i : dataobjects) {
                        list.add(i);
                    }
                }
            }
        }
        if (user != null && id == -1) {
            // cald for outgoing msgs
            Log.d(constants.zeeley, "updating list cause of outgoing msg");
            if (list.size() == 0) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                dataobject object = new dataobject(user.getName(),
                        CommonMethods.getCurrentTime(), message,
                        String.valueOf(user.getId()), new settingsObj(user.getSettings()), isGroup);
                object.setTime(CommonMethods.getCurrentTime());
                object.setDate(CommonMethods.getCurrentDate());

                // if (!fromMe) object.setColorMsg(true);
                list.add(object);
                Gson gson = new Gson();
                editor.putString(constants.CHATS_LIST, gson.toJson(list));
                editor.apply();
                //:todo insert  data into database after downloading data
                return object;
            }
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).getId().equals(String.valueOf(user.getId()))) {
                    dataobject obj = list.get(j);
                    if (constants.Current_Id != id) {

                        if (!fromMe) obj.setColorMsg(true);
                    }
                    obj.setLastmessage(message);


                    obj.setDate(CommonMethods.getCurrentDate());
                    obj.setTime(CommonMethods.getCurrentTime());
                    if (j != 0) {
                        list.remove(j);
                        list.add(0, obj);
                        return obj;
                    }
                    //list.set(0, obj);
                    return obj;
                }
            }

            dataobject object = new dataobject(user.getName(), CommonMethods.getCurrentTime(),
                    message, String.valueOf(user.getId()), new settingsObj(user.getSettings()), isGroup
            );
            object.setTime(CommonMethods.getCurrentTime());
            object.setDate(CommonMethods.getCurrentDate());
            // todo insert datainto database after dwnld data
            //if (!fromMe) object.setColorMsg(true);
            list.add(0, object);
            return object;
        } else if (user == null) {
            if (isGroup) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getId().equals(String.valueOf(id))) {

                        dataobject obj = list.get(j);
                        if (constants.Current_Id != id) {
                            constants.notifications.add(obj.getName() + " " + message);
                            msgNotifications.sendNotification(String.valueOf(id), message, constants.Notification_Time, context);
                            if (!fromMe) obj.setColorMsg(true);
                        }
                        obj.setLastmessage(message);
                       // if (!fromMe) obj.setColorMsg(true);
                        obj.setDate(CommonMethods.getCurrentDate());
                        if (j != 0) {
                            list.remove(j);
                            list.add(0, obj);
                            // constants.notifications.add(obj.getName() + " " + message);
                            Intent intent = new Intent(constants.newMsg);
                            localBroadcastManager = LocalBroadcastManager.getInstance(context);
                            localBroadcastManager.sendBroadcast(intent);
                            return obj;
                        }
                        //list.set(0, obj);
                        return obj;
                    }
                }
            } else {
                // todo download data from server
                Log.d(constants.zeeley, "updating list cause of incoming msg");
                if (list.size() == 0) {
                    new profiletask(context, fromMe, true);
                    return null;
                }
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getId().equals(String.valueOf(id))) {
                        dataobject obj = list.get(j);
                        if (constants.Current_Id != id) {
                            constants.notifications.add(obj.getName() + " " + message);
                            msgNotifications.sendNotification(String.valueOf(id), message, constants.Notification_Time, context);
                            if (!fromMe) obj.setColorMsg(true);
                        }
                        obj.setLastmessage(message);
                        //if (!fromMe) obj.setColorMsg(true);
                        obj.setDate(CommonMethods.getCurrentDate());
                        if (j != 0) {
                            list.remove(j);
                            list.add(0, obj);
                            return obj;
                        }
                        //list.set(0, obj);
                        Intent intent = new Intent(constants.newMsg);
                        localBroadcastManager = LocalBroadcastManager.getInstance(context);
                        localBroadcastManager.sendBroadcast(intent);
                        return obj;
                    }
                }
                new profiletask(context, fromMe, false).execute(String.valueOf(id), message);

                return null;
            }

        }

        return null;
    }

    private static class profiletask extends AsyncTask<String, Void, dataobject> {
        Context context;
        dataobject obj = null;
        boolean fromMe;
        boolean fromEmpty;
        String pk, message;

        public profiletask(Context context, boolean fromMe, boolean fromEmpty) {
            this.context = context;
            this.fromMe = fromMe;
            this.fromEmpty = fromEmpty;
        }

        @Override
        protected dataobject doInBackground(String... params) {
            String pk = params[0];
            String message = params[1];
            this.pk = pk;
            this.message = message;

            try {
                Log.d(constants.zeeley, "new user so geting info");
                URL url = new URL("http://zeeley.com/android_info/?id=" + pk);
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
                String current_intrest = object.getString("current_intrest");
                String seting = object.getString("priv_string");
                settingsObj settings = new settingsObj(seting);
                String latitude = object.getString("latitude");
                String longitude = object.getString("longitude");
                URL u = new URL("http://zeeley.com/media/file_" + pk + ".thumbnail");
                HttpURLConnection img = (HttpURLConnection) u.openConnection();
                img.setRequestMethod("GET");
                img.setInstanceFollowRedirects(true);
                Bitmap b = BitmapFactory.decodeStream(img.getInputStream());
                profileObj prof = new profileObj(pk, first_name, last_name, gender, current_intrest, latitude, longitude, age, settings);
                database db = new database(context);
                db.insertintoProf(prof);
                obj = new dataobject(first_name + " " + last_name, CommonMethods.getCurrentTime(), message, pk, new settingsObj(seting), false);


            } catch (Exception e) {
                Log.d(constants.zeeley, "eror geting info in updater profile tsk");
            }

            return obj;
        }

        @Override
        protected void onPostExecute(dataobject object) {
            if (fromEmpty && object != null) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                //:TODO get user information from server
                constants.notifications.add(object.getName() + " " + object.getLastmessage());
                msgNotifications.sendNotification(pk, message, constants.Notification_Time, context);
                if (!fromMe) object.setColorMsg(true);
                list.add(object);
                Gson gson = new Gson();
                editor.putString(constants.CHATS_LIST, gson.toJson(list));
                editor.apply();
                Intent intent = new Intent(constants.newMsg);
                localBroadcastManager = LocalBroadcastManager.getInstance(context);
                localBroadcastManager.sendBroadcast(intent);
            } else if (!fromEmpty && object != null) {
                constants.notifications.add(object.getName() + " " + object.getLastmessage());
                msgNotifications.sendNotification(pk, message, constants.Notification_Time, context);
                //todo from server..
                if (!fromMe) object.setColorMsg(true);
                list.add(0, object);
                Intent intent = new Intent(constants.newMsg);
                localBroadcastManager = LocalBroadcastManager.getInstance(context);
                localBroadcastManager.sendBroadcast(intent);
            }
            super.onPostExecute(object);
        }
    }
   /* public static void delete(dataobject d) {
        if (list != null && list.size() > 0) {
            for (int k = 0; k < list.size(); k++) {
                if (list.get(k).getUser().getId() == d.getUser().getId()) {
                    list.remove(k);
                }
            }
        }
    }*/

    /* public static void updateChats() {

         if (list != null && MainActivity.c != null) {
             MainActivity.c.update(list);
         }
     }*/

    public static void unColorMsg(String id, Context context) {
        isChanged = true;
        isSaved = false;
        if (list == null) {
            list = new ArrayList<>();
            SharedPreferences sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
            if (sharedPreferences.contains(constants.CHATS_LIST)) {
                String jsonchats = sharedPreferences.getString(constants.CHATS_LIST, null);
                if (jsonchats != null) {
                    Gson gson = new Gson();
                    dataobject[] dataobjects = gson.fromJson(jsonchats, dataobject[].class);
                    for (dataobject i : dataobjects) {
                        list.add(i);
                    }
                }
            }
        }
        if (list != null && list.size() > 0) {
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).getId().equals(id)) {
                    list.get(j).setColorMsg(false);
                    return;
                }
            }

        }
    }

    public static void updateBlocks(Context context, boolean canInvite,  boolean canViewProfile,  boolean canMessage,
                                    String id,boolean canAccessLocation){
        isChanged = true;
        isSaved = false;
        if (list == null) {
            list = new ArrayList<>();
            SharedPreferences sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
            if (sharedPreferences.contains(constants.CHATS_LIST)) {
                String jsonchats = sharedPreferences.getString(constants.CHATS_LIST, null);
                if (jsonchats != null) {
                    Gson gson = new Gson();
                    dataobject[] dataobjects = gson.fromJson(jsonchats, dataobject[].class);
                    for (dataobject i : dataobjects) {
                        list.add(i);
                    }
                }
            }
        }

            for(dataobject obj:list){
                if(obj.getId().equals(id)){
                    settingsObj so=obj.getSettings();
                    Log.d(constants.zeeley,"bfr"+ so.canInvite + " " + so.canViewProfile + " " + so.canMessage + " " + so.canAccessLocation + " ");
                    so.setCanViewProfile(canViewProfile);
                    so.setCanInvite(canInvite);
                    so.setCanMessage(canMessage);
                    so.setCanAccessLocation(canAccessLocation);
                    Log.d(constants.zeeley, "aftr"+so.canInvite + " " + so.canViewProfile + " " + so.canMessage + " " + so.canAccessLocation + " ");
                    break;
                }
            }

    }

    public static void updateGrpList(Context context,String id,String senderName,String message,String date) {
        isChanged = true;
        isSaved = false;
        if (list == null) {
            list = new ArrayList<>();
            SharedPreferences sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
            if (sharedPreferences.contains(constants.CHATS_LIST)) {
                String jsonchats = sharedPreferences.getString(constants.CHATS_LIST, null);
                if (jsonchats != null) {
                    Gson gson = new Gson();
                    dataobject[] dataobjects = gson.fromJson(jsonchats, dataobject[].class);
                    for (dataobject i : dataobjects) {
                        list.add(i);
                    }
                }
            }
        } else {
           /* SharedPreferences sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
            String g = sharedPreferences.getString(constants.Group_Obj, constants.DEFAULT);
            if (!g.equals(constants.DEFAULT)) {
                Gson gson = new Gson();
                dataobject d = gson.fromJson(g, dataobject.class);
                list.add(0, d);
            }*/
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).getId().equals(id)) {
                    dataobject obj = list.get(j);
                    if (constants.Current_Id != Integer.parseInt(id)) {

                        if (senderName!=null) obj.setColorMsg(true);
                    }
                    String lm;
                    if(senderName!=null){
                         lm=senderName+" : "+message;
                    }
                    else lm=message;
                    obj.setDate(date);
                    obj.setLastmessage(lm);

                    if (j != 0) {
                        list.remove(j);
                        list.add(0, obj);

                    }
                    //list.set(0, obj);
                  break;
                }
            }
        }
    }

}
   /* public static boolean isPresent(String id, Context context) {
        if (list == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences
                    (constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
            String json = sharedPreferences.getString()
        }
        if (list != null && list.size() > 0) {
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).getId().equals(id)) {
                    list.get(j).setColorMsg(false);
                    return;
                }
            }

        }

        return false;
    }*/





