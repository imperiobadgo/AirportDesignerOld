package com.pukekogames.airportdesigner.GameInstance;

import com.pukekogames.airportdesigner.Helper.CommonMethods;
import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Helper.GameLogic.GameplayWarning;
import com.pukekogames.airportdesigner.Helper.GameLogic.VehicleTask;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Helper.Pathfinding.ConnectionCheck;
import com.pukekogames.airportdesigner.Helper.Pathfinding.ConnectionMissing;
import com.pukekogames.airportdesigner.Helper.Pathfinding.Dijkstra;
import com.pukekogames.airportdesigner.Objects.*;
import com.pukekogames.airportdesigner.Objects.Airlines.Airline;
import com.pukekogames.airportdesigner.Objects.Buildings.Building;
import com.pukekogames.airportdesigner.Objects.Buildings.Depot;
import com.pukekogames.airportdesigner.Objects.Buildings.Tower;
import com.pukekogames.airportdesigner.Objects.Roads.ParkGate;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Roads.Runway;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;
import com.pukekogames.airportdesigner.Objects.Vehicles.StreetVehicle;
import com.pukekogames.airportdesigner.Objects.Vehicles.Vehicle;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplanePerformance;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplaneState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Marko Rapka on 25.03.2016.
 */
public class Airport implements Serializable {
    //    public static final boolean DEBUG = true;
    private static final int MAXVEHICLESATGATE = 2;
    private static final long serialVersionUID = -2197413813911692132L;
    public int airplaneCount = 0;
    public int airplanesToNextLevel = 0;
    private float windDirection = 120.0f;
    private CopyOnWriteArrayList<Runway> runways;
    private CopyOnWriteArrayList<Road> roads;
    private CopyOnWriteArrayList<RoadIntersection> roadIntersections;
    private CopyOnWriteArrayList<Airplane> airplanes;
    private CopyOnWriteArrayList<Vehicle> vehicles;
    private CopyOnWriteArrayList<Building> buildings;
    private Tower tower;

    transient private ArrayList<ConnectionMissing> connectionsMissing;
    boolean generatingNewChecks;
    transient private ArrayList<ConnectionCheck> connectionChecks;
    private ArrayList<VehicleTask> vehicleTasks;
    private ArrayList<Airplane> nextAirplanes;
    private int timeToNextAirplane = 100;
    private int timeToNextBus = 100;
    Random rand;
    private boolean pauseSimulation = false;
    private int removeCount = 0;


    private void ClearReferences() {
        removeCount++;
        if (removeCount > 40) {
            removeCount = 0;
            System.gc();//trigger GarbageCollector
        }
    }

    Airport() {
        rand = new Random();
//        renderObjects = new LinkedList<GameObject>();
//        selectedRoadIntersections = new LinkedList<RoadIntersection>();
//        planeInstruction = new ArrayList<Airplane>();

        runways = new CopyOnWriteArrayList<>();
        roads = new CopyOnWriteArrayList<>();
        roadIntersections = new CopyOnWriteArrayList<>();
        airplanes = new CopyOnWriteArrayList<>();
        vehicles = new CopyOnWriteArrayList<>();
        buildings = new CopyOnWriteArrayList<>();
//        vehicleNeededs = new CopyOnWriteArrayList<>();
        connectionsMissing = new ArrayList<>();
        vehicleTasks = new ArrayList<>();
        nextAirplanes = new ArrayList<>();
        tower = null;
    }

//    public static Airport Instance() {
//        return ourInstance;
//    }
//
//    static void setAirport(Airport airport) {
//        ourInstance = airport;
//    }

//    public void loadImages(){
//        for (Runway runway: runways){
//            Handler.setObjectImage(runway);
//            Image middle = ImageLoader.Instance().getImage(ImageLoader.indexRunwayMiddle);
//            runway.setMiddleImage(middle);
//        }
//        for (GameObject object: roads){
//            Handler.setObjectImage(object);
//        }
//        for (GameObject object: airplanes){
//            Handler.setObjectImage(object);
//        }
//        for (GameObject object: vehicles){
//            Handler.setObjectImage(object);
//        }
//        Handler.setObjectImage(windIndicator);
//    }

