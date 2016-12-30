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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cpen321.floatproject.users.User;
import com.cpen321.floatproject.database.UsersDBInteractor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.base.rest.PayPalRESTException;

import java.util.HashMap;
import java.util.Map;

public class FuturePaymentAgreement extends AppCompatActivity {

    private DatabaseReference databaseref = FirebaseDatabase.getInstance().getReference();
    private UsersDBInteractor usersDBInteractor = new UsersDBInteractor();

    private String pledge_amount;
    private String title;
    private String userid;

    //set environment as the testing sandbox (can also be set to do actual transactions)
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CLIENT_ID =
            "AfwKVfqhDY263y2WMId3yTpSlalnyNCP47ebWaVH0q0d20sXeO8je9-kM2zWHuV2zKXmxIkbf9UMggdF";
    private static final String CLIENT_SECRET =
            "EDoORf_P0yhpdjSDkCuQ1vDcIRQLGEtqXSZi34nA5UxZQ0xHqFuXNxUqDiLX6jYwGUHuewSbOcxBTMNQ";
    private static final String GET_REFRESH =
            "http://ec2-54-213-91-175.us-west-2.compute.amazonaws.com/paypal-sdk/refresh.php";

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

        Log.i("test", "test");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pledge_amount = extras.getString("PledgeAmount");
            title = extras.getString("Title");
            userid = extras.getString("UserID");
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

    private void sendAuthorizationToServer(PayPalAuthorization authorization) throws PayPalRESTException {
        final String authorization_code = authorization.getAuthorizationCode();
        final String metadata_id = PayPalConfiguration.getClientMetadataId(this);

        Log.d("auth", authorization_code);
        Log.d("metadata", metadata_id);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest putRequest = new StringRequest(Request.Method.POST, GET_REFRESH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Refresh Token Response", response);
                        final String refresh_token = response;

                        databaseref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = usersDBInteractor.read(userid, dataSnapshot);
                                user.setRefreshToken(refresh_token);
                                user.setMetadataid(metadata_id);
                                usersDBInteractor.update(user, databaseref);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Something Went Wrong");
                    }
                }
        ) {
            // We send the strings we want to the server in this method
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //for future payments
                params.put("authorizationCode", authorization_code);
                params.put("clientMetadataId", metadata_id);
                params.put("clientSecret", CLIENT_SECRET);
                params.put("clientId", CLIENT_ID);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

        };
        int socketTimeout = 5000; // 50 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        requestQueue.add(putRequest);
    }

    public void agreePayment(View pressed) {
        Intent intent = new Intent(FuturePaymentAgreement.this, PayPalFuturePaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypal_configuration);
        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }

    private void showScript() {
        TextView text_amount = (TextView) findViewById(R.id.pledge_amount);
        text_amount.setText(pledge_amount + " CAD");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            PayPalAuthorization auth = data
                    .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
            if (auth != null) {
                try {
                    sendAuthorizationToServer(auth);
                } catch (PayPalRESTException e) {
                    Log.i("failure", "paypalrestexception");
                    e.printStackTrace();
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("FuturePaymentExample", "The user canceled.");
        } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("FuturePaymentExample",
                    "Invalid PayPalConfiguration");
        }
        Intent intent = new Intent(this, MapPage.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}