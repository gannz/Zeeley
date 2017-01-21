package com.zeeley;

import android.graphics.Bitmap;

/**
 * Created by gannu on 14-09-2016.
 */
public class groupParticipant {
    private String groupId,name,pk,settings;
    private Bitmap image;

    public groupParticipant(String groupId, String name, String pk, Bitmap image,String settings) {
        this.groupId = groupId;
        this.name = name;
        this.pk = pk;
        this.image = image;
        this.settings=settings;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getSettings() {
        return settings;
    }

    public String getName() {
        return name;
    }

    public String getPk() {
        return pk;
    }

    public Bitmap getImage() {
        return image;
    }
}
