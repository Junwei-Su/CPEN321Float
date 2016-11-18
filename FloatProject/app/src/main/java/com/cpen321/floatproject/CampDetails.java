package com.cpen321.floatproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by sfarinas on 10/17/2016.
 */
public class CampDetails extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campdetails);

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