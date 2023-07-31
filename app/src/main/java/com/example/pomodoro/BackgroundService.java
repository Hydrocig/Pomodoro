package com.example.pomodoro;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;

public class BackgroundService extends Service {

    Notification notification;
    Timer timer;
    String notificationText = "";
    private static final int NOTIFICATION_ID = 1;

    NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        timer = new Timer();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notification = createNotification();
        startForeground(1, notification);

        // Check if the intent has the correct action
        if (intent != null && Timer.ACTION_UPDATE_NOTIFICATION.equals(intent.getAction())) {
            String notificationText = intent.getStringExtra("notificationText");

            int min = (Integer.parseInt(notificationText)/(1000*60));
            int sec = (((int)Integer.parseInt(notificationText)/1000)-min*60);

            setTitle(Long.toString(min)+":"+Long.toString(sec));
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        stopForeground(true);
        super.onDestroy();
    }

    private Notification createNotification() {
        String channelId = "Pomodoro";
        timer = new Timer();


        String contentText;
        if (notificationText.isEmpty()) {
            contentText = "Currently no timer running";
        } else {
            contentText = notificationText;
        }

        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, channelId)
                    .setContentTitle("Running in Background")
                    .setContentText(contentText)
                    .setColor(Color.parseColor("#FB5607"))
                    .setSmallIcon(R.drawable.ic_stat_accessibility);
        }else {
            builder = new Notification.Builder(this)
                    .setContentTitle("Running in Background")
                    .setContentText(contentText)
                    .setColor(Color.parseColor("#FB5607"))
                    .setSmallIcon(R.drawable.ic_stat_accessibility);
        }
        return builder.build();
    }

    private void showNotification(Notification notification) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void setTitle(String title){
        this.notificationText = title;
        updateNotification();
    }

    public String getTitle(){
        return notificationText;
    }

    private void updateNotification() {
        Notification notification = createNotification();
        showNotification(notification);
    }
}
