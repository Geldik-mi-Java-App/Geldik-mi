package com.oguzcanaygun.loginregister;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.google.android.material.snackbar.Snackbar;
import com.oguzcanaygun.loginregister.databinding.ActivityMapAlarmAddBinding;

public class MapAlarmAddActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapAlarmAddBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Circle circle;
    String[] locationPermissions;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private int intentRadius;
    private LatLng intentLatlng;
    private boolean permissionDeniedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapAlarmAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Additional Permissions
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.VIBRATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        }, 2);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationPermissions = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        };

        initLocationCallback();

        if (checkLocationPermissions()) {
            requestLocationUpdates();
        } else {
            requestLocationPermissions();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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
        if (checkLocationPermissions()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(createLocationRequest(), locationCallback, null);
        } else {
            Toast.makeText(this, "Konum izni gerekli", Toast.LENGTH_LONG).show();
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
        intentRadius = radius;
        intentLatlng = latLng;
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
        if (checkLocationPermissions()) {
            enableLocationFeatures();
        } else {
            requestLocationPermissions();
        }
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private boolean checkLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    @SuppressLint("MissingPermission")
    private void enableLocationFeatures() {
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                zoomToUserLocation();
                return false;
            }
        });
    }

    private void zoomToUserLocation() {
        if (mMap != null && fusedLocationProviderClient != null) {
            if (checkLocationPermissions()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
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
                Toast.makeText(this, "Konum izni gerekli.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (checkLocationPermissions()) {
                initLocationCallback();
                requestLocationUpdates();
                enableLocationFeatures();
            } else {
                if (permissionDeniedOnce) {
                    showPermissionSnackbar();
                } else {
                    Toast.makeText(this, "Konum izni gerekli.", Toast.LENGTH_SHORT).show();
                    permissionDeniedOnce = true;
                }
            }
        }
    }

    private void showPermissionSnackbar() {
        Snackbar.make(binding.getRoot(), "Konum izni verilmedi. Uygulama ayarlarına gidin ve izinleri etkinleştirin.", Snackbar.LENGTH_INDEFINITE)
                .setAction("Ayarlar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        navigateToAppSettings();
                    }
                })
                .show();
    }

    private void navigateToAppSettings() {
        Intent settingsIntent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        settingsIntent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(settingsIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationCallback != null && fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdates();
    }

    public void backButton(View view) {
        Intent intent = new Intent(MapAlarmAddActivity.this, UserActivity.class);
        startActivity(intent);
        finish();
    }

    public void sendAlarm(View view) {
        if (circle != null) {
            Intent intent = new Intent(MapAlarmAddActivity.this, UserActivity.class);
            intent.putExtra("radius", intentRadius);
            intent.putExtra("latlng", intentLatlng);

            final EditText nameInput = new EditText(this);
            nameInput.setInputType(InputType.TYPE_CLASS_TEXT);

            new AlertDialog.Builder(this)
                    .setTitle("kaydedeceğiniz alarmın adını belirtiniz")
                    .setView(nameInput)
                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String alarmNameStr = nameInput.getText().toString();
                            if (!alarmNameStr.isEmpty()) {
                                intent.putExtra("alarm_ismi", alarmNameStr);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Geçerli bir alarm ismi belirtiniz", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        } else if (circle == null) {
            Toast.makeText(this, "Lütfen önce ulaşmak istediğiniz alanı belirleyiniz", Toast.LENGTH_SHORT).show();
        }
    }
}