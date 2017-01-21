package com.zeeley;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class settings extends AppCompatPreferenceActivity {

    //public static final String privacy_url = "http://www.http://zeeley.com/android_privacy";
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.hey
     */
    private static Context context;
    private static List<PreferenceActivity.Header> _headers;
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof CheckBoxPreference) {
                Boolean b = (Boolean) value;
                if (preference.getKey().equals("delete_all_chats")) {
                    if (b) {

                    }
                }
            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else if (preference instanceof SwitchPreference) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                boolean v = sp.getBoolean(preference.getKey(), false);
                Boolean answer = (Boolean) value;
                if (preference.getKey().equals("hideFromMap")) {
                    Log.d(constants.zeeley, " hidemap value aftr change is"+v);
                    Log.d(constants.zeeley, "calling privacyhidemap method");
                    privacyHideMap(answer, preference);
                } else if (preference.getKey().equals("hideDistance")) {
                    Log.d(constants.zeeley, " hidedistance value aftr change is"+v);
                    privacyHideDistance(answer, preference);
                    Log.d(constants.zeeley, "calling privacy hide distance method");
                }
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void privacyHideMap(final boolean value, final Preference preference) {
        final String change = value ? "1" : "0";
        final String msg;
        if (value)
            msg = "Hiding from Map";
        else msg = "Unhiding from Map";

        final ProgressDialog progressDialog = new ProgressDialog(context);
      /*  if(!constants.isNetworkAvailable(this)){
            Toast.makeText(Interst_List.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }*/
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                Log.d(constants.zeeley, "inside post execute of phm");
                progressDialog.setMessage(msg);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                Log.d(constants.zeeley, " inside doinbg phm");
                //http://www.http://zeeley.com/android_privacy/?hide_map_all=&hide_dist_all=&block_prof=&block_location=&block_msg=&block_invite&unblock_invite=&unblock_location=&unblock_prof=&unblock_msg
                String urls = "http://zeeley.com/android_privacy/?hide_map_all=" + change + "&hide_dist_all=&block_prof=&block_location=&block_msg=&" +
                        "block_invite=&unblock_invite=&unblock_location=&unblock_prof=&unblock_msg=";
                URL url = null;
                try {
                    url = new URL(urls);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setInstanceFollowRedirects(true);
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                        return true;
                    }
                } catch (Exception e) {
                    Log.d("zeeley", " excep in phm " + e.getMessage());
                    return false;
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean answer) {
                Log.d(constants.zeeley, " inside on post exec");
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

                progressDialog.dismiss();

                if (answer) {
                    if (value){

                        ((SwitchPreference) preference).setSummary("People cannot find you on map");

                    }

                    else{
                        ((SwitchPreference) preference).setSummary("People can find you on map");
                    }

                    Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(context, "Unsuccessful", Toast.LENGTH_SHORT).show();
                }
                super.onPostExecute(answer);
            }
        }.execute();

    }


    private static void privacyHideDistance(final boolean value, final Preference preference) {

        final String change = value ? "1" : "0";
        final String msg;
        if (value)
            msg = "Hiding Distance..";
        else msg = "Unhiding Distance";

        final ProgressDialog progressDialog = new ProgressDialog(context);
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                progressDialog.setMessage(msg);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                String urls = "http://zeeley.com/android_privacy/?hide_map_all=&hide_dist_all=" + change + "&block_prof=&block_location=&block_msg=&" +
                        "block_invite=&unblock_invite=&unblock_location=&unblock_prof=&unblock_msg=";
                URL url = null;
                try {
                    url = new URL(urls);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setInstanceFollowRedirects(true);
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                        return true;
                    }
                } catch (Exception e) {
                    Log.d("zeeley", e.getMessage());
                    return false;
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean answer) {
                progressDialog.dismiss();
                if (answer) {
                    if (value) {
                        ((SwitchPreference) preference).setSummary("Your exact distance is not shown");
                    } else {
                        ((SwitchPreference) preference).setSummary("Your exact distance is shown");
                    }
                    Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Unsuccessful", Toast.LENGTH_SHORT).show();
                }
                super.onPostExecute(answer);
            }
        }.execute();

    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.

        if (preference instanceof SwitchPreference || preference instanceof CheckBoxPreference) {
            Boolean value = PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getBoolean(preference.getKey(), false);
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, value);
        } else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {

        ViewGroup rootView = (ViewGroup) findViewById(R.id.action_bar_root);
        View view = getLayoutInflater().inflate(R.layout.toolbar, rootView, false);
        rootView.addView(view, 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Settings");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        _headers = target;
        loadHeadersFromResource(R.xml.pref_headers, target);
        setContentView(R.layout.settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("Settings");

    }


    @Override
    public void setListAdapter(ListAdapter adapter) {
        if (adapter == null) {
            super.setListAdapter(null);
        } else {
            super.setListAdapter(new myadapter(this, 0, _headers));
        }
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return
                chatsFragment.class.getName().equals(fragmentName)
                        || privacyFragment.class.getName().equals(fragmentName)
                        || notificationFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class chatsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.chats);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("clearAllchats"));
            bindPreferenceSummaryToValue(findPreference("delete_all_chats"));
            bindPreferenceSummaryToValue(findPreference("font_size"));

        }

        @Override
        public void onResume() {
            super.onResume();

            if (getView() != null) {
                View frame = (View) getView().getParent();
                if (frame != null)
                    frame.setPadding(0, 0, 0, 0);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.settings, container, false);
            if (layout != null) {
                AppCompatPreferenceActivity activity = (AppCompatPreferenceActivity) getActivity();
                Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
                activity.setSupportActionBar(toolbar);
                ActionBar bar = activity.getSupportActionBar();
                bar.setDisplayHomeAsUpEnabled(true);
                bar.setTitle("chats");
            }
            return layout;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), settings.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class notificationFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.

            bindPreferenceSummaryToValue(findPreference("messageNotifications"));
            bindPreferenceSummaryToValue(findPreference("messageVibrate"));
            bindPreferenceSummaryToValue(findPreference("messageLed"));
            bindPreferenceSummaryToValue(findPreference("groupNotifications"));
            bindPreferenceSummaryToValue(findPreference("groupVibrate"));
            bindPreferenceSummaryToValue(findPreference("groupLed"));
            bindPreferenceSummaryToValue(findPreference("invitationNotifications"));
            bindPreferenceSummaryToValue(findPreference("invitationVibrate"));
            bindPreferenceSummaryToValue(findPreference("invitationLed"));

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.settings, container, false);
            if (layout != null) {
                AppCompatPreferenceActivity activity = (AppCompatPreferenceActivity) getActivity();
                Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
                activity.setSupportActionBar(toolbar);
                ActionBar bar = activity.getSupportActionBar();
                bar.setDisplayHomeAsUpEnabled(true);
                bar.setTitle("Notifications");
            }
            return layout;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {

                startActivity(new Intent(getActivity(), settings.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onResume() {
            super.onResume();

            if (getView() != null) {
                View frame = (View) getView().getParent();
                if (frame != null)
                    frame.setPadding(0, 0, 0, 0);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof RingtonePreference) {

                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }
            } else if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            }
            return true;
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class privacyFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.privacy);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            Preference map = findPreference("hideFromMap");
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            boolean value = sp.getBoolean(map.getKey(), false);
            Log.d(constants.zeeley,"curnt hidemap value is "+value);
            if (value)
                map.setSummary("People cannot find you on map");
            else map.setSummary("People can find you on map");
            map.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
            Preference dist = findPreference("hideDistance");
            boolean dval = sp.getBoolean(dist.getKey(), false);
            Log.d(constants.zeeley,"curnt hidedistance value is "+dval);
            if (dval)
                dist.setSummary("Your exact distance is not shown");
            else dist.setSummary("Your exact distance is shown");
            dist.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
            bindPreferenceSummaryToValue(findPreference("profile_photo"));
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.settings, container, false);
            if (layout != null) {
                AppCompatPreferenceActivity activity = (AppCompatPreferenceActivity) getActivity();
                Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
                activity.setSupportActionBar(toolbar);
                ActionBar bar = activity.getSupportActionBar();
                bar.setDisplayHomeAsUpEnabled(true);
                bar.setTitle("Privacy");
            }
            return layout;
        }

        @Override
        public void onResume() {
            super.onResume();

            if (getView() != null) {
                View frame = (View) getView().getParent();
                if (frame != null)
                    frame.setPadding(0, 0, 0, 0);
            }
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), settings.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }


    }


    private class myadapter extends ArrayAdapter<Header> {
        List<Header> objects;
        LayoutInflater inflater;

        public myadapter(Context context, int resource, List<Header> objects) {

            super(context, resource, objects);
            this.objects = objects;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.settings_row, parent, false);
                viewholder holder = new viewholder(convertView);
                convertView.setTag(holder);
            }
            viewholder holder = (viewholder) convertView.getTag();
            Header header = objects.get(position);
            holder.setData(R.drawable.ic_notifications_black_24dp, header.getTitle(getResources()).toString());
            return convertView;
        }

        private class viewholder {
            ImageView icon;
            TextView name;

            public viewholder(View view) {
                icon = (ImageView) view.findViewById(R.id.icon);
                name = (TextView) view.findViewById(R.id.name);
            }

            public void setData(int image, String name) {
                this.icon.setImageResource(image);
                this.name.setText(name);
            }
        }

    }

}
