package com.cpen321.floatproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by sfarinas on 10/17/2016.
 */
public class CampDetails extends Activity {

    private DatabaseReference databaseref;
    private DatabaseReference campaignref;
    private ValueEventListener campaignlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campdetails);

        Intent intent = getIntent();
        final String theCampaign = intent.getStringExtra("key");
        Log.d("Tag", "camptitleinfo2 = " + theCampaign);

        databaseref = FirebaseDatabase.getInstance().getReference();
        campaignref = databaseref.child("campaigns").child(theCampaign);

        TextView tv = (TextView) findViewById(R.id.camptitledeets);
        tv.setText(theCampaign);

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