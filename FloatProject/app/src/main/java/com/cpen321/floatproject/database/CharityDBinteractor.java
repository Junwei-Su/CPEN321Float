package com.cpen321.floatproject.database;

import com.cpen321.floatproject.User;
import com.cpen321.floatproject.charities.Charity;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

/**
 * Created by Little_town on 12/24/2016.
 */

public class CharityDBinteractor implements Readable {
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
