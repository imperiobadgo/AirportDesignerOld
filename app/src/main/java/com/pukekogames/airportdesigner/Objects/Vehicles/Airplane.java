package com.pukekogames.airportdesigner.Objects.Vehicles;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Helper.GameLogic.GameplayWarning;
import com.pukekogames.airportdesigner.Helper.Geometry.PointFloat;
import com.pukekogames.airportdesigner.Helper.Geometry.Vector2D;
import com.pukekogames.airportdesigner.Helper.Pathfinding.Dijkstra;
import com.pukekogames.airportdesigner.Helper.TimeStamp;
import com.pukekogames.airportdesigner.Objects.*;
import com.pukekogames.airportdesigner.Objects.Airlines.Airline;
import com.pukekogames.airportdesigner.Objects.Roads.ParkGate;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Roads.Runway;
import com.pukekogames.airportdesigner.Objects.Roads.Street;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplanePerformance;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplaneState;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 21.03.2016.
 */
public class Airplane extends Vehicle {

    private static final long serialVersionUID = -4254908385568668087L;
    private int category;
    private float altitude = 0f;
//    private AirplanePerformance performance;

    private ArrayList<AirplaneServices> neededServices;
    private ArrayList<AirplaneServices> bordingServices;
    private Airline airline;
    private String callSign;
    private TimeStamp plannedTime;

    private AirplaneState state;
    private int turnaroundTime = 0;
    private float parkgateRunwayDistance = 0;
    private int samePositionCount = 0;
    private boolean holdPosition = false;

    private boolean isLeavingGateForReposition = false;

    public Airplane(AirplanePerformance performance, Airline airline) {
        super(0, 0);
        this.airline = airline;
        this.plannedTime = new TimeStamp(-1);
        this.performance = performance;
        turnaroundTime = 0;
        state = AirplaneState.Arrival;
        callSign = "";
        imageID = performance.getImageID();
        category = performance.getCategory();
        collisionRadius = performance.collisionRadius;
        neededServices = new ArrayList<>();
        bordingServices = new ArrayList<>();
    }

    @Override
    public void clicked(int mx, int my) {

    }

