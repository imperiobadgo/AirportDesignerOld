package com.pukekogames.airportdesigner.Objects.Vehicles;

import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.CateringTruckData;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.VehicleState;

/**
 * Created by Marko Rapka on 28.09.2016.
 */
public class CateringTruck extends StreetVehicle {

    private static final long serialVersionUID = 6702445420550998953L;

    public CateringTruck(float x, float y) {
        super(x, y);
        setImageID(Images.indexCateringTruck);
        performance = new CateringTruckData();
        driveState = VehicleState.depot;
    }

    @Override
    AirplaneServices getService() {
        return AirplaneServices.catering;
    }

    @Override
    int getServiceTime() {
        return 500 + random.nextInt(400);
    }


    @Override
    public void clicked(int mx, int my) {

    }

}
