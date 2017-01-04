package com.cpen321.floatproject;

import com.cpen321.floatproject.campaigns.DestinationCampaign;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.util.ArrayList;
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
        list_of_location.add(new LatLng(0,0));
        DestinationCampaign test = new DestinationCampaign(
                "CampName", 10, "Charity", "Destination",
        20, new LatLng(0,0), "Owner_Account", 10, "2016-10-01", "Destination", new LatLng(0,0), list_of_location,"CampPic");
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
        DestinationCampaign test = new DestinationCampaign(
                "CampName", 10, "Charity", "Destination",
                20, new LatLng(0,0), "Owner_Account", 10, "2016-10-01", "Destination", new LatLng(50,50), list_of_location,"CampPic");
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


}
