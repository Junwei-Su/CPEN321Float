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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.EventListener;

/**
 * Created by sfarinas on 10/17/2016.
 */
public class CampDetails extends Activity {

    private DatabaseReference databaseref;
    private DatabaseReference campaignref;

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
        campaignref = databaseref.child("campaigns").child(theCampaign);
        Log.d("Tag", "testingreference = " + campaignref.toString());

        campaignref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String user = dataSnapshot.child("owner_account").getValue(String.class);
                Log.d("Tag", "campdeetuser = " + user);
                TextView tv = (TextView) findViewById(R.id.userdeets);
                tv.setText(user);

                String charity = dataSnapshot.child("charity").getValue(String.class);
                tv = (TextView) findViewById(R.id.charitydeets);
                tv.setText(charity);

                String description = dataSnapshot.child("description").getValue(String.class);
                tv = (TextView) findViewById(R.id.descriptiondeets);
                tv.setText(description);

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

    }

}