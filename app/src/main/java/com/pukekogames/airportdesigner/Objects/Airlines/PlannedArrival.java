package com.pukekogames.airportdesigner.Objects.Airlines;

import com.pukekogames.airportdesigner.Helper.TimeStamp;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Marko Rapka on 20.11.2016.
 */
public class PlannedArrival implements Serializable {
    private static final long serialVersionUID = 3352078072540373573L;
    private static final int MAXDIFFS = 5;

//    public static final int MAXHOUROFFSET = 3;


//    private int targetHour;
//    private int hour;
    private int maxHourOffset;
    private TimeStamp plannedTime;
    private TimeStamp time;
    private String callSign;
    private Airplane airplane;
    private ArrayList<Integer> lastTurnaroundTimeDiffs;
    private int lastTurnaraoundTimeDiff;
    private boolean accepted;
    private boolean editable;

    public PlannedArrival(int hour) {
        lastTurnaroundTimeDiffs = new ArrayList<>();
        generateTimeStemp(hour);
//        this.targetHour = hour;

    }

    PlannedArrival(int hour, Airplane airplane) {
        hour %= 24;
        lastTurnaroundTimeDiffs = new ArrayList<>();
        generateTimeStemp(hour);
//        this.targetHour = hour;
//        this.hour = hour;
        setAirplane(airplane);

    }

    public PlannedArrival(int hour, int maxHourOffset, Airplane airplane) {
        hour %= 24;
        lastTurnaroundTimeDiffs = new ArrayList<>();
        generateTimeStemp(hour);
//        this.targetHour = hour;
//        this.hour = hour;
        this.maxHourOffset = maxHourOffset;
        setAirplane(airplane);
    }

    private void generateTimeStemp(int hour){
        plannedTime = new TimeStamp(0);
        plannedTime.setHour(hour);
        time = new TimeStamp(0);
        time.setHour(hour);
        editable = true;
    }


    public void setHour(int hour) {
        time.setHour(hour);
//        this.hour = hour;
    }

    public int getHour() {
        return time.getHour();
    }

    public int getTargetHour() {
        return plannedTime.getHour();
    }

    public void setAirplane(Airplane airplane) {
        this.airplane = airplane;
        this.airplane.setPlannedTime(new TimeStamp(time.getTime()));
    }

    public Airplane generateNewAirplane() {
        Airline airline = airplane.getAirline();
        Airplane newAirplane = new Airplane(airplane.getPerformance(), airline);
        newAirplane.setPlannedTime(new TimeStamp(time.getTime()));
        newAirplane.setCallSign(getCallSign());
        editable = false;
        return newAirplane;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public int getMaxHourOffset() {
        return maxHourOffset;
    }

    public void addFinishedAirplanes(Airplane airplane) {
        editable = true;
        if (lastTurnaroundTimeDiffs.size() > MAXDIFFS) {
            lastTurnaroundTimeDiffs.remove(0);
        }
        int turnaroundTimeDiff = airplane.getMaxTurnaroundTime() - airplane.getTurnaroundTime();
        lastTurnaroundTimeDiffs.add(turnaroundTimeDiff);
        lastTurnaraoundTimeDiff = turnaroundTimeDiff;
    }

//    private void updateMedian(){
//        int median = 0;
//        for (Integer lastTurnaroundTimeDiff: lastTurnaroundTimeDiffs){
//            median += lastTurnaroundTimeDiff;
//        }
//        if (lastTurnaroundTimeDiffs.size() > 0){
//            lastTurnaraoundTimeMedian = median / lastTurnaroundTimeDiffs.size();
//        }
//
//    }


    public boolean isEditable() {
        return editable;
    }

    public int getLastTurnaraoundTimeDiff() {
        return lastTurnaraoundTimeDiff;
    }

    public float[] getLastTurnAroundDiffs() {
        float[] result = new float[lastTurnaroundTimeDiffs.size()];

        for (int i = 0; i < lastTurnaroundTimeDiffs.size(); i++) {
            result[i] = lastTurnaroundTimeDiffs.get(i);

        }
        return result;
    }


        public float getPercentage() {
        int difference;
        int targetHour = plannedTime.getHour();
        int hour = time.getHour();
        if (targetHour > 18 && hour < 6) {
            difference = Math.abs(targetHour - 24 - hour);
        } else if (targetHour < 6 && hour > 18) {
            difference = Math.abs(hour - 24 - targetHour);
        } else {
            difference = Math.abs(targetHour - hour);
        }
        float percentage = 1 - ((float) difference / (float) maxHourOffset);

        return percentage;
    }

    @Override
    public String toString() {
        return callSign;
    }
}
