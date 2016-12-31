package com.cpen321.floatproject.activities;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.cpen321.floatproject.R;
import com.facebook.Profile;

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

    public void toCreateUser(View view) {
        Intent intent = new Intent(this, CreateUser.class);
        startActivity(intent);
    }

}