package com.pukekogames.airportdesigner.Rendering;

import android.graphics.*;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Geometry.PointFloat;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Helper.Geometry.Vector2D;
import com.pukekogames.airportdesigner.Helper.MoneyChange;
import com.pukekogames.airportdesigner.Objects.Airlines.Airline;
import com.pukekogames.airportdesigner.Objects.Airlines.PlannedArrival;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.UIElements.Button;
import com.pukekogames.airportdesigner.Objects.UIElements.Label;
import com.pukekogames.airportdesigner.Objects.UIElements.ScrollList;
import com.pukekogames.airportdesigner.Objects.UIElements.TimeListItem;
import com.pukekogames.airportdesigner.R;
import com.pukekogames.airportdesigner.Settings;


/**
 * Created by Marko Rapka on 12.07.2016.
 */
public class RenderGui {

    static Matrix matrix;

    public static void renderButton(Canvas canvas, Paint paint, Button button) {
        paint.reset();
        if (matrix == null) {
            matrix = new Matrix();
        }
        matrix.reset();

        boolean noVisual = button.isNoVisual();
        float x = button.getX();
        float y = button.getY();
        float width = button.getWidth();
        float height = button.getHeight();
        if (!noVisual) {
            float ratio = button.getAni_Ratio();
            if (ratio < 0) {
                ratio = 1;
            } else if (ratio < Render.ratiominButton) {
                ratio = 0;
            }
            Bitmap mainBitmap = BitmapLoader.Instance().getBitmap(button.getImageID());

            if (mainBitmap != null) {
//                button.setDimension(mainBitmap.getWidth(), mainBitmap.getHeight());
                paint.setAlpha((int) (ratio * 255));
                if (button.isShowBackground()) {
                    Bitmap background;
                    if (button.getHasClicked() > 0) {
                        background = BitmapLoader.Instance().getBitmap(BitmapLoader.indexCircleButtonBackgroundClicked);
                    } else {
                        background = BitmapLoader.Instance().getBitmap(BitmapLoader.indexCircleButtonBackground);
                    }
                    float bitmapWidth = background.getWidth();
                    float bitmapHeight = background.getHeight();
                    float scaleX = width / bitmapWidth;
                    float scaleY = height / bitmapHeight;
                    matrix.setScale(scaleX, scaleY);
                    matrix.postTranslate(x, y);
                    canvas.drawBitmap(background, matrix, paint);
                }


                float bitmapWidth = mainBitmap.getWidth();
                float bitmapHeight = mainBitmap.getHeight();
                float scaleX = width / bitmapWidth;
                float scaleY = height / bitmapHeight;
                matrix.setScale(scaleX, scaleY);
                matrix.postTranslate(x, y);
                canvas.drawBitmap(mainBitmap, matrix, paint);
            } else {
                if (button.isEnabled()) {
                    paint.setColor(Color.CYAN);
                } else {
                    paint.setColor(Color.GRAY);
                }
                paint.setAlpha((int) (ratio * 255));
                canvas.drawRect((int) x, (int) y, (int) (x + width), (int) (y + height), paint);

            }
            if (button.getHasClicked() > 0 && !button.isShowBackground()) {
                paint.setColor(Settings.Instance().selectedColor);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                if (button.getImageID() >= 0) {
                    int radius = (int) Math.max(width, height) / 2;
                    canvas.drawCircle((int) x + width / 2, (int) y + height / 2, radius, paint);
                } else {
                    canvas.drawRect((int) x, (int) y, (int) (x + width), (int) (y + height), paint);
                }

            }


            if (button.isContentSet()) {
                String content = button.getContent();
                paint.reset();
                if (ratio > Render.ratiominButton) {
                    paint.setAlpha((int) (ratio * 255));
                    paint.setColor(Settings.Instance().fontColor);
                    paint.setTextSize(Settings.Instance().normalFontSize);
                    Rect bounds = new Rect();
                    paint.getTextBounds(content, 0, content.length(), bounds);

                    float middleX = x + width / 2;
                    float middleY = y + height / 2;
                    float contentX = middleX - (bounds.width() / 2);
                    float contentY = middleY + (bounds.height() / 3);
                    canvas.drawText(button.getContent(), (int) contentX, (int) contentY, paint);

                }

                //contentX = contentX - stringWidth / 2;

                //g.fillRect((int) contentX, (int) contentY - fontSize, (int) fontSize, (int) fontSize);

            }

        }


//        Render.drawSelection(g, button);
    }

    public static void renderTimeSlotList(Canvas canvas, Paint paint, ScrollList slotList) {
        paint.reset();

        float ratio = slotList.getAni_Ratio();
        float x = slotList.getX();
        float y = slotList.getY();

        float fontSize = slotList.getFontHeight();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiominButton) {
            ratio = 0;
        }
        paint.setAlpha((int) (ratio * 255));


