package com.cpen321.floatproject;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.Profile;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Profile.getCurrentProfile()!=null)
            startMapActivity();

    }

    //starts Map activity
    private void startMapActivity(){
        Intent intent = new Intent(this, Map.class);
        startActivity(intent);
    }
}