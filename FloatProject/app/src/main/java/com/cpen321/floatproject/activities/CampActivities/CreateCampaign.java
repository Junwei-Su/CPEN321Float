package com.cpen321.floatproject.activities.CampActivities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cpen321.floatproject.GPS.GetGPSLocation;
import com.cpen321.floatproject.R;
import com.cpen321.floatproject.utilities.CatchEmptyFields;
import com.cpen321.floatproject.activities.PaypalActivities.FuturePaymentAgreement;
import com.cpen321.floatproject.activities.Log_in_and_map.MapPage;
import com.cpen321.floatproject.campaigns.Campaign;
import com.cpen321.floatproject.campaigns.DestinationCampaign;
import com.cpen321.floatproject.database.DB;
import com.cpen321.floatproject.users.User;
import com.cpen321.floatproject.utilities.Algorithms;
import com.cpen321.floatproject.utilities.UtilityMethod;
import com.facebook.Profile;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by sfarinas on 10/17/2016.
 */

/*
 * this class represent the campaign creating activity in the application
 */
public class CreateCampaign extends AppCompatActivity {
    private String title;
    private String charity;
    private String description;
    private long pledge;
    private String userid;
    private double initlocatlatitude;
    private double initlocatlongitude;
    private LatLng init_location;
    private LatLng dest_location;

    //clarence added for destination field
    private String destination;
    long time_length;
    private String campPic_url = "default_camp"; //default picture

    private GetGPSLocation gps;

    private TextView launchLat;
    private TextView launchLong;
    private ImageView photo;
    private Uri selectedImageURI;

    private Button return_camp;
    private Button launch_camp;
    private boolean isTakenStat;
    private String[] messages;
    private int index;
    private Spinner charitySpinner;
    private ArrayAdapter<String> charityAdapter;
    private String[] charityList = {"United Way", "Red Cross"}; //charity list

