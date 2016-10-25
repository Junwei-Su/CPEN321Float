package com.cpen321.floatproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by sfarinas on 10/17/2016.
 */
public class Map extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button button = (Button) findViewById(R.id.joincamp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start _Submenu activity
                Intent intent = new Intent(v.getContext(), JoinCampaign.class);
                startActivity(intent);
            }
        });

        button = (Button) findViewById(R.id.createcamp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start _Submenu activity
                Intent intent = new Intent(v.getContext(), CreateCampaign.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng ubc = new LatLng(49.261312, -123.253783);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mapbuttonpanel);
        int height = linearLayout.getHeight();

        map.setPadding(0, 0, 0, height);

        map.addMarker(new MarkerOptions()
                .position(ubc)
                .title("UBC"));

        Circle circle = map.addCircle(new CircleOptions()
                .center(ubc)
                .radius(10000)
                .strokeColor(ContextCompat.getColor(this, R.color.darkbackground))
                .fillColor(ContextCompat.getColor(this, R.color.background)));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ubc)   // Sets the center of the map to Mountain View
                .zoom(10)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}