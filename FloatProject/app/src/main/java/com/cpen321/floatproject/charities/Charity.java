package com.cpen321.floatproject.charities;

/**
 * Created by Little_town on 12/24/2016.
 */

/*
 * this class represent a charity object in the application
 */

public class Charity {

    private String name;
    private String logo;
    private String description;
    private String link;

    public Charity(){}

    public Charity(String name, String logo, String description, String link){
        this.name=name;
        this.logo=logo;
        this.description=description;
        this.link=link;
    }

    public String getName(){ return this.name; }

    public String getLogo(){ return this.logo; }

    public String getDescription(){ return this.description; }

    public String getLink(){ return this.link; }

}
