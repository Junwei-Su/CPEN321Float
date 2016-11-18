package com.cpen321.floatproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MakeFuturePayment extends AppCompatActivity {

    Campaign myCampaign;
    Payment createdPayment;
    String title;
    String payment_amount;

    private DatabaseReference databaseref = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.future_payment);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title = extras.getString("Title");
        }
        showScript();

        //listener for returning to map page
        Button map_button = (Button) findViewById(R.id.return_map2);
        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapPage.class);
                startActivity(intent);
            }
        });
    }

    private void makePayment() throws PayPalRESTException, IOException {

        databaseref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myCampaign = dataSnapshot.child("campaigns").child(title).getValue(Campaign.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        String json = myCampaign.json_api_context;
        String metadata_id = myCampaign.metadata_id;
        payment_amount = myCampaign.goal_amount;
        //BigDecimal payment_amount = new BigDecimal(String.valueOf(myCampaign.goal_amount));

        Gson gson = new Gson();
        APIContext api_context = gson.fromJson(json, APIContext.class);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");
        Amount amount = new Amount();
        amount.setTotal(payment_amount); //get the amount from the database
        amount.setCurrency("CAD");
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription("Payment for succeeded campaign");
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

        com.paypal.api.payments.FuturePayment futurePayment = new com.paypal.api.payments.FuturePayment();
        futurePayment.setIntent("authorize");
        futurePayment.setPayer(payer);
        futurePayment.setTransactions(transactions);

        //get the metadataId and api_context from the database
        createdPayment = futurePayment.create(api_context, metadata_id);
    }

    private void showScript(){
        TextView text_title = (TextView) findViewById(R.id.campaign_title);
        TextView text_amount = (TextView) findViewById(R.id.future_payment_amount);
        TextView text_id = (TextView) findViewById(R.id.future_payment_id);

        //Showing the details of the transaction
        text_title.setText(payment_amount);
        text_amount.setText(payment_amount);
        //text_id.setText(createdPayment.toString());
    }
}
