package com.cpen321.floatproject;

import com.cpen321.floatproject.algorithm.Algorithms;
import com.google.android.gms.maps.model.LatLng;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Date;

/**
 * Created by Little_town on 12/22/2016.
 */

public class AlgorithmTest {
    @Test
    public void testStringDateConverter(){
        String dateString = "2016-12-24";
        Date result = Algorithms.string_to_date(dateString);
        Assert.assertEquals(2016, result.getYear());
    }

    @Test
    public void testCalculateDistance(){
        LatLng loc1 = new LatLng(0,0);
        LatLng loc2 = new LatLng(49.0, -123.0);
        double expectedResult = 12340.0;
        double errorMargin = 0.05;

        double distance  = Algorithms.calculateDistance(loc1, loc2);
        if(Math.abs(distance-expectedResult)/distance <= errorMargin){
            Assert.assertTrue(true);
        }
        else{
            Assert.assertTrue(false);
        }
    }
}
