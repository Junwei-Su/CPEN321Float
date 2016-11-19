public abstract class accessData{
	protected DatabaseReference dataRef;
	protected JSONObject obj; //can specify the actual type later
	
	protected accessData(DatabaseReference dataRef){
		this.dataRef = dataRef;
		getData(dataRef);
	}
	
	protected void getData(DatabaseReference dataRef){
		dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						this.obj = dataSnapshot.getValue(JSONObject.class);
					}
					@Override
					public void onCancelled(DatabaseError databaseError) {
					}
		}
	}
}
