package com.example.pomodoro;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Timer timer;
    SharedPreferences sharedPreferences;
    NotificationManager notificationManager;
    AudioAttributes audioAttributes;
    TextView timerTextField;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Content from layout
        timerTextField = findViewById(R.id.mainTimer);
        Button startTimerButton = findViewById(R.id.startButton);
        Button stopTimerButton = findViewById(R.id.stopButton);
        Button pauseTimerButton = findViewById(R.id.pauseButton);
        Button resumeTimerButton = findViewById(R.id.resumeButton);
        ImageButton skipTimerButton = findViewById(R.id.skipButton);
        ImageButton settingsButton = findViewById(R.id.settingsButton);

        //Object initialization
        SimpleCalc simpleCalc = new SimpleCalc();
        timer = new Timer(timerTextField, startTimerButton, pauseTimerButton);
        timer.updateOrderArray();
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        timerTextField.setText(simpleCalc.intToString(sharedPreferences.getInt("workTime", 25))+":00");

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
            }
        });

        pauseTimerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                timer.pauseTimer();
                pauseTimerButton.setVisibility(View.INVISIBLE);
                resumeTimerButton.setVisibility(View.VISIBLE);
            }
        });

        resumeTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.timerResume();
                pauseTimerButton.setVisibility(View.VISIBLE);
                resumeTimerButton.setVisibility(View.INVISIBLE);
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
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                switchActivities();
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

            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel(channelID, channelName, importance);
            channel.setDescription(channelDescription);
            channel.setSound(null, audioAttributes);

            notificationManager.createNotificationChannel(channel);
        }
    }
}