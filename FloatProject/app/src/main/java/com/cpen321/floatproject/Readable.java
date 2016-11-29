package com.cpen321.floatproject;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by clarence on 2016-11-24.
 */

public interface Readable {

    /*
    * Read object from database with ID
    */
    public  Object read(String ID, DataSnapshot dataSnapshot);

}