        paint.setTextSize(Math.round(fontSize / 0.9));

        int startIndex = slotList.getStartIndex();

        int size = Math.min(slotList.getShowingCount(), slotList.size());
        int selectedIndex = slotList.getSelectedIndex(); //GameInstance.Instance().getHour();

        String timeHeader = Settings.getString(R.string.AirlineScreen_timeSlotHeader);

        float xOffsetSecondColumn = paint.measureText(timeHeader) + fontSize / 2;

        canvas.drawText(timeHeader, x, y - fontSize, paint);

        for (int i = 0; i < size; i++) {
            float yTable = y + i * (fontSize + 4);
            float xTable = x;
            int currentHour = i + startIndex;

            if (currentHour == selectedIndex) {
                paint.setColor(Settings.Instance().selectedColor);
            } else {
                paint.setColor(Settings.Instance().fontColor);
            }

            Object object = slotList.getItem(currentHour);

            if (object instanceof TimeListItem) {
                TimeListItem listItem = (TimeListItem) object;
                boolean isAccepted = listItem.isAccepted();
                if (!isAccepted){
                    paint.setColor(Color.YELLOW);
                    canvas.drawRect(xTable , yTable - fontSize ,xTable + slotList.getWidth(), yTable, paint);
                    if (currentHour == selectedIndex) {
                        paint.setColor(Settings.Instance().selectedColor);
                    } else {
                        paint.setColor(Settings.Instance().fontColor);
                    }
                }
                String hourText = String.format("%02d", listItem.getHour());

                canvas.drawText(hourText, xTable, yTable, paint);

                float xOffsetForCount = paint.measureText(hourText) + fontSize;
                String countText = listItem.getCount() + "";
                canvas.drawText(countText, xTable + xOffsetForCount, yTable, paint);

                Object listObject = listItem.getObject();
                String content = listObject.toString();

                canvas.drawText(content, xTable + xOffsetSecondColumn, yTable, paint);

                if (listObject instanceof PlannedArrival){
                    PlannedArrival arrival = (PlannedArrival) listObject;
                    int timeDiff = arrival.getLastTurnaraoundTimeDiff();
                    float xOffsetThirdColumn = paint.measureText(content) + fontSize / 2;
                    String status;
                    if (timeDiff >= 0){
                        status = Settings.getString(R.string.AirlineScreen_timeSlotOnTime);
                    }else{
                        status = Settings.getString(R.string.AirlineScreen_timeSlotDelay);
                    }
                    canvas.drawText(status, xTable + xOffsetSecondColumn + xOffsetThirdColumn, yTable, paint);
                }


            } else if (object instanceof Airline) {
                canvas.drawText(object.toString(), xTable, yTable, paint);
            }
        }
    }

    public static void renderLabel(Canvas canvas, Paint paint, Label label) {
        paint.reset();

        float ratio = label.getAni_Ratio();
        float x = label.getX();
        float y = label.getY();

        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiominButton) {
            ratio = 0;
        }
        paint.setAlpha((int) (ratio * 255));


        paint.setTextSize(label.getFontSize());

        canvas.drawText(label.getContent(), x, y, paint);
    }

    public static void drawBuildCost(Canvas canvas, Paint paint, long buildCost, Road buildRoad) {
        if (buildRoad == null) {
            return;
        }
        paint.reset();
        paint.setTextSize(Settings.Instance().normalFontSize);

        PointInt buildPos = Render.getPositionForRender(buildRoad.getEndPosition().x, buildRoad.getEndPosition().y);

        canvas.drawText(buildCost + " €", buildPos.x - 20, buildPos.y - 40, paint);
        canvas.drawText("" + Math.round(buildRoad.getLength()), buildPos.x - 20, buildPos.y - 20, paint);

    }

    public static void drawBuildCollision(Canvas canvas, Paint paint, PointFloat collisionPoint) {
        paint.reset();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        PointInt renderPosition = Render.getPositionForRender((int) collisionPoint.x, (int) collisionPoint.y);
        int size = 4;
        canvas.drawRect(renderPosition.x - size, renderPosition.y - size, renderPosition.x + size, renderPosition.y + size, paint);
        int radius = 7;
        canvas.drawCircle(renderPosition.x, renderPosition.y, radius, paint);
    }

    public static void drawMoney(Canvas canvas, Paint paint) {
        paint.reset();
        paint.setTextSize(30);
        long money = GameInstance.Instance().getMoney();
        canvas.drawText(money + " €", 10, 30, paint);

        for (MoneyChange change : GameInstance.Instance().getLastMoneyChange()) {
            if (change.amount < 0) {
                paint.setColor(Color.argb(255 - change.time, 255, 0, 0));
            } else {
                paint.setColor(Color.argb(255 - change.time, 0, 255, 0));
            }

            canvas.drawText(change.amount + " €", 10, 30 + change.time, paint);
        }

        paint.reset();
        paint.setTextSize(17);
        canvas.drawText("Level: " + GameInstance.Settings().level, 10, 45, paint);
        canvas.drawText("Mode: " + GameInstance.Settings().buildMode, 10, 60, paint);

        if (GameInstance.Settings().gameSpeed > 1) {
            canvas.drawText("GS: " + GameInstance.Settings().gameSpeed, 60, 75, paint);
        }

        canvas.drawText("RE: " + GameInstance.AirlineManager().getReputation(), 10, 75, paint);

        int hour, minute;
        hour = GameInstance.Instance().getHour();
        minute = GameInstance.Instance().getMinute();
        //String.format("%02d", hour) adds leading zero
        canvas.drawText(String.format("%02d", hour) + ":" + String.format("%02d", minute), 100, 17, paint);
    }

    public static void drawWind(Canvas canvas, Paint paint) {
        paint.reset();
        float windDirection = (GameInstance.Airport().getWindDirection() + 180) % 360;

        PointInt middlePos = new PointInt(120, 50);

        double dirX = (float) Math.cos(Math.toRadians(windDirection));
        double dirY = (float) Math.sin(Math.toRadians(windDirection));

        double length = Math.sqrt(Math.pow(dirX, 2) + Math.pow(dirY, 2));
        dirX /= length;
        dirY /= length;
        double direction = Math.atan2(dirY, dirX);
        double angleSeparation = Math.toRadians(20);
        double leftDirection = (direction - angleSeparation) % 360;
        double rightDirection = (direction + angleSeparation) % 360;
        int innerRadius = -20;
        int outerRadius = 20;
        int sideRadius = 10;
        PointInt startPos = new PointInt((int) (middlePos.x + dirX * innerRadius), (int) (middlePos.y + dirY * innerRadius));
        PointInt endPos = new PointInt((int) (middlePos.x + dirX * outerRadius), (int) (middlePos.y + dirY * outerRadius));
        double directionX = Math.cos(leftDirection);
        double directionY = Math.sin(leftDirection);
        PointInt leftPos = new PointInt((int) (middlePos.x + directionX * sideRadius), (int) (middlePos.y + directionY * sideRadius));
        directionX = Math.cos(rightDirection);
        directionY = Math.sin(rightDirection);
        PointInt rightPos = new PointInt((int) (middlePos.x + directionX * sideRadius), (int) (middlePos.y + directionY * sideRadius));

        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2);
        canvas.drawLine(startPos.x, startPos.y, endPos.x, endPos.y, paint);
        canvas.drawLine(leftPos.x, leftPos.y, endPos.x, endPos.y, paint);
        canvas.drawLine(rightPos.x, rightPos.y, endPos.x, endPos.y, paint);
    }

    public static void drawVector(Canvas canvas, Paint paint, Vector2D vector, PointInt middlePos) {
        paint.reset();
        if (vector == null) return;

        double dirX = vector.getX();
        double dirY = vector.getY();

        double length = Math.sqrt(Math.pow(dirX, 2) + Math.pow(dirY, 2));
        dirX /= length;
        dirY /= length;
        double direction = Math.atan2(dirY, dirX);
        double angleSeparation = Math.toRadians(20);
        double leftDirection = (direction - angleSeparation) % 360;
        double rightDirection = (direction + angleSeparation) % 360;
        int innerRadius = -20;
        int outerRadius = 20;
        int sideRadius = 10;
        PointInt startPos = new PointInt((int) (middlePos.x + dirX * innerRadius), (int) (middlePos.y + dirY * innerRadius));
        PointInt endPos = new PointInt((int) (middlePos.x + dirX * outerRadius), (int) (middlePos.y + dirY * outerRadius));
        double directionX = Math.cos(leftDirection);
        double directionY = Math.sin(leftDirection);
        PointInt leftPos = new PointInt((int) (middlePos.x + directionX * sideRadius), (int) (middlePos.y + directionY * sideRadius));
        directionX = Math.cos(rightDirection);
        directionY = Math.sin(rightDirection);
        PointInt rightPos = new PointInt((int) (middlePos.x + directionX * sideRadius), (int) (middlePos.y + directionY * sideRadius));

        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2);
        canvas.drawLine(startPos.x, startPos.y, endPos.x, endPos.y, paint);
        canvas.drawLine(leftPos.x, leftPos.y, endPos.x, endPos.y, paint);
        canvas.drawLine(rightPos.x, rightPos.y, endPos.x, endPos.y, paint);
    }

    public static void drawMessages(Canvas canvas, Paint paint) {
        paint.reset();
        paint.setTextSize(25);
        if (GameInstance.Instance().isCurrentMessageNew()) {
            paint.setColor(Color.RED);
        }
        String message = GameInstance.Instance().getCurrentMessage();
        if (message != null) {
            canvas.drawText(message, 200, 30, paint);
        }
    }
}
