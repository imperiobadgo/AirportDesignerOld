package com.pukekogames.airportdesigner;

import android.content.Intent;
import android.view.MotionEvent;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Helper.ChangeValues;
import com.pukekogames.airportdesigner.Helper.Command;
import com.pukekogames.airportdesigner.Helper.CommonMethods;
import com.pukekogames.airportdesigner.Objects.Airlines.Airline;
import com.pukekogames.airportdesigner.Objects.Airlines.PlannedArrival;
import com.pukekogames.airportdesigner.Objects.UIElements.*;
import com.pukekogames.airportdesigner.Rendering.BitmapLoader;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 27.11.2016.
 */
public class AirlineScreen extends Screen {

    //    private ScrollList airlineList;
    private ScrollList timeSlotList;
    private ChangeValues selectedAirline;
    private ChangeValues airlineReputation;
    private Button acceptAirplane;
    private Button removeAirplane;
    private ArrayList<TimeListItem> sortedTimeList;
    private int[] numberOfArrivals;

    public AirlineScreen(UIManager manager) {
        super(manager);
        sortedTimeList = new ArrayList<>();
        numberOfArrivals = new int[24];
    }

    @Override
    public void SetupScreen() {

//        airlineList = new ScrollList(Alignment.TopLeft, 200, 100, 100, 30, 12, true);
//
//        for (int i = 0; i < GameInstance.AirlineManager().AirlinesCount(); i++) {
//            Airline airline = GameInstance.AirlineManager().getAirline(i);
//
//            for (int j = 0; j < airlineList.size(); j++) {
//                Airline currentAirline = (Airline) airlineList.getItem(j);
//                int plannedArrivalsCount = airline.PlannedArivalsCount();
//                int currentPlannedArrivalsCount = currentAirline.PlannedArivalsCount();
//                if (plannedArrivalsCount >= currentPlannedArrivalsCount){
//                    airlineList.insert(j,airline);
//                }
//            }
//            airlineList.addItem(airline);
//        }
//
//        airlineList.setCommand(new Command() {
//            @Override
//            public void execute(Object object) {
//                selectedAirline =(Airline) airlineList.getItemAtSelectedIndex();
//                UpdateTimeSlotList();
//            }
//        });
//
//        objects.add(airlineList);


        timeSlotList = new ScrollList(Alignment.TopLeft, 10, 200, 400, 33, 8, true);
        objects.add(timeSlotList);

        selectedAirline = new ChangeValues();

        final UpdateableLabel selectedAirlineLabel = new UpdateableLabel(selectedAirline, Alignment.TopLeft, 80, 100, 30);
        objects.add(selectedAirlineLabel);

        airlineReputation = new ChangeValues();

        UpdateableLabel airplaneReputationLabel = new UpdateableLabel(airlineReputation, Alignment.TopLeft, 300, 100, 30);
        objects.add(airplaneReputationLabel);

        acceptAirplane = new Button(Alignment.TopLeft, 500, 200, 100, 50, new Command() {
            @Override
            public void execute(Object object) {
                TimeListItem selectedItem = (TimeListItem) timeSlotList.getItemAtSelectedIndex();
                if (selectedItem != null) {
                    Object selectedObject = selectedItem.getObject();
                    if (selectedObject instanceof PlannedArrival) {
                        PlannedArrival plannedArrival = (PlannedArrival) selectedObject;
                        plannedArrival.setAccepted(true);
                        selectedItem.setAccepted(true);
                        UpdateScreen();
                    }
                }
            }
        });
        acceptAirplane.setContent("accept airplane");
        acceptAirplane.setEnabled(false);
        acceptAirplane.setNoVisual(true);
        acceptAirplane.setReactionTime(20);
        objects.add(acceptAirplane);

        removeAirplane = new Button(Alignment.TopLeft, 500, 300, 100, 50, new Command() {
            @Override
            public void execute(Object object) {
                TimeListItem selectedItem = (TimeListItem) timeSlotList.getItemAtSelectedIndex();
                if (selectedItem != null) {
                    Object selectedObject = selectedItem.getObject();
                    if (selectedObject instanceof PlannedArrival) {
                        PlannedArrival plannedArrival = (PlannedArrival) selectedObject;
                        plannedArrival.setAccepted(false);
                        selectedItem.setAccepted(false);
                        UpdateScreen();
                    }
                }
            }
        });
        removeAirplane.setContent("remove airplane");
        removeAirplane.setEnabled(false);
        removeAirplane.setNoVisual(true);
        removeAirplane.setReactionTime(20);
        objects.add(removeAirplane);

        Button showGraphButton = new Button(Alignment.BottomLeft, 450, -100, 100, 50, new Command() {
            @Override
            public void execute(Object object) {
                if (sortedTimeList.size() == 0) return;

                TimeListItem selectedItem = (TimeListItem) timeSlotList.getItemAtSelectedIndex();
                if (selectedItem != null) {
                    Object selectedObject = selectedItem.getObject();
                    if (selectedObject instanceof PlannedArrival) {
                        PlannedArrival plannedArrival = (PlannedArrival) selectedObject;
                        Intent graphIntent = new Intent("com.pukekogames.airportdesigner.GRAPHACTIVITY");
                        float[] plotData = plannedArrival.getLastTurnAroundDiffs();

                        graphIntent.putExtra("data", plotData);
                        graphIntent.putExtra("xAxis", "Time");
                        graphIntent.putExtra("yAxis", "TimeDiffs");
                        game.startActivity(graphIntent);
                    }
                }
            }
        });

        showGraphButton.setContent("Show TimeSlot Graph");
        objects.add(showGraphButton);

        Button showReputationGraphButton = new Button(Alignment.BottomLeft, 600, -100, 100, 50, new Command() {
            @Override
            public void execute(Object object) {
                Airline selectedAirline = GameInstance.AirlineManager().getSelectedAirline();
                if (selectedAirline == null) return;
                float[] reputation = selectedAirline.getLastReputation();
                Intent graphIntent = new Intent("com.pukekogames.airportdesigner.GRAPHACTIVITY");

                graphIntent.putExtra("data", reputation);

                graphIntent.putExtra("xAxis", "Time");
                graphIntent.putExtra("yAxis", "Reputation");
                game.startActivity(graphIntent);
            }
        });

        showReputationGraphButton.setContent("Show Reputation");
        objects.add(showReputationGraphButton);

        UpdateScreen();
    }

