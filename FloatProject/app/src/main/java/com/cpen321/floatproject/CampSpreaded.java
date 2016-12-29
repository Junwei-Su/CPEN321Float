package com.cpen321.floatproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cpen321.floatproject.campaigns.DestinationCampaign;
import com.cpen321.floatproject.database.DB;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Little_town on 12/29/2016.
 */

public class CampSpreaded extends Activity {

    GetGPSLocation currentLoc;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.camp_spreaded);
        Intent intent = getIntent();
        currentLoc = new GetGPSLocation(CampSpreaded.this, CampSpreaded.this);
        final LatLng currentLocation = new LatLng(currentLoc.getLatitude(),currentLoc.getLongitude());

        //read camp and add loc to camp list of loc
        final String theCampaign = intent.getStringExtra("Title");
        DB.camp_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DestinationCampaign campaign = DB.campDBinteractor.read(theCampaign,dataSnapshot);
                campaign.add_location(currentLocation);
                DB.campDBinteractor.update(campaign,DB.root_ref);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //read user and add camp id to user list camp followed
        final String theUser = intent.getStringExtra("UserId");
        DB.user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = DB.usersDBinteractor.read(theUser,dataSnapshot);
                user.addFollowedCamp(theCampaign);
                DB.usersDBinteractor.update(user, DB.root_ref);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
