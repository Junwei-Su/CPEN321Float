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
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.api.payments.FuturePayment;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class FuturePaymentAgreement extends AppCompatActivity {

    private DatabaseReference databaseref = FirebaseDatabase.getInstance().getReference();
    User user;
    String pledge_amount;
    String title;
    String userid;
    //set environment as the testing sandbox (can also be set to do actual transactions)
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CLIENT_ID =
            "AaeuSB3UXO3lpq6BSvl6wan9CzqH71hDWnF2K8iBKgJPGGBtkEAvoTQ-ooxWWaCioyoYDE-x0wgHjsuQ";
    private static final String CLIENT_SECRET =
            "ED5YbURCLIgiNGuO8OfL9yY3MPUNHvejc_0MjVCzKhxR_v1pQVn9MqO944bdEMUp1TGiF-V65VU9LMhW";
    private static final String GET_REFRESH =
            "http://ec2-54-213-91-175.us-west-2.compute.amazonaws.com/paypal-sdk/test_refresh.php";

//    private static final String CLIENT_ID = "AX8PdEKmNADu_-4PQJ5sGojLRhWzV4yLXkOb9tGVRi_yLsii9a7SNNQ4g_VOr7VexhmjDgXUvf2vqZaN";
//    private static final String CLIENT_SECRET = "EC8mkhijObvyvT29FZzGQXJDaU6F6mS-IxUcUWHLFIhruWvoUPEP8wzspEPJYae0w9jUEgK3yUZzAwi_";

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
        String environment = "sandbox";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest putRequest = new StringRequest(Request.Method.POST, GET_REFRESH,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        // The server should return a refresh token
                        Log.d("Refresh Token Response", response);
                        String refresh_token = response;
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Something Went Wrong");
                    }
                }
        ) {
            // We send the strings we want to the server in this method
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String> ();
                //for future payments
                params.put("authorizationCode", authorization_code);
                params.put("clientMetadataId", metadata_id);
                params.put("clientSecret",CLIENT_SECRET);
                params.put("clientId",CLIENT_ID);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }

        };
        requestQueue.add(putRequest);

       APIContext api_context = new APIContext(CLIENT_ID, CLIENT_SECRET, "sandbox");
        //APIContext api_context = new APIContext(CLIENT_ID, CLIENT_SECRET, "sandbox");
        //String refresh_token = FuturePayment.fetchRefreshToken(api_context, authorization_code);
        //Log.i("refresh", refresh_token);
        //api_context.setRefreshToken(refresh_token);

        Gson gson = new Gson();
        String json_api_context = gson.toJson(api_context);

        TextView text_amount = (TextView) findViewById(R.id.pledge_amount);
        text_amount.setText("send");

        Log.i("json_api_context", json_api_context);
        Log.i("metadata", metadata_id);

        databaseref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.child("users").child(userid).getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        user.json_api_context = json_api_context;
        user.metadata_id = metadata_id;
        databaseref.child("users").child(userid).setValue(user);
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
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}