public class getUser extends accessData{
	
	//dataRef should be the reference to a user
	
	public getUser(DatabaseReference dataRef){
		super(dataRef);
		printJsonObject(super.obj);
	}
	public void printJsonObject(JSONObject obj){
		log.d("json",obj.toString());
	}
	//public String getName(super.obj){
	//	return 
	//}
	
	public String getName(){
		
	}
	
	
	
}
