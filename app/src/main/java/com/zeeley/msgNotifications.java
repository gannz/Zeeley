package com.zeeley;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.parceler.Parcels;

/**
 * Created by gannu on 01-09-2016.
 */
public class msgNotifications {
    private static PendingIntent pendingIntent;
    private static NotificationManager notificationManager;
    private static NotificationCompat.Builder notificationBuilder;
    private static String default_tone = "content://settings/system/notification_sound";
    private static SharedPreferences sharedPreferences;
    private int color;
    String lightColor;
    private static Uri uri;
    private static final String notificationGroup = "notification_group";

    public static void sendNotification(String pk, String message, long time, Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        uri = Uri.parse(sharedPreferences.getString("NOTIFICATION_SOUND", default_tone));
        String contextText;
        long[] v = {500, 1000};
        if (message.length() > 10) {
            contextText = message.substring(0, 9) + "....";
        } else {
            contextText = message;
        }
        // onlineUser user = intent.getParcelableExtra(USER);

        Intent intent = new Intent(context, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(constants.SOURCE, constants.fromNotification);
        intent.putExtra(constants.BUNDLE,bundle);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);


        notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setContentTitle("zeeley")
                .setSmallIcon(R.drawable.pizza)
                .setContentIntent(pendingIntent)
                .setContentText(constants.notifications.size() + "new  msgs received")
                .setAutoCancel(true)
                        // .setGroup(notificationGroup)
                .setLights(Color.RED, 1000, 800)
                        //.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setShowWhen(true)
                .setWhen(time);
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(constants.notifications.size() + " msgs received");

// Moves events into the expanded layout
        for (int i = 0; i < constants.notifications.size(); i++) {

            inboxStyle.addLine(constants.notifications.get(i));
        }
// Moves the expanded layout object into the notification object.
        notificationBuilder.setStyle(inboxStyle);
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (audio.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                notificationBuilder.setSound(uri);
                notificationBuilder.setVibrate(v);

                break;
            case AudioManager.RINGER_MODE_SILENT:
                // Device is on Silent mode.
                // you should not play sound now.
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                notificationBuilder.setVibrate(v);
                break;
        }
        notificationManager.notify(constants.notificationId, notificationBuilder.build());
    }

    private int setLightColor() {
        String value = sharedPreferences.getString("LED_COLOR", "green");
        if (value.equals("red")) color = Color.RED;
        else if (value.equals("yellow")) color = Color.YELLOW;
        else if (value.equals("blue")) color = Color.BLUE;
        else if (value.equals("purple")) color = Color.MAGENTA;
        else color = Color.GREEN;
        return color;
    }

    private void setVibrateMode() {

    }

    public static void sendMemberAddedNotific(String name, String teamName, String teamId, Context context, long time) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        uri = Uri.parse(sharedPreferences.getString("NOTIFICATION_SOUND", default_tone));
        String contextText;
        long[] v = {500, 1000};
        String msg = name + " added";
        Intent intent = new Intent(context, groupChatScreen.class);
        Bundle b = new Bundle(2);
        dummyGroupProfile groupProfile = new dummyGroupProfile(R.id.userimage, teamName, teamId);
        b.putString(constants.SOURCE, constants.FROM_REGULAR);
        b.putParcelable(constants.Dummy, Parcels.wrap(groupProfile));
        intent.putExtra(constants.BUNDLE, b);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setContentTitle("zeeley")
                .setSmallIcon(R.drawable.pizza)
                .setContentIntent(pendingIntent)
                .setContentText(msg)
                .setAutoCancel(true)
                        // .setGroup(notificationGroup)
                .setLights(Color.RED, 1000, 800)
                        //.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setShowWhen(true)
                .setWhen(time);
        NotificationCompat.BigTextStyle inboxStyle =
                new NotificationCompat.BigTextStyle();
        inboxStyle.setBigContentTitle(name + " added to " + teamName);
        notificationBuilder.setStyle(inboxStyle);
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (audio.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                notificationBuilder.setSound(uri);
                notificationBuilder.setVibrate(v);

                break;
            case AudioManager.RINGER_MODE_SILENT:
                // Device is on Silent mode.
                // you should not play sound now.
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                notificationBuilder.setVibrate(v);
                break;
        }
        notificationManager.notify(constants.notificationId, notificationBuilder.build());
    }

    public static void sendInvitationNotif(Context context, long time, inviteInfo invite) {
        Log.d(constants.zeeley, "inside sendinvite notific");
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent accept = new Intent(context, invitationReciever.class);
        accept.setAction(constants.Accept);
        accept.putExtra(constants.INVITATION, Parcels.wrap(invite));
        Intent reject = new Intent(context, invitationReciever.class);
        reject.setAction(constants.Reject);
        String contentText;
        if (invite.getCurrentInterest() == null) {
            contentText = invite.getTeamName() + " Request";
        } else
            contentText = invite.getCurrentInterest() + " Invitation";

        PendingIntent pendingIntentAccept = PendingIntent.getBroadcast(context, 21, accept, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(context, 24, reject, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intent = new Intent(context, notifications.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 87, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setContentTitle("zeeley")
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.userimg)
                .setAutoCancel(true)
                .setLights(Color.RED, 1000, 800)
                .setShowWhen(true)
                .setWhen(time)
                .addAction(R.drawable.ok, "accept", pendingIntentAccept)
                .addAction(R.drawable.close, "reject", pendingIntentCancel);
        notificationManager.notify(constants.notificationId, notificationBuilder.build());
        Log.d(constants.zeeley, "notic sent");
    }

    public static void sendFormNotif(Context context, long time, String teamName, String senderName, Bitmap profpic, formInfo fi) {
        Log.d(constants.zeeley, "inside sendform notific");
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent accept = new Intent(context, invitationService.class);
        accept.setAction(constants.acceptFormAction);
        accept.putExtra(constants.INVITATION, Parcels.wrap(fi));


        Intent reject = new Intent(context, invitationService.class);
        reject.setAction(constants.rejectFormAction);


        PendingIntent pendingIntentAccept = PendingIntent.getService(context, 21, accept, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentCancel = PendingIntent.getService(context, 24, reject, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intent = new Intent(context, notifications.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 87, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setContentTitle("Join Request")
                .setContentText(senderName + "/n" + teamName)
                .setContentIntent(pendingIntent)
                .setLargeIcon(profpic)
                .setAutoCancel(true)
                .setLights(Color.RED, 1000, 800)
                .setShowWhen(true)
                .setWhen(time)
                .addAction(R.drawable.ok, "join", pendingIntentAccept)
                .addAction(R.drawable.close, "reject", pendingIntentCancel);
        notificationManager.notify(78, notificationBuilder.build());
        Log.d(constants.zeeley, "form join notific sent");
    }
}
