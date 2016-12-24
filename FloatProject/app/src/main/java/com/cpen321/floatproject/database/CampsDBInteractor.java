package com.cpen321.floatproject.database;

import android.util.Log;

import com.cpen321.floatproject.algorithm.Algorithms;
import com.cpen321.floatproject.Campaign;
import com.google.firebase.database.DataSnapshot;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by clarence on 2016-11-24.
 */

public class CampsDBInteractor implements Readable, Writable {

    /*
    *   This method read a Campaign object from database with the campaign ID
    *   @param:
    *       ID: the unique ID of the campaign object in database
    *       dataSnapshot: dataSnapshot of all campaigns object
    *   @return:
    *       Campaign object search for
     */
    @Override
    public Campaign read(String ID, DataSnapshot dataSnapshot) {

        //get the dataSnapshot of the campaign object with this ID
        DataSnapshot camp_snap =  dataSnapshot.child(ID);

        //if campaign no exist;
//        if(){
//
//        }

        //retrive the camp information
        String accumulated_donation = camp_snap.child("accumulated_donation").getValue().toString();
        String campaign_name = (String)camp_snap.child("campaign_name").getValue();
        String charity = (String)camp_snap.child("charity").getValue();
        String description = (String)camp_snap.child("description").getValue();
        String destination = (String)camp_snap.child("destination").getValue();
        String goal_amount = camp_snap.child("goal_amount").getValue().toString();

        LatLng initial_location =  dataSnapshotToLatLng(camp_snap.child("initial_location"));
        LatLng dest_location = dataSnapshotToLatLng(camp_snap.child("dest_location"));
        List<LatLng> list_locations = dataSnapshotToLatLngList(camp_snap.child("list_locations"));

        String owner_account = (String)camp_snap.child("owner_account").getValue();
        String time_left = (String)camp_snap.child("time_left").getValue();

        Campaign to_return = new Campaign(
                accumulated_donation, campaign_name, charity, description,
                destination, goal_amount, initial_location,  dest_location,
                owner_account, time_left, list_locations
        );

        return to_return;
    }

    @Override
    public void update(Object o, DatabaseReference databaseReference) {
        Campaign campaign_update = (Campaign) o;
        DatabaseReference camp_update_ref = databaseReference.child(campaign_update.campaign_name);

        camp_update_ref.child("accumulated_donation").setValue(campaign_update.accumulated_donation);
        camp_update_ref.child("campaign_name").setValue(campaign_update.campaign_name);
        camp_update_ref.child("charity").setValue(campaign_update.charity);
        camp_update_ref.child("description").setValue(campaign_update.description);
        camp_update_ref.child("destination").setValue(campaign_update.destination);
        camp_update_ref.child("goal_amount").setValue(campaign_update.goal_amount);

        camp_update_ref.child("initial_location").setValue(campaign_update.initial_location);
        camp_update_ref.child("dest_location").setValue(campaign_update.destination);
        camp_update_ref.child("list_locations").setValue(campaign_update.list_locations);

        camp_update_ref.child("owner_account").setValue(campaign_update.owner_account);
        camp_update_ref.child("time_left").setValue(campaign_update.time_left);
    }

    @Override
    public void put(Object o, DatabaseReference databaseReference) {

    }

    /*
    *   this method search through the database for campaigns has location within the radius (in kilometer)
    *   @param:
    *   currentLoc : currentLocation of the user
    *   radius : radius to search within
     *  dataSnapshot: a datasnapshot of all campaigns object in firebase
    */
    public List<Campaign> searchNearbyCamps(LatLng currentLoc, int radius, DataSnapshot dataSnapshot){
        List<Campaign> nearbyCamp_list = new LinkedList<>();
        for(DataSnapshot camps : dataSnapshot.getChildren()){

            List<LatLng> list_location = dataSnapshotToLatLngList(camps);
            //List<LatLng> list_location = (List<LatLng>) camps.child("list_locations");

            //iterate through the location and check if there exist one within radius
            for(LatLng location: list_location){
                if(Algorithms.calculateDistance(currentLoc, location) <= radius ){
                    Campaign camp_within_radius = read((String) camps.child("campaign_name").getValue(),
                            dataSnapshot);
                    nearbyCamp_list.add(camp_within_radius);
                }

            }
        }

        return nearbyCamp_list;
    }

    /**
     * Takes in a datashapshot and returns a LatLng object with the coordinates
     * @param datasnapshot
     * @return a LatLng object with the coordinates in datasnapshot
     */
    private LatLng dataSnapshotToLatLng (DataSnapshot datasnapshot){
        //get coordinates of campaign launch location
        Map<String, Double> mapcoords = (HashMap<String,Double>) datasnapshot.getValue();

        //create LatLng object out of coordinates
        return new LatLng(mapcoords.get("latitude"), mapcoords.get("longitude"));
    };

    /**
     * Takes in a datashapshot and returns a list of LatLng object with the coordinates
     * @param datasnapshot
     * @return a list of LatLng object with the coordinates in datasnapshot
     */
    private List<LatLng> dataSnapshotToLatLngList (DataSnapshot datasnapshot){
        //get coordinates of location
        List<LatLng> to_return = new LinkedList<LatLng>();
        for(DataSnapshot loc : datasnapshot.getChildren()){
            LatLng location_to_add = dataSnapshotToLatLng(loc);
            to_return.add(location_to_add);
        }

        return to_return;
    };



}