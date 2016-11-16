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

    private void sendAuthorizationToServer(PayPalAuthorization authorization) throws JSONException{

        String authorization_code = authorization.getAuthorizationCode();

//        APIContext context = new APIContext(CLIENT_ID, CLIENT_SECRET, "sandbox");
//        String refreshToken = FuturePayment.fetchRefreshToken(context, authorization_code);
//
//        context.setRefreshToken(refreshToken);

        // Create Payment Object
        /**
         * Send authorization response to server, where it can
         * exchange the authorization code for OAuth access and refresh tokens.
         *
         * Your server must then store these tokens, so that your server code
         * can execute payments for this user in the future.
         *
         * A more complete example that includes the required app-server to
         * PayPal-server integration is available from
         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
         */
    }

    //used for future payments (pledges)
    public void sendData() {
        // Get the Client Metadata ID from the SDK
        String metadataId = PayPalConfiguration.getClientMetadataId(this);

        //  Log.i("FuturePaymentExample", "Client Metadata ID: " + metadataId);
        // TODO: Send metadataId and transaction details to your server for processing with paypal
        // displayResultText("Client Metadata Id received from SDK");
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

//    Payer payer = new Payer();
//    payer.setPaymentMethod("paypal");
//    Amount amount = new Amount();
//    amount.setTotal("0.17");
//    amount.setCurrency("USD");
//    Transaction transaction = new Transaction();
//    transaction.setAmount(amount);
//    transaction.setDescription("This is the payment tranasction description.");
//    List<Transaction> transactions = new ArrayList<Transaction>();
//    transactions.add(transaction);
//
//    FuturePayment futurePayment = new FuturePayment();
//    futurePayment.setIntent("authorize");
//    futurePayment.setPayer(payer);
//    futurePayment.setTransactions(transactions);
//
//    Payment createdPayment = futurePayment.create(context, correlationId);
//    System.out.println(createdPayment.toString());
}