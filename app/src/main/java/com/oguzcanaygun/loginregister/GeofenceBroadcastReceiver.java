package com.oguzcanaygun.loginregister;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "GeofenceChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final long[] VIBRATION_PATTERN = {0, 1000, 1000};

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e("GeofenceReceiver", "Error: " + geofencingEvent.getErrorCode());
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        String geofenceRequestId = geofencingEvent.getTriggeringGeofences().get(0).getRequestId();

        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                // Handle the geofence enter transition
                Log.d("GeofenceReceiver", "Entered geofence: " + geofenceRequestId);
                triggerAlarm(context, geofenceRequestId);
                break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                // Handle the geofence exit transition
                Log.d("GeofenceReceiver", "Exited geofence: " + geofenceRequestId);
                break;

            default:
                Log.e("GeofenceReceiver", "Unknown transition: " + geofenceTransition);
        }
    }

    private void triggerAlarm(Context context, String geofenceRequestId) {
        // Vibrate the phone
        vibratePhone(context);

        // Send a message to the screen
        showNotification(context, "Entered geofence: " + geofenceRequestId);

        // Play a default alarm sound
        playDefaultAlarmSound(context);
    }

    private void vibratePhone(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(VIBRATION_PATTERN, -1));
            } else {
                // Deprecated in API 26
                vibrator.vibrate(VIBRATION_PATTERN, -1);
            }
        }
    }

    private void showNotification(Context context, String message) {
        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_warning_24)
                .setContentTitle("Geofence Alarm")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Geofence Channel";
            String description = "Channel for Geofence Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void playDefaultAlarmSound(Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.nokia);
        mediaPlayer.start();
    }
}
