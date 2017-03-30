package com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData;

/**
 * Created by Marko Rapka on 02.10.2016.
 */
public class TankTruckData extends VehiclePerformance {

    private static final long serialVersionUID = 2443881812782068037L;

    public TankTruckData() {
        maxSpeed = 10f;
        acceleration = 0.2f;
        deceleration = 0.3f;
        turnRate = 2f;
        targetPointDistance = 300;
    }

}
