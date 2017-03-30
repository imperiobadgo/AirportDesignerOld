package com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData;

/**
 * Created by Marko Rapka on 12.06.2016.
 */
public class BusData extends VehiclePerformance {

    private static final long serialVersionUID = 4807229972137095606L;

    public BusData() {
        maxSpeed = 10f;
        acceleration = 0.2f;
        deceleration = 0.3f;
        turnRate = 2f;
        targetPointDistance = 300;
    }
}
