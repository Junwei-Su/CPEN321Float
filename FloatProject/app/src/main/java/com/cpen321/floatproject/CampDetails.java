package com.cpen321.floatproject;

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

import com.cpen321.floatproject.campaigns.Campaign;
import com.cpen321.floatproject.database.CampsDBInteractor;
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

    private DatabaseReference databaseref;
    private DatabaseReference campaignref;
    private DatabaseReference campaignsref;
    private DatabaseReference charitiesref;
    private DatabaseReference charityref;
    private DatabaseReference launchuserref;
    private DatabaseReference usersref;

    private FirebaseStorage storageref;
    private StorageReference imagesref;

    private ValueEventListener launchuserlistener;
    private ValueEventListener charitylistener;

    private CampsDBInteractor dbInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campdetails);

        Intent intent = getIntent();
        final String theCampaign = intent.getStringExtra("key");
        Log.d("Tag", "camptitleinfo2 = " + theCampaign);


        TextView tv = (TextView) findViewById(R.id.camptitledeets);
        tv.setText(theCampaign);

        databaseref = FirebaseDatabase.getInstance().getReference();
        usersref = databaseref.child("users");
        charitiesref = databaseref.child("charities");
        campaignsref = databaseref.child("campaigns");
        campaignref = campaignsref.child(theCampaign);
//        Log.d("Tag", "testingreference = " + campaignref.toString());

        //Getting the picture
        storageref = FirebaseStorage.getInstance();
        imagesref = storageref.getReferenceFromUrl("gs://float-568c7.appspot.com/images");

        dbInteractor = new CampsDBInteractor();
        campaignsref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Campaign campaign = dbInteractor.read(theCampaign, dataSnapshot);

                //update campaign image
                StorageReference imageref = imagesref.child(campaign.getCampaign_pic());
                setDBPictureOnImageView(imageref, R.id.campaignpicdeets);

                launchuserref = usersref.child(campaign.getOwner_account());
                launchuserref.addListenerForSingleValueEvent(launchuserlistener);

                String charity = campaign.getCharity();
                TextView tv = (TextView) findViewById(R.id.charitydeets);
                tv.setText(charity);
                charityref = charitiesref.child(charity);
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
                Intent intent = new Intent(v.getContext(), InstantPayment.class);
                startActivity(intent);
            }
        });

        //TODO remove this copy from MapPage
        launchuserlistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //update profile pic of launcher of campaign
                String launchuserpic = dataSnapshot.child("profile_pic").getValue(String.class);
                Log.d("Tag", "launchuserpic = " + launchuserpic);
                StorageReference launchuserpicref = imagesref.child(launchuserpic);
                setDBPictureOnImageView(launchuserpicref, R.id.userpicdeets);

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
                Log.d("Tag", "charitypic = " + charitypic);
                StorageReference logoRef = imagesref.child(charitypic);
                setDBPictureOnImageView(logoRef, R.id.charitypicdeets);
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
    public void setDBPictureOnImageView(StorageReference storageReference, final int imageViewID){
        final File localFile;
        try {
            localFile = File.createTempFile("images", "png");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    ImageView testPic = (ImageView) findViewById(imageViewID);
                    Uri uri = Uri.fromFile(localFile);
                    testPic.setImageURI(uri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}