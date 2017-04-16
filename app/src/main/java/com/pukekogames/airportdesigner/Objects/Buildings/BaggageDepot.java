package com.pukekogames.airportdesigner.Objects.Buildings;

import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.Prices;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Vehicles.BaggageTruck;
import com.pukekogames.airportdesigner.Objects.Vehicles.StreetVehicle;

/**
 * Created by Marko Rapka on 16.04.2017.
 */
public class BaggageDepot extends Depot {
    public BaggageDepot(Road road) {
        super(road);
        setImageID(Images.indexBaggageDepot);
    }

    @Override
    public long getStartCosts() {
        return Prices.BaggagatruckStart;
    }

    @Override
    StreetVehicle createVehicle() {
        return new BaggageTruck(Align_X, Align_Y);
    }

    @Override
    public AirplaneServices getService() {
        return AirplaneServices.baggage;
    }
}
