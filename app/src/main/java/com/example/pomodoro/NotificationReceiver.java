package com.example.pomodoro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NotificationReceiver extends BroadcastReceiver {
    MainActivity mainActivity;

    public NotificationReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public NotificationReceiver(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent localIntent = new Intent("com.example.pomodoro.ACTION_PAUSE");
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }
}
