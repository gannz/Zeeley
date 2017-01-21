package com.zeeley;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.parceler.Parcels;

import java.util.ArrayList;

public class groupProfile extends AppCompatActivity implements View.OnClickListener {
    ArrayList<blockedItem> datasource;
    String groupId, grpName;
    Cursor cursor;
    TextView exit;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_profile);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        exit = (TextView) findViewById(R.id.exit);
        exit.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dummyGroupProfile gp = Parcels.unwrap(getIntent().getParcelableExtra(constants.Dummy));
        groupId = gp.getId();
        grpName = gp.getName();
        ListView listView = (ListView) findViewById(R.id.list);
        cursor = new database(this).getReadableDatabase().query(database.GroupTable + groupId, new String[]{database.Name,database.ID,
                database.UserId, database.Profpic}, null, null, null, null, null);
        myAdapter adapter = new myAdapter(this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(adapter);
        dynamicToolbarColor();
        toolbarTextAppernce();

        final String[] items = {"Message", "View profile"};
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                new MaterialDialog.Builder(groupProfile.this)
                        .items(items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                dialog.dismiss();
                                groupParticipant gp = new database(groupProfile.this).getGrpParticipant(groupId, String.valueOf(id));
                                Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.default_profpic);
                                dummyProfile dummy = new dummyProfile(gp.getName(), gp.getPk(), gp.getSettings(),bitmap);
                                switch (which) {
                                    case 0:

                                        Intent i = new Intent(groupProfile.this, chatactivity.class);
                                        Bundle b = new Bundle();
                                        b.putString(constants.SOURCE, constants.FROM_REGULAR);
                                        b.putParcelable(constants.Dummy, Parcels.wrap(dummy));
                                        i.putExtra(constants.BUNDLE, b);
                                        startActivity(i);
                                        break;
                                    case 1:
                                        Intent intent = new Intent(groupProfile.this, userProfile.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean(constants.callDB,false);
                                        bundle.putBoolean(constants.From_onlineUser, false);
                                        bundle.putParcelable(constants.Dummy, Parcels.wrap(dummy));
                                        intent.putExtra(constants.BUNDLE, bundle);
                                        startActivity(intent);
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });
    }

    private void dynamicToolbarColor() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.userimg);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {

            @Override
            public void onGenerated(Palette palette) {
                collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(ContextCompat.getColor(groupProfile.this, R.color.colorPrimary)));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getMutedColor(ContextCompat.getColor(groupProfile.this, R.color.colorPrimaryDark)));
            }
        });
    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);

    }

    @Override
    protected void onDestroy() {
        cursor.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.exit:
                String title = "Exit " + grpName + "?";
                new MaterialDialog.Builder(this)
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
        }
    }

    class myAdapter extends CursorAdapter {
        Context context;
        LayoutInflater inflater;
        Cursor cursor;
        SQLiteDatabase sqLiteDatabase;

        public myAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            this.context = context;
            cursor = c;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = inflater.inflate(R.layout.grp_participant, parent, false);
            viewholder holder = new viewholder(view);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(final View view, final Context context, final Cursor cursor) {
            viewholder holder = (viewholder) view.getTag();
            byte[] img = cursor.getBlob(cursor.getColumnIndex(database.Profpic));
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            holder.image.setImageBitmap(bitmap);
            holder.name.setText(cursor.getString(cursor.getColumnIndex(database.Name)));
            int id = cursor.getInt(cursor.getColumnIndex(database.UserId));
            view.setId(id);

        }

        public class viewholder {
            public ImageView image;
            public TextView name;


            public viewholder(View itemView) {
                image = (ImageView) itemView.findViewById(R.id.chatimage);
                name = (TextView) itemView.findViewById(R.id.chatname);
            }
        }

    }
}
