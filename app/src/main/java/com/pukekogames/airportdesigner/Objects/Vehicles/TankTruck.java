package com.pukekogames.airportdesigner.Objects.Vehicles;

import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.TankTruckData;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.VehicleState;

/**
 * Created by Marko Rapka on 02.10.2016.
 */
public class TankTruck extends StreetVehicle {

    private static final long serialVersionUID = 8522404617285484225L;

    public TankTruck(float x, float y) {
        super(x, y);
        setImageID(Images.indexTankTruck);
        performance = new TankTruckData();
        driveState = VehicleState.depot;
    }

    @Override
    AirplaneServices getService() {
        return AirplaneServices.tank;
    }

    @Override
    int getServiceTime() {
        return 400 + random.nextInt(100);
    }

    @Override
    public void clicked(int mx, int my) {

    }
}
