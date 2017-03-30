package com.pukekogames.airportdesigner.Rendering;

import android.graphics.*;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.GameLogic.GameplayWarning;
import com.pukekogames.airportdesigner.Helper.Geometry.PointFloat;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;
import com.pukekogames.airportdesigner.Objects.Vehicles.StreetVehicle;
import com.pukekogames.airportdesigner.Objects.Vehicles.Vehicle;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplaneState;
import com.pukekogames.airportdesigner.Settings;

/**
 * Created by Marko Rapka on 25.09.2016.
 */
public class RenderVehicle {

    private static Matrix matrix;

    public static void render(Canvas canvas, Paint paint, Vehicle vehicle) {
        if (matrix == null) {
            matrix = new Matrix();
        }
        if (vehicle instanceof Airplane) {
            Airplane airplane = (Airplane) vehicle;
            renderAirplane(canvas, paint, airplane);
        } else if (vehicle instanceof StreetVehicle) {
            StreetVehicle streetVehicle = (StreetVehicle) vehicle;
            renderStreetVehicle(canvas, paint, streetVehicle);
        }

        float ratio = vehicle.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiomin) {
            ratio = 0;
        }

        drawClosestVehicle(canvas, paint, vehicle);

        if (vehicle.getWarnings().size() > 0) {
            paint.reset();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.RED);
            paint.setAlpha((int) (ratio * 255));

            PointInt centerPos = Render.getPositionForRender(vehicle.getAlign_X(), vehicle.getAlign_Y());
            Bitmap bitmap = BitmapLoader.Instance().getBitmap(vehicle.getImageID());
            float scale = GameInstance.Settings().Zoom;
            int scaledWidth;
            int scaledHeight;
            if (bitmap != null) {

                scaledWidth = (int) (scale * bitmap.getWidth());
                scaledHeight = (int) (scale * bitmap.getHeight());

            } else {
                scaledWidth = (int) (scale * vehicle.getWidth());
                scaledHeight = (int) (scale * vehicle.getHeight());

            }
            int radius = (int) (Math.min(scaledHeight, scaledWidth) * 0.3);
            canvas.drawCircle(centerPos.x, centerPos.y, radius, paint);

            if (vehicle.isSelected()) {
                float startX = 200;
                float startY = 30;
                float height = 20;
                paint.setColor(Color.RED);
                paint.setAlpha((int) (ratio * 255));
                paint.setTextSize(25);
                for (int i = 0; i < vehicle.getWarnings().size(); i++) {
                    GameplayWarning warning = vehicle.getWarnings().get(i);
                    canvas.drawText(warning.name(), startX, startY + i * height, paint);

                }
                if (vehicle.getWarnings().contains(GameplayWarning.cantFindPath)) {
                    DrawTargetIntersection(canvas, paint, vehicle.getTargetIntersection());
                }
            }
        }

    }

    static void renderStreetVehicle(Canvas canvas, Paint paint, StreetVehicle vehicle) {
        paint.reset();
//        drawVisitedPoints(g);
        float ratio = vehicle.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiomin) {
            ratio = 0;
        }
        boolean selected = vehicle.isSelected();
        if (selected) Render.drawPath(canvas, paint, vehicle.getNextRoads());

        PointInt centerPos = Render.getPositionForRender(vehicle.getAlign_X(), vehicle.getAlign_Y());
        Bitmap bitmap = BitmapLoader.Instance().getBitmap(vehicle.getImageID());

        if (bitmap != null) {
            vehicle.setDimension(bitmap.getWidth(), bitmap.getHeight());
            float scale = GameInstance.Settings().Zoom * 0.35f;
            float heading = vehicle.getHeading();

            int scaledWidth = (int) (scale * bitmap.getWidth());
            int scaledHeight = (int) (scale * bitmap.getHeight());

            matrix.reset();

            matrix.postScale(scale, scale);
            matrix.postTranslate(-scaledWidth / 2f, -scaledHeight / 1.6f);
            matrix.postRotate(heading + 90);
            matrix.postTranslate(centerPos.x, centerPos.y);

            paint.setColor(Color.rgb(255, 255, 255));
            paint.setAlpha((int) (ratio * 255));
            canvas.drawBitmap(bitmap, matrix, paint);

            if (selected) {

                paint.setColor(Settings.Instance().fontColor);
                paint.setAlpha((int) (ratio * 255));
                paint.setTextSize(Settings.Instance().normalFontSize);
                if (vehicle.getServiceTimeLeft() > 0)
                    canvas.drawText("bt:" + vehicle.getServiceTimeLeft(), centerPos.x - scaledWidth / 2, (int) (centerPos.y - scaledHeight / 1.5), paint);
            }

        }
