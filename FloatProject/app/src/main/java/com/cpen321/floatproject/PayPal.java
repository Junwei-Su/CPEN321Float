package com.cpen321.floatproject;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.net.Uri;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import org.json.JSONException;
import java.math.BigDecimal;



public class PayPal extends AppCompatActivity {

    //set environment as the testing sandbox (can also be set to do actual transactions)
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CLIENT_ID = "Af5k4cThWWw1eesKCntlNvvQVKoL0OcT1ecA_tCjM4w1tetK0YuR1WndV_RGF_iJthLkFeHrwcikrEWf";

    private String payment_amount;
    TextView p_response;

    //configure PayPal with the environment and client ID
    private static PayPalConfiguration p_configuration = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CLIENT_ID)
            .merchantName("test")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

    private static final int REQUEST_CODE_PAYMENT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pal);

        Intent p_service = new Intent(this, PayPalService.class);
        p_service.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, p_configuration);
        startService(p_service); //the PayPal service
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
        Intent p_intent = new Intent(this, PaymentActivity.class);
        p_intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, p_configuration);
        p_intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(p_intent, REQUEST_CODE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        //Get the payment details
                        String payment_details = confirmation.toJSONObject().toString(4);

                        //Starting a new activity to show the payment status
                        startActivity(new Intent(this, PaymentStatus.class)
                                .putExtra("PaymentDetails", payment_details)
                                .putExtra("PaymentAmount", payment_amount));

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

