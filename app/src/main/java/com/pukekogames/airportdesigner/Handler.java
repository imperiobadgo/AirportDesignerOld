package com.pukekogames.airportdesigner;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.widget.Toast;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.*;
import com.pukekogames.airportdesigner.Helper.ClassTranslation.BuildingType;
import com.pukekogames.airportdesigner.Helper.ClassTranslation.RoadType;
import com.pukekogames.airportdesigner.Helper.ClassTranslation.TypeTranslation;
import com.pukekogames.airportdesigner.Helper.Geometry.Line;
import com.pukekogames.airportdesigner.Helper.Geometry.PointFloat;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Helper.Pathfinding.ConnectionMissing;
import com.pukekogames.airportdesigner.Objects.*;
import com.pukekogames.airportdesigner.Objects.Buildings.Building;
import com.pukekogames.airportdesigner.Objects.Roads.*;
import com.pukekogames.airportdesigner.Objects.UIElements.Button;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;
import com.pukekogames.airportdesigner.Activities.Game;
import com.pukekogames.airportdesigner.Rendering.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Marko Rapka on 25.05.2016.
 */
public class Handler {
    private Game game;
    private UIManager uiManager;


    private ClickableGameObject selected;//current selected ClickableGameObject
    private int selectedTime = 0;
    private ClickableGameObject lastSelected;//is used to tell the inputfield, that is was selected last for keyboard input and for the airplane possible targets

    private LinkedList[] renderQueue;//ordered objectslist for rendering

    private int doubleMouseClickTime = 0;
    private PointInt lastMousePosition = new PointInt();

    //copyOnWriteArrayList is needed because the main thread and the touchEventThread can be writing and reading the array on the same time
    CopyOnWriteArrayList<ClickableGameObject> selectableGameObjects = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<PointFloat> intersectPoints = new CopyOnWriteArrayList<>();

    RoadIntersection buildIntersection = new RoadIntersection(new PointFloat(0, 0));//for rendering buildintersection
    Road buildRoad = null;
    long buildCost = 0L;
    RoadIntersection firstRoadIntersection = null;//for connecting two intersections with road
    private PointInt buildOffset = new PointInt();

    private ChangeValues cV;
    private PointInt touchPosOnTable = new PointInt();

    private PointInt lastTouch = new PointInt();
    private int lastTouchTime = 0;

    ClickableGameObject choiceForThis;
    private boolean isMoving;


    public Handler(Game game) {
        this.game = game;

        cV = new ChangeValues();


        renderQueue = new LinkedList[GameInstance.Settings().RenderDepth];

        for (int i = 0; i < renderQueue.length; i++) {
            renderQueue[i] = new LinkedList<>();
        }

    }

