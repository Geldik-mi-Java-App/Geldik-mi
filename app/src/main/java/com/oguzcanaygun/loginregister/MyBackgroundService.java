package com.oguzcanaygun.loginregister;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;

public class MyBackgroundService extends Service {

    private static final int NOTIFICATION_ID = 1;
    public static final String CHANNEL_ID = "MyBackgroundServiceChannel";
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        // Register the BroadcastReceiver
        registerReceiver(notificationActionReceiver, new IntentFilter("OPEN_APP_ACTION"));

    }

    @SuppressLint("NotificationTrampoline")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyBackgroundService", "Service started");

        if (intent != null && "OPEN_APP_ACTION".equals(intent.getAction())) {
            // Handle the custom action (e.g., open UserActivity)
            Intent openAppIntent = new Intent(this, UserActivity.class);
            openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.startActivity(openAppIntent);
        } else {
            showNotification();
            // Start location tracking
            startLocationUpdates();

        }

        // Return START_STICKY to ensure the service restarts if it gets terminated
        return START_STICKY;
    }
    private BroadcastReceiver notificationActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("OPEN_APP_ACTION")) {
                // Handle the custom action (e.g., open UserActivity)
                Intent openAppIntent = new Intent(context, UserActivity.class);
                openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(openAppIntent);
            }
        }
    };

    private void showNotification() {
        // Create an explicit intent for an Activity in your app
        Intent notificationIntent = new Intent(this, UserActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Create a PendingIntent for the notification action
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Create an intent for the custom action (e.g., open the app)
        Intent openAppIntent = new Intent(this, UserActivity.class);
        openAppIntent.setAction("OPEN_APP_ACTION");  // Set the action for identification
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent openAppPendingIntent = PendingIntent.getActivity(
                this,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Alarmınız aktif durumda")
                .setContentText("Uygulamayı açmak için tıklayınız")
                .setSmallIcon(R.drawable.baseline_emoji_transportation_24)
                .setContentIntent(contentIntent)
                .setOngoing(true) // Notification won't be swiped away by the user
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .addAction(R.drawable.baseline_emoji_transportation_24, "Open App", openAppPendingIntent);

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
                    3000,
                    100,
                    locationListener
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void updateNotification(String contentText) {
        // Generate a unique notification ID based on current time
        int updatedNotificationId = (int) System.currentTimeMillis();

        // Update the existing notification with latitude and longitude information
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Your App is Running")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.baseline_emoji_transportation_24)
                .setOngoing(true) // Notification won't be swiped away by the user
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(updatedNotificationId, builder.build());
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister the BroadcastReceiver
        unregisterReceiver(notificationActionReceiver);

        // Cleanup code here

        // Remove the notification when the service is stopped
        stopForeground(true);

        // Stop location updates
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}