package com.cpen321.floatproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by sfarinas on 10/17/2016.
 */
public class Map extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        Button button = (Button) findViewById(R.id.joincamp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start _Submenu activity
                Intent intent = new Intent(v.getContext(), JoinCampaign.class);
                startActivity(intent);
            }
        });

        button = (Button) findViewById(R.id.createcamp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start _Submenu activity
                Intent intent = new Intent(v.getContext(), CreateCampaign.class);
                startActivity(intent);
            }
        });
    }
}
