package com.zeeley;

import android.graphics.Bitmap;

import org.parceler.Parcel;

/**
 * Created by gannu on 09-09-2016.
 */
@Parcel
public class dummyProfile {
    public String name, id;
    public String settings;
    Bitmap image;
    public dummyProfile() {

    }

    public dummyProfile(String name, String id, String settings, Bitmap image) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.settings = settings;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
    public Bitmap getImage() {
        return image;
    }

    public String getSettings() {
        return settings;
    }
}