    @Override
    public void tick() {
        if (state == AirplaneState.Init) {
            return;
        }
        warnings.clear();
        if (pathfinding != null) {
            pathfinding.nextStep();
            if (pathfinding.hasPathFound()) {
                hasFailedToFoundPath = false;
                setReferencesOnRoadPath(false);
                nextRoads = pathfinding.getShortestPath();
                if (state == AirplaneState.Pushback && nextRoads.size() > 0) state = AirplaneState.TaxiToRunway;
                setReferencesOnRoadPath(true);
                samePositionCount = 0;
                pathfinding = null;
//                System.out.print("nexttargets found! length: " + nextRoads.size() + " : ");
//                for (Integer roadInterIndex: nextRoads){
//                    System.out.print(roadInterIndex + " ");
//                }
//                System.out.println();
            } else if (pathfinding.hasFailed()) {
//                state = AirplaneState.Waiting;
                pathfinding = null;
                hasFailedToFoundPath = true;
            }
        }
        if (hasFailedToFoundPath) {
            warnings.add(GameplayWarning.cantFindPath);
            if (pathfinding == null) {
                updateSearch();
            }
            return;
        }
        if (!(state == AirplaneState.Arrival || state == AirplaneState.Landing || state == AirplaneState.Takeoff || state == AirplaneState.Departure)) {
            turnaroundTime++;
        }

        setDistanceToNextVehicle();
        if (waitTime > 0) {
            waitTime--;
            return;
        }
        if (neededServices.size() > 0 || bordingServices.size() > 0) {
            if (isServiceNotPossible()) {
                warnings.add(GameplayWarning.noServicesPossible);
            }
            return;
        }

        if (state == AirplaneState.ClearedForDeparture && isRunwayBlocked()) { //waiting for cleared runway
            return;
        }

        if (state == AirplaneState.Boarding) {
            state = AirplaneState.ReadyForPushback;
        }

        float headingDifference = updateHeadingAndDistanceToTarget(continueHeading());

        float headingDifferenceToNextRoad = getHeadingdifferenzToNextRoad();

        float calcSpeed = speed;
        if (calcSpeed < 9) {
            calcSpeed = 9;
        }

        float distanceToCorner = calcSpeed * 55 + (Math.abs(headingDifferenceToNextRoad) / 180) * 200;

        if (state == AirplaneState.Landing) distanceToCorner = calcSpeed * 60;
//        if (state == AirplaneState.TaxiToRunway && nextRoads.size() == 0) distanceToCorner = speed * 20;
        if (state == AirplaneState.ArrivedAtGate) distanceToCorner = calcSpeed * 10;
//        if (state == AirplaneState.ClearedForDeparture) distanceToCorner = calcSpeed * 2;
        if (state == AirplaneState.ClearedForDeparture) distanceToCorner = calcSpeed * 40;

        if (pathfinding != null && toTarget.Length() < (Math.abs(distanceToCorner) * 1.1)) {
            //wait for pathfinding
            targetSpeed = 0;
            updateVelocity();
            setPosition(Align_X, Align_Y);
            return;
        }

        if (hasReachedRunway() || (toTarget.Length() < Math.abs(distanceToCorner) && !continueHeading() && currentRoad != null)) {
            reachedNextRoad();
            if (!(currentRoad instanceof ParkGate)) {
                currentRoad.removeVehicle(this);
            }
            if ((currentRoad instanceof ParkGate && state == AirplaneState.TaxiToRunway) ||
                    (currentRoad instanceof ParkGate && state == AirplaneState.TaxiToGate && isLeavingGateForReposition) ||
                    (currentRoad instanceof Runway && state == AirplaneState.TaxiToGate)) {

                currentRoad.setBlocked(false);
                if (currentRoad instanceof Runway) {
                    ((Runway) currentRoad).removeAirplaneBlockingRunway(this);
                }
                currentRoad.removeVehicle(this);
                isLeavingGateForReposition = false;
            }
            samePositionCount++;

            if (nextRoads.size() > 0) {
                samePositionCount = 0;
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
                samePositionCount = 0;
                if (currentRoad instanceof ParkGate && state == AirplaneState.TaxiToGate) {//arrived at gate
                    state = AirplaneState.ArrivedAtGate;
                    setNeededServices();
                    targetSpeed = 0f;
                    speed = targetSpeed;

                } else if (state == AirplaneState.ArrivedAtGate) {

                    state = AirplaneState.Boarding;
                } else if (state == AirplaneState.TaxiToRunway) {
                    state = AirplaneState.ReadyForDeparture;
                } else if (state == AirplaneState.ClearedForDeparture) {
                    takeOff();
                } else if (state == AirplaneState.Landing) {

                    if (currentRoad != null && targetIntersection == null) {

                        RoadIntersection lastIntersection;
                        if (sameDirectionAsRoad) {
                            lastIntersection = currentRoad.getNext();
                        } else {
                            lastIntersection = currentRoad.getLast();
                        }
                        Road[] attachedRaods = lastIntersection.getRoadArray();
                        Road connectedRoad = null;
                        for (int i = 0; i < attachedRaods.length; i++) {
                            Road road = attachedRaods[i];
                            if (!(road instanceof Runway)) {
                                connectedRoad = road;
                                break;
                            }

                        }
                        if (connectedRoad != null) {
                            if (currentRoad instanceof Runway) {
                                ((Runway) currentRoad).removeAirplaneBlockingRunway(this);
                            }
                            currentRoad.setBlocked(false);
                            currentRoad.removeVehicle(this);
                            setRoad(connectedRoad);
                            //get of the runway
                            if (lastIntersection.equals(connectedRoad.getLast())) {
                                targetIntersection = connectedRoad.getNext();
                            } else {
                                targetIntersection = connectedRoad.getLast();
                            }
                        } else {
                            state = AirplaneState.WaitingForGate;
                        }
                    } else {
                        state = AirplaneState.WaitingForGate;
                    }
                }
            }
            if (samePositionCount > 100) {
//                    state = AirplaneState.Waiting;
            }
        }

//        if ((toTarget.Length() < Math.abs(distanceToCorner) && !continueHeading() && currentRoad != null) || //found next target point
//                (toTarget.Length() < speed * 90 && nextRoads.size() == 0 && state == AirplaneState.TaxiToRunway && currentRoad != null) //reached holding point in front of runway
//                ) {
//
//        }
//        diffX = (float) Math.cos(Math.toRadians(heading));
//        diffY = (float) Math.sin(Math.toRadians(heading));
        AirplanePerformance airplanePerformance = (AirplanePerformance) performance;
        if (holdPosition) {
            targetSpeed = 0f;
        } else if (state == AirplaneState.TaxiToRunway || state == AirplaneState.TaxiToGate || state == AirplaneState.ClearedForDeparture || (state == AirplaneState.Landing && altitude == 0f)) {
            targetSpeed = airplanePerformance.taxiSpeed;
        } else if (state == AirplaneState.ArrivedAtGate) {
            targetSpeed = airplanePerformance.taxiSpeed / 2;
        } else if (state == AirplaneState.Takeoff || state == AirplaneState.Departure) {
            targetSpeed = performance.maxSpeed;
        } else if (state == AirplaneState.Waiting || state == AirplaneState.Boarding ||
                state == AirplaneState.ReadyForPushback || state == AirplaneState.WaitingForGate ||
                state == AirplaneState.ReadyForDeparture) {
            targetSpeed = 0f;
        }
        if (state == AirplaneState.Takeoff && headingDifference > 2 && altitude == 0) {
            targetSpeed = airplanePerformance.taxiSpeed;
        }
        updateVelocity();

//        float differenzSpeed = targetSpeed - speed;
//        if (differenzSpeed < -performance.deceleration) {
//            speed -= performance.deceleration;
//        } else if (differenzSpeed > performance.acceleration) {
//            speed += performance.acceleration;
//        }
//        if (speed < 1 && targetSpeed == 0) speed = 0;
//        Align_X += diffX * speed;
//        Align_Y += diffY * speed;
        if ((state == AirplaneState.Takeoff && speed >= airplanePerformance.takeOffSpeed) || state == AirplaneState.Departure) {
            altitude += airplanePerformance.climbrate;
        } else if (state == AirplaneState.Landing || state == AirplaneState.Arrival) {
            altitude -= airplanePerformance.sinkrate;
            if (altitude < 0f) altitude = 0f;
        }
        if (altitude > 1000f && state == AirplaneState.Takeoff) {
            if (currentRoad instanceof Runway) {
                ((Runway) currentRoad).removeAirplaneBlockingRunway(this);
            }
            currentRoad.setBlocked(false);//make runway free again
            currentRoad.removeVehicle(this);
            state = AirplaneState.Departure;
        }
        if (altitude < 1000f && state == AirplaneState.Arrival) {
            state = AirplaneState.Landing;
        }
        if (altitude > 3000f && state == AirplaneState.Departure) {
            int subtractMoney = turnaroundTime - Math.round(getMaxTurnaroundTime() * 0.8f);
            subtractMoney /= 100;
            if (subtractMoney < 0) subtractMoney = 0;
            long rewardMoney = (Prices.AirplaneTookOff * category) - (long) subtractMoney;
            if (rewardMoney < 0) {
                rewardMoney = 0;
            }
            GameInstance.Instance().addMoney(rewardMoney);
            GameInstance.Airport().RemoveVehicle(this);
        }

        setPosition(Align_X, Align_Y);
    }

