
public class User {
    String name;
    String account_name;
    String address;
    
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
