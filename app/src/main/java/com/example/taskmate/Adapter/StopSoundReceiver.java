package com.example.taskmate.Adapter;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

public class StopSoundReceiver extends BroadcastReceiver {
    private static final int REQUEST_CODE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Stop the sound by stopping the service
        Intent serviceIntent = new Intent(context, SoundService.class);
        context.stopService(serviceIntent);
        Log.d("GURUPRASAD","Service stop");
        // Cancel the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(REQUEST_CODE);
    }
}
