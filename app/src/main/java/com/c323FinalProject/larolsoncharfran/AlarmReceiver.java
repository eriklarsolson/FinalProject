package com.c323FinalProject.larolsoncharfran;

import android.content.*;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent background = new Intent(context, LocationService.class);
        context.startService(background);

        System.out.println("LAT: " + background.getStringExtra("LAT"));
        System.out.println("LNG: " + background.getStringExtra("LNG"));
    }

}