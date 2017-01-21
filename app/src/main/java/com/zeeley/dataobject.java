
package com.zeeley;

import android.graphics.Bitmap;
import android.location.Location;
import android.widget.ImageView;

import org.parceler.Parcel;

@Parcel
public class dataobject {
    public String name, date, lastmessage, time, interest;

    // onlineUser user;
    String id;
    settingsObj settings;
    String na = "N/A";
    Double latitude, longitude;
    boolean colorMsg = false;
    public boolean isGroup;
    public dataobject() {

    }

    public dataobject(String name, String date, String lastmessage,String id, settingsObj settings,boolean isGroup) {
        this.name = name;
        this.date = date;
        this.lastmessage = lastmessage;
        this.id = id;
        this.settings = settings;
        this.isGroup=isGroup;
    }

    public boolean isColorMsg() {
        return colorMsg;
    }

    public void setColorMsg(boolean colorMsg) {
        this.colorMsg = colorMsg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public settingsObj getSettings() {
        return settings;
    }

    public void setSettings(settingsObj settings) {
        this.settings = settings;
    }

    public String getId() {
        return id;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setId(String id) {
        this.id = id;
    }

   /* public dataobject(String name, String date, String lastmessage, Bitmap profile_pic, onlineUser user) {
        this.name = name;
        this.date = date;
        this.lastmessage = lastmessage;
        this.user = user;
        this.profile_pic = profile_pic;
    }*/

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    /* public long getId() {
          return id;
      }

      public void setId(long id) {
          this.id = id;
      }

      public long getServer_id() {
          return server_id;
      }

      public void setServer_id(long server_id) {
          this.server_id = server_id;
      }*/
    public String getDistance() {
        if (constants.MY_LONGITUDE != null && constants.MY_LATITUDE != null) {
            Location l1 = new Location("my_location");
            l1.setLatitude(constants.MY_LATITUDE);
            l1.setLongitude(constants.MY_LONGITUDE);

            Location l2 = new Location("onlineUser_location");
            l2.setLatitude(latitude);
            l2.setLongitude(longitude);

            float distance = l1.distanceTo(l2);
            if (!settings.isDistanceKnown()) {
                distance += 30;
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


}