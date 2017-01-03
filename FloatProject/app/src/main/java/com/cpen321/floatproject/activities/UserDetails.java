package com.cpen321.floatproject.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cpen321.floatproject.R;
import com.cpen321.floatproject.database.DB;
import com.cpen321.floatproject.users.User;
import com.cpen321.floatproject.utilities.ActivityUtility;
import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sfarinas on 12/28/2016.
 */
public class UserDetails extends Activity {
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details);

        DB.user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get User object
                Profile profile = Profile.getCurrentProfile();
                user = DB.usersDBinteractor.read(profile.getId(), dataSnapshot);

                //update profile picture
                StorageReference imageref = DB.images_ref.child(user.getProfile_pic());
                ImageView profilepic = (ImageView) findViewById(R.id.profile_pic);
                ActivityUtility.setPictureOnImageView(imageref, profilepic);

                //update username
                TextView textView = (TextView) findViewById(R.id.username);
                textView.setText(user.getName());

                //update blurb
                textView = (TextView) findViewById(R.id.blurb);
                textView.setText(user.getBlurb());

                //update stats
                ListView userStats = (ListView) findViewById(R.id.user_stats);
                userStats.setAdapter(new ArrayAdapter<>(
                        UserDetails.this,
                        R.layout.stats_list_item,
                        getStatsArray(user).toArray()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //wire createcamp button to CreateCampaign activity
        ImageButton createCamp = (ImageButton) findViewById(R.id.createcamp);
        createCamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateCampaign.class);
                startActivity(intent);

            }
        });

        ImageButton mapButton = (ImageButton) findViewById(R.id.mapCamp);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jumpToMapView = new Intent(v.getContext(), MapPage.class);
                startActivity(jumpToMapView);

            }
        });

        ImageButton listButton = (ImageButton) findViewById(R.id.listCamp);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jumpToListView = new Intent(v.getContext(), CampListView.class);
                startActivity(jumpToListView);

            }
        });
    }

    /**
     * returns a list of Strings where
     * contents are name, date joined, blurb, amount raised and amount donated,
     * in that order
     * @param user
     * @return List of strings
     */
    private List<String> getStatsArray(User user){
        //name, date_join, blurb,amount_raised,_donated
        List<String> stats = new LinkedList<>();

        stats.add("Date Joined: " + user.getDate());
        stats.add("Amount donated: $" + String.valueOf(user.getAmount_donated()));
        stats.add("Amount raised: $" + String.valueOf(user.getAmount_raised()));

        List<String> camps_followed_list = user.getList_of_campaign_followed();
        List<String> camps_initialized_list = user.getList_of_campaign_initialize();

        String camps_followed_string;
        String camps_initialized_string;

        if(camps_followed_list.size() != 0){
            camps_followed_string = Arrays.toString(camps_followed_list.toArray());
            //remove outer square brackets
            camps_followed_string = camps_followed_string.substring(1,camps_followed_string.length()-1);
        }else{
            camps_followed_string = "none";
        }

        if(camps_initialized_list.size() != 0){
            camps_initialized_string = Arrays.toString(camps_initialized_list.toArray());
            //remove outer square brackets
            camps_initialized_string = camps_initialized_string.substring(1, camps_initialized_string.length()-1);
        }else{
            camps_initialized_string = "none";
        }

        stats.add("Campaigns followed: " + camps_followed_string);
        stats.add("Campaigns started: " + camps_initialized_string);

        return stats;
    }

}
