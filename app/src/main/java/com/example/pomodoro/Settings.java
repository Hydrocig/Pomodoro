package com.example.pomodoro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {
    ImageButton backToMainButton;
    Button saveButton;
    EditText smallPauseEdit;
    EditText bigPauseEdit;
    EditText workTimeEdit;
    Switch backgroundSwitch;

    BackgroundService backgroundService;

    SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        //Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        //UI-Elements
        backToMainButton = findViewById(R.id.backToMainButton);
        saveButton = findViewById(R.id.saveButton);
        smallPauseEdit = findViewById(R.id.smallPauseEdit);
        bigPauseEdit = findViewById(R.id.bigPauseEdit);
        workTimeEdit = findViewById(R.id.workTimeEdit);
        backgroundSwitch = findViewById(R.id.backgroundSwitch);

        MainActivity main = new MainActivity();

        backToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeToSharedPreferences(getSmallPauseEditText(), getBigPauseEditText(), getWorkTimeEditText());
            }
        });

        backgroundSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!backgroundSwitch.isChecked()){
                    backgroundService.onDestroy();
                }
            }
        });
    }

    public int getSmallPauseEditText() {
        try {
            String result = smallPauseEdit.getText().toString();
            return Integer.parseInt(result);
        }catch (NumberFormatException | NullPointerException e){
            return 5;
        }
    }

    public int getBigPauseEditText() {
        try {
            String result = bigPauseEdit.getText().toString();
            return Integer.parseInt(result);
        }catch (NumberFormatException | NullPointerException e){
            return 20;
        }
    }

    public int getWorkTimeEditText() {
        try {
            String result = workTimeEdit.getText().toString();
            return Integer.parseInt(result);
        }catch (NumberFormatException | NullPointerException e){
            return 25;
        }
    }

    private void writeToSharedPreferences(int small, int big, int work){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("smallBreak", small);
        editor.putInt("bigBreak", big);
        editor.putInt("workTime", work);
        editor.apply();
    }
}
