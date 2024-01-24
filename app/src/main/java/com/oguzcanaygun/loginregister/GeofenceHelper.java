package com.oguzcanaygun.loginregister;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class GeofenceHelper {

    private final GeofencingClient geofencingClient;

    public GeofenceHelper(Context context) {
        geofencingClient = LocationServices.getGeofencingClient(context);
    }

    public void addGeofence(List<Geofence> geofenceList, PendingIntent geofencePendingIntent,Context context) {
        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .addGeofences(geofenceList)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent);
    }

    public void removeGeofence(PendingIntent geofencePendingIntent) {
        geofencingClient.removeGeofences(geofencePendingIntent);
    }
}
