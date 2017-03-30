package com.pukekogames.airportdesigner.Objects.Roads;

import com.pukekogames.airportdesigner.Objects.Images;

/**
 * Created by Marko Rapka on 21.03.2016.
 */
public class Taxiway extends Road {


    private static final long serialVersionUID = -7790813053272729589L;

    public Taxiway() {
        super();
        setImageID(Images.indexTaxiway);
    }

    @Override
    public void clicked(int mx, int my) {

    }

    @Override
    public void tick() {
        super.tick();
//        calculateNewDirection(heading + 0.1f, length);

    }
}
