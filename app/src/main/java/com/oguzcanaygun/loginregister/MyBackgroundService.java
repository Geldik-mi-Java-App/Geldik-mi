package com.oguzcanaygun.loginregister;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

public class MyBackgroundService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "MyBackgroundServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Service created");
        createNotificationChannel();
        Notification notification = showNotification();
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ArkaPlan Çalışması", "Uygulama yürütülüyor");
        startGeofencingService(intent);

        // Return START_STICKY to ensure the service restarts if it gets terminated
        return START_STICKY;
    }

    private void startGeofencingService(Intent intent) {
        Log.d(TAG, "startGeofencingService: Start geofencing service");

        String selectedAlarmName = intent.getStringExtra("name");
        double selectedAlarmLatitude = intent.getDoubleExtra("latitude", 0.0);
        double selectedAlarmLongitude = intent.getDoubleExtra("longitude", 0.0);
        float selectedAlarmRadius = intent.getFloatExtra("radius", 0.0f);

        // Create an intent for the geofencing service
        Intent geofencingIntent = new Intent(this, GeofenceBroadcastReceiver.class);
        PendingIntent geofencingPendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                geofencingIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Create a geofencing request
        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(new Geofence.Builder()
                        .setRequestId(selectedAlarmName) // Provide a unique ID for the geofence
                        .setCircularRegion(selectedAlarmLatitude, selectedAlarmLongitude, selectedAlarmRadius)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build())
                .build();

        // Get the geofencing client and add the geofences
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, geofencingPendingIntent)
                .addOnSuccessListener(aVoid -> Log.d("MyBackgroundService", "Geofences added successfully"))
                .addOnFailureListener(e -> Log.e("MyBackgroundService", "Error adding geofences: " + e.getLocalizedMessage()));
    }

    private Notification showNotification() {
        Log.d(TAG, "showNotification: Displaying notification");

        // Create an explicit intent for an Activity in your app
        Intent notificationIntent = new Intent(this, UserActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Your App is Running")
                .setContentText("Click to open the app")
                .setSmallIcon(R.drawable.baseline_emoji_transportation_24)
                .setContentIntent(pendingIntent)
                .setOngoing(true) // Notification won't be swiped away by the user
                .setPriority(NotificationCompat.PRIORITY_LOW);

        return builder.build();
    }

    private void createNotificationChannel() {
        Log.d(TAG, "createNotificationChannel: Creating notification channel");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Service destroyed");

        // Cleanup code here

        // Remove the notification when the service is stopped
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}