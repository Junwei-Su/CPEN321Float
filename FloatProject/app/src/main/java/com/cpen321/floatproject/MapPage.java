package com.cpen321.floatproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.FuturePayment;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.paypal.base.Constants.CLIENT_ID;

/**
 * Created by sfarinas on 10/17/2016.
 */
public class MapPage extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    //references to points in Firebase database
    private DatabaseReference databaseref;
    private DatabaseReference usersref;
    private DatabaseReference listcampaignsref;
    private DatabaseReference listlocationsref;
    private DatabaseReference listcharitiesref;
    private DatabaseReference charityref;
    private DatabaseReference campaignref;
    private DatabaseReference launchuserref;

    //references to points in Firebase storage
    private FirebaseStorage storage;
    private StorageReference imagesRef;

    //listeners for realtime database
    private ValueEventListener listlocationslistener;
    private ChildEventListener listcampaignslistener;
    private ValueEventListener campaignlistener;
    private ValueEventListener charitylistener;
    private ValueEventListener launchuserlistener;

    private GoogleMap map;

    //height, in pixels, of buttonpanel and infowindow
    private int buttonpanelheight;
    private int infowindowheight;

    //list of circles presently on 'map'
    private List<Circle> listCircles = new LinkedList<>();

    //default location of user on map on activity creation
    private LatLng userlocation = new LatLng(49.251899, -123.231948);

    //default position of camera on map on activity creation
    private CameraPosition defaultcamerapos = new CameraPosition.Builder()
            .target(userlocation)   // Sets the center of the map to Mountain View
            .zoom(15)                   // Sets the zoom
            .bearing(0)                // Sets the orientation of the camera to east
            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
            .build();                   // Creates a CameraPosition from the builder

    private String charityname;
    private String owner_account_id; //username of person who launched campaign

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
        //Log.d("Tag", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        final LinearLayout infowindow = (LinearLayout) findViewById(R.id.infowindow);

        //listen to infowindow once to obtain height in pixels
        infowindow.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //called when layout is ready yet before drawn

                        LinearLayout infowindow = (LinearLayout) findViewById(R.id.infowindow);
                        infowindowheight = infowindow.getHeight();

                        //Log.d("Tag", "infowindowheight = " + infowindowheight);

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

                        //Log.d("Tag", "buttonpanelheight = " + buttonpanelheight);

                        //alert layoutreadylistener buttonpanel is created
                        layoutreadylistener("buttonpanel");

                        //remove listener
                        if(Build.VERSION.SDK_INT <= 14)
                            mapbuttonpanel.getViewTreeObserver().removeGlobalOnLayoutListener( this );
                        else
                            mapbuttonpanel.getViewTreeObserver().removeOnGlobalLayoutListener( this );
                    }
                }
        );

        //get a handle on map
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //wire joincamp button to CampDetails activity
        Button button = (Button) findViewById(R.id.joincamp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start _Submenu activity
                Intent intent = new Intent(v.getContext(), CampDetails.class);
                startActivity(intent);
            }
        });

        //wire createcamp button to CreateCampaign activity
        button = (Button) findViewById(R.id.createcamp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start _Submenu activity
                Intent intent = new Intent(v.getContext(), CreateCampaign.class);
                startActivity(intent);

            }
        });

        //after infowindow is closed, update map paddings and camera
        ImageButton imageButton = (ImageButton) findViewById(R.id.closeWindow);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout infowindow = (LinearLayout) findViewById(R.id.spacerparent);
                infowindow.setVisibility(View.GONE);

                //update map margins after window is gone
                map.setPadding(0, 0, 0, buttonpanelheight);

                //move camera to fit campaign circles in screen
                zoomFitCircles();

                //TODO add button to reset camera position to user's location
                //map.animateCamera(CameraUpdateFactory.newCameraPosition(defaultcamerapos));
            }
        });

        //wire details button to CampDetails activity
        button = (Button) findViewById(R.id.details);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView tv = (TextView) findViewById(R.id.campaigntitle);

               String campaignname = tv.getText().toString();

                 Log.d("Tag", "camptitleinfo = " + campaignname);

                //start _Submenu activity
                Intent intent = new Intent(v.getContext(), CampDetails.class);
                intent.putExtra("key",campaignname);
                startActivity(intent);

            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //Log.d("Tag", "buttonpanelheight in onCreate() = " + Integer.toString(buttonpanelheight));

        //obtain references to Firebase database
        databaseref = FirebaseDatabase.getInstance().getReference();
        listcampaignsref = databaseref.child("campaigns");
        usersref = databaseref.child("users");
        listcharitiesref = databaseref.child("charities");

        //obtain Facebook ID of logged in user
        Profile profile = Profile.getCurrentProfile();

        if(profile!=null) {

            String userid = profile.getId();
            //Log.d("Tag", "My userid is: " + userid);

            //update Facebook username on top right of screen
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
        }

        //add listener to add markers to campaigns' launch points on map in realtime
        listcampaignslistener = new ChildEventListener() {
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

        //listener to draw float circles of a campaign
        listlocationslistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                DataSnapshot locat;
                Circle circle;

                while(true){
//                    Log.d("Tag", "Another iteration!");
                    if(! dataSnapshot.hasChild(Integer.toString(i)))
                        break;

                    locat = dataSnapshot.child(Integer.toString(i));

                    LatLng floatLocation = dataSnapshotToLatLng(locat);

                    //add a campaign circle to the map
                    circle = map.addCircle(new CircleOptions()
                            .center(floatLocation)
                            .radius(getResources().getInteger(R.integer.floatradius))
                            .strokeColor(ContextCompat.getColor(getApplicationContext(), R.color.circleoutline))
                            .fillColor(ContextCompat.getColor(getApplicationContext(), R.color.circlefill)));

                    //add a campaign circle to the list of circles
                    listCircles.add(circle);
                    //Log.d("Tag", "add circle: " + listCircles);

                    i++;
                }

                //move camera to fit circles on screen
                zoomFitCircles();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //listener to update username, campaign_name and charity name on infowindow
        campaignlistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //update campaign image
                campaignpic = dataSnapshot.child("campaign_pic").getValue(String.class);
                StorageReference imageref = imagesRef.child(campaignpic);
                setDBPictureOnImageView(imageref, R.id.campaignpic);

                //attach listener to launch user profile pic
                owner_account_id = dataSnapshot.child("owner_account").getValue(String.class);
                Log.d("Tag", "owner_account_id = " + owner_account_id);
                launchuserref = usersref.child(owner_account_id);
                launchuserref.addListenerForSingleValueEvent(launchuserlistener);

                String title = dataSnapshot.child("campaign_name").getValue(String.class);

                //attach listener to charity
                charityname = dataSnapshot.child("charity").getValue(String.class);
                Log.d("Tag", "charityname = " + charityname);
                charityref = listcharitiesref.child(charityname);
                charityref.addListenerForSingleValueEvent(charitylistener);

                //TextView tv = (TextView) findViewById(R.id.username);
                //tv.setText(owner_account_id);

                TextView tv = (TextView) findViewById(R.id.campaigntitle);
                tv.setText(title);

                tv = (TextView) findViewById(R.id.charityname);
                tv.setText(charityname);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        launchuserlistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //update profile pic of launcher of campaign
                launchuserpic = dataSnapshot.child("profile_pic").getValue(String.class);
                Log.d("Tag", "launchuserpic = " + launchuserpic);
                StorageReference launchuserpicref = imagesRef.child(launchuserpic);
                setDBPictureOnImageView(launchuserpicref, R.id.userpic);

                //update username of launcher of campaign
                String launchusername = dataSnapshot.child("name").getValue(String.class);
                TextView tv = (TextView) findViewById(R.id.username);
                tv.setText(launchusername);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        charitylistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                charitypic = dataSnapshot.child("logo").getValue(String.class);
                Log.d("Tag", "charitypic = " + charitypic);
                StorageReference logoRef = imagesRef.child(charitypic);
                setDBPictureOnImageView(logoRef, R.id.charitypic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //Getting the picture
        storage = FirebaseStorage.getInstance();
        imagesRef = storage.getReferenceFromUrl("gs://float-568c7.appspot.com/images");
        StorageReference lighthouseRef = imagesRef.child("lighthouse.png");
        //setDBPictureOnImageView(lighthouseRef, R.id.campaignpic);
        //setDBPictureOnImageView(lighthouseRef, R.id.userpic);

        final CampsDBInteractor testReader = new CampsDBInteractor();

        listcampaignsref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Campaign campaign = testReader.read("testCamp1", dataSnapshot);
                Log.d("Tag", campaign.campaign_name);
                Log.d("Tag", campaign.accumulated_donation);
                Log.d("Tag", campaign.campaign_pic);
                Log.d("Tag", campaign.charity);
                Log.d("Tag", campaign.goal_amount);
                Log.d("Tag", campaign.description);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

          //testing code for read
//        Campaign campaign = testReader.read("testCamp1", listcampaignsref.child("testCamp1"));
//        Log.d("Tag", "MapPage " + campaign.campaign_name);
//        Log.d("Tag", "MapPage " + campaign.accumulated_donation);
//        Log.d("Tag", "MapPage " + campaign.campaign_pic);
//        Log.d("Tag", "MapPage " + campaign.charity);
//        Log.d("Tag", "MapPage " + campaign.goal_amount);
//        Log.d("Tag", "MapPage " + campaign.description);

    }



    /**
     * Updates an ImageView object with file in Firebase database at storageReference
     * @param storageReference
     * @param imageViewID
     */
    public void setDBPictureOnImageView(StorageReference storageReference, final int imageViewID){
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
        //Log.d("Tag", "onMapReady()");

        this.map = map;

        //disable toolbar from appearing when a marker is clicked
        map.getUiSettings().setMapToolbarEnabled(false);

        map.setOnMarkerClickListener(this);

        map.animateCamera(CameraUpdateFactory.newCameraPosition(defaultcamerapos));

        //map.setPadding(0, 0, 0, buttonpanelheight);
        layoutreadylistener("map");

        //Log.d("Tag", "buttonpanelheight in onMapReady() = " + Integer.toString(buttonpanelheight));
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        // Get name of campaign
        String campaignname = (String) marker.getTag();

        //make campaign preview pop-up window appear
        LinearLayout campinfo = (LinearLayout) findViewById(R.id.spacerparent);
        campinfo.setVisibility(View.VISIBLE);

        //remove database listeners from database
        databaseref.removeEventListener(listlocationslistener);
        databaseref.removeEventListener(campaignlistener);

        //Log.d("Tag", "Started removing circles.");
        //remove old circles
        while(listCircles.size()!=0) {
            //remove circle from map
            listCircles.get(0).remove();
            //remove circle from list
            listCircles.remove(0);
            //Log.d("Tag", "remove circle: " + listCircles);
        }

        //update campaign reference to new campaign
        Log.d("Tag", "campaignname = " + campaignname);
        campaignref = listcampaignsref.child(campaignname);
        //add listener to new campaign
        campaignref.addValueEventListener(campaignlistener);

        //get database reference to list of locations of different campaign
        listlocationsref = campaignref.child("list_locations");
        //attach listener to list of locations of different campaign
        listlocationsref.addListenerForSingleValueEvent(listlocationslistener);

        //update map padding to bring Google logo up
        map.setPadding(0, 0, 0, buttonpanelheight + infowindowheight);

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

    /**
     * Takes in a datashapshot and returns a LatLng object with the coordinates
     * @param datasnapshot
     * @return a LatLng object with the coordinates in datasnapshot
     */
    private LatLng dataSnapshotToLatLng (DataSnapshot datasnapshot){
        //get coordinates of campaign launch location
        Map<String, Double> mapcoords = (HashMap<String,Double>) datasnapshot.getValue();

        //create LatLng object out of coordinates
        return new LatLng(mapcoords.get("latitude"), mapcoords.get("longitude"));
    };

    private void makeFuturePayment(String title){
        Profile profile = Profile.getCurrentProfile();
        String userid = profile.getId();

        startActivity(new Intent(this, MakeFuturePayment.class)
                .putExtra("Title", title)
                .putExtra("UserID", userid));
    }

    private void zoomFitCircles(){
        //if no circles, do nothing
        if(listCircles.size() == 0)
            return;

        //create bounds object by supplying coordinates we need to include
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(Circle c : listCircles){
            builder.include(c.getCenter());
        }

        //create a bounds object the contains the included
        LatLngBounds bounds = builder.build();
        double degoffsetnorth = getResources().getInteger(R.integer.floatradius)/111111.0;
        double degoffseteast = getResources().getInteger(R.integer.floatradius)/
                111111.0/Math.cos(bounds.northeast.latitude/180.0*Math.PI);
        double degoffsetwest = getResources().getInteger(R.integer.floatradius)/
                111111.0/Math.cos(bounds.southwest.latitude/180.0*Math.PI);
        //Log.d("Tag", "degoffsetnorth = " + degoffsetnorth);
        //Log.d("Tag", "degoffseteast = " + degoffseteast);
        //Log.d("Tag", "degoffsetwest = " + degoffsetwest);

        //Log.d("Tag", "old northeast = " + bounds.northeast.latitude + ", " + bounds.northeast.longitude);
        //Log.d("Tag", "old southwest = " + bounds.southwest.latitude + ", " + bounds.southwest.longitude);

        LatLng northeast = new LatLng(bounds.northeast.latitude + degoffsetnorth,
                bounds.northeast.longitude + degoffseteast);
        LatLng southwest = new LatLng(bounds.southwest.latitude - degoffsetnorth,
                bounds.southwest.longitude - degoffsetwest);

        bounds = bounds.including(northeast);
        bounds = bounds.including(southwest);

        //Log.d("Tag", "new northeast = " + bounds.northeast.latitude + ", " + bounds.northeast.longitude);
        //Log.d("Tag", "new southwest = " + bounds.southwest.latitude + ", " + bounds.southwest.longitude);

        //provide bounds object and padding
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                (int)getResources().getDimension(R.dimen.activity_horizontal_margin));
        map.animateCamera(cu);
    }
}