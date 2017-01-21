package com.zeeley;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class info extends Fragment implements infoadapter.ClickListener {
    public static final String SAVED_DATA = "saved data";
    //RecyclerView recyclerView;
    //infoadapter adapter;
    Context context;
    private MenuInflater menuInflater;
    ArrayList<onlineUser> datasource = new ArrayList<>();
    //Parcelable[] list;
    private final String Default = "N/A";
    myadapter adapter;
    ArrayList<Parcelable> list;
    public static final String USER = "user";
    public static final String ONLINE_USERS = "onlineusers";
    private Boolean fromMainactivity = false;
    private Boolean fromForward = false;
    private Boolean fromBlockLists = false;
    private ArrayList<String> messagesToForward;
    ListView listView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private boolean exit = false;
    public static boolean updateData = false;
    private locateMarker locateMarkerListener;
    SwipeRefreshLayout swipeRefreshLayout;

    public info() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        this.context = context;
        if (context instanceof locateMarker) {
            locateMarkerListener = (locateMarker) context;
        }
    }


    public static info getInstance(ArrayList<Parcelable> list, ArrayList<String> msgsList, String s) {
        info i = new info();
        Bundle b = new Bundle();
        b.putString(constants.SOURCE, s);
        b.putParcelableArrayList(ONLINE_USERS, list);
        //b.putParcelableArray(ONLINE_USERS, list);
        b.putStringArrayList(constants.FORWARD_MSGSLIST, msgsList);
        i.setArguments(b);
        return i;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        menuInflater = getActivity().getMenuInflater();

        if (savedInstanceState != null) {
            // datasource=savedInstanceState.getParcelableArrayList(SAVED_DATA);
        }
        Bundle b = getArguments();
        String from = b.getString(constants.SOURCE);
        list = b.getParcelableArrayList(ONLINE_USERS);
        //list = b.getParcelableArray(ONLINE_USERS);
        for (Parcelable p : list) {
            datasource.add((onlineUser) Parcels.unwrap(p));
        }
        if (from != null) {
            if (from.equals(constants.FROM_REGULAR)) {
                fromMainactivity = true;

            } else if (from.equals(constants.FROM_FORWARD)) {
                fromForward = true;
                messagesToForward = b.getStringArrayList(constants.FORWARD_MSGSLIST);
            } else if (from.equals(constants.From_blockedList)) {
                fromBlockLists = true;
            }

        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        MenuItem search = menu.findItem(R.id.mysearch);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVED_DATA, list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info2, container, false);
       /* recyclerView = (RecyclerView) v.findViewById(R.id.chatrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new infoadapter();
        adapter.setdatasource(datasource);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());*/
        listView = (ListView) v.findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
        adapter = new myadapter(context, datasource);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final onlineUser user = (onlineUser) (listView.getItemAtPosition(position));
                if (fromMainactivity) {
                    Intent intent = new Intent(context, userProfile.class);
                    Bundle bundle = new Bundle(2);
                    bundle.putBoolean(constants.From_onlineUser, true);
                    //bundle.putString(constants.SOURCE, constants.From_onlineUser);
                    bundle.putParcelable(constants.USER, Parcels.wrap(user));
                    intent.putExtra(constants.BUNDLE, bundle);
                    startActivity(intent);
                } else if (fromForward) {
                    new MaterialDialog.Builder(context)
                            .title("Forward to " + user.getName() + "?")
                            .positiveText("ok")
                            .negativeText("cancel")
                            .autoDismiss(true)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    getActivity().setResult(Activity.RESULT_OK);
                                    getActivity().finish();
                                    dialog.dismiss();
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
                } else if (fromBlockLists) {
                    showblockContact(user);
                }

            }
        });
        if (fromMainactivity) {
            registerForContextMenu(listView);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!constants.isNetworkAvailable(context)){
                    Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else
                    new FetchOtherUsers(context, constants.userInterst).execute();

            }
        });

        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            onlineUser user = datasource.get(info.position);
            menu.setHeaderTitle(user.getName());
            menuInflater.inflate(R.menu.info_context, menu);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            Log.d(constants.zeeley, "info context cald");
            final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int id = item.getItemId();
            final onlineUser user = datasource.get(info.position);
            switch (id) {
                case R.id.profile:
                    Intent intent = new Intent(context, userProfile.class);
                    Bundle bundle = new Bundle(2);
                    bundle.putBoolean(constants.From_onlineUser, true);
                    //bundle.putString(constants.SOURCE, constants.From_onlineUser);
                    bundle.putParcelable(constants.USER, Parcels.wrap(user));
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
                                    datasource.remove(info.position);
                                    adapter.changedatasource(datasource);
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
                case R.id.locate:
                    locateMarkerListener.locateMarkerOnMap(String.valueOf(user.getId()));
                    break;
                case R.id.mute:
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
            return true;
        }
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        locateMarkerListener = null;
    }

    public void showBlock(final onlineUser user) {
        settingsObj settings = user.getSettings();
        Log.d(constants.zeeley, settings.getSettingsString());

        String title = "Block/Unblock " + user.getName() + " from";
        final ArrayList<Integer> list = new ArrayList<>(4);
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
                            // blockCall.blockOnlineUser(list.get(0) == 0, list.get(1) == 0, list.get(2) == 0, list.get(3) == 0, context, user);
                            if(!constants.isNetworkAvailable(context)){
                                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                finalBlockCall.blockOnlineUser(finalStates, finalIndices, context, user);
                            }

                        }
                        updateData = true;
                    }
                })
                .show();

    }

    public void showblockContact(final onlineUser user) {
        settingsObj settings = user.getSettings();
        Log.d(constants.zeeley, settings.getSettingsString());

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
        // final ArrayList<Integer> unblockIndices = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (list.get(i) == 0) {
                switch (i) {
                    case 0:
                        blockMenu.add("Sending invitation");
                        //unblockIndices.add(0);
                        break;
                    case 1:
                        blockMenu.add("Viewing profile");
                        //unblockIndices.add(1);
                        break;
                    case 2:
                        blockMenu.add("Messaging");
                        // unblockIndices.add(2);
                        break;
                    case 3:
                        blockMenu.add("Accesing location");
                        // unblockIndices.add(3);
                        break;
                }
            } //else blockIndices.add(i);
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
                        if(!constants.isNetworkAvailable(context)){
                            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            blockCall.blockOnlineUser(caninvite, canviewprofile, canmessage, canaccesslocation, context, user, list);
                        }

                        /*for (int i : unblockIndices) {

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
                        blockCall.blockOnlineUser(caninvite, canviewprofile, canmessage, canaccesslocation, context, user);**/

                    }
                })
                .show();
    }

    @Override
    public void onDestroy() {
        if (fromMainactivity) {
            unregisterForContextMenu(listView);
        }

        super.onDestroy();
    }

    @Override
    public void onClick(final onlineUser user, boolean isInvite) {
        final Intent i = new Intent(getContext(), chatactivity.class);
        final Bundle b = new Bundle();

        if (isInvite) {

            Toast.makeText(context, " send invitation clicked", Toast.LENGTH_SHORT).show();
        } else {
            if (fromForward) {
                new MaterialDialog.Builder(context)
                        .title("Forward to " + user.getName() + "?")
                        .positiveText("ok")
                        .negativeText("cancel")
                        .autoDismiss(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                b.putString(constants.SOURCE, constants.FROM_FORWARD);
                                b.putStringArrayList(constants.FORWARD_MSGSLIST, messagesToForward);
                                b.putParcelable(constants.USER, Parcels.wrap(user));
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

            } else if (fromMainactivity) {
                Intent intent = new Intent(context, userProfile.class);
                intent.putExtra(USER, Parcels.wrap(user));
                startActivity(intent);

            }
        }
    }

    @Override
    public void onLongClick(onlineUser user) {

    }

    private class myadapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        private ArrayList<onlineUser> list;
        private ArrayList<onlineUser> result;
        private ArrayList<onlineUser> copy;

        public myadapter(Context context, ArrayList<onlineUser> list) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.list = list;
            this.copy = new ArrayList<>(list);
            result = new ArrayList<onlineUser>();
        }

        public void changedatasource(ArrayList<onlineUser> list) {
            this.list = list;
            this.copy.clear();
            this.copy.addAll(list);
            if (result == null) {
                result = new ArrayList<onlineUser>();
            }
            this.notifyDataSetChanged();
        }

        public void filter(String text) {
            if (text.isEmpty()) {
                result.clear();
                result.addAll(copy);
            } else {
                result.clear();
                text = text.toLowerCase();
                for (onlineUser i : copy) {
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
        public int getCount() {
            return datasource.size();
        }

        @Override
        public Object getItem(int i) {

            return datasource.get(i);
        }

        @Override
        public long getItemId(int i) {
            onlineUser user = (onlineUser) getItem(i);
            return user.getId();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflater.inflate(R.layout.inforow, viewGroup, false);
                viewholder holder = new viewholder(view);
                view.setTag(holder);
            }

            viewholder holder = (viewholder) view.getTag();
            final onlineUser user = (onlineUser) getItem(i);
            holder.setData(user.getImage(), R.drawable.ic_action_send_now, user.getName(), user.getLocation(), user.getDistance(),user.getInterest());
            holder.invitation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(context)
                            .title("Send Invitation to " + user.getName() + "?")
                            .positiveText("send")
                            .negativeText("cancel")
                            .autoDismiss(true)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    if(!constants.isNetworkAvailable(context)){
                                        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    new AsyncTask<String, Void, Void>() {

                                        boolean isDone = false;
                                        ProgressDialog progressDialog;

                                        @Override
                                        protected void onPreExecute() {
                                            super.onPreExecute();
                                            progressDialog = new ProgressDialog(context);
                                            progressDialog.setMessage("Sending Invitation");
                                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                            progressDialog.setIndeterminate(true);
                                            progressDialog.setCancelable(false);
                                            progressDialog.show();
                                        }

                                        @Override
                                        protected Void doInBackground(String... params) {
                                            try {
                                                URL url = new URL("http://zeeley.com/invitesportee/?invited=" + params[0]);
                                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                                connection.setRequestMethod("GET");
                                                connection.setInstanceFollowRedirects(true);
                                                connection.connect();

                                                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
                                                    isDone = true;
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            return null;
                                        }

                                        protected void onPostExecute(Void aVoid) {
                                            progressDialog.dismiss();
                                            if (isDone)
                                                Toast.makeText(context, "Invitation sent", Toast.LENGTH_SHORT).show();
                                            else
                                                Toast.makeText(context, "Invitation not sent", Toast.LENGTH_SHORT).show();
                                        }
                                    }.execute(String.valueOf(user.getId()));
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

                //Toast.makeText(context," send invitation clicked",Toast.LENGTH_SHORT).show();

            });
            return view;
        }


    }

    private class viewholder {
        ImageView profilePic, invitation;
        TextView name, location, distance,interest;

        viewholder(View view) {
            profilePic = (ImageView) view.findViewById(R.id.chatimage);
            invitation = (ImageView) view.findViewById(R.id.invitation);
            name = (TextView) view.findViewById(R.id.name);
            location = (TextView) view.findViewById(R.id.location);
            distance = (TextView) view.findViewById(R.id.distance);
            interest = (TextView) view.findViewById(R.id.interest);
        }

        public void setData(Bitmap profPic, int invitation, String name, String location, String distance,String interest) {

            this.profilePic.setImageBitmap(profPic);
            this.distance.setText(distance);
            this.location.setText(location);
            if(interest.equals("Cricket")){
                Log.d(constants.zeeley,"interest is crick so showng invtatn btn");
                this.invitation.setVisibility(View.VISIBLE);
                this.invitation.setImageResource(invitation);
            }
            else {
                Log.d(constants.zeeley,"not shwng invitn btn");
                this.invitation.setVisibility(View.GONE);
            }
            this.name.setText(name);
            this.interest.setText(interest);
        }
    }

    private class FetchOtherUsers extends AsyncTask<Void, Void, String> {

        private double latitude, longitude;
        ArrayList<onlineUser> newUsers = new ArrayList<>();
        Context context;
        GPSTracker gps;
        String interest;

        public FetchOtherUsers(Context context, String interest) {
            this.context = context;
            this.interest = interest;
            gps = new GPSTracker(context);
            if (gps.canGetLocation()) {
                gpsOn();
            } else {
                gpsOff();
            }

        }

        private void gpsOn() {
            this.latitude = gps.getLatitude();
            this.longitude = gps.getLongitude();
            constants.MY_LATITUDE = gps.getLatitude();
            constants.MY_LONGITUDE = gps.getLongitude();
        }

        private void gpsOff() {
            gps.showSettingsAlert();

        }


        protected String doInBackground(Void... urls) {
            Log.d(constants.zeeley, "insid dbg of fetch users");
            String response = "";


            String url_string = "http://zeeley.com/android_position?latitude=" + String.valueOf(latitude) + "&longitude=" +
                    String.valueOf(longitude) + "&interest=" + interest;

            try {
                URL url = new URL(url_string);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setInstanceFollowRedirects(true);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    response = stringBuilder.toString();
                    constants.userInterst = interest;
                    JSONArray jData = new JSONArray(response);

                    for (int i = 0; i < jData.length(); i++) {
                        JSONObject jsonObject = jData.getJSONObject(i);
                        JSONObject obj = jsonObject.getJSONObject("fields");
                        String id = jsonObject.getString("pk");
                        URL u = new URL("http://zeeley.com/media/file_" + id + ".thumbnail");
                        HttpURLConnection img = (HttpURLConnection) u.openConnection();
                        img.setRequestMethod("GET");
                        img.setInstanceFollowRedirects(true);
                        Bitmap b = BitmapFactory.decodeStream(img.getInputStream());

                        //TODO: remov the image from constructor
                        onlineUser user = new onlineUser(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")), obj.getString("first_name") + " " +
                                obj.getString("last_name"), obj.getString("current_intrest"),
                                new settingsObj(obj.getString("priv_string")), Long.parseLong(id), b);


                        newUsers.add(user);
                    }

                } else {
                    stringBuilder.append("Error code " + responseCode);
                    response = stringBuilder.toString();
                }
                bufferedReader.close();

            } catch (MalformedURLException e1) {
                Log.e("ERROR", e1.getMessage(), e1);
                Log.d(constants.zeeley, "exp ocrd " + e1.getMessage());
                e1.printStackTrace();
            } catch (UnsupportedEncodingException e1) {
                Log.d(constants.zeeley, "exp ocrd " + e1.getMessage());
                e1.printStackTrace();
            } catch (ProtocolException e1) {
                Log.d(constants.zeeley, "exp ocrd " + e1.getMessage());
                e1.printStackTrace();
            } catch (Exception e1) {
                Log.d(constants.zeeley, "exp ocrd " + e1.getMessage());
                e1.printStackTrace();

            }

            return response;
        }

        protected void onPostExecute(String response) {
            Log.d(constants.zeeley, "inside onpst exec");

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            try {
                JSONArray array = new JSONArray(response);
                if (array.length() > 0 && !newUsers.isEmpty()) {
                    Log.d(constants.zeeley, "list not empty users availble");
                    constants.USERS_LIST.clear();
                    constants.USERS_LIST.addAll(newUsers);
                    adapter.changedatasource(newUsers);
                }

            } catch (Exception e) {
                Log.d(constants.zeeley, "excep ocrd " + e.getMessage());
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


    }

    public interface locateMarker {
        public void locateMarkerOnMap(String id);
    }
}


