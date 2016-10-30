import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

public class Database{
    private Connection connection;
    private Statement statement;
    private ResultSet result_set;
    
    private String address = "jdbc:mysql://floatteamdb.coihfnsthcgh.us-west-2.rds.amazonaws.com";
    private String connection_name = "FloatTeam";
    private String password = "floatteam1";
    
    public Database(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(address,connection_name,password);
            statement = connection.createStatement();
            
        }catch (Exception ex){
            System.out.println("Error: "+ ex);
        }
        
    }
    
    //query User information from the database
    //require: user account name
    //renturn: User object
    public User getUser(String account){
        User to_return = new User();
        try{
            String query = "SELECT * FROM floatDB.User where account = '"+ account + "\'";
            result_set = statement.executeQuery(query);
            while(result_set.next()){
                //extracting information
                String account_name = result_set.getString("account");
                String name = result_set.getString("user_name");
                String date = result_set.getString("date_join");
                String blurb = result_set.getString("blurb");
                boolean is_charity = Boolean.valueOf(result_set.getString("is_charity"));
                int amount_gained = Integer.parseInt(result_set.getString("amount_gained"));
                int amount_raised = Integer.parseInt(result_set.getString("amount_raised"));
                int amount_donated = Integer.parseInt(result_set.getString("amount_donated"));
                String city = result_set.getString("city");
                String province = result_set.getString("province");
                String country = result_set.getString("country");
                return new User(name, account_name, date, blurb, is_charity, 
                        amount_gained, amount_raised, amount_donated, city, province, country);
            }
        }catch(Exception ex){
            
        }
        return to_return;
    }
    
    //query Campaign information from the database
    //require: campaign name
    //return: campaign object
    public Campaign getCampaign(String campaign_name){
        Campaign to_return = new Campaign();
        try{
            String query = "SELECT * FROM floatDB.Campaign where campaign_name = '"+ campaign_name + "\'";
            result_set = statement.executeQuery(query);
            while(result_set.next()){
                String campaignName = result_set.getString("campaign_name");
                String charity = result_set.getString("charity");
                int goal_amount = Integer.parseInt(result_set.getString("goal_amount"));
                String initial_loc_string = result_set.getString("initial_location");
                Location initial_loc = Location.toLocation(initial_loc_string);
                String destination_string = result_set.getString("destination");
                Location destination = Location.toLocation(destination_string);
                String owner_account = result_set.getString("owner_account");
                int accumulated_donation = Integer.parseInt(result_set.getString("accumulated_donation"));
                int time_left = Integer.parseInt(result_set.getString("time_left"));
                String description = result_set.getString("description");
              //query the user name based on the owner_account
                User owner = this.getUser(owner_account);
                

                return new Campaign(campaignName,owner, description, charity, initial_loc, 
                        destination, goal_amount, time_left, accumulated_donation);
            }
        }catch(Exception ex){
        }
        return to_return;
    }
    
}
