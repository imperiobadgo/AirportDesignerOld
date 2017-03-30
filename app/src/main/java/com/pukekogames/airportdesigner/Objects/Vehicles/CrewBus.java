package com.pukekogames.airportdesigner.Objects.Vehicles;

import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.CrewBusData;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.VehicleState;

/**
 * Created by Marko Rapka on 02.10.2016.
 */
public class CrewBus extends StreetVehicle {

    private static final long serialVersionUID = -1075121516378040030L;

    public CrewBus(float x, float y) {
        super(x, y);
        setImageID(Images.indexCrewBus);
        performance = new CrewBusData();
        driveState = VehicleState.depot;
    }

    @Override
    AirplaneServices getService() {
        return AirplaneServices.crew;
    }

    @Override
    int getServiceTime() {
        return 100 + random.nextInt(100);
    }

    @Override
    public void clicked(int mx, int my) {

    }
}