    private void UpdateNumberOfArrivals(){
        for (int i = 0; i < numberOfArrivals.length; i++) {
            numberOfArrivals[i] = 0;
        }
        PlannedArrival[] arrivals = GameInstance.AirlineManager().getPlannedArrivals();
        //count plannedArrivals for each hour
        for (PlannedArrival arrival : arrivals) {
            if (arrival.isAccepted()){
                int hour = arrival.getHour();
                numberOfArrivals[hour] += 1;
            }
        }
    }

    private void UpdateTimeSlotList() {
        UpdateNumberOfArrivals();
        Airline selectedAirline = GameInstance.AirlineManager().getSelectedAirline();
        if (selectedAirline != null) {
            this.selectedAirline.message = selectedAirline.getAirlineName();
            airlineReputation.message = "Reputation " + selectedAirline.getReputation();
            sortedTimeList.clear();
            timeSlotList.clear();
            for (int i = 0; i < selectedAirline.PlannedArivalsCount(); i++) {
                PlannedArrival plannedArrival = selectedAirline.getPlannedArival(i);
                TimeListItem listItem = new TimeListItem(plannedArrival.getHour(), plannedArrival, plannedArrival.isAccepted());
                listItem.setCount(numberOfArrivals[plannedArrival.getHour()]);
                boolean added = false;
                for (int j = 0; j < sortedTimeList.size(); j++) {
                    TimeListItem currentItem = sortedTimeList.get(j);
                    if (listItem.getHour() <= currentItem.getHour()) {
                        //add new entry at corresponding index
                        sortedTimeList.add(j, listItem);
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    sortedTimeList.add(listItem);
                }

            }
            TimeListItem lastItem = null;
            for (TimeListItem listItem : sortedTimeList) {
                timeSlotList.addItem(listItem);
//                if (lastItem == null) {
//                    timeSlotList.addItem(listItem);
//                } else {
//                    if (lastItem.getHour() == listItem.getHour()) {
//                        //increase last entry if there a multiple arrivals in the same hour
//                        int lastCount = (int) lastItem.getObject() + 1;
//                        lastItem.setObject(lastCount);
//                        continue;
//                    } else {
//                        timeSlotList.addItem(listItem);
//                    }
//                }
//
//                lastItem = listItem;
            }

            if (timeSlotList.getStartIndex() + 4 > timeSlotList.size()) {
                timeSlotList.setStartIndex(timeSlotList.size() - 4);
            }

        }
    }

    @Override
    public void UpdateScreen() {
        super.UpdateScreen();
        UpdateTimeSlotList();

        TimeListItem selectedItem = (TimeListItem) timeSlotList.getItemAtSelectedIndex();
        if (selectedItem != null) {
            Object selectedObject = selectedItem.getObject();
            if (selectedObject instanceof PlannedArrival) {
                PlannedArrival plannedArrival = (PlannedArrival) selectedObject;
                setAcceptedButtons(plannedArrival);
            }
        }else{
            acceptAirplane.setEnabled(false);
            acceptAirplane.setNoVisual(true);
            removeAirplane.setEnabled(false);
            removeAirplane.setNoVisual(true);
        }
    }

    @Override
    public void ClearScreen() {
        super.ClearScreen();
//        airlineList = null;
        timeSlotList = null;
        sortedTimeList.clear();
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        if (CommonMethods.isTouchInside((int) event.getX(), (int) event.getY(), timeSlotList)){
            timeSlotList.clicked((int) event.getX(), (int) event.getY());

            TimeListItem selectedItem = (TimeListItem) timeSlotList.getItemAtSelectedIndex();
            if (selectedItem != null) {
                Object selectedObject = selectedItem.getObject();
                if (selectedObject instanceof PlannedArrival) {
                    PlannedArrival plannedArrival = (PlannedArrival) selectedObject;
                    setAcceptedButtons(plannedArrival);
                }
            }
            return true;
        }
        return false;
    }

    private void setAcceptedButtons(PlannedArrival selectedArrival){
        if (selectedArrival.isAccepted()){
            acceptAirplane.setEnabled(false);
            acceptAirplane.setNoVisual(true);
            removeAirplane.setEnabled(true);
            removeAirplane.setNoVisual(false);
        }else{
            acceptAirplane.setEnabled(true);
            acceptAirplane.setNoVisual(false);
            removeAirplane.setEnabled(false);
            removeAirplane.setNoVisual(true);
        }

    }

    @Override
    public boolean touchReleased(MotionEvent event) {
        return false;
    }

    @Override
    public void setSubMenuButtons(UIStack stack) {

        int diameter = (int) (Settings.Instance().ButtonCircleDiameter * 1.2);
        int distance = 20;
        Button changeSelectedAirlineButton = new Button(Alignment.TopRight, xTopRight - 3 * (diameter + distance), yTopRight, diameter, diameter, new Command() {
            @Override
            public void execute(Object object) {
                Intent airlineIntent = new Intent("com.pukekogames.airportdesigner.AIRLINEACTIVITY");
                game.startActivity(airlineIntent);
                UpdateTimeSlotList();
//                game.setAirlineScreen();
            }
        });
//        changeSelectedAirlineButton.setContent(game.getString(R.string.ChangeRoadBuildButton_Text));
        changeSelectedAirlineButton.setImageID(BitmapLoader.indexButtonBuildRoad);
//        setObjectBitmap(changeSelectedAirlineButton);
        BitmapLoader.reposition(changeSelectedAirlineButton);
        objects.add(changeSelectedAirlineButton);
        stack.addButton(changeSelectedAirlineButton);

        buttonStack = stack;

    }
}
