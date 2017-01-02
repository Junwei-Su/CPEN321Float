package com.cpen321.floatproject.campaigns;

import com.cpen321.floatproject.utilities.Algorithms;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Campaign {

    //campaign field
    private long accumulated_donation;
    private String campaign_name;
    private String charity;
    private String description;
    private long goal_amount;
    private String initial_date;
    private LatLng initial_location;
    private List<LatLng> list_locations;
    private String owner_account;
    private long time_length; //time_length holds the initial starting time in milliseconds. Length of campaign is hard coded to 4 days
    private String campaign_pic = "default_camp_pic.jpg"; //default campaign picture

    public Campaign() {
    }

    public Campaign(long accumulated_donation, String campaign_name, String charity, String description,
                    long goal_amount, LatLng initial_location, String owner_account,
                    long time_length,String initial_date,String campaign_pic) {
        this.accumulated_donation = accumulated_donation;
        this.campaign_name = campaign_name;
        this.charity = charity;
        this.description = description;
        this.goal_amount = goal_amount;
        this.owner_account = owner_account;
        this.time_length = time_length;
        this.initial_location = initial_location;
        list_locations = new ArrayList<LatLng>();
        list_locations.add(initial_location);
        this.initial_date = initial_date;
        this.campaign_pic = campaign_pic;
    }

    public Campaign(long accumulated_donation, String campaign_name, String charity, String description,
                    long goal_amount, LatLng initial_location, String owner_account,
                    long time_length,String initial_date, List<LatLng> list_of_location,String campaign_pic ) {
        this.accumulated_donation = accumulated_donation;
        this.campaign_name = campaign_name;
        this.charity = charity;
        this.description = description;
        this.goal_amount = goal_amount;
        this.owner_account = owner_account;
        this.time_length = time_length;
        this.initial_location = initial_location;
        list_locations = list_of_location;
        list_locations.add(initial_location);
        this.initial_date = initial_date;
        this.campaign_pic = campaign_pic;
    }

    public long getAccumulated_donation(){
        return this.accumulated_donation;
    }

    public void add_donation(long newDonation){
        this.accumulated_donation += newDonation;
    }

    public String getCampaign_name(){
        return this.campaign_name;
    }

    public String getCharity(){
        return this.charity;
    }

    public long getGoal_amount(){
        return this.goal_amount;
    }

    public void setGoal_amount(long new_goal){
        this.goal_amount = new_goal;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String new_description){
        this.description = new_description;
    }

    public LatLng getInitial_location(){
        return this.initial_location;
    }

    public List<LatLng> getList_locations(){
        return this.list_locations;
    }

    public void add_location(LatLng newLoc){
        Double thresHold = 1.0;
        LatLng zero_loc = new LatLng(0.0, 0.0);

        if(Algorithms.calculateDistance(zero_loc, newLoc)<= thresHold) return;

        for (LatLng loc: this.list_locations){
            if(Algorithms.calculateDistance(loc, newLoc)<= thresHold) {
                return;
            }
        }
        this.list_locations.add(newLoc);
    }

    public String getOwner_account(){
        return this.owner_account;
    }

    public String getCampaign_pic(){
        return this.campaign_pic;
    }

    public void setCampaign_pic(String newPic){
        this.campaign_pic = newPic;
    }

    public long getTime_length(){
        return this.time_length;
    }

    public String getInitial_date(){
        return this.initial_date;
    }

}



