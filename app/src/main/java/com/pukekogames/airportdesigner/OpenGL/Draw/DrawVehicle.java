package com.pukekogames.airportdesigner.OpenGL.Draw;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;
import com.pukekogames.airportdesigner.Objects.Vehicles.StreetVehicle;
import com.pukekogames.airportdesigner.Objects.Vehicles.Vehicle;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplaneState;
import com.pukekogames.airportdesigner.OpenGL.OpenGLRenderer;
import com.pukekogames.airportdesigner.OpenGL.PrimitiveHelper;
import com.pukekogames.airportdesigner.OpenGL.Texture;
import com.pukekogames.airportdesigner.OpenGL.TextureLoader;
import com.pukekogames.airportdesigner.Rendering.Render;

/**
 * Created by Marko Rapka on 15.04.2017.
 */
public class DrawVehicle {

    static void draw(Vehicle vehicle){

        PointInt centerPos = Render.getPositionForRender(vehicle.getAlign_X(), vehicle.getAlign_Y());

        if (vehicle instanceof Airplane) {
            Airplane airplane = (Airplane) vehicle;
            renderAirplane(airplane, centerPos);
        } else if (vehicle instanceof StreetVehicle) {
            StreetVehicle streetVehicle = (StreetVehicle) vehicle;
            renderStreetVehicle(streetVehicle, centerPos);
        }
    }

    private static void renderAirplane(Airplane airplane, PointInt centerPos) {
        airplane.setCenterPos(centerPos);

        boolean selected = airplane.isSelected();
        float ratio = airplane.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < DrawObject.RATIOMIN) {
            ratio = 0;
        }

        if (selected && (airplane.getState() == AirplaneState.Boarding || airplane.getState() == AirplaneState.ArrivedAtGate)) {
            //draw vehicle heading for this airplane
            StreetVehicle vehicle = GameInstance.Airport().getVehicleForAirplane(airplane);
            drawAttentionForVehicle(vehicle, centerPos);
        }

        Texture texture = TextureLoader.Instance().getTexture(airplane.getImageID());

        if (texture != null){
            airplane.setDimension(texture.getWidth(), texture.getHeight());
            float scale = GameInstance.Settings().Zoom;
            if (airplane.getCategory() <= 2) {
                scale *= 0.5;
            } else if (airplane.getCategory() == 3) {

            }

            float altitude = airplane.getAltitude();
            float speed = airplane.getSpeed();
            int waitTime = airplane.getWaitTime();
            float heading = airplane.getHeading();
            boolean holdPosition = airplane.isHoldPosition();
            int turnaroundTime = airplane.getTurnaroundTime();
            int maxTurnaroundTime = airplane.getMaxTurnaroundTime();
            float startCriticalTime = maxTurnaroundTime * 0.7f;
            AirplaneState state = airplane.getState();

            int scaledWidth = Math.round(scale * texture.getWidth());
            int scaledHeight = Math.round(scale * texture.getHeight());

            PrimitiveHelper.drawClickleableGameObject(airplane,scale, scale, ratio);

        }



    }

    private static void drawAttentionForVehicle(StreetVehicle vehicle, PointInt centerPos) {

    }

    private static void renderStreetVehicle(StreetVehicle streetVehicle, PointInt centerPos) {

    }
}
