package com.pukekogames.airportdesigner.Objects.Buildings;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplaneState;

/**
 * Created by Marko Rapka on 16.10.2016.
 */
public class Tower extends Building {

    private static final long serialVersionUID = 2039871905208224087L;
    int timeToNextInstruction = 0;

    public Tower(Road road) {
        super(road);
        setImageID(Images.indexTower);
        //remove condition in RenderBuilding for showing max capacity
    }

    @Override
    public void tick() {
        super.tick();
        if (timeToNextInstruction > 0) {
            timeToNextInstruction--;
        }
    }

    public void InstructAirplane(Airplane airplane) {
        AirplaneState state = airplane.getState();
        if (timeToNextInstruction > 0) {
            return;
        }

        switch (state) {

            case WaitingForGate:

                RoadIntersection nextGateIntersection = GameInstance.Airport().getIntersectionOfNextFreeGate(airplane.getPerformance().NeedingTerminal());
                if (nextGateIntersection != null) {
                    airplane.searchRoute(nextGateIntersection);
                }

                break;
            case ReadyForPushback:

                RoadIntersection nextRunwayIntersection = GameInstance.Airport().getIntersectionOfNextPossibleRunway();
                if (nextRunwayIntersection != null) {
                    airplane.searchRoute(nextRunwayIntersection);
                }
                break;
            case ReadyForDeparture:

                for (RoadIntersection intersection : airplane.getPossibleTargets()) {
                    airplane.searchRoute(intersection);
                    break;
                }
                break;
            default:
                return;

        }
        timeToNextInstruction = 100;
    }
}
