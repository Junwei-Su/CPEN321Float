package com.cpen321.floatproject.database;

import com.cpen321.floatproject.charities.Charity;
import com.google.firebase.database.DataSnapshot;

/**
 * Created by Little_town on 12/24/2016.
 */

/*
 * this class serves a communication layer between the android application and
 * firebase for the charity object
 */

public class CharityDBinteractor implements Readable {
    /*
    * Read object from database with ID
    * @Param: String: ID of the object
    *        DataSnapshot: the dataSnapshot that has the object
    * @return: corresponding object
    */
    @Override
    public Charity read(String ID, DataSnapshot dataSnapshot) {
        //get the dataSnapshot of the charity object with this ID
        DataSnapshot charitySnap =  dataSnapshot.child(ID);

        //if charity not exist;
        if(charitySnap==null){
            return null;
        }

        //retrive the charity information
        String name = (String) charitySnap.child("name").getValue();
        String logo = (String) charitySnap.child("logo").getValue();
        String description = (String) charitySnap.child("description").getValue();
        String link = (String) charitySnap.child("link").getValue();

        Charity to_return = new Charity(name, logo, description, link);

        return to_return;
    }
}
