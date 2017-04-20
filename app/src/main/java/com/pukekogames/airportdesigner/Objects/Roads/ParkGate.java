package com.pukekogames.airportdesigner.Objects.Roads;

import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Helper.Geometry.PointFloat;
import com.pukekogames.airportdesigner.Helper.Geometry.Vector2D;
import com.pukekogames.airportdesigner.Objects.Buildings.Building;
import com.pukekogames.airportdesigner.Objects.Buildings.Terminal;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.Vehicles.StreetVehicle;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 07.04.2016.
 */
public class ParkGate extends Road {

    private static final long serialVersionUID = -6707035370180680973L;
    private int waitBlock = 0;
    transient public ArrayList<AirplaneServices> connectedServices;
    private boolean connectedRoadHasTerminal;
    private int vehiclesServices;


    public ParkGate() {
        super();
        setImageID(Images.indexParkGate);
    }

    @Override
    public void clicked(int mx, int my) {

    }

    @Override
    public void tick() {
        super.tick();
//        if (vehiclesOnRoad.size() == 0){
//            waitBlock++;
//            if (waitBlock > 100){//reset when no airplane is for some time on the parkgate
//                waitBlock = 0;
//                blocked = false;
//            }
//        }else{
//            waitBlock = 0;
//            blocked = true;
//        }

    }

    public boolean isConnectedRoadHasTerminal() {
        return connectedRoadHasTerminal;
    }

    public void checkForTerminal(){
        connectedRoadHasTerminal = false;
        for (Road road: next.getRoadArray()){
            if (road == this){
                continue;
            }
            if (road.getBuilding() != null){
                Building building = road.getBuilding();
                if (building instanceof Terminal){
                    connectedRoadHasTerminal = true;
                    break;
                }
            }

        }

    }

    public PointFloat getCornerPosition(int number){

        float factor = 2.5f;
        Vector2D perp = new Vector2D(dirY * length / factor, - dirX * length / factor);
//        System.out.println("perp: " + perp.getX() + " " + perp.getY() + " length: " + perp.Length() + " center: " + centerPos.x + " " + centerPos.y);
        //perp.Normalize();

        switch(number){
            case 1:
                return new PointFloat(startPos.x + dirX * length * 0.9f + perp.getX() * 0.6f, startPos.y + dirY * length * 0.9f + perp.getY() * 0.6f);
            case 2:
                return new PointFloat(centerPos.x + perp.getX() * 0.9f, centerPos.y + perp.getY() * 0.9f);
            case 3:
                return new PointFloat(startPos.x + dirX * length * 0.15f + perp.getX() * 0.4f, startPos.y + dirY * length * 0.15f + perp.getY() * 0.4f);
            case 4:
                return new PointFloat(startPos.x + dirX * length * 0.2f - perp.getX() * 0.5f, startPos.y + dirY * length * 0.2f - perp.getY() * 0.5f);
            case 5:
                return new PointFloat(centerPos.x - perp.getX(), centerPos.y - perp.getY());
            case 6:
                return new PointFloat(startPos.x + dirX * length * 0.9f - perp.getX() * 0.4f, startPos.y + dirY * length * 0.9f - perp.getY()* 0.4f);
            default:
                return endPos;
        }

    }

    public void addVehicle(StreetVehicle vehicle){
        vehiclesServices++;
    }

    public void removeVehicle(StreetVehicle vehicle){
        vehiclesServices--;
    }

    public void resetVehiclesServicing(){
        vehiclesServices = 0;
    }

    public int getEntryNumber(){
        return 1;
    }

    public int getLeavePosition(){
        return 6;
    }

    public boolean isServicePosition(int number){
        if (vehiclesServices > 1){
            return number == 2;
        }else{
            return number == 5;
        }
    }

    public boolean isLeavePosition(int number){
        return number == 6;
    }


}
