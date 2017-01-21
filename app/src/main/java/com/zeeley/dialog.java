package com.zeeley;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import java.lang.reflect.Method;

/**
 * Created by gannu on 16-07-2016.
 */
public class dialog {

    public static void clearchats(Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Clear ALL chats?");
        builder.setMessage("This deletes all messages from all chats");
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
               // clear database
            }
        });
        builder.setCancelable(true);
        builder.create().show();

    }
    public static void deleteChats(final Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Delete ALL chats?");
        builder.setMessage("This deletes all the chats and their messages");
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
                SharedPreferences sharedPreferences=c.getSharedPreferences("GET_CHATSFILE",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor =sharedPreferences.edit();
                editor.clear();
                editor.commit();
            }
        });
        builder.setCancelable(true);
        builder.create().show();

    }
}
