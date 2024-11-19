package com.oguzcanaygun.loginregister;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class EntryScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        playAlarmSound(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(EntryScreenActivity.this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        EntryScreenActivity.this,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE // Flag'i buraya ekledik
                );
                startActivity(intent);
                finish();
            }
        }, 4000);

    }
    private void playAlarmSound(Context context) {
        // Play a notification sound
        int soundResourceId = R.raw.intro;
        Ringtone ringtone = RingtoneManager.getRingtone(context, Uri.parse("android.resource://" + context.getPackageName() + "/" + soundResourceId));
        ringtone.play();
    }
}