package com.cpen321.floatproject;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Created by sfarinas on 10/17/2016.
 */
public class CreateCampaign extends AppCompatActivity {
    private String title;
    private String charity;
    private String description;
    private String goal;
    private String pledge;
    private String userid;
    private double initlocatlatitude;
    private double initlocatlongitude;
    private double destlocatlatitude;
    private double destlocatlongitude;
    private LatLng init_location;
    private LatLng dest_location;

    private DatabaseReference databaseref = FirebaseDatabase.getInstance().getReference();

    private GetGPSLocation gps;

    private TextView launchLat;
    private TextView launchLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createcampaign);

        launchLat = (TextView) findViewById(R.id.initlocatlatitude);
        launchLong = (TextView) findViewById(R.id.initlocatlongitude);

        Button button = (Button) findViewById(R.id.launchcamp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCampaign();

                //launchCampaign();
                //TODO remove listener or grey out button after campaign is launched.

                //start pledge_agreement activity (where the user agrees to pay the specified amount in the future)
                //things to pass in: amount they paid, title
                startActivity(new Intent(v.getContext(), FuturePaymentAgreement.class)
                        .putExtra("PledgeAmount", pledge)
                        .putExtra("Title", title)
                        .putExtra("UserID", userid));
            }
        });

        button = (Button) findViewById(R.id.savecamp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCampaign();
            }
        });

        Button getCoords = (Button) findViewById(R.id.getCoords);
        getCoords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps = new GetGPSLocation(CreateCampaign.this, CreateCampaign.this);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    launchLat.setText(Double.toString(latitude));
                    launchLong.setText(Double.toString(longitude));
                }
                else{
                    gps.showSettingsAlert();
                }
            }
        });
    }

    //add campaign to online database
    public void addCampaign (){

        //these are the strings we need to save for a new campaign
        EditText myText = (EditText) findViewById(R.id.titlein);
        title = myText.getText().toString();
        Log.d("Tag",title);

        myText = (EditText) findViewById(R.id.charityin);
        charity = myText.getText().toString();
        Log.d("Tag", charity);

        myText = (EditText) findViewById(R.id.goalin);
        goal = myText.getText().toString();
        Log.d("Tag",goal);

        myText = (EditText) findViewById(R.id.pledgein);
        pledge = myText.getText().toString();
        Log.d("Tag", pledge);

        TextView myTextView = (TextView) findViewById(R.id.initlocatlatitude);
        initlocatlatitude = Double.parseDouble(myTextView.getText().toString());
        Log.d("Tag", "initlocatlatitude: " + initlocatlatitude);

        myTextView = (TextView) findViewById(R.id.initlocatlongitude);
        initlocatlongitude = Double.parseDouble(myTextView.getText().toString());
        Log.d("Tag", "initlocatlongitude: " + initlocatlongitude);

        init_location = new LatLng(initlocatlatitude, initlocatlongitude);

        myText = (EditText) findViewById(R.id.destlocatlatitude);
        destlocatlatitude = Double.parseDouble(myText.getText().toString());
        Log.d("Tag", "destlocatlatitude: " + destlocatlatitude);

        myText = (EditText) findViewById(R.id.destlocatlongitude);
        destlocatlongitude = Double.parseDouble(myText.getText().toString());
        Log.d("Tag", "destlocatlongitude: " + destlocatlongitude);

        dest_location = new LatLng(destlocatlatitude, destlocatlongitude);

        myText = (EditText) findViewById(R.id.descriptionin);
        description = myText.getText().toString();
        Log.d("Tag", description);

        //get Facebook numerical ID of signed in user
        Profile profile = Profile.getCurrentProfile();
        userid = profile.getId();

        //get pro
        Query queryRef =  databaseref.child("users").child(userid);
        if(queryRef != null) {
            String user_name = queryRef.orderByKey().equalTo("name").toString();
            Campaign myCampaign = new Campaign("0", title, charity, description,
                    goal, pledge, init_location, dest_location, user_name,
                    Integer.toString(R.integer.defaulttimeremaining));
            databaseref.child("campaigns").child(title).setValue(myCampaign);
        }else{
            Log.d("Tag", "Error creating campaign. Need to create account first.");
        }
    }

    //launch campaign
    private static void launchCampaign(){
    }

    //save campaign
    private static void saveCampaign(){}
}