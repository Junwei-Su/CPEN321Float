package com.cpen321.floatproject.utilities;

import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Little_town on 12/25/2016.
 */

public class UtilityMethod {
    /*
    * convert an EditText to double and check if it is null
    * if null, return 0.0
     */
    public static Double text_to_double(EditText s){
        Double toReturn = 0.0;
        if(s == null) {
            return  toReturn;
        } else {
            toReturn = Double.parseDouble(s.getText().toString());
        }
        return toReturn;
    }

    /*
    * convert an EditText to long and check if it is null
    * if null, return 0
     */
    public static long text_to_long(EditText s){
        long toReturn = 0;
        if(s == null) {
            return  toReturn;
        } else {
            toReturn = Integer.valueOf(s.getText().toString());
        }
        return toReturn;
    }

    /**
     * Takes in a datashapshot and returns a LatLng object with the coordinates
     * @param datasnapshot
     * @return a LatLng object with the coordinates in datasnapshot
     */
    public static LatLng dataSnapshotToLatLng (DataSnapshot datasnapshot){
        //get coordinates of campaign launch location
        Map<String, Double> mapcoords = (HashMap<String,Double>) datasnapshot.getValue();

        //create LatLng object out of coordinates
        return new LatLng(mapcoords.get("latitude"), mapcoords.get("longitude"));
    };
}
