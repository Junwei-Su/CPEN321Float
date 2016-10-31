package test.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;

public class PayPal extends AppCompatActivity {

    //holds the status (pink icon)
    TextView p_response;

    //The PayPal serivce
    Intent p_service;

    //set environment as the testing sandbox (can also be set to do actual transactions)
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CLIENT_ID = "Af5k4cThWWw1eesKCntlNvvQVKoL0OcT1ecA_tCjM4w1tetK0YuR1WndV_RGF_iJthLkFeHrwcikrEWf";

    //configure PayPal with the environment and client ID
    private static PayPalConfiguration p_configuration = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CLIENT_ID);

    //identifying numbers to distinguish between instant payments and future payments
    private static final int REQUEST_CODE_PAYMENT = 101;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pal);

        p_response = (TextView) findViewById(R.id.response);
        p_service = new Intent(this, PayPalService.class);

        p_service.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, p_configuration);
        startService(p_service); //the PayPal service
    }

    void makeDonation(View view){
        //get the user input then convert it to an integer
        EditText editText = (EditText) findViewById(R.id.user_input);
       String s_amount = editText.getText().toString();
        int amount = 0;
        try{
        amount = Integer.parseInt(s_amount); }
        catch (NumberFormatException e) {}

        PayPalPayment payment =
                new PayPalPayment(new BigDecimal(amount), "USD", "Test Payment", PayPalPayment.PAYMENT_INTENT_SALE);

        //create intent and send the same configuration for restart resiliency
        Intent p_intent = new Intent(this, PaymentActivity.class);
        p_intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, p_configuration);
        p_intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(p_intent, REQUEST_CODE_PAYMENT);
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {
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
    public void makePledgeaa(View view) {
        // Get the Client Metadata ID from the SDK
        String metadataId = PayPalConfiguration.getClientMetadataId(this);

        //  Log.i("FuturePaymentExample", "Client Metadata ID: " + metadataId);
        // TODO: Send metadataId and transaction details to your server for processing with paypal
        // displayResultText("Client Metadata Id received from SDK");
    }

    public void makePledge(View pressed) {
        Intent intent = new Intent(this, PayPalFuturePaymentActivity.class);

        PayPalPayment payment =
                new PayPalPayment(new BigDecimal(100), "USD", "Test future payment", PayPalPayment.PAYMENT_INTENT_SALE);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, p_configuration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_PAYMENT || requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if(resultCode == Activity.RESULT_OK) {
                // confirm that payment worked
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation != null) {
                    String status = confirmation.getProofOfPayment().getState();
                    if(status.equals("approved")) // if the payment worked, the state equals approved
                        p_response.setText("payment approved");
                    else
                        p_response.setText("error in the payment");
                }
                else
                    p_response.setText("confirmation is null");
            }
        }
    }

   /* @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }*/

}
