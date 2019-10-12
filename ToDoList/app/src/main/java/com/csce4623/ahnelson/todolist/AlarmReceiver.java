package com.csce4623.ahnelson.todolist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager NM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationChannel NC = new NotificationChannel("my_channel_01","TEST", NotificationManager.IMPORTANCE_DEFAULT);
        NC.setDescription("Test");
        NC.enableLights(true);
        NC.setLightColor(Color.RED);
        NC.enableVibration(true);
        NM.createNotificationChannel(NC);

        Intent reminderIntent = new Intent(context, HomeActivity.class);
        int noteId = intent.getIntExtra("noteID", -1);
        String noteTitle = intent.getStringExtra("noteTitle");
        reminderIntent.putExtra("noteID", noteId);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(context, "my_channel_01")
                .setContentTitle("To-Do Reminder")
                .setContentText(noteTitle + " is due now!")
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .build();

        NM.notify(0,notification);
    }
}
