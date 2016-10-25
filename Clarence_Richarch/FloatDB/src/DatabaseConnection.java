import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnection {
    private Connection connection;
    private Statement statement;
    private ResultSet result_set;
    
    private String address = "floatteamdb.coihfnsthcgh.us-west-2.rds.amazonaws.com";
    private String connection_name = "FloatTeam";
    private String password = "floatteam1";
    
    public DatabaseConnection(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(address,connection_name,password);
            
            
        }catch (Exception ex){
            System.out.println("Error: "+ ex);
        }
        
    }
}
