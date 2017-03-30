package com.pukekogames.airportdesigner.Helper.Pathfinding;

import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Roads.ParkGate;

/**
 * Created by Marko Rapka on 31.12.2016.
 */
public class ConnectionMissing {
    AirplaneServices service;
    ParkGate gate;
    RoadIntersection start;
    RoadIntersection target;

    public ConnectionMissing(RoadIntersection start, RoadIntersection target,ParkGate gate, AirplaneServices airplaneService) {
        this.start = start;
        this.target = target;
        this.service = airplaneService;
        this.gate = gate;
    }

    public RoadIntersection getStart() {
        return start;
    }

    public RoadIntersection getTarget() {
        return target;
    }

    public AirplaneServices getService() {
        return service;
    }

    public ParkGate getGate() {
        return gate;
    }
}
