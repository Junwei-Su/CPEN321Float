import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class User {
	

	public String amount_donated;
	public String amount_gained;
	public String amount_raised;
	public String blurb;
	public String city;
	public String country;
	public String date_join;
	public String is_charity;
	public List<String> list_camp_init;
	public List<String> list_camp_join;
	public String name;
	public String profile_pic;
	public String province;

	public User() {
	}

	public User(amount_donated,amount_gained,amount_raised,blurb,city,country,date_join,is_charity,name,profile_pic,province) {
		this.amount_donated = amount_donated;
		this.amount_gained=amount_gained;
		this.amount_raised=amount_raised;
		this.blurb=blurb;
		this.city=city;
		this.country=country;
		this.date_join=date_join;
		this.is_charity=is_charity;
		this.name = name;
		this.profile_pic = profile_pic;
		this.province = province;
		list_camp_init = new ArrayList<String>();
		list_camp_join = new ArrayList<String>();
	}
}



//code to create a new campaign 
private DatabaseReference mDatabase;
// ...
mDatabase = FirebaseDatabase.getInstance().getReference();
User temp = new User();
mDatabase.child("users").child("testUserFromApp").setValue(temp);
