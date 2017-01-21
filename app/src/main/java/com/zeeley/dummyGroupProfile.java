package com.zeeley;

import org.parceler.Parcel;

/**
 * Created by gannu on 14-09-2016.
 */

@Parcel
public class dummyGroupProfile {

    public int image;
    public String name, id;

    public dummyGroupProfile() {
    }

    public dummyGroupProfile(int image, String name, String id) {
        this.image = image;
        this.name = name;
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
