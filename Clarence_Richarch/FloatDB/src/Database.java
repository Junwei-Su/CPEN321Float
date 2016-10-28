import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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
            System.out.println("Records from Database: ");
            while(result_set.next()){
                String name = result_set.getString("user_name");
                String account1 = result_set.getString("account");
//                String address = result_set.getString("address");
                //expand here when when expand the database for user
                //need to extract a list of associate campaigns
                to_return = new User(name, account1);
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
            String query = "SELECT * FROM floatDB.Campaign where name = '"+ campaign_name + "\'";
            result_set = statement.executeQuery(query);
            System.out.println("Records from Database: ");
            while(result_set.next()){
                String name = result_set.getString("name");
                String ownder_name = result_set.getString("account");
                String description = result_set.getString("description");
                String initial_loc_string = result_set.getString("initial_location");
//                private String name;
//                private int owner_id;
//                private String description;
//                private Location initial_location;
//                private Location destination;
//                private int pledge_amount;
                //to_return = new Campaign(name, ownder_name, address);
            }
        }catch(Exception ex){
            
        }
        return to_return;
    }
    
}
