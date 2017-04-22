package com.pukekogames.airportdesigner.Rendering;

import android.graphics.*;
import com.pukekogames.airportdesigner.GameInstance.Airport;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Helper.Pathfinding.ConnectionMissing;
import com.pukekogames.airportdesigner.Objects.Buildings.Building;
import com.pukekogames.airportdesigner.Objects.GameObject;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Roads.Runway;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;
import com.pukekogames.airportdesigner.Objects.Vehicles.Vehicle;
import com.pukekogames.airportdesigner.Settings;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Marko Rapka on 17.09.2016.
 */
public class RenderAirport {

    private Matrix matrix;
    private Bitmap roadMap;

    private static RenderAirport ourInstance = new RenderAirport();

    public static RenderAirport Instance() {
        return ourInstance;
    }


    private LinkedList<GameObject> renderObjects;
    private LinkedList<RoadIntersection> selectedRoadIntersections;
    private ArrayList<Airplane> planeInstruction;
    private ArrayList<GameObject> roadMapList;

    private RenderAirport() {
        renderObjects = new LinkedList<GameObject>();
        selectedRoadIntersections = new LinkedList<RoadIntersection>();
        planeInstruction = new ArrayList<Airplane>();
        roadMapList = new ArrayList<>();
    }


    public void render(Canvas canvas, Paint paint, Airport airport) {

        renderObjects.clear();
        selectedRoadIntersections.clear();


//        if (roadMap == null || GameInstance.Settings().shouldUpdateRoadMap) {
//            roadMapList.clear();
//
//            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//            roadMap = Bitmap.createBitmap(1000, 1000, conf);
//
//            for (int i = 0; i < airport.getRoadCount(); i++) {
//                Road road = airport.getRoad(i);
//                if (road instanceof Runway) continue;
//                roadMapList.add(road);
//            }
//            for (int i = 0; i < airport.getRoadIntersectionCount(); i++) {
//                RoadIntersection intersection = airport.getRoadIntersection(i);
//
//                roadMapList.add(intersection);
//            }
//            for (int i = 0; i < airport.getRunwayCount(); i++) {
//                Runway runway = airport.getRunway(i);
//                roadMapList.add(runway);
//            }
//
//
//            Canvas roadCanvas = new Canvas(roadMap);
//            for (int i = 0; i < roadMapList.size(); i++) {
//                GameObject object = roadMapList.get(i);
//                Render.render(roadCanvas, paint, object);
//            }
//            GameInstance.Settings().shouldUpdateRoadMap = false;
//        }
//        drawRoadMap(canvas, paint);



        for (int i = 0; i < airport.getRoadCount(); i++) {
            Road road = airport.getRoad(i);
            if (road instanceof Runway) continue;
            renderObjects.add(road);
        }

        for (int i = 0; i < airport.getRoadIntersectionCount(); i++) {
            RoadIntersection intersection = airport.getRoadIntersection(i);
            if (intersection.isSelected()) selectedRoadIntersections.add(intersection);
//            intersection.render(canvas);
            renderObjects.add(intersection);
        }

        for (int i = 0; i < airport.getRunwayCount(); i++) {
            Runway runway = airport.getRunway(i);
            renderObjects.add(runway);
        }

        //first draw only roads
        for (GameObject object : renderObjects) {
            object.setAni_Ratio(GameInstance.Settings().airportShift);
            Render.render(canvas, paint, object);
        }

        if (GameInstance.Settings().Zoom > RenderRoad.MINZOOMROADMARKINGS) {
            for (GameObject object : renderObjects) {
                if (object instanceof Road) {
                    Road road = (Road) object;
                    RenderRoad.drawRoadMarkings(canvas, paint, road);
                }


            }
        }
        renderObjects.clear();

        for (int i = 0; i < airport.getVehicleCount(); i++) {
            Vehicle vehicle = airport.getVehicle(i);
            if (vehicle instanceof Airplane) continue;
            renderObjects.add(vehicle);
        }

        for (int i = 0; i < airport.getBuildingCount(); i++) {
            Building building = airport.getBuilding(i);
            renderObjects.add(building);
        }

        planeInstruction.clear();

        for (int i = 0; i < airport.getAirplaneCount(); i++) {
            Airplane airplane = airport.getAirplane(i);
            renderObjects.add(airplane);
            if (airplane.isWaitingForInstructions()) {
                planeInstruction.add(airplane);
            }
        }


        for (GameObject object : renderObjects) {
            object.setAni_Ratio(GameInstance.Settings().airportShift);
            Render.render(canvas, paint, object);
        }

        for (Airplane plane : planeInstruction) {
            Render.drawAttention(plane.getCenterPos(), paint, canvas, Settings.Instance().attentionColor, null);
        }
        for (RoadIntersection selectedIntersection : selectedRoadIntersections) {
            Render.drawPossibleSelection(canvas, paint, selectedIntersection);
        }


        if (airport.getConnectionsMissing() != null && !airport.isGeneratingNewChecks()) {
            paint.reset();
            try {
                for (ConnectionMissing connectionMissing : airport.getConnectionsMissing()) {
                    if (airport.isGeneratingNewChecks()) break;
                    RoadIntersection roadIntersection = connectionMissing.getTarget();
                    RenderRoadIntersection.drawPossibleSelection(canvas, paint, roadIntersection, true);
                }
            }catch(Exception e){

            }
        }

//        if (firstInit == 0) {
//            windIndicator.connectPositions(new PointFloat(-2000, 0), new PointFloat(-1000, 1000));
////            windIndicator.calculatePositions();
//            windIndicator.setShowDirection(true);
//            Handler.setObjectImage(windIndicator);
//            firstInit = 1;
//        }
//        windIndicator.calculateNewDirection(windDirection, 1000);
//        Render.render(g, windIndicator);

    }

    private void drawRoadMap(Canvas canvas, Paint paint) {
        if (roadMap != null) {
            if (matrix == null) {
                matrix = new Matrix();
            }

            PointInt renderCenter = Render.getPositionForRender(GameInstance.Settings().MapCenter.x, GameInstance.Settings().MapCenter.y);
            float scale = GameInstance.Settings().Zoom;


            matrix.reset();

            matrix.postScale(scale, scale);
            matrix.postTranslate(renderCenter.x, renderCenter.y);
            paint.reset();
            paint.setColor(Color.rgb(255, 255, 255));
            canvas.drawBitmap(roadMap, matrix, paint);
        }
    }
}
