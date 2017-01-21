package com.zeeley;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gannu on 17-07-2016.
 */
public class constants {
    public static final String SOURCE = "source";
    public static final String FROM_FORWARD = "from_forward";
    public static final String FROM_REGULAR = "from_regular";
    public static final String FORWARD_MSGSLIST = "forward msgs list";
    public static final String USER = "user";
    public static final String latitude = "latitude";
    public static final String longitude = "longitude";
    public static final String Trip = "trip";
    public static final String callDB = "calldb";
    public static final String isJoined = "isjoined";
    public static final String All_Interests = "all interest";
    public static final String BUNDLE = "bundle";
    public static final String MY_SHAREDPREFERENCES = "mysharedprefereences";
    public static final String LOGGED_IN_USERS = "loggedinusers";
    public static final String LOGGED_IN_USERS_SET = "loggedinuserssset";
    public static final String IS_LOGGEDIN = "isloggedin?";
    //public static List<Parcelable> onlineUserList = new ArrayList<>();
    public static final String INTEREST_LISTS = "interests list";
    public static Parcelable[] pa;
    public static final int notificationId = 525;
    public static final String BLOCKED_CONTACTS = "blocked contacts";
    public static final String isChange = "ischange";
    public static final String INTEREST = "INTEREST";
    public static final String DEFAULT = "default values";
    public static final String JSONDATA = "jssondaata";
    public static final String INVITATION = "Invitation";
    public static final String unSendMessages = "unsend messages";
    public static final String VIEWINNG_PROFILE = "Viewing Profile";
    public static final String Group_Obj = "groupobject";
    public static final String MESSAGING = "Messaging";
    public static final String Accesing_location = "Accessing Location";
    public static final String LOGIN = "login";
    public static final String joinRequestAction = "joinrequestaction";
    public static final String joinedAction = "joinedaction";
    public static final String deleteAction = "deleteAction";
    public static final String joinAction = "joinAction";
    public static final String submitAction = "submitAction";
    public static final String requestSent = "requestsent";
    public static final String joined = "joined";
    public static final String Id = "id";
    public static final String callFinish = "callfinish";
    public static final String DntShowCB = "dont show check box";
    public static final String CHATS_LIST = "chats list";
    public static ArrayList<onlineUser> USERS_LIST = new ArrayList<>();
    public static String userInterst;
    public static final String Interest_Id = "interest id";
    public static final String UPDATE_DATA = "update data";
    public static final String From_blockedList = "from blocked contacts";
    public static final String From_addInterest = "from add interesst";
    public static final String From_showInterest = "from show interest";
    public static final String From_chooseInterest = "choose interest";
    public static final String From_changeInterest = "change interest";
    public static Double MY_LATITUDE;
    public static Double MY_LONGITUDE;
    public static final String Latitude = "latitude";
    public static final String Longitude = "longitude";
    public static final String fromInterestList = "fromintereslist";
    public static final String fromNotification = "fromnotification";
    public static final String newMsg = "newmsg";
    public static final String teamId = "teamid";
    public static final String senderId = "senderid";
    public static final String teamName = "teamname";
    public static final String invitaionInterest = "invitationinterest";
    public static final String notificationTime = "notificationtime";
    public static final String isFormInvitaion = "isforminvitation";
    public static final String finishUrself = "finish";
    public static final String acceptAction = "acceptaction";
    public static final String zeeley = "zeeley";
    public static final String Accept = "accept";
    public static final String acceptFormAction = "acceptformaction";
    public static final String rejectFormAction = "rejectformaction";
    public static final String Reject = "reject";
    public static Integer Current_Id = -1;
    public static final String From_onlineUser = "fromonlineuser";
    //public static final String From_chats="fromchats";
    public static final String userName = "userName";
    public static final String userMail = "usermail";
    public static final String userGender = "usergender";
    public static final String userbirthday = "userbirthday";
    public static final String downloadProfileAction = "downloadprofileaction";
    public static final String userProfPic = "profile picture.jpg";

    public static final String Message_Action = "messageAction";
    public static final String New_Message = "newMessage";
    public static ArrayList<String> notifications = new ArrayList<>();
    public static final String Dummy = "dummy";
    static Cursor cursor;
    static HashMap<Integer, Bitmap> images = new HashMap<>();
    public static long Notification_Time;
    public static final String NA = "N/A";



