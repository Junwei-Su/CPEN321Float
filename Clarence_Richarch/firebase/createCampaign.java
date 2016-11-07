@IgnoreExtraProperties
public class Campaign{
	class location{
		public Long latitude;
		public Long longitude;
		public location(Long latitude, Long longitude){
			this.latitude = latitude;
			this.longitude = longitude;
		}
	}
	public String accumulated_donation;
	public String campaign_name;
	public String charity;
	public String description;
	public String destination;
	public String goal_amount;
	public location initial_location;
	public List<location> list_locations;
	public String owner_account;
	public String time_left;
	
	public Campaign(){	
	}
	
	public Campaign(String accumulated_donation, String campaign_name, String charity, String description,
		String destination, String goal_amount, Long latitude, Long longitude, String owner_account, String time_left){
			this.accumulated_donation = accumulated_donation;
			this.campaign_name = campaign_name;
			this.charity = charity;
			this.description = description;
			this.destination = destination;
			this.goal_amount = goal_amount;
			this.owner_account = owner_account;
			this.time_left = time_left;
			this.initial_location(latitude,longitude);
			list_locations.add(initial_location);
		}
}


private DatabaseReference mDatabase;
// ...
mDatabase = FirebaseDatabase.getInstance().getReference();


Campaign temp = new Campaign("accumulated_donation", "campaign_name", "charity", "description",
		"destination", "goal_amount", 321, 123, "owner_account", "time_left");

mDatabase.child("campaigns").child("tempID").setValue(user);





