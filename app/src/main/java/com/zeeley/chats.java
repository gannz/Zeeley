package com.zeeley;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class chats extends Fragment implements chatadapter.ClickListener {
    private Context context;
    private ListView listView;
    private MenuInflater menuInflater;
    // this datasource is obtained from sharedpreferences
    public ArrayList<dataobject> datasource = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private Boolean fromMainactivity = false;
    private Boolean fromForward = false;
    private ArrayList<String> messagesToForward;
    private int Position;
    myAdapter myadapter;
    public static boolean updateData = false;
    public static boolean isActive = false;
    public static boolean fromBlocked = false;
    LocalBroadcastManager localBroadcastManager;
    BroadcastReceiver receiver;
    IntentFilter filter;

    public chats() {

    }

    public static chats getInstance(String s, ArrayList<String> list) {
        chats c = new chats();
        Bundle b = new Bundle();
        b.putString("source", s);
        b.putStringArrayList(constants.FORWARD_MSGSLIST, list);
        c.setArguments(b);
        return c;
    }

    @Override
    public void onResume() {
        Log.d(constants.zeeley, " onresume");
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(constants.notificationId);
        constants.notifications.clear();
        if (chats_updater.isChanged) {
            Log.d(constants.zeeley, "updating adapter calld in onresume");
            datasource.clear();
            datasource.addAll(chats_updater.list);
            myadapter.changedatasource(datasource);
            myadapter.notifyDataSetChanged();
            chats_updater.isChanged = false;
        }
        localBroadcastManager.registerReceiver(receiver, filter);
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(constants.zeeley, " oncreate cald in chats frag");
        context = getActivity();
        menuInflater = getActivity().getMenuInflater();
        Bundle b = getArguments();
        String from = b.getString("source");
        if (from != null) {
            if (from.equals(constants.FROM_REGULAR)) {

                fromMainactivity = true;
                messagesToForward = b.getStringArrayList(constants.FORWARD_MSGSLIST);
            } else if (from.equals(constants.FROM_FORWARD)) {
                fromForward = true;
                messagesToForward = b.getStringArrayList(constants.FORWARD_MSGSLIST);
            } else if (from.equals(constants.From_blockedList)) {
                fromBlocked = true;
            }
        }
        sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
        if(chats_updater.list!=null){
            if(chats_updater.list.isEmpty()){
                if (sharedPreferences.contains(constants.CHATS_LIST)) {
                    String jsonchats = sharedPreferences.getString(constants.CHATS_LIST, null);

                    if (jsonchats != null) {
                        Log.d(constants.zeeley, " loading chats from preferences ");
                        Gson gson = new Gson();
                        dataobject[] dataobjects = gson.fromJson(jsonchats, dataobject[].class);
                        datasource.clear();
                        for (dataobject i : dataobjects) {
                            datasource.add(i);
                        }
                    }
                }
            }
            else {
                Log.d(constants.zeeley," list of chatsupdatr not empty so getng data");
                datasource.clear();
                datasource.addAll(chats_updater.list);
            }
        }
       else {
            if (sharedPreferences.contains(constants.CHATS_LIST)) {
                String jsonchats = sharedPreferences.getString(constants.CHATS_LIST, null);

                if (jsonchats != null) {
                    Log.d(constants.zeeley, " loading chats from preferences ");
                    Gson gson = new Gson();
                    dataobject[] dataobjects = gson.fromJson(jsonchats, dataobject[].class);
                    datasource.clear();
                    for (dataobject i : dataobjects) {
                        datasource.add(i);
                    }
                    chats_updater.list=new ArrayList<>();
                    chats_updater.list.addAll(datasource);
                }

            }
        }
        setHasOptionsMenu(true);
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(constants.newMsg)) {
                    if (chats_updater.isChanged) {
                        Log.d(constants.zeeley, "updating adapter calld in onresume");
                        datasource.clear();
                        datasource.addAll(chats_updater.list);
                        myadapter.changedatasource(datasource);
                        myadapter.notifyDataSetChanged();
                        chats_updater.isChanged = false;
                    }
                }
            }
        };
        filter = new IntentFilter(constants.newMsg);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            dataobject obj = datasource.get(info.position);
            menu.setHeaderTitle(datasource.get(info.position).getName());
            if (obj.isGroup()) {
                menuInflater.inflate(R.menu.info_grp_context, menu);
            } else {
                menuInflater.inflate(R.menu.chats_context, menu);
            }
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            Log.d(constants.zeeley, "chats context cald");
            final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int id = item.getItemId();
            final dataobject user = datasource.get(menuInfo.position);
            if (user.isGroup()) {
                switch (id) {
                    case R.id.grpinfo:
                        Intent intent = new Intent(context, groupProfile.class);
                        dummyGroupProfile gp = new dummyGroupProfile(R.id.userimage, user.getName(), user.getId());
                        intent.putExtra(constants.Dummy, Parcels.wrap(gp));
                        startActivity(intent);
                        break;
                   /* case R.id.mute:
                        new MaterialDialog.Builder(context)
                                .title("Mute " + user.getName() + " for..")
                                .autoDismiss(true)
                                .items(new String[]{"8 Hours", "1 Week", "1 Year"})
                                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        /**
                                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                         * returning false here won't allow the newly selected radio button to actually be selected.
                                         **/
                                       /* dialog.setSelectedIndex(which);
                                        return true;
                                    }
                                })
                                .positiveText("ok")
                                .negativeText("cancel")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                        int i = dialog.getSelectedIndex();
                                        Toast.makeText(context, i + " is selected", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .alwaysCallSingleChoiceCallback()
                                .show();
                        break;*/
                    case R.id.exit:
                        String title = "Exit " + user.getName() + "?";
                        new MaterialDialog.Builder(context)
                                .title(title)
                                .positiveText("CANCEL")
                                .negativeText("EXIT")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    }
                                })
                                .show();
                        break;
                }
            } else {
                switch (id) {
                    case R.id.profile:
                        dummyProfile dp = new dummyProfile(user.getName(), user.getId(), user.getSettings().getSettingsString(),
                                constants.getImage(Integer.parseInt(user.getId()), context));
                        Intent intent = new Intent(context, userProfile.class);
                        Bundle bundle = new Bundle(2);
                        bundle.putBoolean(constants.From_onlineUser, false);
                        bundle.putParcelable(constants.Dummy, Parcels.wrap(dp));
                        intent.putExtra(constants.BUNDLE, bundle);
                        startActivity(intent);
                        break;
                    case R.id.remove:
                        new MaterialDialog.Builder(context)
                                .title("Remove " + user.getName() + " from List?")
                                .positiveText("ok")
                                .negativeText("cancel")
                                .autoDismiss(true)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                        datasource.remove(menuInfo.position);
                                        myadapter.changedatasource(datasource);
                                        String chatlist = sharedPreferences.getString(constants.CHATS_LIST, constants.DEFAULT);
                                        Gson gson = new Gson();
                                        dataobject[] objs = gson.fromJson(chatlist, dataobject[].class);
                                        ArrayList<dataobject> users = new ArrayList<>(Arrays.asList(objs));
                                        for (int i = 0; i < users.size(); i++) {
                                            if (user.getId().equals(users.get(i).getId())) {
                                                users.remove(i);
                                                break;
                                            }
                                        }
                                        SharedPreferences.Editor editor;
                                        editor = sharedPreferences.edit();
                                        editor.remove(constants.CHATS_LIST);
                                        editor.putString(constants.CHATS_LIST, gson.toJson(users));
                                        editor.apply();
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
                    case R.id.block:
                        showBlock(user);

                        break;
                    /*case R.id.mute:
                        new MaterialDialog.Builder(context)
                                .title("Mute " + user.getName() + " for..")
                                .autoDismiss(true)
                                .items(new String[]{"8 Hours", "1 Week", "1 Year"})
                                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        /**
                                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                         * returning false here won't allow the newly selected radio button to actually be selected.
                                         **/
                                       /* dialog.setSelectedIndex(which);
                                        return true;
                                    }
                                })
                                .positiveText("ok")
                                .negativeText("cancel")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                        int i = dialog.getSelectedIndex();
                                        Toast.makeText(context, i + " is selected", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .alwaysCallSingleChoiceCallback()
                                .show();
                        break;*/
                }
            }
            return true;
        }

        return false;
    }

    public void showBlock(final dataobject user) {
        settingsObj settings = user.getSettings();
        Log.d(constants.zeeley, "setngs when show block cald"+settings.canInvite + " " + settings.canViewProfile + " " + settings.canMessage + " " + settings.canAccessLocation + " ");

        String title = "Block/Unblock " + user.getName() + " from";
        final ArrayList<Integer> list = new ArrayList<>(4);
        Log.d(constants.zeeley, "setings of " + user.getName() + " " + user.getSettings().getSettingsString());
        Log.d(constants.zeeley,settings.canInvite+" "+settings.canViewProfile+" "+settings.canMessage+" "+settings.canAccessLocation+" ");
        if (settings.CanInvite())
            list.add(0);
        else list.add(1);
        if (settings.CanViewProfile())
            list.add(0);
        else list.add(1);
        if (settings.CanMessage())
            list.add(0);
        else list.add(1);
        if (settings.CanAccessLocation())
            list.add(0);
        else list.add(1);
        Integer[] defaultIndices;
        if (list.get(0) == 1 && list.get(1) == 1 && list.get(2) == 1 && list.get(3) == 1) {
            title = "Unblock " + user.getName() + " from";
        }
        if (list.get(0) == 0 && list.get(1) == 0 && list.get(2) == 0 && list.get(3) == 0) {
            title = "Block " + user.getName() + " from";
        }


        defaultIndices = list.toArray(new Integer[list.size()]);
        Log.d(constants.zeeley,"def indic are "+defaultIndices[0]+" "+defaultIndices[1]+" "+defaultIndices[2]+" "+defaultIndices[3]+" ");
        new MaterialDialog.Builder(context)
                .title(title)
                .items(R.array.block)
                .itemsCallbackMultiChoice(defaultIndices, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                        for (Integer i : which) {
                            dialog.setSelectedIndex(i);
                        }
                        return true;
                    }
                })
                .positiveText("ok")
                .alwaysCallMultiChoiceCallback()
                .negativeText("cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        // blockedItem item=new blockedItem(user.getName(),);
                        Integer[] indices = dialog.getSelectedIndices();
                        ArrayList<Integer> finalIndices = new ArrayList<Integer>(4);
                        for (int i = 0; i < 4; i++) {
                            boolean contains = false;
                            for (Integer j : indices) {
                                if (i == j) {
                                    contains = true;
                                    break;
                                }
                            }
                            if (contains)
                                finalIndices.add(1);
                            else finalIndices.add(0);
                        }
                        boolean changed = false;
                        for (int i = 0; i < 4; i++) {
                            if ((int) list.get(i) != finalIndices.get(i)) {
                                changed = true;
                                break;
                            }
                        }
                        if (changed) {
                            ArrayList<String> finalStates = new ArrayList<String>(4);
                            for (int i = 0; i < 4; i++) {
                                if ((int) list.get(i) != finalIndices.get(i)) {
                                    finalStates.add("changed");
                                } else {
                                    finalStates.add("unchanged");
                                }
                            }

                            //blockCall.block(list.get(0) == 0, list.get(1) == 0, list.get(2) == 0, list.get(3) == 0, context, user);
                            finalBlockCall.block(finalStates, finalIndices, context, user);
                        }
                        updateData = true;
                    }
                })
                .show();

    }

    public void showblockContact(final dataobject user)
    {
        settingsObj settings = user.getSettings();
        Log.d(constants.zeeley, "aftr"+settings.canInvite + " " + settings.canViewProfile + " " + settings.canMessage + " " + settings.canAccessLocation + " ");
        String title = "Block " + user.getName() + " from";
        final ArrayList<Integer> list = new ArrayList<>(4);
        final ArrayList<String> blockMenu = new ArrayList<>();
        if (settings.CanInvite())
            list.add(0);
        else list.add(1);
        if (settings.CanViewProfile())
            list.add(0);
        else list.add(1);
        if (settings.CanMessage())
            list.add(0);
        else list.add(1);
        if (settings.CanAccessLocation())
            list.add(0);
        else list.add(1);
        //final ArrayList<Integer> blockIndices = new ArrayList<>();
        //final ArrayList<Integer> unblockIndices = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (list.get(i) == 0) {
                switch (i) {
                    case 0:
                        blockMenu.add("Sending invitation");
                        //unblockIndices.add(0);
                        break;
                    case 1:
                        blockMenu.add("Viewing profile");
                        // unblockIndices.add(1);
                        break;
                    case 2:
                        blockMenu.add("Messaging");
                        // unblockIndices.add(2);
                        break;
                    case 3:
                        blockMenu.add("Accesing location");
                        //unblockIndices.add(3);
                        break;
                }
            }// else blockIndices.add(i);
        }
        if (blockMenu.size() == 0) {
            Toast.makeText(context, user.getName() + " blocked from Invitation,Viewing profile," +
                    "messaging,accesing location", Toast.LENGTH_SHORT).show();
            return;
        }
        new MaterialDialog.Builder(context)
                .title(title)
                .items(blockMenu)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                        for (Integer i : which) {
                            dialog.setSelectedIndex(i);
                        }
                        return true;
                    }
                })
                .positiveText("ok")
                .alwaysCallMultiChoiceCallback()
                .negativeText("cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        // blockedItem item=new blockedItem(user.getName(),);
                        Integer[] indices = dialog.getSelectedIndices();
                        if (indices.length == 0) {
                            return;
                        }
                        boolean canaccesslocation = true, caninvite = true, canmessage = true, canviewprofile = true;
                        for (Integer i : indices) {
                            String menu = blockMenu.get(i);
                            switch (menu) {
                                case "Messaging":
                                    canmessage = false;
                                    break;
                                case "Accesing location":
                                    canaccesslocation = false;
                                    break;
                                case "Viewing profile":
                                    canviewprofile = false;
                                    break;
                                case "Sending invitation":
                                    caninvite = false;
                                    break;
                            }
                        }
                       blockCall.block(caninvite, canviewprofile, canmessage, canaccesslocation, context, user, list);

                       /* for (int i : unblockIndices) {

                            for (Integer j : indices) {
                                if (i == j) {
                                    unblockIndices.remove(Integer.valueOf(i));
                                    blockIndices.add(i);
                                }
                            }
                        }
                        boolean canaccesslocation = true, caninvite = true, canmessage = true, canviewprofile = true;
                        for (int i : unblockIndices) {
                            switch (i) {
                                case 0:
                                    caninvite = true;
                                    break;
                                case 1:
                                    canviewprofile = true;
                                    break;
                                case 2:
                                    canmessage = true;
                                    break;
                                case 3:
                                    canaccesslocation = true;
                                    break;
                            }
                        }
                        for (int i : blockIndices) {
                            switch (i) {
                                case 0:
                                    caninvite = false;
                                    break;
                                case 1:
                                    canviewprofile = false;
                                    break;
                                case 2:
                                    canmessage = false;
                                    break;
                                case 3:
                                    canaccesslocation = false;
                                    break;
                            }
                        }
                        blockCall.block(caninvite, canviewprofile, canmessage, canaccesslocation, context, user);*/

                    }
                })
                .show();
    }

    @Override
    public void onDestroy() {
        isActive = false;
        if (fromMainactivity) {
            unregisterForContextMenu(listView);
        }

        super.onDestroy();
    }


    public void saveData() {

        if (!chats_updater.isSaved) {
            Log.d(constants.zeeley,"unsavd so saving data ");
            SharedPreferences sharedPreferences;
            SharedPreferences.Editor editor;
            sharedPreferences = context.getSharedPreferences(constants.MY_SHAREDPREFERENCES, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String jsonstring = gson.toJson(datasource);
            if (sharedPreferences.contains(constants.CHATS_LIST)) {
                editor.remove(constants.CHATS_LIST);
            }
            editor.putString(constants.CHATS_LIST, jsonstring);
            editor.apply();
            chats_updater.isSaved = true;
        }

    }


    @Override
    public void onPause() {
        Log.d(constants.zeeley," onpause cald");
         saveData();
        localBroadcastManager.unregisterReceiver(receiver);
        super.onPause();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        MenuItem search = menu.findItem(R.id.mysearch);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                myadapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myadapter.filter(newText);
                return true;
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_chats, container, false);
        listView = (ListView) v.findViewById(R.id.list);
        myadapter = new myAdapter(context, datasource);
        listView.setAdapter(myadapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dataobject obj = (dataobject) (listView.getItemAtPosition(position));

                if (fromMainactivity) {
                    if (obj.isGroup()) {
                        Log.d(constants.zeeley, "launching grup chat activity");
                        Intent i = new Intent(context, groupChatScreen.class);
                        Bundle b = new Bundle(2);
                        dummyGroupProfile groupProfile = new dummyGroupProfile(R.id.userimage, obj.getName(), obj.getId());
                        b.putString(constants.SOURCE, constants.FROM_REGULAR);
                        b.putParcelable(constants.Dummy, Parcels.wrap(groupProfile));
                        i.putExtra(constants.BUNDLE, b);
                        startActivity(i);
                        if (obj.isColorMsg()) {
                            chats_updater.unColorMsg(String.valueOf(id), context);
                        }
                    } else {
                        dummyProfile user = new dummyProfile(obj.getName(), obj.getId(), obj.getSettings().getSettingsString(),
                                constants.getImage(Integer.parseInt(obj.getId()), context));
                        Log.d(constants.zeeley, " launching chatactivity");
                        Intent i = new Intent(context, chatactivity.class);
                        Bundle b = new Bundle(2);
                        b.putString(constants.SOURCE, constants.FROM_REGULAR);
                        b.putParcelable(constants.Dummy, Parcels.wrap(user));
                        i.putExtra(constants.BUNDLE, b);
                        startActivity(i);
                        if (obj.isColorMsg()) {
                            chats_updater.unColorMsg(String.valueOf(id), context);
                        }
                    }

                } else if (fromForward) {
                    final dummyProfile user = new dummyProfile(obj.getName(), obj.getId(), obj.getSettings().getSettingsString(),
                            constants.getImage(Integer.parseInt(obj.getId()), context));
                    new MaterialDialog.Builder(context)
                            .title("Forward to " + obj.getName() + "?")
                            .positiveText("ok")
                            .negativeText("cancel")
                            .autoDismiss(true)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    dialog.dismiss();
                                    getActivity().setResult(Activity.RESULT_OK);
                                    getActivity().finish();
                                    Intent i = new Intent(context, chatactivity.class);
                                    Bundle b = new Bundle();
                                    b.putString(constants.SOURCE, constants.FROM_FORWARD);
                                    b.putParcelable(constants.Dummy, Parcels.wrap(user));
                                    b.putStringArrayList(constants.FORWARD_MSGSLIST, messagesToForward);
                                    i.putExtra(constants.BUNDLE, b);
                                    startActivity(i);
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else if (fromBlocked) {
                    showblockContact(obj);
                }
            }
        });
        if (fromMainactivity) {
            registerForContextMenu(listView);
        }

        return v;
    }

    @Override
    public void onClick(dataobject d) {
        dataobject obj = d;
        final dummyProfile user = new dummyProfile(obj.getName(), obj.getId(), obj.getSettings().getSettingsString(),
                constants.getImage(Integer.parseInt(d.getId()), context));
        if (fromMainactivity) {
            Log.d(constants.zeeley, " launching chatactivity");
            Intent i = new Intent(context, chatactivity.class);
            Bundle b = new Bundle();
            b.putString(constants.SOURCE, constants.FROM_REGULAR);
            b.putParcelable(constants.Dummy, Parcels.wrap(user));
            i.putExtra(constants.BUNDLE, b);
            startActivity(i);
           /* if (obj.isColorMsg()) {
                TextView textView = (TextView) view.findViewById(R.id.message);
                textView.setTextColor(Color.DKGRAY);
                chats_updater.unColorMsg(String.valueOf(id));
            }*/
        } else if (fromForward) {
            new MaterialDialog.Builder(context)
                    .title("Forward to " + obj.getName() + "?")
                    .positiveText("ok")
                    .negativeText("cancel")
                    .autoDismiss(true)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            dialog.dismiss();
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                            Intent i = new Intent(context, chatactivity.class);
                            Bundle b = new Bundle();
                            b.putString(constants.SOURCE, constants.FROM_FORWARD);
                            b.putParcelable(constants.Dummy, Parcels.wrap(user));
                            b.putStringArrayList(constants.FORWARD_MSGSLIST, messagesToForward);
                            i.putExtra(constants.BUNDLE, b);
                            startActivity(i);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else if (fromBlocked) {
            showblockContact(obj);
        }
    }


    @Override
    public void onLongClick(final dataobject d) {
        String[] items = {"View Profile", "Remove from list", "Delete Chat", "Block", "Mute"};
        new MaterialDialog.Builder(context)
                .title(d.getName())
                .items(items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                dummyProfile dummy = new dummyProfile(d.getName(), d.getId(), d.getSettings().getSettingsString(),
                                        constants.getImage(Integer.parseInt(d.getId()), context));
                                Intent intent = new Intent(context, userProfile.class);
                                Bundle bundle = new Bundle(2);
                                bundle.putBoolean(constants.From_onlineUser, false);
                                //bundle.putString(constants.SOURCE, constants.From_onlineUser);
                                bundle.putParcelable(constants.Dummy, Parcels.wrap(dummy));
                                intent.putExtra(constants.BUNDLE, bundle);
                                startActivity(intent);
                                break;
                            case 1:
                                Toast.makeText(context, "remove clicked", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(context, "delete clicked", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                showBlock(d);
                                break;
                            case 4:
                                new MaterialDialog.Builder(context)
                                        .title("Mute " + d.getName() + " for..")
                                        .autoDismiss(true)
                                        .items(new String[]{"8 Hours", "1 Week", "1 Year"})
                                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                            @Override
                                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                /**
                                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                                 **/
                                                dialog.setSelectedIndex(which);
                                                return true;
                                            }
                                        })
                                        .positiveText("ok")
                                        .negativeText("cancel")
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                                int i = dialog.getSelectedIndex();
                                                Toast.makeText(context, i + " is selected", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .alwaysCallSingleChoiceCallback()
                                        .show();
                                break;
                        }
                    }
                })
                .show();

    }

    private class myAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        private ArrayList<dataobject> list;
        private ArrayList<dataobject> result;
        private ArrayList<dataobject> copy;


        public myAdapter(Context context, ArrayList<dataobject> list) {
            this.context = context;
            this.list = list;
            this.copy = new ArrayList<>(list);
            result = new ArrayList<dataobject>();
            inflater = LayoutInflater.from(context);
        }

        public void changedatasource(ArrayList<dataobject> list) {
            this.list = list;
            this.copy.clear();
            this.copy.addAll(list);
            if (result == null) {
                result = new ArrayList<dataobject>();
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);

        }

        @Override
        public long getItemId(int i) {
            dataobject obj = (dataobject) getItem(i);
            return Long.parseLong(obj.getId());

        }

        public void filter(String text) {
            if (text.isEmpty()) {
                result.clear();
                result.addAll(copy);
            } else {
                result.clear();
                text = text.toLowerCase();
                for (dataobject i : copy) {
                    if (i.getName().toLowerCase().contains(text)) {
                        result.add(i);
                    }
                }

            }
            list.clear();
            list.addAll(result);
            notifyDataSetChanged();

        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflater.inflate(R.layout.chatrow, viewGroup, false);
                viewholder holder = new viewholder(view);
                view.setTag(holder);
            }

            viewholder holder = (viewholder) view.getTag();
            dataobject user = (dataobject) getItem(i);
            holder.setData(user);
            return view;

        }

        private class viewholder {
            public ImageView imageView;
            public TextView name, date, lastmessage;

            public viewholder(View itemView) {
                imageView = (ImageView) itemView.findViewById(R.id.chatimage);
                name = (TextView) itemView.findViewById(R.id.chatname);
                date = (TextView) itemView.findViewById(R.id.date);
                lastmessage = (TextView) itemView.findViewById(R.id.message);
            }

            public void setData(dataobject obj) {
                this.imageView.setImageBitmap(constants.getImage(Integer.valueOf(obj.getId()), context));
                this.name.setText(obj.getName());
                this.date.setText(obj.getDate());
                this.date.setTextColor(Color.DKGRAY);
                this.lastmessage.setText(obj.getLastmessage());
                if (obj.isColorMsg()) this.lastmessage.setTextColor(Color.GREEN);
                else this.lastmessage.setTextColor(Color.DKGRAY);
            }
        }
    }
}