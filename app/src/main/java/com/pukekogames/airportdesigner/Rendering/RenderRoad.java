package com.pukekogames.airportdesigner.Rendering;

import android.graphics.*;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.CommonMethods;
import com.pukekogames.airportdesigner.Helper.Geometry.Line;
import com.pukekogames.airportdesigner.Helper.Geometry.PointFloat;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Roads.*;
import com.pukekogames.airportdesigner.Settings;

/**
 * Created by Marko Rapka on 30.06.2016.
 */
public class RenderRoad {

    static int TAXIWAYCOLOR = Color.rgb(158, 158, 158);
    static int RUNWAYCOLOR = Color.rgb(139, 133, 133);
    static int STREETCOLOR = Color.rgb(157, 157, 157);

    static float MINZOOMRUNWAY = 0.15f;
    static float MINZOOMTAXIWAY = 0.4f;
    public static float MINZOOMROADMARKINGS = 0.12f;

    static void render(Canvas canvas, Paint paint, Road road) {
        if (!Instance().isRoadInView(road)) {
            road.isInView = false;
            return;
        } else {
            road.isInView = true;
        }

        float ratio = road.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiomin) {
            ratio = 0;
        }
        road.calculatePositions();

        if (road instanceof Runway) {
            Runway runway = (Runway) road;
            Instance().renderRunway(canvas, paint, runway);
        } else if (road instanceof ParkGate) {
            ParkGate parkGate = (ParkGate) road;
            renderParkGate(canvas, paint, parkGate);
        } else if (road instanceof Taxiway) {
            Taxiway taxiway = (Taxiway) road;
            renderTaxiway(canvas, paint, taxiway);
        } else if (road instanceof Street) {
            Street street = (Street) road;
            renderStreet(canvas, paint, street);
        }

