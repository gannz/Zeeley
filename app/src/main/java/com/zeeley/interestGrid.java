package com.zeeley;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by gannu on 02-08-2016.
 */
public class interestGrid {
    Context context;
    LayoutInflater inflater;
    public static Adapter adapter;

    public void setupGrid(Context c) {
        context = c;
        inflater = LayoutInflater.from(c);
        View layout = inflater.inflate(R.layout.interestgrid, null);
        GridView gridView = (GridView) layout.findViewById(R.id.gridView);
        SQLiteDatabase sqLiteDatabase = new database(c).getWritableDatabase();
        final Cursor cursor = sqLiteDatabase.query(database.TABLE, new String[]{database.ID, database.interest, database.image}, null, null, null, null, null, null);
        adapter = new Adapter(c, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int Id = -1;
                if (cursor.moveToLast()) {
                    Id = cursor.getInt(cursor.getColumnIndex(database.ID));
                }
                if (view.getId() == Id) {
                    Intent intent = new Intent(context, InterestAdder.class);
                    context.startActivity(intent);
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setView(layout);
    }

    class Adapter extends CursorAdapter {

        public Adapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = inflater.inflate(R.layout.itemgrid, parent, false);
            viewholder holder = new viewholder();
            holder.image = (ImageView) view.findViewById(R.id.image);
            holder.image.setImageResource(cursor.getInt(cursor.getColumnIndex(database.image)));
            holder.image.setAdjustViewBounds(true);
            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.image.setPadding(8, 8, 8, 8);
            holder.title = (TextView) view.findViewById(R.id.name);
            holder.title.setText(cursor.getString(cursor.getColumnIndex(database.interest)));
            view.setLayoutParams(new GridView.LayoutParams(90, 90));
            view.setTag(holder);
            view.setId(cursor.getInt(cursor.getColumnIndex(database.ID)));
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            viewholder holder = (viewholder) view.getTag();
            holder.image.setImageResource(cursor.getInt(cursor.getColumnIndex(database.image)));
            holder.title.setText(cursor.getString(cursor.getColumnIndex(database.interest)));
            view.setId(cursor.getInt(cursor.getColumnIndex(database.ID)));
        }
    }

    class viewholder {
        ImageView image;
        TextView title;
    }
}
