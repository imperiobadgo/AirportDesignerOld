package com.pukekogames.airportdesigner.OpenGL.Draw;

import com.pukekogames.airportdesigner.Objects.GameObject;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Vehicles.Vehicle;
import com.pukekogames.airportdesigner.OpenGL.PrimitiveHelper;

import java.util.SimpleTimeZone;

/**
 * Created by Marko Rapka on 16.04.2017.
 */
public class DrawObject {

    public static float RATIOMIN = 0.1f;
    public static float RATIOMINBUTTON = 0.5f;

    public static void draw(GameObject object){

        if (object instanceof Vehicle){
            Vehicle vehicle = (Vehicle) object;
            DrawVehicle.draw(vehicle);
        }else if (object instanceof Road){
            Road road = (Road) object;
            PrimitiveHelper.drawRoad(road);
        }

    }
}
