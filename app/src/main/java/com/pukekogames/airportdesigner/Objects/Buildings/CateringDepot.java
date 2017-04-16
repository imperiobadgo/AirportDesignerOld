package com.pukekogames.airportdesigner.Objects.Buildings;

import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.Prices;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Vehicles.CateringTruck;
import com.pukekogames.airportdesigner.Objects.Vehicles.StreetVehicle;

/**
 * Created by Marko Rapka on 28.09.2016.
 */
public class CateringDepot extends Depot {


    private static final long serialVersionUID = -8602577964583267242L;

    public CateringDepot(Road road) {
        super(road);
        setImageID(Images.indexCateringDepot);
    }

    @Override
    public long getStartCosts() {
        return Prices.CateringTruckStart;
    }

    @Override
    StreetVehicle createVehicle() {
        return new CateringTruck(Align_X, Align_Y);
    }

    @Override
    public AirplaneServices getService() {
        return AirplaneServices.catering;
    }
}
