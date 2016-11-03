/*
*  Set up on android:
*  https://firebase.google.com/docs/storage/android/start
*/



//creat an instance of Firebase Storage

FirebaseStorage storage = FirebaseStorage.getInstance();
StorageReference storageRef = storage.getReferenceFromUrl("gs://float-568c7.appspot.com");

//Now, we can access the file in such a way:
//StorageReference fileRef = storageRef.child("path/name.jpg");
//The hierarchical structure is just the same as the one on our local hard disk.
//Some methods:
//									   
//				
//				StorageReference ref = storageRef.child("images/user1/pic1.jpg") 
//				StorageReference ref = storageRef.getParent()
//				StorageReference ref = storageRef.getRoot()
//			 	String path = ref.getPath();
//				String path = ref.getName();
//

//To upload a file:
//first creat the reference to the file
StorageReference imagesRef = storageRef.child("images");
StorageReference userRef = imagesRef.child(userID); //userID is a string
StorageReference fileRef = userRef.child(campaignID); //creat the file reference

file = Uri.fromFile(new File(localPathTotheFile));
metadata = new StorageMetadata.Builder()
        .setContentType("image/jpeg")
        .build();
fileRef.putFile(file,metadata);


