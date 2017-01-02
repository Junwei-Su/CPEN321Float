package com.cpen321.floatproject.database;

import com.cpen321.floatproject.users.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Little_town on 12/22/2016.
 */

public class UsersDBInteractor implements Readable, Writable {
    @Override
    public User read(String ID, DataSnapshot dataSnapshot) {
        //get the dataSnapshot of the user object with this ID
        DataSnapshot userSnap =  dataSnapshot.child(ID);

        //if user not exist;
        if(userSnap==null){
            return null;
        }

        //retrive the user information
        long amount_donated = (Long)userSnap.child("amount_donated").getValue();
        long amount_raised = (Long)userSnap.child("amount_raised").getValue();
        String account_name = (String) userSnap.child("account_name").getValue();
        String address = (String) userSnap.child("address").getValue();
        String blurb = (String) userSnap.child("blurb").getValue();
        String date_join = (String) userSnap.child("date_join").getValue();
        String name = (String) userSnap.child("name").getValue();
        String profile_pic = (String) userSnap.child("profile_pic").getValue();

        List<String> list_camp_init;
        List<String> list_camp_join;
        if (dataSnapshotToCampList(userSnap.child("list_camp_init")) == null){
            list_camp_init = new LinkedList<String>();
        }else{
            list_camp_init = dataSnapshotToCampList(userSnap.child("list_camp_init"));
        }

        if(dataSnapshotToCampList(userSnap.child("list_camp_join")) == null){
            list_camp_join = new LinkedList<String>();
        }else{
            list_camp_join = dataSnapshotToCampList(userSnap.child("list_camp_join"));
        }

        User to_return = new User(name, account_name, date_join, blurb,
                amount_raised, amount_donated, address,
                list_camp_init, list_camp_join, profile_pic);

        if (userSnap.child("metadata_id") != null) {
            String metadata_id = (String) userSnap.child("metadata_id").getValue();
            to_return.setMetadataid(metadata_id);
        }

        if (userSnap.child("refresh_token") != null){
            String refresh_token = (String) userSnap.child("refresh_token").getValue();
            to_return.setRefreshToken(refresh_token);
        }

        return to_return;
    }

    @Override
    public void update(Object o, DatabaseReference databaseReference) {
        User user_update = (User) o;
        DatabaseReference user_update_ref = DB.user_ref.child(user_update.getAccount_name());
        //update infomation
        user_update_ref.child("amount_donated").setValue(user_update.getAmount_donated());
        user_update_ref.child("amount_raised").setValue(user_update.getAmount_raised());
        user_update_ref.child("address").setValue(user_update.getAddress());
        user_update_ref.child("blurb").setValue(user_update.getBlurb());
        user_update_ref.child("list_camp_init").setValue(user_update.getList_of_campaign_initialize());
        user_update_ref.child("list_camp_join").setValue(user_update.getList_of_campaign_followed());
        user_update_ref.child("name").setValue(user_update.getName());
        user_update_ref.child("profile_pic").setValue(user_update.getProfile_pic());
        user_update_ref.child("metadata_id").setValue(user_update.getMetadataid());
        user_update_ref.child("refresh_token").setValue(user_update.getRefreshToken());
    }

    @Override
    public void put(Object o, DatabaseReference databaseReference) {
        User to_push = (User) o;
        String key = to_push.getAccount_name();

        Map<String, Object> userValue = to_push.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + key, userValue);
        databaseReference.updateChildren(childUpdates);
    }

    /*
     * return a list of campaign objects given a datasnapshot
     */
    public List<String> dataSnapshotToCampList(DataSnapshot datasnapshot){
        List<String> to_return = new LinkedList<String>();

        for(DataSnapshot camp_name : datasnapshot.getChildren()){
            String camp_to_add = (String)camp_name.getValue();
            to_return.add(camp_to_add);
        }

        return to_return;
    }

}
