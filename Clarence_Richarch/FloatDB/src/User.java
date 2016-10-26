import java.util.LinkedList;
import java.util.List;

public class User {
    
    //field in database
    String name;
    String account_name;
    String address;
    
    //extra fields
    List<Campaign> list_of_campaign_followed = new LinkedList<Campaign>();
    List<Campaign> list_of_campaign_initialize = new LinkedList<Campaign>();
    
    
    public User(){}
    
    public User(String name, String account_name, String address){
        this.name = name;
        this.account_name = account_name;
        this.address = address;
    }
    
    public String returnName(){
        return this.name;
    }
    
    public String returnAccount_name(){
        return this.account_name;
    }
    
    public String returnAddress(){
        return this.address;
    }
}
