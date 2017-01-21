package com.zeeley;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
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
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class chatactivity extends AppCompatActivity {
    // onlinedummy dummy;
    static int i = 1;
    public static final String ACTIONBAR_TITLE = "NAME";
    public static final String PROFILE_PIC = "PROFILEPIC";
    final int finishUrself = 71;
    public ArrayList<ChatMessage> chatlist = new ArrayList<>();// msgs obtained from db
    public fragment_adapter adapter;
    ProgressDialog progressDialog;
    View rootView;
    ListView msgListView;
    ImageView emojiButton;
    ImageView submitButton;
    private boolean messageKhatam = false;
    int itemsCount;
    ArrayList<String> messages;
    String pk, lastDate = constants.DEFAULT;
    private settingsObj settings;
    EmojiconEditText emojiconEditText;
    SearchView searchView;
    moreChats loadChats;
    BroadcastReceiver receiver;
    LocalBroadcastManager manager;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    View header, footer;
    LayoutInflater inflater;
    dummyProfile dummy;
    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        manager = LocalBroadcastManager.getInstance(this);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
        sharedPreferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(pk, false);
        editor.apply();
        inflater = getLayoutInflater();
        progressDialog = new ProgressDialog(this);
        header = inflater.inflate(R.layout.header, msgListView,
                false);
        header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO load more messages
                Log.d("zeeley", "header clicked");
                if (!constants.isNetworkAvailable(chatactivity.this)) {
                    Toast.makeText(chatactivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else {
                    loadMoreChat();
                }

            }
        });

        emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        rootView = findViewById(R.id.root_view);
        emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        submitButton = (ImageView) findViewById(R.id.submit_btn);
        msgListView = (ListView) findViewById(R.id.lv);
        // bundle
        Bundle b = getIntent().getBundleExtra(constants.BUNDLE);
        dummy = Parcels.unwrap(b.getParcelable(constants.Dummy));
        pk = dummy.getId();
        settings = new settingsObj(dummy.getSettings());
        if (!settings.canMessage) {
            footer = View.inflate(this, R.layout.block_footer, msgListView);
            TextView block = (TextView) footer.findViewById(R.id.list_item_section_text);
            block.setText("You are blocked by user.You can not send messages");
            msgListView.addFooterView(footer);
        }
        TextView toolbarTitle = (TextView) findViewById(R.id.actionbar_title);

        ArrayList<ChatMessage> msgs = new database(this).getChat(pk);
        if (msgs != null) {
            chatlist.addAll(0, msgs);
            if (msgs.size() < 15) {
                messageKhatam = true;
            } else {
                messageKhatam = false;
                msgListView.addHeaderView(header);
            }
        }
        // Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        if (!chatlist.isEmpty()) {
            ChatMessage msg = chatlist.get(0);
            if (msg.isDate())
                lastDate = msg.getDate();
        }
        adapter = new fragment_adapter(chatactivity.this, chatlist);

        msgListView.setAdapter(adapter);

        //getChat();

        if (dummy != null) {
            toolbarTitle.setText(dummy.getName());
            toolbarTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(chatactivity.this, userProfile.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(constants.From_onlineUser, false);
                    bundle.putBoolean(constants.callFinish, true);
                    bundle.putParcelable(constants.Dummy, Parcels.wrap(dummy));
                    intent.putExtra(constants.BUNDLE, bundle);
                    startActivity(intent);
                }
            });
            ImageView imageView = (ImageView) findViewById(R.id.actionbar_icon);
            imageView.setImageBitmap(dummy.getImage());
           /* Resources res = getResources();
            Bitmap src = BitmapFactory.decodeResource(res, dummy.getImage());
            RoundedBitmapDrawable dr =
                    RoundedBitmapDrawableFactory.create(res, src);
            dr.setCircular(true);
            //dr.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);
            // imageView.setImageDrawable(dr);
            imageView.setImageDrawable(dr);
            //imageView.setImageResource(dummy.getImage());*/
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
                        new MaterialDialog.Builder(chatactivity.this)
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
                    ChatMessage message = Parcels.unwrap(intent.getParcelableExtra(constants.New_Message));
                    adapter.add(message);
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
        if (sharedPreferences.getBoolean(pk, false)) {
            // load msgs from database
        }
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
                Intent intent = new Intent(chatactivity.this, userProfile.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(constants.From_onlineUser, false);
                bundle.putBoolean(constants.callFinish, true);
                //bundle.putString(constants.SOURCE, constants.From_onlineUser);
                bundle.putParcelable(constants.Dummy, Parcels.wrap(dummy));
                intent.putExtra(constants.BUNDLE, bundle);
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
                        adapter.deleteAll();
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
        if (!settings.canMessage) {
            Toast.makeText(this, "You cant message", Toast.LENGTH_SHORT).show();
            Log.d(constants.zeeley, "blockd by user");
        } else {
            if (message.trim().length() > 0) {

                final ChatMessage chatMessage = new ChatMessage(message, true);
                chatMessage.setTime(CommonMethods.getCurrentTime());
                chatMessage.setDate(CommonMethods.getCurrentDate());
                emojiconEditText.setText("");
                ChatMessage cm = chatlist.get(chatlist.size() - 1);
                Log.d(constants.zeeley, "last msg is " + cm.getMessage() + "and date is " + cm.getDate());
                if (!cm.getDate().equals(CommonMethods.getCurrentDate())) {
                    Log.d(constants.zeeley, "dates unequal so adding new date ");
                    ChatMessage msg = new ChatMessage(null, null);
                    msg.setIsDate(true);
                    msg.setDate(CommonMethods.getCurrentDate());
                    adapter.add(msg);

                }
                adapter.add(chatMessage);
                chats_updater.updatelist(this, dummy, -1, message, true, false);
                if (!constants.isNetworkAvailable(chatactivity.this)) {
                    Toast.makeText(chatactivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else
                    sendMessage(message, pk, this);

//  send msg to server and insert data into database.

            }
        }


    }

    @Override
    protected void onPause() {
        // chats_updater.updateChats();
        manager.unregisterReceiver(receiver);
        constants.Current_Id = -1;
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public void delete(SparseBooleanArray array) {
        progressDialog.setMessage("Deleting Messages");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        for (int i = 0; i < array.size(); i++) {
            if (array.valueAt(i)) {
                ChatMessage chatMessage = (ChatMessage) (msgListView.getItemAtPosition(i));
                //
                // long id=chatMessage.getId();
                //remove this id from database...
                adapter.chatMessageList.remove(i);
            }
        }
        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
        Toast.makeText(chatactivity.this, itemsCount + " messages deleted", Toast.LENGTH_SHORT).show();
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
                    String msg = dateAndTime + " " + dummy.getName() + ":" + chatMessage.getMessage() + "\n";
                    stringBuilder.append(msg);
                }
            }
        }
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("copiedMsgs", stringBuilder.toString());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(chatactivity.this, itemsCount + " messages copied", Toast.LENGTH_SHORT).show();
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
        popup.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new OnSoftKeyboardOpenCloseListener() {

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
        popup.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

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
        popup.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                emojiconEditText.dispatchKeyEvent(event);
            }
        });

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        emojiButton.setOnClickListener(new OnClickListener() {

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
        submitButton.setOnClickListener(new OnClickListener() {

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

    public class moreChats extends AsyncTask<Integer, Void, Void> {

        String error = null;
        ArrayList<ChatMessage> list;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(chatactivity.this);
            progressDialog.setMessage("Loading more messages");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected Void doInBackground(Integer... params) {
            int start = 10 * params[0] + 5;
            int end = start + 10;
            try {
                Log.d("zeeley", " loading more new msgs");
                URL url = new URL("http://zeeley.com/scroll_msgs?a=" + Integer.toString(start) +
                        "&b=" + Integer.toString(end) + "&f=" + pk);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setInstanceFollowRedirects(true);

                if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String s; (s = r.readLine()) != null; ) stringBuilder.append(s);
                    JSONArray array = new JSONArray(stringBuilder.toString());
                    if (array.length() < 10) messageKhatam = true;
                    if (array.length() != 0) {
                        JSONObject fields;
                        JSONObject parameters;
                        String m_message, post_time;
                        int m_id;
                        String d_old = "";
                        boolean isMine;
                        list = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            parameters = array.getJSONObject(i);
                            fields = parameters.getJSONObject("fields");
                            m_id = fields.getInt("m_id");
                            m_message = fields.getString("m_message");
                            post_time = fields.getString("post_time");
                            /// Log.d(constants.zeeley, "msg "+i+ m_message+" time "+post_time);
                            isMine = true;
                            if (m_id == Integer.parseInt(pk)) isMine = false;
                            ChatMessage message = new ChatMessage(m_message, isMine);

                            if (i == 0) {
                                d_old = CommonMethods.dateFormat.format(parseDate(post_time));
                                ChatMessage m = new ChatMessage(null, null);
                                m.setIsDate(true);
                                m.setDate(d_old);
                                list.add(m);
                            }
                            String d = CommonMethods.dateFormat.format(parseDate(post_time));
                            if (!d.equalsIgnoreCase(d_old)) {
                                Log.d(constants.zeeley, "date changed dOld is " + d_old + "dNew is " + d);
                                ChatMessage m = new ChatMessage(null, null);
                                m.setIsDate(true);
                                m.setDate(d);
                                list.add(m);
                                d_old = d;
                            }
                            list.add(message);

                        }
                    }
                }
            } catch (Exception e) {
                Log.d(constants.zeeley, "excep ocurd while ldng mor msgs");
                error = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            if (error != null)
                Toast.makeText(chatactivity.this, error, Toast.LENGTH_SHORT).show();
            if (list != null && list.size() > 0) {
               /* Log.d(constants.zeeley, " list size is "+ list.size()+" and list msgs are");
                for (ChatMessage msg : list) {
                    if (msg.isDate())
                        Log.d(constants.zeeley, " its date " + msg.getDate());
                    else if (msg.isImage())
                        Log.d(constants.zeeley, " its image");
                    else Log.d(constants.zeeley, " its msg " + msg.getMessage());
                }

                Log.d(constants.zeeley, list.size() + "new msgs r availble");*/
                if (messageKhatam)
                    msgListView.removeHeaderView(header);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                for (int i = list.size() - 1; i >= 0; i--) {
                    if (list.get(i).isDate()) {
                       /* Date d= null,od=null;
                        try {
                            d = dateFormat.parse(list.get(i).getDate());
                            od = dateFormat.parse(chatlist.get(0).getDate());

                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.d(constants.zeeley," excep parsing date in chatactivity 697");
                        }


                        if(d!=null&&d.compareTo(od)==0){
                            chatlist.remove(0);
                        }else {
                            Log.d(constants.zeeley," date obj is null 704");
                        }*/
                        String nd = list.get(i).getDate();
                        String od = chatlist.get(0).getDate();
                        if (nd.equals(od)) {
                            chatlist.remove(0);
                        }
                        chatlist.addAll(0, list);
                      /*  Log.d(constants.zeeley," chatlist msgs are");
                        for (ChatMessage msg : chatlist) {
                            if (msg.isDate())
                                Log.d(constants.zeeley, " its date " + msg.getDate());
                            else if (msg.isImage())
                                Log.d(constants.zeeley, " its image");
                            else Log.d(constants.zeeley, " its msg " + msg.getMessage());
                        }*/
                        adapter.changedataSource(chatlist);
                        msgListView.smoothScrollToPositionFromTop(list.size(), 200, 500);
                        break;
                    }
                }

            }
        }
    }

    public class LoadChat extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter = new fragment_adapter(chatactivity.this, chatlist);
            msgListView.setAdapter(adapter);
            if (!messageKhatam) {
                msgListView.addHeaderView(header, null, false);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL("http://zeeley.com/android_message/" + "?to=" + pk);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String s; (s = r.readLine()) != null; ) stringBuilder.append(s);
                    JSONArray array = new JSONArray(stringBuilder.toString());
                    if (array.length() < 15) messageKhatam = true;
                    if (array.length() != 0) {
                        JSONObject fields;
                        JSONObject parameters;
                        String m_id, m_message, post_time;
                        Date d_old = null;
                        boolean isMine;
                        for (int i = 0; i < array.length(); i++) {
                            parameters = array.getJSONObject(i);
                            fields = parameters.getJSONObject("fields");
                            m_id = fields.getString("m_id");
                            m_message = fields.getString("m_message");
                            post_time = fields.getString("post_time");
                            isMine = false;
                            if (Integer.parseInt(m_id) == Integer.parseInt(pk)) isMine = true;
                            ChatMessage map = new ChatMessage(m_message, isMine);
                            map.setDate(post_time);
                            new database(chatactivity.this).addChatUser(map, pk);
                            ChatMessage message = new ChatMessage(m_message, isMine);
                            if (i == 0) {
                                d_old = parseDate(post_time);
                                putDate(d_old);
                            }
                            Date d = parseDate(post_time);

                            if (!CommonMethods.dateFormat.format(d_old).equalsIgnoreCase(CommonMethods.dateFormat.format(d))) {
                                putDate(d);
                                d_old = d;
                            }
                            message.setTime(CommonMethods.format.format(d));
                            chatlist.add(message);

                        }
                    }
                }

            } catch (Exception e) {
                Toast.makeText(chatactivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    private void putDate(Date date) {
        ChatMessage message = new ChatMessage(null, null);
        message.setIsDate(true);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        message.setDate(dateFormat.format(date));
        chatlist.add(message);
    }

    public void loadMoreChat() {
        if (!messageKhatam) {
            Log.d(constants.zeeley, "msg not katam caling morechats");
            new moreChats().execute(i);
            i++;
        }

    }

    public static Date parseDate(String d) {
        String date = d.substring(0, d.indexOf('T'));
        String time = d.substring(d.indexOf('T') + 1, d.indexOf('Z'));
        // Log.d(constants.zeeley, "inchatactivity parsedate date is " + date);
        //Log.d(constants.zeeley, "inchatactivity parsedate time is " + time);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date dt;

        try {

            dt = df.parse(date + " " + time);
            // Log.d(constants.zeeley, "in chatactivity parsedate date object is not null  ");
            return dt;
        } catch (ParseException e) {
            // Log.w(constants.zeeley, "inchatactivity parsedate date object is null ");
            e.printStackTrace();
        }

        // Log.w(constants.zeeley, "inchatactivity parsedate returning null for date obj ");
        return null;
    }

    public void getChat() {// called when dummy is online..
        new LoadChat().execute();
        i = 0;
    }

    public static String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void sendMessage(final String message, String pk, final Context context) {

        new AsyncTask<String, Void, Void>() {
            boolean result = false;
            String id, msg;

            @Override
            protected Void doInBackground(String... params) {
                try {
                    this.id = params[1];
                    this.msg = params[0];
                    URL url = new URL("http://zeeley.com/android_sendmessage/?message=" + URLEncoder.encode(params[0], "utf-8") + "&to=" + params[1]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Charset", "utf-8");

                    connection.setInstanceFollowRedirects(true);
                    connection.setConnectTimeout(5000);
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        result = true;
                        Log.d("zeeley", "msg sent to server");
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        Log.d(constants.zeeley, bufferedReader.readLine());

                    } else {
                        Log.d("zeeley", "msg not sent to server");

                    }

                } catch (Exception e) {

                    Log.d("zeeley", e.getMessage());

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                ChatMessage map = new ChatMessage(msg, true);
                map.setDate(createDateString(new Date(System.currentTimeMillis())));

                if (result) {
                    Log.d("zeeley", "message added to sentmessages");

                } /*else {
                    map.setIsSent(false);
                    if (isNetworkAvailable(context)) {
                        Log.d("zeeley", "trying to send again");
                        sendMessage(message, id, context);
                    }
                }*/
                new database(context).addChatUser(map, id);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,message, pk);
    }

    public static String createDateString(Date d) {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss.SSS");
        return date.format(d) + 'T' + time.format(d) + 'Z';
    }

}

