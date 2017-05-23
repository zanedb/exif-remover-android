package org.openssf.exifremover;

// Main android imports
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

// Main Java imports
import java.io.File;
import java.io.IOException;

// Easy Permissions - simple android permissions manager by github/google-samples
import pub.devrel.easypermissions.EasyPermissions;

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
        String[] perms = new String[0];
        perms = new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Yay, we have the permissions! Time for toast!
            Toast.makeText(this, getString(R.string.select_image), Toast.LENGTH_SHORT).show();
            selectFile();
        } else {
            // No permissions.. waaaa!! Toast reminder
            Toast.makeText(this, getString(R.string.required_permissions), Toast.LENGTH_SHORT).show();
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
        Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.select_image));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        // Start intent
        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    public void buttonRemoveEXIF(View view) {
        if(imageUri == null) {
            // Ask to select image before removing EXIF
            Toast.makeText(this, getString(R.string.select_image), Toast.LENGTH_SHORT).show();
        } else {
            // Tell the user EXIF removal and compression has started
            Toast.makeText(this, getString(R.string.removing), Toast.LENGTH_SHORT).show();
            // Generate filepath from URI of image
            String filepath = getRealPathFromURIPath(imageUri, this);
            if(filepath.endsWith("jpg")) {
                // Create string for edited file path
                String newfilepath = filepath;

                // Create new file for renaming
                File jpeg = new File(filepath);

                // Remove JPG extension (for android EXIF manipulation)
                newfilepath = newfilepath.substring(0, newfilepath.length() - 3);

                // Replace with JPEG extension (for android EXIF manipulation)
                newfilepath += "jpeg";

                // Add new file for renaming purposes
                File newjpeg = new File(newfilepath);

                // Rename file from JPG to JPEG (required for ExifInterface EXIF manipulation, no image loss)
                boolean rename = jpeg.renameTo(newjpeg);

                // Check if file renaming was successful
                if(rename) {
                    // Call function to remove EXIF data
                    removeEXIF(newfilepath);
                }
            } else if(filepath.endsWith("jpeg")) {
                removeEXIF(filepath);
            } else {
                Toast.makeText(this, getString(R.string.file_support), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void removeEXIF(String filepath) {
        try {
            // Create new ExifInterface for managing EXIF tags
            ExifInterface exif = new ExifInterface(filepath);

            // Set all dangerous EXIF tags to null
            exif.setAttribute(ExifInterface.TAG_DATETIME, "");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, "");
            }
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, "");
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, "");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_APERTURE_VALUE, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_ARTIST, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_COMPONENTS_CONFIGURATION, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_COMPRESSION, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_EXPOSURE_BIAS_VALUE, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_EXPOSURE_INDEX, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_EXPOSURE_MODE, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_EXPOSURE_PROGRAM, "");
            }
            exif.setAttribute(ExifInterface.TAG_EXPOSURE_TIME, "");
            exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, "");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_AREA_INFORMATION, "");
            }
            exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, "");
            exif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, "");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_DEST_BEARING, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_DEST_BEARING_REF, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_DEST_DISTANCE, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_DEST_DISTANCE_REF, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_DEST_LATITUDE, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_DEST_LATITUDE_REF, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_DIFFERENTIAL, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_DOP, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_VERSION_ID, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_TRACK_REF, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_TRACK, "");
            }
            exif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, "");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_STATUS, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_SPEED_REF, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_SPEED, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_SATELLITES, "");
            }
            exif.setAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD, "");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_MAP_DATUM, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_IMG_DIRECTION_REF, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_GPS_MEASURE_MODE, "");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif.setAttribute(ExifInterface.TAG_SOFTWARE, "");
            }

            // Write edited attributes back to file
            exif.saveAttributes();

            // Tell user it's done
            Toast.makeText(this, getString(R.string.done), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // Warn about error and give error code. See wiki for complete error code list.
            Toast.makeText(this, "IOException, [Code " + IOExceptionCode + "]", Toast.LENGTH_SHORT).show();
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
            header.setText(getString(R.string.ready_to_remove));
        }
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        // Create new cursor
        @SuppressLint("Recycle") Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
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
