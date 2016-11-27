package com.cpen321.floatproject;

import java.util.LinkedList;
import java.util.List;

public class User {

    //field in database
    String name;
    String account_name;
    String date;
    String blurb;
    boolean is_charity;
    int amount_gain;
    int amount_raised;
    int amount_donated;
    String city;
    String province;
    String country;
    String metadata_id;
    String refresh_token;

    //extra fields
    List<Campaign> list_of_campaign_followed = new LinkedList<Campaign>();
    List<Campaign> list_of_campaign_initialize = new LinkedList<Campaign>();


    public User(){}

    public User(String name, String account){
        this.name = name;
        this.account_name = account;
    }

    public User(String name, String account_name,String date, String blurb, boolean is_charity, int amount_gain,
                int amount_raised, int amount_donated, String city, String province, String country){
        this.name = name;
        this.account_name = account_name;
        this.date = date;
        this.blurb = blurb;
        this.is_charity=is_charity;
        this.amount_gain = amount_gain;
        this.amount_raised = amount_raised;
        this.amount_donated = amount_donated;
        this.city = city;
        this.province = province;
        this.country = country;
    }

    public String returnName(){
        return this.name;
    }

    public String returnAccount_name(){
        return this.account_name;
    }

    public String returnDate(){
        return this.date;
    }

    public String returnBlurb(){
        return this.blurb;
    }

    public boolean returnIs_charity(){
        return this.is_charity;
    }

    public int returnAmount_gain(){
        return amount_gain;
    }

    public int returnAmount_raised(){
        return amount_raised;
    }
}