    public void CheckGateServicePossibility() {
        generatingNewChecks = true;
        if (connectionChecks == null) {
            connectionChecks = new ArrayList<>();
        }
        if (connectionsMissing == null) {
            connectionsMissing = new ArrayList<>();
        }
        connectionChecks.clear();
        connectionsMissing.clear();
        boolean firstLoop = true;

        for (Building building : buildings) {
            Depot depot;
            if (building instanceof Depot) {
                depot = (Depot) building;
            } else {
                continue;
            }

            for (ParkGate gate : getAllGates()) {
                if (connectionChecks == null) {
                    //when another thread is killing this process
                    generatingNewChecks = false;
                    return;
                }
                if (firstLoop) {
                    gate.checkForTerminal();
                }
                Dijkstra search = new Dijkstra(depot.getRoad().getNext(), gate.getNext());
                ConnectionCheck newCheck = new ConnectionCheck(search, depot, gate, depot.getRoad().getNext(), gate.getNext());
                connectionChecks.add(newCheck);
            }
            firstLoop = false;
        }
        generatingNewChecks = false;
    }

    public void AddRoad(Road road) {
//        Handler.setObjectImage(road);
        RoadIntersection inter = road.getLast();
        if (inter != null) {
            inter.addRoad(road);
        }
        inter = road.getNext();
        if (inter != null) {
            inter.addRoad(road);
        }
        road.updatePosition();
        roads.add(road);
        if (road instanceof Runway) {
            Runway runway = (Runway) road;
            runways.add(runway);
        }
        GameInstance.Settings().shouldUpdateRoadMap = true;
    }

    public void AddRoadIntersection(RoadIntersection roadIntersection) {
        roadIntersections.add(roadIntersection);
        GameInstance.Settings().shouldUpdateRoadMap = true;

    }

    public RoadIntersection getRoadIntersection(int index) {
        if (index < 0 || index > roadIntersections.size() - 1) return null;
        return roadIntersections.get(index);
    }

    public void removeRoad(Road road, RoadIntersection intersection) {
        if (road instanceof Runway) {
            Runway runway = (Runway) road;
            runways.remove(runway);
        }
        roads.remove(road);

//        Log.i(Game.TAG, "Removed road: " + road.getCenterPosition().toString());
        if (intersection == null) {
            road.getLast().removeRoad(road);
            road.getNext().removeRoad(road);
        } else {
            if (intersection.equals(road.getLast())) {
                road.getNext().removeRoad(road);
            } else if (intersection.equals(road.getNext())) {
                road.getLast().removeRoad(road);
            }
        }
        removeEmptyRoadIntersections();
        //TODO: tell every vehicle that roads have changed!
        for (Vehicle vehicleOnRoad : road.getVehiclesOnRoad()) {
            vehicleOnRoad.setRoad(null);
        }
        GameInstance.Settings().shouldUpdateRoadMap = true;
        ClearReferences();
    }

    private void removeEmptyRoadIntersections() {
        CopyOnWriteArrayList<RoadIntersection> newList = new CopyOnWriteArrayList<RoadIntersection>();
        for (RoadIntersection intersection : roadIntersections) {
            if (intersection.getRoadArray().length > 0) {
                newList.add(intersection);
            } else {
//                Log.i(Game.TAG, "Coll: Removed roadIntersection: " + intersection.getPosition().toString());
            }
        }
        roadIntersections = newList;
        GameInstance.Settings().shouldUpdateRoadMap = true;
    }

