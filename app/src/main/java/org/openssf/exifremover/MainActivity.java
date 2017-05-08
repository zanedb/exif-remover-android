package org.openssf.exifremover;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import it.sephiroth.android.library.exif2.ExifInterface;
import it.sephiroth.android.library.exif2.ExifTag;

public class MainActivity extends AppCompatActivity {

    // Request codes
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 1;
    public static final int PICK_IMAGE = 2;
    public static final int IOExceptionCode = 3;

    // Define Uri for image to be selected
    public static Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getPermissions(View view) {
        String[] perms = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Yay, we have the permissions! Time for toast!
            Toast.makeText(this, "Select an image.", Toast.LENGTH_SHORT).show();
            selectFile();
        } else {
            // No permissions.. waaaa!! Toast reminder
            Toast.makeText(this, "These permissions are required", Toast.LENGTH_SHORT).show();
            // Then ask again
            ActivityCompat.requestPermissions(MainActivity.this,
                    perms, PERMISSIONS_MULTIPLE_REQUEST);
        }
    }

    public void selectFile() {
        // Create new intent for getting image
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        // Create intent for choosing how to complete
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        // Create intent for choosing image
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        // Start intent
        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    public void removeEXIF(View view) {
        if(imageUri == null) {
            // Ask to select image before removing EXIF
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        } else {
            // Tell the user EXIF removal and compression has started
            Toast.makeText(this, "Removing and compressing..", Toast.LENGTH_SHORT).show();
            // Generate filepath from URI of image
            String filepath = getRealPathFromURIPath(imageUri, this);
            // Create FileOutputStream
            FileOutputStream out;
            try {
                // DANGER ZONE: Currently in development, not currently working.. may be removed..
                ExifInterface exif = new ExifInterface();
                android.media.ExifInterface exifinterface = new android.media.ExifInterface(filepath);
                exif.readExif(filepath, ExifInterface.Options.OPTION_ALL);
                List<ExifTag> all_tags = exif.getAllTags();
                
                Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                // Warn about error and give error code. See wiki for details.
                Toast.makeText(this, "An error occurred. Code: " + IOExceptionCode, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Execute code on intent result
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            // Get URI from image selected
            imageUri = data.getData();
            // Initialize header TextView for editing
            TextView header = (TextView)findViewById(R.id.header);
            // Change text when ready to remove EXIF
            header.setText("Ready to remove EXIF");
        }
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        // Create new cursor
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            // If cursor is empty, return path of URI file
            return contentURI.getPath();
        } else {
            // Otherwise, return the file path
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
}