    private boolean hasReachedRunway() {
        float calcSpeed = speed;
        if (calcSpeed < 9) {
            calcSpeed = 9;
        }

        float distanceToRunway = calcSpeed * 50;

        boolean hasReached = (toTarget.Length() < distanceToRunway && nextRoads.size() == 0 && state == AirplaneState.TaxiToRunway && currentRoad != null);

        return hasReached;
    }

    private boolean hasReachedGate() {
        float distanceToGate = collisionRadius * 1.2f;
        boolean hasReached = toTarget.Length() < speed * distanceToGate && !continueHeading() && currentRoad != null;

        return hasReached;
    }

    private boolean isRunwayBlocked() {
        Runway runway = GameInstance.Airport().getConnectedRunway(targetIntersection);
        if (runway != null) {
            if (runway.isBlocked()) return true;
            if (runway.getPositionInDepartingAirplanes(this) > 0) return true;
        }
        return false;
    }

    private boolean continueHeading() {
        if (state == AirplaneState.Departure) return true;
        float dotProduct = Vector2D.DotProduct(toTarget, headingDirection);
        return state == AirplaneState.Takeoff && dotProduct > 0;
    }

    private void takeOff() {
        Runway runway = GameInstance.Airport().getConnectedRunway(targetIntersection);
        if (runway != null) {
            if (runway.isBlocked()) return;//wait until runway is free for takeoff
            //takeoff
            setRoad(runway);

            if (Math.sqrt(Math.pow(runway.getStartPosition().x - targetPoint.x, 2) + Math.pow(runway.getStartPosition().y - targetPoint.y, 2)) <
                    Math.sqrt(Math.pow(runway.getEndPosition().x - targetPoint.x, 2) + Math.pow(runway.getEndPosition().y - targetPoint.y, 2))) {
//                heading = (runway.getHeading() + 180) % 360;
                targetIntersection = runway.getNext();
            } else {
//                heading = runway.getHeading();
                targetIntersection = runway.getLast();
            }
            runway.addAirplaneBlockingRunway(this);
            runway.removeAirplaneForDeparture(this);
            runway.addVehicle(this);
            state = AirplaneState.Takeoff;
        }
    }

