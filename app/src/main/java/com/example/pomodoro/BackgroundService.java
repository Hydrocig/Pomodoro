package com.example.pomodoro;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class BackgroundService extends Service {

    Notification notification;
    Timer timer;
    MainActivity mainActivity;
    String notificationText = "";
    private static final int NOTIFICATION_ID = 1;

    NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    public BackgroundService(){

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

        String contentText;
        if (notificationText.isEmpty() || timer.getCanceled()) {
            System.out.println(timer.getCanceled());
            contentText = "Currently no timer running";
        } else {
            contentText = notificationText;
        }

        NotificationCompat.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle("Running in Background")
                    .setContentText(contentText)
                    .setColor(Color.parseColor("#FB5607"))
                    .setSmallIcon(R.drawable.pomodorotimerblack);
        }else {
            builder = new NotificationCompat.Builder(this)
                    .setContentTitle("Running in Background")
                    .setContentText(contentText)
                    .setColor(Color.parseColor("#FB5607"))
                    .setSmallIcon(R.drawable.pomodorotimerblack);
        }

        if(false){
            try {
                Intent intent = new Intent(this, NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                Intent intentStop = new Intent(this, NotificationReceiverStop.class);
                PendingIntent pendingIntentStop = PendingIntent.getBroadcast(this, 124, intentStop, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                // Create the actions and add it to the notification
                NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                        R.drawable.pomodorotimerblack, "Pause Timer", pendingIntent
                ).build();

                NotificationCompat.Action actionStop = new NotificationCompat.Action.Builder(
                        R.drawable.pomodorotimerblack, "Stop Timer", pendingIntentStop
                ).build();

                builder.addAction(action);
                builder.addAction(actionStop);
            }catch (NullPointerException ignored){
                ;
            }
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

    public void updateNotification() {
        Notification notification = createNotification();
        showNotification(notification);
    }
}