    public void removeRoadIntersection(RoadIntersection roadIntersection) {
//        Log.i(Game.TAG, "Removed roadIntersection. Found  " + roadIntersection.getRoadArray().length + " roads.");
        for (int i = 0; i < roadIntersection.getRoadArray().length; i++) {
            Road road = roadIntersection.getRoadArray()[i];
            removeRoad(road, roadIntersection);
//            Log.i(Game.TAG, "Removed roadIntersection. removed  " + i + ". road.");
        }
        roadIntersections.remove(roadIntersection);
//        Log.i(Game.TAG, "Removed roadIntersection: " + roadIntersection.getPosition().toString());
        //TODO: tell every vehicle that roads have changed!
        ClearReferences();
    }


    public Road getRoad(int index) {
        if (index < 0 || index > roads.size() - 1) return null;
        return roads.get(index);
    }

    public Runway getRunway(int index) {
        if (index < 0 || index > runways.size() - 1) return null;
        return runways.get(index);
    }

    public int getRunwayCount() {
        return runways.size();
    }

    public void AddVehicle(Vehicle vehicle) {
//        Handler.setObjectImage(vehicle);
        if (vehicle instanceof Airplane) {
            Airplane airplane = (Airplane) vehicle;
            airplanes.add(airplane);
        }
        vehicles.add(vehicle);
    }

    public void RemoveVehicle(Vehicle vehicle) {
        Road currentRoad = vehicle.getCurrentRoad();
        if (vehicle instanceof Airplane) {
            Airplane airplane = (Airplane) vehicle;
            GameInstance.Instance().AddMessage("Passed " + airplane.getCallSign() + " to radar.");
            if (airplane.getAirline() != null) {
                GameInstance.AirlineManager().AddFinishedVehicle(airplane);
            }
            airplaneCount += 1;
            airplanesToNextLevel += 1;
            if (airplanesToNextLevel % (3 + (4 * GameInstance.Settings().level)) == 0) {
                GameInstance.Settings().level += 1;
                GameInstance.Instance().AddMessage("Reached level " + GameInstance.Settings().level + "!");
                airplanesToNextLevel = 0;


                if (rand.nextInt(5) == 2 || GameInstance.Settings().level == 3) {
                    GameInstance.AirlineManager().AddNewAirline();
                } else {
                    for (int i = 0; i < GameInstance.AirlineManager().AirlinesCount(); i++) {
                        Airline airline = GameInstance.AirlineManager().getAirline(i);
                        int count = rand.nextInt(2) + rand.nextInt(2) + rand.nextInt(2);
                        for (int j = 0; j < count; j++) {
                            airline.addNewPlannedArrival();
                        }
                    }
                }
            }

            for (Runway runway : runways) {
                runway.removeAirplaneBlockingRunway(airplane);
                runway.removeAirplaneForDeparture(airplane);
            }

            airplanes.remove(airplane);
        } else if (vehicle instanceof StreetVehicle) {
            StreetVehicle streetVehicle = (StreetVehicle) vehicle;
            Depot homeDepot = streetVehicle.getHomeDepot();
            homeDepot.vehicleReachedDepot(streetVehicle);
            streetVehicle.clearVehicle();
        }
        for (Road tempRoad : vehicle.getNextRoads()) {
            if (tempRoad instanceof ParkGate) tempRoad.setBlocked(false);
            tempRoad.removeVehicle(vehicle);
        }

        currentRoad.removeVehicle(vehicle);
        currentRoad.setBlocked(false);
        vehicles.remove(vehicle);
        ClearReferences();
    }

    public Vehicle getVehicle(int index) {
        if (index < 0 || index > vehicles.size() - 1) return null;
        return vehicles.get(index);
    }

    public int getVehicleCount() {
        return vehicles.size();
    }

    public Airplane getAirplane(int index) {
        if (index < 0 || index > airplanes.size() - 1) return null;
        return airplanes.get(index);
    }

    public int getAirplaneCount() {
        return airplanes.size();
    }