    private final static int REQUEST_CODE_PAY = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createcampaign);

        launchLat = (TextView) findViewById(R.id.initlocatlatitude);
        launchLong = (TextView) findViewById(R.id.initlocatlongitude);
        photo = (ImageView) findViewById(R.id.photo);

        return_camp = (Button) findViewById(R.id.return_camp);
        return_camp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //charity spinner
        charitySpinner = (Spinner) findViewById(R.id.charityin);
        charityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, charityList);
        charityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        charitySpinner.setAdapter(charityAdapter);


        launch_camp = (Button) findViewById(R.id.launchcamp);
        launch_camp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });

        launchGPS();

        Button getCoords = (Button) findViewById(R.id.getCoords);
        getCoords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGPS();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAY && resultCode == RESULT_OK) {
            addCampaign();
            Intent intent = new Intent(this, MapPage.class);
            startActivity(intent);
        } else if (requestCode == REQUEST_CODE_PAY && resultCode != RESULT_OK) {
            Intent intent = new Intent(this, MapPage.class);
            startActivity(intent);
        }
        try {
            // When an Image is picked
            if (requestCode == 1 && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                selectedImageURI = data.getData();
                photo.setImageURI(selectedImageURI);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    //add campaign to online database
    public void addCampaign() {

        //these are the strings we need to save for a new campaign
        EditText myText = (EditText) findViewById(R.id.titlein);
        title = myText.getText().toString();

        int position = charitySpinner.getSelectedItemPosition();
        charity = charityList[position];

        TextView myTextView = (TextView) findViewById(R.id.initlocatlatitude);
        initlocatlatitude = Double.parseDouble(myTextView.getText().toString());

        myTextView = (TextView) findViewById(R.id.initlocatlongitude);
        initlocatlongitude = Double.parseDouble(myTextView.getText().toString());

        init_location = new LatLng(initlocatlatitude, initlocatlongitude);

        myText = (EditText) findViewById(R.id.destination);
        destination = myText.getText().toString();

        Double lat = null;
        Double lng = null;
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            TextView address = (EditText) findViewById(R.id.destination);
            destination = address.getText().toString();
            List<Address> list = geocoder.getFromLocationName(destination, 1);
            Address returnAddress = list.get(0);
            lat = returnAddress.getLatitude();
            lng = returnAddress.getLongitude();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dest_location = new LatLng(lat, lng);

        myText = (EditText) findViewById(R.id.descriptionin);
        description = myText.getText().toString();


        time_length = System.currentTimeMillis();
        campPic_url = title + "_pic.jpg";

        StorageReference picRef = DB.images_ref.child(campPic_url);
        UploadTask uploadTask = picRef.putFile(selectedImageURI);

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


        DB.user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = DB.usersDBinteractor.read(userid, dataSnapshot);
                user.addInitCamp(title);
                DB.usersDBinteractor.update(user, DB.user_ref);

                Date currentDate = new Date();
                int INITIAL_ACCU = 0;
                String dateString = Algorithms.date_to_string(currentDate);

                Campaign myCampaign = new DestinationCampaign(title, INITIAL_ACCU, charity, description,
                        pledge, init_location, userid, time_length, dateString, destination, dest_location, campPic_url);

                DB.campDBinteractor.put(myCampaign, DB.root_ref);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, 1);
    }

    private void check() {
        messages = new String[6];
        index = 0;

        EditText myText = (EditText) findViewById(R.id.titlein);
        if (isEmpty(myText)) {
            messages[index++] = "- Enter a valid title";
            isTakenStat = false;
            checkCont();
        } else {
            isTaken(myText.getText().toString());
        }
    }

    private void checkCont() {

        if (isTakenStat) {
            messages[index++] = "- Enter a new title (the current one is taken)";
        }

        if (selectedImageURI == null) {
            messages[index++] = "- Upload a campaign image";
        }

        EditText myText = (EditText) findViewById(R.id.pledgein);
        if (isEmpty(myText) || UtilityMethod.text_to_long(myText) == 0) {
            messages[index++] = "- Enter a pledge amount";
        }

        TextView myTextView1 = (TextView) findViewById(R.id.initlocatlatitude);
        TextView myTextView2 = (TextView) findViewById(R.id.initlocatlongitude);
        if(myTextView1 == null || myTextView1.getText().equals("") ||
                myTextView2 == null || myTextView2.getText().equals("")){
            messages[index++] = "- Get your current location";
        }

        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            TextView address = (EditText) findViewById(R.id.destination);
            destination = address.getText().toString();
            List<Address> list = geocoder.getFromLocationName(destination, 1);
            if (list == null || list.size() == 0) {
                messages[index++] = "- Enter a valid destination";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        myText = (EditText) findViewById(R.id.descriptionin);
        if (isEmpty(myText)) {
            messages[index++] = "- Enter a description message";
        }

        if (index != 0) {
            Intent intent = new Intent(this, CatchEmptyFields.class)
                    .putExtra("Message0", messages[0])
                    .putExtra("Message1", messages[1])
                    .putExtra("Message2", messages[2])
                    .putExtra("Message3", messages[3])
                    .putExtra("Message4", messages[4])
                    .putExtra("Message5", messages[5]);
            startActivityForResult(intent, 1001);
        } else {
            EditText mT = (EditText) findViewById(R.id.pledgein);
            pledge = UtilityMethod.text_to_long(mT);

            Profile profile = Profile.getCurrentProfile();
            userid = profile.getId();
            //start pledge_agreement activity (where the user agrees to pay the specified amount in the future)
            startActivityForResult(new Intent(this, FuturePaymentAgreement.class)
                    .putExtra("PledgeAmount", pledge)
                    .putExtra("Title", title)
                    .putExtra("UserID", userid), REQUEST_CODE_PAY);
        }
    }


    private void isTaken(final String s) {
        DB.camp_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(s))
                    isTakenStat = true;
                else
                    isTakenStat = false;
                checkCont();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }

    private void launchGPS(){
        gps = new GetGPSLocation(CreateCampaign.this, CreateCampaign.this);

        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            launchLat.setText(Double.toString(latitude));
            launchLong.setText(Double.toString(longitude));
        } else {
            gps.showSettingsAlert();
        }
    }
}
