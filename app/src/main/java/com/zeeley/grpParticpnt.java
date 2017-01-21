package com.zeeley;

import android.graphics.Bitmap;

/**
 * Created by gannu on 19-09-2016.
 */
public class grpParticpnt {
    String name, pk, settings;
    Bitmap image;

    public grpParticpnt(String name, String pk, String settings) {
        this.name = name;
        this.pk = pk;
        this.settings = settings;
    }

    public grpParticpnt(String name, String pk, String settings, Bitmap image) {
        this.name = name;
        this.pk = pk;
        this.settings = settings;
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getPk() {
        return pk;
    }

    public String getSettings() {
        return settings;
    }
}
