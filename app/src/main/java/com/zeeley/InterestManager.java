package com.zeeley;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class InterestManager {

    public final int COTRAVELLING = 0;
    public final int RESTAURANT = 1;
    public final int CAB_SHARING = 2;
    public final int PC_GAME_SHARING = 3;
    public final int MOVIE_SHARING = 4;
    public final int QUIKY = 5;
    private Context context = null;

    public InterestManager(Context context) {
        this.context = context;
    }

    public static class Cotravelling {


        public void createCotravelEvent(String place_from, String place_to, String from_lat, String from_long,
                                        String to_lat, String to_long, String max_memb, String remark, String date, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/cotravelling/" + "?place_from=" + place_from +
                    "&place_to=" + place_to + "&from_lat=" + from_lat + "&from_long=" + from_long + "&to_lat=" + to_lat
                    + "&to_long=" + to_long + "&max_memb=" + max_memb + "&remark=" + remark + "&date=" + date, context, false,null);
            manager.execute();

        }

        public void joinTravellingTrip(String pk, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/trip_request/?trip_id=" + pk, context, true,pk);
            manager.execute();
        }

        public void deleteUser(String pk, String trip_id, String remark, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/trip_edit/?trip_id=" + trip_id + "&memb_remove="
                    + pk + "&remark=" + remark, context, null,pk);
            manager.execute();
        }
    }

    public static class Restaurant {

        public void createRestaurantEvent(String restName, String date, String remark, boolean isVeg, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/rest_share/" + "?restaurant=" + restName +
                    "&remark=" + remark + "&date=" + date + "&food_type=" + (isVeg ? "1" : "0"), context, false,null);
            manager.execute();

        }

        public void joinRestaurant(String trip_id, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/rest_request/?trip_id=" + trip_id, context, true,trip_id);
            manager.execute();
        }

        public void deleteUser(String pk, String trip_id, String remark, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/rest_edit/?trip_id=" + trip_id + "&memb_remove="
                    + pk + "&remark=" + remark, context, null,pk);
            manager.execute();
        }
    }

    public static class CabSharing {

        public void createCabSharing(String place_from, String place_to, String from_lat, String from_long, String to_lat,
                                     String to_long, String max_memb, String remark, String date, String time, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/cab_sharing/?place_from=" + place_from +
                    "&place_to=" + place_to +
                    "&from_lat=" + from_lat +
                    "&from_long=" + from_long +
                    "&to_lat=" + to_lat +
                    "&to_long=" + to_long +
                    "&max_memb=" + max_memb +
                    "&remark=" + remark +
                    "&date=" + date +
                    "&time=" + time, context, false,null);
            manager.execute();

        }

        public void joinCab(String trip_id, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/cab_request/?trip_id=" + trip_id, context, true,trip_id);
            manager.execute();
        }

        public void deleteUser(String trip_id, String pk, String remark, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/cab_edit/?trip_id=" + trip_id + "&memb_remove="
                    + pk + "&remark=" + remark, context, null,pk);
            manager.execute();
        }
    }

    public static class PCGame {

        public void createPCGameSharing(String gameName, String date, String time, String ip, String remark, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/pcgame_share/" + "?game=" + gameName +
                    "&date=" + date + "&time=" + time + "&ip=" + ip + "&remark=" + remark, context, false,null);
            manager.execute();
        }

        public void joinGame(String trip_id, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/pcgame_request/?trip_id=" + trip_id, context, true,trip_id);
            manager.execute();
        }

        public void deleteUser(String pk, String trip_id, String remark, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/pcgame_edit/?trip_id=" + trip_id + "&memb_remove="
                    + pk + "&remark=" + remark, context, null,pk);
            manager.execute();
        }
    }

    public static class Movie {

        public void createMovieShare(String filmName, String date, String remark, String theatre, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/movie_share/" + "?film=" + filmName +
                    "&remark=" + remark + "&date=" + date + "&theatre=" + theatre, context, false,null);
            manager.execute();
        }

        public void joinMovie(String trip_id, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/movie_request/?trip_id=" + trip_id, context, true,trip_id);
            manager.execute();
        }

        public void deleteUser(String pk, String trip_id, String remark, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/movie_edit/?trip_id=" + trip_id + "&memb_remove="
                    + pk + "&remark=" + remark, context, null,pk);
            manager.execute();
        }
    }

    public static class Quiky {

        public void createQuicky(String quickyName, String date, String time, String remark, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/quicky_share/" + "?quicky=" + quickyName +
                    "&remark=" + remark + "&date=" + date + "&time=" + time, context, false,null);
            manager.execute();
        }

        public void joinQuicky(String trip_id, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/quicky_request/?trip_id=" + trip_id, context, true,trip_id);
            manager.execute();
        }

        public void deleteUser(String pk, String trip_id, String remark, Context context) {
            urlManager manager = new urlManager("http://zeeley.com/quicky_edit/?trip_id=" + trip_id + "&memb_remove="
                    + pk + "&remark=" + remark, context, null,pk);
            manager.execute();
        }
    }

    public static class urlManager extends AsyncTask<Void, Void, Void> {
        int join;
        boolean isDone;
        String url;
        Context context;
        String tripId;
        Boolean isJoin;
        ProgressDialog progressDialog;

        urlManager(String url, Context context, Boolean isJoin,String tripId) {
            Log.d(constants.zeeley, "url is " + url);
            this.url = url;
            join = -1;
            this.tripId=tripId;
            this.context = context;
            this.isJoin = isJoin;
            progressDialog=new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(isJoin==null){
                progressDialog.setMessage("Removing user..");
            }
            else if(isJoin){
                progressDialog.setMessage("Sending Join Request.."); 
            }
            else {
                progressDialog.setMessage("Submitting Form");
            }
           
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.d(constants.zeeley, "inside doinbg of manager");
                String ur=url.replace(" ", "%20");
                Log.d(constants.zeeley,"the url is "+ ur);
                URL u = new URL(ur);
                HttpURLConnection connection = (HttpURLConnection) u.openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    isDone = true;
                    Log.d(constants.zeeley, "jooined");
                } else {
                    Log.d(constants.zeeley, "not jooined");
                    isDone = false;
                }


            }/* catch (Exception e) {
                Log.w(constants.zeeley, "except ocurd while submiing form " + e.getMessage());
                isDone = false;
            }*/ catch (MalformedURLException e) {
                Log.d(constants.zeeley,"malfrmd excep");
                e.printStackTrace();
            } catch (ProtocolException e) {
                Log.d(constants.zeeley,"protocol excep");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(constants.zeeley,"io excep");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            if (isDone) {
                LocalBroadcastManager manager=LocalBroadcastManager.getInstance(context);
                if (isJoin == null){
                    Toast.makeText(context, "User Removed", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(constants.deleteAction);
                    if(tripId!=null){
                        intent.putExtra(constants.Id,tripId);
                    }
                    manager.sendBroadcast(intent);
                }
                else if (isJoin){
                    Toast.makeText(context, "Join Request sent", Toast.LENGTH_LONG).show();
                    if(tripId!=null){
                        SharedPreferences preferences=context.getSharedPreferences(constants.MY_SHAREDPREFERENCES,Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString(tripId,constants.requestSent);
                        editor.apply();
                        Intent intent=new Intent(constants.joinAction);
                        manager.sendBroadcast(intent);
                    }

                }
                else{
                    Intent intent=new Intent(constants.submitAction);
                    manager.sendBroadcast(intent);
                    Toast.makeText(context, "Form submitted", Toast.LENGTH_LONG).show();
                }

            } else
                Toast.makeText(context, "Some error ocoured", Toast.LENGTH_LONG).show();
        }
    }
}