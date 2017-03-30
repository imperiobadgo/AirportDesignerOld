package com.pukekogames.airportdesigner.Rendering;

import android.graphics.*;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Objects.Buildings.Building;
import com.pukekogames.airportdesigner.Objects.Buildings.Depot;
import com.pukekogames.airportdesigner.Objects.Buildings.Tower;
import com.pukekogames.airportdesigner.Objects.Vehicles.StreetVehicle;
import com.pukekogames.airportdesigner.Settings;

/**
 * Created by Marko Rapka on 23.09.2016.
 */
public class RenderBuilding {

    private static Matrix matrix;
    private static PointInt startPos = null;
    private static PointInt endPos = null;


    public static void render(Canvas canvas, Paint paint, Building building) {
        if (matrix == null) {
            matrix = new Matrix();
        }

        if (startPos == null) {
            startPos = new PointInt();
        }
        if (endPos == null) {
            endPos = new PointInt();
        }

        paint.reset();

        float ratio = building.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiomin) {
            ratio = 0;
        }

        Bitmap bitmap = BitmapLoader.Instance().getBitmap(building.getImageID());
        if (bitmap != null) {
            PointInt centerPos = Render.getPositionForRender(building.getAlign_X(), building.getAlign_Y());
            building.setDimension(bitmap.getWidth(), bitmap.getHeight());
            float scale = GameInstance.Settings().Zoom;

            float heading = building.getHeading();

            int scaledWidth = (int) (scale * bitmap.getWidth());
            int scaledHeight = (int) (scale * bitmap.getHeight());

            matrix.reset();

            matrix.postScale(scale, scale);
            matrix.postTranslate(-scaledWidth / 2f, -scaledHeight / 2f);
            matrix.postRotate(heading + 90);
            matrix.postTranslate(centerPos.x, centerPos.y);

            paint.setColor(Color.rgb(255, 255, 255));
            paint.setAlpha((int) (ratio * 255));

            canvas.drawBitmap(bitmap, matrix, paint);

            if (building.isUserWantsDemolition()) {
                paint.reset();
                int radius = 30;

                startPos.set((float) (Math.cos(Math.toRadians(heading)) * radius + centerPos.x), (float) (Math.sin(Math.toRadians(heading)) * radius + centerPos.y));

                endPos.set((float) (-Math.cos(Math.toRadians(heading)) * radius + centerPos.x), (float) (-Math.sin(Math.toRadians(heading)) * radius + centerPos.y));

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                paint.setColor(Color.RED);
                paint.setAlpha((int) (ratio * 255));

                canvas.drawLine(startPos.x, startPos.y, endPos.x, endPos.y, paint);
            }

            if (building instanceof Depot) {
                if (((Depot)building).isOnLimit() && !(building instanceof Tower)) {
                    paint.reset();
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(2);
                    paint.setColor(Color.MAGENTA);
                    paint.setAlpha((int) (ratio * 255));

                    int radius = (int) (Math.min(scaledHeight, scaledWidth) * 0.4);
                    canvas.drawCircle(centerPos.x, centerPos.y, radius, paint);
                }
            }
            if (building.isSelected()) {

                int radius = (int) (Math.min(scaledHeight, scaledWidth) * 0.5);

                paint.setColor(Settings.Instance().selectedColor);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                paint.setAlpha((int) (ratio * 255));
                canvas.drawCircle(centerPos.x, centerPos.y, radius, paint);

                if (building instanceof Depot) {
                    Depot depot = (Depot) building;
                    //show vehicles from this building
                    for (int i = 0; i < depot.getVehicleOnTheRoadCount(); i++) {
                        StreetVehicle vehicle = depot.getVehicleOnTheRoad(i);
                        if (vehicle == null) continue;
                        RenderVehicle.drawAttentionForVehicle(canvas, paint, vehicle, centerPos);
                    }
                }

            }
        }


    }

    public static void drawPossibleSelection(Canvas canvas, Paint paint, Building building) {
        paint.reset();
        float ratio = building.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiomin) {
            ratio = 0;
        }

        boolean selected = building.isSelected();
        PointInt renderCenter = Render.getPositionForRender((int) building.getAlign_X(), (int) building.getAlign_Y());
        float scale = GameInstance.Settings().Zoom * GameInstance.Settings().Scale;
        int radius = (int) (250 * scale);
        if (selected) {
            paint.setColor(Settings.Instance().selectedColor);
        } else {
            paint.setColor(Settings.Instance().possibleSelectionColor);
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setAlpha((int) (ratio * 255));
        canvas.drawCircle(renderCenter.x, renderCenter.y, radius, paint);
    }
}