//        if (GameInstance.Settings().DebugMode) {
//
//
//            RoadIntersection lastTarget = vehicle.getTargetIntersection();
//            if (lastTarget != null) {
//                paint.reset();
//
//                paint.setColor(Color.RED);
//
//                int size = 5;
//                PointFloat lastTargetPoint = lastTarget.getPosition();
//                PointInt renderTarget = Render.getPositionForRender(lastTargetPoint.x, lastTargetPoint.y);
//                canvas.drawRect(renderTarget.x - size, renderTarget.y - size, renderTarget.x + size, renderTarget.y + size, paint);
//
//                PointInt roadTarget = vehicle.getRoadTarget();
//                PointInt headingPoint = vehicle.getHeadingPoint();
//
//                renderTarget = Render.getPositionForRender((float) roadTarget.x, (float) roadTarget.y);
//                canvas.drawRect(renderTarget.x - size, renderTarget.y - size, renderTarget.x + size, renderTarget.y + size, paint);
//                renderTarget = Render.getPositionForRender((float) headingPoint.x, (float) headingPoint.y);
//                canvas.drawLine(centerPos.x, centerPos.y, renderTarget.x, renderTarget.y, paint);
//            }
//        }

    }

    static void renderAirplane(Canvas canvas, Paint paint, Airplane airplane) {

        boolean selected = airplane.isSelected();
        float ratio = airplane.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiomin) {
            ratio = 0;
        }
