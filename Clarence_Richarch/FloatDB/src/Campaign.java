import java.util.LinkedList;
import java.util.List;

/*
 * this is representation of a Campaign object in our float project
 */
public class Campaign {

    //fields in database
    private String name;
    private String owner_name;
    private String description;
    private Location initial_location;
    private Location destination;
    private int pledge_amount;
    
    //extra fields
    List<Action> list_of_location  = new LinkedList<Action>();
    
    public Campaign(){}
    
    public Campaign(String title, String owner_name, String description, Location init, Location dest, int pledge_amount){
        this.name = title;
        this.owner_name = owner_name;
        this.description = description;
        this.initial_location = init;
        this.destination = dest;
        this.pledge_amount = pledge_amount;
    }
    
    public String returnName(){
        return this.name;
    }
    
    public String returnUser_id(){
        return this.owner_name;
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
