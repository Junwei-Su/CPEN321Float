package com.cpen321.floatproject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.api.payments.FuturePayment;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import org.json.JSONException;

public class FuturePaymentAgreement extends AppCompatActivity {

    private DatabaseReference databaseref = FirebaseDatabase.getInstance().getReference();
    Campaign myCampaign;
    String pledge_amount;
    String title;
    //set environment as the testing sandbox (can also be set to do actual transactions)
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CLIENT_ID = "Af5k4cThWWw1eesKCntlNvvQVKoL0OcT1ecA_tCjM4w1tetK0YuR1WndV_RGF_iJthLkFeHrwcikrEWf";
    private static final String CLIENT_SECRET = "EKL6xAq_d5Njo0ioytfPf0bxlizEY1Blnpl00lFupsVVTApbsAzrwRxFZu3DIJWIxbsTehSndlMd-lO8";

    //configure PayPal with the environment and client ID
    private static PayPalConfiguration paypal_configuration = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CLIENT_ID)
            .merchantName("FLOAT")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

    //identifying numbers to distinguish between instant payments and future payments
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.future_payment_agreement);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pledge_amount = extras.getString("PledgeAmount");
            title = extras.getString("Title");
        }
        showScript();

        Intent paypal_service = new Intent(this, PayPalService.class);
        paypal_service.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypal_configuration);
        startService(paypal_service); //the PayPal service

        Button donate_button = (Button) findViewById(R.id.return_from_consent);
        donate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start paypal
                Intent intent = new Intent(v.getContext(), MapPage.class);
                startActivity(intent);
            }
        });
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) throws JSONException, PayPalRESTException {
        String authorization_code = authorization.getAuthorizationCode();
        String metadata_id = PayPalConfiguration.getClientMetadataId(this);
        String environment = "sandbox";

        APIContext api_context = new APIContext(CLIENT_ID, CLIENT_SECRET, "sandbox");
        String refresh_token = FuturePayment.fetchRefreshToken(api_context, authorization_code);
        api_context.setRefreshToken(refresh_token);

        Gson gson = new Gson();
        String json_api_context = gson.toJson(api_context);

        //update the campaign to include the paypal tokens
        Query queryRef =  databaseref.child("campaigns").child(title);
        databaseref.child("campaigns").child(title);

        databaseref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myCampaign = dataSnapshot.child("campaigns").child("title").getValue(Campaign.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        myCampaign.json_api_context = json_api_context;
        myCampaign.metadata_id = metadata_id;
        databaseref.child("campaigns").child(title).setValue(myCampaign);
    }

    public void agreePayment(View pressed) {
        Intent intent = new Intent(FuturePaymentAgreement.this, PayPalFuturePaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypal_configuration);
        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }

    private void showScript() {
        TextView text_amount = (TextView) findViewById(R.id.pledge_amount);
        text_amount.setText(pledge_amount+" CAD");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            PayPalAuthorization auth = data
                    .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
            if (auth != null) {
                try {
                    String authorization_code = auth.getAuthorizationCode();
                    sendAuthorizationToServer(auth);

                } catch (JSONException e) {
                    Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                } catch (PayPalRESTException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("FuturePaymentExample", "The user canceled.");
        } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("FuturePaymentExample",
                    "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
        }
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}