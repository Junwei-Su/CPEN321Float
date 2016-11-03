
public class Location {
    private double longtitude;
    private double latitude;
    
    public Location(double d, double e){
        this.longtitude=d;
        this.latitude=e;
    }
    
    public double returnLongtitude(){
        return this.longtitude;
    }
    
    public double returnLatitude(){
        return this.latitude;
    }
    
    public String toString(){
        return String.valueOf(this.longtitude) +"," +String.valueOf(this.latitude);
    }
    
    public static Location toLocation(String s){
        String delims = ",";
        String[] tokens = s.split(delims);
        return new Location(Double.valueOf(tokens[0]),Double.valueOf(tokens[1]));
    }
}