    private void updateConnectionCheck() {
        if (generatingNewChecks) return;
        ArrayList<ConnectionCheck> removeChecks = new ArrayList<>();//remove checks from connectionChecklist when search completed
        for (ConnectionCheck check : connectionChecks) {
            if (generatingNewChecks) break;
            Dijkstra search = check.getDijkstra();
            search.nextStep();
            if (search.hasPathFound()) {

                ParkGate gate = check.getGate();
                if (gate.connectedServices == null) {
                    gate.connectedServices = new ArrayList<>();
                }
                gate.connectedServices.add(check.getDepotForSearch().getService());

                removeChecks.add(check);
            }
            if (search.hasFailed()) {
                ConnectionMissing missing = new ConnectionMissing(check.getStart(), check.getTarget(), check.getGate(), check.getDepotForSearch().getService());

                boolean addMissingConnection = true;

                //check, whether the gate has the service already
                ParkGate gate = missing.getGate();
                if (gate.connectedServices != null) {
                    if (gate.connectedServices.contains(missing.getService())) {
                        addMissingConnection = false;
                    }
                }

                if (addMissingConnection) {
                    connectionsMissing.add(missing);
                    removeChecks.add(check);
                }
            }
            if (generatingNewChecks) break;
        }

        for (ConnectionCheck removeCheck : removeChecks) {
            if (generatingNewChecks) break;
            connectionChecks.remove(removeCheck);
        }
        if (connectionChecks.size() == 0) {
            connectionChecks = null;
        }
    }

    public void tick() {
        if (pauseSimulation) return;

        if (connectionChecks != null && connectionsMissing != null) {
            updateConnectionCheck();
        }

        float angleChange = 0.05f - (0.01f * (GameInstance.Settings().level));
        if (angleChange < 0.01f) {
            angleChange = 0.01f;
        }
        windDirection = (windDirection + angleChange) % 360;

        if (timeToNextAirplane < 0) {

            if (getAmountOfFreeGates() > 0) {
                if (newAirplane())
                    timeToNextAirplane = rand.nextInt(100) + 50;
            }

        } else {
            if (timeToNextAirplane > -5) timeToNextAirplane--;
        }

        for (Road road : roads) {
            road.tick();
        }

        for (Building building : buildings) {
            building.tick();
        }


        for (Airplane airplane : airplanes) {

            if (airplane.isWaitingForInstructions() && tower != null) {
                tower.InstructAirplane(airplane);
            }

            AirplaneServices[] services = airplane.needsService();
            if (services.length > 0) {
                NeedVehicle(airplane, services);
            }else{
                AirplaneServices[] bordingServices = airplane.needsBordingService();
                if (bordingServices.length > 0){
                    NeedVehicle(airplane, bordingServices);
                }
            }
        }
        ArrayList<Vehicle> removeVehicles = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            if (Math.abs(vehicle.getCenterPos().x) > 50000 || Math.abs(vehicle.getCenterPos().y) > 50000) {
                removeVehicles.add(vehicle);
                continue;
            }
            vehicle.tick();
        }

