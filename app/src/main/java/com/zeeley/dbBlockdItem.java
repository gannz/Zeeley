package com.zeeley;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by gannu on 11-11-2016.
 */
public class dbBlockdItem {
    String name;
    Bitmap image;
    int id;
    boolean CanInvite,CanViewProfile,CanMessage,CanAccessLocation;

    public dbBlockdItem(String name, Bitmap image, int id, boolean canInvite, boolean canViewProfile, boolean canMessage, boolean canAccessLocation) {
        this.name = name;
        this.image = image;
        this.id = id;
        CanInvite = canInvite;
        CanViewProfile = canViewProfile;
        CanMessage = canMessage;
        CanAccessLocation = canAccessLocation;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public boolean isCanInvite() {
        return CanInvite;
    }

    public boolean isCanViewProfile() {
        return CanViewProfile;
    }

    public boolean isCanMessage() {
        return CanMessage;
    }

    public boolean isCanAccessLocation() {
        return CanAccessLocation;
    }
}
