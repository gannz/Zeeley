package com.zeeley;

import android.graphics.Bitmap;
import android.location.Location;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcel;

/**
 * Created by gannu on 03-07-2016.
 */
@Parcel
public class onlineUser {
    String name, location, interest, gender, age, phonenumber;
    int kilometers;
    int meters;
    Double latitude;
    long id;
    Double longitude;
    String primaryKey;
    String na="N/A";
    Bitmap image;
    settingsObj settings;
    String first_Name;
    String lastName;

    public String getFirst_Name() {
        int i=name.indexOf(" ");
        return name.substring(0,i-1);
    }

    public void setFirst_Name(String first_Name) {
        this.first_Name = first_Name;
    }

    public String getLastName() {
        int i=name.indexOf(" ");
        if(i>0)
        return name.substring(i+1,name.length()-1);
        else return "";
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public settingsObj getSettings() {
        return settings;
    }



    public onlineUser(Double latitude, Double longitude, String name, String interest,settingsObj settings, long id,Bitmap image) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.interest = interest;
        this.id = id;
        this.image=image;
        this.settings=settings;
    }

    public onlineUser() {

    }

    public long getId() {
        return id;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getDistance() {
        if(constants.MY_LONGITUDE!=null&&constants.MY_LATITUDE!=null){
            Location l1 = new Location("my_location");
            l1.setLatitude(constants.MY_LATITUDE);
            l1.setLongitude(constants.MY_LONGITUDE);

            Location l2 = new Location("onlineUser_location");
            l2.setLatitude(latitude);
            l2.setLongitude(longitude);

            float distance = l1.distanceTo(l2);
            if(!settings.isDistanceKnown()){
                distance+=30;
            }
            String dist = distance + " M";
            if (distance > 1000.0f) {
                distance = distance / 1000.0f;
                dist = distance + " KM";
            }
           return dist;
        }
        return na;

    }


    public String getInterest() {
        return interest;
    }

    public String getLocation() {
        return na;
    }

    public String getGender() {
        return gender;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getAge() {
        return age;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
