package com.pukekogames.airportdesigner.Helper.ClassTranslation;

import com.pukekogames.airportdesigner.Objects.Buildings.*;
import com.pukekogames.airportdesigner.Objects.Roads.*;

/**
 * Created by Marko Rapka on 24.08.2016.
 */
public class TypeTranslation {

    public static Road translateRoad(RoadType roadType){
        Road returnRoad = null;
        switch (roadType){
            case taxiway:
                returnRoad = new Taxiway();
                break;
            case runway:
                returnRoad = new Runway();
                break;
            case street:
                returnRoad = new Street();
                break;
            case parkGate:
                returnRoad = new ParkGate();
                break;
        }
        return returnRoad;
    }

    public static Building translateDepot(BuildingType buildingType, Road road){
        Building returnBuilding = null;
        switch (buildingType){

            case busDepot:
                returnBuilding = new BusDepot(road);
                break;
            case cateringDepot:
                returnBuilding = new CateringDepot(road);
                break;
            case crewBusDepot:
                returnBuilding = new CrewBusDepot(road);
                break;
            case tankDepot:
                returnBuilding = new TankDepot(road);
                break;
            case tower:
                returnBuilding = new Tower(road);
                break;
            case terminal:
                returnBuilding = new Terminal(road);
                break;
        }
        return returnBuilding;
    }

    public static Road translateRoadFromString(String roadString){
        Road returnRoad = null;
        switch (roadString) {
            case "Taxiway":
                returnRoad = new Taxiway();

                break;
            case "Runway":
                returnRoad = new Runway();

                break;
            case "Street":
                returnRoad = new Street();

                break;
            case "ParkGate":
                returnRoad = new ParkGate();

                break;
        }
        return returnRoad;
    }
}
