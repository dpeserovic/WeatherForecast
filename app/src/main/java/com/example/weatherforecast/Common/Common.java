package com.example.weatherforecast.Common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static final String APP_ID = "66bc70b5ef059169cbfb70d0257ae777";
    public static Location current_location = null;
    public static String units = "metric";

    public static String convertUnixToHour(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String formatted = simpleDateFormat.format(date);
        return formatted;
    }

    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d.M. - HH:mm");
        String formatted = simpleDateFormat.format(date);
        return formatted;
    }

    public static String convertUnixToLongDate(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d. MMM E - HH:mm");
        String formatted = simpleDateFormat.format(date);
        return formatted;
    }
}