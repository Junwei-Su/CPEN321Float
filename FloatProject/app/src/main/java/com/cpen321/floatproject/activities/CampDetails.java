package com.cpen321.floatproject.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cpen321.floatproject.R;
import com.cpen321.floatproject.campaigns.Campaign;
import com.cpen321.floatproject.database.CampsDBInteractor;
import com.cpen321.floatproject.database.DB;
import com.cpen321.floatproject.utilities.ActivityUtility;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

/**
 * Created by sfarinas on 10/17/2016.
 */
public class CampDetails extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.campdetails);

        Intent intent = getIntent();
        theCampaign= intent.getStringExtra("key");

        TextView tv = (TextView) findViewById(R.id.camptitledeets);
        tv.setText(theCampaign);

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
                Intent intent = new Intent(v.getContext(), CampSpreaded.class)
                        .putExtra("Title", campaign.getCampaign_name())
                        .putExtra("UserId", Profile.getCurrentProfile().getId());
                startActivity(intent);
            }
        });

        launchuserlistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //update profile pic of launcher of campaign
                String launchuserpic = dataSnapshot.child("profile_pic").getValue(String.class);
                StorageReference launchuserpicref = DB.images_ref.child(launchuserpic);
                userPic = (ImageView) findViewById(R.id.userpicdeets);
                ActivityUtility.setPictureOnImageView(launchuserpicref,userPic);

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

    //TODO copied method from MapPage. Look for possible refactor later.
    /**
     * Updates an ImageView object with file in Firebase database at storageReference
     * @param storageReference
     * @param imageViewID
     */
//    public void setDBPictureOnImageView(StorageReference storageReference, final int imageViewID){
//        final File localFile;
//        try {
//            localFile = File.createTempFile("images", "png");
//            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                    // Local temp file has been created
//                    ImageView testPic = (ImageView) findViewById(imageViewID);
//                    Uri uri = Uri.fromFile(localFile);
//                    testPic.setImageURI(uri);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle any errors
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}