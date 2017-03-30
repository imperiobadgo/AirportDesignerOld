package com.pukekogames.airportdesigner.Helper.Geometry;

import java.io.Serializable;

/**
 * Created by Marko Rapka on 28.06.2016.
 */
public class PointFloat implements Serializable {

    private static final long serialVersionUID = -8610696439083448802L;
    public float x;
    public float y;

    public PointFloat() {
        x = 0f;
        y = 0f;
    }

    public PointFloat(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }
}
