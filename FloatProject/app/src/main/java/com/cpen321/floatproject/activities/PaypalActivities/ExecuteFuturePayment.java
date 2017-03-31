package com.cpen321.floatproject.activities.PaypalActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

/**
 * Executes a future payment, charges the user the specified amount without retrieving further permissions from the user
 */
public class ExecuteFuturePayment extends AppCompatActivity {

    private String title; // title of the campaign
    private String userid; //user that is being charged

    //ID and Secret associated with the paypal account that payments will be directed to
    private static final String CLIENT_ID =
            "AfwKVfqhDY263y2WMId3yTpSlalnyNCP47ebWaVH0q0d20sXeO8je9-kM2zWHuV2zKXmxIkbf9UMggdF";
    private static final String CLIENT_SECRET =
            "EDoORf_P0yhpdjSDkCuQ1vDcIRQLGEtqXSZi34nA5UxZQ0xHqFuXNxUqDiLX6jYwGUHuewSbOcxBTMNQ";
    //location of the server/php script
    private static final String MAKE_PAYMENT =
            "http://ec2-54-213-91-175.us-west-2.compute.amazonaws.com/paypal-sdk/future_payment.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.execute_future_payment);

        //retrieve the campaign title and the user that started the campaign
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title = extras.getString("Title");
            userid = extras.getString("UserID");
        }

        try {
            //make the payment
            makePayment();
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makePayment() throws PayPalRESTException, IOException {

        //retrieve the amount we need to charge to user from the database. Also retrieve the refresh token
        //and metadata id so that the payment can be processed
        DB.root_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = DB.usersDBinteractor.read(userid, dataSnapshot.child("users"));
                Log.i("user", userid);
                Campaign campaign = DB.campDBinteractor.read(title, dataSnapshot.child("campaigns"));
                String amount = String.valueOf(campaign.getGoal_amount());
                Log.i("amount", amount);
                Log.i("refresh", user.getRefreshToken());
                Log.i("metadata", user.getMetadataid());
                makePayment(amount, user.getRefreshToken(), user.getMetadataid());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void makePayment(final String payment_amount, final String refresh_token, final String metadata_id){

        //send relevant information to the server, where the payment will be processed
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest putRequest = new StringRequest(Request.Method.POST, MAKE_PAYMENT,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        //the server should return a refresh token.
                        Log.d("Payment ID", response);
                        setResult(RESULT_OK);
                        finish();
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
            //we send the strings we want to the server in this method
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
        int socketTimeout = 30000; //set the timeout limit to 30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        requestQueue.add(putRequest);
    }
}