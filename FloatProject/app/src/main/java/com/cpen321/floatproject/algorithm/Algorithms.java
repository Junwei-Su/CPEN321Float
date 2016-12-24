package com.cpen321.floatproject.algorithm;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by clarence on 2016-11-25.
 */

public class Algorithms {

    /*
    *  calculate the distance between two LatLng objects in kilometers
     */
    public static double calculateDistance(LatLng loc1, LatLng loc2){

        double lat1 = loc1.latitude;
        double lat2 = loc2.latitude;
        double lng1 = loc1.longitude;
        double lng2 = loc2.longitude;

        double earthRadius = 6371; //in kilometers
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        return dist;
    }


    /*
    * convert yyyy-mm-dd date string to date object
     */
    public static Date string_to_date(String dateString){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /*
    * convert yyyy-mm-dd date to date string
     */
    public static String date_to_string(Date date){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String strDate = format.format(date);
        return strDate;
    }
}