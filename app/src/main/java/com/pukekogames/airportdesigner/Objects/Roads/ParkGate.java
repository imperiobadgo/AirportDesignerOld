package com.pukekogames.airportdesigner.Objects.Roads;

import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Objects.Buildings.Building;
import com.pukekogames.airportdesigner.Objects.Buildings.Terminal;
import com.pukekogames.airportdesigner.Objects.Images;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 07.04.2016.
 */
public class ParkGate extends Road {

    private static final long serialVersionUID = -6707035370180680973L;
    private int waitBlock = 0;
    transient public ArrayList<AirplaneServices> connectedServices;
    private boolean connectedRoadHasTerminal;


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


}
