package com.pukekogames.airportdesigner.Objects.Buildings;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Helper.GameLogic.VehicleTask;
import com.pukekogames.airportdesigner.Objects.Roads.ParkGate;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Vehicles.StreetVehicle;
import com.pukekogames.airportdesigner.Objects.Vehicles.Vehicle;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 25.08.2016.
 */
public abstract class Depot extends Building {


    private static final long serialVersionUID = 5114466060996682414L;
    transient ArrayList<StreetVehicle> vehiclesOnTheRoad; //all Vehicles started from this depot
    ArrayList<ParkGate> parkGates;
    int lastVehicleStartet;
    int pauseTime = 200;

    int capacity;

    public Depot(Road road) {
        super(road);


        vehiclesOnTheRoad = new ArrayList<>();
        capacity = 5;
    }

    @Override
    public void tick() {
        if (lastVehicleStartet > 0) lastVehicleStartet --;
        super.tick();
    }

    @Override
    protected boolean canRemoveBuilding() {
        return super.canRemoveBuilding() && vehiclesOnTheRoad.size() == 0;
    }

    public Vehicle startVehicle(VehicleTask task){
        if (!canStartVehicle()) {
            return null;
        }
        StreetVehicle vehicle = createVehicle();
        vehicle.setHomeDepot(this);
        task.setVehicle(vehicle, getService());
        vehicle.setTask(task);
        vehicle.setHeading((heading + 180) % 360);
        vehicle.setRoad(road);
        vehicle.searchRoute(task.getParkIntersection());
        vehiclesOnTheRoad.add(vehicle);
        GameInstance.Airport().AddVehicle(vehicle);

        lastVehicleStartet = pauseTime;
        if (vehiclesOnTheRoad.size() == capacity) {
            GameInstance.Instance().AddMessage(getService().toString() + " depot has reached its capacity!");
        }
        return vehicle;
    }
    public abstract long getStartCosts();
    abstract StreetVehicle createVehicle();

    boolean canStartVehicle() {
        boolean canstart = !userWantsDemolition && vehiclesOnTheRoad.size() < capacity;
        if (lastVehicleStartet > 0) return false;
        return canstart && GameInstance.Instance().removeMoney(getStartCosts());

    }

    public void vehicleReachedDepot(Vehicle vehicle){

        vehiclesOnTheRoad.remove(vehicle);

//        GameInstance.Airport().RemoveVehicle(vehicle);
    }

    public void removeVehicleJustFromList(Vehicle vehicle){
        vehiclesOnTheRoad.remove(vehicle);
    }

    public int getCapacity() {
        return capacity;
    }

    public StreetVehicle getVehicleOnTheRoad(int index){
        if (index < 0 || index  >= vehiclesOnTheRoad.size()) return null;
        return vehiclesOnTheRoad.get(index);
    }

    public int getVehicleOnTheRoadCount(){return vehiclesOnTheRoad.size();}

    public boolean isOnLimit(){
        return vehiclesOnTheRoad.size() == capacity;
    }

    public void setCapacity(int capacaty) {
        this.capacity = capacaty;
    }

    public abstract AirplaneServices getService();

    public void UpdateParkgateList(){

    }

    public void addVehicleDesirialization(StreetVehicle vehicle){
        vehiclesOnTheRoad.add(vehicle);
    }

    public void customWriteObject(){
        vehiclesOnTheRoad = new ArrayList<>();
    }
}
