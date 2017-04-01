package com.cpen321.floatproject.utilities;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

/**
 * Created by Little_town on 12/27/2016.
 */

/*
 * this class serves as a container for some common activity methods shared among the app activities
 */
public class ActivityUtility extends Activity {

    /**
     * Updates an ImageView object with file in Firebase database at storageReference
     * @param storageReference
     * @param imageViewID
     */
    public void setPictureOnImageViewID(StorageReference storageReference, final int imageViewID){
        final File localFile;
        try {
            localFile = File.createTempFile("images", "png");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    ImageView testPic = (ImageView) findViewById(imageViewID);
                    Uri uri = Uri.fromFile(localFile);
                    testPic.setImageURI(uri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an ImageView object with file in Firebase database at storageReference
     * @param storageReference
     * @param imageView
     */
    public static void setPictureOnImageView(StorageReference storageReference, final ImageView imageView){
        final File localFile;
        try {
            localFile = File.createTempFile("images", "png");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Uri uri = Uri.fromFile(localFile);
                    imageView.setImageURI(uri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts given angle in degrees to radians.
     * @param degrees angle to be converted into radians.
     * @return given angle converted from degrees into radians.
     */
    public static double degreestoradians(double degrees){
        double radiansperdegree = Math.PI/180.0;

        return degrees*radiansperdegree;
    }

    /**
     * Converts given distance on earth in metres to geographical degrees.
     * Note that this is an approximation.
     * @param metres distance to be converted into degrees.
     * @return given distance in degrees.
     */
    public static double metrestodegrees(int metres){
        double metresperdegree = 111111.0;

        return metres/metresperdegree;
    }
}
