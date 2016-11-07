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
import android.widget.TextView;

import com.facebook.Profile;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sfarinas on 10/17/2016.
 */
public class MapPage extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private DatabaseReference databaseref;
    private DatabaseReference usersref;
    private DatabaseReference listcampaignsref;
    private DatabaseReference listlocationsref;
    private ChildEventListener listlocationslistener;

    private GoogleMap map;
    private int buttonpanelheight;
    private int infowindowheight;

    private List<Circle> listCircles = new LinkedList<Circle>();

    private LatLng userlocation = new LatLng(49.251899, -123.231948);
    private CameraPosition defaultcamerapos = new CameraPosition.Builder()
            .target(userlocation)   // Sets the center of the map to Mountain View
            .zoom(15)                   // Sets the zoom
            .bearing(0)                // Sets the orientation of the camera to east
            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
            .build();                   // Creates a CameraPosition from the builder
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    boolean mapready = false; //true when onMapReady() is called
    boolean buttonpanelready = false;

    //initialize map paddings when map, buttonpanel, and
    private void layoutreadylistener(String caller){
        if(caller.equals("map"))
            mapready = true;
        else if(caller.equals("buttonpanel"))
            buttonpanelready = true;

        if(mapready && buttonpanelready)
            map.setPadding(0, 0, 0, buttonpanelheight);
    }

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

                        layoutreadylistener("buttonpanel");

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
                Intent intent = new Intent(v.getContext(), CampDetails.class);
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

        button = (Button) findViewById(R.id.details);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start _Submenu activity
                Intent intent = new Intent(v.getContext(), CampDetails.class);
                startActivity(intent);
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        Log.d("Tag", "buttonpanelheight in onCreate() = " + Integer.toString(buttonpanelheight));

        databaseref = FirebaseDatabase.getInstance().getReference();
        listcampaignsref = databaseref.child("campaigns");
        usersref = databaseref.child("users");

        Profile profile = Profile.getCurrentProfile();
        String userid = profile.getId();
        Log.d("Tag", "My userid is: " + userid);

        DatabaseReference thisuserref = usersref.child(userid);
        thisuserref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String thisusernamestr = dataSnapshot.child("name").getValue(String.class);
                TextView thisusername = (TextView) findViewById(R.id.thisusername);
                thisusername.setText(thisusernamestr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ChildEventListener listcampaignslistener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //get name of campaign
                String title = dataSnapshot.child("campaign_name").getValue(String.class);

                LatLng launchcoords = dataSnapshotToLatLng(dataSnapshot.child("initial_location"));

                //create marker at campaign launch location
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(launchcoords)
                        .title(title));

                //attaches campaign name to marker
                marker.setTag(title);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        listcampaignsref.addChildEventListener(listcampaignslistener);

        listlocationslistener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LatLng floatLocation = dataSnapshotToLatLng(dataSnapshot);

                //add a campaign circle to the map
                Circle circle = map.addCircle(new CircleOptions()
                        .center(floatLocation)
                        .radius(getResources().getInteger(R.integer.floatradius))
                        .strokeColor(ContextCompat.getColor(getApplicationContext(), R.color.circleoutline))
                        .fillColor(ContextCompat.getColor(getApplicationContext(), R.color.circlefill)));

                //add a campaign circle to the list of circles
                listCircles.add(circle);
                Log.d("Tag", "add circle: " + listCircles);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("Tag", "onMapReady()");

        this.map = map;

        //disable toolbar from appearing when a marker is clicked
        map.getUiSettings().setMapToolbarEnabled(false);

        map.setOnMarkerClickListener(this);

        map.animateCamera(CameraUpdateFactory.newCameraPosition(defaultcamerapos));

        map.setPadding(0, 0, 0, buttonpanelheight);

        layoutreadylistener("map");
        Log.d("Tag", "buttonpanelheight in onMapReady() = " + Integer.toString(buttonpanelheight));
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        // Get name of campaign
        String campaignname = (String) marker.getTag();

        // Get campaign starter
        String user =



        //make campaign preview pop-up window appear
        LinearLayout campinfo = (LinearLayout) findViewById(R.id.spacerparent);
        campinfo.setVisibility(View.VISIBLE);

        //changing fields of details window
        TextView tv = (TextView) findViewById(R.id.campaigntitle);
        tv.setText(campaignname);

        tv = (TextView) findViewById(R.id.username);
        tv.setText(user);





            //stop updating map from last clicked campaign
            databaseref.removeEventListener(listlocationslistener);

            Log.d("Tag", "Started removing circles.");
            //remove old circles
            while(listCircles.size()!=0) {
                //remove circle from map
                listCircles.get(0).remove();
                //remove circle from list
                listCircles.remove(0);
                Log.d("Tag", "remove circle: " + listCircles);
            }

            //get database reference to list of locations of different campaign
            listlocationsref = listcampaignsref.child(campaignname).child("list_locations");

            //attach listener to list of locations of different campaign
            listlocationsref.addChildEventListener(listlocationslistener);

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
                "MapPage Page", // TODO: Define a title for the content shown.
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
                "MapPage Page", // TODO: Define a title for the content shown.
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

    private LatLng dataSnapshotToLatLng (DataSnapshot datasnapshot){
        //get coordinates of campaign launch location
        Map<String, Double> mapcoords = (HashMap<String,Double>) datasnapshot.getValue();

        //create LatLng object out of coordinates
        return new LatLng(mapcoords.get("latitude"), mapcoords.get("longitude"));
    };
}