package com.cpen321.floatproject.activities.UserActivities;

import android.net.Uri;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cpen321.floatproject.R;
import com.cpen321.floatproject.activities.Log_in_and_map.MapPage;
import com.cpen321.floatproject.database.DB;
import com.cpen321.floatproject.users.User;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by sfarinas on 12/30/2016.
 */
public class CreateUser extends Activity {
    private Uri selectedImageURI;
    private ImageView profilepic;
    private String name;
    private String userid;
    private String address;
    private String blurb;
    private EditText userDetail;
    private int INIT_AMOUNT=0;
    private Profile profile;
    private String profilePic_url = "default_profilepic.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);

        profilepic = (ImageView) findViewById(R.id.photo);
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, 1);
    }

    public void saveuser(View view) {

        profile = Profile.getCurrentProfile();
        userid = profile.getId();

        userDetail = (EditText) findViewById(R.id.namein);
        name = userDetail.getText().toString();

        userDetail = (EditText) findViewById(R.id.userAddress);
        address = userDetail.getText().toString();

        userDetail = (EditText) findViewById(R.id.blurbin);
        blurb = userDetail.getText().toString();

        //get current date as string
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        String date = dateFormat.format(calendar.getTime());

        if(selectedImageURI != null) {
            profilePic_url = userid + "_profilepic.jpg";

            StorageReference picRef = DB.images_ref.child(profilePic_url);
            UploadTask uploadTask = picRef.putFile(selectedImageURI);


            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });
        }

        Profile profile = Profile.getCurrentProfile();
        if(profile!=null){

            User newUser = new User(name, userid, date, blurb, INIT_AMOUNT, INIT_AMOUNT, address,
                    new ArrayList<String>(), new ArrayList<String>(), profilePic_url);

            //DB.user_ref.child(userid).setValue(newUser);
            DB.usersDBinteractor.put(newUser, DB.root_ref);

            Intent intent = new Intent(this, MapPage.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 1 && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                selectedImageURI = data.getData();
                profilepic.setImageURI(selectedImageURI);

            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }
}
