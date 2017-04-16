package com.pukekogames.airportdesigner.GameInstance;

import com.pukekogames.airportdesigner.Helper.ClassTranslation.BuildingType;
import com.pukekogames.airportdesigner.Helper.ClassTranslation.RoadType;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Objects.ClickableGameObject;

import java.io.Serializable;

/**
 * Created by Marko Rapka on 07.09.2016.
 */
public class CommonSettings implements Serializable {

    private static final long serialVersionUID = 3803244527196407112L;
    private static CommonSettings ourInstance = new CommonSettings();

    CommonSettings(){}

    //    public static CommonSettings Instance() {
//        return ourInstance;
//    }
//
    static void setSettings(CommonSettings settings){
        ourInstance = settings;
    }

    //Rendering
    public float Zoom = 0.3f;
    public float Scale = 0.5f;
    public int RenderDepth = 10;
    public PointInt screenSize = new PointInt(1280, 720);
    public int buttonReactionShowTime = 10;
    public int ButtonWidth = 200;
    public int ButtonHeight = 50;
    public boolean shouldUpdateRoadMap = true;
    public float airportShift = 1;
    public float shiftPerTick = 0.08f;

    //interaction
    public PointInt MapCenter = new PointInt(400, 250);
    public int MapSizeX = 2000;
    public int MapSizeY = 1000;
    public int DoubleMouseClick = 15;
    public int clickRadius = 35;
    public int singleClickTime = 2;
    public int SelectionTime = 200;
    public int MessageShowTime = 200;

    //Game
    public int level = 1;
    public int gameSpeed = 1;
    public int maxBoardingTime = 3000;
    public int gameType = 0;//0 normalGame, 1 debugAirport , 2 LOAD Airport
    public boolean loaded = false;
    transient public ClickableGameObject selectedObject;
    public boolean CollisionDetection = true;
    public boolean DebugMode = false;

    public int maxTime = 6000;

    //building
    transient public boolean buildRoadSelected = false;
    transient public int buildMode = 0; //0 no building, 1 building roads with needed intersections, 2 deleting, 3 build Building
    public RoadType buildRoad = RoadType.None;
    public BuildingType buildDepot = BuildingType.None;
    public boolean selectionCompleted = false;//tell the handler to update
    public int buildOffset = -100;
    transient public long buildPrice = 0;
    public int snapToLineDistance = 300;
    public int buildMinRadius = 1800;
}