//        drawVisitedPoints(g);
        if (selected) Render.drawPath(canvas, paint, airplane.getNextRoads());


        PointInt centerPos = Render.getPositionForRender(airplane.getAlign_X(), airplane.getAlign_Y());
        airplane.setCenterPos(centerPos);
        Bitmap bitmap = BitmapLoader.Instance().getBitmap(airplane.getImageID());


        if (selected && (airplane.getState() == AirplaneState.Boarding || airplane.getState() == AirplaneState.ArrivedAtGate)) {
            //draw vehicle heading for this airplane
            StreetVehicle vehicle = GameInstance.Airport().getVehicleForAirplane(airplane);
            drawAttentionForVehicle(canvas, paint, vehicle, centerPos);
        }

        paint.reset();

        if (bitmap != null) {
            airplane.setDimension(bitmap.getWidth(), bitmap.getHeight());
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

            int scaledWidth = Math.round(scale * bitmap.getWidth());
            int scaledHeight = Math.round(scale * bitmap.getHeight());

            if (altitude > 0.0f) {
                String altitudeStr = Math.round(altitude) + "";
                paint.setColor(Settings.Instance().fontColor);
                paint.setAlpha((int) (ratio * 255));

                paint.setTextSize(Settings.Instance().normalFontSize);
                canvas.drawText(altitudeStr, centerPos.x - scaledWidth / 2, (int) (centerPos.y - scaledHeight / 1.5), paint);
            }
            if (GameInstance.Settings().DebugMode || selected) {
                String speedStr = "s: " + Math.round(speed);
                paint.setColor(Settings.Instance().fontColor);
                paint.setAlpha((int) (ratio * 255));

                paint.setTextSize(Settings.Instance().normalFontSize);
                canvas.drawText(speedStr, centerPos.x - scaledWidth / 2, (int) (centerPos.y + scaledHeight / 1.5), paint);
                if (waitTime > 0)
                    canvas.drawText("bt:" + waitTime, centerPos.x - scaledWidth / 2, (int) (centerPos.y - scaledHeight / 1.5), paint);
                if (turnaroundTime > 0) {
                    float p = (float) turnaroundTime / (float) maxTurnaroundTime;
                    float subtractPercent = startCriticalTime / maxTurnaroundTime;

//                    canvas.drawText("tt: " + Math.round(255 * p) + " " + turnaroundTime, (int) (centerPos.x - scaledWidth / 1.5), (int) (centerPos.y - scaledHeight / 2), paint);
                    canvas.drawText("tt: " + Math.round(turnaroundTime / 100) + "/" + Math.round(maxTurnaroundTime / 100), (int) (centerPos.x - scaledWidth / 1.5), centerPos.y - scaledHeight / 2, paint);
                }
            }

            if (turnaroundTime > startCriticalTime) {
                float percent = ((float) turnaroundTime - startCriticalTime) / ((float) maxTurnaroundTime - startCriticalTime);
                if (percent > 1) {
                    percent = 1;
                }

                paint.setColor(Color.argb(Math.round(ratio * (255 * percent)), 255, 100, 0));

                int radius = (int) (Math.min(scaledHeight, scaledWidth) * 0.3);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(7);
                canvas.drawCircle(centerPos.x, centerPos.y, radius, paint);
            }

            matrix.reset();

            matrix.postScale(scale, scale);
            matrix.postTranslate(-scaledWidth / 2f, -scaledHeight / 2f);
            matrix.postRotate(heading + 90);
            matrix.postTranslate(centerPos.x, centerPos.y);

            paint.setColor(Color.rgb(255, 255, 255));
            paint.setAlpha((int) (ratio * 255));
            canvas.drawBitmap(bitmap, matrix, paint);

//            AffineTransform orig = g2d.getTransform();
//
//            // First scale, second rotate, and third translate image
//            AffineTransform transform = AffineTransform.getTranslateInstance(centerPos.x - scaledWidth / 2, centerPos.y - scaledHeight / 2);
//            transform.rotate(Math.toRadians(heading + 90), scaledWidth / 2, scaledHeight / 2);
//            transform.scale(scale, scale);
//
//
//            g2d.drawImage(image, transform, null);
//
//
//            AffineTransform af = new AffineTransform();
//            af.rotate(Math.toRadians(heading + 90), collisionBox.getCenterX(), collisionBox.getCenterY());
//            Area area = new Area(collisionBox);
//            area = area.createTransformedArea(af);
//            g2d.draw(area);
            boolean showCollisionBox = selected || holdPosition;
            switch (state) {
                case Init:
                    break;
                case Waiting:
                    paint.setColor(Settings.Instance().attentionColor);
                    showCollisionBox = true;
                    break;
                case Boarding:
                    paint.setColor(Color.CYAN);
                    break;
                case ReadyForPushback:
                    paint.setColor(Settings.Instance().attentionColor);
                    showCollisionBox = true;
                    break;
                case TaxiToGate:
                    paint.setColor(Color.GREEN);
                    break;
                case Pushback:
                    break;
                case TaxiToRunway:
                    paint.setColor(Color.GREEN);
                    break;
                case Landing:
                    paint.setColor(Color.GRAY);
                    break;
                case WaitingForGate:
                    paint.setColor(Settings.Instance().attentionColor);
                    showCollisionBox = true;
                    break;
                case ReadyForDeparture:
                    paint.setColor(Settings.Instance().attentionColor);
                    showCollisionBox = true;
                    break;
                case ClearedForDeparture:
                    break;
                case Takeoff:
                    break;
                case Arrival:
                    break;
                case Departure:
                    break;
            }
            if (showCollisionBox) {
                if (selected) {//|| selectedTime > 0) {
                    paint.setColor(Color.BLUE);
                }
                if (holdPosition) {
                    paint.setColor(Settings.Instance().attentionColor);
                }
                paint.setAlpha((int) (ratio * 255));
                int radius = (int) (Math.min(scaledHeight, scaledWidth) * 0.5);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                canvas.drawCircle(centerPos.x, centerPos.y, radius, paint);


            }


        } else {
//            paint.setColor(Color.BLACK);
//            paint.setStrokeWidth(2);
//            canvas.drawRect(x, y, width, height, paint);
        }
