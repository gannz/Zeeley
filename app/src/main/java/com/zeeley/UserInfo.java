package com.zeeley;

import android.graphics.Bitmap;

/**
 * Created by Kushagra on 03-07-2016.
 */
public class UserInfo {

    private String first_name;
    private String last_name;
    private String interest;
    private String latitude;
    private String longitude;
    private String primaryKey;
    private String setting;
    //private Bitmap image;

    /*public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {

        return image;
    }*/

    /*public String getLast_msg() {
        return last_msg;
    }

    public void setLast_msg(String last_msg) {
        this.last_msg = last_msg;
    }*/

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }



    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getInterest() {
        return interest;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }



    public String getPrimaryKey() {
        return primaryKey;
    }
}
