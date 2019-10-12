package com.csce4623.ahnelson.todolist;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Calendar;
import static android.content.Context.ALARM_SERVICE;

public class NotificationScheduler
{
    public void scheduleNotification(Context context, long futureMS, String title, int noteID){
        if(futureMS > SystemClock.elapsedRealtime()){
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Log.i("Schedule", "Scheduling an alarm");
            Log.i("Schedule", "Note id: "+noteID+ " Note Title: "+ title);
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("noteID", noteID);
            intent.putExtra("noteTitle", title);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, noteID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureMS, alarmIntent);
        }

    }

}