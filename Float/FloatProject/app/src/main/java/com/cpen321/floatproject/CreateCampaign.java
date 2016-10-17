package com.cpen321.floatproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by sfarinas on 10/17/2016.
 */
public class CreateCampaign extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createcampaign);

        final Button launchButton = (Button) findViewById(R.id.launchcamp);
        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addCampaign();
                launchCampaign();
            }
        });
    }

    //add campaign to online database
    private static void addCampaign (){}

    //launch campaign
    private static void launchCampaign(){}
}