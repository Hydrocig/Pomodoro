package com.example.pomodoro;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Timer extends MainActivity {

    int counter = 0;
    boolean paused = false;

    int milliLeft;
    int min;
    int sec;


    //Object initialization
    SimpleCalc simpleCalc = new SimpleCalc();

    private TextView timerTextField;
    private Button startTimerButton;
    private Button pauseTimerButton;

    public Timer(TextView timerTextField, Button startTimerButton, Button pauseTimerButton) {
        this.timerTextField = timerTextField;
        this.startTimerButton = startTimerButton;
        this.pauseTimerButton = pauseTimerButton;
    }

    //Declare Timer
    CountDownTimer cTimer = null;

    //start timer function
    void startTimer(long duration, long interval){
        cTimer = new CountDownTimer(duration * 1000, interval) {//60000
            @Override
            public void onTick(long millisUntilFinished) {
                milliLeft =(int)millisUntilFinished;
                min = ((int)millisUntilFinished/(1000*60));
                sec = (((int)millisUntilFinished/1000)-min*60);
                timerTextField.setText(Long.toString(min)+":"+Long.toString(sec));
            }

            @Override
            public void onFinish() {
                timerTextField.setText("Pause");
                cancelTimer();
                startTimerButton.setVisibility(View.VISIBLE);
                pauseTimerButton.setVisibility(View.INVISIBLE);
                counter = counter + 1;
            }
        };
        cTimer.start();

    }

    //cancel timer
    public void cancelTimer(){
        cTimer.cancel();
    }

    public void pauseTimer(){
        paused = true;
        cTimer.cancel();
    }

    public void timerResume(){
        paused = false;
        startTimer(milliLeft,1);
    }

    public int getCounter(){
        return counter;
    }

    public void setCounter(int counter){
        this.counter = counter;
    }
}
