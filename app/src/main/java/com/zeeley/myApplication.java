package com.zeeley;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

/**
 * Created by gannu on 27-08-2016.
 */
public class myApplication extends Application {
    @Override
    public void onCreate() {

        super.onCreate();
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
        nMgr.cancel(constants.notificationId);
        constants.notifications.clear();
        CookieManager manager=new CookieManager(new PersistentCookieStore(getApplicationContext()), CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
    }
}
