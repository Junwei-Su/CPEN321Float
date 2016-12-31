package com.cpen321.floatproject.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cpen321.floatproject.R;
import com.cpen321.floatproject.campaigns.DestinationCampaign;
import com.cpen321.floatproject.database.DB;
import com.cpen321.floatproject.database.UsersDBInteractor;
import com.cpen321.floatproject.utilities.ActivityUtility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Little_town on 12/26/2016.
 */

public class CampListAdapter extends BaseAdapter{

    private static ArrayList<DestinationCampaign> campArrayList;
    private ActivityUtility activityUtility = new ActivityUtility();
    private LayoutInflater mInflater;
    private Context context;

    public CampListAdapter(Context context, ArrayList<DestinationCampaign> campArrayList) {
        this.campArrayList = campArrayList;
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return campArrayList.size();
    }

    public Object getItem(int position) {
        return campArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.camp_list_layout, null);
            holder = new ViewHolder();

            holder.campPic = (ImageView) convertView.findViewById(R.id.campListPic);
            holder.campName = (TextView) convertView.findViewById(R.id.campListName);
            holder.userName = (TextView) convertView.findViewById(R.id.campListUser);
            holder.destination = (TextView) convertView.findViewById(R.id.campListDest);
            holder.details = (Button) convertView.findViewById(R.id.details);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final DestinationCampaign campaign = campArrayList.get(position);
        StorageReference imageRef = DB.images_ref.child(campaign.getCampaign_pic());
        activityUtility.setPictureOnImageView(imageRef, (ImageView) convertView.findViewById(R.id.campListPic));
        holder.campName.setText(campaign.getCampaign_name());

        DB.user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user_name = (String) dataSnapshot.child(campaign.getOwner_account()).child("name").getValue();
                holder.userName.setText(user_name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        View.OnClickListener detailslistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String campaignname = campaign.getCampaign_name();

                //start CampDetails activity
                Intent intent = new Intent(v.getContext(), CampDetails.class);
                intent.putExtra("key", campaignname);
                context.startActivity(intent);
            }
        };

        holder.destination.setText(campaign.getDestination());
        holder.details.setOnClickListener(detailslistener);

        return convertView;
    }

    static class ViewHolder {
        ImageView campPic;
        TextView campName;
        TextView userName;
        TextView destination;
        Button details;
    }

    public void directToDetail(View view){
        TextView tv = (TextView) view.findViewById(R.id.campListName);
        String campaignname = tv.getText().toString();

        Log.d("Tag", "camptitleinfo = " + campaignname);
        //start CampDetails activity
        Intent intent = new Intent(view.getContext(), CampDetails.class);
        intent.putExtra("key", campaignname);
        activityUtility.startActivity(intent);
    }
}
