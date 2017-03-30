package com.pukekogames.airportdesigner.GameInstance;

import com.pukekogames.airportdesigner.Objects.Airlines.Airline;
import com.pukekogames.airportdesigner.Objects.Airlines.AirlineList;
import com.pukekogames.airportdesigner.Objects.Airlines.PlannedArrival;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplaneDataCessna;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplaneDataSmall;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplanePerformance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Marko Rapka on 20.11.2016.
 */
public class AirlineManager implements Serializable {
    private static final long serialVersionUID = -8176310535787167826L;

    private ArrayList<Airline> airlines;
    ArrayList<Integer> freeIndeces;
    private Airline selectedAirline;
    private Random random;

    private int reputation;

    public AirlineManager() {
        airlines = new ArrayList<>();
        freeIndeces = new ArrayList<>();
        random = new Random();
        for (int i = 0; i < AirlineList.maxIndex(); i++) {
            freeIndeces.add(i);
        }
    }

    public void AddNewAirline(){
        if (freeIndeces.size() == 0) return;
        int r = random.nextInt(freeIndeces.size() - 1);
        int index = freeIndeces.get(r);
        freeIndeces.remove(r);
        Airline airline = new Airline(index);
        airlines.add(airline);
    }

    public Airplane generateNewPrivateAirplane(){
        Airplane airplane = new Airplane(getNewPrivateAirplane(), null);
        String callSign = "D-E";
        for (int i = 0; i < 3; i++) {
            callSign += AirlineList.CHARS.charAt(random.nextInt(AirlineList.CHARS.length()));
        }
        airplane.setCallSign(callSign);
        return airplane;
    }

    private AirplanePerformance getNewPrivateAirplane() {
        int category = 0;

        if (GameInstance.Settings().level == 1) {
            category = 0;
        } else {
            category = random.nextInt(3);
        }

        switch (category) {
            case 0:
                return new AirplaneDataSmall();
            case 1:
            case 2:
            case 3:
                return new AirplaneDataCessna();
            default:
                return new AirplaneDataSmall();
        }
    }

    public void AddFinishedVehicle(Airplane airplane) {
        Airline airline = airplane.getAirline();
        airline.addFinishedAirplanes(airplane);
        UpdateReputation();
    }

    public void getNextAirplanes(int hour, ArrayList<Airplane> nextAirplanes) {
        for (Airline airline : airlines) {
            airline.getNextAirplanes(hour, nextAirplanes);
        }
    }

    public Airline getAirline(int index) {
        if (index >= 0 && index < airlines.size()) {
            return airlines.get(index);
        }
        return null;
    }

    public int AirlinesCount() {
        return airlines.size();
    }

    public Airline getSelectedAirline() {
        return selectedAirline;
    }

    public void setSelectedAirline(Airline selectedAirline) {
        this.selectedAirline = selectedAirline;
    }

    private void UpdateReputation() {
        int rep = 1;
        for (Airline airline : airlines) {
            rep += airline.getReputation();
        }
        reputation = Math.round(rep / (float) airlines.size());
    }

    public int getReputation() {
        return reputation;
    }

    public boolean updateFlightPlan(){
        boolean updatedSomething = false;
        for (Airline airline: airlines){
            updatedSomething = airline.updatePlannedArrival() || updatedSomething;
        }
        return updatedSomething;
    }

    public PlannedArrival[] getPlannedArrivals(){
        ArrayList<PlannedArrival> plannedArrivals = new ArrayList<>();
        for (Airline airline : airlines) {
            for (int i = 0; i < airline.PlannedArivalsCount(); i++) {
                plannedArrivals.add(airline.getPlannedArival(i));
            }
        }
        return plannedArrivals.toArray(new PlannedArrival[plannedArrivals.size()]);
    }

    public void clear() {
        airlines.clear();
    }

}
