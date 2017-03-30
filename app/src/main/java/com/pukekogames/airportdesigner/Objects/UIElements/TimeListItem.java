package com.pukekogames.airportdesigner.Objects.UIElements;

import java.io.Serializable;

/**
 * Created by Marko Rapka on 27.11.2016.
 */
public class TimeListItem implements Serializable {
    private static final long serialVersionUID = -5821199837045080440L;

    private int hour;
    private int count;
    private Object object;
    private boolean accepted;

    public TimeListItem(int hour,Object object, boolean accepted){
        this.hour = hour;
        this.object = object;
        this.accepted = accepted;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