    public static void dialog(Context c, String title, final int i) {
        Log.d(constants.zeeley," ");

        new MaterialDialog.Builder(c)
                .title(title)
                .positiveText("ok")
                .negativeText("cancel")
                .autoDismiss(true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (i) {
                            case 0:

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
   /* if(!constants.isNetworkAvailable(Interst_List.this)){
        Toast.makeText(Interst_List.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }
    else{

    }
            new FetchOtherUsers(Interst_List.this, constants.getInterstTitle((int) id)).execute();*/

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static Bitmap getImage(Integer pk, Context context) {
        if (images.isEmpty()) {
            SQLiteDatabase db = new database(context).getReadableDatabase();
            cursor = db.query(database.Image_Table, new String[]{database.UserId, database.Profpic}, null, null, null, null, null);
            //cursor.moveToFirst();
            while (cursor.moveToNext()) {
                byte[] img = cursor.getBlob(cursor.getColumnIndex(database.Profpic));
                Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                images.put(cursor.getInt(cursor.getColumnIndex(database.UserId)), bitmap);
            }
            cursor.close();
            db.close();
        }
        if (images.get(pk) == null) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.default_profpic);
        }
        return images.get(pk);
    }

    public static ArrayList<Parcelable> getinfoData(String interest) {
        Log.d(constants.zeeley,"getinfodata in cnstns cald");
        ArrayList<Parcelable> users = new ArrayList<>();
        for (onlineUser user : USERS_LIST) {

            users.add(Parcels.wrap(user));
        }
        return users;
    }

    public ArrayList<interestItem> getInterestitems() {
        ArrayList<interestItem> items = new ArrayList<>();
        //interestItem


        return items;
    }

    public static ArrayList<onlineUser> getInterestUsers(String interest) {
        ArrayList<onlineUser> users = new ArrayList<>();
        for (int i = 0; i < USERS_LIST.size(); i++) {
            onlineUser user = USERS_LIST.get(i);
            if (user.getInterest().equals(interest)) {
                users.add(user);
            }
        }
        return users;
    }

    private static HashMap<Integer, String> interestTitles = new HashMap<>();

    private static void setInterestTitles() {
        interestTitles.put(1, "Pizza");
        interestTitles.put(2, "Hamburger");
        interestTitles.put(3, "Roll");
        interestTitles.put(4, "Biryani");
        interestTitles.put(5, "Cricket");
        interestTitles.put(6, "Football");
        interestTitles.put(7, "Basketball");
        interestTitles.put(8, "Tennis");
        interestTitles.put(9, "Gym");
        interestTitles.put(10, "Guitar");
        interestTitles.put(11, "Singer");
        interestTitles.put(12, "Drummer");
        interestTitles.put(13, "Film");
        interestTitles.put(14, "Study");
        interestTitles.put(15, "Restaurant");
        interestTitles.put(16, "Taxi");
        interestTitles.put(17, "Auto");
        interestTitles.put(18, "Dating");
        interestTitles.put(19, "Chating");
        /////
        interestTitles.put(1, "Cricket");
        interestTitles.put(1, "Football");
        interestTitles.put(1, "Guitar");
        interestTitles.put(1, "Piano");
        interestTitles.put(1, "Dance");
        interestTitles.put(1, "Drum");
        interestTitles.put(1, "Flute");
        interestTitles.put(1, "Singing");
        interestTitles.put(1, "Saxophone");
        interestTitles.put(1, "Violin");
        interestTitles.put(1, "Tabla");
        interestTitles.put(1, "Singles In the City");
        interestTitles.put(1, "Dating");
        interestTitles.put(1, "Travelling");
        interestTitles.put(1, "Chess");
        interestTitles.put(1, "Band");
        interestTitles.put(1, "Kabaddi");
        interestTitles.put(1, "Athletics");
        interestTitles.put(1, "Badminton");
        interestTitles.put(1, "Basketball");
        interestTitles.put(1, "Boxing");
        interestTitles.put(1, "Bungee");
        interestTitles.put(1, "Cook");
        interestTitles.put(1, "Cycling");
        interestTitles.put(1, "Entrepreneurship");
        interestTitles.put(1, "Film");
        interestTitles.put(1, "Pizza");


       /* "Gym"
        "Hillclimbing"
        "Hockey"
        "Judo"
        "Khokho"
        "Party"
        "Restaurant"
        "Rugby"
        "Stunt"
        "Swimming"
        "Tabletennis"
        "Tennis"
        "Volleyball"
        "Quicky"
        "Pizza"
        "Gaming"
        "Chat"
        "Study"
        "Cab_Sharing"*/

    }

    private static HashMap<Integer, Integer> interestImages = new HashMap<>();

    private static void setInterestImages() {
        interestImages.put(1, R.drawable.pizza);
        interestImages.put(2, R.drawable.hamburger);
        interestImages.put(3, R.drawable.wrap);
        interestImages.put(4, R.drawable.biryani);
        interestImages.put(5, R.drawable.cricket);
        interestImages.put(6, R.drawable.football);
        interestImages.put(7, R.drawable.basketball);
        interestImages.put(8, R.drawable.tennis);
        interestImages.put(9, R.drawable.gym);
        interestImages.put(10, R.drawable.guitar);
        interestImages.put(11, R.drawable.singer);
        interestImages.put(12, R.drawable.drummer);
        interestImages.put(13, R.drawable.movie);
        interestImages.put(14, R.drawable.study);
        interestImages.put(15, R.drawable.restaurant);
        interestImages.put(16, R.drawable.taxi);
        interestImages.put(17, R.drawable.auto);
        interestImages.put(18, R.drawable.dating);
        interestImages.put(19, R.drawable.chating);
    }

    public static String getInterstTitle(int id) {
        if (interestTitles.isEmpty()) {
            setInterestTitles();
        }
        return interestTitles.get(id);
    }

    public static int getInterestImage(int id) {
        if (interestImages.isEmpty()) {
            setInterestImages();
        }

        return interestImages.get(id);
    }

    public static ArrayList<onlineUser> getUsersList() {
        return USERS_LIST;
    }

    public static String getDistance(Double latitude, Double longitude, String settings) {
        if (constants.MY_LONGITUDE != null && constants.MY_LATITUDE != null) {
            Location l1 = new Location("my_location");
            l1.setLatitude(constants.MY_LATITUDE);
            l1.setLongitude(constants.MY_LONGITUDE);

            Location l2 = new Location("onlineUser_location");
            l2.setLatitude(latitude);
            l2.setLongitude(longitude);

            float distance = l1.distanceTo(l2);
            settingsObj set = new settingsObj(settings);
            if (!set.isDistanceKnown()) {
                distance += 30;
            }
            String dist = distance + " M";
            if (distance > 1000.0f) {
                distance = distance / 1000.0f;
                dist = distance + " KM";
            }
            return dist;
        }
        return NA;

    }
}