    public void setTargetPoint(PointFloat targetPoint) {
        this.targetPoint = targetPoint;
    }

    public ArrayList<RoadIntersection> getPossibleTargets() {
        ArrayList<RoadIntersection> returnList = new ArrayList<>();
        switch (state) {
            case Init:
                break;
            case Waiting:
                addPossibleGates(returnList);
                break;
            case ReadyForPushback:
                returnList.addAll(GameInstance.Airport().getIntersectionsOfAllPossibleRunways());
                break;
            case TaxiToGate:
//                ArrayList<ParkGate> gates = GameInstance.Airport().getAllFreeGates();
//                for (ParkGate gate: gates){
//                    RoadIntersection intersection = GameInstance.Airport().getRoadIntersection(gate.getIndexNext());
//                    returnList.add(intersection);
//                }
                break;
            case TaxiToRunway:
//                returnList.addAll(GameInstance.Airport().getIntersectionsOfAllPossibleRunways());
                break;
            case Landing:
                break;
            case WaitingForGate:
                addPossibleGates(returnList);
                break;
            case Takeoff:
                break;
            case Arrival:
                break;
            case Departure:
                break;
            case ArrivedAtGate:
            case Boarding:
                if (warnings.contains(GameplayWarning.noServicesPossible)) {
                    addPossibleGates(returnList);
                }
                break;
            case Pushback:
                break;
            case ReadyForDeparture:
                returnList.add(targetIntersection);
                break;
            case ClearedForDeparture:
                break;
        }
        return returnList;
    }

    private void addPossibleGates(ArrayList<RoadIntersection> returnList) {
        boolean needTerminal = getPerformance().NeedingTerminal();
        //prefer parkGate according to performance
        for (ParkGate gate : GameInstance.Airport().getAllFreeGates(needTerminal)) {
            returnList.add(gate.getNext());
        }
        if (returnList.size() == 0){
            if (needTerminal){
                //terminal needed but there are no parkGates with terminal, so get a parkGate without terminal
                for (ParkGate gate : GameInstance.Airport().getAllFreeGates(false)) {
                    returnList.add(gate.getNext());
                }
            }else{
                //no terminal needed but there are no parkGates without terminal, so get a parkGate with terminal
                for (ParkGate gate : GameInstance.Airport().getAllFreeGates(true)) {
                    returnList.add(gate.getNext());
                }
            }

        }
    }

    @Override
    public void searchRoute(RoadIntersection nextIntersection) {
        RoadIntersection lastIntersection;
        if (currentRoad == null) return;
        if (sameDirectionAsRoad) {
            lastIntersection = currentRoad.getNext();
        } else {
            lastIntersection = currentRoad.getLast();
        }
        if (currentRoad instanceof ParkGate) {
            if (!sameDirectionAsRoad) {
                lastIntersection = currentRoad.getNext();
            } else {
                lastIntersection = currentRoad.getLast();
            }
        }
        AirplaneState newState = AirplaneState.Waiting;
        switch (state) {
            case Init:
                break;
            case Waiting:
                newState = AirplaneState.TaxiToGate;
                break;
            case ReadyForPushback:
                newState = AirplaneState.TaxiToRunway;
                break;
            case TaxiToGate:
                newState = AirplaneState.TaxiToGate;
                break;
            case TaxiToRunway:
                newState = AirplaneState.TaxiToRunway;
                break;
            case Landing:
                break;
            case WaitingForGate:
                currentRoad.removeVehicle(this);
                newState = AirplaneState.TaxiToGate;
                parkgateRunwayDistance = new Vector2D(lastIntersection.getPosition(), nextIntersection.getPosition()).Length();
                break;
            case Takeoff:
                break;
            case Arrival:
                break;
            case Departure:
                break;
            case ArrivedAtGate:
            case Boarding:
                currentRoad.removeVehicle(this);
                newState = AirplaneState.TaxiToGate;
                isLeavingGateForReposition = true;
                break;
            case Pushback:
                break;
            case ReadyForDeparture:
                state = AirplaneState.ClearedForDeparture;

                Runway runway = GameInstance.Airport().getConnectedRunway(nextIntersection);
                if (runway != null) {
                    runway.addAirplaneForDeparture(this);
                }

                break;
            case ClearedForDeparture:
                break;
        }
        if (lastIntersection.equals(nextIntersection) || nextIntersection == null) return;
        targetIntersection = nextIntersection;
        setReferencesOnRoadPath(false);
        pathfinding = new Dijkstra(lastIntersection, nextIntersection, this);
        state = newState;
        neededServices.clear();
        bordingServices.clear();

//        System.out.println("new Pathfinding startet: " + indexoflastIntersection + " - " + indexofNextIntersection);

    }

