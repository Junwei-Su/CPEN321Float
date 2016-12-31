package com.cpen321.floatproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cpen321.floatproject.R;
import com.cpen321.floatproject.database.DB;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by sfarinas on 10/26/2016.
 */
public class MainFragment extends Fragment {

    private TextView mTextDetails;

    private ValueEventListener userslistener;

    private CallbackManager mCallbackManager;
    private Profile mprofile;
    private ProfileTracker mprofiletracker;

    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            if(Profile.getCurrentProfile() == null) {
                mprofiletracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        // profile2 is the new mprofile
                        mprofile = profile2;
                        DB.user_ref.addListenerForSingleValueEvent(userslistener);
                        mprofiletracker.stopTracking();
                    }
                };
                // no need to call startTracking() on mprofiletracker
                // because it is called by its constructor, internally.
            }
            else {
                mprofile = Profile.getCurrentProfile();
                DB.user_ref.addListenerForSingleValueEvent(userslistener);
            }

            userslistener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("Tag", "onDatachange for userslistener called.");
                    DataSnapshot user = dataSnapshot.child(mprofile.getId());
                    String name = user.child("name").getValue(String.class);

                    Intent intent;
                    if(name == null){
                        //direct to CreateUser
                        intent = new Intent(getContext(), CreateUser.class);
                    }else{
                        //direct to MapPage
                        intent = new Intent(getContext(), MapPage.class);
                    }

                    startActivity(intent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };


    public MainFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return inflater.inflate(R.layout.mainfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, mCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
