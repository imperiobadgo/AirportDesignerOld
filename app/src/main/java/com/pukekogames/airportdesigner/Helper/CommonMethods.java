package com.pukekogames.airportdesigner.Helper;

import com.pukekogames.airportdesigner.GameInstance.Airport;
import com.pukekogames.airportdesigner.Helper.Geometry.PointFloat;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Objects.Buildings.Building;
import com.pukekogames.airportdesigner.Objects.Buildings.Depot;
import com.pukekogames.airportdesigner.Objects.ClickableGameObject;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Roads.Runway;
import com.pukekogames.airportdesigner.Objects.Vehicles.StreetVehicle;
import com.pukekogames.airportdesigner.Objects.Vehicles.Vehicle;


/**
 * Created by Marko Rapka on 12.06.2016.
 */
public class CommonMethods {

    public static double getDistance(PointFloat p1, PointFloat p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public static double getDistance(PointInt p1, PointInt p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public static double getDistance(PointInt p1, PointFloat p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public static double getDistance(PointFloat p1, PointInt p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public static double pointToLineDistance(PointFloat A, PointFloat B, PointFloat P) {
        double normalLength = Math.sqrt((B.x - A.x) * (B.x - A.x) + (B.y - A.y) * (B.y - A.y));
        return Math.abs((P.x - A.x) * (B.y - A.y) - (P.y - A.y) * (B.x - A.x)) / normalLength;
    }

    //>0 P is on left side, =0 P is on the line, <0 P is on the right side
    public static double whichSidePointToLine(PointFloat A, PointFloat B, PointFloat P) {
        return (B.x - A.x) * (P.y - A.y) - (P.x - A.x) * (B.y - A.y);
    }

    public static boolean isTouchInside(int x, int y, ClickableGameObject object) {
        return (x > object.getAlign_X() &&
                x < object.getAlign_X() + object.getWidth() &&
                y > object.getAlign_Y() &&
                y < object.getAlign_Y() + object.getHeight());
    }

    public static RoadIntersection getUsedRunwayIntersection(Runway runway, float windDirection){

        float maxCrosswindAllowed = 0.4f; //0 is direct runway heading, 1 is 90 degrees

        float firstDirection = runway.getHeading();
        float secondDirection = (runway.getHeading() + 180) % 360;
        double runwayX = Math.sin((firstDirection * Math.PI) / 180);//firstdirectionvector
        double runwayY = Math.cos((firstDirection * Math.PI) / 180);
        double windX = Math.sin((windDirection * Math.PI) / 180);//windvector
        double windY = Math.cos((windDirection * Math.PI) / 180);
        double scala1 = runwayX * windX + runwayY * windY;//dotproduct
        runwayX = Math.sin((secondDirection * Math.PI) / 180);//seconddirectionvector
        runwayY = Math.cos((secondDirection * Math.PI) / 180);
        double scala2 = runwayX * windX + runwayY * windY;//dotproduct
        if (1 - scala1 < maxCrosswindAllowed) {//positive if both vectors are intersecting in small angle
            RoadIntersection intersection = runway.getLast();
            return intersection;
        } else if (1 - scala2 < maxCrosswindAllowed) {
            RoadIntersection intersection = runway.getNext();
            return intersection;
        }
        return null;
    }

    public static void loadAllObjectReferences(Airport airport) {

        for (int i = 0; i < airport.getRoadIntersectionCount(); i++) {
            RoadIntersection intersection = airport.getRoadIntersection(i);
            if (intersection == null) continue;
            intersection.customWriteObject();
        }

        for (int i = 0; i < airport.getRoadCount(); i++) {
            Road road = airport.getRoad(i);
            if (road == null) continue;
            road.customWriteObject();
            RoadIntersection intersection = road.getLast();
            if (intersection != null) {
                intersection.addRoad(road);
            }
            intersection = road.getNext();
            if (intersection != null) {
                intersection.addRoad(road);
            }
        }

        for (int i = 0; i < airport.getBuildingCount(); i++) {
            Building building = airport.getBuilding(i);
            if (building instanceof Depot){
                ((Depot)building).customWriteObject();
            }

        }


        for (int i = 0; i < airport.getVehicleCount(); i++) {
            Vehicle vehicle = airport.getVehicle(i);
            if (vehicle == null) continue;
            Road currentRoad = vehicle.getCurrentRoad();
            if (currentRoad != null) {
                currentRoad.addVehicle(vehicle);
//                Log.i(Game.TAG,"currentroad.addVehicle");
            }
            if (vehicle instanceof StreetVehicle){
                StreetVehicle streetVehicle = (StreetVehicle) vehicle;
                Depot homeDepot = streetVehicle.getHomeDepot();
                if (homeDepot != null){
                    homeDepot.addVehicleDesirialization(streetVehicle);
                }
            }

        }
//        GameInstance.Airport().loadBitmaps();
//        Log.i(Game.TAG,"Done loading.");
    }


}