    void setNeededServices() {
        neededServices.clear();
        neededServices.add(AirplaneServices.crew);
        ParkGate gate = null;
        if (currentRoad instanceof ParkGate) {
            gate = (ParkGate) currentRoad;
        }

        if (category > 1) {
            if (gate != null){
                if (!gate.isConnectedRoadHasTerminal()){
                    //just need bus service, if the parkGate has no terminal
                    neededServices.add(AirplaneServices.bus);
                }
            }else{
                neededServices.add(AirplaneServices.bus);
            }
        }
        if (category > 2) {
            neededServices.add(AirplaneServices.tank);
            neededServices.add(AirplaneServices.baggage);
        }
        if (category > 3) {
            neededServices.add(AirplaneServices.catering);
            bordingServices.add(AirplaneServices.tank);
            bordingServices.add(AirplaneServices.baggage);
        }

        if (category > 2) {
            if (gate != null){
                if (!gate.isConnectedRoadHasTerminal()){
                    //just need bus service, if the parkGate has no terminal
                    neededServices.add(AirplaneServices.bus);
                }
            }else{
                neededServices.add(AirplaneServices.bus);
            }
            bordingServices.add(AirplaneServices.crew);
        }
    }

    public void completedService(AirplaneServices service) {
        if (neededServices.size() == 0 && bordingServices.size() == 0) return;

        if (neededServices.contains(service)) {
            neededServices.remove(service);
        } else {
            if (bordingServices.contains(service)) {
                bordingServices.remove(service);
                GameInstance.Instance().addMoney(category * 5L);
            }
        }
        if (neededServices.size() == 0 && bordingServices.size() == 0) {
            state = AirplaneState.ReadyForPushback;
        }
    }

    public AirplaneServices[] needsService() {
        if (isServiceNotPossible()) {
            return new AirplaneServices[0];
        }
        return neededServices.toArray(new AirplaneServices[neededServices.size()]);
    }

    public AirplaneServices[] needsBordingService() {
        if (isServiceNotPossible()) {
            return new AirplaneServices[0];
        }
        return bordingServices.toArray(new AirplaneServices[bordingServices.size()]);
    }

    public boolean isServiceNotPossible() {
        boolean isServiceNotPossible = true;
        if (currentRoad.getNext() != null) {
            for (int i = 0; i < currentRoad.getNext().getRoadArray().length; i++) {
                Road road = currentRoad.getNext().getRoadArray()[i];
                if (road instanceof Street) {
                    isServiceNotPossible = false;
                    break;
                }
            }
        }
        return isServiceNotPossible;
    }

    public void setState(AirplaneState state) {
        this.state = state;
    }

    public boolean isWaitingForInstructions() {
        if (holdPosition) return true;
        switch (state) {
            case Init:
                break;
            case Waiting:
                return true;
            case Boarding:
                break;
            case ReadyForPushback:
                return true;
            case TaxiToGate:
                break;
            case Pushback:
                break;
            case TaxiToRunway:
                break;
            case ReadyForDeparture:
                return true;
            case Landing:
                break;
            case WaitingForGate:
                return true;
            case ClearedForDeparture:
                break;
            case Takeoff:
                break;
            case Arrival:
                break;
            case Departure:
                break;
        }
        return false;
    }

    public AirplanePerformance getPerformance() {
        return (AirplanePerformance) performance;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public AirplaneState getState() {
        return state;
    }

    public float getAltitude() {
        return altitude;
    }

    public Airline getAirline() {
        return airline;
    }

    public boolean isHoldPosition() {
        return holdPosition;
    }

    public void setHoldPosition(boolean holdPosition) {
        this.holdPosition = holdPosition;
    }

    public int getCategory() {
        return category;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public TimeStamp getPlannedTime() {
        return plannedTime;
    }

    public void setPlannedTime(TimeStamp plannedTime) {
        this.plannedTime = plannedTime;
    }

    public void resetTurnaroundTime() {
        turnaroundTime = 0;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public int getMaxTurnaroundTime() {
        int fewRunwayExtra = 0;
        if (GameInstance.Airport().getRunwayCount() < 2) {
            fewRunwayExtra = 2000;
        } else if (GameInstance.Airport().getRunwayCount() == 2) {
            fewRunwayExtra = 1000;
        }
        int extraServices = 0;
        if (category > 2) {
            extraServices = 1000 * category;
        }

        return category * 3000 + Math.round(parkgateRunwayDistance / 1.2f) + fewRunwayExtra + extraServices;
    }
}

