package com.cpen321.floatproject.activities.CampActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.cpen321.floatproject.R;
import com.cpen321.floatproject.activities.Log_in_and_map.MapPage;
import com.cpen321.floatproject.campaigns.DestinationCampaign;
import com.cpen321.floatproject.database.DB;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Little_town on 12/26/2016.
 */

public class CampListView extends Activity {

    ArrayList<DestinationCampaign> campaigns;
    ListView listView;
    ImageButton createCamp;
    ImageButton listButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camp_list_view);
        campaigns = new ArrayList<>();
        listView = (ListView) findViewById(R.id.campListView);

        DB.camp_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot camps: dataSnapshot.getChildren()){
                    String camps_name = camps.getKey().toString();
                    DestinationCampaign to_add = DB.campDBinteractor.read(camps_name, dataSnapshot);
                    campaigns.add(to_add);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        CampListAdapter campListAdapter = new CampListAdapter(CampListView.this,  campaigns);

        listView.setAdapter(campListAdapter);

        //wire createcamp button to CreateCampaign activity
        createCamp = (ImageButton) findViewById(R.id.createcamp);
        createCamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start _Submenu activity
                Intent intent = new Intent(v.getContext(), CreateCampaign.class);
                startActivity(intent);

            }
        });

        listButton = (ImageButton) findViewById(R.id.mapCamp);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //user click the list button
                //UI go to list page
                Intent jumpToMapView = new Intent(v.getContext(), MapPage.class);
                //need to pass the current location of the user
                startActivity(jumpToMapView);

            }
        });

    }
}
