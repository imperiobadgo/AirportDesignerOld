package com.pukekogames.airportdesigner.Objects.Roads;

import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 21.03.2016.
 */
public class Runway extends Road{

    private static final long serialVersionUID = 4307441291500202286L;
    private int middle;
    private ArrayList<Airplane> airplanesForDeparture;
    private ArrayList<Airplane> airplanesBlockRunway;
    private int[] exits = new int[6];
    private float enddistanz = 200;
    private int waitBlock = 0;

    public Runway() {
        super();
        middle = Images.indexRunwayMiddle;
        setImageID(Images.indexRunwayEnd);

        airplanesForDeparture = new ArrayList<>();
        airplanesBlockRunway = new ArrayList<>();
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

    public int getMiddleID() {
        return middle;
    }

    @Override
    public boolean isBlocked(){
        return airplanesBlockRunway.size() > 0;
    }

    public boolean isBlockedForDeparture() {
        return airplanesForDeparture.size() > 0;
    }

    public int getPositionInDepartingAirplanes(Airplane airplane){
        return airplanesForDeparture.indexOf(airplane);
    }

    public void addAirplaneForDeparture(Airplane airplane) {
        airplanesForDeparture.add(airplane);
    }

    public void removeAirplaneForDeparture(Airplane airplane){
        airplanesForDeparture.remove(airplane);
    }

    public void addAirplaneBlockingRunway(Airplane airplane) {
        airplanesBlockRunway.add(airplane);
    }

    public void removeAirplaneBlockingRunway(Airplane airplane){
        airplanesBlockRunway.remove(airplane);
    }
}
