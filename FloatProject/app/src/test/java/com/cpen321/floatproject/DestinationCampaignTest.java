package com.cpen321.floatproject;

import com.cpen321.floatproject.campaigns.DestinationCampaign;
import com.cpen321.floatproject.utilities.Algorithms;
import com.cpen321.floatproject.utilities.UtilityMethod;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Richard on 1/3/2017.
 */

public class DestinationCampaignTest {
    private final int success = 2;
    private final int inProgress = 0;
    private final int expire = 1;


    //returnStatus() should return 2 when the original location is the same as the destination.
    @Test
    public void returnStatusTestOriginal() {
        List<LatLng> list_of_location = new ArrayList<LatLng>();
        list_of_location.add(new LatLng(0.0,0.0));
        DestinationCampaign test = new DestinationCampaign(
                "CampName", 10, "Charity", "Destination",
        20, new LatLng(0.0,0.0), "Owner_Account", 10, "2016-10-01", "Destination", new LatLng(0,0), list_of_location,"CampPic");
        if(test.returnStatus() == success)
            assert true;
        else
            assert false;
    }

    //returnStatus() should return 0 because it's in progress
    @Test
    public void returnStatusTestInProgress(){
        List<LatLng> list_of_location = new ArrayList<LatLng>();
        list_of_location.add(new LatLng(0,0));
        Date currentDate = new Date();
        String date = Algorithms.date_to_string(currentDate);
        DestinationCampaign test = new DestinationCampaign(
                "CampName", 10, "Charity", "Destination",
                20, new LatLng(0,0), "Owner_Account", System.currentTimeMillis(), date , "Destination", new LatLng(50,50), list_of_location,"CampPic");
        if(test.returnStatus() == inProgress)
            assert true;
        else
            assert false;
    }

    //returnStatus() should return 1 because the campaign is expired.
    @Test
    public void returnStatusTestExpire(){
        List<LatLng> list_of_location = new ArrayList<LatLng>();
        list_of_location.add(new LatLng(0,0));
        DestinationCampaign test = new DestinationCampaign(
                "CampName", 10, "Charity", "Destination",
                20, new LatLng(0,0), "Owner_Account", 10, "2018-10-01", "Destination", new LatLng(50,50), list_of_location,"CampPic");
        if(test.returnStatus() == expire)
            assert true;
        else
            assert false;
    }

    //returnStatus() should success when one of the location is close to the destination location.
    @Test
    public void returnStatusSuccessLong(){
        List<LatLng> list_of_location = new ArrayList<LatLng>();
        list_of_location.add(new LatLng(0.0,0.0));
        list_of_location.add(new LatLng(10,5));
        list_of_location.add(new LatLng(15,20));
        list_of_location.add(new LatLng(25,35));
        list_of_location.add(new LatLng(30,45));
        list_of_location.add(new LatLng(50,50));
        DestinationCampaign test = new DestinationCampaign(
                "CampName", 10, "Charity", "Destination",
                20, new LatLng(0.0,0.0), "Owner_Account", System.currentTimeMillis(), "2016-10-01", "Destination", new LatLng(50,50), list_of_location,"CampPic");
        if(test.returnStatus() == success)
            assert true;
        else
            assert false;
    }

    //returnStatus() should success even though it's expired but one location is close to the destination.
    @Test
    public void returnStatusSuccessOverExpire(){
        List<LatLng> list_of_location = new ArrayList<LatLng>();
        list_of_location.add(new LatLng(0,0));
        list_of_location.add(new LatLng(10,5));
        list_of_location.add(new LatLng(15,20));
        list_of_location.add(new LatLng(25,35));
        list_of_location.add(new LatLng(30,45));
        list_of_location.add(new LatLng(50,50));
        DestinationCampaign test = new DestinationCampaign(
                "CampName", 10, "Charity", "Destination",
                20, new LatLng(0,0), "Owner_Account", System.currentTimeMillis(), "2018-10-01", "Destination", new LatLng(50,50), list_of_location,"CampPic");
        if(test.returnStatus() == success)
            assert true;
        else
            assert false;
    }






}