//        if (GameInstance.Settings().DebugMode) {
//            RoadIntersection lastTarget = airplane.getTargetIntersection();
//            if (lastTarget != null) {
//                paint.reset();
//
//                paint.setColor(Color.RED);
//
//                if (airplane.getCurrentRoad() == null) {
//                    paint.setTextSize(Settings.Instance().normalFontSize);
//                    canvas.drawText("No current Road!", centerPos.x, centerPos.y, paint);
//                }
//
//                DrawTargetIntersection(canvas, paint, lastTarget);
//
//                int size = 5;
//
//                PointInt roadTarget = airplane.getRoadTarget();
//                PointInt headingPoint = airplane.getHeadingPoint();
//
//                PointInt renderTarget = Render.getPositionForRender((float) roadTarget.x, (float) roadTarget.y);
//                canvas.drawRect(renderTarget.x - size, renderTarget.y - size, renderTarget.x + size, renderTarget.y + size, paint);
//                renderTarget = Render.getPositionForRender((float) headingPoint.x, (float) headingPoint.y);
//                canvas.drawLine(centerPos.x, centerPos.y, renderTarget.x, renderTarget.y, paint);
//            }
//
//        }


    }

    public static void drawAttentionForVehicle(Canvas canvas, Paint paint, Vehicle vehicle, PointInt sourcePoint) {
        if (vehicle != null) {

            PointInt centerPos = Render.getPositionForRender(vehicle.getAlign_X(), vehicle.getAlign_Y());

            Bitmap bitmap = BitmapLoader.Instance().getBitmap(vehicle.getImageID());
            float scale = GameInstance.Settings().Zoom;
            int scaledWidth = Math.round(scale * bitmap.getWidth());
            int scaledHeight = Math.round(scale * bitmap.getHeight());

            int radius = (int) (Math.min(scaledHeight, scaledWidth) * 0.5);

            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            canvas.drawCircle(centerPos.x, centerPos.y, radius, paint);

            Render.drawAttention(centerPos, paint, canvas, Color.GREEN, sourcePoint);
        }
    }

    private static void drawClosestVehicle(Canvas canvas, Paint paint, Vehicle vehicle) {
        float distance = vehicle.getDistanceToNextVehicle();

        if (vehicle.getClosestVehicle() != null && GameInstance.Settings().DebugMode) {

            float ratio = vehicle.getAni_Ratio();
            if (ratio < 0) {
                ratio = 1;
            } else if (ratio < Render.ratiomin) {
                ratio = 0;
            }

            Vehicle closeVehicle = vehicle.getClosestVehicle();

            PointInt vehiclePos = Render.getPositionForRender(vehicle.getAlign_X(), vehicle.getAlign_Y());

            paint.reset();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.MAGENTA);
            paint.setStrokeWidth(2);
            paint.setAlpha((int) (ratio * 255));

            float scale = GameInstance.Settings().Zoom;
            int radius = (int) (scale * vehicle.getCollisionRadius()) / 2;
            canvas.drawCircle(vehiclePos.x, vehiclePos.y, radius, paint);

            if (distance > 0 && distance < 1200) {
                PointInt closeVehiclePos = Render.getPositionForRender(closeVehicle.getAlign_X(), closeVehicle.getAlign_Y());
                canvas.drawLine(vehiclePos.x, vehiclePos.y, closeVehiclePos.x, closeVehiclePos.y, paint);
            }
        }
    }

    private static void DrawTargetIntersection(Canvas canvas, Paint paint, RoadIntersection lastTarget) {
        if (lastTarget != null) {
            paint.reset();
            float ratio = lastTarget.getAni_Ratio();
            if (ratio < 0) {
                ratio = 1;
            } else if (ratio < Render.ratiomin) {
                ratio = 0;
            }
            paint.setColor(Color.RED);
            paint.setAlpha((int) (ratio * 255));
            int size = 5;
            PointFloat lastTargetPoint = lastTarget.getPosition();
            PointInt renderTarget = Render.getPositionForRender(lastTargetPoint.x, lastTargetPoint.y);
            canvas.drawRect(renderTarget.x - size, renderTarget.y - size, renderTarget.x + size, renderTarget.y + size, paint);
        }
    }

}
