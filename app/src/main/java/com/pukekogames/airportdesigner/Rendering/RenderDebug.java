package com.pukekogames.airportdesigner.Rendering;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.pukekogames.airportdesigner.Helper.Geometry.Line;
import com.pukekogames.airportdesigner.Helper.Geometry.PointFloat;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Settings;


/**
 * Created by Marko Rapka on 31.07.2016.
 */
public class RenderDebug {
    public static void renderLine(Canvas canvas,Paint paint, Line line, float length) {
        paint.reset();
        paint.setColor(Settings.Instance().attentionColor);

        PointFloat centerPoint = line.getStartPoint();
        PointFloat direction = line.getDirection();

        PointInt start = Render.getPositionForRender(centerPoint.x - direction.x * length, centerPoint.y - direction.y * length);
        PointInt end = Render.getPositionForRender(centerPoint.x + direction.x * length, centerPoint.y + direction.y * length);

        canvas.drawLine(start.x, start.y, end.x, end.y, paint);

    }
}
