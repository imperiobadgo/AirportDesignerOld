package com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData;

import java.io.Serializable;

/**
 * Created by Marko Rapka on 12.06.2016.
 */
public class VehiclePerformance implements Serializable {
    private static final long serialVersionUID = 8138948079732938119L;
    public float maxSpeed;
    public float acceleration;
    public float deceleration;
    public float turnRate;
    public int targetPointDistance;
}
