package com.pukekogames.airportdesigner.Objects.Airlines;

/**
 * Created by Marko Rapka on 19.03.2017.
 */
public class AirlineList {

    public static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVW";

    private static Object[][] airlines = {
            {"Air Berlin", "AB"},
            {"Lufthansa", "LH"},
            {"Emirates", "EK"},
            {"United", "UD"},
            {"North Air", "NA"},
            {"Jet Airlines", "JE"},
            {"Air American", "AA"},
            {"Eastern Air", "EA"}
    };

    public static String getAirlineName(int id){
        if (id < 0 || id > airlines.length) return "Airline";
        return (String) airlines[id][0];
    }

    public static String getAirlineCode(int id){
        if (id < 0 || id > airlines.length) return "AIR";
        return (String) airlines[id][1];
    }

    public static int maxIndex(){
        return airlines.length;
    }
}
