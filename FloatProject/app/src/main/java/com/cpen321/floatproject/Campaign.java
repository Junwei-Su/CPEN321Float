package com.cpen321.floatproject;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

/*
 * this is representation of a Campaign object in our float project
 */
public class Campaign {

    //fields in database
    private String name;
    private User owner;
    private String description;
    private String charity;
    private Location initial_location;
    private Location destination;
    private int pledge_amount;
    private int time_left;
    private int accumulated_donation;

    //extra fields
    //List<Action> list_of_location  = new LinkedList<Action>();
    List<LatLng> list_of_locations = new LinkedList<LatLng>();

    public Campaign(){}

    public Campaign(String title, User owner, String description, String charity, Location init, Location dest, int pledge_amount, int time_left,
                    int accumulated_donation){
        this.name = title;
        this.owner = owner;
        this.description = description;
        this.initial_location = init;
        this.destination = dest;
        this.pledge_amount = pledge_amount;
        this.charity = charity;
        this.time_left = time_left;
        this.accumulated_donation = accumulated_donation;
    }

    public String returnName(){
        return this.name;
    }

    public User returnOwner(){
        return this.owner;
    }

    public String returnDescription(){
        return this.description;
    }

    public Location returnInitial_location(){
        return this.initial_location;
    }

    public Location returnDestination(){
        return this.destination;
    }

    public int returnPledge_amount(){
        return this.pledge_amount;
    }
}
