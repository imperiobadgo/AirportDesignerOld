package com.pukekogames.airportdesigner.Helper;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;

import java.io.Serializable;

/**
 * Created by Marko Rapka on 13.03.2017.
 */
public class TimeStamp implements Serializable {
    private static final long serialVersionUID = 201484954470453184L;

    private int time;
    private float oneHour;

    public TimeStamp(int time){
        this.time = time;
        oneHour = (GameInstance.Settings().maxTime / 24f);
    }

    public int getMinute(){
        if (time < 0) return GameInstance.Instance().getMinute();
        return  (int) (((time % oneHour) / oneHour) * 60);
    }

    public int getHour(){
        if (time < 0) return GameInstance.Instance().getHour();
        return (int) (time / oneHour);
    }

    public void setHour(int hour){
        hour %= 24;
        time = Math.round(oneHour * hour);
    }

    public void setTime(int time){
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void increase(){
        time += 1;
        if (time > GameInstance.Settings().maxTime) time = 0;
    }

    public int getMinuteDifference(TimeStamp otherTime){
        int timeDiff = time - otherTime.getTime();

        return (int) ((timeDiff / oneHour) * 60);
    }
}
