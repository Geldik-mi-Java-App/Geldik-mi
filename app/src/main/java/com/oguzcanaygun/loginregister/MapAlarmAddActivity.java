package com.oguzcanaygun.loginregister;

import static androidx.core.location.LocationManagerCompat.requestLocationUpdates;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oguzcanaygun.loginregister.databinding.ActivityMapAlarmAddBinding;

public class MapAlarmAddActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapAlarmAddBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Circle circle;
    String[] locationPermissions;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapAlarmAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationPermissions = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            initLocationCallback();
            requestLocationUpdates();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void initLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (android.location.Location location : locationResult.getLocations()) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                }
            }
        };
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(createLocationRequest(), locationCallback, null);
        } else {
            Toast.makeText(this, "Konum izni reddedildi", Toast.LENGTH_LONG).show();
        }

    }

    private LocationRequest createLocationRequest() {
        return new LocationRequest.Builder(10000)
                .setMinUpdateIntervalMillis(5000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();
    }

    private void showRadiusInputDialog(final LatLng latLng) {
        final EditText radiusInput = new EditText(this);
        radiusInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(this)
                .setTitle("Alan'ın Çapını belirtiniz(Metre)")
                .setView(radiusInput)
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String radiusStr = radiusInput.getText().toString();
                        if (!TextUtils.isEmpty(radiusStr)) {
                            int radius = Integer.parseInt(radiusStr);
                            addCircle(latLng, radius);
                        }


                    }
                })
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    @SuppressLint("ResourceType")
    private void addCircle(LatLng latLng, int radius) {
        if (circle != null) {
            circle.remove();
        }
        int strokeColor = getResources().getColor(R.color.colorRed, getTheme());
        int fillColor = getResources().getColor(R.color.redTransparent, getTheme());
        circle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeWidth(2)
                .strokeColor(strokeColor)
                .fillColor(fillColor));

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                showRadiusInputDialog(latLng);
            }
        });
        if (checkLocationPermission()) {
            // Permissions are granted, enable the location features
            enableLocationFeatures();
        } else {
            // Permissions are not granted, request them
            requestLocationPermissions();
        }

    }
    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_PERMISSION_REQUEST_CODE);
    }
    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    @SuppressLint("MissingPermission")
    private void enableLocationFeatures() {
        // Enable the "My Location" button and blue dot
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                // Call your method to zoom to the user's location
                zoomToUserLocation();
                return false;
            }
        });
    }
    private void zoomToUserLocation(){

        if (mMap != null && fusedLocationProviderClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                            } else {
                                Toast.makeText(this, "Konumunuzun açık olduğundan emin olun", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Handle the case where permissions are not granted
                Toast.makeText(this, "Konum izni gerekli.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocationCallback();
                requestLocationUpdates();
                enableLocationFeatures();
            } else {
                Toast.makeText(this, "Konum izni reddedildi.", Toast.LENGTH_SHORT).show();
                navigateToAppSettings();
            }
        }
    }
    private void navigateToAppSettings() {
        Intent settingsIntent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        settingsIntent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(settingsIntent);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (locationCallback!=null&&fusedLocationProviderClient!=null){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);}
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdates();
    }
   public void backButton(View view){
        Intent intent = new Intent(MapAlarmAddActivity.this, UserActivity.class);
        startActivity(intent);
        finish();
   }

}