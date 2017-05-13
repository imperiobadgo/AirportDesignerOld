package com.pukekogames.airportdesigner.Helper.Pathfinding;


import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Roads.Runway;
import com.pukekogames.airportdesigner.Objects.Roads.Street;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;
import com.pukekogames.airportdesigner.Objects.Vehicles.StreetVehicle;
import com.pukekogames.airportdesigner.Objects.Vehicles.Vehicle;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Marko Rapka on 07.04.2016.
 */
public class Dijkstra implements Serializable {
    private static final double blockedDistance = 3000;//distance added for each other vehicle on a road
    private static final double wrongDirectionDistance = 6000;//distance added for wrong directionInUse on the road
    private static final int maxAirplanesOnRoadAllowed = 50;
    private static final long serialVersionUID = 85992701899240581L;

    private boolean foundPath = false;
    private RoadIntersection startIntersection;
    private RoadIntersection targetIntersection;
    private int[] visited;
    private double[] distance;
    private RoadIntersection[] beforePoint;
    private ArrayList<RoadIntersection> nextNodes;
    private ArrayList<Road> shortestPath;
    private int stepCount;
    private Vehicle caller;

    public Dijkstra(RoadIntersection startIntersection, RoadIntersection targetIntersection, Vehicle caller) {
        this.startIntersection = startIntersection;
        this.targetIntersection = targetIntersection;
        this.caller = caller;

        Initialize();
    }

    public Dijkstra(RoadIntersection startIntersection, RoadIntersection targetIntersection) {
        //this constructor is called, to check, whether there is a connection
        this.startIntersection = startIntersection;
        this.targetIntersection = targetIntersection;
        this.caller = null;

        Initialize();
    }

    private void Initialize() {
        int intersectionCount = GameInstance.Airport().getRoadIntersectionCount();
        visited = new int[intersectionCount];
        distance = new double[intersectionCount];
        beforePoint = new RoadIntersection[intersectionCount];

        nextNodes = new ArrayList<RoadIntersection>();
        shortestPath = new ArrayList<Road>();
        //initialize
        for (int i = 0; i < distance.length; i++) {
            distance[i] = Double.MAX_VALUE;
        }
        int indexOfStartIntersection = GameInstance.Airport().getIndexInRoadIntersectionArray(startIntersection);
        if (indexOfStartIntersection >= 0 && indexOfStartIntersection < intersectionCount) {
            visited[indexOfStartIntersection] = 1;
            beforePoint[indexOfStartIntersection] = startIntersection;
            distance[indexOfStartIntersection] = 0;
            nextNodes.add(startIntersection);
        }
    }

    public Road[] searchShortestPath() {

        while (!foundPath) {
            nextStep();
        }

        Road[] returnPath = new Road[shortestPath.size()];
        for (int i = 0; i < shortestPath.size(); i++) {
            returnPath[i] = shortestPath.get(i);
        }
        return returnPath;
    }

