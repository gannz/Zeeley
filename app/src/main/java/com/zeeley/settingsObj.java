package com.zeeley;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.parceler.Parcel;

import java.net.HttpURLConnection;
import java.net.URL;

@Parcel
public class settingsObj {

    public boolean isLocationAccessible = true;
    public boolean isDistanceKnown = true;
    public boolean canMessage = true;
    public boolean canInvite = true;
    public boolean canViewProfile = true;
    public boolean canAccessLocation = true;
    public String settingsString;

    public settingsObj(){}

    public settingsObj(String setting) {
        char[] settings = setting.toCharArray();
        isLocationAccessible = settings[0] == '1' ? false : true;
        isDistanceKnown = settings[1] == '1' ? false : true;
        canMessage = settings[4] == '1' ? false : true;
        canInvite = settings[5] == '1' ? false : true;
        canViewProfile = settings[2] == '1' ? false : true;
        canAccessLocation = settings[3] == '1' ? false : true;
        this.settingsString=setting;
    }

    public String getSettingsString() {
        return settingsString;
    }

    public void setIsLocationAccessible(boolean isLocationAccessible) {
        this.isLocationAccessible = isLocationAccessible;
    }

    public void setIsDistanceKnown(boolean isDistanceKnown) {
        this.isDistanceKnown = isDistanceKnown;
    }

    public void setCanMessage(boolean canMessage) {
        this.canMessage = canMessage;
    }

    public void setCanInvite(boolean canInvite) {
        this.canInvite = canInvite;
    }

    public void setCanViewProfile(boolean canViewProfile) {
        this.canViewProfile = canViewProfile;
    }

    public void setCanAccessLocation(boolean canAccessLocation) {
        this.canAccessLocation = canAccessLocation;
    }

    public boolean isLocationAccessible() {
        return isLocationAccessible;
    }

    public boolean isDistanceKnown() {
        return isDistanceKnown;
    }

    public boolean CanMessage() {
        return canMessage;
    }

    public boolean CanInvite() {
        return canInvite;
    }

    public boolean CanViewProfile() {
        return canViewProfile;
    }

    public boolean CanAccessLocation() {
        return canAccessLocation;
    }

    public void Timer(long time) {
        Thread t = new Thread(new TimeOut(time));
        t.start();
    }

    private void removeTimer() {
        //TODO: Remove timer from timer table
    }


    private class TimeOut implements Runnable {

        private long time;

        public TimeOut(long time) {
            this.time = time;

        }

        @Override
        public void run() {
            try {
                for (long currentTime; (currentTime = System.currentTimeMillis()) < time; ) {
                    canMessage = true;
                    Thread.sleep(time - currentTime);
                }
            } catch (Exception e) {
                Log.e("fucked", e.getMessage());
            } finally {
                removeTimer();
            }
        }
    }
}
