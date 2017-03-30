package com.pukekogames.airportdesigner;


import android.content.Intent;
import android.view.MotionEvent;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Helper.ChangeValues;
import com.pukekogames.airportdesigner.Helper.Command;
import com.pukekogames.airportdesigner.Helper.CommonMethods;
import com.pukekogames.airportdesigner.Objects.Airlines.PlannedArrival;
import com.pukekogames.airportdesigner.Objects.UIElements.*;

/**
 * Created by Marko Rapka on 20.11.2016.
 */
public class TimeWindowScreen extends Screen {

    static final String EURO = "\u20AC";

    //    private ScrollList timeWindowList;
    private TimeTable table;
    private int selectedHour;
    private PlannedArrival selectedArrival;
    private ChangeValues selectedTimeWindowCost;


    public TimeWindowScreen(UIManager manager) {
        super(manager);
    }

    @Override
    public void SetupScreen() {
//        timeWindowList = new ScrollList(Alignment.TopLeft, 200, 100, 100, 30, 12, true);

//        timeWindowList.setStartIndex(0);
//        timeWindowList.setCommand(new Command() {
//            @Override
//            public void execute(Object object) {
//                selectedHour = timeWindowList.getSelectedIndex();
//                UpdateContent();
//            }
//        });
//        objects.add(timeWindowList);


        table = new TimeTable(Alignment.TopLeft, 300, 100, 100, 100, 12, 10, true);
        table.setCommand(new Command() {
            @Override
            public void execute(Object object) {
                selectedArrival = table.getSelectedArrival();
            }
        });
        objects.add(table);
//        Random r = new Random();
//        int count = r.nextInt(10) + r.nextInt(10) + r.nextInt(10) + r.nextInt(10) + 5;
//        for (int i = 0; i < count; i++) {
//            table.addItem(new PlannedArrival(r.nextInt(17) + 5 ,r.nextInt(2) + r.nextInt(2) + 1, null));
//        }



        selectedTimeWindowCost = new ChangeValues();


        Button shiftToEarlierTimeSlotButton = new Button(Alignment.BottomLeft, 200, -100, 100, 50, new Command() {
            @Override
            public void execute(Object object) {
                if (selectedArrival != null) {
                    if (selectedArrival.isEditable()) table.moveToOtherTimeSlot(-1);
                }
            }
        });
        shiftToEarlierTimeSlotButton.setContent("earlier");
        shiftToEarlierTimeSlotButton.setReactionTime(5);
        objects.add(shiftToEarlierTimeSlotButton);

        Button shiftToLaterTimeSlotButton = new Button(Alignment.BottomLeft, 400, -100, 100, 50, new Command() {
            @Override
            public void execute(Object object) {
                if (selectedArrival != null) {
                    if (selectedArrival.isEditable()) table.moveToOtherTimeSlot(1);
                }
            }
        });
        shiftToLaterTimeSlotButton.setContent("later");
        shiftToLaterTimeSlotButton.setReactionTime(5);
        objects.add(shiftToLaterTimeSlotButton);


        Button showGraphButton = new Button(Alignment.BottomLeft, 600, -100, 100, 50, new Command() {
            @Override
            public void execute(Object object) {
                Intent graphIntent = new Intent("com.pukekogames.airportdesigner.GRAPHACTIVITY");
                float[] plotData = new float[24];

                for (int i = 0; i < 24; i++) {
                    plotData[i] = table.getTimeSlotCountAtHour(i);
                }

                graphIntent.putExtra("data", plotData);
                graphIntent.putExtra("xAxis", "Time");
                game.startActivity(graphIntent);
            }
        });

        showGraphButton.setContent("Show Graph");
        showGraphButton.setReactionTime(40);
        objects.add(showGraphButton);

//
        UpdateContent();
    }

    private void UpdateContent() {
        table.clear();
        PlannedArrival[] arrivals = GameInstance.AirlineManager().getPlannedArrivals();
        for (PlannedArrival arrival : arrivals) {
            if (arrival.isAccepted()) table.addItem(arrival);
        }
    }

    @Override
    public void UpdateScreen() {
        super.UpdateScreen();
        UpdateContent();
    }

    @Override
    public void ClearScreen() {
        super.ClearScreen();
//        timeWindowList = null;
        table = null;
        selectedTimeWindowCost = null;

    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    public boolean onTouch(MotionEvent event) {
//        if (CommonMethods.isTouchInside((int) event.getX(), (int) event.getY(), timeWindowList)) {
//
//            timeWindowList.clicked((int) event.getX(), (int) event.getY());
//            return true;
//        }
        if (CommonMethods.isTouchInside((int) event.getX(), (int) event.getY(), table)) {
            table.clicked((int) event.getX(), (int) event.getY());
            return true;
        }
        return false;
    }


    @Override
    public boolean touchReleased(MotionEvent event) {
        //Align_X + width / 2, Align_Y + height / 2
//        if (    event.getX() > timeWindowList.getAlign_X() &&
//                event.getX() < timeWindowList.getAlign_X() + timeWindowList.getWidth() &&
//                event.getY() > timeWindowList.getAlign_Y() &&
//                event.getY() < timeWindowList.getAlign_Y() + timeWindowList.getHeight()){
//            timeWindowList.clicked((int) event.getX(),(int) event.getY());
//            System.out.println("Timeslotclicked");
//            return true;
//        }
//        System.out.println("Not Timeslotclicked");
        return false;
    }

    @Override
    public void setSubMenuButtons(UIStack stack) {

    }
}
