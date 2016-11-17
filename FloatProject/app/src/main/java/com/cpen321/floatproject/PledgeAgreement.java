package com.cpen321.floatproject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.api.payments.FuturePayment;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import org.json.JSONException;

public class PledgeAgreement extends AppCompatActivity {

    private DatabaseReference databaseref = FirebaseDatabase.getInstance().getReference();

    String pledge_amount;
    //set environment as the testing sandbox (can also be set to do actual transactions)
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CLIENT_ID = "Af5k4cThWWw1eesKCntlNvvQVKoL0OcT1ecA_tCjM4w1tetK0YuR1WndV_RGF_iJthLkFeHrwcikrEWf";
    public static final String CLIENT_SECRET = "";

    //configure PayPal with the environment and client ID
    private static PayPalConfiguration p_configuration = new PayPalConfiguration()
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
        setContentView(R.layout.activity_pledge_agreement);

       Bundle extras = getIntent().getExtras();
        if (extras != null)
            pledge_amount = extras.getString("PledgeAmount");
        showScript();

        Intent paypal_service = new Intent(this, PayPalService.class);
        paypal_service.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, p_configuration);
        startService(paypal_service); //the PayPal service
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) throws JSONException, PayPalRESTException {
        String metadataId = PayPalConfiguration.getClientMetadataId(this);
        String authorization_code = authorization.getAuthorizationCode();
        APIContext api_context = new APIContext(CLIENT_ID, CLIENT_SECRET, "sandbox");
        String refresh_token = FuturePayment.fetchRefreshToken(api_context, authorization_code);
        api_context.setRefreshToken(refresh_token);
        //send metadataId to firebase
        //send APIContext to firebase
    }

    public void agreePayment(View pressed) {
        Intent intent = new Intent(this, PayPalFuturePaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, p_configuration);
        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }

    private void showScript() {
        TextView text_amount = (TextView) findViewById(R.id.pledge_amount);
        text_amount.setText(pledge_amount+" CAD");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
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
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}