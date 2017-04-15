package com.pukekogames.airportdesigner.Rendering;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Geometry.Line;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Objects.*;
import com.pukekogames.airportdesigner.Objects.Buildings.Building;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.UIElements.Button;
import com.pukekogames.airportdesigner.Objects.UIElements.Label;
import com.pukekogames.airportdesigner.Objects.UIElements.ScrollList;
import com.pukekogames.airportdesigner.Objects.UIElements.TimeTable;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;
import com.pukekogames.airportdesigner.Objects.Vehicles.Bus;
import com.pukekogames.airportdesigner.Objects.Vehicles.Vehicle;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 30.06.2016.
 */
public class Render {

    static float ratiomin = 0.1f;
    static float ratiominButton = 0.5f;
    static Line ScreenTopLine;
    static Line ScreenLeftLine;
    static Line ScreenRightLine;
    static Line ScreenBottomLine;
    public static int renderCalls = 0;

    public static void render(Canvas canvas, Paint paint, GameObject object) {
        if (object == null) return;

        if (ScreenTopLine == null){
            ScreenTopLine = new Line(0,0,GameInstance.Settings().screenSize.x, 0);
        }
        if (ScreenLeftLine == null){
            ScreenLeftLine = new Line(0,0,0, GameInstance.Settings().screenSize.y);
        }
        if (ScreenRightLine == null){
            ScreenRightLine = new Line(GameInstance.Settings().screenSize.x,0,GameInstance.Settings().screenSize.x, GameInstance.Settings().screenSize.y);
        }
        if (ScreenBottomLine == null){
            ScreenBottomLine = new Line(0,GameInstance.Settings().screenSize.y,GameInstance.Settings().screenSize.x, GameInstance.Settings().screenSize.y);
        }

        if (object.getAni_Ratio() > 0) {
            //moving object animation
            float ratio = object.getAni_Ratio();
            float oldX = BitmapLoader.getRepositionX(object);
            float oldY = BitmapLoader.getRepositionY(object);
            float aniX = object.getAni_X();
            float aniY = object.getAni_Y();
            float diffX = oldX - aniX;
            float diffY = oldY - aniY;

            object.setPosition(aniX + diffX * ratio, aniY + diffY * ratio);
        }
        renderCalls += 1;
        if (object instanceof Vehicle) {
            Vehicle vehicle = (Vehicle) object;
            RenderVehicle.render(canvas, paint, vehicle);
        } else if (object instanceof Road) {
            Road road = (Road) object;
            RenderRoad.render(canvas, paint, road);
        } else if (object instanceof RoadIntersection) {
            RoadIntersection roadIntersection = (RoadIntersection) object;
            RenderRoadIntersection.render(canvas, paint, roadIntersection);
        } else if (object instanceof Building) {
            Building building = (Building) object;
            RenderBuilding.render(canvas, paint, building);
        } else if (object instanceof Button) {
            Button button = (Button) object;
            RenderGui.renderButton(canvas, paint, button);
        }else if (object instanceof ScrollList) {
            ScrollList slotList = (ScrollList) object;
            RenderGui.renderTimeSlotList(canvas, paint, slotList);
        }else if (object instanceof TimeTable){
            TimeTable table = (TimeTable) object;
            RenderTimeTable.render(canvas, paint, table);
        }else if (object instanceof Label){
            Label label = (Label) object;
            RenderGui.renderLabel(canvas,paint,label);
        }

    }

    public static void drawAttention(PointInt centerPos, Paint paint, Canvas canvas, int attentionColor, PointInt sourcePoint) {
        int dis = 50;
        if (centerPos.x - dis < 0 || centerPos.x + dis > GameInstance.Settings().screenSize.x || centerPos.y - dis < 0 || centerPos.y + dis > GameInstance.Settings().screenSize.y) {
            drawArrow(centerPos, paint, canvas, attentionColor, sourcePoint);
        }
    }

