
public class Main {
    public static void main(String[] args){
        Database connection = new Database();
        User u = connection.getUser("float1");
        System.out.println(u.returnAddress());
    }
}
