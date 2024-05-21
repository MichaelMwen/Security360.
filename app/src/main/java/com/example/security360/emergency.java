package com.example.security360;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class emergency extends AppCompatActivity {
    private Button emergencyButton;
    private DatabaseHandler db;
    private static final int MY_PERMISSIONS_REQUEST_CODE = 1011;
    private Location location;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        // Naming convention: Use camelCase for variable names
        emergencyButton = findViewById(R.id.but_em);
        db = new DatabaseHandler(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        emergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionsIfNeeded();
            }
        });
    }

    private void requestPermissionsIfNeeded() {
        ArrayList<String> permissionsToRequest = new ArrayList<>();

        // Check and request location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        // Check and request SMS permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.SEND_SMS);
        }

        if (!permissionsToRequest.isEmpty()) {
            String[] permissionsArray = permissionsToRequest.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permissionsArray, MY_PERMISSIONS_REQUEST_CODE);
        } else {
            // All permissions granted, proceed with app functionality
            getLocationAndSendSms();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with functionality
                getLocationAndSendSms();
            } else {
                // Handle permission denied scenarios
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(permissions[0])) {
                        showPermissionRationaleDialog(permissions);
                    } else {
                        Toast.makeText(this, "Please enable permissions in Settings", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void showPermissionRationaleDialog(final String[] permissions) {
        new AlertDialog.Builder(emergency.this)
                .setMessage("These permissions are mandatory for the app to function properly. Please allow them.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(permissions, MY_PERMISSIONS_REQUEST_CODE);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create().show();
    }

    private void getLocationAndSendSms() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    location = task.getResult();
                    sendEmergencySMS();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get location", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle location permission not granted case
        }
    }

    private void sendEmergencySMS() {
        if (location != null) {
            // Get emergency contacts from database
            Cursor cursor = db.getAllContacts();
            ArrayList<String> numbers = new ArrayList<>();
            while (cursor.moveToNext()) {
                numbers.add(cursor.getString(2)); // Assuming phone numbers are in the third column
            }

            // Generate Google Maps link
            String googleMapsLink = "https://www.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude();

            // Create SMS message with Google Maps link
            String message = "I AM IN A EMERGENCY, PLEASE HELP!\nMy Location: " +
                    "\nLatitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude() +
                    "\nGoogle Maps Link: " + googleMapsLink;

            // Send SMS to each contact
            for (String number : numbers) {
                try {
                    SmsManager.getDefault().sendTextMessage(number, null, message, null, null);
                    Log.d("SMS", "SMS sent to: " + number);
                } catch (Exception e) {
                    Log.e("SMS", "Failed to send SMS to: " + number, e);
                }
            }
            Toast.makeText(getApplicationContext(), "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Handle location permission not granted case or location not available
        }
    }


    // New method to generate Google Maps link
    private String generateGoogleMapsLink(double latitude, double longitude) {
        return "https://www.google.com/maps?q=" + latitude + "," + longitude;
    }

    // New method to open Google Maps link
    private void openGoogleMaps(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}