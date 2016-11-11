import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Charities {
	
	public logo;

	public Charities() {
	}

	public Charities(String logo){
		this.logo = logo;
	}
	
}


//code to create a new campaign 
private DatabaseReference mDatabase;
// ...
mDatabase = FirebaseDatabase.getInstance().getReference();
Charities temp = new Charities("charity");
mDatabase.child("charities").child("testCharityFromApp").setValue(temp);