    public boolean OnTouch(MotionEvent event) {
        if (lastTouchTime > 0 || isMoving) return false;
        lastTouchTime = GameInstance.Settings().singleClickTime;
//        Log.i(Game.TAG, "Touched!");
        double lastTouchDistance = CommonMethods.getDistance(new PointInt(event.getX(), event.getY()), lastTouch);
        updatePosition((int) event.getX(), (int) event.getY());
        boolean doubleTouch = false;
        if (doubleMouseClickTime > 0) {
            doubleTouch = true;
        } else {
            doubleMouseClickTime = GameInstance.Settings().DoubleMouseClick;
        }
        boolean hasSomethingClicked = false;

        hasSomethingClicked = clickScreenObject(event, true);


        if (uiManager.getScreenState() != ScreenState.Game) {
            return uiManager.onTouch(event);
        }

        //don't select anything when zoomed far away
        if (GameInstance.Settings().Zoom < RenderRoad.MINZOOMROADMARKINGS) return false;

        //this stuff is just important in gameScreen
        if (GameInstance.Settings().buildMode == 1 && !hasSomethingClicked) {


            if (selectableGameObjects.size() > 0) {
                ClickableGameObject clickedObject = null;
                for (ClickableGameObject ClObject : selectableGameObjects) {
                    hasSomethingClicked = clickGameObject(ClObject, (int) event.getX(), (int) event.getY(), true);
                    if (hasSomethingClicked) {
                        clickedObject = ClObject;
                        break;
                    }
                }
                if (clickedObject != null) {
                    if (clickedObject instanceof RoadIntersection && GameInstance.Settings().buildRoad != RoadType.None) {
                        RoadIntersection intersection = (RoadIntersection) clickedObject;
//                        if (GameInstance.Settings().buildRoad == RoadType.runway) {
//                            Runway connectedRunway = GameInstance.Airport().getConnectedRunway(intersection);
//                            if (connectedRunway != null) {
//                                Toast toast = Toast.makeText(game, R.string.Building_CantBuildRunway_Toast, Toast.LENGTH_SHORT);
//                                toast.show();
//                                return true;
//                            }
//                        }

                        //select first roadIntersection
                        firstRoadIntersection = intersection;
                        selectableGameObjects.clear();

                        buildOffset.set(0, 0);
                        buildIntersection.setTablePosition(firstRoadIntersection.getPosition().x, firstRoadIntersection.getPosition().y);
                        buildRoad = TypeTranslation.translateRoad(GameInstance.Settings().buildRoad);
                        buildRoad.setLast(firstRoadIntersection);
                        buildRoad.setNext(buildIntersection);

                        PointInt tablePos = mouseToTablePos((int) event.getX(), (int) event.getY());
                        BuildingHelper.calcBuildPos(tablePos.x, tablePos.y, firstRoadIntersection, buildIntersection, lines, intersectPoints);
                        buildRoad.updatePosition();
                        uiManager.setSelectableRoadIntersections(intersection);

//                        }
                    }
                } else {
                    //calculate build offset
                    PointFloat buildPos = buildIntersection.getPosition();
                    if (Math.sqrt(Math.pow(buildPos.x - touchPosOnTable.x, 2) + Math.pow(buildPos.y - touchPosOnTable.y, 2)) < 2000) {
                        buildOffset.set(buildPos.x - touchPosOnTable.x, buildPos.y - touchPosOnTable.y);
                    }
                }
            }

            return true;
        } else if (GameInstance.Settings().buildMode == 2 && !hasSomethingClicked) {
            if (selectableGameObjects.size() > 0) {
                ClickableGameObject clickedObject = null;
                for (ClickableGameObject ClObject : selectableGameObjects) {
                    hasSomethingClicked = clickGameObject(ClObject, (int) event.getX(), (int) event.getY(), true);
                    if (hasSomethingClicked) {
                        clickedObject = ClObject;
                        break;
                    }
                }
                if (clickedObject != null) {
                    if (clickedObject instanceof Building) {
                        Building depot = (Building) clickedObject;
                        depot.toggleUserWantsDemolition();
                    } else if (clickedObject instanceof Road) {
                        Road road = (Road) clickedObject;
                        road.toggleUserWantsDemolition();
                    }

                    setAllDeletableObjects();
                }


            }
            hasSomethingClicked = true;
        } else if (GameInstance.Settings().buildMode == 3 && !hasSomethingClicked && GameInstance.Settings().buildRoad == RoadType.None) {
            // build buildings
            if (selectableGameObjects.size() > 0) {
                ClickableGameObject clickedObject = null;
                for (ClickableGameObject ClObject : selectableGameObjects) {
                    hasSomethingClicked = clickGameObject(ClObject, (int) event.getX(), (int) event.getY(), true);
                    if (hasSomethingClicked) {
                        clickedObject = ClObject;
                        break;
                    }
                }
                if (clickedObject != null) {
                    if (clickedObject instanceof Road) {


                        //money check
                        if (GameInstance.Instance().removeMoney(GameInstance.Settings().buildPrice)) {
                            Road road = (Road) clickedObject;

                            Building building = TypeTranslation.translateDepot(GameInstance.Settings().buildDepot, road);

                            GameInstance.Airport().AddBuilding(building);
                            setSelectableObjectsForBuildBuilding();
//                            if (GameInstance.Settings().buildDepot != BuildingType.terminal){
//                                GameInstance.Settings().buildDepot = BuildingType.None;
//                                selectableGameObjects.clear();
//                            }
                        } else {
                            Toast toast = Toast.makeText(game, R.string.Building_NotEnoughMoney_Toast, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
            }
            hasSomethingClicked = true;
        }

        if (!hasSomethingClicked) {
            if (selectableGameObjects.size() > 0) {
                //select something for target
                ClickableGameObject clickedObject = null;
                for (ClickableGameObject ClObject : selectableGameObjects) {
                    hasSomethingClicked = clickGameObject(ClObject, (int) event.getX(), (int) event.getY(), false);
                    if (hasSomethingClicked) {
                        clickedObject = ClObject;
                        break;
                    }
                }
                if (clickedObject != null) {
                    if (choiceForThis instanceof Airplane) {
                        Airplane plane = (Airplane) choiceForThis;
                        if (clickedObject instanceof RoadIntersection) {
                            RoadIntersection intersection = (RoadIntersection) clickedObject;
                            plane.searchRoute(intersection);
                            uiManager.clearSelectableObjects();
                        }
                    }
                }
            } else {
                uiManager.removeCircleButtons();
                //click any airport object
                Object[] airportObjects = GameInstance.Airport().getClickableObjects();
                for (Object tempObject : airportObjects) {
                    hasSomethingClicked = clickGameObject(tempObject, (int) event.getX(), (int) event.getY(), false);
                    if (hasSomethingClicked) break;
                }

                //selectMissingConnection
                if (GameInstance.Airport().getConnectionsMissing() != null) {
                    for (ConnectionMissing missing : GameInstance.Airport().getConnectionsMissing()) {
                        hasSomethingClicked = clickGameObject(missing.getTarget(), (int) event.getX(), (int) event.getY(), false);

                        if (hasSomethingClicked) {
                            break;
                        }
                    }
                }

            }
        }

//        if (!hasSomethingClicked) {
//            setSelected(null);
//            if (uiManager.removeSelectionButton().isEnabled()) {
//                selectableGameObjects.clear();
//            }
//            uiManager.removeCircleButtons();
//        }
        if (!hasSomethingClicked && doubleTouch) {

            if (lastTouchDistance < 40) {
                GameInstance.Settings().DebugMode = !GameInstance.Settings().DebugMode;
            }
        }

        return hasSomethingClicked;
    }

    boolean clickGameObject(Object object, int mx, int my, boolean justCheckCollision) {
        if (object instanceof ClickableGameObject) {
            ClickableGameObject ClObject = (ClickableGameObject) object;
//            Log.i(Game.TAG, ClObject.toString() + " checked!");
            if (ClObject.isColliding(new PointInt(mx, my))) {
//                Log.i(Game.TAG, ClObject.toString() + " clicked!");
                if (justCheckCollision) {
                    return true;
                }
                ClObject.clicked(mx, my);
                setSelected(ClObject);
                uiManager.clickButtonCircle(ClObject, mx, my);

//                if (ClObject instanceof Depot) {
//                    game.setDepotScreen();
//                }
                return true;
            }
        }
        return false;
    }

    public PointInt mouseToTablePos(int mx, int my) {
        float zoom = GameInstance.Settings().Zoom * GameInstance.Settings().Scale;
        PointInt tableCenter = GameInstance.Settings().MapCenter;
        int tableSizeX = GameInstance.Settings().MapSizeX;
        int tableSizeY = GameInstance.Settings().MapSizeY;
        //Position of the top left corner of the table in the Viewport
        Point tableTopLeft = new Point((int) (tableCenter.x - tableSizeX * zoom), (int) (tableCenter.y - tableSizeY * zoom));
        return new PointInt((int) ((mx - tableTopLeft.x) / zoom), (int) ((my - tableTopLeft.y) / zoom));
    }

    private void updatePosition(int mx, int my) {
        lastTouch.set(mx, my);
        lastMousePosition.set(mx, my);
        PointInt posOnTable = mouseToTablePos(mx, my);
        touchPosOnTable.set(posOnTable.x, posOnTable.y);
//        Log.i(Game.TAG, "X: " + posOnTable.x + " Y: " + posOnTable.y + " OnScreen: " + "X: " + mx + " Y: " + my);
//        cV.message = "X: " + posOnTable.x + " Y: " + posOnTable.y;
    }

    //    CopyOnWriteArrayList<RoadIntersection> nearIntersection = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<Line> lines = new CopyOnWriteArrayList<>();

    public boolean touchMoved(float lastTouchX, float lastTouchY) {
        if (GameInstance.Settings().buildMode == 1) {
            PointInt tablePos = mouseToTablePos((int) lastTouchX, (int) lastTouchY);
//            PointInt tablePos = mouseToTablePos((int) lastTouchX, (int) lastTouchY + GameInstance.Settings().buildOffset);
            boolean canBuild = BuildingHelper.calcBuildPos(tablePos.x + buildOffset.x, tablePos.y + buildOffset.y, firstRoadIntersection, buildIntersection, lines, intersectPoints);
            if (buildRoad != null) {
                float costLength = buildRoad.getLength() / 100;
                buildCost = Math.round(costLength * GameInstance.Settings().buildPrice);
                if (!canBuild) {
                    buildCost = 0;
                }
                buildRoad.updatePosition();
            }
            return true;
        }
        return false;
    }

    public boolean touchReleased(MotionEvent event) {
        return clickScreenObject(event, false) || uiManager.touchReleased(event);
    }

    private boolean clickScreenObject(MotionEvent event, boolean clickUIObject) {
        boolean hasSomethingClicked = false;

        if (clickUIObject) {
            // don't trigger UIObjects (buttonstack) when touch released

            for (GameObject tempObject : uiManager.getUIObject()) {
                hasSomethingClicked = clickGameObject(tempObject, (int) event.getX(), (int) event.getY(), false);
                if (hasSomethingClicked) break;

            }
        }
        if (!hasSomethingClicked) {
            for (GameObject tempObject : uiManager.getScreenObjects()) {
                hasSomethingClicked = clickGameObject(tempObject, (int) event.getX(), (int) event.getY(), false);
                if (hasSomethingClicked) break;

            }
        }
        return hasSomethingClicked;
    }

    public boolean OnScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return uiManager.OnScroll(e1, e2, distanceX, distanceY);
    }

    public void tick() {
        if (uiManager != null) {
            uiManager.tick();
        }

        GameInstance.Instance().tick();

        if (GameInstance.Settings().buildRoadSelected) {
            GameInstance.Settings().buildRoadSelected = false;
            uiManager.setSelectableRoadIntersections(null);
        }

        for (Object airportObject : GameInstance.Airport().getClickableObjects()) {
            updateSelected(airportObject);
        }

        if (lastTouchTime > 0) lastTouchTime--;

        if (selectedTime > 0) selectedTime--;
        else setSelected(null);//reset selected

        if (doubleMouseClickTime > 0) doubleMouseClickTime--;

        if (GameInstance.Settings().selectionCompleted) {

            if (GameInstance.Settings().buildDepot != BuildingType.None) {
                setSelectableObjectsForBuildBuilding();
            }
            GameInstance.Settings().selectionCompleted = false;
        }
    }

    void setSelectableObjectsForBuildBuilding() {
        selectableGameObjects.clear();
        for (int i = 0; i < GameInstance.Airport().getRoadCount(); i++) {
            Road road = GameInstance.Airport().getRoad(i);
            if (!(road instanceof Street && road.getBuilding() == null)) {
                continue;
            }
            BuildingType type = GameInstance.Settings().buildDepot;

            if (type == BuildingType.terminal) {
                if (road.getLength() >= GameInstance.Settings().buildMinRadius)
                    selectableGameObjects.add(road);
            } else {

                if (road.getLength() > GameInstance.Settings().buildMinRadius * 1.2)
                    selectableGameObjects.add(road);
            }

        }
    }

    private void updateSelected(Object object) {
        if (object instanceof ClickableGameObject) {
            ClickableGameObject cl = (ClickableGameObject) object;
            cl.setLastSelected(lastSelected);
            cl.setSelected(selected);
        }

    }

    public void render(Canvas canvas, Paint paint) {
        int renderDepth = GameInstance.Settings().RenderDepth;

        for (int i = 0; i < renderQueue.length; i++) {
            renderQueue[i].clear();
        }

        //order GameObjects for different renderlayers
        for (GameObject object : uiManager.getUIObject()) {
            int renderLayer = object.getRenderOrder();
            if (renderLayer > renderDepth - 1) {//is on top off everything
                renderLayer = renderDepth - 1;
            }


            renderQueue[renderLayer].add(object);
        }
        //order GameObjects for different renderlayers
        for (GameObject object : uiManager.getScreenObjects()) {
            int renderLayer = object.getRenderOrder();
            if (renderLayer > renderDepth - 1) {//is on top off everything
                renderLayer = renderDepth - 1;
            }


            renderQueue[renderLayer].add(object);
        }

        RenderAirport.Instance().render(canvas, paint, GameInstance.Airport());

        for (ClickableGameObject clickableGameObject : selectableGameObjects) {
            Render.drawPossibleSelection(canvas, paint, clickableGameObject);
        }

        if (GameInstance.Settings().buildMode == 1) {


            if (GameInstance.Settings().DebugMode) {
                for (Line line : lines) {
                    RenderDebug.renderLine(canvas, paint, line, 1000);
                }
            }
            if (buildRoad != null && buildIntersection != null && GameInstance.Settings().buildRoad != RoadType.None) {

                Render.render(canvas, paint, buildIntersection);
                Render.drawPossibleSelection(canvas, paint, buildIntersection);

                Render.render(canvas, paint, buildRoad);
                RenderGui.drawBuildCost(canvas, paint, buildCost, buildRoad);

                paint.reset();
                paint.setColor(Color.MAGENTA);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(1);
//                PointInt firstInterPos = Render.getPositionForRender(buildIntersection.getPosition().x - buildOffset.x, buildIntersection.getPosition().y - buildOffset.y);
                PointInt firstInterPos = Render.getPositionForRender(buildIntersection.getPosition().x, buildIntersection.getPosition().y);

                canvas.drawCircle(firstInterPos.x, firstInterPos.y, saftyDistance(), paint);

                for (PointFloat intersection : intersectPoints) {
                    RenderGui.drawBuildCollision(canvas, paint, intersection);
                }
            }
        }

        for (LinkedList<GameObject> renderLayer : renderQueue) {
            if (renderLayer == null) continue;

            for (GameObject tempObject : renderLayer) {
                Render.render(canvas, paint, tempObject);
            }
        }
        drawDebug(canvas, paint);
        RenderGui.drawWind(canvas, paint);
        RenderGui.drawMoney(canvas, paint);
        RenderGui.drawMessages(canvas, paint);
    }

    private void drawDebug(Canvas canvas, Paint paint) {
        paint.reset();
        int debugX = 10;
        int debugY = 100;

        paint.reset();
        paint.setColor(Settings.Instance().fontColor);
        paint.setTextSize(15);
        canvas.drawText(cV.message, debugX, debugY, paint);

        if (GameInstance.Settings().DebugMode) {
            if (lastTouchTime > 0) {
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(5);
                canvas.drawCircle(lastTouch.x, lastTouch.y, 20, paint);
            }
            paint.setColor(Color.MAGENTA);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            canvas.drawCircle(lastMousePosition.x, lastMousePosition.y, 10, paint);


            paint.reset();
            paint.setColor(Settings.Instance().fontColor);
            paint.setTextSize(15);
//            canvas.drawText(cV.message, debugX, debugY, paint);
            canvas.drawText("X: " + touchPosOnTable.x + " Y: " + touchPosOnTable.y, debugX, debugY + 20, paint);
            canvas.drawText("CurrentAirplanes: " + GameInstance.Airport().getAirplaneCount(), debugX, debugY + 45, paint);
            canvas.drawText("FinishedAirplane: " + GameInstance.Airport().airplaneCount, debugX, debugY + 60, paint);
            canvas.drawText("CollisionDet: " + GameInstance.Settings().CollisionDetection, debugX, debugY + 75, paint);
            if (selected instanceof Airplane) {
                Airplane plane = (Airplane) selected;
                canvas.drawText("PlaneState: " + plane.getState().name(), debugX, debugY + 120, paint);
                canvas.drawText("Holding Position: " + plane.isHoldPosition(), debugX, debugY + 140, paint);
            }
            int nextAirplanes = GameInstance.Airport().getNextAirplanes().size();
            canvas.drawText("NextAirplaneCount: " + nextAirplanes, debugX, debugY + 160, paint);
        }
    }

    public void repositionAll(PointInt dimension) {
//        BitmapLoader.repositionAll(dimension, objects);
        BitmapLoader.repositionAll(dimension, uiManager.getUIObject());
    }

    private void setSelected(ClickableGameObject gameObject) {
        if (gameObject == null) {
            selected = null;
            GameInstance.Settings().selectedObject = null;
            selectedTime = 0;
            return;
        }

        selected = gameObject;
        if (!(selected instanceof Button)) {
            GameInstance.Settings().selectedObject = selected;
        }

        selectedTime = GameInstance.Settings().SelectionTime;
        lastSelected = gameObject;
        updateSelected(selected);
    }

    void clearSelectableObjects() {
        selectableGameObjects.clear();
        choiceForThis = null;
    }

    public void setupUIManager() {
        uiManager = new UIManager(this, game);
        uiManager.setMainOptions();
    }

    void setAllDeletableObjects() {
        selectableGameObjects.clear();

        for (int i = 0; i < GameInstance.Airport().getBuildingCount(); i++) {
            Building building = GameInstance.Airport().getBuilding(i);
            selectableGameObjects.add(building);
        }
        int roadCount = GameInstance.Airport().getRoadCount();
        if (roadCount > 1) {

            ArrayList<Road> addToSelectRoads = new ArrayList<>();
            ArrayList<Road> userWantsDeleteRoads = new ArrayList<>();
            int taxiwayCount = 0;
            for (int i = 0; i < GameInstance.Airport().getRoadCount(); i++) {
                Road road = GameInstance.Airport().getRoad(i);
                if (road.getBuilding() != null) {
                    continue;
                }
                if (road.isUserWantsDemolition()) {
                    userWantsDeleteRoads.add(road);
                } else {
                    if (road instanceof Taxiway) taxiwayCount++;
                    addToSelectRoads.add(road);
                }
            }
            if (taxiwayCount < 2) {
                //to avoid deleting last taxiway
                for (int i = 0; i < addToSelectRoads.size(); i++) {
                    Road road = addToSelectRoads.get(i);
                    if (road instanceof Taxiway) {
                        addToSelectRoads.remove(i);
                        break;
                    }
                }
            }
            selectableGameObjects.addAll(userWantsDeleteRoads);
            selectableGameObjects.addAll(addToSelectRoads);

        }
    }

    public void setFpsLabel(ChangeValues cV) {
        this.cV = cV;
    }

    public boolean isDraggingBuildIntersection(float mx, float my) {
        lastMousePosition.set(mx, my);
        if (uiManager.getButtonCircleCount() > 0) return true;
        if (GameInstance.Settings().buildMode == 0 || buildIntersection == null) return false;

//        PointInt centerPos = buildIntersection.getPositionForRender(Align_X + width / 2, Align_Y + height / 2);

        PointInt centerPos = GameObject.getPositionForRender(buildIntersection.getPosition().x - buildOffset.x, buildIntersection.getPosition().y - buildOffset.y);
        int diffX = centerPos.x - lastMousePosition.x;
        int diffY = centerPos.y - lastMousePosition.y;
        double distance = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
//        float zoom = GameInstance.Settings().Zoom * GameInstance.Settings().Scale;
//        float radius = zoom * 2000;//should be outside the check and snapradius
//        if (CommonMethods.getDistance(buildIntersection.getPosition(), buildOffset) < saftyDistance()) {
        return distance < saftyDistance();
//        }else{
//            return false;
//        }

    }

    private float saftyDistance() {
        float zoom = GameInstance.Settings().Zoom * GameInstance.Settings().Scale;
        float radius = zoom * 2000;//should be outside the check and snapradius
        return radius;
    }

    public boolean dontDragWorld() {
        return uiManager.getScreenState() != ScreenState.Game;
    }

    public void setIsMoving(boolean isMoving){
        this.isMoving = isMoving;
    }
}
