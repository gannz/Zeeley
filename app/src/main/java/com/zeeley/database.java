package com.zeeley;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

/**
 * Created by gannu on 03-08-2016.
 */
public class database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "zeeleydatabase";
    private static int DATABASE_VERSION = 1;
    public static String ID = "_id";
    public static final String TABLE = "INTERESTGRID";
    public static final String COLUMN_IS_PHOTO = "isPhoto";
    public static final String interest = "interest";
    public static final String image = "image";
    public static final String Name = "name";
    public static final String Distance = "distance";
    public static final String Profpic = "picture";
    public static final String InterestId = "interest";
    public static final String UserId = "userid";
    public static final String block_photo = "blockphoto";
    public static final String block_invitation = "blockinvitation";
    public static final String block_location = "blocklocation";
    public static final String block_messaging = "blockmessaging";
    public static final String TeamName = "teamName";
    public static final String TeamId = "teamId";
    public static final String BLOCK_TABLE = "block_table";
    public static final String COLUMN_PROF_PIC = "profilepicture";
    private Context context;
    private byte[] img = null;
    public static final String notific_table = "notificationTable";
    private static final String CREATE_TABEL = "CREATE TABLE " + TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + interest + " TEXT NOT NULL, " + image + " INTEGER NOT NULL);";
    private static final String create_block_table = "CREATE TABLE " + BLOCK_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + Name + " TEXT," + COLUMN_PROF_PIC + " blob," + UserId + " INTEGER NOT NULL," + block_invitation + " INTEGER NOT NULL,"
            + block_location + " INTEGER NOT NULL," + block_messaging + " INTEGER NOT NULL," + block_photo + " INTEGER NOT NULL);";
    public static final String TABLE_USERS = "users";
    public static final String Profile_table = "profiletable";
    public static final String COLUMN_PRIMARY_KEY = "primary_key";
    public static final String COLUMN_FIRST_NAME = "firstname";
    public static final String COLUMN_LAST_NAME = "lastname";
    public static final String COLUMN_CURRENT_INTEREST = "current_intrest";
    public static final String COLUMN_GENDER = "gender";//TODO: add this in chat table. 1 for male 0 for female
    public static final String COLUMN_AGE = "age";//TODO: add this field and remove latitude and longitude
    public static final String COLUMN_ID = "_id";

    public static final String COLUMN_CHAT_STRING = "chatstring";
    public static final String COLUMN_CHAT_TIME = "chattime";
    public static final String COLUMN_CHAT_DATE = "chatdate";
    public static final String COLUMN_IS_MINE = "ismine";
    public static final String contact_no = "contactno";
    public static final String Latitude = "latitude";
    public static final String Longitude = "longitude";
    public static final String Settings = "settings";
    public static final String isFormInvitation = "isforminvitation";
    public static final String Image_Table = "imagetable";
    public static final String onlineUsersTable = "onlineuserstable";
    //public static final String COLUMN_SETTING = "setting";
    public static final String unSendTable = "unsendtable";
    public static final String pk = "pk";
    public static final String GroupTable = "groupTable_";
    private static final String message = "message";
    private static final String create_unsend_table = "CREATE TABLE " + unSendTable + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + message + " TEXT NOT NULL, "
            + pk + " TEXT NOT NULL);";
    String CREATE_ONLINEUSERS_TABLE = "CREATE TABLE " + onlineUsersTable + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_PRIMARY_KEY + " TEXT," +
            Name + " TEXT," +
            Latitude + " TEXT," +
            Longitude + " TEXT," +
            COLUMN_PROF_PIC + " blob," +
            COLUMN_CURRENT_INTEREST + " TEXT," +
            Settings + " TEXT" + ");";

    database(Context c)

    {

        super(c, DATABASE_NAME, null, DATABASE_VERSION);
        context = c;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
Log.d(constants.zeeley,"exec oncrete in db");
        String CREATE_PROF_TABLE = "CREATE TABLE " + Profile_table + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PRIMARY_KEY + " TEXT," +
                COLUMN_FIRST_NAME + " TEXT," +
                COLUMN_LAST_NAME + " TEXT," +
                contact_no + " TEXT," +
                Latitude + " TEXT," +
                Longitude + " TEXT," +
                COLUMN_PROF_PIC + " blob," +
                COLUMN_CURRENT_INTEREST + " TEXT," +
                COLUMN_GENDER + " TEXT," +
                Settings + " TEXT," +
                COLUMN_AGE + " INTEGER" + ");";


        String create_notific_table = "CREATE TABLE " + notific_table + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                UserId + " INTEGER NOT NULL," +
                COLUMN_FIRST_NAME + " TEXT," +
                COLUMN_LAST_NAME + " TEXT," +
                TeamId + " TEXT," +
                TeamName + " TEXT," +
                contact_no + " TEXT," +
                Latitude + " TEXT," +
                Longitude + " TEXT," +
                COLUMN_PROF_PIC + " blob," +
                COLUMN_CURRENT_INTEREST + " TEXT," +
                COLUMN_GENDER + " TEXT," +
                Settings + " TEXT," +
                isFormInvitation + " INTEGER NOT NULL," +
                COLUMN_AGE + " INTEGER" + ");";

        String Create_Image_Table = "CREATE TABLE " + Image_Table + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Profpic + " blob," + UserId + " INTEGER NOT NULL);";

        db.execSQL(CREATE_ONLINEUSERS_TABLE);
        db.execSQL(CREATE_PROF_TABLE);
        db.execSQL(Create_Image_Table);
        db.execSQL(create_unsend_table);
        //db.execSQL(CREATE_TABEL);
        db.execSQL(create_notific_table);
        db.execSQL(create_block_table);
        // setupTable(db);
        /// setupNotific(db);
    }

    public void createGrpMemTable(String groupId) {
        String groupMembersTable = "CREATE TABLE " + GroupTable + groupId + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Name + " TEXT NOT NULL, " + Settings + " TEXT," + Profpic + " blob," + UserId + " INTEGER NOT NULL);";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(groupMembersTable);
        db.close();
    }

    public void insertBlockMembr(dbBlockdItem blockdItem) {
        // 1 at any block means it is blockd 0 meaans unblockd
        SQLiteDatabase db = this.getWritableDatabase();
        int id = db.delete(BLOCK_TABLE, UserId + "=" + blockdItem.getId(), null);
        Log.d(constants.zeeley, "rows affectd "+id);
        if (id == 0)
            Log.d(constants.zeeley, "user doesnt exixt in db");
        if(blockdItem.isCanAccessLocation()&&blockdItem.isCanInvite()&&blockdItem.isCanMessage()&&blockdItem.isCanViewProfile())
            return;
        ContentValues values = new ContentValues();
        values.put(Name, blockdItem.getName());
        values.put(UserId, blockdItem.getId());
        values.put(block_invitation, blockdItem.isCanInvite() ? 0 : 1);
        values.put(block_location, blockdItem.isCanAccessLocation() ? 0 : 1);
        values.put(block_messaging, blockdItem.isCanMessage() ? 0 : 1);
        values.put(block_photo, blockdItem.isCanViewProfile() ? 0 : 1);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Bitmap img = blockdItem.getImage();
        img.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] b = bos.toByteArray();
        values.put(COLUMN_PROF_PIC, b);
        db.insert(BLOCK_TABLE, null, values);

        db.close();

    }

    public ArrayList<blockedItem> getBlockMbrs() {

        SQLiteDatabase sqdb=this.getReadableDatabase();

        Cursor cursor=sqdb.query(BLOCK_TABLE,new String[]{Name,COLUMN_PROF_PIC,UserId,block_photo,
               block_messaging,block_location,block_invitation},null,null,null,null,ID+" DESC");
        if(cursor.getCount()==0){
            cursor.close();
            sqdb.close();
            return null;
        }
        else {
            ArrayList<blockedItem> list=new ArrayList<>();
            cursor.moveToFirst();
            do {
                byte[] img=cursor.getBlob(cursor.getColumnIndex(COLUMN_PROF_PIC));
                Bitmap bitmap= BitmapFactory.decodeByteArray(img, 0, img.length);
                blockedItem bi=new blockedItem(cursor.getString(cursor.getColumnIndex(Name)),bitmap,
                        cursor.getInt(cursor.getColumnIndex(UserId)));
                int canmessage,canaccesslocation,canviewprofile,caninvite;
                canmessage=cursor.getInt(cursor.getColumnIndex(block_messaging));
                canaccesslocation=cursor.getInt(cursor.getColumnIndex(block_location));
                canviewprofile=cursor.getInt(cursor.getColumnIndex(block_photo));
                caninvite=cursor.getInt(cursor.getColumnIndex(block_invitation));
                ArrayList<String> blocks=new ArrayList<>();
                if(canaccesslocation==1){
                    blocks.add("Accessing location");
                }
                if(caninvite==1){
                    blocks.add("Invitation");
                }
                if(canmessage==1){
                    blocks.add("Messaging");
                }
                if(canviewprofile==1){
                    blocks.add("Viewing profile");
                }
                bi.setBlockedFrom(blocks);
                Log.d(constants.zeeley,bi.getName());
                list.add(bi);
            }while (cursor.moveToNext());
            cursor.close();
            sqdb.close();
            return list;
        }
    }

    public void addGrpMembers(String grpId, ArrayList<grpParticpnt> gps, HashMap<String, Bitmap> imgs) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM sqlite_master WHERE name ='groupTable_" + grpId + "' and type='table'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() != 1 && cursor.getCount() == 0) {
            createGrpMemTable(grpId);
            Log.d("zeeley", grpId + " table created because it did not exist");
        }
        cursor.close();
        SQLiteDatabase s = this.getWritableDatabase();
        for (grpParticpnt gp : gps) {
            ContentValues values = new ContentValues();
            values.put(Name, gp.getName());
            values.put(Settings, gp.getSettings());
            values.put(UserId, gp.getPk());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Bitmap bitmap = imgs.get(gp.getPk());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] img = bos.toByteArray();
            values.put(Profpic, img);
            s.insert(GroupTable + grpId, null, values);
        }
        s.close();
    }

    public void insertOnlineUsers(ArrayList<onlineUser> users) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM sqlite_master WHERE name ='" + onlineUsersTable + "' and type='table'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() == 1) {
            Log.d(constants.zeeley, "deleting table");
            String d = "DROP TABLE IF EXISTS " + onlineUsersTable;
            db.execSQL(d);
        }
        cursor.close();
        db.execSQL(CREATE_ONLINEUSERS_TABLE);
        ContentValues values = new ContentValues();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        for (onlineUser user : users) {
            values.put(Latitude, String.valueOf(user.getLatitude()));
            values.put(Longitude, String.valueOf(user.getLongitude()));
            values.put(Name, user.getName());
            user.getImage().compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] i = bos.toByteArray();
            values.put(COLUMN_PROF_PIC, i);
            values.put(COLUMN_CURRENT_INTEREST, user.getInterest());
            values.put(Settings, user.getSettings().getSettingsString());
            values.put(COLUMN_PRIMARY_KEY, String.valueOf(user.getId()));
            db.insert(onlineUsersTable, null, values);
        }
        db.close();
    }

    public groupParticipant getGrpParticipant(String groupId, String pk) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(GroupTable + groupId, new String[]{Name, Profpic, Settings}, UserId + "=" + pk, null, null, null, null);

        if (cursor.getCount() == 1) {

            byte[] img = cursor.getBlob(cursor.getColumnIndex(Profpic));
            Bitmap b = BitmapFactory.decodeByteArray(img, 0, img.length);
            groupParticipant gp = new groupParticipant(groupId, cursor.getString(cursor.getColumnIndex(Name)), pk,
                    b, cursor.getString(cursor.getColumnIndex(Settings)));
            cursor.close();
            db.close();
            return gp;
        }
        return null;
    }

    public void addGrpParticipant(groupParticipant participant) {
        String groupId = participant.getGroupId();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM sqlite_master WHERE name ='groupTable_" + groupId + "' and type='table'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() != 1 && cursor.getCount() == 0) {
            createGrpMemTable(groupId);
            Log.d("zeeley", groupId + " table created because it did not exist");
        }
        cursor.close();
        Bitmap bitmap = participant.getImage();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] img = bos.toByteArray();
        SQLiteDatabase s = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserId, participant.getPk());
        values.put(Settings, participant.getSettings());
        values.put(Name, participant.getName());
        values.put(Profpic, img);
        s.insert(GroupTable + groupId, null, values);
        s.close();
    }

    public void putImages(ArrayList<String> ids, HashMap<String, Bitmap> imgs) {
        if (!ids.isEmpty() && !imgs.isEmpty()) {
            SQLiteDatabase db = this.getWritableDatabase();

            for (String id : ids) {
                Bitmap bitmap = imgs.get(id);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] i = bos.toByteArray();
                ContentValues values = new ContentValues(2);
                values.put(UserId, Integer.valueOf(id));
                values.put(Profpic, i);
                db.insert(Image_Table, null, values);
            }
            db.close();
        }

    }

    public void putImage(String id, Bitmap image) {

        SQLiteDatabase db = this.getWritableDatabase();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] i = bos.toByteArray();
        ContentValues values = new ContentValues(2);
        values.put(UserId, Integer.valueOf(id));
        values.put(Profpic, i);
        db.insert(Image_Table, null, values);

        db.close();


    }

    public void insertintoProf(profileObj obj) {
        //// TODO: 02-10-2016 update profile pic also
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.userimg);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, bos);
        img = bos.toByteArray();
       /* String query="INSERT OR REPLACE INTO "+ Profile_table +"("+COLUMN_ID+","+COLUMN_PRIMARY_KEY+","+COLUMN_FIRST_NAME+","+
        COLUMN_LAST_NAME+","+contact_no+","+Latitude+","+Longitude+","+COLUMN_PROF_PIC+","+COLUMN_CURRENT_INTEREST+","+
        COLUMN_GENDER+","+COLUMN_AGE+")"+"VALUES"+"(SELECT"+COLUMN_ID+"FROM "+ Profile_table+ "WHERE"+ COLUMN_PRIMARY_KEY+"="+
                obj.getPk()+","+obj.getPk()+","+obj.getFirst_name()+","+obj.getLast_name()+","+obj.getContactNo()+","+
                obj.getLat()+","+obj.getLongit()+","+img+","+obj.getCurrentInterest()+","+obj.getGender()+","+obj.getAge()+");";*/
        String where = COLUMN_PRIMARY_KEY + "=?";
        String[] whereArgs = new String[]{obj.getPk()};
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PROF_PIC, img);
        values.put(COLUMN_PRIMARY_KEY, obj.getPk());
        values.put(COLUMN_FIRST_NAME, obj.getFirst_name());
        values.put(COLUMN_LAST_NAME, obj.getLast_name());
        values.put(contact_no, obj.getContactNo());
        values.put(Latitude, obj.getLat());
        values.put(Longitude, obj.getLongit());
        values.put(COLUMN_CURRENT_INTEREST, obj.getCurrentInterest());
        values.put(COLUMN_GENDER, obj.getGender());
        values.put(Settings, obj.getSettings().getSettingsString());
        values.put(COLUMN_AGE, obj.getAge());
        int i = db.update(Profile_table, values, where, whereArgs);
        if (i == 0) {
            db.insert(Profile_table, null, values);
        }
        db.close();
    }


    public profileObj getProfile(String pk) {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + Profile_table + " WHERE " + COLUMN_PRIMARY_KEY + "='" + pk + "'";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            database.close();
            return null;
        } else {
            profileObj obj = new profileObj(pk, cursor.getColumnName(cursor.getColumnIndex(COLUMN_FIRST_NAME)),
                    cursor.getColumnName(cursor.getColumnIndex(COLUMN_LAST_NAME)),
                    cursor.getColumnName(cursor.getColumnIndex(COLUMN_GENDER)),
                    cursor.getColumnName(cursor.getColumnIndex(COLUMN_CURRENT_INTEREST)),
                    cursor.getColumnName(cursor.getColumnIndex(Latitude)),
                    cursor.getColumnName(cursor.getColumnIndex(Longitude)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_AGE)), null);
            cursor.close();
            database.close();
            return obj;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);

    }


    public void updateTableUsers(String pk, String columnName, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(columnName, value);
        String where = COLUMN_PRIMARY_KEY + "= ?";
        String[] whereargs = new String[]{pk};
        db.update(TABLE_USERS, values, where, whereargs);
        db.close();
    }

    public void addUnsendMessage(String id, String msg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(message, msg);
        values.put(pk, id);
        db.insert(unSendTable, null, values);
        db.close();
    }

    public ArrayList<ChatMessage> getChat(String pk) {
        ArrayList<ChatMessage> messages = new ArrayList<>();
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT * FROM sqlite_master WHERE name ='table_" + pk + "' and type='table'";
            Cursor c = db.rawQuery(query, null);
            if (c.getCount() != 1) {
                c.close();
                Log.d(constants.zeeley, "table never creatd");
                throw new Exception("Table not created");
            }

            Cursor cursor = db.rawQuery("SELECT * FROM table_" + pk, null);
            Log.d(constants.zeeley, String.valueOf(cursor.getCount()));
            //rawQuery(query, null);
            if (cursor.getCount() == 0) {
                cursor.close();
                Log.d(constants.zeeley, "cursor problem ");
                throw new Exception("Nothing present in table");
            }

            ChatMessage chatMessage;
            cursor.moveToFirst();
            String d_old = null, d;
            for (int i = 0; i < cursor.getCount(); i++) {
                if (i == 0) {
                    d_old = CommonMethods.dateFormat.format(chatactivity.parseDate(cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_TIME))));
                    Log.d(constants.zeeley, "d_old :" + d_old);
                    ChatMessage message = new ChatMessage(null, null);
                    message.setIsDate(true);
                    message.setDate(d_old);
                    messages.add(message);
//                Log.d(constants.zeeley, message.getMessage());
                }
                chatMessage = new ChatMessage(cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_STRING)), (cursor.getInt(cursor.getColumnIndex(COLUMN_IS_MINE)) == 1));
                chatMessage.setIsDate(false);
                chatMessage.setTime(CommonMethods.format.format(chatactivity.parseDate(cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_TIME)))));
                d = CommonMethods.dateFormat.format(chatactivity.parseDate(cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_TIME))));
                chatMessage.setDate(d);
                if (!d.equalsIgnoreCase(d_old)) {
                    ChatMessage message = new ChatMessage(null, null);
                    message.setIsDate(true);
                    message.setDate(d);
                    messages.add(message);
                    d_old = d;

                }
                messages.add(chatMessage);
                cursor.moveToNext();
                Log.d(constants.zeeley, "message: " + chatMessage.getMessage());

            }
            cursor.close();
        } catch (Exception e) {
            Log.d("zeeley", "Could not retieve messages from database " + e.getMessage());
        }

        return messages;
    }

   /* public void setupNotific(SQLiteDatabase database) {

        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.userimg);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, bos);
        img = bos.toByteArray();

        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, "Jay");
        values.put(COLUMN_LAST_NAME, "Patel");
        values.put(contact_no, "7860465010");
        values.put(Latitude, "125.2");
        values.put(Longitude, "153.2");
        values.put(COLUMN_PROF_PIC, img);
        values.put(COLUMN_CURRENT_INTEREST, "Pizza");
        values.put(COLUMN_GENDER, "Male");
        values.put(Settings, "000000");
        values.put(COLUMN_AGE, "21");
        values.put(UserId, 3);
        database.insert(notific_table, null, values);
        values.put(COLUMN_FIRST_NAME, "Babdi");
        values.put(COLUMN_LAST_NAME, "Saga");
        values.put(contact_no, "7860465010");
        values.put(Latitude, "74");
        values.put(Longitude, "153.2");
        values.put(COLUMN_PROF_PIC, img);
        values.put(COLUMN_CURRENT_INTEREST, "Dating");
        values.put(COLUMN_GENDER, "Male");
        values.put(Settings, "000000");
        values.put(COLUMN_AGE, "21");
        values.put(UserId, 7);
        database.insert(notific_table, null, values);
        values.put(COLUMN_FIRST_NAME, "Gaurav");
        values.put(COLUMN_LAST_NAME, "Ranjan");
        values.put(contact_no, "7860465010");
        values.put(Latitude, "52.2");
        values.put(Longitude, "153.2");
        values.put(COLUMN_PROF_PIC, img);
        values.put(COLUMN_CURRENT_INTEREST, "Cricket");
        values.put(COLUMN_GENDER, "Male");
        values.put(Settings, "000000");
        values.put(COLUMN_AGE, "21");
        values.put(UserId, 5);
        database.insert(notific_table, null, values);
    }*/

    public void setupTable(SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(interest, "Pizza");
        values.put(image, R.drawable.pizza);
        database.insert(TABLE, null, values);
        values.put(interest, "Cricket");
        values.put(image, R.drawable.cricket);
        database.insert(TABLE, null, values);
        values.put(interest, "Taxi");
        values.put(image, R.drawable.taxi);
        database.insert(TABLE, null, values);
        values.put(interest, "Movie");
        values.put(image, R.drawable.movie);
        database.insert(TABLE, null, values);
        values.put(interest, "Restaurant");
        values.put(image, R.drawable.restaurant);
        database.insert(TABLE, null, values);
        values.put(interest, "Dating");
        values.put(image, R.drawable.dating);
        database.insert(TABLE, null, values);
        values.put(interest, "Basketball");
        values.put(image, R.drawable.basketball);
        database.insert(TABLE, null, values);
        values.put(interest, "Study");
        values.put(image, R.drawable.study);
        database.insert(TABLE, null, values);
        values.put(interest, "Add more");
        values.put(image, R.drawable.twitter);
        database.insert(TABLE, null, values);
    }

    public void addintoNotific(profileObj obj, String teamId, String teamName, Bitmap bitmap, boolean isForm) {


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        img = bos.toByteArray();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROF_PIC, img);
        values.put(TeamId, teamId);
        values.put(TeamName, teamName);
        values.put(isFormInvitation, isForm ? 1 : 0);
        values.put(UserId, Integer.parseInt(obj.getPk()));
        values.put(COLUMN_FIRST_NAME, obj.getFirst_name());
        values.put(COLUMN_LAST_NAME, obj.getLast_name());
        values.put(contact_no, obj.getContactNo());
        values.put(Latitude, obj.getLat());
        values.put(Longitude, obj.getLongit());
        values.put(COLUMN_CURRENT_INTEREST, obj.getCurrentInterest());
        values.put(COLUMN_GENDER, obj.getGender());
        values.put(Settings, obj.getSettings().getSettingsString());
        values.put(COLUMN_AGE, obj.getAge());
        db.insert(notific_table, null, values);
    }


    public void createTable(String pk) {
        Log.d("zeeley", " creating table" + pk);
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS table_" + pk + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CHAT_STRING + " TEXT," +
                COLUMN_CHAT_TIME + " TEXT," +
                COLUMN_IS_PHOTO + " INTEGER," +
                COLUMN_IS_MINE + " INTEGER" + ");";


       /* String CREATE_TABLE_QUERY = "CREATE TABLE " + pk + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_CHAT_STRING + " TEXT NOT NULL, " +
                COLUMN_CHAT_TIME + " TEXT NOT NULL, "   + COLUMN_IS_MINE + " INTEGER NOT NULL);";*/
        db.execSQL(CREATE_TABLE_QUERY);
        db.close();
    }

    public void createGroupChatTable(String teamID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_TABLE_QUERY = "CREATE TABLE team_" + teamID + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_FIRST_NAME + " TEXT," +
                COLUMN_CHAT_STRING + " TEXT," +
                COLUMN_IS_MINE + " INTEGER," +
                COLUMN_CHAT_DATE + " TEXT," +
                COLUMN_IS_PHOTO + " INTEGER," +
                COLUMN_CHAT_TIME + " TEXT" + ");";

        db.execSQL(CREATE_TABLE_QUERY);
        db.close();
    }

    public void addgroupParticipants(ArrayList<groupParticipant> gps) {
        // called in mylogin1
        String gpId = gps.get(0).getGroupId();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM sqlite_master WHERE name ='groupTable_" + gpId + "' and type='table'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() != 1 && cursor.getCount() == 0) {
            createGrpMemTable(gpId);
            Log.d("zeeley", gpId + " table created because it did not exist");
        }
        cursor.close();
        SQLiteDatabase s = this.getWritableDatabase();
        for (groupParticipant gp : gps) {
            ContentValues values = new ContentValues();
            values.put(Name, gp.getName());
            values.put(Settings, gp.getSettings());
            values.put(UserId, gp.getPk());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Bitmap bitmap = gp.getImage();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] img = bos.toByteArray();
            values.put(Profpic, img);
            s.insert(GroupTable + gp.getGroupId(), null, values);
        }
    }

    public ArrayList<GroupChatMessage> getGroupChat(String teamid) {
        Log.d(constants.zeeley, "get grp chat cald in database");
        ArrayList<GroupChatMessage> messages = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT * FROM sqlite_master WHERE name ='team_" + teamid + "' and type='table'";
            Cursor c = db.rawQuery(query, null);
            if (c.getCount() != 1) {
                c.close();
                Log.d(constants.zeeley, "grp chat table with id " + teamid + " dosnt exist");

                throw new Exception("team_" + teamid + " does not exist");
            }

            Cursor cursor = db.rawQuery("SELECT * FROM team_" + teamid, null);
            Log.d(constants.zeeley, " no of msgs in grpchat table are " + cursor.getCount() + "");
            //rawQuery(query, null);


            GroupChatMessage chatMessage;
            Log.d("zeeley", String.valueOf(cursor.getCount()) + " total messages");
            cursor.moveToFirst();
            String d_old = null, d;
            for (int i = 0; i < cursor.getCount(); i++) {
                if (i == 0) {
                    // d_old = CommonMethods.dateFormat.format(chatactivity.parseDate(cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_TIME))));
                    d_old = cursor.getString(cursor.getColumnIndex(database.COLUMN_CHAT_DATE));
                    GroupChatMessage message = new GroupChatMessage();
                    message.setIsDate(true);
                    message.setDate(d_old);
                    messages.add(message);
                }
                Log.d("zeeley", cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_TIME)));
                // d = CommonMethods.dateFormat.format(chatactivity.parseDate(cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_TIME))));
                d = cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_DATE));
                chatMessage = new GroupChatMessage(teamid,
                        cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_STRING)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_TIME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_DATE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_NAME)),
                        (cursor.getInt(cursor.getColumnIndex(COLUMN_IS_MINE)) == 1));
                chatMessage.setIsDate(false);
                chatMessage.setIsImage(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_PHOTO)) == 1);

                if (!d.equalsIgnoreCase(d_old)) {
                    GroupChatMessage message = new GroupChatMessage();
                    message.setIsDate(true);
                    message.setDate(d);
                    messages.add(message);
                    d_old = d;

                }
                messages.add(chatMessage);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(constants.zeeley, "excep in getgrpchat in db " + e.getMessage());
            Log.d("zeeley", "Could not output data");
        }


        return messages;
    }

    public void addGroupMessage(ArrayList<GroupChatMessage> messages, String teamid) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT * FROM sqlite_master WHERE name ='team_" + teamid + "' and type='table'";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() != 1 && cursor.getCount() == 0) {
                createGroupChatTable(teamid);
                Log.d("zeeley", teamid + " group table created because not existent");
            }
            cursor.close();

            for (GroupChatMessage chat : messages) {
                Log.d("zeeley", chat.getMessage());
                ContentValues values = new ContentValues();
                values.put(COLUMN_FIRST_NAME, chat.getName());
                values.put(COLUMN_CHAT_STRING, chat.getMessage());
                values.put(COLUMN_IS_PHOTO, chat.isImage() ? 1 : 0);
                values.put(COLUMN_IS_MINE, chat.isMine() ? 1 : 0);
                //values.put(COLUMN_CHAT_TIME, chat.getUnparsedTime());
                values.put(COLUMN_CHAT_DATE, chat.getDate());
                values.put(COLUMN_CHAT_TIME, chat.getTime());
                db.insert("team_" + teamid, null, values);
            }
            Log.d("zeeley", "messages added");
            db.close();
        } catch (Exception r) {
            Log.d("zeeley", "Could not add list of groupmessage: " + r.getMessage());
        }
    }

    public void addGroupMessage(GroupChatMessage chat, String teamid) {
        Log.d(constants.zeeley, " inside addgm in db");
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT * FROM sqlite_master WHERE name ='team_" + teamid + "' and type='table'";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() == 0) {
                createGroupChatTable(teamid);
                Log.d("zeeley", teamid + " table created");
            }
            cursor.close();
            ContentValues values = new ContentValues();
            if (chat.isMine) {
                values.put(COLUMN_FIRST_NAME, "");
            } else {
                values.put(COLUMN_FIRST_NAME, chat.getName());
            }

            values.put(COLUMN_CHAT_STRING, chat.getMessage());
            values.put(COLUMN_IS_PHOTO, chat.isImage() ? 1 : 0);
            values.put(COLUMN_IS_MINE, chat.isMine() ? 1 : 0);
            values.put(COLUMN_CHAT_TIME, chat.getTime());
            values.put(COLUMN_CHAT_DATE, chat.getDate());


            db.insert("team_" + teamid, null, values);
            db.close();
            Log.d(constants.zeeley, " msg aded in db");
        } catch (Exception e) {
            Log.d("zeeley", "Could not add message reason: " + e.getMessage());
        }
    }


    public void deleteGroupChat(String teamID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS team_" + teamID);
        db.close();
    }

    /*public void addUser(UserInfo user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRIMARY_KEY, user.getPrimaryKey() + "");
        values.put(COLUMN_FIRST_NAME, user.getFirst_name());
        values.put(COLUMN_LAST_NAME, user.getLast_name());
        values.put(COLUMN_CURRENT_INTEREST, user.getInterest());
        //Bitmap bitmap = user.getImage();

        //ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);


        //values.put(COLUMN_PROF_PIC, bos.toByteArray());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_USERS, null, values);
        db.close();
    }*/
    public void addChatUser(ChatMessage map, String pk) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT * FROM sqlite_master WHERE name ='table_" + pk + "' and type='table'";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() != 1 && cursor.getCount() == 0) {
                createTable(pk);
                Log.d("zeeley", pk + " table created because it did not exist");
            }
            cursor.close();
            query = "SELECT " + COLUMN_ID + " FROM table_" + pk;
            cursor = db.rawQuery(query, null);
            if (cursor.getCount() > 15) {
                cursor.moveToFirst();
                String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
                db.delete("table_" + pk, COLUMN_ID + " = ?", new String[]{id});
            }
            cursor.close();
            ContentValues values = new ContentValues();
            values.put(COLUMN_IS_MINE, map.isMine() ? 1 : 0);
            values.put(COLUMN_IS_PHOTO, map.isImage() ? 1 : 0);
            values.put(COLUMN_CHAT_STRING, map.getMessage());
            values.put(COLUMN_CHAT_TIME, map.getDate());
            db.insert("table_" + pk, null, values);
            db.close();
        } catch (Exception e) {
            Log.d("zeeley", "Something went wrong while inserting data in table: " + e.getMessage());
        }
    }

    public void addChatUser(ArrayList<ChatMessage> messages, String pk) {
        try {
            Log.d("zeeley", "databse method " + pk + " called");
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT * FROM sqlite_master WHERE name ='table_" + pk + "' and type='table'";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() != 1 && cursor.getCount() == 0) {
                createTable(pk);
                Log.d("zeeley", pk + " table created");
            }
            cursor.close();

            for (ChatMessage map : messages) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_IS_MINE, map.isMine() ? 1 : 0);
                values.put(COLUMN_IS_PHOTO, map.isImage() ? 1 : 0);
                values.put(COLUMN_CHAT_STRING, map.getMessage());
                Log.d("zeeley", map.getMessage() + " --> message added to database");
                values.put(COLUMN_CHAT_TIME, map.getDate());
                db.insert("table_" + pk, null, values);
            }
            Log.d("zeeley", "messages added");
            db.close();
        } catch (Exception e) {
            Log.d("zeeley", "not able to add messages to table " + e.getMessage());
        }
    }

    public void removeChatUser(String pk) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP TABLE IF EXISTS table_" + pk;
        db.execSQL(query);
        db.close();
    }

    /*public Iterator getAllPreviousUsers() {
        Stack<UserInfo> stack = new Stack<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS;
        Cursor cursor = db.rawQuery(query, null);
        UserInfo info = new UserInfo();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                info.setFirst_name(cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_NAME)));
                info.setLast_name(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_NAME)));

                info.setInterest(cursor.getString(cursor.getColumnIndex(COLUMN_CURRENT_INTEREST)));
                info.setPrimaryKey(cursor.getString(cursor.getColumnIndex(COLUMN_PRIMARY_KEY)));
                stack.push(info);
            }
        }
        db.close();
        return stack.iterator();
    }*/


}
