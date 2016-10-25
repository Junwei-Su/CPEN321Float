
public class Location {
    private Float longtitude;
    private Float latitude;
    
    public Location(float lo, float la){
        this.longtitude=lo;
        this.latitude=la;
    }
    
    public float returnLongtitude(){
        return this.longtitude;
    }
    
    public float returnLatitude(){
        return this.latitude;
    }
    
}
