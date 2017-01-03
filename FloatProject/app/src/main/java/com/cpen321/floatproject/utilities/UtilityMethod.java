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
        return new LatLng(castToDouble(datasnapshot.child("latitude")),
                castToDouble(datasnapshot.child("longitude")));
    }

    private static Double castToDouble(DataSnapshot datasnapshot){
        if (datasnapshot.getValue() instanceof Long)
            return ((Long) datasnapshot.getValue()).doubleValue();
        else
            return ((Double) datasnapshot.getValue());
    }
/*
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
        }
}
