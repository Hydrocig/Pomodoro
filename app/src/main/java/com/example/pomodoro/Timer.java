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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Timer {
    int counter = 0;

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

    public Timer(TextView timerTextField, Button startTimerButton, Button pauseTimerButton) {
        this.timerTextField = timerTextField;
        this.startTimerButton = startTimerButton;
        this.pauseTimerButton = pauseTimerButton;
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
                milliLeftLong = millisUntilFinished;
                milliLeft = (int)millisUntilFinished;
                min = ((int)millisUntilFinished/(1000*60));
                sec = (((int)millisUntilFinished/1000)-min*60);
                timerTextField.setText(Long.toString(min)+":"+Long.toString(sec));
                //backgroundService.setTitle(Long.toString(millisUntilFinished));

                // Send an intent to the BackgroundService with the updated notification text
                Intent intent = new Intent(timerTextField.getContext(), BackgroundService.class);
                intent.setAction(Timer.ACTION_UPDATE_NOTIFICATION);
                intent.putExtra("notificationText", Long.toString(millisUntilFinished));
                timerTextField.getContext().startService(intent);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                //cancelTimer();
                startTimerButton.setVisibility(View.VISIBLE);
                pauseTimerButton.setVisibility(View.INVISIBLE);
                counter = counter + 1;
                timerTextField.setText(orderArray[counter]+":00");
            }
        };
        cTimer.start();
    }

    //cancel timer
    public void pauseTimer(){
        cTimer.cancel();
    }

    public void skipTimer(){
        try {
            cTimer.cancel();
        }catch (NullPointerException e){
            ;
        }
    }

    public void timerResume(){
        startTimer(milliLeftLong/60000);
    }

    public void stopTimer(){
        try {
            cTimer.cancel();
            counter = 0;
        }catch (NullPointerException e){
            ;
        }
    }

    public float getMilliLeft(){
        return milliLeftLong;
    }

    public float getMilliLeftLong(){
        return milliLeftLong/60000;
    }

    public void setMilliLeftLong(float milliLeftLong){
        this.milliLeftLong = milliLeftLong;
    }

    public CharSequence getMilliLeftLongFormat(){
        int min = (int) (milliLeftLong / 60000);
        int sec = (int) ((milliLeftLong / 1000) % 60);
        return String.format("%02d:%02d", min, sec);
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
