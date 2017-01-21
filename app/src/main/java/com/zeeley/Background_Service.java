package com.zeeley;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.support.v7.app.NotificationCompat.Builder;

public class Background_Service extends Service {
    public static final String USER = "user";
    private PendingIntent pendingIntent;
    private NotificationManager notificationManager;
    private Builder notificationBuilder;
    private String default_tone = "content://settings/system/notification_sound";
    private SharedPreferences sharedPreferences;
    String lightColor;
    Uri uri;

    public Background_Service() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        uri = Uri.parse(sharedPreferences.getString("NOTIFICATION_SOUND", default_tone));

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    private void setLightColor() {
        String value = sharedPreferences.getString("LED_COLOR", "green");

    }

    private void setVibrateMode() {

    }

    private void sendNotification(Intent intent, String message, long time) {

        String contextText;
        long[] v = {500, 1000};
        if (message.length() > 10) {
            contextText = message.substring(0, 9) + "....";
        } else {
            contextText = message;
        }
        onlineUser user = intent.getParcelableExtra(USER);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notificationBuilder = (Builder) new Builder(this)
                .setContentTitle(user.getName())
               // .setSmallIcon(user.getImage())
                .setContentIntent(pendingIntent)
                .setContentText(contextText)
                .setAutoCancel(true)
                .setLights(Color.RED, 1000, 800)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setShowWhen(true)
                .setWhen(time);
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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
        notificationManager.notify(1, notificationBuilder.build());
    }

    class task extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            /*
            continously check for msgs..if received check id in application class..if matches update adapter,update chatslist,update db
            if doesnt match send notification,update chatslist,update db


             */
            return null;
        }
    }
}
