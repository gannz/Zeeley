package com.zeeley;

import android.location.Location;

/**
 * Created by gannu on 07-09-2016.
 */
public class profileObj {

    String gender,contactNo,currentInterest,lat,longit, pk,first_name,last_name,na="N/A";
    int age;
    settingsObj settings;

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getPk() {
        return pk;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }
    public String getDistance() {
        //// TODO: 09-09-2016 store lat and long of user in shardpref and use them if couldnt get gps on
        if(constants.MY_LONGITUDE!=null&&constants.MY_LATITUDE!=null){
            Location l1 = new Location("my_location");
            l1.setLatitude(constants.MY_LATITUDE);
            l1.setLongitude(constants.MY_LONGITUDE);

            Location l2 = new Location("onlineUser_location");
            l2.setLatitude(Double.valueOf(lat));
            l2.setLongitude(Double.valueOf(longit));

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

    public profileObj(String pk,String first_name,String last_name,String gender,
                      String currentInterest, String lat, String longit, int age,settingsObj settings) {
        this.pk=pk;
        this.first_name=first_name;
        this.last_name=last_name;

        this.gender = gender;
        this.contactNo = contactNo;
        this.currentInterest = currentInterest;
        this.lat = lat;
        this.longit = longit;
        this.age = age;
        this.settings=settings;
    }

    public settingsObj getSettings() {
        return settings;
    }

    public String getGender() {
        return gender;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getCurrentInterest() {
        return currentInterest;
    }

    public String getLat() {
        return lat;
    }

    public String getLongit() {
        return longit;
    }

    public int getAge() {
        return age;
    }

    public String getName(){
        if(last_name!=null)
        return first_name+" "+last_name;
        else return first_name;
    }

    public String getLocation() {
        return na;
    }
}
