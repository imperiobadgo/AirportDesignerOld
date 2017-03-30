package com.pukekogames.airportdesigner.Objects.UIElements;

import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Helper.ChangeValues;


/**
 * Created by Marko Rapka on 06.11.2015.
 */
public class UpdateableLabel extends Label {

    private static final long serialVersionUID = 6497926278671881091L;
    private ChangeValues content;

    public UpdateableLabel(ChangeValues cV, Alignment alignment, float x, float y, int fontSize) {
        super(cV.message, alignment, x, y, fontSize);
        this.content = cV;
    }

    public void tick() {

    }

    @Override
    public String getContent(){
        return content.message;
    }
}
