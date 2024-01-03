package com.oguzcanaygun.loginregister;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.location.LocationListener;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyBackgroundService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "MyBackgroundServiceChannel";
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; // Minimum time between location updates (1 minute)
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10.0f; // Minimum distance between location updates (10 meters)

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyBackgroundService", "Service started");
        showNotification();

        // Start location tracking
        startLocationUpdates();

        // Return START_STICKY to ensure the service restarts if it gets terminated
        return START_STICKY;
    }

    private void showNotification() {
        // Create an explicit intent for an Activity in your app
        Intent notificationIntent = new Intent(this, UserActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Your App is Running")
                .setContentText("Click to open the app")
                .setSmallIcon(R.drawable.baseline_emoji_transportation_24)
                .setContentIntent(pendingIntent)
                .setOngoing(true) // Notification won't be swiped away by the user
                .setPriority(NotificationCompat.PRIORITY_LOW);

        // Show the notification and start the service in the foreground
        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "MyBackgroundServiceChannel";
            String description = "Channel for MyBackgroundService";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void startLocationUpdates() {
        // Initialize location manager and listener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Handle location updates here
                Log.d("MyBackgroundService", "Location updated: " + location.getLatitude() + ", " + location.getLongitude());

                // Update notification with latitude and longitude
                updateNotification("Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Handle status changes if needed
            }

            @Override
            public void onProviderEnabled(String provider) {
                // Handle provider enabled if needed
            }

            @Override
            public void onProviderDisabled(String provider) {
                // Handle provider disabled if needed
            }
        };

        try {
            // Request location updates
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    locationListener
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void updateNotification(String contentText) {
        // Update the existing notification with latitude and longitude information
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Your App is Running")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.baseline_emoji_transportation_24)
                .setOngoing(true) // Notification won't be swiped away by the user
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup code here

        // Remove the notification when the service is stopped
        stopForeground(true);

        // Stop location updates
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}