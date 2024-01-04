package com.oguzcanaygun.loginregister;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Start the SplashActivity when the broadcast is received
        Intent splashIntent = new Intent(context, SplashActivity.class);
        splashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(splashIntent);
    }
}