package com.cpen321.floatproject.activities;

import android.content.Intent;
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
import com.cpen321.floatproject.R;
import com.cpen321.floatproject.campaigns.Campaign;
import com.cpen321.floatproject.database.DB;
import com.cpen321.floatproject.users.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.paypal.base.rest.PayPalRESTException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MakeFuturePayment extends AppCompatActivity {

    private String title;
    private String userid;

    private static final String CLIENT_ID =
            "AfwKVfqhDY263y2WMId3yTpSlalnyNCP47ebWaVH0q0d20sXeO8je9-kM2zWHuV2zKXmxIkbf9UMggdF";
    private static final String CLIENT_SECRET =
            "EDoORf_P0yhpdjSDkCuQ1vDcIRQLGEtqXSZi34nA5UxZQ0xHqFuXNxUqDiLX6jYwGUHuewSbOcxBTMNQ";
    private static final String MAKE_PAYMENT =
            "http://ec2-54-213-91-175.us-west-2.compute.amazonaws.com/paypal-sdk/future_payment.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.future_payment);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title = extras.getString("Title");
            userid = extras.getString("UserID");
        }

        TextView text_title = (TextView) findViewById(R.id.campaign_title1);
        text_title.setText(title);

        try {
            makePayment();
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makePayment() throws PayPalRESTException, IOException {

        DB.root_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = DB.usersDBinteractor.read(userid, dataSnapshot.child("users"));
                Campaign campaign = DB.campDBinteractor.read(title, dataSnapshot.child("campaigns"));
                String amount = String.valueOf(campaign.getGoal_amount());
                makePayment(amount, user.getRefreshToken(), user.getMetadataid());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void makePayment(final String payment_amount, final String refresh_token, final String metadata_id){

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest putRequest = new StringRequest(Request.Method.POST, MAKE_PAYMENT,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // The server should return a refresh token
                        Log.d("Payment ID", response);
                        setContentView(R.layout.future_payment_status);
                        showScript(payment_amount, response);
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

                params.put("payment_amount", payment_amount);
                params.put("metadata_id", metadata_id);
                params.put("client_secret", CLIENT_SECRET);
                params.put("client_id", CLIENT_ID);
                params.put("refresh_token", refresh_token);
                params.put("payment_description", "payment");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }

        };
        int socketTimeout = 30000; // 30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        requestQueue.add(putRequest);
    }

    private void showScript(String payment_amount, String payment_id){
        TextView text_title = (TextView) findViewById(R.id.campaign_title2);
        TextView text_amount = (TextView) findViewById(R.id.amount);
        TextView text_id = (TextView) findViewById(R.id.future_payment_id);

        payment_amount = payment_amount.concat(" CAD");

        text_title.setText(title);
        text_amount.setText(payment_amount);
        text_id.setText(payment_id);

        Button map_button = (Button) findViewById(R.id.return_map2);
        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapPage.class);
                startActivity(intent);
            }
        });
    }
}