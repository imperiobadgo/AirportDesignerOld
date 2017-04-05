package com.pukekogames.airportdesigner.Objects.Vehicles;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Helper.GameLogic.GameplayWarning;
import com.pukekogames.airportdesigner.Helper.GameLogic.VehicleTask;
import com.pukekogames.airportdesigner.Helper.Geometry.PointFloat;
import com.pukekogames.airportdesigner.Helper.Pathfinding.Dijkstra;
import com.pukekogames.airportdesigner.Objects.Buildings.Depot;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Roads.ParkGate;
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
    public boolean reachedParkGate;
    public int parkgateNumber;

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
        if (hasFailedToFoundPath) {
            warnings.add(GameplayWarning.cantFindPath);
            updateSearch();
        }

        if (serviceTime > 0 && driveState == VehicleState.servicing) {
            serviceTime--;
        } else if (serviceTime <= 0 && driveState == VehicleState.servicing) {
            task.getAirplane().completedService(getService());
            GameInstance.Airport().removeVehicleTask(task);
            driveHome();
            ignoreCollisionTime = 100;
        }
        setDistanceToNextVehicle();

        if (reachedParkGate && driveState == VehicleState.arrivedAtGate) {
            driveOnParkGate();
            return;
        }
        updateHeadingAndDistanceToTarget();


        float headingDifference = getHeadingdifferenzToNextRoad();

        float distanceToCorner;

        float calcSpeed = speed;
        if (calcSpeed < 8) {
            calcSpeed = 8;
        }

        if (headingDifference > 0) {
            distanceToCorner = calcSpeed * 60 + (headingDifference / 180) * 180;//rightturn need to be earlier
        } else if (headingDifference < 0) {
            distanceToCorner = calcSpeed * 50 + (headingDifference / 180) * 180;//leftturn need to be later
        } else {
            distanceToCorner = calcSpeed * 50 + 50;
        }

        if (pathfinding != null && toTarget.Length() < (Math.abs(distanceToCorner) * 1.1)) {
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

                            reachedParkGate = true;
                            serviceTime = getServiceTime();

                            driveState = VehicleState.arrivedAtGate;
                            parkgateNumber = 1;
                            ParkGate targetGate = task.getParkPosition();
                            PointFloat target = targetGate.getCornerPosition(parkgateNumber);

                            targetPoint.set(target.x, target.y);
                        } else {
                            driveHome();
                        }
                    }
                }
            }
        }


        if (driveState == VehicleState.drivingToGate || driveState == VehicleState.drivingToDepot) {
            targetSpeed = performance.maxSpeed;
        } else if (driveState == VehicleState.arrivedAtGate) {
            targetSpeed = performance.maxSpeed / 2;
        } else if (driveState == VehicleState.servicing || driveState == VehicleState.waiting) {
            targetSpeed = 0f;
        }


        updateVelocity();
        setPosition(Align_X, Align_Y);

    }

    void driveOnParkGate() {
        if (task == null) {
            reachedParkGate = false;
            driveHome();
        }
        ParkGate targetGate = task.getParkPosition();
        ignoreCollisionTime = 40;

//        float diffX = targetGate.getCenterPosition().x - Align_X;
//        float diffY = targetGate.getCenterPosition().y - Align_Y;


        toTarget.set(Align_X, Align_Y, targetPoint.x, targetPoint.y);
//
//        float headingDirect = (float) Math.toDegrees(Math.atan2(diffX, diffY)) % 360;
//
//        heading = headingDirect;
//        headingDirection.set(heading);
        float headingToTarget = getHeadingToTarget(null, toTarget.getX(), toTarget.getY(), performance.targetPointDistance);


        updateToDirectHeading(headingToTarget, false);

        float headingShowLength = 600;
        headingPoint.set((int) (Align_X + Math.cos(Math.toRadians(headingToTarget)) * headingShowLength), (int) (Align_Y + Math.sin(Math.toRadians(headingToTarget)) * headingShowLength));

//        Math.sqrt(diffX * diffX + diffY * diffY) > 1000 &&

        if (toTarget.Length() < 200) {
            if (parkgateNumber == 4) {
                driveState = VehicleState.servicing;
                parkgateNumber = 0;
                speed = 0;
            } else {
                parkgateNumber++;
                PointFloat target = targetGate.getCornerPosition(parkgateNumber);

                targetPoint.set(target.x, target.y);
            }

        }

        targetSpeed = performance.maxSpeed / 2f;

//        float diffX = targetPoint.x - Align_X;
//        float diffY = targetPoint.y - Align_Y;

//        double length = Math.sqrt(diffX * diffX + diffY * diffY);
//        diffX /= length;
//        diffY /= length;
//
//        Align_X += diffX * targetSpeed;
//        Align_Y += diffY * targetSpeed;
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

    public void clearVehicle() {
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

    public VehicleState getDriveState() {
        return driveState;
    }
}
