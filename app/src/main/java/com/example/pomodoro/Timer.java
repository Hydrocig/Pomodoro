package com.example.pomodoro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Timer {
    int counter = 0;
    boolean canceled;

    int milliLeft;
    float milliLeftLong;
    int min;
    int sec;

    int smallBrakeMin;
    int bigBrakeMin;
    int workTimeMin;

    public static final String ACTION_UPDATE_NOTIFICATION = "com.example.pomodoro.UPDATE_NOTIFICATION";

    BackgroundService backgroundService;

    private TextView timerTextField;
    private Button startTimerButton;
    private Button pauseTimerButton;
    private ProgressBar progressBar;

    public Timer(TextView timerTextField, Button startTimerButton, Button pauseTimerButton, ProgressBar progressBar) {
        this.timerTextField = timerTextField;
        this.startTimerButton = startTimerButton;
        this.pauseTimerButton = pauseTimerButton;
        this.progressBar = progressBar;
    }

    public Timer(int smallBrakeMin, int bigBrakeMin, int workTimeMin){
        this.smallBrakeMin = smallBrakeMin;
        this.bigBrakeMin  = bigBrakeMin;
        this.workTimeMin = workTimeMin;
    }

    public Timer(){

    }

    int[] orderArray = {workTimeMin, smallBrakeMin, workTimeMin, smallBrakeMin, workTimeMin, smallBrakeMin, workTimeMin, bigBrakeMin};

    //Declare Timer
    CountDownTimer cTimer;

    //start timer function
    void startTimer(float duration){
        backgroundService = new BackgroundService();
        cTimer = new CountDownTimer((long)(duration * 60000), 1) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                canceled = false;
                setCanceled(false);
                //backgroundService.setCanceledBackground(false);
                milliLeftLong = millisUntilFinished;
                milliLeft = (int)millisUntilFinished;
                min = ((int)millisUntilFinished/(1000*60));
                sec = (((int)millisUntilFinished/1000)-min*60)%60;
                String secondsString = String.format(Locale.US, "%02d", sec);
                timerTextField.setText(Long.toString(min)+":"+secondsString);

                //Send an intent to the BackgroundService with the updated notification text
                Intent intent = new Intent(timerTextField.getContext(), BackgroundService.class);
                intent.setAction(Timer.ACTION_UPDATE_NOTIFICATION);
                intent.putExtra("notificationText", Long.toString(millisUntilFinished));
                timerTextField.getContext().startService(intent);

                //Updating Progress Bar
                if(progressBar.getVisibility() == View.INVISIBLE){
                    progressBar.setVisibility(View.VISIBLE);
                }

                progressBar.setProgress((int) (1000 * (duration * 60000 - millisUntilFinished) / (duration * 60000)));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                canceled = true;
                startTimerButton.setVisibility(View.VISIBLE);
                pauseTimerButton.setVisibility(View.INVISIBLE);
                counter = counter + 1;
                timerTextField.setText(orderArray[counter]+":00");
                progressBar.setVisibility(View.INVISIBLE);
            }
        };
        cTimer.start();
    }

    //cancel timer
    public void pauseTimer(){
        if(cTimer != null){
            cTimer.cancel();
        }
    }

    public void skipTimer(){
        try {
            cTimer.cancel();
            canceled = true;
        }catch (NullPointerException e){
            canceled = true;
        }
    }

    public void timerResume(){
        try {
            startTimer(milliLeftLong/60000);
        }catch (ArithmeticException ignored){
            ;
        }
    }

    public void stopTimer(){
        try {
            cTimer.cancel();
            canceled = true;
            counter = 0;
            progressBar.setProgress(0);
            progressBar.setVisibility(View.INVISIBLE);
        }catch (NullPointerException e){
            canceled = true;
        }
    }

    public void setCanceled(boolean input){
        this.canceled = input;
    }

    public boolean getCanceled(){
        System.out.println(canceled);
        return canceled;
    }

    public float getMilliLeft(){
        return milliLeftLong;
    }

    public float getMilliLeftLong(){
        return milliLeftLong/60000;
    }

    public int getCounter(){
        return counter;
    }

    public void setCounter(int counter){
        this.counter = counter;
    }

    public void setWorkTimeMin(int input){
        this.workTimeMin = input;
    }

    public void setSmallBrakeMin(int input){
        this.smallBrakeMin = input;
    }

    public void setBigBrakeMin(int input){
        this.bigBrakeMin = input;
    }

    public void updateOrderArray(){
        orderArray[1] = smallBrakeMin;
        orderArray[3] = smallBrakeMin;
        orderArray[5] = smallBrakeMin;

        orderArray[0] = workTimeMin;
        orderArray[2] = workTimeMin;
        orderArray[4] = workTimeMin;
        orderArray[6] = workTimeMin;

        orderArray[7] = bigBrakeMin;
    }
}
