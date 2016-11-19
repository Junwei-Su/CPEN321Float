public class getUser extends accessData{
	
	//dataRef should be the reference to a user
	
	public getUser(DatabaseReference dataRef){
		super(dataRef);
		printJsonObject(super.obj);
	}
	public void printJsonObject(JsonObject obj){
		log.d("json",obj.toString());
	}
	//public String getName(super.obj){
	//	return 
	//}
	
	public String getName(){
		return super.obj.getString("name");
	}
	public String getAccountName(){
		return super.obj.getString("account_name");
	}
	public String getDate(){
		return super.obj.getString("date");
	}
	public String getBlur(){
		return super.obj.getString("blurb");
	}
	public boolean getIsCharity(){
		return super.obj.getBoolean("is_charity");
	}
	public int getAmountGain(){
		return super.obj.getInt("amount_gain");
	}
	public int getAmountraised(){
		return super.obj.getInt("amount_raised");
	}
	public int getAmountDonated(){
		return super.obj.getInt("amount_donated");
	}
	public String getCity(){
		return super.obj.getString("city");
	}
	public String getProvince(){
		return super.obj.getString("province");
	}
	public String getCountry(){
		return super.obj.getString("country");
	}
	public String getMetadataID(){
		return super.obj.getString("metadata_id");
	}
	public String getJsonApiContext(){
		return super.obj.getString("json_api_context");
	}
	public JsonArray getListOfCampaignFollowed(){
		return super.obj.getJsonArray("list_of_campaign_followed");
	}
	public JsonArray getListOfCampaignInitialize(){
		return super.obj.getJsonArray("list_of_campaign_initialize");
	}
	
	
	
}
