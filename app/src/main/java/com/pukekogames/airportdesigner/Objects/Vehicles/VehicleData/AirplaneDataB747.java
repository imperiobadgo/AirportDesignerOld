package com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData;

import com.pukekogames.airportdesigner.Objects.Images;

/**
 * Created by Marko Rapka on 17.04.2017.
 */
public class AirplaneDataB747 extends AirplanePerformance {

    public AirplaneDataB747(){
        StandardAirplane();
        imageID = Images.indexAirplane747;
        category = 3;
    }
}
