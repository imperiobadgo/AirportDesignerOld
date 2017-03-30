package com.pukekogames.airportdesigner.Main;

import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Helper.Geometry.Vector2D;
import com.pukekogames.airportdesigner.Handler;
import com.pukekogames.airportdesigner.Settings;

/**
 * Created by Marko Rapka on 31.05.2016.
 */
public class TouchHandler extends ScaleGestureDetector.SimpleOnScaleGestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private Handler handler;
    private PointF lastTouchPos;
    PointInt mouseRel;
    PointInt centerScreen;
    PointInt tableCenterScreenBeforeZoom;
    PointInt tableCenterScreen;
    Vector2D zoomMove;

    public TouchHandler(Handler handler) {
        this.handler = handler;
        lastTouchPos = new PointF();
        mouseRel = new PointInt();
        centerScreen = new PointInt();
        tableCenterScreenBeforeZoom = new PointInt();
        tableCenterScreen = new PointInt();
        zoomMove = new Vector2D(0, 0);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        //GameInstance.Settings().DebugMode = !GameInstance.Settings().DebugMode;
//        Log.i(Game.TAG, "Debugmode: " + (Settings.Instance().DebugMode ? "on" : "off"));
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
//        if (handler.getLastTouchTime() <= 0) {
            handler.OnTouch(e);
//        }
        lastTouchPos.set(e.getX(), e.getY());
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (handler.dontDragWorld()) return false;
        //set the table back to the center of the screen
        PointInt dimension = GameInstance.Settings().screenSize;
        centerScreen.set(dimension.x / 2, dimension.y / 2);

        float oldZoom = GameInstance.Settings().Zoom;
        float zoom = Math.max(Settings.Instance().minZoom, Math.min(oldZoom * detector.getScaleFactor(), Settings.Instance().maxZoom));
        tableCenterScreenBeforeZoom = handler.mouseToTablePos(centerScreen.x, centerScreen.y);
        GameInstance.Settings().Zoom = zoom;


        float deltaZoom = oldZoom - zoom;
        PointInt tableCenter = GameInstance.Settings().MapCenter;
        tableCenterScreen = handler.mouseToTablePos(centerScreen.x, centerScreen.y);

        float translateFactor = 0.5f;
        //move difference for zooming to specific point
        zoomMove.set(tableCenterScreen.x - tableCenterScreenBeforeZoom.x, tableCenterScreen.y - tableCenterScreenBeforeZoom.y);

        zoomMove.Multiply(zoom);

        tableCenter.x = (int) (tableCenter.x + zoomMove.getX() * translateFactor);
        tableCenter.y = (int) (tableCenter.y + zoomMove.getY() * translateFactor);

        if (deltaZoom > 0) {
            //only reset camera when zooming out
            if (zoom < (Settings.Instance().minZoom + Settings.Instance().maxZoom) / 10) {//just on the "last" bit

                mouseRel.set(tableCenter.x - centerScreen.x, tableCenter.y - centerScreen.y);
                float factor = 0.05f;

                tableCenter.x = (int) (tableCenter.x - mouseRel.x * factor);
                tableCenter.y = (int) (tableCenter.y - mouseRel.y * factor);
            }
        }

        return deltaZoom != 0;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return handler.OnScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}
