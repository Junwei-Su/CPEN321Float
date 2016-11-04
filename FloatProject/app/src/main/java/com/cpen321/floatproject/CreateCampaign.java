package com.cpen321.floatproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
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

        Button button = (Button) findViewById(R.id.launchcamp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addCampaign();
                //launchCampaign();
            }
        });

        button = (Button) findViewById(R.id.savecamp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Tag","buttonPress2");
            }
        });

    }

    //add campaign to online database
    public void addCampaign (){

        //these are the strings we need to save for a new campaign
        EditText myText = (EditText) findViewById(R.id.titlein);
        String title = myText.getText().toString();
        Log.d("Tag",title);

        myText = (EditText) findViewById(R.id.goalin);
        String goal = myText.getText().toString();
        Log.d("Tag",goal);

        myText = (EditText) findViewById(R.id.pledgein);
        String pledge = myText.getText().toString();
        Log.d("Tag",pledge);

        myText = (EditText) findViewById(R.id.descriptionin);
        String description = myText.getText().toString();
        Log.d("Tag",description);
    }

    //launch campaign
    private static void launchCampaign(){}

    //save campaign
    private static void saveCampaign(){}
}