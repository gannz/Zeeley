package com.zeeley;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by gannu on 15-08-2016.
 */
public class blockedItem {
    String name;
    ArrayList<String> blockedFrom;
    Bitmap image;
    int id;
    public blockedItem(String name,Bitmap image,int id){
        this.name=name;
        this.image=image;
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getBlockedFrom() {
        return blockedFrom;
    }

    public int getId() {
        return id;
    }

    public void setBlockedFrom(ArrayList<String> blockedFrom) {
        this.blockedFrom = blockedFrom;
    }

    public Bitmap getImage() {
        return image;
    }
}
