package test.test;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.net.Uri;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
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

    //configure PayPal with the environment and client ID
    private static PayPalConfiguration p_configuration = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CLIENT_ID)
            .merchantName("test")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

    //identifying numbers to distinguish between instant payments and future payments
    private static final int REQUEST_CODE_PAYMENT = 101;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 102;

    private int future_payment_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pal);

        Intent p_service = new Intent(this, PayPalService.class);

        p_service.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, p_configuration);
        startService(p_service); //the PayPal service
    }

    int getAmount(EditText editText){
        String s_amount = editText.getText().toString();
        int amount = 0;
        try{
            amount = Integer.parseInt(s_amount); }
        catch (NumberFormatException e) {
            Log.e("InstantPaymentExample", "Error in receiving user input");
        }
        return amount;
    }

    public void makeDonation(View view){
        //get the user input then convert it to an integer
        int amount = getAmount((EditText) findViewById(R.id.user_donation_input));

        PayPalPayment payment =
                new PayPalPayment(new BigDecimal(amount), "USD", "Test Payment", PayPalPayment.PAYMENT_INTENT_SALE);

        //create intent and send the same configuration for restart resiliency
        Intent p_intent = new Intent(this, PaymentActivity.class);
        p_intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, p_configuration);
        p_intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(p_intent, REQUEST_CODE_PAYMENT);
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) throws JSONException{
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
    public void onMakePledgePressed(View view) {
        // Get the Client Metadata ID from the SDK
        String metadataId = PayPalConfiguration.getClientMetadataId(this);

        //future_payment_amount (amount to be paid later)
        Log.i("FuturePaymentExample", "Client Metadata ID: " + metadataId);
        // TODO: Send metadataId and transaction details to your server for processing with paypal
        // displayResultText("Client Metadata Id received from SDK");
    }

    public void makePledge(View pressed) {
        future_payment_amount = getAmount((EditText) findViewById(R.id.user_pledge_input));

        Intent intent = new Intent(this, PayPalFuturePaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, p_configuration);

        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FUTURE_PAYMENT){
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth = data
                        .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        //String authorization_code = auth.getAuthorizationCode();

                        sendAuthorizationToServer(auth);
                    }
                    catch (JSONException e) {
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

        else if(requestCode == REQUEST_CODE_PAYMENT ) {
            if(resultCode == Activity.RESULT_OK) {
                // confirm that payment worked
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation != null) {
                    String status = confirmation.getProofOfPayment().getState();
                    if(status.equals("Approved")) // if the payment worked, the state equals approved
                        Log.i("InstantPaymentExample", "Payment approved");
                    else
                        Log.i("InstantPaymentExample", "error in the payment");
                }
                else
                    Log.i("InstantPaymentExample", "confirmation is null");
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
