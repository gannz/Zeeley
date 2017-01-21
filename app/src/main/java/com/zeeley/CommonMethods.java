package com.zeeley;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by gannu on 16-06-2016.
 */
public class CommonMethods {
    static SimpleDateFormat format=new SimpleDateFormat("HH:mm");
    static SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMM-yyyy");

    public static String getCurrentTime() {
        Calendar c=Calendar.getInstance();
        return  format.format(c.getTime());
    }
    public static String  getCurrentDate()  {
        Calendar c = Calendar.getInstance();

        return dateFormat.format(c.getTime());

    }


}
