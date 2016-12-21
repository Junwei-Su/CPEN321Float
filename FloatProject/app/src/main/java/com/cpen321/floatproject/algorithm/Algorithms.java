package com.cpen321.floatproject.algorithm;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by clarence on 2016-11-25.
 */

public class Algorithms {

    /*
    *  calculate the distance between two LatLng objects in kilometers
     */
    public static double calculateDistance(LatLng loc1, LatLng loc2){
        double MILE_TO_KILOMETER_RATIO = 1.609344;
        double theta = loc1.longitude - loc2.longitude;
        double dist = Math.sin(deg_to_rad(loc1.latitude)) * Math.sin(deg_to_rad(loc2.latitude))
                + Math.cos(deg_to_rad(loc1.latitude))
                * Math.cos(deg_to_rad(loc2.latitude))
                * Math.cos(deg_to_rad(theta));
        dist = Math.acos(dist);
        dist = deg_to_rad(dist);
        dist = dist * 60 * 1.1515 * MILE_TO_KILOMETER_RATIO;
        return dist;
    }

    /*
    * convert degree to radius
     */
    private static double deg_to_rad(double deg) {
        double DEGREE_PER_PI = 180.0;
        return (deg * Math.PI / DEGREE_PER_PI);
    }
}