    public boolean nextStep() {
        if (nextNodes.size() < 1) return false;
        stepCount++;
//        System.out.println("StepCount: " + stepCount);

        RoadIntersection currentIntersection = nextNodes.remove(0);
        int indexOfCurrentIntersection = GameInstance.Airport().getIndexInRoadIntersectionArray(currentIntersection);

        if (indexOfCurrentIntersection > visited.length - 1 || indexOfCurrentIntersection > distance.length - 1 || indexOfCurrentIntersection > beforePoint.length - 1) {
            return false;
        }
        visited[indexOfCurrentIntersection] = 1;
        if (currentIntersection.equals(targetIntersection)) {
            foundPath = true;
            collectPath();
            return true;
        }
        Road[] roadArray = currentIntersection.getRoadArray();
        for (Road road : roadArray) {
            if (road instanceof Runway) continue;//dont taxi on Runways
            if (caller != null) {
                if (caller instanceof Airplane) {
                    if (road instanceof Street) continue; //dont taxi on streets
                }
                if (caller instanceof StreetVehicle) {
                    if (!(road instanceof Street)) continue;//dont drive anywhere else than streets
                }
            } else {
                if (!(road instanceof Street)) continue;//dont drive anywhere else than streets
            }
            if (road.isRoadNotUseable()) continue;

            RoadIntersection oppositeIntersection = currentIntersection.equals(road.getLast()) ? road.getNext() : road.getLast();
            int indexOfOppositeIntersection = GameInstance.Airport().getIndexInRoadIntersectionArray(oppositeIntersection);
            if (indexOfOppositeIntersection > visited.length - 1 || indexOfOppositeIntersection > distance.length - 1 || indexOfOppositeIntersection > beforePoint.length - 1) {
                return false;
            }
            double newDistance = distance[indexOfCurrentIntersection] + road.getLength();
            if (distance[indexOfOppositeIntersection] > newDistance) {

                if (caller != null) {
                    if (caller instanceof StreetVehicle) {
                        ArrayList<Vehicle> vehiclesOnTheRoad = road.getVehiclesOnRoad();
                        int airplanesOnRoad = road.getVehiclesOnRoad().size();

                        if (vehiclesOnTheRoad.contains(caller)) {
                            //remove itself from the vehicleListCount
                            airplanesOnRoad--;
                        }
                        if (airplanesOnRoad > maxAirplanesOnRoadAllowed) continue;
                        distance[indexOfOppositeIntersection] = newDistance + blockedDistance * airplanesOnRoad;
                    } else {
                        boolean wrongDirection = false;
                        if (road.getLast().equals(currentIntersection) && road.getDirectionInUse() == 2) {
                            wrongDirection = true;
                        } else if (road.getNext().equals(currentIntersection) && road.getDirectionInUse() == 1) {
                            wrongDirection = true;
                        }
                        if (wrongDirection) {
                            distance[indexOfOppositeIntersection] = newDistance + wrongDirectionDistance;
                        } else {
                            distance[indexOfOppositeIntersection] = newDistance;
                        }

                    }
                }else{
                    distance[indexOfOppositeIntersection] = newDistance;//basic distance at connectionCheck
                }

                beforePoint[indexOfOppositeIntersection] = currentIntersection;
                if (visited[indexOfOppositeIntersection] == 0) {
                    insertNextNode(oppositeIntersection, newDistance); //only add to nextnodes if not already visited
                }

                if (oppositeIntersection.equals(targetIntersection)) {
                    foundPath = true;
                    collectPath();
                    break;
                }
            }

        }

        return foundPath;
    }

    private void insertNextNode(RoadIntersection intersection, double newDistance) {
        if (nextNodes.size() == 0) {
            nextNodes.add(intersection);
            return;
        }
        boolean added = false;
        for (int i = 0; i < nextNodes.size(); i++) {
            RoadIntersection nodeIntersection = nextNodes.get(i);
            int index = GameInstance.Airport().getIndexInRoadIntersectionArray(nodeIntersection);
            if (distance[index] > newDistance) {
                nextNodes.add(i, intersection);
                added = true;
                break;
            }
        }
        if (!added) nextNodes.add(intersection);
    }

    private void collectPath() {//return roadsindices
        shortestPath.clear();
        ArrayList<RoadIntersection> intersection = new ArrayList<RoadIntersection>();
        RoadIntersection currentIntersection = targetIntersection;
        if (currentIntersection == null || startIntersection == null){
            //target or start was removed
            foundPath = false;
            return;
        }
        int indexOfCurrentIntersection = GameInstance.Airport().getIndexInRoadIntersectionArray(currentIntersection);
        int zahler = 0;
        while (!currentIntersection.equals(startIntersection) && zahler < beforePoint.length) {
            intersection.add(0, currentIntersection);
            currentIntersection = beforePoint[indexOfCurrentIntersection];
            indexOfCurrentIntersection = GameInstance.Airport().getIndexInRoadIntersectionArray(currentIntersection);
            zahler++;
        }
        intersection.add(0, startIntersection);

        RoadIntersection nextIntersection;
        for (int i = 0; i < intersection.size() - 1; i++) {
            currentIntersection = intersection.get(i);
            nextIntersection = intersection.get(i + 1);
            RoadIntersection currentRoadIntersection = currentIntersection;
            RoadIntersection nextRoadIntersection = nextIntersection;
            //search for same roadIndex
            search:
            for (int j = 0; j < currentRoadIntersection.getRoadArray().length; j++) {
                for (int k = 0; k < nextRoadIntersection.getRoadArray().length; k++) {
                    if (currentRoadIntersection.getRoadArray()[j].equals(nextRoadIntersection.getRoadArray()[k])) {
                        Road nextRoad = currentRoadIntersection.getRoadArray()[j];
                        shortestPath.add(nextRoad);
                        break search;
                    }
                }
            }
        }
    }

    public int[] getVisited() {
        return visited;
    }

    public ArrayList<Road> getShortestPath() {
        return shortestPath;
    }

    public boolean hasPathFound() {
        return foundPath;
    }

    public boolean hasFailed() {
        return nextNodes.size() == 0 && !foundPath;
    }
}
