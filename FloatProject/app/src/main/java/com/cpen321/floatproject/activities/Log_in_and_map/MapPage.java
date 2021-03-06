package com.cpen321.floatproject.activities.Log_in_and_map;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cpen321.floatproject.GPS.GetGPSLocation;
import com.cpen321.floatproject.R;
import com.cpen321.floatproject.activities.CampActivities.CampDetails;
import com.cpen321.floatproject.activities.CampActivities.CampListView;
import com.cpen321.floatproject.activities.CampActivities.CreateCampaign;
import com.cpen321.floatproject.campaigns.Campaign;
import com.cpen321.floatproject.charities.Charity;
import com.cpen321.floatproject.database.CampsDBInteractor;
import com.cpen321.floatproject.database.CharityDBinteractor;
import com.cpen321.floatproject.database.DB;
import com.cpen321.floatproject.database.UsersDBInteractor;
import com.cpen321.floatproject.users.User;
import com.cpen321.floatproject.utilities.ActivityUtility;
import com.cpen321.floatproject.utilities.UtilityMethod;
import com.facebook.Profile;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sfarinas on 10/17/2016.
 */

/*
 * this class represent the map page activity in the application
 */
public class MapPage extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    //references to points in Firebase database
    private DatabaseReference campaignref;

    //listeners for realtime database
    private ValueEventListener listlocationslistener;
    private ChildEventListener listcampaignslistener;
    private ValueEventListener campaignslistener;
    private ValueEventListener charitieslistener;
    private ValueEventListener launchuserslistener;

    //database interactors
    private CampsDBInteractor campsDBInteractor;
    private CharityDBinteractor charityDBinteractor;
    private UsersDBInteractor usersDBInteractor;

    private GoogleMap map;

    //views
    RelativeLayout infowindow;



    //height, in pixels, of buttonpanel and infowindow
    private int buttonpanelheight;
    private int infowindowheight;
    private int padding = 0;

    //top of views
    private int buttonpaneltop;
    private int infowindowtop;

    //animation durations
    private long slideduration = 300;


    //infowindow visibility flag
    private boolean infowindowvisible = false;

    //list of circles presently on 'map'
    private List<Circle> listCircles = new LinkedList<>();


    private GetGPSLocation currentLocation;

    //default location of user on map on activity creation
    private LatLng userlocation;// = new LatLng(49.251899, -123.231948);

    //default position of camera on map on activity creation
    private CameraPosition defaultcamerapos;

    private String charityname;
    private String launchusername; //facebook numerical id of person who launched campaign
    private String launchusernamestr;
    private String campaignname;

    //filenames of pictures
    private String campaignpic;
    private String launchuserpic;
    private String charitypic;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    boolean mapready = false; //true when onMapReady() is called
    boolean buttonpanelready = false;

    //initialize map paddings when map and buttonpanel are ready
    private void layoutreadylistener(String caller) {
        if (caller.equals("map"))
            mapready = true;
        else if (caller.equals("buttonpanel"))
            buttonpanelready = true;

        if (mapready && buttonpanelready)
            map.setPadding(padding, padding, padding, buttonpanelheight);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        campsDBInteractor = new CampsDBInteractor();
        charityDBinteractor = new CharityDBinteractor();
        usersDBInteractor = new UsersDBInteractor();

        if (!locationOn()) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        } else {

            //get current location with gps, prompts if gps off
            currentLocation = new GetGPSLocation(MapPage.this, MapPage.this);
            if (currentLocation.canGetLocation()) {

                userlocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                Log.d("Tag", "My location is: " + userlocation);
            } else {
                currentLocation.showSettingsAlert();
            }

            //set default camera position to the current location
            defaultcamerapos = new CameraPosition.Builder()
                    .target(userlocation)   // Sets the center of the map to Mountain View
                    .zoom(13)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();

            infowindow = (RelativeLayout) findViewById(R.id.infowindow);

            //listen to infowindow once to obtain height in pixels
            infowindow.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            //called when layout is ready yet before drawn

                            infowindowheight = infowindow.getHeight();
                            infowindowtop = infowindow.getTop();

                            if (Build.VERSION.SDK_INT <= 14)
                                infowindow.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            else
                                infowindow.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
            );

            LinearLayout mapbuttonpanel = (LinearLayout) findViewById(R.id.mapbuttonpanel);
            mapbuttonpanel.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            //called when layout is ready yet before drawn

                            LinearLayout mapbuttonpanel = (LinearLayout) findViewById(R.id.mapbuttonpanel);
                            buttonpanelheight = mapbuttonpanel.getHeight();
                            buttonpaneltop = mapbuttonpanel.getTop();

                            //alert layoutreadylistener buttonpanel is created
                            layoutreadylistener("buttonpanel");

                            //remove listener
                            if (Build.VERSION.SDK_INT <= 14)
                                mapbuttonpanel.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            else
                                mapbuttonpanel.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
            );

            //get a handle on map
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            //wire createcamp button to CreateCampaign activity
            ImageButton imageButton = (ImageButton) findViewById(R.id.createcamp);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //start _Submenu activity
                    Intent intent = new Intent(v.getContext(), CreateCampaign.class);
                    startActivity(intent);

                }
            });

            //after infowindow is closed, update map paddings and camera
            imageButton = (ImageButton) findViewById(R.id.closewindow);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //update map margins after window is gone
                    map.setPadding(padding, padding, padding, buttonpanelheight);

                    //move camera to fit campaign circles in screen
                    zoomFitCircles();

                    //animate infowindow disappearing
                    ObjectAnimator infowindowanimator = ObjectAnimator.ofFloat(infowindow, View.Y, infowindowtop, buttonpaneltop);
                    infowindowanimator.setDuration(slideduration);
                    infowindowanimator.start();
                    infowindowvisible = false;
                }
            });

            //click on map icon to return map to currentlocation
            ImageButton centreButton = (ImageButton) findViewById(R.id.centreMap);
            centreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(defaultcamerapos));
                }
            });

            //clarence code for camp list viewer
            ImageButton listButton = (ImageButton) findViewById(R.id.listCamp);
            listButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //user click the list button
                    //UI go to list page
                    Intent jumpToListView = new Intent(v.getContext(), CampListView.class);
                    //need to pass the current location of the user
                    startActivity(jumpToListView);

                }
            });

            //wire details button to CampDetails activity
            Button button = (Button) findViewById(R.id.details);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextView tv = (TextView) findViewById(R.id.campaigntitle);

                    String campaignname = tv.getText().toString();

                    //start CampDetails activity
                    Intent intent = new Intent(v.getContext(), CampDetails.class);
                    intent.putExtra("key", campaignname);
                    startActivity(intent);

                    map.setPadding(padding, padding, padding, buttonpanelheight);
                    zoomFitCircles();
                    ObjectAnimator infowindowanimator = ObjectAnimator.ofFloat(infowindow, View.Y, infowindowtop, buttonpaneltop);
                    infowindowanimator.setDuration(0);
                    infowindowanimator.start();
                    infowindowvisible = false;
                }
            });

            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

            //obtain Facebook ID of logged in user
            Profile profile = Profile.getCurrentProfile();

            if (profile != null) {

                String userid = profile.getId();
                Log.d("Tag", "My userid is: " + userid);

                //update Facebook launchusername on top right of screen
                DatabaseReference thisuserref = DB.user_ref.child(userid);
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
            }

            //add listener to add markers to campaigns' launch points on map in realtime
            listcampaignslistener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //get name of campaign
                    String title = dataSnapshot.child("campaign_name").getValue(String.class);
                    Log.d("Tag", "title = " + title);

                    LatLng launchcoords = UtilityMethod.dataSnapshotToLatLng(dataSnapshot.child("initial_location"));

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
            DB.camp_ref.addChildEventListener(listcampaignslistener);

            //listener to draw float circles of a campaign
            listlocationslistener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int i = 0;
                    DataSnapshot list_locations = dataSnapshot.child("list_locations");
                    DataSnapshot locat;
                    Circle circle;

                    while (true) {

                        if (!list_locations.hasChild(Integer.toString(i)))
                            break;

                        locat = list_locations.child(Integer.toString(i));

                        LatLng floatLocation = UtilityMethod.dataSnapshotToLatLng(locat);

                        //add a campaign circle to the map
                        circle = map.addCircle(new CircleOptions()
                                .center(floatLocation)
                                .radius(getResources().getInteger(R.integer.floatradius))
                                .strokeColor(ContextCompat.getColor(getApplicationContext(), R.color.circleoutline))
                                .fillColor(ContextCompat.getColor(getApplicationContext(), R.color.circlefill)));

                        //add a campaign circle to the list of circles
                        listCircles.add(circle);

                        i++;
                    }

                    //move camera to fit circles on screen
                    zoomFitCircles();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            //listener to update launchusername, campaign_name and charity name on infowindow
            campaignslistener = new ValueEventListener() {


                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Campaign campaign = campsDBInteractor.read(campaignname, dataSnapshot);

                    //update campaign image
                    campaignpic = campaign.getCampaign_pic();
                    StorageReference imageref = DB.images_ref.child(campaignpic);
                    setDBPictureOnImageView(imageref, R.id.campaignpic);

                    //attach listener to launch user profile pic
                    launchusername = campaign.getOwner_account();
                    Log.d("Tag", "launchusername = " + launchusername);

                    DB.user_ref.addListenerForSingleValueEvent(launchuserslistener);

                    //attach listener to charity
                    charityname = campaign.getCharity();
                    Log.d("Tag", "charityname = " + charityname);

                    DB.char_ref.addListenerForSingleValueEvent(charitieslistener);

                    TextView tv = (TextView) findViewById(R.id.campaigntitle);
                    tv.setText(campaignname);

                    tv = (TextView) findViewById(R.id.charityname);
                    tv.setText(charityname);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            launchuserslistener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = usersDBInteractor.read(launchusername, dataSnapshot);

                    //update profile pic of launcher of campaign
                    launchuserpic = user.getProfile_pic();
                    Log.d("Tag", "launchuserpic = " + launchuserpic);
                    StorageReference launchuserpicref = DB.images_ref.child(launchuserpic);
                    setDBPictureOnImageView(launchuserpicref, R.id.userpic);

                    launchusernamestr = user.getName();
                    TextView tv = (TextView) findViewById(R.id.username);
                    tv.setText(launchusernamestr);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            charitieslistener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Charity charity = charityDBinteractor.read(charityname, dataSnapshot);

                    charitypic = charity.getLogo();
                    Log.d("Tag", "charitypic = " + charitypic);
                    StorageReference logoRef = DB.images_ref.child(charitypic);
                    setDBPictureOnImageView(logoRef, R.id.charitypic);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
    }

    /**
     * Updates an ImageView object with file in Firebase database at storageReference
     *
     * @param storageReference
     * @param imageViewID
     */
    public void setDBPictureOnImageView(StorageReference storageReference, final int imageViewID) {
        final File localFile;
        try {
            localFile = File.createTempFile("images", "png");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    ImageView testPic = (ImageView) findViewById(imageViewID);
                    Uri uri = Uri.fromFile(localFile);
                    testPic.setImageURI(uri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {

        this.map = map;

        //disable toolbar from appearing when a marker is clicked
        map.getUiSettings().setMapToolbarEnabled(false);

        map.setOnMarkerClickListener(this);

        map.animateCamera(CameraUpdateFactory.newCameraPosition(defaultcamerapos));

        layoutreadylistener("map");

    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        // Get name of campaign
        campaignname = (String) marker.getTag();

        //make campaign preview pop-up window appear
        LinearLayout campinfo = (LinearLayout) findViewById(R.id.spacerparent);
        campinfo.setVisibility(View.VISIBLE);

        //remove database listeners from database
        DB.root_ref.removeEventListener(listlocationslistener);
        DB.root_ref.removeEventListener(campaignslistener);

        //remove old circles
        while (listCircles.size() != 0) {
            //remove circle from map
            listCircles.get(0).remove();
            //remove circle from list
            listCircles.remove(0);
        }

        //add listener to new campaign
        DB.camp_ref.addListenerForSingleValueEvent(campaignslistener);

        campaignref = DB.camp_ref.child(campaignname);

        //attach listener to list of locations of different campaign
        campaignref.addListenerForSingleValueEvent(listlocationslistener);

        //update map padding to bring Google logo up
        map.setPadding(padding, padding, padding, buttonpanelheight + infowindowheight);

        //animate infowindow appearing
        if (!infowindowvisible) {
            ObjectAnimator infowindowanimator = ObjectAnimator.ofFloat(infowindow, View.Y, buttonpaneltop, infowindowtop);
            infowindowanimator.setDuration(slideduration);
            infowindowanimator.start();
            infowindowvisible = true;
        }

        //tell caller we have not consumed the click event, enabling default marker behaviour
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

    private void zoomFitCircles() {

        //if no circles, do nothing
        if (listCircles.size() == 0)
            return;

        //create bounds object by supplying coordinates we need to include
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Circle c : listCircles) {
            builder.include(c.getCenter());
        }

        //create a bounds object the contains the included
        LatLngBounds bounds = builder.build();
        int radiusinmeters = getResources().getInteger(R.integer.floatradius);
        double radiusindegrees = ActivityUtility.metrestodegrees(radiusinmeters);
        double degoffsetnorth = radiusindegrees;
        double degoffseteast = radiusindegrees /
                Math.cos(ActivityUtility.degreestoradians(bounds.northeast.latitude));
        double degoffsetwest = radiusindegrees /
                Math.cos(ActivityUtility.degreestoradians(bounds.southwest.latitude));

        LatLng northeast = new LatLng(bounds.northeast.latitude + degoffsetnorth,
                bounds.northeast.longitude + degoffseteast);
        LatLng southwest = new LatLng(bounds.southwest.latitude - degoffsetnorth,
                bounds.southwest.longitude - degoffsetwest);

        bounds = bounds.including(northeast);
        bounds = bounds.including(southwest);
        //provide bounds object and padding
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                (int) getResources().getDimension(R.dimen.activity_horizontal_margin));
        map.animateCamera(cu);
    }

    private boolean locationOn() {
        int off = 0;
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (off == 0)
            return false;
        else
            return true;
    }

}