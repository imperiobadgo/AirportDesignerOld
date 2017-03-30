package com.pukekogames.airportdesigner.Objects.UIElements;

import android.view.MotionEvent;
import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Helper.Command;
import com.pukekogames.airportdesigner.Helper.CommonMethods;
import com.pukekogames.airportdesigner.Objects.Airlines.PlannedArrival;
import com.pukekogames.airportdesigner.Objects.ClickableGameObject;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 09.03.2017.
 */
public class TimeTable extends ClickableGameObject {

    private static final long serialVersionUID = -3834527295604585811L;
    private static final int MAXXCOUNT = 24;

    private int startColumnIndex;
    private int startRowIndex;
    private int showingColumnCount;
    private int showingRowCount;
    private int rowHeight;
    private int columnWidth;

    private ArrayList<PlannedArrival> plannedArrivals;
    private int[] timeSlotCount;
    private boolean selectable;
    private int selectedRow;
    private Command command;

    public TimeTable(Alignment alignment, float x, float y, float width, float height, int showingColumnCount, int showingRowCount, boolean selectable) {
        super(alignment, x, y, width, height);
        this.showingColumnCount = showingColumnCount;
        this.showingRowCount = showingRowCount;
        this.selectable = selectable;
        rowHeight = 30;
        columnWidth = 40;
        this.width = columnWidth * showingColumnCount;
        this.height = rowHeight * showingRowCount;
        plannedArrivals = new ArrayList<>();
        timeSlotCount = new int[24];
        updateHeight();
    }

    @Override
    public void clicked(int mx, int my) {
        if (selectable) {
            int diffY = my - (int) y;
            selectedRow = Math.round(diffY / rowHeight) + startRowIndex;
            if (selectedRow < 0) {
                selectedRow = 0;
            }

            if (selectedRow >= plannedArrivals.size()) {
                selectedRow = plannedArrivals.size() - 1;
            }

            if (command != null) command.execute(this);
        }
    }

    @Override
    public void tick() {

    }

    public void addItem(PlannedArrival arrival) {
        plannedArrivals.add(arrival);
        updateHeight();
        updateTimeSlotCount();
    }

    public PlannedArrival getItem(int index) {
        if (index < 0 || index > plannedArrivals.size() - 1) return null;
        return plannedArrivals.get(index);
    }

    public void moveToOtherTimeSlot(int amount){
        if (plannedArrivals.size() <= selectedRow) return;
        PlannedArrival selectedArrival = plannedArrivals.get(selectedRow);
        int currentHour = selectedArrival.getHour();
        int targetHour = selectedArrival.getTargetHour();
        int maxOffset = selectedArrival.getMaxHourOffset();
        if (targetHour - maxOffset > 0 && targetHour + maxOffset < 23){
            currentHour += amount;
            if (targetHour + maxOffset < currentHour){
                currentHour = targetHour + maxOffset;
            }
            if (targetHour - maxOffset > currentHour){
                currentHour = targetHour - maxOffset;
            }
            selectedArrival.setHour(currentHour);
            updateTimeSlotCount();
        }

    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        boolean scrolled = false;
        if (CommonMethods.isTouchInside((int) e1.getX(), (int) e1.getY(), this)) {
            scrolled = true;
            int minDifference = 15;
            if (distanceX > minDifference) {
                increaseStartXIndex();
            } else if (distanceX < -minDifference) {
                decreaseStartXIndex();
            }
            if (distanceY > minDifference) {
                increaseStartYIndex();
            } else if (distanceY < -minDifference) {
                decreaseStartYIndex();
            }

        }
        return scrolled;
    }

    private void increaseStartXIndex() {
        if (startColumnIndex + showingColumnCount < MAXXCOUNT) {
            startColumnIndex += 1;
        }
    }

    private void decreaseStartXIndex() {
        if (startColumnIndex > 0) {
            startColumnIndex -= 1;
        }
    }

    private void increaseStartYIndex() {
        if (startRowIndex + showingRowCount < plannedArrivals.size()) {
            startRowIndex += 1;
        }
    }

    private void decreaseStartYIndex() {
        if (startRowIndex > 0) {
            startRowIndex -= 1;
        }
    }


    private void updateHeight() {
//        if (plannedArrivals.size() == 0) {
//            height = rowHeight;
//            return;
//        }
//        height = Math.min(rowHeight * showingRowCount, rowHeight * plannedArrivals.size());
    }

    private void updateTimeSlotCount(){
        for (int i = 0; i < timeSlotCount.length; i++) {
            timeSlotCount[i] = 0;
        }
        for (PlannedArrival arrival : plannedArrivals) {
            int hour = arrival.getHour();
            timeSlotCount[hour] += 1;
        }
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public PlannedArrival getSelectedArrival(){
        if (plannedArrivals.size() == 0) return null;
        return plannedArrivals.get(selectedRow);
    }

    public int getTimeSlotCountAtHour(int hour){
        return timeSlotCount[hour];
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void clear(){
        plannedArrivals.clear();
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public int getStartColumnIndex() {
        return startColumnIndex;
    }

    public int getStartRowIndex() {
        return startRowIndex;
    }

    public int getShowingColumnCount() {
        return showingColumnCount;
    }

    public int getShowingRowCount() {
        return showingRowCount;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
