package com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData;

/**
 * Created by Marko Rapka on 02.10.2016.
 */
public class CrewBusData extends VehiclePerformance {

    private static final long serialVersionUID = -5434764889605439707L;

    public CrewBusData() {
        maxSpeed = 10f;
        acceleration = 0.2f;
        deceleration = 0.3f;
        turnRate = 2f;
        targetPointDistance = 300;
    }
}