        if (road.isUserWantsDemolition()) {
            paint.reset();
            PointInt startPos = Render.getPositionForRender(road.getStartPosition().x, road.getStartPosition().y);
            PointInt endPos = Render.getPositionForRender(road.getEndPosition().x, road.getEndPosition().y);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(7);
            paint.setColor(Color.RED);
            paint.setAlpha((int) (ratio * 255));

            canvas.drawLine(startPos.x, startPos.y, endPos.x, endPos.y, paint);


        }
//        int directionInUse = road.getDirectionInUse();
//        if (directionInUse > 0 && GameInstance.Settings().DebugMode) {
//            paint.reset();
//            PointInt startPos = Render.getPositionForRender(road.getStartPosition().x, road.getStartPosition().y);
//            PointInt endPos = Render.getPositionForRender(road.getEndPosition().x, road.getEndPosition().y);
//            Vector2D direction = new Vector2D(startPos.x, startPos.y, endPos.x, endPos.y);
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setStrokeWidth(5);
//            paint.setColor(Color.YELLOW);
//            paint.setAlpha((int) (ratio * 255));
//
//            if (directionInUse == 1) {
//                canvas.drawLine(startPos.x, startPos.y, startPos.x + direction.getX() / 2, startPos.y + direction.getY() / 2, paint);
//            } else {
//                canvas.drawLine(endPos.x, endPos.y, endPos.x - direction.getX() / 2, endPos.y - direction.getY() / 2, paint);
//            }
//
//
//        }

    }

    Matrix matrix;
    private static RenderRoad ourInstance = new RenderRoad();

    RenderRoad() {
        matrix = new Matrix();
    }


    static RenderRoad Instance() {
        return ourInstance;
    }

    void renderRunway(Canvas canvas, Paint paint, Runway runway) {
        float ratio = runway.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiomin) {
            ratio = 0;
        }

        paint.reset();
        float length = runway.getLength();
        Bitmap bitmap = null;
        Bitmap middle = null;

        if (GameInstance.Settings().Zoom > MINZOOMRUNWAY) {

            bitmap = BitmapLoader.Instance().getBitmap(runway.getImageID());
            middle = BitmapLoader.Instance().getBitmap(runway.getMiddleID());
        }
        float heading = runway.getHeading();
        if (length == 0) return;
        if (bitmap != null) {
            int imageLength = bitmap.getWidth();
            int imageHeight = bitmap.getHeight();

            runway.setDimension(imageLength, imageHeight);
            PointInt renderCenter = Render.getPositionForRender((int) runway.getCenterPosition().x, (int) runway.getCenterPosition().y);
            float scale = GameInstance.Settings().Zoom * GameInstance.Settings().Scale;

//            float scale = GameInstance.Settings().Scale;


            int imageCount = Math.round(length / imageLength);
            float scaleX = (length / (imageCount * imageLength)) * scale;
            paint.setAlpha((int) (ratio * 255));

            for (int i = 0; i < imageCount; i++) {

                matrix.reset();
                if (i == imageCount - 1) {

                    float rotHead = (heading + 180) % 360;
                    matrix.postScale(scaleX, scale);
                    matrix.postTranslate(-length * scale / 2, -imageHeight * scale / 2f);
                    matrix.postRotate(rotHead);
                    matrix.postTranslate(renderCenter.x, renderCenter.y);

                } else {

                    matrix.postScale(scaleX, scale);
                    matrix.postTranslate(-(length * scale / 2) + (i * scaleX * imageLength), -imageHeight * scale / 2f);
                    matrix.postRotate(heading);
                    matrix.postTranslate(renderCenter.x, renderCenter.y);

                }

                if (i == 0 || i == imageCount - 1) {
                    canvas.drawBitmap(bitmap, matrix, paint);
//                    g2d.drawImage(image, transform, null);
                } else {
                    canvas.drawBitmap(middle, matrix, paint);
//                    g2d.drawImage(middle, transform, null);
                }
            }

            if (GameInstance.Settings().DebugMode) {
                paint.setColor(Color.RED);
                paint.setAlpha((int) (ratio * 255));

                paint.setTextSize(Settings.Instance().normalFontSize);
                canvas.drawText("" + runway.getVehiclesOnRoad().size(), renderCenter.x, renderCenter.y, paint);

                if (runway.isBlocked()) {
                    paint.setColor(Color.RED);
                    paint.setAlpha((int) (ratio * 255));
                    int size = 5;
                    canvas.drawRect(renderCenter.x - size, renderCenter.y - size, renderCenter.x + size, renderCenter.y + size, paint);
                }
                if (runway.isBlockedForDeparture()) {
                    paint.setColor(Color.RED);
                    paint.setAlpha((int) (ratio * 255));
                    int size = 5;
                    canvas.drawText("dep. block", renderCenter.x - 40, renderCenter.y - size, paint);

                    canvas.drawRect(renderCenter.x - size, renderCenter.y - 3 * size, renderCenter.x + size, renderCenter.y - size, paint);
                }
            }

        } else {
            drawRoadLine(canvas, paint, runway, ratio, RUNWAYCOLOR);
        }

        RoadIntersection usedIntersection = CommonMethods.getUsedRunwayIntersection(runway, GameInstance.Airport().getWindDirection());
        if (usedIntersection != null) {
            PointInt centerPos = Render.getPositionForRender(runway.getCenterPosition().x, runway.getCenterPosition().y);
            PointInt interPos = Render.getPositionForRender(usedIntersection.getPosition().x, usedIntersection.getPosition().y);

            if (ratio == 1) {
                Render.drawArrow(centerPos, paint, canvas, Color.BLUE, interPos);
            }
        }

        if (runway.isShowDirection()) {
            PointInt renderStart = Render.getPositionForRender((int) runway.getStartPosition().x, (int) runway.getStartPosition().y);
            paint.setColor(Color.GREEN);
            paint.setAlpha((int) (ratio * 255));
            canvas.drawLine(renderStart.x, renderStart.y, renderStart.x + (int) (runway.getDirX()), renderStart.y + (int) (runway.getDirY()), paint);
        }
    }

    static void renderParkGate(Canvas canvas, Paint paint, ParkGate parkGate) {
        paint.reset();
        float ratio = parkGate.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiomin) {
            ratio = 0;
        }

        float length = parkGate.getLength();

        Bitmap bitmap = null;
        if (GameInstance.Settings().Zoom > MINZOOMTAXIWAY) {
            bitmap = BitmapLoader.Instance().getBitmap(parkGate.getImageID());
        }

        if (length == 0) return;
        PointInt renderCenter = Render.getPositionForRender((int) parkGate.getCenterPosition().x, (int) parkGate.getCenterPosition().y);
        if (bitmap != null) {
            int imageLength = bitmap.getWidth();
            int imageHeight = bitmap.getHeight();

            parkGate.setDimension(imageLength, imageHeight);

            Instance().fillRoadImage(canvas, paint, renderCenter, parkGate);



        } else {
            drawRoadLine(canvas, paint, parkGate, ratio, TAXIWAYCOLOR);
        }



        if (GameInstance.Settings().DebugMode) {
            paint.reset();
            paint.setColor(Color.RED);
            paint.setAlpha((int) (ratio * 255));
            paint.setTextSize(Settings.Instance().normalFontSize);
            canvas.drawText("" + parkGate.getVehiclesOnRoad().size(), renderCenter.x, renderCenter.y, paint);
            if (parkGate.isConnectedRoadHasTerminal()){
                paint.setColor(Color.rgb(255,255,0));
                paint.setAlpha((int) (ratio * 255));
                int size = 8;
                canvas.drawRect(renderCenter.x - size, renderCenter.y - size, renderCenter.x + size, renderCenter.y + size, paint);
            }

            if (parkGate.isBlocked()) {
                paint.setColor(Color.RED);
                paint.setAlpha((int) (ratio * 255));
                int size = 5;
                canvas.drawRect(renderCenter.x - size, renderCenter.y - size, renderCenter.x + size, renderCenter.y + size, paint);
            }
        }

    }

    static void renderTaxiway(Canvas canvas, Paint paint, Taxiway taxiway) {
        paint.reset();
        float ratio = taxiway.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiomin) {
            ratio = 0;
        }
        float length = taxiway.getLength();

