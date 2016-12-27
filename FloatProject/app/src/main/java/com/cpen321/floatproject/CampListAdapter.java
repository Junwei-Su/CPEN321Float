package com.cpen321.floatproject;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.cpen321.floatproject.campaigns.Campaign;

import java.util.List;

/**
 * Created by Little_town on 12/26/2016.
 */

public class CampListAdapter extends ArrayAdapter<Campaign> {

    public CampListAdapter(Context context, int resource, List<Campaign> objects) {
        super(context, resource, objects);
    }
}
