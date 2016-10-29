
public class Main {
    public static void main(String[] args){
        Database database = new Database();
        User u = database.getUser("float1");
        System.out.println(u.returnAccount_name());
    }
}