//        Bitmap bitmap = null;
//        if (GameInstance.Settings().Zoom > MINZOOMTAXIWAY) {
//            bitmap = BitmapLoader.Instance().getBitmap(taxiway.getImageID());
//        }

        if (length == 0) return;
//        if (bitmap != null) {
//            int imageLength = bitmap.getWidth();
//            int imageHeight = bitmap.getHeight();
//
//            taxiway.setDimension(imageLength, imageHeight);
//            PointInt renderCenter = Render.getPositionForRender((int) taxiway.getCenterPosition().x, (int) taxiway.getCenterPosition().y);
//            Instance().fillRoadImage(canvas, paint, renderCenter, taxiway);
//
//            if (GameInstance.Settings().DebugMode) {
//                paint.setColor(Color.RED);
//                paint.setAlpha((int) (ratio * 255));
//                paint.setTextSize(Settings.Instance().normalFontSize);
//                canvas.drawText("" + taxiway.getVehiclesOnRoad().size(), renderCenter.x, renderCenter.y, paint);
//            }
//
//        } else {
        drawRoadLine(canvas, paint, taxiway, ratio, TAXIWAYCOLOR);

        if (GameInstance.Settings().DebugMode) {
            paint.reset();
            PointInt renderCenter = Render.getPositionForRender((int) taxiway.getCenterPosition().x, (int) taxiway.getCenterPosition().y);
            paint.setColor(Color.RED);
            paint.setAlpha((int) (ratio * 255));
            paint.setTextSize(Settings.Instance().normalFontSize);
            canvas.drawText("" + taxiway.getVehiclesOnRoad().size(), renderCenter.x, renderCenter.y, paint);
        }
