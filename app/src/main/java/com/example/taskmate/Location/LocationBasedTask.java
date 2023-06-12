package com.example.taskmate.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.PaintKt;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taskmate.Activities.Constants;
import com.example.taskmate.Activities.MainActivity;
import com.example.taskmate.Adapter.PlaceAutoSuggestAdapter;
import com.example.taskmate.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.taskmate.databinding.ActivityLocationBasedTaskBinding;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

public class LocationBasedTask extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private ActivityLocationBasedTaskBinding binding;
    private GoogleApiClient.Builder googleApiClientBuilder;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastlocation;
    private Marker currentuserlocationmarker;
    private static final int Request_user_location_code = 99;
    private LocationCallback locationCallback;

    private static final String NOTIFICATION_CHANNEL_ID = "TaskNotificationChannel";
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private Address userAddress;
    private Double Targetlat, Targetlng, userlat, userlang;
    private String TargetName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkuserlocationpermission();
        }


        binding = ActivityLocationBasedTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // Handle the received location updates here
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    // Do something with the location data
                }
            }
        };
    }


    public void onClick(View view) {
        if (view.getId() == R.id.searchaddress) {
            AutoCompleteTextView addressfield = findViewById(R.id.location_search);
            addressfield.setAdapter(new PlaceAutoSuggestAdapter(LocationBasedTask.this, android.R.layout.simple_list_item_1));
            String address = addressfield.getText().toString();

            List<Address> addressList = null;
            MarkerOptions markerOptions = new MarkerOptions();


            if (!TextUtils.isEmpty(address)) {
                Geocoder geocoder = new Geocoder(LocationBasedTask.this);
                try {
                    addressList = geocoder.getFromLocationName(address, 1);
                    if (addressList != null) {
                        for (int i = 0; i < addressList.size(); i++) {
                            Address useraddress = addressList.get(i);
                            LatLng latLng = new LatLng(useraddress.getLatitude(), useraddress.getLongitude());
                            Targetlat = useraddress.getLatitude();
                            Targetlng = useraddress.getLongitude();
                            markerOptions
                                    .position(latLng)
                                    .title(address)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            TargetName = address;
                            mMap.addMarker(markerOptions);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(@NonNull Marker marker) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LocationBasedTask.this);
                                    builder.setTitle("Add to Task")
                                            .setMessage("Do you want to add this location to your task?")
                                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    userAddress = useraddress;
                                                }
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                    return false;
                                }
                            });
                        }
                    }

                } catch (IOException e) {
                    Constants.ErrorToast(LocationBasedTask.this, "Error : " + e.getMessage());
                }
            } else {
                Constants.ErrorToast(LocationBasedTask.this, "Enter Address Please");
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            buildGoogleApiClient();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_location_code);
        }


    }

    public boolean checkuserlocationpermission() {
        if (ContextCompat.checkSelfPermission(LocationBasedTask.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_location_code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_location_code);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Request_user_location_code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, trigger the notification
                double distance = calculateDistance(userlat, userlang, Targetlat, Targetlng);
                String notificationMessage = "Target location: " + TargetName + "\nDistance: " + distance + " meters away";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    triggerNotification(notificationMessage);
                }
            } else {
                // Permission denied, handle it accordingly (e.g., show an error message)
                Constants.ErrorToast(this, "Location Permission Denied");
            }
            return;
        }

    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClientBuilder = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API);
        googleApiClient = googleApiClientBuilder.build();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lastlocation = location;
        if (currentuserlocationmarker != null) {
            currentuserlocationmarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        userlat = location.getLatitude();
        userlang = location.getLongitude();
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentuserlocationmarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));


        if (googleApiClientBuilder != null) {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        }


        double distance = calculateDistance(userlat, userlang, Targetlat, Targetlng);

        if (distance <= 500) {
            String notificationMessage = "Target location: " + TargetName + "\nDistance: " + distance + " meters away";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                triggerNotification(notificationMessage);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void triggerNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.tasklogo)
                .setContentTitle("Task Notification")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Trigger the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }


//    private void triggerNotification(String message) {
//        if (notificationManager == null) {
//            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        }
//
//        // Create notification channel (required for Android Oreo and above)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Task Notifications", NotificationManager.IMPORTANCE_HIGH);
//            channel.setDescription("Notifications for task proximity");
//            channel.enableLights(true);
//            channel.setLightColor(Color.RED);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        // Create the notification
//        Notification.Builder builder;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID);
//        } else {
//            builder = new Notification.Builder(this);
//        }
//        builder.setContentTitle("Task Proximity Alert")
//                .setContentText(message)
//                .setSmallIcon(R.drawable.your_notification_icon)
//                .setAutoCancel(true);
//
//        // Display the notification
//        notificationManager.notify(NOTIFICATION_ID, builder.build());
//    }
//


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000) // Update interval in milliseconds
                .setFastestInterval(2000); // Fastest update interval in milliseconds




        // Check if location services are available
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request location updates using the newer FusedLocationProviderClient
//            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//           fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            LocationServices.getFusedLocationProviderClient(this)
                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            // Handle location updates
                            onLocationChanged(locationResult.getLastLocation());
                        }
                    }, null);
        }

    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Radius of the Earth in kilometers
        double earthRadius = 6371;

        // Convert latitude and longitude values to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate the differences between coordinates
        double latDiff = lat2Rad - lat1Rad;
        double lonDiff = lon2Rad - lon1Rad;

        // Calculate the distance using the Haversine formula
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        // Return the distance in meters
        return distance * 1000;
    }
}