package com.cpen321.floatproject.utilities;

import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Little_town on 12/25/2016.
 */

public class UtilityMethod {
    /*
    * convert an EditText to double and check if it is null
    * if null, return 0.0
    * @param EditText
    * @return Double version of the text
     */
    public static Double text_to_double(EditText s) {
        Double toReturn = 0.0;
        if (s == null) {
            return toReturn;
        } else {
            toReturn = Double.parseDouble(s.getText().toString());
        }
        return toReturn;
    }

    /*
    * convert an EditText to long and check if it is null
    * if null, return 0
    * @param EditText
    * @return long version of the text
     */
    public static long text_to_long(EditText s) {
        long toReturn = 0;
        if (s == null) {
            return toReturn;
        } else {
            toReturn = Integer.valueOf(s.getText().toString());
        }
        return toReturn;
    }

    /**
     * Takes in a datashapshot and returns a LatLng object with the coordinates
     *
     * @param datasnapshot
     * @return a LatLng object with the coordinates in datasnapshot
     */
    public static LatLng dataSnapshotToLatLng(DataSnapshot datasnapshot) {

        Double latitude;
        Double longitude;

        //get coordinates of campaign launch location
        if (datasnapshot.child("latitude").getValue() instanceof Long &&
                datasnapshot.child("longitude").getValue() instanceof Long) {
            latitude = ((Long) datasnapshot.child("latitude").getValue()).doubleValue();
            longitude = ((Long) datasnapshot.child("longitude").getValue()).doubleValue();
        }
        else if (datasnapshot.child("latitude").getValue() instanceof Long &&
                !(datasnapshot.child("longitude").getValue() instanceof Long)) {
            latitude = ((Long) datasnapshot.child("latitude").getValue()).doubleValue();
            longitude = ((Double) datasnapshot.child("longitude").getValue());
        }
        else if (!(datasnapshot.child("latitude").getValue() instanceof Long) &&
                datasnapshot.child("longitude").getValue() instanceof Long) {
            latitude = ((Double) datasnapshot.child("latitude").getValue());
            longitude = ((Long) datasnapshot.child("longitude").getValue()).doubleValue();
        }
        else {
            latitude = ((Double) datasnapshot.child("latitude").getValue());
            longitude = ((Double) datasnapshot.child("longitude").getValue());
        }

    return new LatLng(latitude, longitude);
};

/**
 * Takes in a datashapshot and returns a list of LatLng object with the coordinates
 *
 * @param datasnapshot
 * @return a list of LatLng object with the coordinates in datasnapshot
 */
public static List<LatLng>dataSnapshotToLatLngList(DataSnapshot datasnapshot){
        //get coordinates of location
        List<LatLng>to_return=new LinkedList<LatLng>();
        for(DataSnapshot loc:datasnapshot.getChildren()){
        LatLng location_to_add=dataSnapshotToLatLng(loc);
        to_return.add(location_to_add);
        }

        return to_return;
        };

        }
