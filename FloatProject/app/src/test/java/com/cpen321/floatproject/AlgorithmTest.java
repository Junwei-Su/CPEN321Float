package com.cpen321.floatproject;

import com.cpen321.floatproject.utilities.Algorithms;
import com.google.android.gms.maps.model.LatLng;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Date;

/**
 * Created by Little_town on 12/22/2016.
 */

public class AlgorithmTest {


    @Test
    public void testCalculateDistance1(){
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

    @Test
    public void testCalculateDistance2(){
        LatLng loc1 = new LatLng(-100,23);
        LatLng loc2 = new LatLng(102.3, -200);
        double expectedResult = 20015;
        double errorMargin = 0.5;

        double distance  = Algorithms.calculateDistance(loc1, loc2);
        if(Math.abs(distance-expectedResult)/distance <= errorMargin){
            Assert.assertTrue(true);
        }
        else{
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testStringToDate1() {
        String str = "1999-10-01";
        Date dtout = Algorithms.string_to_date(str);
        Date dt = new Date(99,9,1);
        if(dtout.equals(dt))
            assert true;
        else
            assert false;
    }

    @Test
    public void testStringToDate2() {
        String str = "1999-10-01";
        Date dtout = Algorithms.string_to_date(str);
        Date dt = new Date(98,9,1);
        if(!dtout.equals(dt))
            assert true;
        else
            assert false;
    }
    @Test
    public void testStringToDate3(){
        String str = "19991001";
        Date dtout = Algorithms.string_to_date(str);
        if(dtout == null)
            assert true;
        else
            assert false;
    }

    @Test
    public void testDateToString1(){
        Date dt = new Date(99,9,1);
        String str = "1999-10-01";
        if(Algorithms.date_to_string(dt).equals(str))
            assert true;
        else
            assert false;
    }

    @Test
    public void testDateToString2(){
        Date dt = new Date(98,9,1);
        String str = "1999-10-01";
        if(!Algorithms.date_to_string(dt).equals(str))
            assert true;
        else
            assert false;
    }
}
