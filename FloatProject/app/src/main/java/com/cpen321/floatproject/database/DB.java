package com.cpen321.floatproject.database;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Little_town on 12/26/2016.
 */

public class DB {
    final static public DatabaseReference root_ref = FirebaseDatabase.getInstance().getReference();
    final static public DatabaseReference user_ref = root_ref.child("users");
    final static public DatabaseReference camp_ref = root_ref.child("campaigns");
    final static public DatabaseReference char_ref = root_ref.child("charities");
}
