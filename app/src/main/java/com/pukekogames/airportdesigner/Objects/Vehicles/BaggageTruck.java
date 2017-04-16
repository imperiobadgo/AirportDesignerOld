package com.pukekogames.airportdesigner.Objects.Vehicles;

import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.BaggageTruckData;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.VehicleState;

/**
 * Created by Marko Rapka on 16.04.2017.
 */
public class BaggageTruck extends StreetVehicle {

    public BaggageTruck(float x, float y) {
        super(x, y);
        setImageID(Images.indexBaggageTruck);
        performance = new BaggageTruckData();
        driveState = VehicleState.depot;
    }

    @Override
    public void clicked(int mx, int my) {

    }

    @Override
    AirplaneServices getService() {
        return AirplaneServices.baggage;
    }

    @Override
    int getServiceTime() {
        return 300 + random.nextInt(400);
    }
}
