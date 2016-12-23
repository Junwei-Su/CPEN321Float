package com.cpen321.floatproject;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpen321.floatproject.campaigns.Campaign;
import com.cpen321.floatproject.campaigns.DestinationCampaign;
import com.facebook.Profile;
import com.firebase.client.utilities.Base64;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
    private ImageView photo;
    private Uri selectedImageURI;

    private FirebaseStorage storage;
    private StorageReference imagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createcampaign);

        launchLat = (TextView) findViewById(R.id.initlocatlatitude);
        launchLong = (TextView) findViewById(R.id.initlocatlongitude);
        photo = (ImageView) findViewById(R.id.photo);

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

        storage = FirebaseStorage.getInstance();
        imagesRef = storage.getReferenceFromUrl("gs://float-568c7.appspot.com/images");
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 1 && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                selectedImageURI = data.getData();
                photo.setImageURI(selectedImageURI);
                Log.d("url2", selectedImageURI.toString());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

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

        StorageReference riversRef = imagesRef.child(title + "_pic.jpg");
        UploadTask uploadTask = riversRef.putFile(selectedImageURI);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });

        //get pro
        Query queryRef =  databaseref.child("users").child(userid);
        Log.d("Tag", "userid = " + userid);
        if(queryRef != null) {
            String user_name = queryRef.orderByKey().equalTo("name").toString();


            Campaign myCampaign = new DestinationCampaign(title, 0, charity, description,
                    Integer.valueOf(goal), init_location,userid, 10, "2016-12-24", "home", dest_location);
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