package com.cpen321.floatproject.activities.PaypalActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cpen321.floatproject.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Displays the status of the instant payment made by the user
 */
public class InstantPaymentStatus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instant_payment_status);

        //button and listener for returning to map page
        Button map_button = (Button) findViewById(R.id.return_map);
        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Getting the Intent, along with the payment details
        Intent intent = getIntent();
        try {
            JSONObject jsonDetails = new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonDetails.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
        }
        catch (JSONException e) {
        }
    }

    private void showDetails(JSONObject jsonDetails, String paymentAmount) throws JSONException {

        TextView text_amount = (TextView) findViewById(R.id.payment_amount);
        TextView text_status= (TextView) findViewById(R.id.payment_status);
        TextView text_id = (TextView) findViewById(R.id.payment_id);

        //Showing the details of the transaction
        text_amount.setText(paymentAmount+" CAD");
        text_status.setText(jsonDetails.getString("state"));
        text_id.setText(jsonDetails.getString("id"));
    }
}