    static void drawArrow(PointInt centerPos, Paint paint, Canvas canvas, int attentionColor, PointInt sourcePoint){
        PointInt middlePos;

        if (sourcePoint == null){
            //CenterScreenPoint
            middlePos = new PointInt(GameInstance.Settings().screenSize.x / 2, GameInstance.Settings().screenSize.y / 2);
        }else{
            middlePos = sourcePoint;
        }

        double dirX = centerPos.x - middlePos.x;
        double dirY = centerPos.y - middlePos.y;
        double length = Math.sqrt(Math.pow(dirX, 2) + Math.pow(dirY, 2));
        dirX /= length;
        dirY /= length;
        double direction = Math.atan2(dirY, dirX);
        double angleSeparation = Math.toRadians(7);
        double leftDirection = (direction - angleSeparation) % 360;
        double rightDirection = (direction + angleSeparation) % 360;
        int innerRadius = 60;
        int outerRadius = 100;
        int sideRadius = 90;
        PointInt startPos = new PointInt((int) (middlePos.x + dirX * innerRadius), (int) (middlePos.y + dirY * innerRadius));
        PointInt endPos = new PointInt((int) (middlePos.x + dirX * outerRadius), (int) (middlePos.y + dirY * outerRadius));
        double directionX = Math.cos(leftDirection);
        double directionY = Math.sin(leftDirection);
        PointInt leftPos = new PointInt((int) (middlePos.x + directionX * sideRadius), (int) (middlePos.y + directionY * sideRadius));
        directionX = Math.cos(rightDirection);
        directionY = Math.sin(rightDirection);
        PointInt rightPos = new PointInt((int) (middlePos.x + directionX * sideRadius), (int) (middlePos.y + directionY * sideRadius));

        paint.setColor(attentionColor);
        paint.setStrokeWidth(2);
        canvas.drawLine(startPos.x, startPos.y, endPos.x, endPos.y, paint);
        canvas.drawLine(leftPos.x, leftPos.y, endPos.x, endPos.y, paint);
        canvas.drawLine(rightPos.x, rightPos.y, endPos.x, endPos.y, paint);
    }

    private static PointInt tableTopPosition = null;

    public static PointInt getPositionForRender(float x, float y) {
        float zoom = GameInstance.Settings().Zoom * GameInstance.Settings().Scale;
        PointInt tableCenter = GameInstance.Settings().MapCenter;
        int tableSizeX = GameInstance.Settings().MapSizeX;
        int tableSizeY = GameInstance.Settings().MapSizeY;

        if (tableTopPosition == null) {
            tableTopPosition = new PointInt();
        }
        //Position of the top left corner of the table in the Viewport
        tableTopPosition.set(tableCenter.x - tableSizeX * zoom, tableCenter.y - tableSizeY * zoom);
        return new PointInt(x * zoom + tableTopPosition.x, y * zoom + tableTopPosition.y);
    }

    static boolean isPositionInView(PointInt pos){
        int screenWidth = GameInstance.Settings().screenSize.x;
        int screenHeight = GameInstance.Settings().screenSize.y;
        int puffer = 20;
        return !(pos.x < -puffer || pos.x > screenWidth + puffer || pos.y < -puffer || pos.y > screenHeight + puffer);
    }


    public static void drawPossibleSelection(Canvas canvas, Paint paint, ClickableGameObject object) {
        if (object instanceof Airplane) {
            Airplane airplane = (Airplane) object;

        } else if (object instanceof Bus) {
            Bus bus = (Bus) object;

        } else if (object instanceof Road) {
            Road road = (Road) object;
            RenderRoad.drawPossibleSelection(canvas, paint, road);
        } else if (object instanceof RoadIntersection) {
            RoadIntersection roadIntersection = (RoadIntersection) object;
            RenderRoadIntersection.drawPossibleSelection(canvas, paint, roadIntersection, false);
        } else if (object instanceof Building) {
            Building building = (Building) object;
            RenderBuilding.drawPossibleSelection(canvas, paint, building);
        }
    }

    private static PointInt posStart = null;
    private static PointInt posEnd = null;

    static void drawPath(Canvas canvas, Paint paint, ArrayList<Road> nextRoads) {
        paint.reset();

        paint.setColor(Color.RED);

        if (posStart == null) {
            posStart = new PointInt();
        }
        if (posEnd == null) {
            posEnd = new PointInt();
        }

        for (Road road : nextRoads) {

            posStart.x = (int) road.getStartPosition().x;
            posStart.y = (int) road.getStartPosition().y;
            posStart = getPositionForRender(posStart.x, posStart.y);

            posEnd.x = (int) road.getEndPosition().x;
            posEnd.y = (int) road.getEndPosition().y;
            posEnd = getPositionForRender(posEnd.x, posEnd.y);
            canvas.drawLine(posStart.x, posStart.y, posEnd.x, posEnd.y, paint);
            canvas.drawLine(posStart.x - 1, posStart.y - 1, posEnd.x + 1, posEnd.y + 1, paint);
        }
    }
}
