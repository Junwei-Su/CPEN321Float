package com.cpen321.floatproject.activities.CampActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cpen321.floatproject.GPS.GetGPSLocation;
import com.cpen321.floatproject.R;
import com.cpen321.floatproject.activities.PaypalActivities.ExecuteFuturePayment;
import com.cpen321.floatproject.activities.Log_in_and_map.MapPage;
import com.cpen321.floatproject.campaigns.DestinationCampaign;
import com.cpen321.floatproject.database.DB;
import com.cpen321.floatproject.users.User;
import com.facebook.Profile;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Little_town on 12/29/2016.
 */

/*
 * this class represent the campaign spreading activity in the application
 */
public class CampSpreaded extends Activity {

    GetGPSLocation currentLoc;
    Button checkMark;
    LatLng currentLocation;

    private final static int REQUEST_CODE_SUCCESS = 1;
    private int IN_PROGRESS=0;
    private int EXPIRE = 1;
    private int SUCCEED = 2;

    private String title;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.camp_spreaded);
        Intent intent = getIntent();
        //get current location
        currentLoc = new GetGPSLocation(CampSpreaded.this, CampSpreaded.this);

        if (currentLoc.canGetLocation()) {
            currentLocation = new LatLng(currentLoc.getLatitude(),currentLoc.getLongitude());
        }
        else{
            currentLoc.showSettingsAlert();
        }

        currentLocation = new LatLng(currentLoc.getLatitude(),currentLoc.getLongitude());

        //read camp and add loc to camp list of loc
        final String theCampaign = intent.getStringExtra("Title");
        DB.camp_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DestinationCampaign campaign = DB.campDBinteractor.read(theCampaign,dataSnapshot);

                campaign.add_location(currentLocation);

                DB.campDBinteractor.update(campaign,DB.root_ref);
                title = campaign.getCampaign_name();
                check(campaign.returnStatus());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //read user and add camp id to user list camp followed
        final String theUser = intent.getStringExtra("UserId");
        DB.user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
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

        checkMark = (Button) findViewById(R.id.check_mark_button);
        checkMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void check(int status) {
        if (status == SUCCEED) {
            Profile profile = Profile.getCurrentProfile();
            String userid = profile.getId();

            startActivityForResult(new Intent(this, ExecuteFuturePayment.class)
                    .putExtra("Title", title)
                    .putExtra("UserID", userid), REQUEST_CODE_SUCCESS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SUCCESS && resultCode == RESULT_OK) {
            DB.campDBinteractor.removeCamp(title);
            Intent intent = new Intent(this, MapPage.class);
            startActivity(intent);
        }
        else if (requestCode == REQUEST_CODE_SUCCESS && resultCode != RESULT_OK){
            Intent intent = new Intent(this, MapPage.class);
            startActivity(intent);
        }
    }
}
