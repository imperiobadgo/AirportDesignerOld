package com.pukekogames.airportdesigner.Helper.Geometry;

import java.io.Serializable;

/**
 * Created by Marko Rapka on 28.06.2016.
 */
public class PointInt implements Serializable {

    private static final long serialVersionUID = 1991946467588358875L;
    public int x;
    public int y;

    public PointInt() {
        x = 0;
        y = 0;
    }

    public PointInt(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public PointInt(float x, float y){
        this.x = Math.round(x);
        this.y = Math.round(y);
    }

    public void set(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y){
        this.x = Math.round(x);
        this.y = Math.round(y);
    }
}
