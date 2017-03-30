package com.pukekogames.airportdesigner.Objects.Buildings;

import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.Prices;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Vehicles.Bus;
import com.pukekogames.airportdesigner.Objects.Vehicles.StreetVehicle;

/**
 * Created by Marko Rapka on 24.09.2016.
 */
public class BusDepot extends Depot {

    private static final long serialVersionUID = -2978486852094801782L;

    public BusDepot(Road road) {
        super(road);
        setImageID(Images.indexBusDepot);
    }

    @Override
    public long getStartCosts() {
        return Prices.BusStart;
    }

    @Override
    StreetVehicle createVehicle() {
        return new Bus(Align_X, Align_Y);
    }

    @Override
    public AirplaneServices getService() {
        return AirplaneServices.bus;
    }

    @Override
    public void clicked(int mx, int my) {

    }
}
