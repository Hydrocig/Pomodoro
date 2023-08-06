package com.example.pomodoro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NotificationReceiverStop extends BroadcastReceiver {
    MainActivity mainActivity;

    public NotificationReceiverStop(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public NotificationReceiverStop(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent localIntentStop = new Intent("com.example.pomodoro.ACTION_STOP");
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntentStop);
    }
}
