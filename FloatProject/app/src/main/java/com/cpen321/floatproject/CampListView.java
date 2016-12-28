package com.cpen321.floatproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.cpen321.floatproject.campaigns.Campaign;
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

        //clarence manual debug
        Log.d("CampList", "camp_list_layout = " + String.valueOf(R.layout.camp_list_layout));
        Log.d("CampList", "applicationContext = " + getApplicationContext().toString());

//        CampListAdapter campListAdapter = new CampListAdapter(getApplicationContext(), R.layout.camp_list_layout, R.id.campListName, campaigns);
        CampListAdapter campListAdapter = new CampListAdapter(getApplicationContext(),  campaigns);

        listView.setAdapter(campListAdapter);
    }
}
