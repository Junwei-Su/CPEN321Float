package com.cpen321.floatproject.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cpen321.floatproject.R;

public class CatchEmptyFields extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catch_empty_fields);

        Bundle extras = getIntent().getExtras();
        String m0 = extras.getString("Message0");
        String m1 = extras.getString("Message1");
        String m2 = extras.getString("Message2");
        String m3 = extras.getString("Message3");
        String m4 = extras.getString("Message4");
        String m5 = extras.getString("Message5");

        TextView a = (TextView) findViewById(R.id.a);
        a.setText(m0);
        TextView b = (TextView) findViewById(R.id.b);
        b.setText(m1);
        TextView c = (TextView) findViewById(R.id.c);
        c.setText(m2);
        TextView d = (TextView) findViewById(R.id.d);
        d.setText(m3);
        TextView e = (TextView) findViewById(R.id.e);
        e.setText(m4);
        TextView f = (TextView) findViewById(R.id.f);
        f.setText(m5);

        Button try_again = (Button) findViewById(R.id.try_again);
        try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}

