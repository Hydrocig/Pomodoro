package com.example.pomodoro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
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

    //Object initialization
    int smallBrakeMin;
    int bigBrakeMin;
    int workTimeMin;


    SimpleCalc simpleCalc = new SimpleCalc();

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

    int[] orderArray = {workTimeMin, smallBrakeMin, workTimeMin, smallBrakeMin, workTimeMin, smallBrakeMin, workTimeMin, bigBrakeMin};

    //Declare Timer
    CountDownTimer cTimer;

    //start timer function
    void startTimer(float duration){
        cTimer = new CountDownTimer((long)(duration * 60000), 1) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                milliLeftLong = millisUntilFinished;
                milliLeft = (int)millisUntilFinished;
                min = ((int)millisUntilFinished/(1000*60));
                sec = (((int)millisUntilFinished/1000)-min*60);
                timerTextField.setText(Long.toString(min)+":"+Long.toString(sec));
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
        cTimer.cancel();
        counter = 0;
    }

    public int getCounter(){
        return counter;
    }

    public void setCounter(int counter){
        this.counter = counter;
    }

    public int getWorkTimeMin(){
        return workTimeMin;
    }

    public void setWorkTimeMin(int input){
        this.workTimeMin = input;
    }

    public int getSmallBrakeMin(){
        return smallBrakeMin;
    }

    public void setSmallBrakeMin(int input){
        this.smallBrakeMin = input;
    }

    public int getBigBrakeMin(){
        return bigBrakeMin;
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
