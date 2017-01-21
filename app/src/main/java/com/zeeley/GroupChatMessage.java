package com.zeeley;

import android.graphics.Bitmap;
import android.util.Log;

import org.parceler.Parcel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gannu on 14-09-2016.
 */

@Parcel
public class GroupChatMessage {
    public Boolean isMine;
    public String Name;
    public String teamID;
    public String time;
    public String message;
    public String date;
    public Bitmap image;
    // public String dbDate;
    // public String dbTime;
    public boolean isDate = false;
    public boolean isImage = false;

    public boolean isDate() {
        return isDate;
    }

    public void setIsDate(boolean isDate) {
        this.isDate = isDate;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setIsImage(boolean isImage) {
        this.isImage = isImage;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public GroupChatMessage() {
    }

   /* public GroupChatMessage(String date, Boolean isMine, String message, String time) {
        this.date = date;
        this.isMine = isMine;
        this.message = message;
        this.time = time;

    }*/

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public GroupChatMessage(String teamID, String message, String time, String date, String name, Boolean isMine) {
        // this contructor used for storing msgs in db
        this.message = message;
        this.teamID = teamID;
        this.time = time;
        this.date = date;
        this.Name = name;
        this.isMine = isMine;
    }

    public String getUnparsedTime() {
        return time;
    }


    public String getName() {
        return Name;
    }

    public String getMessage() {
        return message;
    }

    public String getTeamID() {
        return teamID;
    }

   /* public String getTime() {
        if (time.contains("T") && time.contains("Z"))
            return CommonMethods.format.format(parseDate(time));
        else return time;
    }*/

    public String getTimeForDB() {
        if (time.contains("T") && time.contains("Z"))
            return time;
        else return time;
    }

   /* public String getDate() {
        return CommonMethods.dateFormat.format(parseDate(time));
    }*/

    public String getTime(){
        return time;
    }

    public String getDate() {
        return date;
    }

    public Boolean isMine() {

        return this.isMine;
    }

    public static Date parseDate(String d) {
        Log.d(constants.zeeley, "exec parsedate in grpchtmsg obj");
        Log.d(constants.zeeley, "unparse time cald in date is " + d);
        String date = d.substring(0, d.indexOf('T'));
        String time = d.substring(d.indexOf('T') + 1, d.indexOf('Z'));

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date dt;

        try {
            dt = df.parse(date + " " + time);

            return dt;
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return null;
    }

}
