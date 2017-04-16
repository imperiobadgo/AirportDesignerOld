package com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData;

/**
 * Created by Marko Rapka on 16.04.2017.
 */
public class BaggageTruckData extends VehiclePerformance {

    public BaggageTruckData() {
        maxSpeed = 9f;
        acceleration = 0.2f;
        deceleration = 0.3f;
        turnRate = 2f;
        targetPointDistance = 300;
    }
}
