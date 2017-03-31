package com.cpen321.floatproject.users;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * this class represent a user object in the application
 */

public class User {

    //field in database
    private String name;
    private String account_name;
    private String date_join;
    private String blurb;
    private long amount_raised;
    private long amount_donated;
    private String address;
    private String metadata_id;
    private String refresh_token;
    private String profile_pic;
    private List<String> list_of_campaign_followed = new LinkedList<String>();
    private List<String> list_of_campaign_initialize = new LinkedList<String>();


    public User(){}

    /* The simplest constructor */
    public User(String name, String account){
        this.name = name;
        this.account_name = account;
    }

    /* The short version constructor */
    public User(String name, String account_name,String date_join, String blurb, boolean is_charity, long amount_gain,
                long amount_raised, long amount_donated, String address){

        this.name = name;
        this.account_name = account_name;
        this.date_join = date_join;
        this.blurb = blurb;
        this.amount_raised = amount_raised;
        this.amount_donated = amount_donated;
        this.address = address;
    }

    /* The full constructor cover all the fields */
    public User(String name, String account_name,String date_join, String blurb,
                long amount_raised, long amount_donated, String address,
                List<String> list_of_campaign_initialize, List<String> list_of_campaign_followed, String profile_pic){
        Log.d("Tag", "Large constructor used.");
        this.name = name;
        this.account_name = account_name;
        this.date_join = date_join;
        this.blurb = blurb;
        this.amount_raised = amount_raised;
        this.amount_donated = amount_donated;
        this.address = address;
        this.list_of_campaign_followed = list_of_campaign_followed;
        this.list_of_campaign_initialize = list_of_campaign_initialize;
        this.profile_pic = profile_pic;
    }

    /* Functions to retrieve fields of a user */
    public String getName(){
        return this.name;
    }

    public void setName(String new_name){ this.name = new_name; }

    public String getAccount_name(){
        return this.account_name;
    }

    public String getBlurb(){ return this.blurb; }

    public void setBlurb(String newBlurb){ this.blurb = newBlurb; }

    public long getAmount_raised(){ return this.amount_raised; }

    public void addAmount_raised(long incre){ this.amount_raised+=incre;}

    public long getAmount_donated(){return this.amount_donated;}

    public void addAmount_donated(long incre){this.amount_donated+= incre;}

    public List<String> getList_of_campaign_followed(){ return this.list_of_campaign_followed;}

    public List<String> getList_of_campaign_initialize(){ return this.list_of_campaign_initialize;}

    public void addInitCamp(String newInit){
        if(this.list_of_campaign_initialize.contains(newInit)!=true){
            this.list_of_campaign_initialize.add(newInit);
        }
    }

    public void addFollowedCamp(String newFollow){
        if(this.list_of_campaign_followed.contains(newFollow) !=true){
            this.list_of_campaign_followed.add(newFollow);
        }
    }

    public String getDate(){
        return this.date_join;
    }

    public String getProfile_pic(){return this.profile_pic;}

    public void setProfile_pic(String newPic){this.profile_pic = newPic;}

    public String getAddress(){return this.address;}

    public void setAddress(String newAdd){this.address = newAdd;}

    public void setMetadataid(String metadata_id){this.metadata_id = metadata_id;}

    public void setRefreshToken(String refresh_token){this.refresh_token = refresh_token;}

    public String getMetadataid(){return metadata_id;}

    public String getRefreshToken(){return refresh_token;}

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("account_name", this.getAccount_name());
        result.put("address", this.getAddress());
        result.put("amount_donated", this.getAmount_donated());
        result.put("amount_raised", this.getAmount_raised());
        result.put("blurb", this.getBlurb());
        result.put("date_join", this.getDate());
        result.put("list_camp_init", this.getList_of_campaign_initialize());
        result.put("list_camp_join", this.getList_of_campaign_followed());
        result.put("name", this.getName());
        result.put("profile_pic", this.getProfile_pic());

        return result;
    }

}
