package com.example.pomodoro;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Content from layout
        TextView timerTextField = findViewById(R.id.mainTimer);
        Button startTimerButton = findViewById(R.id.startButton);
        Button stopTimerButton = findViewById(R.id.stopButton);
        Button pauseTimerButton = findViewById(R.id.pauseButton);
        Button resumeTimerButton = findViewById(R.id.resumeButton);
        long test = 1;

        //Object initialization
        SimpleCalc simpleCalc = new SimpleCalc();
        Timer timer = new Timer(timerTextField, startTimerButton, pauseTimerButton);

        timerTextField.setText(simpleCalc.intToString(timer.getWorkTimeMin()));

        //Create Timer on Button click
        startTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimerButton.setVisibility(View.INVISIBLE);
                pauseTimerButton.setVisibility(View.VISIBLE);
                timer.startTimer(timer.orderArray[timer.getCounter()%timer.orderArray.length],1);
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
    }
}