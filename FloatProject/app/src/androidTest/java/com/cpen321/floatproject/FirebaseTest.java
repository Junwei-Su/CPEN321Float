package com.cpen321.floatproject;

import android.test.AndroidTestCase;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;

import org.junit.Test;

/**
 * Created by sfarinas on 11/19/2016.
 */
public class FirebaseTest extends AndroidTestCase{

    public void searchNearbyCampTest(){

        Firebase.setAndroidContext(mContext);
        Firebase firebase = new Firebase("https://float-568c7.firebaseio.com/");

        Firebase camps = firebase.child("campaigns");

        if(camps != null)
            assert true;
        else
            assert false;
    }
}
