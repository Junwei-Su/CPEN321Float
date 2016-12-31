package com.cpen321.floatproject.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cpen321.floatproject.GPS.GetGPSLocation;
import com.cpen321.floatproject.R;
import com.cpen321.floatproject.campaigns.Campaign;
import com.cpen321.floatproject.database.DB;
import com.cpen321.floatproject.utilities.ActivityUtility;
import com.cpen321.floatproject.utilities.Algorithms;
import com.facebook.Profile;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Created by sfarinas on 10/17/2016.
 */
public class CampDetails extends Activity {

    private long total_time = DateUtils.DAY_IN_MILLIS *4;
    private DatabaseReference charityref;
    private DatabaseReference launchuserref;
    private ValueEventListener launchuserlistener;
    private ValueEventListener charitylistener;
    private Button float_button;

    private Campaign campaign;
    private String theCampaign;
    private String charity;
    private ImageView campPic;
    private ImageView userPic;
    private ImageView charPic;

    private double radius = 500;

    private boolean in_range;
    private CountDownTimer mCountDownTimer;
    private long mInitialTime;
    private TextView mTextView;
    private LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.campdetails);

        Intent intent = getIntent();
        theCampaign = intent.getStringExtra("key");

        ImageButton return_button = (ImageButton) findViewById(R.id.return_map3);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapPage.class);
                startActivity(intent);
            }
        });

        String htmlString = "<u>".concat(theCampaign).concat("</u>");
        TextView tv = (TextView) findViewById(R.id.camptitledeets);
        tv.setText(Html.fromHtml(htmlString));

        DB.camp_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                campaign = DB.campDBinteractor.read(theCampaign, dataSnapshot);

                //update campaign image
                StorageReference imageref = DB.images_ref.child(campaign.getCampaign_pic());
                campPic = (ImageView) findViewById(R.id.campaignpicdeets);
                ActivityUtility.setPictureOnImageView(imageref, campPic);

                launchuserref = DB.user_ref.child(campaign.getOwner_account());
                launchuserref.addListenerForSingleValueEvent(launchuserlistener);

                charity = campaign.getCharity();
                TextView tv = (TextView) findViewById(R.id.charitydeets);
                tv.setText(charity);
                charityref = DB.char_ref.child(charity);
                charityref.addListenerForSingleValueEvent(charitylistener);

                startTimer();
                setButtonStatus();
                setDistanceRemaining();

                tv = (TextView) findViewById(R.id.descriptiondeets);
                tv.setText(campaign.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Button donate_button = (Button) findViewById(R.id.donate_button);
        donate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start paypal
                Intent intent = new Intent(v.getContext(), InstantPayment.class)
                        .putExtra("Title", campaign.getCampaign_name())
                        .putExtra("Owner_id", campaign.getOwner_account())
                        .putExtra("Current User_id", Profile.getCurrentProfile().getId());
                startActivity(intent);
            }
        });

        float_button = (Button) findViewById(R.id.float_button);
        float_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (in_range && mInitialTime > 0) {
                    Intent intent = new Intent(v.getContext(), CampSpreaded.class)
                            .putExtra("Title", campaign.getCampaign_name())
                            .putExtra("UserId", Profile.getCurrentProfile().getId());
                    startActivity(intent);
                }
            }
        });

        launchuserlistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //update profile pic of launcher of campaign
                String launchuserpic = dataSnapshot.child("profile_pic").getValue(String.class);
                StorageReference launchuserpicref = DB.images_ref.child(launchuserpic);
                userPic = (ImageView) findViewById(R.id.userpicdeets);
                ActivityUtility.setPictureOnImageView(launchuserpicref, userPic);

                //update username of launcher of campaign
                String launchusername = dataSnapshot.child("name").getValue(String.class);
                TextView tv = (TextView) findViewById(R.id.userdeets);
                tv.setText(launchusername);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        charitylistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String charitypic = dataSnapshot.child("logo").getValue(String.class);
                StorageReference logoRef = DB.images_ref.child(charitypic);
                charPic = (ImageView) findViewById(R.id.charitypicdeets);
                ActivityUtility.setPictureOnImageView(logoRef, charPic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }

    private void setButtonStatus(){
        LatLng initial_location = campaign.getInitial_location();
        GetGPSLocation currentLoc = new GetGPSLocation(CampDetails.this, CampDetails.this);
        if (currentLoc.canGetLocation()) {
            currentLocation = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
            double distance = Algorithms.calculateDistance(currentLocation, initial_location);
            Log.i("distance", Double.toString(distance));
            in_range = (distance <= radius);
            Button b = (Button) findViewById(R.id.float_button);
            if (mInitialTime>0) {
                if (in_range)
                    b.setText("FLOAT");
                else {
                    b.setText("NOT IN RANGE");
                    b.setBackgroundColor(Color.WHITE);
                    b.setTextColor(Color.BLACK);
                }
            }
            else {
                b.setText("OUT OF TIME");
                b.setBackgroundColor(Color.WHITE);
                b.setTextColor(Color.BLACK);
            }
        } else {
            currentLoc.showSettingsAlert();
        }
    }

    private void startTimer(){
        Date current_date = new Date();
        Date initial_date = Algorithms.string_to_date(campaign.getInitial_date());
        long difference = current_date.getTime() - initial_date.getTime();
        mInitialTime = total_time-difference;
        mTextView = (TextView) findViewById(R.id.timer);

        if (mInitialTime <= 0)
            mTextView.setBackgroundResource(R.drawable.rounded_corners_red);

        mCountDownTimer = new CountDownTimer(mInitialTime, 1000) {
            StringBuilder time = new StringBuilder();

            @Override
            public void onFinish() {
                mTextView.setText(DateUtils.formatElapsedTime(0));
                //mTextView.setText("Times Up!");
            }

            @Override
            public void onTick(long millisUntilFinished) {
                time.setLength(0);
                // Use days if appropriate
                if (millisUntilFinished > DateUtils.DAY_IN_MILLIS * 2)
                    mTextView.setBackgroundResource(R.drawable.rounded_corners_green);
                else if (millisUntilFinished > DateUtils.DAY_IN_MILLIS * 1)
                    mTextView.setBackgroundResource(R.drawable.rounded_corners_yellow);
                else if (millisUntilFinished > DateUtils.HOUR_IN_MILLIS * 5)
                    mTextView.setBackgroundResource(R.drawable.rounded_corners_orange);
                else
                    mTextView.setBackgroundResource(R.drawable.rounded_corners_red);

                if (millisUntilFinished > DateUtils.DAY_IN_MILLIS) {
                    long count = millisUntilFinished / DateUtils.DAY_IN_MILLIS;
                    if (count > 1)
                        time.append(count).append(" days, ");
                    else
                        time.append(count).append(" day, ");

                    millisUntilFinished %= DateUtils.DAY_IN_MILLIS;
                }

                time.append(DateUtils.formatElapsedTime(Math.round(millisUntilFinished / 1000d)));
                mTextView.setText(time.toString());
            }
        }.start();
    }

    public void setDistanceRemaining(){
        //LatLng goal = campaign.getGoalLocation();
        LatLng goal = new LatLng(49.018038, -123.081825); //hard coded for now
        double distance = Algorithms.calculateDistance(goal, currentLocation);
        BigDecimal dist = new BigDecimal(distance).setScale(2, RoundingMode.HALF_EVEN);
        TextView d_text = (TextView) findViewById(R.id.dist_remaining);
        d_text.setText(dist.toString().concat(" km"));
    }
}


