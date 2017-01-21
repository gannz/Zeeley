package com.zeeley;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class my_login1 extends AppCompatActivity implements View.OnClickListener {
    EditText mail;
    EditText pass;

    Button login, fb_login;
    TextView signup;
    ArrayList<dataobject> list = new ArrayList<>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    final String url_path = "http://zeeley.com/android_login";
    final static String FIELDS_STRING = "fields";
    final static String FIRST_NAME = "first_name";
    final static String LAST_NAME = "last_name";
    final static String CURRENT_INTEREST = "current_intrest";
    final static String PRIMARY_KEY = "pk";
    public static final String USER_DATA = "userdata";
    final static String SETTINGS_STRING = "priv_string";
    final static String IS_LOCATION_ACCESSIBLE = "isLocationAccessible";
    final static String IS_DISTANCE_KNOWN = "isDistanceKnown";
    ProgressDialog progressDialog;
    //facebook login
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    Bundle parameters;
    private String user_name;
    private String user_gender;
    private String user_birthday;
    private String user_email;
    Profile profile;
    HashMap<String, ArrayList<ChatMessage>> hashmap;
    HashMap<String, Bitmap> images;
    ArrayList<String> pks = new ArrayList<>();
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            profile = Profile.getCurrentProfile();
            // nextActivity(profile);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FacebookSdk.sdkInitialize(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylogin1);

        //facebook login start
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };


        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                //nextActivity(currentProfile);
            }

        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday"));

        callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                profile = Profile.getCurrentProfile();

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                String email = null;
                                try {
                                    user_email = object.getString("email");
                                    user_birthday = object.getString("birthday");
                                    user_name = object.getString("name");
                                    user_gender = object.getString("gender");

                                    new getFacebookProfilePicture(profile.getId());


                                    // 01/31/1980 format
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

                //nextActivity(profile);
                Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

                Toast.makeText(getApplicationContext(), "There is some error. Try again.", Toast.LENGTH_SHORT).show();

            }
        };
        loginButton.registerCallback(callbackManager, callback);
        //facebook login end

        sharedPreferences = getSharedPreferences(constants.LOGIN, Context.MODE_PRIVATE);
       /* if(!sharedPreferences.contains(constants.LOGGED_IN_USERS)){
            editor=sharedPreferences.edit();
            editor.putStringSet(constants.LOGGED_IN_USERS,)

        }*/


        mail = (EditText) findViewById(R.id.user);
        pass = (EditText) findViewById(R.id.pass);
        signup = (TextView) findViewById(R.id.signup);


        login = (Button) findViewById(R.id.login);
       /* login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(my_login1.this, Interst_List.class);
                i.putExtra(constants.SOURCE, constants.From_chooseInterest);
                startActivity(i);
            }
        });*/
        login.setOnClickListener(this);
        //fb_login = (Button) findViewById(R.id.fb_login);
        //fb_login.setOnClickListener(this);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(my_login1.this, my_login2.class);
                startActivity(it);

            }
        });


    }

 public void sendFbdata(String email,String bday,String name,String gender){

 }
    @Override
    protected void onStop() {
        super.onStop();

        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public void registerFbUser(String email, String fbid, String prof_pic, String first_name, String last_name,
                               String date, String month, String year, boolean isMale){
        new AsyncTask<String,Void,Void>(){
            public Void doInBackground(String... params){
                try{
                    URL url = new URL("http://www.zeeley.com/android_fblogin/"
                            + "?gmail=" + params[0]
                            + "&fbid=" + params[1]
                            + "&prof_pic=" + params[2]
                            + "&first_name=" + params[3]
                            + "&last_name=" + params[4]
                            + "&date=" + params[5]
                            + "&month=" + params[6]
                            + "&year=" + params[7]
                            + "&male=" + params[8]);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setInstanceFollowRedirects(true);
                    conn.connect();

                }catch(Exception e){
                    Log.d("Zeeley",e.getMessage());
                }
                return null;
            }
        }.execute(email, fbid, prof_pic, first_name, last_name,
                date, month, year, (isMale?"1":"0"));
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.login:
                if(!constants.isNetworkAvailable(this)){
                    Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
                else {
                    try {

                        String urlParams = "?email=" + URLEncoder.encode(mail.getText().toString(), "utf-8") + "&password=" +
                                URLEncoder.encode(pass.getText().toString(), "utf-8");
                        URL url = new URL(url_path + urlParams);
                        new SetUpConnection().execute(url);

                    } catch (Exception e) {
                        Log.d(constants.zeeley, "excep when login btn clickd");
                        System.out.println(e.getMessage());
                    }
                }

        }

    }

    private class SetUpConnection extends AsyncTask<URL, Integer, String> {

        boolean loggedIn = false;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(my_login1.this);
            progressDialog.setMessage("Logging in..");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
            Log.d("zeeley", "setup conn  start");
        }

        @Override
        protected String doInBackground(URL... params) {
            StringBuilder st = new StringBuilder();
            try {
                HttpURLConnection conn = (HttpURLConnection) params[0].openConnection();

                conn.setRequestMethod("GET");
                conn.setInstanceFollowRedirects(true);
                conn.setRequestProperty("charset", "utf-8");
                conn.connect();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    for (String s; (s = r.readLine()) != null; ) {
                        st.append(s + "\n");
                    }

                    JSONArray jsonArray = new JSONArray(st.toString());
                    loggedIn = true;
                    return st.toString();
                } else
                    return "Error " + conn.getResponseCode() + ": " + conn.getResponseMessage();
            } catch (ProtocolException e) {
                Log.e("zeeley", e.getMessage());
            } catch (JSONException e) {
                loggedIn = false;

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return st.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("zeeley", "Handed Over to populate list");
            if (loggedIn) {
                // show remember pass dialog and save email
                new populateList().execute(s);

                SharedPreferences sharedPreferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, MODE_PRIVATE);
                sendRegistrationToken(FirebaseInstanceId.getInstance().getToken());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(constants.IS_LOGGEDIN, true);
                editor.apply();
            } else {
                Toast.makeText(my_login1.this, s, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }

    private void sendRegistrationToken(String token) {
        new AsyncTask<String, Void, Void>() {

            boolean isDone = false;

            @Override
            protected Void doInBackground(String... params) {
                String token = params[0];
                try {
                    URL url = new URL("http://zeeley.com/android_reg_key/?key=" + token);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.setInstanceFollowRedirects(true);
                    if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        isDone = true;
                    }

                } catch (Exception e) {
                    //Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (isDone) {
                    Toast.makeText(my_login1.this, "token sent to server", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(my_login1.this, "token not sent", Toast.LENGTH_LONG).show();
            }
        }.execute(token);
    }


    private class populateList extends AsyncTask<String, String, Void> {

        JSONObject parameters;
        JSONObject fields;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            images = new HashMap<>();
            hashmap = new HashMap<>();


        }

        @Override
        protected void onPostExecute(Void pa) {
            super.onPostExecute(pa);
            new LoadGroupChat().execute();
            database db = new database(my_login1.this);
            Log.d("zeeley", "database operations start");
            for (String id : pks) {
                ArrayList<ChatMessage> chatmessages = hashmap.get(id);
                Log.d("zeeley", id + "in post execute");
                db.createTable(id);
                Log.d("zeeley", id + "table created");
                db.addChatUser(chatmessages, id);

            }
            db.putImages(pks, images);
            //TODO:call loadGroupMessages AsyncTask
            //TODO: but below code in postexecute method of loadGroupMessages asynctask

        }

        @Override
        protected void onProgressUpdate(String... values) {

            SharedPreferences preferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(constants.userName, values[0] + " " + values[1]);
            editor.putString(CURRENT_INTEREST, values[2]);
            editor.putString(PRIMARY_KEY, values[3]);
            settingsObj s = new settingsObj(values[4]);
            editor.putBoolean(IS_LOCATION_ACCESSIBLE, s.CanAccessLocation());
            editor.putBoolean(IS_DISTANCE_KNOWN, s.isDistanceKnown());
            editor.apply();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {


                JSONArray jsonArray = new JSONArray(params[0]);
                for (int i = 0; i < jsonArray.length(); i++) {
                    parameters = jsonArray.getJSONObject(i);
                    fields = parameters.getJSONObject(FIELDS_STRING);

                    if (i == 0) {

                        publishProgress(fields.getString(FIRST_NAME), fields.getString(LAST_NAME), fields.getString(CURRENT_INTEREST),
                                parameters.getString(PRIMARY_KEY), fields.getString(SETTINGS_STRING));
                    } else {
                        String firstName = fields.getString(FIRST_NAME);
                        // Log.d("Deb1FirstName", firstName);
                        String lastName = fields.getString(LAST_NAME);
                        String currentInterest = fields.getString(CURRENT_INTEREST);
                        String pk = parameters.getInt(PRIMARY_KEY) + "";
                        String setting = fields.getString(SETTINGS_STRING);
                        URL u = new URL("http://zeeley.com/media/file_" + pk + ".thumbnail");
                        HttpURLConnection con = (HttpURLConnection) u.openConnection();
                        con.setRequestMethod("GET");
                        con.setInstanceFollowRedirects(true);
                        Bitmap b = BitmapFactory.decodeStream(con.getInputStream());
                        images.put(pk, b);
                        Log.d("zeeley", pk);
                        pks.add(pk);

                        URL url = new URL("http://zeeley.com/android_message/" + "?to=" + pk);
                        Log.d("zeeley", "calling messaging url");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setInstanceFollowRedirects(true);
                        Date date = null;
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            Log.d("zeeley", " msgs loading");
                            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            StringBuilder stringBuilder = new StringBuilder();
                            for (String s; (s = r.readLine()) != null; ) stringBuilder.append(s);
                            JSONArray array = new JSONArray(stringBuilder.toString());

                            if (array.length() != 0) {
                                ArrayList<ChatMessage> messages = new ArrayList<>();
                                JSONObject fields;
                                JSONObject parameters;
                                String last_msg = null;
                                for (int k = array.length() - 1; k >= 0; k--) {
                                    parameters = array.getJSONObject(k);
                                    fields = parameters.getJSONObject("fields");
                                    //sender id
                                    int m_id = fields.getInt("m_id");
                                    String m_message = fields.getString("m_message");
                                    String post_time = fields.getString("post_time");
                                    date = parseDate(post_time);
                                    Log.d(constants.zeeley, m_message);
                                    // String date = post_time.
                                    boolean isMine = false;
                                    if (m_id == Integer.parseInt(pk))
                                        isMine = true;
                                    if (k == array.length() - 1) {
                                        last_msg = m_message;
                                        dataobject user = new dataobject(firstName + " " + lastName, CommonMethods.dateFormat.format(date),
                                                last_msg, pk, new settingsObj(setting), false);
                                        list.add(user);
                                    }
                                    ChatMessage chatMessage = new ChatMessage(m_message, isMine);
                                    chatMessage.setIsImage(fields.getBoolean("m_photo"));
                                    chatMessage.setDate(post_time);

                                    messages.add(chatMessage);
                                    //db.addChatUser(chatMessage, pk);

                                }
                                hashmap.put(pk, messages);
                            }
                        } else {
                            Log.d("zeeley", "messaging url not called");
                        }
                    }

                }
            } catch (Exception e) {
                Log.d("zeeley", e.getMessage());
            }
            return null;
        }


    }

    public Date parseDate(String d) {
        String date = d.substring(0, d.indexOf('T'));
        String time = d.substring(d.indexOf('T') + 1, d.indexOf('Z'));

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date dt;

        try {
            dt = df.parse(date + " " + time);
            return dt;
        } catch (ParseException e) {
            Log.e("zeeley", "cant parse post_time");
        }


        return null;
    }


    class getFacebookProfilePicture extends AsyncTask<Void, Void, Void> {
        URL imageUrl = null;
        Bitmap bitmap = null;
        String userID;

        public getFacebookProfilePicture(String userID) {
            this.userID = userID;
        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                imageUrl = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
                bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            FileOutputStream out;
            try {
                if (bitmap != null) {
                    out = openFileOutput(constants.userProfPic, Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.close();
                }
                SharedPreferences preferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(constants.userName, user_name);
                editor.putString(constants.userbirthday, user_birthday);
                editor.putString(constants.userGender, user_gender);
                editor.putString(constants.userMail, user_email);
                editor.apply();
            } catch (Exception e) {
                e.printStackTrace();

                super.onPostExecute(aVoid);
            }
        }


    }

    private class LoadGroupChat extends AsyncTask<Void, Void, Void> {
        SharedPreferences preferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
        String pk = preferences.getString(PRIMARY_KEY, "-1");
        HashMap<String, ArrayList<GroupChatMessage>> map = new HashMap<>();
        ArrayList<String> teamID = new ArrayList<>();
        ArrayList<groupParticipant> groupParticipants = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL("http://zeeley.com/android_team/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);
                BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                HashMap<String, String> teamNames = new HashMap<>();
                for (String s; (s = r.readLine()) != null; ) {
                    stringBuilder.append(s);
                }
                JSONArray array = new JSONArray(stringBuilder.toString());
                JSONObject parameters, fields;
                for (int i = 0; i < array.length(); i++) {
                    parameters = array.getJSONObject(i);
                    fields = parameters.getJSONObject("fields");
                    String t_id = parameters.getInt("pk") + "";
                    String team_name = fields.getString("team_name");
                    Log.d(constants.zeeley, t_id + " " + team_name);
                    teamID.add(t_id);
                    teamNames.put(t_id, team_name);
                }
                for (String team : teamID) {
                    HashMap<String, String> names = new HashMap<>();
                    url = new URL("http://zeeley.com/team_left_info/?team_id=" + team + "&trip_id=");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setInstanceFollowRedirects(true);

                    r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    stringBuilder = new StringBuilder();
                    for (String s; (s = r.readLine()) != null; ) {
                        stringBuilder.append(s);
                    }

                    array = new JSONArray(stringBuilder.toString());
                    for (int i = 0; i < array.length(); i++) {
                        parameters = array.getJSONObject(i);
                        fields = parameters.getJSONObject("fields");
                        String name = fields.getString("first_name");
                        String settings = fields.getString("priv_string");
                        Log.d(constants.zeeley, name);
                        String p_k = String.valueOf(parameters.getInt("pk"));

                        Log.d("zeeley", p_k + " : " + name);
                        names.put(p_k, name);
                        URL u = new URL("http://zeeley.com/media/file_" + p_k + ".thumbnail");
                        HttpURLConnection con = (HttpURLConnection) u.openConnection();
                        con.setRequestMethod("GET");
                        con.setInstanceFollowRedirects(true);
                        Bitmap b = BitmapFactory.decodeStream(con.getInputStream());
                        groupParticipant gp = new groupParticipant(team, name, p_k, b, settings);
                        groupParticipants.add(gp);
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
                    ArrayList<GroupChatMessage> messages = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        parameter = array.getJSONObject(i);
                        fields = parameter.getJSONObject("fields");
                        String post_time = fields.getString("post_time");
                        String id = String.valueOf(fields.getInt("teamc_id"));
                        String message = fields.getString("teamc_message");
                        Log.d(constants.zeeley, message);
                        String team_id = String.valueOf(fields.getInt("teamc_group"));
                        Log.d(constants.zeeley, id);

                        Log.d("zeeley", names.size() + "");
                        Date date = chatactivity.parseDate(post_time);

                        GroupChatMessage gcm = new GroupChatMessage(team_id, message,CommonMethods.format.format(date),CommonMethods.dateFormat.format(date), names.get(id), Integer.parseInt(id) == Integer.parseInt(pk));
                        gcm.setIsImage(fields.getBoolean("teamc_photo"));
                        messages.add(gcm);
                        Log.d(constants.zeeley, gcm.getMessage() + " mess" +
                                "age added to arraylist");
                        if (i == array.length() - 1) {
                            dataobject obj = new dataobject(teamNames.get(team_id), CommonMethods.dateFormat.format(chatactivity.parseDate(post_time)),
                                    message, team_id, null, true);
                            list.add(obj);
                        }
                    }
                    map.put(team, messages);
                    Log.d("zeeley", "messages added to hashmap");
                }

            } catch (Exception e) {
                Log.d("zeeley", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (map != null) {
                database database = new database(my_login1.this);
                for (String teamid : teamID) {
                    database.createGroupChatTable(teamid);
                    Log.d("zeeley", "Group chat table created");
                    // Log.d("zeeey", map.get(teamid).toString());
                    database.addGroupMessage(map.get(teamid), teamid);
                    Log.d("zeeley", "group message added");

                }
            }
            if (!groupParticipants.isEmpty()) {
                new database(my_login1.this).addgroupParticipants(groupParticipants);
            }
            if (!list.isEmpty()) {
                Log.d("zeeley", "methods complete");
                SharedPreferences sharedPreferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                editor.putString(constants.CHATS_LIST, gson.toJson(list));
                editor.apply();
                Log.d("zeeley", "list saved in shard preferce");
            } else {
                Toast.makeText(my_login1.this, "list is empty", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
            Intent intent = new Intent(my_login1.this, Interst_List.class);
            intent.putExtra(constants.SOURCE, constants.From_chooseInterest);
            startActivity(intent);
            finish();
        }
    }
}
