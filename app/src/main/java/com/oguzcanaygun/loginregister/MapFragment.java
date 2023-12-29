package com.oguzcanaygun.loginregister;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {
    public interface OnAlarmSelectedListener {
        void onAlarmSelected(double latitude, double longitude, double radius);
    }

    private OnAlarmSelectedListener onAlarmSelectedListener;
    FusedLocationProviderClient fusedLocationProviderClient;
    GoogleMap googleMap;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapFragment.this.googleMap = googleMap;
            LatLng worldView = new LatLng(0, 0);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(worldView));
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(1.0f)); // Adjust the zoom level as needed
            enableMyLocation();

        }
    };

    public void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    public void setOnAlarmSelectedListener(OnAlarmSelectedListener listener) {
        this.onAlarmSelectedListener = listener;
    }

    public void drawCircleOnMap(double latitude, double longitude, double radius) {

        if (googleMap != null) {
            LatLng alarmLocation = new LatLng(latitude, longitude);
            googleMap.clear();
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {

                LatLng userLocation;
                if (location != null) {
                    userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                }
                else {userLocation=null;}

            // Calculate the bounds that include both the circle and user's location
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(alarmLocation);
            builder.include(userLocation);
            LatLngBounds bounds = builder.build();

                CircleOptions circleOptions = new CircleOptions()
                        .center(alarmLocation)
                        .radius(radius)
                        .strokeColor(Color.RED)
                        .fillColor(Color.argb(128, 255, 0, 0));

                googleMap.addCircle(circleOptions);
            // Adjust camera position to fit both the circle and user's location
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 200); // 100 is the padding
            googleMap.animateCamera(cameraUpdate);

        });
    }}
    private float getZoomLevel(double radius) {
        // Adjust this multiplier based on your preference for zoom level
        double scale = radius / 500; // Scale factor
        return (float) (16 - Math.log(scale) / Math.log(2));
    }


    private void findUserLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(userLocation).title("Konumunuz"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
            supportMapFragment.getMapAsync(callback);
        }

        Button findLocationButton = view.findViewById(R.id.findLocationButton);
        findLocationButton.setOnClickListener(v -> findUserLocation());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}