        for (Vehicle removeVehicle : removeVehicles) {
            RemoveVehicle(removeVehicle);
        }

    }

    public int getAmountOfFreeGates() {
        return getAllGates().size() - (airplanes.size() - getAirplanesOnDepartureCount());
    }

    private int getAirplanesOnDepartureCount() {
        int count = 0;
        for (Airplane airplane : airplanes) {
            if (airplane.getState() == AirplaneState.TaxiToRunway || airplane.getState() == AirplaneState.ReadyForDeparture || airplane.getState() == AirplaneState.ClearedForDeparture ||
                    airplane.getState() == AirplaneState.Takeoff || airplane.getState() == AirplaneState.Departure) {

                count += 1;

            }
        }
        return count;
    }

    private RoadIntersection getIntersectionOfNextFreeRunway() {
        ArrayList<RoadIntersection> possibleIntersection = getIntersectionsOfAllFreeRunways();
        RoadIntersection intersection = null;
        if (possibleIntersection.size() > 0) {
            intersection = possibleIntersection.get(rand.nextInt(possibleIntersection.size()));
        }
        return intersection;
    }

    public RoadIntersection getIntersectionOfNextPossibleRunway() {
        ArrayList<RoadIntersection> possibleIntersection = getIntersectionsOfAllPossibleRunways();
        RoadIntersection intersection = null;
        if (possibleIntersection.size() > 0) {
            intersection = possibleIntersection.get(rand.nextInt(possibleIntersection.size()));
        }
        return intersection;
    }

    public ArrayList<RoadIntersection> getIntersectionsOfAllPossibleRunways() {
        ArrayList<RoadIntersection> possibleIntersection = new ArrayList<RoadIntersection>();

        for (Runway runway : runways) {
            RoadIntersection intersection = CommonMethods.getUsedRunwayIntersection(runway, windDirection);
            if (intersection != null) {
                possibleIntersection.add(intersection);
            }

        }
        return possibleIntersection;
    }

    private ArrayList<RoadIntersection> getIntersectionsOfAllFreeRunways() {
        ArrayList<RoadIntersection> possibleIntersection = new ArrayList<RoadIntersection>();

        for (Runway runway : runways) {
            if (runway.isBlocked() || runway.isBlockedForDeparture()) continue;

            RoadIntersection intersection = CommonMethods.getUsedRunwayIntersection(runway, windDirection);
            if (intersection != null) {
                possibleIntersection.add(intersection);
            }

        }
        return possibleIntersection;
    }

    public RoadIntersection getIntersectionOfNextFreeGate(boolean needTerminal) {
        ParkGate gate = null;
        ArrayList<ParkGate> freeGates = getAllFreeGates(needTerminal);
        if (freeGates.size() > 0) {
            gate = freeGates.get(rand.nextInt(freeGates.size()));
        } else if (!needTerminal) {
            freeGates = getAllFreeGates(true);
            if (freeGates.size() > 0) {
                gate = freeGates.get(rand.nextInt(freeGates.size()));
            }
        }

        if (gate == null) return null;
        return gate.getNext();
    }

    public ArrayList<ParkGate> getAllFreeGates(boolean needTerminal) {
        ArrayList<ParkGate> freeGates = new ArrayList<ParkGate>();
        for (Road road : roads) {
            if (road instanceof ParkGate) {
                ParkGate tempGate = (ParkGate) road;
                if (!tempGate.isBlocked() && tempGate.getVehiclesOnRoad().size() == 0) {

                    if (needTerminal) {
                        if (tempGate.isConnectedRoadHasTerminal()) {
                            freeGates.add(tempGate);
                        }
                    } else {
                        if (!tempGate.isConnectedRoadHasTerminal()) {
                            freeGates.add(tempGate);
                        }
                    }
                }
            }
        }
        return freeGates;
    }

    public ArrayList<ParkGate> getAllGates() {
        ArrayList<ParkGate> freeGates = new ArrayList<ParkGate>();
        for (Road road : roads) {
            if (road instanceof ParkGate) {
                ParkGate tempGate = (ParkGate) road;

                freeGates.add(tempGate);

            }
        }
        return freeGates;
    }

    public Integer getRandomIntersection(int currentIndex) {
        if (0 > currentIndex && currentIndex > roadIntersections.size()) return null;
        int nextRoadIndex = -1;
        while (nextRoadIndex == currentIndex || nextRoadIndex == -1)
            nextRoadIndex = rand.nextInt(roadIntersections.size());
        return nextRoadIndex;
    }

    public Runway getConnectedRunway(RoadIntersection intersection) {
        if (intersection == null) return null;
        Runway runway = null;
        for (Road road : intersection.getRoadArray()) {
            if (road == null) continue;
            if (road instanceof Runway) {
                runway = (Runway) road;
                break;
            }
        }
        return runway;
    }

    public int getRoadIntersectionCount() {
        return roadIntersections.size();
    }

    public int getRoadCount() {
        return roads.size();
    }

    public int getIndexInRoadIntersectionArray(RoadIntersection intersection) {
        return roadIntersections.indexOf(intersection);
    }

    public void AddNextAirplane(Airplane airplane) {
        nextAirplanes.add(airplane);
    }

    public ArrayList<Airplane> getNextAirplanes() {
        return nextAirplanes;
    }

    public void AddAllNextAirplane(ArrayList<Airplane> airplanes) {
        if (nextAirplanes.size() < runways.size() * 2) nextAirplanes.addAll(airplanes);
    }

    private boolean newAirplane() {
        if (nextAirplanes.size() == 0) return false;
        RoadIntersection intersection = getIntersectionOfNextFreeRunway();
        if (intersection == null) return false;
        RoadIntersection otherIntersection = null;
        Runway runway = null;
        float heading = 270f;
        for (Road road : intersection.getRoadArray()) {
            if (road == null) continue;
            if (road instanceof Runway) {
                runway = (Runway) road;
                if (Math.sqrt(Math.pow(runway.getStartPosition().x - intersection.getPosition().x, 2) + Math.pow(runway.getStartPosition().y - intersection.getPosition().y, 2)) <
                        Math.sqrt(Math.pow(runway.getEndPosition().x - intersection.getPosition().x, 2) + Math.pow(runway.getEndPosition().y - intersection.getPosition().y, 2))) {
                    heading = runway.getHeading();
                } else {
                    heading = (runway.getHeading() + 180) % 360;
                }
                if (runway.getLast().equals(intersection)) {
                    otherIntersection = runway.getNext();
                } else {
                    otherIntersection = runway.getLast();
                }
                break;
            }
        }
        if (otherIntersection == null) return false;

        Airplane plane = nextAirplanes.get(0);

        if (getAllFreeGates(true).size() + getAllFreeGates(false).size() == 0) return false;
        nextAirplanes.remove(0);

        float dirX = (float) -Math.cos(Math.toRadians(heading));// other direction for spawning airplane towars the runway
        float dirY = (float) -Math.sin(Math.toRadians(heading));

        float endDistance = 16000f;
        float startAltitude = 3000;

        AirplanePerformance performance = plane.getPerformance();
        float speed = performance.maxSpeed; //(performance.maxSpeed + performance.landingSpeed) / 2;
        float timeForBreaking = speed * performance.deceleration;
        float breakDistance = 0.5f * performance.deceleration * timeForBreaking * timeForBreaking + speed;
        breakDistance = breakDistance * speed + 1500;
        endDistance = (startAltitude / performance.sinkrate) * speed + breakDistance;
        PointInt spawnpoint = new PointInt((int) (otherIntersection.getPosition().x + dirX * endDistance), (int) (otherIntersection.getPosition().y + dirY * endDistance));

        plane.setAlign_X(spawnpoint.x);
        plane.setAlign_Y(spawnpoint.y);
        runway.addAirplaneBlockingRunway(plane);
        plane.setHeading(heading);
        plane.setTargetPoint(otherIntersection.getPosition());
        plane.setAltitude(startAltitude);
        plane.setState(AirplaneState.Arrival);
        plane.setSpeed(plane.getPerformance().maxSpeed);
        plane.resetTurnaroundTime();
        AddVehicle(plane);
        plane.setRoad(runway);
        runway.addVehicle(plane);
        return true;
    }

    public Object[] getClickableObjects() {
        ArrayList<ClickableGameObject> returnList = new ArrayList<>();
        returnList.addAll(buildings);
        returnList.addAll(vehicles);
        returnList.addAll(roadIntersections);
        returnList.addAll(roads);
        return returnList.toArray();
    }

    public void clear() {
        roads.clear();
        runways.clear();
        roadIntersections.clear();
        airplanes.clear();
        vehicles.clear();
        buildings.clear();
    }

    public void setPauseSimulation(boolean pauseSimulation) {
        this.pauseSimulation = pauseSimulation;
    }

    public boolean isPauseSimulation() {
        return pauseSimulation;
    }


    public float getWindDirection() {
        return windDirection;
    }

    public void AddBuilding(Building building) {
        Road road = building.getRoad();
        road.setBuilding(building);
        buildings.add(building);
        if (building instanceof Tower) {
            tower = (Tower) building;
        }
    }

    public int getBuildingCount() {
        return buildings.size();
    }

    public Building getBuilding(int index) {
        if (index < 0 || index > buildings.size() - 1) return null;
        return buildings.get(index);
    }

    public void RemoveBuilding(Building building) {
        Road road = building.getRoad();
        road.setBuilding(null);
        if (building instanceof Tower) {
            tower = null;
        }
        for (Road lastRoad : road.getLast().getRoadArray()) {
            if (lastRoad instanceof ParkGate){
                ParkGate gate = (ParkGate) lastRoad;
                gate.checkForTerminal();
            }
        }
        for (Road nextRoad : road.getNext().getRoadArray()) {
            if (nextRoad instanceof ParkGate){
                ParkGate gate = (ParkGate) nextRoad;
                gate.checkForTerminal();
            }
        }

        buildings.remove(building);
    }

    public void NeedVehicle(Airplane airplane, AirplaneServices[] neededServices) {

        boolean foundDepot = false;
        int sameAirplaneTask = 0;
        if (!(airplane.getCurrentRoad() instanceof ParkGate)) {
            return;
        }
        ParkGate gate = (ParkGate) airplane.getCurrentRoad();

        if (connectionsMissing != null) {
            for (ConnectionMissing missing : connectionsMissing) {
                if (missing.getTarget().equals(gate.getNext())) {
                    return;
                }
            }
        }

        for (AirplaneServices neededService : neededServices) {
            boolean serviceAlready = false;

            for (VehicleTask vehicleTask : vehicleTasks) {
                if (vehicleTask.getAirplane().equals(airplane)) {
                    sameAirplaneTask += 1;

                    if (vehicleTask.getService() == neededService) {
                        serviceAlready = true;
                        break;

                    }
                }
            }
            if (serviceAlready) continue;
            if (sameAirplaneTask > MAXVEHICLESATGATE) break;

            for (Building building : buildings) {
                Depot depot;
                if (building instanceof Depot) {
                    depot = (Depot) building;
                } else {
                    continue;
                }

                if (depot.getService() == neededService) {

                    VehicleTask task = new VehicleTask(airplane, gate);
                    Vehicle newVehicle = depot.startVehicle(task);
                    if (newVehicle != null) { // if vehicle was send
                        vehicleTasks.add(task);
                        foundDepot = true;
                    }

                }
                if (foundDepot) break;
            }
            if (foundDepot) break;
        }

        if (!foundDepot) {
            airplane.addWarning(GameplayWarning.noServicesAvailable);
        }

    }

    public void removeVehicleTask(VehicleTask task) {
        if (vehicleTasks.contains(task)) {
            vehicleTasks.remove(task);
        }
    }

    public StreetVehicle getVehicleForAirplane(Airplane airplane) {
        for (VehicleTask vehicleTask : vehicleTasks) {
            if (vehicleTask.getAirplane().equals(airplane)) {
                return vehicleTask.getVehicle();
            }
        }
        return null;

    }

    public boolean isGeneratingNewChecks() {
        return generatingNewChecks;
    }

    public ArrayList<ConnectionMissing> getConnectionsMissing() {
        return connectionsMissing;
    }

    public Tower getTower() {
        return tower;
    }
}

