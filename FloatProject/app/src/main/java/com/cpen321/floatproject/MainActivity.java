package com.cpen321.floatproject;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.cpen321.floatproject.charities.Charity;
import com.cpen321.floatproject.database.CharityDBinteractor;
import com.facebook.Profile;
import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Profile.getCurrentProfile()!=null)
            startMapActivity();

    }

    //starts MapPage activity
    private void startMapActivity(){
        Intent intent = new Intent(this, MapPage.class);
        startActivity(intent);

    }
}