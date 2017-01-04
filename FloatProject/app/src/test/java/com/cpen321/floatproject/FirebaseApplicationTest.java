package com.cpen321.floatproject;

import android.test.ApplicationTestCase;

import com.cpen321.floatproject.utilities.MyApplication;
import com.firebase.client.Firebase;

import org.junit.Test;

import java.util.concurrent.TimeoutException;

/**
 * Created by Richard on 1/3/2017.
 */

public class FirebaseApplicationTest extends ApplicationTestCase<MyApplication> {

    private static MyApplication application;

    public FirebaseApplicationTest() {
        super(MyApplication.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        if (application == null) {
            application = getApplication();
        }
        if (application == null) {
            application = (MyApplication) getContext().getApplicationContext();
            assertNotNull(application);
            long start = System.currentTimeMillis();
            while (!application.isInitialized()){
                Thread.sleep(300);  //wait until FireBase is totally initialized
                if ( (System.currentTimeMillis() - start ) >= 1000 )
                    throw new TimeoutException(this.getClass().getName() +"Setup timeOut");
            }
        }
    }


    @Test
    public void testWrite(){
        Firebase cloud = new Firebase("https://float-568c7.firebaseio.com/");
        cloud.child("message").setValue("Do you have data? You'll love Firebase.");
    }

}

