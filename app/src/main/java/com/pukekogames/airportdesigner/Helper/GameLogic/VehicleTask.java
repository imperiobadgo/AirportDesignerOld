package com.pukekogames.airportdesigner.Helper.GameLogic;

import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Roads.ParkGate;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;
import com.pukekogames.airportdesigner.Objects.Vehicles.StreetVehicle;

import java.io.Serializable;

/**
 * Created by Marko Rapka on 24.09.2016.
 */
public class VehicleTask implements Serializable {

    private static final long serialVersionUID = 95719574199622629L;
    private Airplane airplane;

    private ParkGate parkPosition;

    private StreetVehicle vehicle;

    private AirplaneServices service;


    public VehicleTask(Airplane airplane,ParkGate parkPosition) {
        this.airplane = airplane;
        this.parkPosition = parkPosition;
    }


public void setVehicle(StreetVehicle vehicle, AirplaneServices service){
    this.vehicle = vehicle;
    this.service = service;
}

    public Airplane getAirplane() {
        return airplane;
    }

    public ParkGate getParkPosition(){
        return parkPosition;
    }

    public RoadIntersection getParkIntersection(){
        return parkPosition.getNext();
    }

    public StreetVehicle getVehicle() {
        return vehicle;
    }

    public AirplaneServices getService() {
        return service;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleTask that = (VehicleTask) o;

        if (airplane != null ? !airplane.equals(that.airplane) : that.airplane != null) return false;
        if (parkPosition != null ? !parkPosition.equals(that.parkPosition) : that.parkPosition != null) return false;
        if (vehicle != null ? !vehicle.equals(that.vehicle) : that.vehicle != null) return false;
        return service == that.service;

    }

    @Override
    public int hashCode() {
        int result = airplane != null ? airplane.hashCode() : 0;
        result = 31 * result + (parkPosition != null ? parkPosition.hashCode() : 0);
        result = 31 * result + (vehicle != null ? vehicle.hashCode() : 0);
        result = 31 * result + (service != null ? service.hashCode() : 0);
        return result;
    }
}
