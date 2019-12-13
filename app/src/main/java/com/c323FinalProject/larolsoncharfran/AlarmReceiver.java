package com.c323FinalProject.larolsoncharfran;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    private final String ALARM_BUNDLE = "ALARM_BUNDLE";
    int indexOfReceiver;

    // this constructor is called by the alarm manager.
    public AlarmReceiver(){ }

    // you can use this constructor to create the alarm.
    //  Just pass in the main activity as the context,
    //  any extras you'd like to get later when triggered
    //  and the timeout
    public AlarmReceiver(Context context, Bundle extras, int timeoutInSeconds){
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ALARM_BUNDLE, extras);

        long time = extras.getLong("TIME");
        indexOfReceiver = extras.getInt("INDEX");

        int id = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        LoginActivity.pendingIntents.add(pendingIntent);

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // here you can get the extras you passed in when creating the alarm
        //intent.getBundleExtra(REMINDER_BUNDLE));

        Toast.makeText(context, "Alarm went off!", Toast.LENGTH_SHORT).show();
        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.putExtra(ALARM_BUNDLE, intent.getBundleExtra(ALARM_BUNDLE));
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);

        System.out.println("1: " + LoginActivity.broadcastReceivers);
        LoginActivity.broadcastReceivers.remove(indexOfReceiver);
        System.out.println("2: " + LoginActivity.broadcastReceivers);
    }
}