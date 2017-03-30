package com.pukekogames.airportdesigner.Objects.Buildings;

import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.Prices;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Vehicles.StreetVehicle;
import com.pukekogames.airportdesigner.Objects.Vehicles.TankTruck;

/**
 * Created by Marko Rapka on 02.10.2016.
 */
public class TankDepot extends Depot  {
    private static final long serialVersionUID = -9175432386573787847L;

    public TankDepot(Road road) {
        super(road);
        setImageID(Images.indexTankDepot);
    }

    @Override
    public long getStartCosts() {
        return Prices.TanktruckStart;
    }

    @Override
    StreetVehicle createVehicle() {
        return new TankTruck(Align_X, Align_Y);
    }

    @Override
    public AirplaneServices getService() {
        return AirplaneServices.tank;
    }

    @Override
    public void clicked(int mx, int my) {

    }
}
