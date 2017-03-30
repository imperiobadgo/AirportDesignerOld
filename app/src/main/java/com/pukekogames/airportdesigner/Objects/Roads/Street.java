package com.pukekogames.airportdesigner.Objects.Roads;

import com.pukekogames.airportdesigner.Objects.Images;

/**
 * Created by Marko Rapka on 12.06.2016.
 */
public class Street extends Road {

    private static final long serialVersionUID = 1020733963317254164L;

    public Street(){
        super();
        setImageID(Images.indexStreet);
    }
    @Override
    public void clicked(int mx, int my) {

    }

    @Override
    public void tick() {
        super.tick();
    }
}
