package com.cpen321.floatproject;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.ArrayList;

public class Campaign {

    public String accumulated_donation;
    public String campaign_name;
    public String charity;
    public String description;
    public String destination;
    public String goal_amount;
    public LatLng initial_location;
    public LatLng dest_location;
    public List<LatLng> list_locations;
    public String owner_account;
    public String time_left;
    public int status; //0 if campaign is in progress,
    // 1 if campaign succeeded,
    //3 when campaign is finished (campaign succeeded and payment completed) or campaign failed
    public String campaign_pic = "lighthouse.png"; //TODO remove hardcoded default value

    public Campaign() {
    }

    public Campaign(String accumulated_donation, String campaign_name, String charity, String description,
                    String destination, String goal_amount, Double start_lat, Double start_long, Double end_lat, Double end_long, String owner_account,
                    String time_left, int status) {
        this.accumulated_donation = accumulated_donation;
        this.campaign_name = campaign_name;
        this.charity = charity;
        this.description = description;
        this.destination = destination;
        this.goal_amount = goal_amount;
        this.owner_account = owner_account;
        this.time_left = time_left;
        this.initial_location = new LatLng(start_lat, start_long);
        this.dest_location = new LatLng(end_lat, end_long);
        list_locations = new ArrayList<LatLng>();
        list_locations.add(initial_location);
        this.status = status;
    }
}



