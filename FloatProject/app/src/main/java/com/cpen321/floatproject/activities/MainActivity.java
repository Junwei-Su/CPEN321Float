package com.cpen321.floatproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cpen321.floatproject.R;
import com.facebook.Profile;
import com.facebook.ProfileTracker;

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
        int location = locationOn();
        if (location == 0){
            TextView t = (TextView) findViewById(R.id.locationOn);
            t.setText("Please turn on location services before proceeding");
        }
        else {
            Intent intent = new Intent(this, MapPage.class);
            startActivity(intent);
        }
    }

    public void toCreateUser(View view) {
        Intent intent = new Intent(this, CreateUser.class);
        startActivity(intent);
    }

    private int locationOn(){
        int off = 0;
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return off;
    }


}