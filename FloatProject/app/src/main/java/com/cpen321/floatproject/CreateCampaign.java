package com.cpen321.floatproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.Profile;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Created by sfarinas on 10/17/2016.
 */
public class CreateCampaign extends Activity {

    private DatabaseReference databaseref = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createcampaign);

        Button button = (Button) findViewById(R.id.launchcamp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 addCampaign();

                //launchCampaign();
                //TODO remove listener or grey out button after campaign is launched.

                //start pledge_agreement activity (where the user agrees to pay the specified amount in the future)
                //things to pass in: amount they paid
                startActivity(new Intent(v.getContext(), PledgeAgreement.class)
                        .putExtra("PledgeAmount", ((EditText)findViewById(R.id.pledgein)).getText().toString()));
            }
        });

        button = (Button) findViewById(R.id.savecamp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCampaign();
            }
        });
    }

    //add campaign to online database
    public void addCampaign (){

        //these are the strings we need to save for a new campaign
        EditText myText = (EditText) findViewById(R.id.titlein);
        String title = myText.getText().toString();
        Log.d("Tag",title);

        myText = (EditText) findViewById(R.id.charityin);
        String charity = myText.getText().toString();
        Log.d("Tag", charity);

        myText = (EditText) findViewById(R.id.goalin);
        String goal = myText.getText().toString();
        Log.d("Tag",goal);

        myText = (EditText) findViewById(R.id.pledgein);
        String pledge = myText.getText().toString();
        Log.d("Tag", pledge);

        myText = (EditText) findViewById(R.id.initlocatlatitude);
        double initlocatlatitude = Double.parseDouble(myText.getText().toString());
        Log.d("Tag", "initlocatlatitude: " + initlocatlatitude);

        myText = (EditText) findViewById(R.id.initlocatlongitude);
        double initlocatlongitude = Double.parseDouble(myText.getText().toString());
        Log.d("Tag", "initlocatlongitude: " + initlocatlongitude);

        myText = (EditText) findViewById(R.id.destlocatlatitude);
        double destlocatlatitude = Double.parseDouble(myText.getText().toString());
        Log.d("Tag", "destlocatlatitude: " + destlocatlatitude);

        myText = (EditText) findViewById(R.id.destlocatlongitude);
        double destlocatlongitude = Double.parseDouble(myText.getText().toString());
        Log.d("Tag", "destlocatlongitude: " + destlocatlongitude);

        myText = (EditText) findViewById(R.id.descriptionin);
        String description = myText.getText().toString();
        Log.d("Tag", description);

        //get Facebook numerical ID of signed in user
        Profile profile = Profile.getCurrentProfile();
        String userid = profile.getId();

        //get pro
        Query queryRef =  databaseref.child("users").child(userid);
        if(queryRef != null) {
            String user_name = queryRef.orderByKey().equalTo("name").toString();

            Campaign myCampaign = new Campaign("0", title, charity, description,
                    goal, pledge, initlocatlatitude, initlocatlongitude, destlocatlatitude, destlocatlongitude, user_name,
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