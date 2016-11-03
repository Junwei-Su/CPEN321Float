import java.sql.Date;

public class Action {
//    CREATE TABLE Actions(action_id INT, action_type VARCHAR(30), associated_camp_id INT, 
//            associated_user_id INT, date DATE, location VARCHAR(30), user_id INT);
    
    //field in database
    Action_type action;
    int campaign_id;
    int user_id;
    Date date;
    Location location;
    
    //extra fields
    
    public Action(Action_type action, int campaign_id, int user_id, Date date, Location location){
        this.action = action;
        this.campaign_id = campaign_id;
        this.user_id = user_id;
        this.date = date;
        this.location = location;
    }
    
    public String returnAction(){
        if(this.action == Action_type.SPREAD){
            return "Spread";
        }
        
        if(this.action == Action_type.DONATE){
            return "Donate";
        }
        
        if(this.action == Action_type.INITIALIZE){
            return "Initialize";
        }
        return "";
    }
    
    public int returnCampaign_id(){
        return this.campaign_id;
    }
    
    public int returnUser_id(){
        return this.user_id;
    }
    
    public String returnDate(){
        return this.date.toString();
    }
    
    public String returnLocation(){
        return this.location.toString();
    }
    
}
