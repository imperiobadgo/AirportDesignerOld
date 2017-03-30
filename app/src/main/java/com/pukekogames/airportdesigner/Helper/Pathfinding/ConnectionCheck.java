package com.pukekogames.airportdesigner.Helper.Pathfinding;

import com.pukekogames.airportdesigner.Objects.Buildings.Depot;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Roads.ParkGate;

/**
 * Created by Marko Rapka on 31.12.2016.
 */
public class ConnectionCheck {
    Dijkstra dijkstra;
    Depot depotForSearch;
    ParkGate gate;
    RoadIntersection start;
    RoadIntersection target;

    public ConnectionCheck(Dijkstra dijkstra,Depot depot,ParkGate gate, RoadIntersection start, RoadIntersection target) {
        this.dijkstra = dijkstra;
        this.depotForSearch = depot;
        this.gate = gate;
        this.start = start;
        this.target = target;
    }

    public Dijkstra getDijkstra() {
        return dijkstra;
    }

    public Depot getDepotForSearch() {
        return depotForSearch;
    }

    public ParkGate getGate() {
        return gate;
    }

    public RoadIntersection getStart() {
        return start;
    }

    public RoadIntersection getTarget() {
        return target;
    }
}
