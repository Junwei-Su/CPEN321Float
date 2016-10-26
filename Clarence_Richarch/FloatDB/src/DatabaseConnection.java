import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnection {
    private Connection connection;
    private Statement statement;
    private ResultSet result_set;
    
    private String address = "jdbc:mysql://floatteamdb.coihfnsthcgh.us-west-2.rds.amazonaws.com";
    private String connection_name = "FloatTeam";
    private String password = "floatteam1";
    
    public DatabaseConnection(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(address,connection_name,password);
            statement = connection.createStatement();
            
        }catch (Exception ex){
            System.out.println("Error: "+ ex);
        }
        
    }
    
    public void getUserData(){
        try{
            String query = "SELECT * FROM floatDB.User";
            result_set = statement.executeQuery(query);
            System.out.println("Records from Database: ");
            while(result_set.next()){
                System.out.println(result_set.getString("name"));
                System.out.println(result_set.getString("address"));
            }
        }catch(Exception ex){
            
        }
    }
}
