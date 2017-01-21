package com.zeeley;


import android.util.Log;

import org.parceler.Parcel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gannu on 23-09-2016.
 */
@Parcel
public class Trip {
    public String tripType;
    public String heading;
    public String remark;
    public String date_from;
    public int trip_id,adminId;
    public String trip_city;
    public String trip_to;
    public String trip_city_from;


    public Trip(String tripType, String heading, String remark, String trip_city, int trip_id, String date_from, String trip_to,int adminId) {
        this.tripType = tripType;
        this.trip_to = trip_to;
        this.date_from = date_from;
        this.heading = heading;
        this.remark = remark;
        this.trip_city = trip_city;
        this.trip_id = trip_id;
        this.adminId=adminId;
    }

    public Trip() {

    }

    public int getAdminId() {
        return adminId;
    }

    public String getTripType() {
        return tripType;
    }

    public String getHeading() {
        return heading;
    }

    public String getRemark() {
        return remark;
    }

    public Date getStartDate() {

        return parseDate(date_from);
    }

    public String getTrip_city_from() {
        return trip_city_from;
    }

    public void setTrip_city_from(String trip_city_from) {
        this.trip_city_from = trip_city_from;
    }

    public String getTrip_city() {
        return trip_city;
    }// where one place is needed

    public String getEndDate() {
        return trip_to;
    }

    public int getTrip_id() {
        return trip_id;
    }

    public static Date parseDate(String d) {
        String date = d.substring(0, d.indexOf('T'));
        String time = d.substring(d.indexOf('T') + 1, d.indexOf('Z'));
        Log.d(constants.zeeley, "inchatactivity parsedate date is " + date);
        Log.d(constants.zeeley, "inchatactivity parsedate time is " + time);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt;

        try {

            dt = df.parse(date + " " + time);
            Log.d(constants.zeeley, "in trip parsedate date object is not null  ");
            return dt;
        } catch (ParseException e) {
            Log.w(constants.zeeley, "trip parsedate date object is null ");
            e.printStackTrace();
        }

        Log.w(constants.zeeley, "trip parsedate returning null for date obj ");
        return null;
    }

}


