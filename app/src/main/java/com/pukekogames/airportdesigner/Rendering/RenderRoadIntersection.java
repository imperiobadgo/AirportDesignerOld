package com.pukekogames.airportdesigner.Rendering;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Geometry.PointFloat;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Settings;


/**
 * Created by Marko Rapka on 30.06.2016.
 */
public class RenderRoadIntersection {

    static void render(Canvas canvas,Paint paint, RoadIntersection roadIntersection){
        paint.reset();
        float ratio = roadIntersection.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiomin) {
            ratio = 0;
        }
        PointInt renderCenter = Render.getPositionForRender((int) roadIntersection.getPosition().x, (int) roadIntersection.getPosition().y);

        if(!Render.isPositionInView(renderCenter)) return;

        float scale = GameInstance.Settings().Zoom * GameInstance.Settings().Scale;
        int radius = (int) (BitmapLoader.Instance().getBitmap(Images.indexRunwayMiddle).getHeight() * scale / 2f);
        paint.setColor(RenderRoad.TAXIWAYCOLOR);
        paint.setAlpha((int) (ratio * 255));
        canvas.drawCircle(renderCenter.x, renderCenter.y, radius, paint);
//        canvas.drawOval( - radius, renderCenter.y - radius, radius * 2, radius * 2, paint);
    }

    static void drawPossibleSelection(Canvas canvas,Paint paint, RoadIntersection roadIntersection, boolean error){
        paint.reset();
        float ratio = roadIntersection.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiomin) {
            ratio = 0;
        }
        PointFloat position = roadIntersection.getPosition();
        boolean selected = roadIntersection.isSelected();
        PointInt renderCenter = Render.getPositionForRender((int) position.x, (int) position.y);
        float scale = GameInstance.Settings().Zoom * GameInstance.Settings().Scale;
        int radius = (int) (200 * scale);
        if (selected) {
            paint.setColor(Settings.Instance().selectedColor);
        }else {
            paint.setColor(Settings.Instance().possibleSelectionColor);
        }
        if (error){
            paint.setColor(Color.RED);
            radius = (int) (220 * scale);
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        paint.setAlpha((int) (ratio * 255));
        canvas.drawCircle(renderCenter.x, renderCenter.y, radius, paint);
    }
}
