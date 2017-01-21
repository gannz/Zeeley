package com.zeeley;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

public class groupChatScreen extends AppCompatActivity {
    static int i = 1;

    public ArrayList<GroupChatMessage> groupChatMessages = new ArrayList<>();// msgs obtained from db
    ProgressDialog progressDialog;
    View rootView;
    ListView msgListView;
    ImageView emojiButton;
    ImageView submitButton;
    static int j = 0;
    private boolean messageKhatam = false;
    int itemsCount;
    ArrayList<String> messages;
    String pk;
    myadapter grpAdapter;
    EmojiconEditText emojiconEditText;
    SearchView searchView;
    BroadcastReceiver receiver;
    LocalBroadcastManager manager;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    View header;
    final int finishUrself = 71;
    LayoutInflater inflater;
    dummyGroupProfile dummygp;
    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_chat_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Bundle b = getIntent().getBundleExtra(constants.BUNDLE);
        dummygp = Parcels.unwrap(b.getParcelable(constants.Dummy));
        pk = dummygp.getId();
        manager = LocalBroadcastManager.getInstance(this);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
        /*sharedPreferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(pk, false);
        editor.apply();*/
        inflater = getLayoutInflater();
        progressDialog = new ProgressDialog(this);
        header = (View) inflater.inflate(R.layout.header, msgListView,
                false);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("zeeley", "header clicked");
                //todo load group chat
                if(!constants.isNetworkAvailable(groupChatScreen.this)){
                    Toast.makeText(groupChatScreen.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
                else {
                    loadmoremessages();
                }

            }
        });
        emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        rootView = findViewById(R.id.root_view);
        emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        submitButton = (ImageView) findViewById(R.id.submit_btn);
        msgListView = (ListView) findViewById(R.id.lv);
        // bundle

        TextView toolbarTitle = (TextView) findViewById(R.id.actionbar_title);
        ImageView imageView = (ImageView) findViewById(R.id.actionbar_icon);
        imageView.setImageResource(R.drawable.userimg);
        //// TODO: 14-09-2016 getmsgs frm database
        ArrayList<GroupChatMessage> msgs = new database(this).getGroupChat(pk);
        if (msgs != null) {
            groupChatMessages.addAll(msgs);
            if (msgs.size() < 15) {
                messageKhatam = true;
            } else {
                messageKhatam = false;
                msgListView.addHeaderView(header);
            }
        }
        // Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        grpAdapter = new myadapter(this, groupChatMessages);
        msgListView.setAdapter(grpAdapter);

        //getChat();

        if (dummygp != null) {
            toolbarTitle.setText(dummygp.getName());
            toolbarTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(groupChatScreen.this, groupProfile.class);
                    dummyGroupProfile gp = new dummyGroupProfile(R.id.userimage, dummygp.getName(), dummygp.getId());
                    intent.putExtra(constants.Dummy, Parcels.wrap(gp));
                    startActivity(intent);
                }
            });

        }


        msgListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        msgListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle(msgListView.getCheckedItemCount() + " ");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.action_mode_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        itemsCount = msgListView.getCheckedItemCount();
                        new MaterialDialog.Builder(groupChatScreen.this)
                                .title("Delete " + msgListView.getCheckedItemCount() + " messages?")
                                .positiveText("ok")
                                .negativeText("cancel")
                                .autoDismiss(true)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        mode.finish();
                                        dialog.dismiss();
                                        delete(msgListView.getCheckedItemPositions());
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        break;
                    case R.id.copy:
                        itemsCount = msgListView.getCheckedItemCount();
                        mode.finish();
                        copy(msgListView.getCheckedItemPositions());
                        break;
                    case R.id.forward:
                        itemsCount = msgListView.getCheckedItemCount();
                        forward(msgListView.getCheckedItemPositions());
                        break;
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        // ----Set autoscroll of listview when a new ChatMessage arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);
        //--------------------------------
        // getConversation();
        ///----------------------------
        emojiSetup();
        String source = b.getString(constants.SOURCE);
        if (source != null) {
            if (source.equals(constants.FROM_FORWARD)) {
                messages = b.getStringArrayList(constants.FORWARD_MSGSLIST);
                for (String s : messages) {
                    sendTextMessage(s);
                }
            }
        }
        filter = new IntentFilter(constants.Message_Action);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(constants.Message_Action)) {

                    GroupChatMessage message = Parcels.unwrap(intent.getParcelableExtra(constants.New_Message));
                    grpAdapter.add(message);
                    Log.d(constants.zeeley, "calng addgm in db to save receivd msg");
                    new database(groupChatScreen.this).addGroupMessage(message, pk);
                }
            }
        };

    }

    public boolean isNetAvailable() {
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    @Override
    protected void onResume() {
        constants.Current_Id = Integer.parseInt(pk);
        manager.registerReceiver(receiver, filter);
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatactivity_menu, menu);

       /* searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.profile_menu:
                Intent intent = new Intent(groupChatScreen.this, groupProfile.class);
                dummyGroupProfile gp = new dummyGroupProfile(R.id.userimage, dummygp.getName(), dummygp.getId());
                intent.putExtra(constants.Dummy, Parcels.wrap(gp));
                startActivity(intent);
                return true;
            case R.id.clearchat:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete Conversation?");
                builder.setMessage("This deletes the entire conversation.");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        grpAdapter.deleteAll();
                    }
                });
                builder.setCancelable(true);
                builder.create();
                builder.show();
                return true;
           /* case R.id.search:
                searchView.onActionViewExpanded();*/
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendTextMessage(String message) {

        if (message.trim().length() > 0) {
            Log.d(constants.zeeley, "insid if of stm");
            final GroupChatMessage groupmessage = new GroupChatMessage(pk, message, CommonMethods.getCurrentTime(),
                    CommonMethods.getCurrentDate(), null, true);
            emojiconEditText.setText("");
            chats_updater.updateGrpList(this, dummygp.getId(), null, message, CommonMethods.getCurrentDate());
            if(groupChatMessages.isEmpty()){
                GroupChatMessage g=new GroupChatMessage();
                g.setIsDate(true);
                g.setDate(CommonMethods.getCurrentDate());
                grpAdapter.add(g);
                grpAdapter.add(groupmessage);
                return;
            }
            GroupChatMessage gcm=groupChatMessages.get(groupChatMessages.size()-1);
            if(!gcm.getDate().equals(CommonMethods.getCurrentDate()))
            {
                GroupChatMessage g=new GroupChatMessage();
                g.setIsDate(true);
                g.setDate(CommonMethods.getCurrentDate());
                grpAdapter.add(g);
            }
            grpAdapter.add(groupmessage);

            //// TODO: 14-09-2016 call chatsupdater
            // chats_updater.updatelist(this, dummy, -1, message, true);
            //// TODO: 14-09-2016 send team msg to server and insert in db
            //sendMessage(message, pk, this);
            if(!constants.isNetworkAvailable(groupChatScreen.this)){
                Toast.makeText(groupChatScreen.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
            }
            else {
                sendMessage(pk, message, this);
            }

//  send msg to server and insert data into database.

        }

    }

    @Override
    protected void onPause() {
        // chats_updater.updateChats();
        manager.unregisterReceiver(receiver);
        constants.Current_Id = -1;
        super.onPause();
    }


    public void delete(SparseBooleanArray array) {
        progressDialog.setMessage("Deleting Messages");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        for (int i = 0; i < array.size(); i++) {
            if (array.valueAt(i)) {
                GroupChatMessage chatMessage = (GroupChatMessage) (msgListView.getItemAtPosition(i));
                //
                // long id=chatMessage.getId();
                //remove this id from database...
                grpAdapter.groupMessageList.remove(i);
            }
        }
        grpAdapter.notifyDataSetChanged();
        progressDialog.dismiss();
        Toast.makeText(groupChatScreen.this, itemsCount + " messages deleted", Toast.LENGTH_SHORT).show();
    }

    public void copy(SparseBooleanArray array) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            if (array.valueAt(i)) {
                ChatMessage chatMessage = (ChatMessage) (msgListView.getItemAtPosition(i));
                String dateAndTime = "[" + chatMessage.getDate() + " " + chatMessage.getTime() + "]";
                if (chatMessage.isMine()) {
                    String msg = dateAndTime + " Ganesh:" + chatMessage.getMessage() + "\n";
                    stringBuilder.append(msg);
                } else {
                    String msg = dateAndTime + " " + dummygp.getName() + ":" + chatMessage.getMessage() + "\n";
                    stringBuilder.append(msg);
                }
            }
        }
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("copiedMsgs", stringBuilder.toString());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(groupChatScreen.this, itemsCount + " messages copied", Toast.LENGTH_SHORT).show();
    }

    public void forward(SparseBooleanArray array) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            if (array.valueAt(i)) {
                ChatMessage chatMessage = (ChatMessage) (msgListView.getItemAtPosition(i));
                list.add(chatMessage.getMessage());
            }
        }
        Intent i = new Intent(this, forward.class);
        Bundle bundle = new Bundle();
        bundle.putString(constants.SOURCE, constants.FROM_FORWARD);
        bundle.putStringArrayList(constants.FORWARD_MSGSLIST, list);
        i.putExtra(constants.BUNDLE, bundle);
        startActivityForResult(i, finishUrself);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == finishUrself) {
            finish();
        }
    }

    private void emojiSetup() {

        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);

        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (emojiconEditText == null || emojicon == null) {
                    return;
                }

                int start = emojiconEditText.getSelectionStart();
                int end = emojiconEditText.getSelectionEnd();
                if (start < 0) {
                    emojiconEditText.append(emojicon.getEmoji());
                } else {
                    emojiconEditText.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                emojiconEditText.dispatchKeyEvent(event);
            }
        });

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        emojiButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (!popup.isShowing()) {

                    //If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        emojiconEditText.setFocusableInTouchMode(true);
                        emojiconEditText.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });

        //On submit, add the edittext text to listview and clear the edittext
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String message = emojiconEditText.getEditableText().toString();

                sendTextMessage(message);


            }
        });
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }

   /* private void putDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        GroupChatMessage message = new GroupChatMessage(dateFormat.format(date), null, null, null);
        groupChatMessages.add(message);
    }*/


    private void loadmoremessages() {
        j++;
        new loadMoreGroupMessages().execute(j);
    }

    public static Date parseDate(String d) {
        String date = d.substring(0, d.indexOf('T'));
        String time = d.substring(d.indexOf('T') + 1, d.indexOf('Z'));

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date dt;

        try {
            dt = df.parse(date + " " + time);
            return dt;
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return null;
    }


    public static String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }


    private void sendMessage(String teamID, final String message, Context context) {
        new AsyncTask<String, Void, Void>() {
            boolean messageSent = false;

            @Override
            protected Void doInBackground(String... params) {

                try {
                    URL url = new URL("http://zeeley.com/android_send_team_message/?sluge=" + URLEncoder.encode(params[0], "utf-8") + "&slug=" + params[1]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setInstanceFollowRedirects(true);
                    connection.setRequestProperty("Charset", "utf-8");
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        messageSent = true;
                        Log.d("zeeley", "grpmessage sent");
                    } else {
                        Log.d("zeeley", "grpmessage not sent");

                    }
                } catch (Exception e) {
                    Log.d("zeeley", "message not sent: " + e.getMessage());
                }
                return null;
            }

            protected void onPostExecute(Void aVoid) {
                if (messageSent) {
                    GroupChatMessage gcm = new GroupChatMessage(pk, message, CommonMethods.getCurrentTime(), CommonMethods.getCurrentDate(), null, true);
                    new database(groupChatScreen.this).addGroupMessage(gcm, pk);
                }

            }
        }.execute(teamID, message);
    }

    public static String createDateString(Date d) {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss.SSS");
        return date.format(d) + 'T' + time.format(d) + 'Z';
    }

    public class myadapter extends BaseAdapter {

        private LayoutInflater inflater = null;
        ArrayList<GroupChatMessage> groupMessageList;
        private static final int MyMessage = 0;
        private static final int DATE = 1;
        private static final int IMAGE = 2;
        private static final int Sender_Msg = 3;

        public myadapter(Context context, ArrayList<GroupChatMessage> list) {
            groupMessageList = list;
            inflater = LayoutInflater.from(context);

        }

        @Override
        public int getViewTypeCount() {
            // Log.d(constants.zeeley, "inside getViewTypeCount");
            return 4;
        }

        @Override
        public boolean isEnabled(int position) {
            Log.d(constants.zeeley, "inside isEnabled " + position);
            GroupChatMessage message = (GroupChatMessage) getItem(position);
            if (message.isDate())
                return false;
            else
                return true;
        }

        @Override
        public int getItemViewType(int position) {
            //Log.d(constants.zeeley, "inside getItemViewType " + position);

            GroupChatMessage gcm = (GroupChatMessage) getItem(position);
            if (gcm.isDate())
                return DATE;
            else if (gcm.isImage())
                return IMAGE;
            else if (gcm.isMine())
                return MyMessage;
            else return Sender_Msg;

        }

        public void deleteAll() {
            if (groupMessageList != null && groupMessageList.size() > 0) {
                groupMessageList.clear();
                notifyDataSetChanged();
                /// also delete conversation from database..
            }
        }

        @Override
        public int getCount() {
            return groupMessageList.size();
        }

        @Override
        public Object getItem(int position) {
            return groupMessageList.get(position);

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Log.d(constants.zeeley, "inside getView " + position);
            GroupChatMessage message = (GroupChatMessage) getItem(position);
            int type = getItemViewType(position);

            if (convertView == null) {
                switch (type) {
                    case IMAGE:
                        Log.d(constants.zeeley, "msg is image cv");
                        convertView = inflater.inflate(R.layout.img_msg, parent, false);
                        imgHolder holder = new imgHolder(convertView);
                        convertView.setTag(holder);
                        break;
                    case DATE:
                        convertView = inflater.inflate(R.layout.date, parent, false);
                        dateHolder datehld = new dateHolder(convertView);
                        convertView.setTag(datehld);
                        break;
                    case MyMessage:

                        Log.d(constants.zeeley, "my msg seting vh");
                        convertView = inflater.inflate(R.layout.message, parent, false);
                        viewholder vh = new viewholder(convertView);
                        convertView.setTag(vh);

                        break;

                    case Sender_Msg:
                        Log.d(constants.zeeley, " not my msg seting sh");
                        convertView = inflater.inflate(R.layout.group_message, parent, false);
                        senderMsgholder smh = new senderMsgholder(convertView);
                        convertView.setTag(smh);
                        break;
                }
            } else {
                if (convertView.getTag() instanceof viewholder) {
                    Log.d(constants.zeeley, "cv not null its vh..my msg");
                } else if (convertView.getTag() instanceof senderMsgholder) {
                    Log.d(constants.zeeley, "cv not null its sh..not my msg");
                } else if (convertView.getTag() instanceof dateHolder) {
                    Log.d(constants.zeeley, "cv not null its dh..");
                } else if (convertView.getTag() instanceof imgHolder) {
                    Log.d(constants.zeeley, "cv not null its ih..");
                }
            }
            switch (type) {
                case IMAGE:
                    imgHolder ih = (imgHolder) convertView.getTag();
                    ih.setData(message.getImage(), message.getTime());
                    if (message.isMine()) {
                        ih.parent_layout.setGravity(Gravity.END);
                        ih.parent_layout.setPadding(100, 0, 0, 0);
                    } else {
                        ih.parent_layout.setGravity(Gravity.START);
                        ih.parent_layout.setPadding(0, 0, 100, 0);
                    }


                    break;
                case MyMessage:

                    Log.d(constants.zeeley, "my msg geting vh");
                    viewholder vh = (viewholder) convertView.getTag();
                    vh.setData(message.getMessage(), message.getTime());
                    vh.parent_layout.setPadding(100, 0, 0, 0);

                    break;
                case Sender_Msg:


                    Log.d(constants.zeeley, " not my msg geting sh");
                    senderMsgholder smh = (senderMsgholder) convertView.getTag();
                    smh.setData(message.getMessage(), message.getTime(), message.getName());


                    break;
                case DATE:
                    dateHolder dh = (dateHolder) convertView.getTag();
                    dh.date.setText(message.getDate());
                    break;

            }
           /* viewholder holder = (viewholder) convertView.getTag();
            if (message.isMine() == null&&message.isDate()) {

                holder.senderName.setText(message.getDate());
                holder.senderName.setTextColor(getColor(R.color.senderName));

            } else if (message.isMine()&&!message.isDate()) {

                holder.senderName.setText(message.getMessage());
                //holder.date.setText(CommonMethods.format.format(chatactivity.parseDate(message.getTime())));
                holder.date.setText(message.getTime());
                holder.layout.setBackgroundResource(R.drawable.bubble2);
                holder.parent_layout.setGravity(Gravity.END);
            }
            // If not mine then align to left
            else if(!message.isMine&&!message.isDate()) {
                holder.senderName.setText(message.getName());
                holder.layout.setBackgroundResource(R.drawable.bubble1);
                holder.parent_layout.setGravity(Gravity.START);
                holder.msg.setText(message.getName());
                holder.date.setText(message.getTime());
            }*/

            return convertView;
        }


        public void add(GroupChatMessage object) {
            Log.d(constants.zeeley, " insid add of adaptr");
            groupMessageList.add(object);
            notifyDataSetChanged();
        }

        private class viewholder {
            TextView msg, date;
            LinearLayout layout, parent_layout;

            public viewholder(View view) {
                msg = (TextView) view.findViewById(R.id.message_text);
                date = (TextView) view.findViewById(R.id.date);
                layout = (LinearLayout) view
                        .findViewById(R.id.bubble_layout);
                parent_layout = (LinearLayout) view
                        .findViewById(R.id.bubble_layout_parent);

            }

            public void setData(String message, String date) {
                msg.setText(message);
                this.date.setText(date);
                layout.setBackgroundResource(R.drawable.bubble2);
                parent_layout.setGravity(Gravity.END);
            }

        }

        private class imgHolder {
            ImageView imageView;
            TextView date, title;
            LinearLayout layout, parent_layout;

            public imgHolder(View view) {
                imageView = (ImageView) view.findViewById(R.id.image);
                date = (TextView) view.findViewById(R.id.date);
                title = (TextView) view.findViewById(R.id.title);
                layout = (LinearLayout) view
                        .findViewById(R.id.bubble_layout);
                parent_layout = (LinearLayout) view
                        .findViewById(R.id.bubble_layout_parent);
            }

            public void setData(Bitmap img, String date) {
                imageView.setImageBitmap(img);
                this.date.setText(date);
            }
        }

        private class senderMsgholder {
            TextView msg, date, senderName;
            LinearLayout layout, parent_layout;

            public senderMsgholder(View view) {
                msg = (TextView) view.findViewById(R.id.message_text);
                date = (TextView) view.findViewById(R.id.time);
                layout = (LinearLayout) view
                        .findViewById(R.id.bubble_layout);
                parent_layout = (LinearLayout) view
                        .findViewById(R.id.bubble_layout_parent);
                senderName = (TextView) view.findViewById(R.id.senderName);
            }

            public void setData(String message, String date, String senderName) {
                msg.setText(message);
                this.senderName.setText(senderName);
                this.date.setText(date);
                layout.setBackgroundResource(R.drawable.bubble1);
                parent_layout.setGravity(Gravity.START);
                parent_layout.setPadding(0, 0, 100, 0);
            }
        }

        private class dateHolder {
            TextView date;
            LinearLayout layout, parent_layout;

            public dateHolder(View view) {
                date = (TextView) view.findViewById(R.id.date);
                parent_layout = (LinearLayout) view
                        .findViewById(R.id.bubble_layout_parent);
                parent_layout.setGravity(Gravity.CENTER);
            }
        }


    }


    private class loadMoreGroupMessages extends AsyncTask<Integer, Void, Void> {
        private ArrayList<GroupChatMessage> list = null;


        @Override
        protected Void doInBackground(Integer... params) {
            int a = params[0] * 10 + 5;
            int b = a + 10;
            try {
                HashMap<String, String> names = new HashMap<>();
                URL url = new URL("http://zeeley.com/team_info/?team_id=" + pk);
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
                for (int i = 0; i < array.length(); i++) {
                    parameters = array.getJSONObject(i);
                    fields = parameters.getJSONObject("fields");
                    String name = fields.getString("first_name");
                    String p_k = parameters.getInt("pk") + "";
                    names.put(p_k, name);
                }
                url = new URL("http://zeeley.com/scroll_team_msgs/?a=" + a + "&b=" + b + "&f=" + pk);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);

                r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                for (String s; (s = r.readLine()) != null; ) {
                    sb.append(s);
                }
                list = new ArrayList<>();
                array = new JSONArray(sb.toString());
                if (array.length() < 10)
                    messageKhatam = true;
                String dOld = "", dCurrent;
                for (int i = 0; i < array.length(); i++) {
                    parameters = array.getJSONObject(i);
                    fields = parameters.getJSONObject("fields");
                    String post_time = fields.getString("post_time");
                    String id = fields.getInt("teamc_id") + "";
                    //// TODO: 08-11-2016 ask mohan to send sender name
                    String message = fields.getString("teamc_message");
                    String team_id = fields.getInt("teamc_group") + "";
                    Date date = chatactivity.parseDate(post_time);
                    if (i == 0) {
                        dOld = CommonMethods.dateFormat.format(date);
                        GroupChatMessage gcm = new GroupChatMessage();
                        gcm.setIsDate(true);
                        gcm.setDate(dOld);
                        list.add(gcm);
                    }
                    dCurrent = CommonMethods.dateFormat.format(date);
                    if (!dOld.equals(dCurrent)) {
                        dOld = dCurrent;
                        GroupChatMessage gcm = new GroupChatMessage();
                        gcm.setIsDate(true);
                        gcm.setDate(dCurrent);
                        list.add(gcm);
                    }
                    GroupChatMessage gcm = new GroupChatMessage(team_id, message, CommonMethods.format.format(date), CommonMethods.dateFormat.format(date),
                            names.get(id), Integer.parseInt(id) == Integer.parseInt(pk));
                    gcm.setIsDate(false);
                    list.add(gcm);
                }
            } catch (Exception e) {
                Log.d("zeeley", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (list != null) {
                for (int i = list.size() - 1; i >= 0; i--) {
                    GroupChatMessage message = list.get(i);
                    if (message.isDate()) {
                        GroupChatMessage gm = groupChatMessages.get(0);
                        if (gm.isDate()) {
                            if (message.getDate().equals(gm.getDate())) {
                                groupChatMessages.remove(0);
                            }
                        }
                        break;
                    }
                }
                if (messageKhatam)
                    msgListView.removeHeaderView(header);
                groupChatMessages.addAll(0, list);
                grpAdapter.notifyDataSetChanged();
                msgListView.smoothScrollToPositionFromTop(list.size(), 200, 500);
            }
        }
    }
}