//            g.drawLine((int) x, (int) y, (int) endX, (int) endY);
//        }
    }

    static void renderStreet(Canvas canvas, Paint paint, Street street) {
        paint.reset();
        float ratio = street.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiomin) {
            ratio = 0;
        }
//        float length = street.getLength();

//        Bitmap bitmap = null;
//        if (GameInstance.Settings().Zoom > MINZOOMRUNWAY) {
//            bitmap = BitmapLoader.Instance().getBitmap(street.getImageID());
//        }

//        if (length == 0) return;
//        if (bitmap != null) {
//            int imageLength = bitmap.getWidth();
//            int imageHeight = bitmap.getHeight();
//
//            street.setDimension(imageLength, imageHeight);
//            PointInt renderCenter = Render.getPositionForRender((int) street.getCenterPosition().x, (int) street.getCenterPosition().y);
//            Instance().fillRoadImage(canvas, paint, renderCenter, street);
//
//            if (GameInstance.Settings().DebugMode) {
//                paint.setColor(Color.RED);
//                paint.setTextSize(Settings.Instance().normalFontSize);
//                paint.setAlpha((int) (ratio * 255));
//                canvas.drawText("" + street.getVehiclesOnRoad().size(), renderCenter.x, renderCenter.y, paint);
//            }
//        }
        drawRoadLine(canvas, paint, street, ratio, STREETCOLOR);

        if (GameInstance.Settings().DebugMode) {
            PointInt renderCenter = Render.getPositionForRender((int) street.getCenterPosition().x, (int) street.getCenterPosition().y);
            paint.reset();
            paint.setColor(Color.RED);
            paint.setAlpha((int) (ratio * 255));
            paint.setTextSize(Settings.Instance().normalFontSize);
            canvas.drawText("" + street.getVehiclesOnRoad().size(), renderCenter.x, renderCenter.y, paint);
        }
    }

    private void fillRoadImage(Canvas canvas, Paint paint, PointInt renderCenter, Road road) {
        paint.reset();
        float scale = GameInstance.Settings().Zoom * GameInstance.Settings().Scale;
//        float scale = GameInstance.Settings().Scale;

        float ratio = road.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiomin) {
            ratio = 0;
        }

        Bitmap bitmap = BitmapLoader.Instance().getBitmap(road.getImageID());
        float length = road.getLength();
        float heading = road.getHeading();

        int imageLength = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        int imageCount = Math.round(length / imageLength);
        float scaleX = (length / (imageCount * imageLength)) * scale;


        matrix.reset();
        for (int i = 0; i < imageCount; i++) {
            matrix.reset();
            if (i == imageCount - 1) {

                float rotHead = (heading + 180) % 360;
                matrix.postScale(scaleX, scale);
                matrix.postTranslate(-length * scale / 2, -imageHeight * scale / 2f);
                matrix.postRotate(rotHead);
                matrix.postTranslate(renderCenter.x, renderCenter.y);

            } else {

                matrix.postScale(scaleX, scale);
                matrix.postTranslate(-(length * scale / 2) + (i * scaleX * imageLength), -imageHeight * scale / 2f);
                matrix.postRotate(heading);
                matrix.postTranslate(renderCenter.x, renderCenter.y);

            }
            paint.setAlpha((int) (ratio * 255));
            canvas.drawBitmap(bitmap, matrix, paint);
        }
    }

    static void drawPossibleSelection(Canvas canvas, Paint paint, Road road) {
        paint.reset();
        float ratio = road.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiomin) {
            ratio = 0;
        }
        PointFloat position = road.getCenterPosition();
        boolean selected = road.isSelected();
        PointInt renderCenter = Render.getPositionForRender((int) position.x, (int) position.y);
        float scale = GameInstance.Settings().Zoom * GameInstance.Settings().Scale;
        int radius = (int) (150 * scale);
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

    private static void drawRoadLine(Canvas canvas, Paint paint, Road road, float ratio, int color) {
        PointInt startPoint = Render.getPositionForRender((int) road.getStartPosition().x, (int) road.getStartPosition().y);
        PointInt endPoint = Render.getPositionForRender((int) road.getEndPosition().x, (int) road.getEndPosition().y);

        float scale = GameInstance.Settings().Zoom * GameInstance.Settings().Scale;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(400 * scale);
        paint.setColor(color);
        paint.setAlpha((int) (ratio * 255));
        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);


    }

    public static void drawRoadMarkings(Canvas canvas, Paint paint, Road road) {
        if (!road.isInView) return;
        if (!(road instanceof Taxiway || road instanceof Street)) return;

        PointInt startPoint = Render.getPositionForRender((int) road.getStartPosition().x, (int) road.getStartPosition().y);
        PointInt endPoint = Render.getPositionForRender((int) road.getEndPosition().x, (int) road.getEndPosition().y);

        float scale = GameInstance.Settings().Zoom * GameInstance.Settings().Scale;
        float ratio = road.getAni_Ratio();
        if (road instanceof Taxiway) {
            paint.setStyle(Paint.Style.STROKE);
            if (road.getDirectionInUse() != 0) {
                paint.setStrokeWidth(30 * scale);
            } else {
                paint.setStrokeWidth(20 * scale);
            }

            paint.setColor(Color.YELLOW);
            paint.setAlpha((int) (ratio * 255));
            canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);

        } else if (road instanceof Street) {

            if (GameInstance.Settings().Zoom > MINZOOMRUNWAY) {
                //show "normal" street markings
                float centerLineLength = 150;
                float length = road.getLength();

                int imageCount = Math.round(length / centerLineLength);
                float dirX = endPoint.x - startPoint.x;
                float dirY = endPoint.y - startPoint.y;
                float dirX1 = (dirX / (length * scale));
                float dirY1 = (dirY / (length * scale));

                float xOffset = dirX1 * (centerLineLength * scale / 2);
                float yOffset = dirY1 * (centerLineLength * scale / 2);

                float xEndPointDir = dirX1 * centerLineLength * scale;
                float yEndPointDir = dirY1 * centerLineLength * scale;


                for (int i = 0; i < imageCount / 2; i++) {

                    PointInt startLinePoint = new PointInt(
                            startPoint.x + dirX1 * i * 2 * centerLineLength * scale + xOffset,
                            startPoint.y + dirY1 * i * 2 * centerLineLength * scale + yOffset);
                    PointInt endLinePoint = new PointInt(startLinePoint.x + xEndPointDir, startLinePoint.y + yEndPointDir);


                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(20 * scale);
                    paint.setColor(Color.WHITE);
                    paint.setAlpha((int) (ratio * 255));
                    canvas.drawLine(startLinePoint.x, startLinePoint.y, endLinePoint.x, endLinePoint.y, paint);

                }
            } else {
                //show simple white line
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(20 * scale);
                paint.setColor(Color.WHITE);
                paint.setAlpha((int) (ratio * 255));
                canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);

            }

        }
    }


    private boolean isRoadInView(Road road) {

        PointInt startPoint = Render.getPositionForRender((int) road.getStartPosition().x, (int) road.getStartPosition().y);
        PointInt endPoint = Render.getPositionForRender((int) road.getEndPosition().x, (int) road.getEndPosition().y);

        boolean startPointInView = Render.isPositionInView(startPoint);
        boolean endPointInView = Render.isPositionInView(endPoint);

        if (startPointInView || endPointInView) {
            return true;
        }
        Line roadLine = new Line(startPoint.x, startPoint.y, endPoint.x - startPoint.x, endPoint.y - startPoint.y);
        boolean intersectTop = Render.ScreenTopLine.intersectLine(roadLine);
        if (intersectTop) return true;
        boolean intersectLeft = Render.ScreenLeftLine.intersectLine(roadLine);
        if (intersectLeft) return true;
        boolean intersectRight = Render.ScreenRightLine.intersectLine(roadLine);
        if (intersectRight) return true;
        boolean intersectBottom = Render.ScreenBottomLine.intersectLine(roadLine);
        return intersectBottom;
    }

}
