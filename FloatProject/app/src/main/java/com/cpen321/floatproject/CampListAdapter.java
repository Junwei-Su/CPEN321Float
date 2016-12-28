package com.cpen321.floatproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cpen321.floatproject.campaigns.DestinationCampaign;
import com.cpen321.floatproject.database.DB;
import com.cpen321.floatproject.utilities.ActivityUtility;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Little_town on 12/26/2016.
 */

public class CampListAdapter extends BaseAdapter{

    private static ArrayList<DestinationCampaign> campArrayList;
    private ActivityUtility activityUtility = new ActivityUtility();
    private LayoutInflater mInflater;

    public CampListAdapter(Context context, ArrayList<DestinationCampaign> campArrayList) {
        this.campArrayList = campArrayList;
        mInflater = LayoutInflater.from(context);
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.camp_list_layout, null);
            holder = new ViewHolder();

        holder.campPic = (ImageView) convertView.findViewById(R.id.campListPic);
        holder.campName = (TextView) convertView.findViewById(R.id.campListName);
        holder.userName = (TextView) convertView.findViewById(R.id.campListUser);
        holder.destination = (TextView) convertView.findViewById(R.id.campListDest);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DestinationCampaign campaign = campArrayList.get(position);
        StorageReference imageRef = DB.images_ref.child(campaign.getCampaign_pic());
        activityUtility.setPictureOnImageView(imageRef,(ImageView) convertView.findViewById(R.id.campListPic));
        holder.campName.setText(campaign.getCampaign_name());
        holder.userName.setText(campaign.getOwner_account());
        holder.destination.setText(campaign.getDestination());

        return convertView;
    }

    static class ViewHolder {
        ImageView campPic;
        TextView campName;
        TextView userName;
        TextView destination;
    }
}
