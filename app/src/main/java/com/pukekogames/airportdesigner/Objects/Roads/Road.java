package com.pukekogames.airportdesigner.Objects.Roads;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Helper.Geometry.PointFloat;
import com.pukekogames.airportdesigner.Objects.Buildings.Building;
import com.pukekogames.airportdesigner.Objects.ClickableGameObject;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Vehicles.Vehicle;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 23.03.2016.
 */
public abstract class Road extends ClickableGameObject {
    private static final long serialVersionUID = 5033622864599222130L;
    PointFloat startPos = new PointFloat();
    PointFloat endPos = new PointFloat();
    PointFloat centerPos = new PointFloat();
    float length;
    int references;
    transient ArrayList<Vehicle> vehiclesOnRoad;
    Building building;
    float dirX;
    float dirY;

    int directionInUse = 0; //no vehicle, 1 in roadDirection, 2 opposite roadDirection
    boolean userWantsDemolition = false;


    RoadIntersection last = null;
    RoadIntersection next = null;
    boolean showDirection = false;
    boolean blocked;

    public Road() {
        super(Alignment.Table, 0, 0, 10, 10);
        length = 0;
        heading = 0;
        vehiclesOnRoad = new ArrayList<Vehicle>();
    }

    public void updatePosition() {
        if (last != null && next != null) {
            connectPositions(last.getPosition(), next.getPosition());
        }
    }

    public void connectPositions(PointFloat p1, PointFloat p2) {
        double diffX = p2.x - p1.x;
        double diffY = p2.y - p1.y;
        Align_X = (float) (p1.x + diffX / 2);
        Align_Y = (float) (p1.y + diffY / 2);
        float length = (float) Math.sqrt(diffX * diffX + diffY * diffY);
        float heading = (float) Math.toDegrees(Math.atan2(diffY, diffX));
        calculateNewDirection(heading, length);
    }

    public void calculateNewDirection(float heading, float length) {
        heading = heading % 360;//prevent overflowing (when greater then 360, starts again at 0)
        this.heading = heading;
        this.length = length;
        dirX = (float) Math.cos(Math.toRadians(heading));
        dirY = (float) Math.sin(Math.toRadians(heading));
//        Align_X = Align_X + dirX / 2;
//        Align_Y = Align_Y + dirY / 2;
//        endPos = new PointFloat.Float(x + dirX, y + dirY);
        calculatePositions();
    }

    public void calculatePositions() {
//        float centerX = x + length / 2;
//        float centerY = y + height / 2;
        centerPos.set(Align_X, Align_Y);

        //rotate startPoint around centerPoint
//        startPos.x = centerPos.x - length / 2;
//        startPos.y = centerPos.y;
//        double x1 = startPos.x - centerPos.x;
//        double y1 = startPos.y - centerPos.y;
//
//        double x2 = x1 * Math.cos(Math.toRadians(heading)) - y1 * Math.sin(Math.toRadians(heading));
//        double y2 = x1 * Math.sin(Math.toRadians(heading)) + y1 * Math.cos(Math.toRadians(heading));

        startPos.x = centerPos.x - dirX * length / 2;
        startPos.y = centerPos.y - dirY * length / 2;

        //rotate endPoint around centerPoint
//        endPos.x = centerPos.x - length / 2;
//        endPos.y = centerPos.y;
//        double x1end = endPos.x - centerPos.x;
//        double y1end = endPos.y - centerPos.y;
//
//        double x2end = x1end * Math.cos(Math.toRadians(heading)) - y1end * Math.sin(Math.toRadians(heading));
//        double y2end = x1end * Math.sin(Math.toRadians(heading)) + y1end * Math.cos(Math.toRadians(heading));

        endPos.x = centerPos.x + dirX * length / 2;
        endPos.y = centerPos.y + dirY * length / 2;

        setPosition(Align_X, Align_Y);
        updateDimensions();
    }

    @Override
    public void setDimension(float width, float height) {
        super.setDimension(width, height);
        calculatePositions();
    }

    public void updateDimensions() {
        width = length;
    }

    @Override
    public void tick() {
        if (userWantsDemolition){
            if (vehiclesOnRoad.size() == 0 && !blocked){
                GameInstance.Airport().removeRoad(this, null);
            }
        }
    }

    public PointFloat getCenterPosition() {
        return centerPos;
    }

    public PointFloat getStartPosition() {
        return startPos;
    }

    public PointFloat getEndPosition() {
        return endPos;
    }


    public RoadIntersection getLast() {
        return last;
    }

    public void setLast(RoadIntersection last) {
        this.last = last;
    }

    public RoadIntersection getNext() {
        return next;
    }

    public void setNext(RoadIntersection next) {
        this.next = next;
    }

    public float getLength() {return length;}
    public ArrayList<Vehicle> getVehiclesOnRoad(){return vehiclesOnRoad;}

    public void addVehicle(Vehicle vehicle){
        vehiclesOnRoad.add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle){
        vehiclesOnRoad.remove(vehicle);
        if (vehiclesOnRoad.size() == 0){
            directionInUse = 0;
        }
    }

    public void setShowDirection(boolean show){showDirection = show;}

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        if (!blocked && this instanceof ParkGate){
            ((ParkGate) this).resetVehiclesServicing();
        }
        this.blocked = blocked;
    }

    public boolean isShowDirection() {
        return showDirection;
    }

    public float getDirX() {
        return dirX;
    }

    public float getDirY() {
        return dirY;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public int getDirectionInUse() {
        return directionInUse;
    }

    public void setDirectionInUse(int directionInUse) {
        if (this.directionInUse == 0 && !(this instanceof Street)){
            this.directionInUse = directionInUse;
        }
    }

    public boolean isRoadNotUseable(){
        return userWantsDemolition;
    }

    public void toggleUserWantsDemolition() {
        userWantsDemolition = !userWantsDemolition;
    }

    public void setUserWantsDemolition(boolean userWantsDemolition) {
        this.userWantsDemolition = userWantsDemolition;
    }

    public boolean isUserWantsDemolition() {
        return userWantsDemolition;
    }

    public void customWriteObject(){
        vehiclesOnRoad = new ArrayList<Vehicle>();
    }


}
