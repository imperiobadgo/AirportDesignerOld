package com.pukekogames.airportdesigner.Rendering;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.pukekogames.airportdesigner.Objects.Airlines.PlannedArrival;
import com.pukekogames.airportdesigner.Objects.UIElements.TimeTable;
import com.pukekogames.airportdesigner.Settings;

/**
 * Created by Marko Rapka on 09.03.2017.
 */
class RenderTimeTable {

    public static void render(Canvas canvas, Paint paint, TimeTable table) {
        paint.reset();

        float ratio = table.getAni_Ratio();
        float x = table.getX();
        float y = table.getY();

        int rowHeight = table.getRowHeight();
        int columnWidth = table.getColumnWidth();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < Render.ratiominButton) {
            ratio = 0;
        }
        paint.setAlpha((int) (ratio * 255));


        paint.setTextSize(Math.round(rowHeight / 0.9));

        int startColumnIndex = table.getStartColumnIndex();
        int startRowIndex = table.getStartRowIndex();
        int showingColumnCount = table.getShowingColumnCount();
        int showingRowCount = table.getShowingRowCount();

        for (int column = 0; column < showingColumnCount; column++) {
            paint.setColor(Color.BLACK);
            paint.setTextSize(rowHeight * 0.9f);
            //draw timeSlot time
            canvas.drawText((column + startColumnIndex) + "", x + column * columnWidth, y - rowHeight / 2, paint);

            canvas.drawLine(x + column * columnWidth, y, x + column * columnWidth, y + showingRowCount * rowHeight, paint);
        }
        canvas.drawLine(x + showingColumnCount * columnWidth, y, x + showingColumnCount * columnWidth, y + showingRowCount * rowHeight, paint);

        for (int row = 0; row < showingRowCount; row++) {
            paint.setColor(Color.BLACK);
            canvas.drawLine(x, y + row * rowHeight, x + showingColumnCount * columnWidth, y + row * rowHeight, paint);

            float offset = 3;
            float top = y + row * rowHeight + offset;
            float bottom = y + row * rowHeight + rowHeight - offset;

            if (row + startRowIndex == table.getSelectedRow()){
                paint.setColor(Color.rgb(235,235,200));
                canvas.drawRect(x + offset, top, x + showingColumnCount * columnWidth - offset, bottom, paint);
            }

            PlannedArrival currentArrival = table.getItem(startRowIndex + row);
            if (currentArrival == null) continue;
            int targetHourIndex = currentArrival.getTargetHour() - startColumnIndex;
            int hourIndex = currentArrival.getHour() - startColumnIndex;

            String callSign = currentArrival.getCallSign();

            float width = paint.measureText(callSign);

            if (!currentArrival.isEditable()){
                paint.setColor(Color.YELLOW);
                canvas.drawRect(x - width - 5, top, x , bottom, paint);
            }

            paint.setColor(Settings.Instance().fontColor);
            canvas.drawText(callSign, x - width - 5, bottom,paint);

            if (hourIndex < 0 || hourIndex >= showingColumnCount) continue;

            int minTime = Math.max(0, targetHourIndex - currentArrival.getMaxHourOffset());
            int maxTime = Math.min(showingColumnCount - 1, targetHourIndex + currentArrival.getMaxHourOffset());

            float maxOffsetLeft = x + minTime * columnWidth + offset;
            float maxOffsetRight = x + maxTime * columnWidth + columnWidth - offset;

            //draw possible timeSlotArea
            paint.setColor(Color.LTGRAY);
            canvas.drawRect(maxOffsetLeft, top, maxOffsetRight, bottom, paint);

            float left = x + hourIndex * columnWidth + offset;
            float right = x + hourIndex * columnWidth + columnWidth - offset;

            //draw current timeSlot
            paint.setColor(Color.YELLOW);
            canvas.drawRect(left, top, right, bottom, paint);

        }
        paint.setColor(Color.BLACK);
        canvas.drawLine(x, y + showingRowCount * rowHeight, x + showingColumnCount * columnWidth, y + showingRowCount * rowHeight, paint);

        for (int column = 0; column < showingColumnCount; column++) {
            int timeSlotCount = table.getTimeSlotCountAtHour(column + startColumnIndex);

            paint.setColor(Color.BLACK);
            paint.setTextSize(rowHeight * 0.8f);
            canvas.drawText(timeSlotCount + "", x + column * columnWidth, y + rowHeight * (showingRowCount + 1), paint);

        }
    }
}
