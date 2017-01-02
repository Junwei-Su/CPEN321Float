package com.cpen321.floatproject.activities;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cpen321.floatproject.R;
import com.cpen321.floatproject.database.DB;
import com.facebook.Profile;
import com.facebook.ProfileTracker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends FragmentActivity {
    Button mapButton;
    ProfileTracker mapPageProfileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapButton = (Button) findViewById(R.id.mapButton);

    }

    @Override
    protected void onResume() {
        super.onResume();

        updateMapButton(Profile.getCurrentProfile());

        mapPageProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                // profile2 is the new mprofile
                updateMapButton(profile2);
                mapPageProfileTracker.stopTracking();
            }
        };
    }

    public void updateMapButton(Profile profile){
        if(profile == null){
            //not logged, make map button gone
            mapButton.setVisibility(View.GONE);
        }else{
            //logged in already, make map button visible
            mapButton.setVisibility(View.VISIBLE);
        }
    }

    //starts MapPage activity
    public void startMapActivity(View view){
        Intent intent = new Intent(this, MapPage.class);
        startActivity(intent);

    }

    public void toCreateUser(View view) {
        Intent intent = new Intent(this, CreateUser.class);
        startActivity(intent);
    }

}