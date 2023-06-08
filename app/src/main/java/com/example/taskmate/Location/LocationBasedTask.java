package com.example.taskmate.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.PaintKt;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentuserlocationmarker;
    private static final int Request_user_location_code = 99 ;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
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


    public void onClick(View view)
    {
        if (view.getId() == R.id.searchaddress) {
            AutoCompleteTextView addressfield = findViewById(R.id.location_search);
            addressfield.setAdapter(new PlaceAutoSuggestAdapter(LocationBasedTask.this,android.R.layout.simple_list_item_1));
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
                            markerOptions
                                    .position(latLng)
                                    .title(address)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            mMap.addMarker(markerOptions);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
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
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
            buildGoogleApiClient();
            return;
        }


    }

    public boolean checkuserlocationpermission()
    {
        if (ContextCompat.checkSelfPermission(LocationBasedTask.this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_location_code);
            }
            else
            {
                ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_location_code);
            }
            return false;
        }
        else
        {
           return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Request_user_location_code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                    }
                }
                else
                {
                    Constants.ErrorToast(this , "Permission Denied" );
                }
                return;
        }
    }

    protected synchronized void buildGoogleApiClient()
    {
            googleApiClient = new GoogleApiClient.Builder(LocationBasedTask.this)
                    .addConnectionCallbacks(LocationBasedTask.this)
                    .addOnConnectionFailedListener(LocationBasedTask.this)
                    .addApi(LocationServices.API)
                    .build();

            googleApiClient.connect();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lastlocation = location;
        if (currentuserlocationmarker !=null)
        {
            currentuserlocationmarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentuserlocationmarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));

        if (googleApiClient!=null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) LocationBasedTask.this);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(500);

        // Check if location services are available
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request location updates using the newer FusedLocationProviderClient
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }

    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }






}