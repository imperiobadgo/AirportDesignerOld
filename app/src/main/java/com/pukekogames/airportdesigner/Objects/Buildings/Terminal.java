package com.pukekogames.airportdesigner.Objects.Buildings;

import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.Roads.Road;

/**
 * Created by Marko Rapka on 22.03.2017.
 */
public class Terminal extends Building {

    public Terminal(Road road) {
        super(road);
        setImageID(Images.indexTerminal);
    }
}
