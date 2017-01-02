package com.cpen321.floatproject.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cpen321.floatproject.GPS.GetGPSLocation;
import com.cpen321.floatproject.R;
import com.cpen321.floatproject.campaigns.DestinationCampaign;
import com.cpen321.floatproject.database.DB;
import com.cpen321.floatproject.users.User;
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
import java.util.List;

/**
 * Created by sfarinas on 10/17/2016.
 */
public class CampDetails extends Activity {

    private DatabaseReference charityref;
    private DatabaseReference launchuserref;
    private ValueEventListener launchuserlistener;
    private ValueEventListener charitylistener;
    private Button float_button;

    private DestinationCampaign campaign;
    private String theCampaign;
    private String charity;
    private ImageView campPic;
    private ImageView userPic;
    private ImageView charPic;

    private List<String> list_of_campaign_followed;
    private User user;

    private boolean in_range;
    private boolean floated;
    private CountDownTimer mCountDownTimer;
    private long mInitialTime;
    private TextView mTextView;
    private LatLng currentLocation;

    private final static double RADIUS = 0.5;
    private final static int REQUEST_CODE_FLOAT = 1;
    private final static int REQUEST_CODE_DONATE = 2;
    private final static long MILLISEC_IN_SEC = 1000;
    private final static int GREEN_DAY_CUT_OFF = 2;
    private final static int YELLOW_DAY_CUT_OFF = 1;
    private final static int  ORANGE_HOUR_CUT_OFF = 5;

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
                finish();
            }
        });

        String htmlString = "<u>".concat(theCampaign).concat("</u>");
        TextView tv = (TextView) findViewById(R.id.camptitledeets);
        tv.setText(Html.fromHtml(htmlString));

        DB.root_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile profile = Profile.getCurrentProfile();
                String userid = profile.getId();

                campaign = DB.campDBinteractor.read(theCampaign, dataSnapshot.child("campaigns"));
                user = DB.usersDBinteractor.read(userid, dataSnapshot.child("users"));

                startTimer();
                list_of_campaign_followed = user.getList_of_campaign_followed();
                if (list_of_campaign_followed.contains(theCampaign))
                    floated = true;
                else
                    floated = false;
                setButtonStatus();
                setDistanceRemaining();

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
                checkIfFloated();
                if (in_range && mInitialTime > 0 && !floated) {
                    Intent intent = new Intent(v.getContext(), CampSpreaded.class)
                            .putExtra("Title", campaign.getCampaign_name())
                            .putExtra("UserId", Profile.getCurrentProfile().getId());
                    startActivityForResult(intent, REQUEST_CODE_FLOAT);
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

    private void checkIfFloated(){
        DB.user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile profile = Profile.getCurrentProfile();
                String userid = profile.getId();

                user = DB.usersDBinteractor.read(userid, dataSnapshot);

                list_of_campaign_followed = user.getList_of_campaign_followed();
                if (list_of_campaign_followed.contains(theCampaign))
                    floated = true;
                else
                    floated = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setButtonStatus(){
        GetGPSLocation currentLoc = new GetGPSLocation(CampDetails.this, CampDetails.this);
        if (currentLoc.canGetLocation()) {
            currentLocation = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
            in_range = canSpread();
            Button b = (Button) findViewById(R.id.float_button);
            if (!floated) {
                if (mInitialTime > 0) {
                    if (in_range)
                        b.setText("FLOAT");
                    else {
                        b.setText("NOT IN RANGE");
                        b.setBackgroundColor(Color.WHITE);
                        b.setTextColor(Color.BLACK);
                    }
                } else {
                    b.setText("OUT OF TIME");
                    b.setBackgroundColor(Color.WHITE);
                    b.setTextColor(Color.BLACK);
                }
            }
            else{
                b.setText("FLOATED");
                b.setBackgroundColor(Color.WHITE);
                b.setTextColor(Color.BLACK);
            }

        } else {
            currentLoc.showSettingsAlert();
        }
    }


    private void startTimer(){
        long total_time = campaign.getTotal_time();
        long starting_time = campaign.getTime_length();
        long difference = System.currentTimeMillis() - starting_time;
        mInitialTime = total_time-difference;
        mTextView = (TextView) findViewById(R.id.timer);

        if (mInitialTime <= 0)
            mTextView.setBackgroundResource(R.drawable.rounded_corners_red);

        mCountDownTimer = new CountDownTimer(mInitialTime, MILLISEC_IN_SEC) {
            StringBuilder time = new StringBuilder();

            @Override
            public void onFinish() {
                mTextView.setText(DateUtils.formatElapsedTime(0));
            }

            @Override
            public void onTick(long millisUntilFinished) {
                time.setLength(0);

                if (millisUntilFinished > DateUtils.DAY_IN_MILLIS * GREEN_DAY_CUT_OFF)
                    mTextView.setBackgroundResource(R.drawable.rounded_corners_green);
                else if (millisUntilFinished > DateUtils.DAY_IN_MILLIS * YELLOW_DAY_CUT_OFF)
                    mTextView.setBackgroundResource(R.drawable.rounded_corners_yellow);
                else if (millisUntilFinished > DateUtils.HOUR_IN_MILLIS * ORANGE_HOUR_CUT_OFF)
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

                time.append(DateUtils.formatElapsedTime(Math.round(millisUntilFinished / MILLISEC_IN_SEC)));
                mTextView.setText(time.toString());
            }
        }.start();
    }

    private void setDistanceRemaining(){
        LatLng goal = campaign.getDest_location();
        double distance = Algorithms.calculateDistance(goal, currentLocation);
        BigDecimal dist = new BigDecimal(distance).setScale(2, RoundingMode.HALF_EVEN);
        TextView d_text = (TextView) findViewById(R.id.dist_remaining);
        d_text.setText(dist.toString().concat(" km"));

        String pledge_amount = String.valueOf(campaign.getGoal_amount());
        TextView p_text = (TextView) findViewById(R.id.pledge_camp);
        p_text.setText(pledge_amount.concat(" CAD"));

        TextView g_text = (TextView) findViewById(R.id.goal_dest);
        g_text.setText(campaign.getDestination());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_CODE_FLOAT:
                Button b = (Button) findViewById(R.id.float_button);
                b.setText("FLOATED");
                b.setBackgroundColor(Color.WHITE);
                b.setTextColor(Color.BLACK);
                floated = true;
                break;
            case REQUEST_CODE_DONATE:
                break;
        }
    }

    private boolean canSpread(){
        if(Algorithms.calculateDistance(campaign.getInitial_location(), currentLocation) <= RADIUS)
            return true;
        for(LatLng loc : campaign.getList_locations()){
            if(Algorithms.calculateDistance(loc, currentLocation)<= RADIUS){
                return true;
            }
        }
        return false;
    }

}


