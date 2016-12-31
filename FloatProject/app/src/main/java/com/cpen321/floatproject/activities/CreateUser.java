package com.cpen321.floatproject.activities;

import android.net.Uri;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cpen321.floatproject.R;
import com.cpen321.floatproject.database.DB;
import com.cpen321.floatproject.users.User;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by sfarinas on 12/30/2016.
 */
public class CreateUser extends Activity {
    Uri selectedImageURI;

    ImageView profilepic;

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
        EditText userDetail = (EditText) findViewById(R.id.username);
        String username = userDetail.getText().toString();

        userDetail = (EditText) findViewById(R.id.namein);
        String name = userDetail.getText().toString();

        userDetail = (EditText) findViewById(R.id.blurbin);
        String blurb = userDetail.getText().toString();

        //get current date as string
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        String date = dateFormat.format(calendar.getTime());

        String profilePic_url = username + "_profilepic.jpg";

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

        Profile profile = Profile.getCurrentProfile();
        if(profile!=null){
            String account_name = profile.getId();

            User newUser = new User(name, account_name, date, blurb, 0, 0, "123 Fiction Lane",
                    new ArrayList<String>(), new ArrayList<String>(), profilePic_url);

            DB.user_ref.child(account_name).setValue(newUser);
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
                Log.d("Tag", selectedImageURI.toString());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }
}
