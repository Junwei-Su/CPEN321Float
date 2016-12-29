package com.cpen321.floatproject.campaigns;

import com.cpen321.floatproject.algorithm.Algorithms;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Little_town on 12/21/2016.
 */

public class  DestinationCampaign extends Campaign {

    private String destination;
    private LatLng dest_location;
    final int RADIUS = 20; //in kilometer

    //without existing location list
    public DestinationCampaign(String campaign_name, long accumulated_donation, String charity, String description,
                               long goal_amount, LatLng initial_location, String owner_account,
                               long time_length, String initial_date, String destination, LatLng dest_location,
                                String campaign_pic){

        //call constructor from super class
        super(accumulated_donation, campaign_name, charity, description, goal_amount,
                initial_location, owner_account, time_length, initial_date, campaign_pic);

        //add new field
        this.destination = destination;
        this.dest_location = dest_location;

    }

    ////with existing location list
    public DestinationCampaign(String campaign_name, long accumulated_donation, String charity, String description,
                               long goal_amount, LatLng initial_location, String owner_account,
                               long time_length, String initial_date, String destination, LatLng dest_location, List<LatLng> list_of_location
                                ,String campaign_pic){

        //call constructor from super class
        super(accumulated_donation, campaign_name, charity, description, goal_amount,
                initial_location, owner_account, time_length, initial_date, list_of_location, campaign_pic);

        //add new field
        this.destination = destination;
        this.dest_location = dest_location;

    }

    public String getDestination(){
        return this.destination;
    }

    public LatLng getDest_location(){
        return this.dest_location;
    }

    public int getRADIUS(){
        return this.RADIUS;
    }

    /*
    * return the status of the campaign and represent it as an int:
    *   statusï¼š 0 = in progress
    *            1 = expire
    *            2 = succeed
     */
    public int returnStatus(){
        int in_progress = 0;
        int expire = 1;
        int successful = 2;


        for(LatLng loc : this.getList_locations()){
            if(Algorithms.calculateDistance(loc, this.getDest_location())<=this.RADIUS){
                return successful;
            }
        }

        //check if expire
        //get current date
        Date current_date = new Date();
        Date initial_date = Algorithms.string_to_date(this.getInitial_date());
        long difference  = current_date.getTime() - initial_date.getTime();
        long time_left = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
        if(time_left <=0 ){
            return expire;
        }

        return in_progress;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("accumulated_donation", this.getAccumulated_donation());
        result.put("campaign_name", this.getCampaign_name());
        result.put("charity", this.getCharity());
        result.put("description", this.getDescription());
        result.put("goal_amount", this.getGoal_amount());
        result.put("initial_date", this.getInitial_date());
        result.put("initial_location", this.getInitial_location());
        result.put("list_locations", this.getList_locations());
        result.put("owner_account", this.getOwner_account());
        result.put("time_length", this.getTime_length());
        result.put("campaign_pic", this.getCampaign_pic());
        result.put("destination", this.getDestination());
        result.put("dest_location", this.getDest_location());

        return result;
    }
}
