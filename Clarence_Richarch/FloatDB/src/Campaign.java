/*
 * this is representation of a Campaign object in our float project
 */
public class Campaign {
    
    //name VARCHAR(100), initializer INT,
    //description VARCHAR(1000), initial_location VARCHAR(30), destination VARCHAR(30), pledge_amount INT);
    
    private String name;
    private int owner_id;
    private String description;
    private Location initial_location;
    private Location destination;
    private int pledge_amount;
    
    
    public Campaign(String title, int user_id, String description, Location init, Location dest, int pledge_amount){
        this.name = title;
        this.user_id = user_id;
        this.description = description;
        this.initial_location = init;
        this.destination = dest;
        this.pledge_amount = pledge_amount;
    }
    
    public String returnName(){
        return this.name;
    }
    
    public int returnUser_id(){
        return this.owner_id;
    }
    
    public String returnDescription(){
        return this.description;
    }
    
    public Location returnInitial_location(){
        return this.initial_location;
    }
    
    public Location returnDestination(){
        return this.destination;
    }
    
    public int returnPledge_amount(){
        return this.pledge_amount;
    }
}
