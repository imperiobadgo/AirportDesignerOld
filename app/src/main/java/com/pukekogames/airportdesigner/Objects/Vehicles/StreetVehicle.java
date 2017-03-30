package com.pukekogames.airportdesigner.Objects.Vehicles;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Helper.GameLogic.GameplayWarning;
import com.pukekogames.airportdesigner.Helper.GameLogic.VehicleTask;
import com.pukekogames.airportdesigner.Helper.Pathfinding.Dijkstra;
import com.pukekogames.airportdesigner.Objects.Buildings.Depot;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.VehicleState;

import java.util.Random;

/**
 * Created by Marko Rapka on 24.09.2016.
 */
public abstract class StreetVehicle extends Vehicle {

    private static final long serialVersionUID = 1995179131686454341L;
    VehicleState driveState;

    Random random;
    VehicleTask task;
    int serviceTime;
    Depot homeDepot;

    StreetVehicle(float x, float y) {
        super(x, y);
        random = new Random();
        collisionRadius = 200;
    }

    @Override
    public void tick() {
        warnings.clear();
        if (pathfinding != null) {
            pathfinding.nextStep();
            if (pathfinding.hasPathFound()) {
                hasFailedToFoundPath = false;
                setReferencesOnRoadPath(false);
                nextRoads = pathfinding.getShortestPath();
                setReferencesOnRoadPath(true);
                pathfinding = null;
//                System.out.println("nexttargets found! length: " + nextRoads.size() + " : " + " targetIntersection: " + targetIntersection);
//                for (Integer roadInterIndex: nextRoads){
//                    System.out.print(roadInterIndex + " ");
//                }
//                System.out.println(
            } else if (pathfinding.hasFailed()) {
                pathfinding = null;
                hasFailedToFoundPath = true;
            }
        }
        if (hasFailedToFoundPath){
            warnings.add(GameplayWarning.cantFindPath);
            updateSearch();
        }

        if (serviceTime > 0 && driveState == VehicleState.servicing) {
            serviceTime--;
        } else if (serviceTime <= 0 && driveState == VehicleState.servicing) {
            task.getAirplane().completedService(getService());
            GameInstance.Airport().removeVehicleTask(task);
            driveHome();
        }
        setDistanceToNextVehicle();
//        double diffX = targetPoint.x - Align_X;
//        double diffY = targetPoint.y - Align_Y;
//        distanceToTarget = Math.sqrt((diffX * diffX) + (diffY * diffY));

        updateHeadingAndDistanceToTarget();

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

        float headingDifference = getHeadingdifferenzToNextRoad();

        float distanceToCorner;

        float calcSpeed = speed;
        if (calcSpeed < 8){
            calcSpeed = 8;
        }

        if (headingDifference > 0) {
            distanceToCorner = calcSpeed * 60 + (headingDifference / 180) * 180;//rightturn need to be earlier
        } else if (headingDifference < 0) {
            distanceToCorner = calcSpeed * 50 + (headingDifference / 180) * 180;//leftturn need to be later
        } else {
            distanceToCorner = calcSpeed * 50 + 50;
        }

        if (pathfinding != null && toTarget.Length() < (Math.abs(distanceToCorner) * 1.1)){
            //wait for pathfinding
            targetSpeed = 0;
            updateVelocity();
            setPosition(Align_X, Align_Y);
            return;
        }

        if (toTarget.Length() < Math.abs(distanceToCorner) && currentRoad != null) { //found next target point
            reachedNextRoad();
            currentRoad.removeVehicle(this);

            if (nextRoads.size() > 0) {

                Road road = nextRoads.remove(0);
                currentRoad.removeVehicle(this);
                setRoad(road);
                if (nextRoads.size() > 0) {
                    nextRoad = nextRoads.get(0);
                }

                if (pathfinding == null && nextRoads.size() > 0) {
                    updateSearch();
                }
            } else if (pathfinding == null) {

                if (targetIntersection.equals(getHomeDepotRoadIntersection())) {
//                    GameInstance.Airport().removeVehicleTask(task);
//                    homeDepot.vehicleReachedDepot(this);
                    GameInstance.Airport().RemoveVehicle(this);
                }

                if (task == null) {

                    driveHome();

                } else {
                    if (serviceTime <= 0 && driveState == VehicleState.drivingToGate) {
                        if (getNextIntersection().equals(task.getParkIntersection())) {
                            serviceTime = getServiceTime();

                            driveState = VehicleState.servicing;
                        } else {
                            driveHome();
                        }
                    }
                }
            }
        }


        if (driveState == VehicleState.drivingToGate || driveState == VehicleState.drivingToDepot) {
            targetSpeed = performance.maxSpeed;
        } else if (driveState == VehicleState.servicing || driveState == VehicleState.waiting) {
            targetSpeed = 0f;
        }


        updateVelocity();
        setPosition(Align_X, Align_Y);

    }

    public void searchRoute(RoadIntersection nextIntersection) {
        RoadIntersection lastIntersection;
        if (currentRoad == null) return;
        if (sameDirectionAsRoad) {
            lastIntersection = currentRoad.getNext();
        } else {
            lastIntersection = currentRoad.getLast();
        }
        VehicleState newState = VehicleState.waiting;
        switch (driveState) {

            case waiting:
                newState = VehicleState.drivingToGate;
                break;
            case depot:
                newState = VehicleState.drivingToGate;
                break;
            case drivingToGate:
                newState = VehicleState.drivingToGate;
                break;
            case servicing:
                newState = VehicleState.drivingToDepot;
                break;
            case drivingToDepot:
                newState = VehicleState.drivingToDepot;
                break;
        }
        if (lastIntersection.equals(nextIntersection) || nextIntersection == null) return;
        targetIntersection = nextIntersection;
        setReferencesOnRoadPath(false);
        pathfinding = new Dijkstra(lastIntersection, nextIntersection, this);
        driveState = newState;
//        System.out.println("new Pathfinding startet: " + indexoflastIntersection + " - " + indexofNextIntersection);

    }

    abstract AirplaneServices getService();

    abstract int getServiceTime();

    void driveHome() {
        driveState = VehicleState.drivingToDepot;
        searchRoute(getHomeDepotRoadIntersection());
    }

    RoadIntersection getHomeDepotRoadIntersection() {
        return homeDepot.getRoad().getNext();
    }

    public void setTask(VehicleTask task) {
        this.task = task;
    }

    public void clearVehicle(){
        if (task != null) {
            GameInstance.Airport().removeVehicleTask(task);
            task = null;
        }
        homeDepot.removeVehicleJustFromList(this);
    }

    public void setHomeDepot(Depot homeDepot) {
        this.homeDepot = homeDepot;
    }

    public Depot getHomeDepot() {
        return homeDepot;
    }

    public int getServiceTimeLeft() {
        return serviceTime;
    }

    public void setDriveState(VehicleState state) {
        driveState = state;
    }
}
