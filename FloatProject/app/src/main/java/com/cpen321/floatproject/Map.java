package com.cpen321.floatproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by sfarinas on 10/17/2016.
 */
public class Map extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    GoogleMap map;
    int buttonpanelheight;
    int infowindowheight;

    LatLng ubc = new LatLng(49.261312, -123.253783);
    CameraPosition defaultcamerapos = new CameraPosition.Builder()
            .target(ubc)   // Sets the center of the map to Mountain View
            .zoom(10)                   // Sets the zoom
            .bearing(0)                // Sets the orientation of the camera to east
            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
            .build();                   // Creates a CameraPosition from the builder
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Tag", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        LinearLayout infowindow = (LinearLayout) findViewById(R.id.infowindow);
        infowindow.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //called when layout is ready yet before drawn

                        LinearLayout infowindow = (LinearLayout) findViewById(R.id.infowindow);

                        infowindowheight = infowindow.getHeight();

                        Log.d("Tag", "infowindowheight = " + infowindowheight);

                        if(Build.VERSION.SDK_INT <= 14)
                            infowindow.getViewTreeObserver().removeGlobalOnLayoutListener( this );
                        else
                            infowindow.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
        );

        LinearLayout mapbuttonpanel = (LinearLayout) findViewById(R.id.mapbuttonpanel);
        infowindow.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //called when layout is ready yet before drawn

                        LinearLayout mapbuttonpanel = (LinearLayout) findViewById(R.id.mapbuttonpanel);

                        buttonpanelheight = mapbuttonpanel.getHeight();

                        Log.d("Tag", "buttonpanelheight = " + buttonpanelheight);

                        map.setPadding(0, 0, 0, buttonpanelheight);

                        if(Build.VERSION.SDK_INT <= 14)
                            mapbuttonpanel.getViewTreeObserver().removeGlobalOnLayoutListener( this );
                        else
                            mapbuttonpanel.getViewTreeObserver().removeOnGlobalLayoutListener( this );
                    }
                }
        );

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

        ImageButton imageButton = (ImageButton) findViewById(R.id.closeWindow);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout infowindow = (LinearLayout) findViewById(R.id.spacerparent);
                infowindow.setVisibility(View.GONE);

                map.setPadding(0, 0, 0, buttonpanelheight);
                map.animateCamera(CameraUpdateFactory.newCameraPosition(defaultcamerapos));
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        Log.d("Tag", Integer.toString(buttonpanelheight));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("Tag", "onMapReady()");

        this.map = map;

        //disable toolbar from appearing when a marker is clicked
        map.getUiSettings().setMapToolbarEnabled(false);

        map.addMarker(new MarkerOptions()
                .position(ubc)
                .title("UBC"));

        Circle circle = map.addCircle(new CircleOptions()
                .center(ubc)
                .radius(10000)
                .strokeColor(ContextCompat.getColor(this, R.color.darkerbackground))
                .fillColor(ContextCompat.getColor(this, R.color.background)));

        map.setOnMarkerClickListener(this);

        map.animateCamera(CameraUpdateFactory.newCameraPosition(defaultcamerapos));

        map.setPadding(0, 0, 0, buttonpanelheight);

        Log.d("Tag", Integer.toString(buttonpanelheight));
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        // Can retrieve the data from the marker.
        //Integer clickCount = (Integer) marker.getTag();

        LinearLayout campinfo = (LinearLayout) findViewById(R.id.spacerparent);
        campinfo.setVisibility(View.VISIBLE);

        map.setPadding(0, 0, 0, buttonpanelheight + infowindowheight);

        map.animateCamera(CameraUpdateFactory.newCameraPosition(defaultcamerapos));

        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Map Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.cpen321.floatproject/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Map Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.cpen321.floatproject/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}