package com.cpen321.floatproject.database;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by clarence on 2016-11-24.
 */

public interface Writable {
    /*
     * Update the corresponding object in the database
     */
    public void update(Object o, DatabaseReference databaseReference);

    /*
     * Put the corresponding object into the database
     */
    public void put(Object o, DatabaseReference databaseReference);
}