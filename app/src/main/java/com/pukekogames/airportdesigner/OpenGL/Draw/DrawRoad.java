package com.pukekogames.airportdesigner.OpenGL.Draw;

import com.pukekogames.airportdesigner.Objects.Roads.*;
import com.pukekogames.airportdesigner.OpenGL.Texture;
import com.pukekogames.airportdesigner.OpenGL.TextureLoader;
import com.pukekogames.airportdesigner.Rendering.Render;

/**
 * Created by Marko Rapka on 16.04.2017.
 */
public class DrawRoad {

    public static void draw(Road road){



        if (road instanceof Runway) {
            Runway runway = (Runway) road;
            drawRunway(runway);
        }else{
            drawRoad(road);
        }

//        if (road instanceof Runway) {
//            Runway runway = (Runway) road;
//            Instance().renderRunway(runway);
//        } else if (road instanceof ParkGate) {
//            ParkGate parkGate = (ParkGate) road;
//            renderParkGate(parkGate);
//        } else if (road instanceof Taxiway) {
//            Taxiway taxiway = (Taxiway) road;
//            renderTaxiway(taxiway);
//        } else if (road instanceof Street) {
//            Street street = (Street) road;
//            renderStreet(street);
//        }

    }

    private static void drawRoad(Road road) {

    }

    private static void drawRunway(Runway runway) {

        float ratio = runway.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < DrawObject.RATIOMIN) {
            ratio = 0;
        }

        Texture texture = TextureLoader.Instance().getTexture(runway.getImageID());

        if (texture != null){
            float length = runway.getLength();
            float heading = runway.getHeading();
        }

    }
}
