package com.pukekogames.airportdesigner.Objects.Buildings;

import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.Prices;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Vehicles.CrewBus;
import com.pukekogames.airportdesigner.Objects.Vehicles.StreetVehicle;

/**
 * Created by Marko Rapka on 02.10.2016.
 */
public class CrewBusDepot extends Depot  {

    private static final long serialVersionUID = -8416516566781983406L;

    public CrewBusDepot(Road road) {
        super(road);
        setImageID(Images.indexCrewBusDepot);
    }

    @Override
    public long getStartCosts() {
        return Prices.CrewbusStart;
    }

    @Override
    StreetVehicle createVehicle() {
        return new CrewBus(Align_X, Align_Y);
    }

    @Override
    public AirplaneServices getService() {
        return AirplaneServices.crew;
    }

    @Override
    public void clicked(int mx, int my) {

    }
}
