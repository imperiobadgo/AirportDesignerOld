package com.pukekogames.airportdesigner.Objects.Buildings;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Objects.ClickableGameObject;
import com.pukekogames.airportdesigner.Objects.Roads.Road;

/**
 * Created by Marko Rapka on 16.10.2016.
 */
public class Building extends ClickableGameObject {

    private static final long serialVersionUID = 5735309242371011376L;
    Road road;
    boolean userWantsDemolition = false;


    protected Building(Road road) {
        super(Alignment.Table, 10, 10, 10, 10);
        this.road = road;
        Align_X = road.getCenterPosition().x;
        Align_Y = road.getCenterPosition().y;
        heading = road.getHeading();
    }

    @Override
    public void clicked(int mx, int my) {

    }

    @Override
    public void tick() {
        if (userWantsDemolition){
            if (canRemoveBuilding()) {
                GameInstance.Airport().RemoveBuilding(this);
            }
        }
    }

    protected boolean canRemoveBuilding(){
        return true;
    }

    public void toggleUserWantsDemolition() {
        userWantsDemolition = !userWantsDemolition;
    }

    public void setUserWantsDemolition(boolean userWantsDemolition) {
        this.userWantsDemolition = userWantsDemolition;
    }

    public boolean isUserWantsDemolition() {
        return userWantsDemolition;
    }

    public Road getRoad() {
        return road;
    }
}
