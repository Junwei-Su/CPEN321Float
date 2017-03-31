package com.cpen321.floatproject.database;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Little_town on 12/26/2016.
 */

/*
 * this class represents a container for the references of database
 */

public class DB {
    final static public DatabaseReference root_ref = FirebaseDatabase.getInstance().getReference();
    final static public DatabaseReference user_ref = root_ref.child("users");
    final static public DatabaseReference camp_ref = root_ref.child("campaigns");
    final static public DatabaseReference char_ref = root_ref.child("charities");
    final static public FirebaseStorage stor_ref =  FirebaseStorage.getInstance();
    final static public StorageReference images_ref = stor_ref.getReferenceFromUrl("gs://float-568c7.appspot.com/images");
    final static public CampsDBInteractor campDBinteractor = new CampsDBInteractor();
    final static public CharityDBinteractor charDBinteractor = new CharityDBinteractor();
    final static public UsersDBInteractor usersDBinteractor = new UsersDBInteractor();
}
