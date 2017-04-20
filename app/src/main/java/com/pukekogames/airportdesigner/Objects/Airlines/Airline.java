package com.pukekogames.airportdesigner.Objects.Airlines;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplaneDataB777;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplaneDataA320;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Marko Rapka on 20.11.2016.
 */
public class Airline implements Serializable {
    private static final long serialVersionUID = -7608506648596791009L;

    private ArrayList<PlannedArrival> plannedArrivals;
    private ArrayList<Integer> lastReputations;

    private int id;
    private Random random;

    public Airline(int id) {
        this.id = id;
        plannedArrivals = new ArrayList<>();
        lastReputations = new ArrayList<>();

        random = new Random();
        int plannedArrivalCount = random.nextInt(5) + 1;
        for (int i = 0; i < plannedArrivalCount; i++) {
            addNewPlannedArrival();
        }

    }

    public void addNewPlannedArrival() {
        Airplane newAirplane;

        if (GameInstance.Settings().level > 3) {
            if (random.nextInt(2) == 1) {
                newAirplane = new Airplane(new AirplaneDataA320(), this);
            } else {
                newAirplane = new Airplane(new AirplaneDataB777(), this);
            }
        } else {
            newAirplane = new Airplane(new AirplaneDataA320(), this);
        }
        int plannedHour = random.nextInt(16) + 5;
        String hourString = plannedHour + "";
        while (hourString.length() < 2) {
            hourString = 0 + hourString;
        }
        String callSign = AirlineList.getAirlineCode(id) + hourString + (random.nextInt(88) + 10);

        PlannedArrival plannedArrival = new PlannedArrival(plannedHour, random.nextInt(2) + random.nextInt(2) + 1, newAirplane);
        plannedArrival.setCallSign(callSign);
        plannedArrivals.add(plannedArrival);
    }

    public void getNextAirplanes(int hour, ArrayList<Airplane> nextAirplanes) {
        for (PlannedArrival arrival : plannedArrivals) {
            if (!arrival.isAccepted()) continue;
            if (arrival.getHour() == hour) {
                nextAirplanes.add(arrival.generateNewAirplane());
            }
        }
    }

    public PlannedArrival getPlannedArival(int index) {
        if (index >= 0 && index <= plannedArrivals.size()) return plannedArrivals.get(index);
        return null;
    }

    public int PlannedArivalsCount() {
        return plannedArrivals.size();
    }

    public boolean updatePlannedArrival() {
        ArrayList<PlannedArrival> removeArrival = new ArrayList<>();
        for (PlannedArrival arrival : plannedArrivals) {
            if (!arrival.isAccepted()) {
                removeArrival.add(arrival);
            }
        }
        for (PlannedArrival arrival : removeArrival) {
            plannedArrivals.remove(arrival);
        }

        int plannedArrivalCount = random.nextInt(5);
        for (int i = 0; i < plannedArrivalCount; i++) {
            addNewPlannedArrival();
        }
        return removeArrival.size() > 0;
    }

    public void addFinishedAirplanes(Airplane airplane) {
        airplane.ClearReferences();
        for (PlannedArrival arrival : plannedArrivals) {
            if (arrival.getCallSign().equals(airplane.getCallSign())) {
                arrival.addFinishedAirplanes(airplane);
                break;
            }
        }
    }

    public int getReputation() {
        int reputation = 0;

        for (PlannedArrival arrival : plannedArrivals) {
            if (arrival.getLastTurnaraoundTimeDiff() < 0) {
                reputation -= 1;
            } else {
                reputation += 1;
            }
        }

        return reputation;
    }

    public float[] getLastReputation() {
        float[] result = new float[lastReputations.size()];

        for (int i = 0; i < lastReputations.size(); i++) {
            result[i] = lastReputations.get(i);

        }
        return result;
    }

    @Override
    public String toString() {
        return AirlineList.getAirlineName(id);
    }

    public String getAirlineName() {
        return AirlineList.getAirlineName(id);
    }
}
