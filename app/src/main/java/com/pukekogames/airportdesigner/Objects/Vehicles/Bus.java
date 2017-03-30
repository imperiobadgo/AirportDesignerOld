package com.pukekogames.airportdesigner.Objects.Vehicles;

import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.BusData;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.VehicleState;

/**
 * Created by Marko Rapka on 12.06.2016.
 */
public class Bus extends StreetVehicle {

    private static final long serialVersionUID = -6360740289601941375L;

    public Bus(float x, float y) {
        super(x, y);

        setImageID(Images.indexBus);
        performance = new BusData();
        driveState = VehicleState.depot;
    }

    @Override
    public void clicked(int mx, int my) {

    }

    @Override
    public void tick() {
        super.tick();
//        warnings.clear();
//        if (pathfinding != null) {
//            pathfinding.nextStep();
//            if (pathfinding.hasPathFound()) {
//                nextRoads = pathfinding.getShortestPath();
//                setReferencesOnRoadPath(true);
//                pathfinding = null;
////                System.out.println("nexttargets found! length: " + nextRoads.size() + " : " + " targetIntersection: " + targetIntersection);
////                for (Integer roadInterIndex: nextRoads){
////                    System.out.print(roadInterIndex + " ");
////                }
////                System.out.println(
//            } else if (pathfinding.hasFailed()) {
//                pathfinding = null;
//            }
//        }
//        if (serviceTime > 0 && driveState == VehicleState.servicing) {
//            serviceTime--;
//        } else if (serviceTime <= 0 && driveState == VehicleState.servicing) {
//            task.generateNewAirplane().completedService(AirplaneServices.bus);
//            GameInstance.Airport().removeVehicleTask(task);
//            driveHome();
//        }

//        double diffX = targetPoint.x - Align_X;
//        double diffY = targetPoint.y - Align_Y;
//        distanceToTarget = Math.sqrt((diffX * diffX) + (diffY * diffY));
//
//
//        float headingToTarget = getHeadingToTarget(currentRoad, diffX, diffY, performance.targetPointDistance);
//
//        float headingDifference = headingToTarget - heading;
//        headingDifference = (headingDifference + 180) % 360 - 180;
//        if (headingDifference < -180) headingDifference += 360;
//
//        if (Math.abs(headingDifference) > performance.turnRate) {
//            if (headingDifference >= 0) {
//                heading += performance.turnRate;
//            } else {
//                heading -= performance.turnRate;
//            }
//        }
//
//        headingDifference = getHeadingdifferenzToNextRoad();
//
//        float distanceToCorner;
//
//
//        if (headingDifference > 0) {
//            distanceToCorner = speed * 60 + (headingDifference / 180) * 200;//rightturn need to be earlier
//        } else if (headingDifference < 0) {
//            distanceToCorner = speed * 50 + (headingDifference / 180) * 200;//leftturn need to be later
//        } else {
//            distanceToCorner = speed * 50 + 50;
//        }
//
//        if (Math.abs(distanceToTarget) < Math.abs(distanceToCorner) && currentRoad != null) { //found next target point
//
//            currentRoad.removeVehicle(this);
//
//            if (nextRoads.size() > 0) {
//
//                Road road = nextRoads.remove(0);
//                currentRoad.removeVehicle(this);
//                setRoad(road);
//                if (nextRoads.size() > 0) {
//                    nextRoad = nextRoads.get(0);
//                }
//
//                if (pathfinding == null && nextRoads.size() > 0) {
//                    updateSearch();
//                }
//            } else if (pathfinding == null) {
//
//                if (targetIntersection.equals(getHomeDepotRoadIntersection())) {
//
//                    homeDepot.vehicleReachedDepot(this);
//                }
//
//                if (task == null) {
//
//                    driveHome();
//
//                } else {
//                    if (serviceTime <= 0 && driveState == VehicleState.drivingToGate) {
//                        if (getNextIntersection().equals(task.getParkPosition())) {
//                            Random random = new Random();
//                            serviceTime = SERVICETIMESET + random.nextInt(500);
//
//                            driveState = VehicleState.servicing;
//                        } else {
//                            driveHome();
//                        }
//                    }
//                }
//            }
//        }
//
//
//        if (driveState == VehicleState.drivingToGate || driveState == VehicleState.drivingToDepot) {
//            targetSpeed = performance.maxSpeed;
//        } else if (driveState == VehicleState.servicing || driveState == VehicleState.waiting) {
//            targetSpeed = 0f;
//        }
//
//
//
//        updateVelocity();
//        setPosition(Align_X, Align_Y);
    }

    @Override
    AirplaneServices getService() {
        return AirplaneServices.bus;
    }

    @Override
    int getServiceTime() {
        return 200 + random.nextInt(600);
    }


}
