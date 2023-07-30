package com.example.pomodoro;

public class SimpleCalc {
    public int milToSec(int number){
        int result = number / 1000;
        return result;
    }

    public int milToSec(long number){
        long result = number / 1000;
        int rresult = (int)result;
        return rresult;
    }

    public String intToString(int input){
        String result = String.valueOf(input);
        return result;
    }

    public int StringToInt(String input){
        int result = Integer.valueOf(input);
        return result;
    }

    public long longToInt(long input){
        int result = (int)input;
        return result;
    }
}
