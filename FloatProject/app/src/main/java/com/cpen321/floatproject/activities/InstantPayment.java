package com.cpen321.floatproject.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.cpen321.floatproject.R;
import com.cpen321.floatproject.campaigns.Campaign;
import com.cpen321.floatproject.database.DB;
import com.cpen321.floatproject.users.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class InstantPayment extends AppCompatActivity {

    //set environment as the testing sandbox (can also be set to do actual transactions)
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CLIENT_ID = "AfwKVfqhDY263y2WMId3yTpSlalnyNCP47ebWaVH0q0d20sXeO8je9-kM2zWHuV2zKXmxIkbf9UMggdF";

    private String payment_amount;
    private String title;
    private String campaign_owner_id;
    private String current_user_id;

    //configure PayPal with the environment and client ID
    private static PayPalConfiguration paypal_configuration = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CLIENT_ID);

    private static final int REQUEST_CODE_PAYMENT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instant_payment);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title = extras.getString("Title");
            campaign_owner_id = extras.getString("Owner_id");
            current_user_id= extras.getString("Current User_id");
        }

        Intent paypal_service = new Intent(this, PayPalService.class);
        paypal_service.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypal_configuration);
        startService(paypal_service); //the PayPal service
    }

    public void makeDonation(View view){

        //get the dollar amount
        EditText e1 = (EditText) findViewById(R.id.dollar_donation_input);
        String dollar_amount = e1.getText().toString();

        //get the cents amount
        EditText e2 = (EditText) findViewById(R.id.cents_donation_input);
        String cents_amount = e2.getText().toString();

        if (cents_amount.length() <= 1)
            cents_amount = ".0".concat(cents_amount);
        else
            cents_amount = ".".concat(cents_amount);

        payment_amount = dollar_amount.concat(cents_amount);

        PayPalPayment payment =
                new PayPalPayment(new BigDecimal(String.valueOf(payment_amount)), "USD", "Test Payment", PayPalPayment.PAYMENT_INTENT_SALE);

        //create intent and send the same configuration for restart resiliency
        Intent paypal_intent = new Intent(this, PaymentActivity.class);
        paypal_intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypal_configuration);
        paypal_intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(paypal_intent, REQUEST_CODE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        if (confirmation.getProofOfPayment().getState().equals("approved")) {
                            DB.root_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    User current_user = DB.usersDBinteractor.read(current_user_id, dataSnapshot.child("users"));
                                    User campaign_owner = DB.usersDBinteractor.read(campaign_owner_id, dataSnapshot.child("users"));
                                    Campaign campaign = DB.campDBinteractor.read(title, dataSnapshot.child("campaigns"));

                                    current_user.addAmount_donated(Double.valueOf(payment_amount));
                                    campaign_owner.addAmount_raised(Double.valueOf(payment_amount));
                                    campaign.add_donation(Double.valueOf(payment_amount));

                                    DB.usersDBinteractor.update(current_user, DB.user_ref);
                                    DB.usersDBinteractor.update(campaign_owner, DB.user_ref);
                                    DB.campDBinteractor.update(campaign, DB.camp_ref);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                        //Get the payment details
                        String payment_details = confirmation.toJSONObject().toString(4);

                        //Starting a new activity to show the payment status
                        startActivity(new Intent(this, InstantPaymentStatus.class)
                                .putExtra("PaymentDetails", payment_details)
                                .putExtra("PaymentAmount", payment_amount));

                        finish();

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}

