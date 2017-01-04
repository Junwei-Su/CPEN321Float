package com.cpen321.floatproject.utilities;
import com.firebase.client.Firebase;

/**
 * Created by Richard on 1/3/2017.
 */

public class MyApplication extends android.app.Application{
    public boolean isInitialized;
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this); //initializeFireBase(context);
        isInitialized = true;
    }

    public boolean isInitialized(){
        return isInitialized;
    }
}

