package com.example.pomodoro;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Timer timer;
    SharedPreferences sharedPreferences;
    NotificationManager notificationManager;
    NotificationReceiver notificationReceiver;
    NotificationReceiverStop notificationReceiverStop;
    MediaPlayer mediaPlayer;
    TextView timerTextField;
    Button pauseTimerButton;
    Button resumeTimerButton;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Content from layout
        timerTextField = findViewById(R.id.mainTimer);
        Button startTimerButton = findViewById(R.id.startButton);
        Button stopTimerButton = findViewById(R.id.stopButton);
        pauseTimerButton = findViewById(R.id.pauseButton);
        resumeTimerButton = findViewById(R.id.resumeButton);
        ImageButton skipTimerButton = findViewById(R.id.skipButton);
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        //Object initialization
        SimpleCalc simpleCalc = new SimpleCalc();
        timer = new Timer(timerTextField, startTimerButton, pauseTimerButton, progressBar);
        timer.updateOrderArray();
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        timerTextField.setText(simpleCalc.intToString(sharedPreferences.getInt("workTime", 25))+":00");
        mediaPlayer = MediaPlayer.create(this, R.raw.button_click);

        notificationReceiver = new NotificationReceiver(this);
        notificationReceiverStop = new NotificationReceiverStop(this);

        // Register the local broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                //System.out.println(timer.getCanceled());
                pauseTimerButton.performClick();
            }
        }, new IntentFilter("com.example.pomodoro.ACTION_PAUSE"));

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                stopTimerButton.performClick();
            }
        }, new IntentFilter("com.example.pomodoro.ACTION_STOP"));

        //Request Notification Permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            System.out.println("Inside");
            requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
        }

        //calling Notification Channel
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();

        //Starting BackgroundService
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        serviceIntent.putExtra("MilliLeftLong", timer.getMilliLeftLong());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(serviceIntent);
        }else {
            startService(serviceIntent);
        }

        //Create Timer on Button click
        startTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimerButton.setVisibility(View.INVISIBLE);
                pauseTimerButton.setVisibility(View.VISIBLE);
                timer.updateOrderArray();
                timer.startTimer(timer.orderArray[timer.getCounter()%timer.orderArray.length]);

                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        });

        pauseTimerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                timer.pauseTimer();
                pauseTimerButton.setVisibility(View.INVISIBLE);
                resumeTimerButton.setVisibility(View.VISIBLE);

                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        });

        resumeTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.timerResume();
                pauseTimerButton.setVisibility(View.VISIBLE);
                resumeTimerButton.setVisibility(View.INVISIBLE);

                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        });

        stopTimerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                timer.stopTimer();
                timerTextField.setText(timer.orderArray[0]+":00");
                pauseTimerButton.setVisibility(View.INVISIBLE);
                resumeTimerButton.setVisibility(View.INVISIBLE);
                startTimerButton.setVisibility(View.VISIBLE);

                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        });

        skipTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.skipTimer();

                startTimerButton.setVisibility(View.VISIBLE);
                pauseTimerButton.setVisibility(View.INVISIBLE);
                resumeTimerButton.setVisibility(View.INVISIBLE);

                timer.setCounter(timer.getCounter()+1);

                timerTextField.setText(timer.orderArray[timer.getCounter()%timer.orderArray.length]+":00");

                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                switchActivities();

                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();

        int smallBreakSetting = sharedPreferences.getInt("smallBreak", 5);
        int bigBreakSetting = sharedPreferences.getInt("bigBreak", 20);
        int workTimeSetting = sharedPreferences.getInt("workTime", 25);
        timer.setSmallBrakeMin(smallBreakSetting);
        timer.setBigBrakeMin(bigBreakSetting);
        timer.setWorkTimeMin(workTimeSetting);
        timer.updateOrderArray();
    }

    public MainActivity(){

    }

    @Override
    public void onPause(){
        super.onPause();

    }

    private void switchActivities(){
        Intent switchActivityIntend = new Intent(this, Settings.class);
        startActivity(switchActivityIntend);
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelID = "Pomodoro";
            String channelName = "Pomodoro Timer";
            String channelDescription = "Pomodoro Timer";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel(channelID, channelName, importance);
            channel.setDescription(channelDescription);
            channel.setSound(null, audioAttributes);
            channel.enableVibration(false);

            notificationManager.createNotificationChannel(channel);
        }
    }
}