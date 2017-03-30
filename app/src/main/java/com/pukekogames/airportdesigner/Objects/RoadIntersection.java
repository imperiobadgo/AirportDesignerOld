package com.pukekogames.airportdesigner.Objects;

import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Helper.Geometry.PointFloat;
import com.pukekogames.airportdesigner.Objects.Roads.Road;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 04.04.2016.
 */
public class RoadIntersection extends ClickableGameObject {
    private static final long serialVersionUID = 94277148976366943L;
    private PointFloat position;
    transient ArrayList<Road> roads;//indeces of the roads in airport list
    int nextListIndex = 0;

    public RoadIntersection(PointFloat position) {
        super(Alignment.Table, position.x, position.y, 10, 10);
        this.position = new PointFloat(position.x, position.y);
        roads = new ArrayList<Road>();
    }

    public int getNextRoad(int currentIndex) {
        int nextindex = currentIndex;
        boolean foundNext = false;
        int count = 0;
        while (!foundNext && count < roads.size() + 5) {
            count++;
            if (nextListIndex < roads.size() - 1) {
                nextListIndex++;
            } else {
                nextListIndex = 0;
            }

            Road nextRoad = roads.get(nextListIndex);
            if (nextRoad == null) continue;
            if (nextRoad.getVehiclesOnRoad().size() > 0) {
                nextindex = currentIndex;
                continue;
            }
            foundNext = true;
        }
        if (nextindex != currentIndex) return nextindex;
        return -1;
    }

    public void addRoad(Road road) {
        roads.add(road);
    }

    public void setPosition(PointFloat position) {
        this.position = position;
    }

    public void setTablePosition(float newX, float newY){
        this.position.set(newX, newY);
    }

    public PointFloat getPosition() {
        return position;
    }

    public Road[] getRoadArray() {
        Road[] roadArray = new Road[roads.size()];
        for (int i = 0; i < roadArray.length; i++) {
            roadArray[i] = roads.get(i);
        }
        return roadArray;
    }

    public void removeRoad(Road road){
        roads.remove(road);
        System.gc();
    }

    @Override
    public void clicked(int mx, int my) {

    }

    @Override
    public void tick() {

    }

    public void customWriteObject(){
        if (roads == null){
            roads = new ArrayList<Road>();
        }
    }


}
