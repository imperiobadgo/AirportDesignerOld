package com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData;

/**
 * Created by Marko Rapka on 20.04.2016.
 */
public enum AirplaneState{
    Init(),
    Waiting(),
    Arrival(),
    Landing(),
    WaitingForGate(),
    TaxiToGate(),
    ArrivedAtGate(),
    Boarding(),
    ReadyForPushback(),
    Pushback(),
    TaxiToRunway(),
    ReadyForDeparture(),
    ClearedForDeparture(),
    Takeoff(),
    Departure()
}
