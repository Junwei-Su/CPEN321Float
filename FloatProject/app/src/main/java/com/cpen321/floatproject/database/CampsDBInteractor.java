package com.cpen321.floatproject.database;

import android.util.Log;

import com.cpen321.floatproject.algorithm.Algorithms;
import com.cpen321.floatproject.campaigns.Campaign;
import com.cpen321.floatproject.campaigns.DestinationCampaign;
import com.google.firebase.database.DataSnapshot;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

import java.lang.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public DestinationCampaign read(String ID, DataSnapshot dataSnapshot) {

        //get the dataSnapshot of the campaign object with this ID
        DataSnapshot camp_snap =  dataSnapshot.child(ID);

        //if campaign no exist;
        if(camp_snap==null){
            return null;
        }

        //retrive the camp information

        long accumulated_donation = (Long)camp_snap.child("accumulated_donation").getValue();
        String campaign_name = (String)camp_snap.child("campaign_name").getValue();

        String charity = (String)camp_snap.child("charity").getValue();
        String description = (String)camp_snap.child("description").getValue();
        String destination = (String)camp_snap.child("destination").getValue();
        long goal_amount = (long)camp_snap.child("goal_amount").getValue();
        long time_length = (long)camp_snap.child("time_length").getValue();
        String initial_date= (String) camp_snap.child("initial_date").getValue();

        LatLng initial_location =  dataSnapshotToLatLng(camp_snap.child("initial_location"));
        //clarence debug
        Log.d("testCamp1",camp_snap.child("dest_location").getValue().toString());
        LatLng dest_location = dataSnapshotToLatLng(camp_snap.child("dest_location"));
        List<LatLng> list_locations = dataSnapshotToLatLngList(camp_snap.child("list_locations"));



        String owner_account = (String)camp_snap.child("owner_account").getValue();
        String campaign_pic = (String) camp_snap.child("campaign_pic").getValue();

        DestinationCampaign to_return = new DestinationCampaign(
                campaign_name, accumulated_donation, charity, description,
                goal_amount, initial_location, owner_account,
                time_length, initial_date, destination, dest_location, list_locations,campaign_pic
        );

        return to_return;
    }


    /*
   *   This method updates a Campaign object in database with a campaign object
   *   @param:
   *       object: campaign
   *       DatabaseReference: databasereference to campaign root
    */
    @Override
    public void update(Object o, DatabaseReference databaseReference) {
        DestinationCampaign campaign_update = (DestinationCampaign) o;
        DatabaseReference camp_update_ref = databaseReference.child(campaign_update.getCampaign_name());
        //update infomation
        camp_update_ref.child("accumulated_donation").setValue(campaign_update.getAccumulated_donation());
        camp_update_ref.child("charity").setValue(campaign_update.getCharity());
        camp_update_ref.child("description").setValue(campaign_update.getDescription());
        camp_update_ref.child("destination").setValue(campaign_update.getDestination());
        camp_update_ref.child("goal_amount").setValue(campaign_update.getGoal_amount());
        camp_update_ref.child("dest_location").setValue(campaign_update.getDest_location());
        camp_update_ref.child("list_locations").setValue(campaign_update.getList_locations());
        camp_update_ref.child("time_length").setValue(campaign_update.getTime_length());
        camp_update_ref.child("initial_date").setValue(campaign_update.getInitial_date());
        camp_update_ref.child("campaign_pic").setValue(campaign_update.getCampaign_pic());

    }

    /*
    * Put the corresponding object into the database
    * @param:
    * o: campgain to be put to database
    * databaseReference: reference to database root
     */
    @Override
    public void put(Object o, DatabaseReference databaseReference) {

        DestinationCampaign to_push = (DestinationCampaign) o;
        String key = to_push.getCampaign_name();

        Map<String, Object> campValue = to_push.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/campaigns/" + key, campValue);
        databaseReference.updateChildren(childUpdates);
    }

    /*
    *   this method search through the database for campaigns has location within the radius (in kilometer)
    *   @param:
    *   currentLoc : currentLocation of the user
    *   radius : radius to search within
     *  dataSnapshot: a datasnapshot of all campaigns object in firebase
    */
    public List<Campaign> searchNearbyCamps(LatLng currentLoc, int radius, DataSnapshot campListSnap){
        List<Campaign> nearbyCamp_list = new LinkedList<>();
        Set<String> addedCamp = new HashSet<String>(); //visited flag
        for(DataSnapshot camps : campListSnap.getChildren()){
            String camps_name = (String) camps.child("campaign_name").getValue();
            List<LatLng> list_location = dataSnapshotToLatLngList(camps.child("list_locations"));

            //iterate through the location and check if there exist one within radius
            for(LatLng location: list_location){
                if (addedCamp.contains(camps_name)!=true) {
                    if (Algorithms.calculateDistance(currentLoc, location) <= radius) {
                        //add to visisted list
                        addedCamp.add(camps_name);
                        //read the camp object
                        Campaign camp_within_radius = read(camps_name, campListSnap);
                        //add to return list
                        nearbyCamp_list.add(camp_within_radius);
                    }
                }else{
                    break;
                }
            }
        }

        return nearbyCamp_list;
    }


    //clarence todo: refactor this to utility class
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