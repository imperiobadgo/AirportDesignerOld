package com.pukekogames.airportdesigner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MotionEvent;
import android.widget.Toast;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Helper.ClassTranslation.BuildingType;
import com.pukekogames.airportdesigner.Helper.ClassTranslation.RoadType;
import com.pukekogames.airportdesigner.Helper.Command;
import com.pukekogames.airportdesigner.Helper.CommonMethods;
import com.pukekogames.airportdesigner.Objects.Buildings.Depot;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Roads.*;
import com.pukekogames.airportdesigner.Objects.UIElements.Button;
import com.pukekogames.airportdesigner.Objects.UIElements.ButtonCircle;
import com.pukekogames.airportdesigner.Objects.UIElements.UIStack;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;
import com.pukekogames.airportdesigner.Objects.Vehicles.StreetVehicle;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplaneState;
import com.pukekogames.airportdesigner.Rendering.BitmapLoader;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 20.11.2016.
 */
public class GameScreen extends Screen {

    ButtonCircle buttonCircle;
    Button removeSelectionButton;
    Button buildRoadbutton;
    Button showNextAirplanesButton;

    public GameScreen(UIManager manager) {
        super(manager);
        buttonCircle = new ButtonCircle(null);
    }

    @Override
    public void SetupScreen() {
        int diameter = (int) (Settings.Instance().ButtonCircleDiameter * 1.2);
        int distance = 20;
        removeSelectionButton = new Button(Alignment.BottomLeft, yTopRight, xTopRight, diameter, diameter, new Command() {
            @Override
            public void execute(Object object) {
                clearSelectableObjects();
            }
        });
//        removeSelectionButton.setContent(game.getString(R.string.RemoveSelectionButton_Text));
        removeSelectionButton.setImageID(BitmapLoader.indexButtonCancel);
        removeSelectionButton.setEnabled(false);
        removeSelectionButton.setNoVisual(true);
        objects.add(removeSelectionButton);
        BitmapLoader.reposition(removeSelectionButton);

        buildRoadbutton = new Button(Alignment.BottomLeft, yTopRight, xTopRight - (diameter + distance), diameter, diameter, new Command() {
            @Override
            public void execute(Object object) {
                if (handler.buildRoad != null) {
                    if (handler.intersectPoints.size() > 0 || handler.buildCost == 0) {
                        Toast toast = Toast.makeText(game, R.string.Building_Collision_Toast, Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }

                    if (handler.buildRoad instanceof Runway) {
                        if (handler.buildRoad.getLength() < 4000) {
                            Toast toast = Toast.makeText(game, R.string.Building_RunwayToShort_Toast, Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                    }
//                    if (buildRoad instanceof ParkGate) {
//                        if (buildRoad.getLength() < 1200) {
//                            Toast toast = Toast.makeText(game, R.string.Building_ParkGateToShort_Toast, Toast.LENGTH_SHORT);
//                            toast.show();
//                            return;
//                        }
//                    }

                    //money check
                    if (GameInstance.Instance().removeMoney(handler.buildCost)) {
                        RoadIntersection intersection = null;
                        boolean shouldAddIntersection = true;
                        for (int i = 0; i < GameInstance.Airport().getRoadIntersectionCount(); i++) {
                            RoadIntersection tempIntersection = GameInstance.Airport().getRoadIntersection(i);
                            double distance = CommonMethods.getDistance(tempIntersection.getPosition(), handler.buildIntersection.getPosition());
                            if (distance < 0.1) {
                                intersection = tempIntersection;
                                shouldAddIntersection = false;
                                break;
                            }
                        }

                        if (shouldAddIntersection) {

                            intersection = new RoadIntersection(handler.buildIntersection.getPosition());

                            GameInstance.Airport().AddRoadIntersection(intersection);//to prevent referencing to the changeable buildingRoadIntersection
                        }
                        handler.buildRoad.setNext(intersection);
                        GameInstance.Airport().AddRoad(handler.buildRoad);

                        handler.buildRoad = null;
                        handler.firstRoadIntersection = null;
                        setSelectableRoadIntersections(null);
                    } else {
                        Toast toast = Toast.makeText(game, R.string.Building_NotEnoughMoney_Toast, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
        buildRoadbutton.setImageID(BitmapLoader.indexButtonConstruct);
        buildRoadbutton.setEnabled(false);
        buildRoadbutton.setNoVisual(true);

        BitmapLoader.reposition(buildRoadbutton);
        objects.add(buildRoadbutton);

        showNextAirplanesButton = new Button(Alignment.BottomLeft, yTopRight, xTopRight - 2 * (diameter + distance), diameter, diameter, new Command() {
            @Override
            public void execute(Object object) {
                Intent intent = new Intent("com.pukekogames.airportdesigner.AIRPLANELISTACTIVITY");

                game.startActivity(intent);
            }
        });
        showNextAirplanesButton.setImageID(BitmapLoader.indexCircleButtonBackground);

        BitmapLoader.reposition(showNextAirplanesButton);
        objects.add(showNextAirplanesButton);

    }

    public void updateNextAirplanesButton(){
        int count = GameInstance.Airport().getNextAirplanes().size();
        showNextAirplanesButton.setContent(count + "");
    }

    @Override
    public void UpdateScreen() {
        super.UpdateScreen();
    }

    @Override
    public void ClearScreen() {
        super.ClearScreen();
    }

    @Override
    public void tick() {
        super.tick();
        updateNextAirplanesButton();
        buttonCircle.tick();
        if (buttonCircle.shouldRemove()) {
            removeCircleButtons();
        }
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        return false;
    }

    @Override
    public boolean touchReleased(MotionEvent event) {
        return false;
    }

    void removeCircleButtons() {
        for (Button button : buttonCircle.getButtons()) {
            objects.remove(button);
        }
        buttonCircle.clearButtons();
    }

    @Override
    public void setSubMenuButtons(UIStack stack) {
        int diameter = (int) (Settings.Instance().ButtonCircleDiameter * 1.2);
        int distance = 20;
        Button changeModeButton = new Button(Alignment.TopRight, xTopRight - 3 * (diameter + distance), yTopRight, diameter, diameter, new Command() {
            @Override
            public void execute(Object object) {
                Intent intent = new Intent("com.pukekogames.airportdesigner.ROADLISTACTIVITY");
                String[] classNames = {
                        "Taxiway",
                        "Runway",
                        "Street",
                        "ParkGate"
                };
                intent.putExtra("ClassNames", classNames);

                game.startActivity(intent);
                switchBuild(1);
            }
        });
//        changeModeButton.setContent(game.getString(R.string.ChangeRoadBuildButton_Text));
        changeModeButton.setImageID(BitmapLoader.indexButtonBuildRoad);
//        setObjectBitmap(changeModeButton);
        BitmapLoader.reposition(changeModeButton);
        objects.add(changeModeButton);
        stack.addButton(changeModeButton);

        Button buildBuildingButton = new Button(Alignment.TopRight, xTopRight - 2 * (diameter + distance), yTopRight, diameter, diameter, new Command() {
            @Override
            public void execute(Object object) {

                Intent intent = new Intent("com.pukekogames.airportdesigner.DEPOTLISTACTIVITY");
                ArrayList<String> classNamesList = new ArrayList<>();

                classNamesList.add("CrewBusDepot");
                classNamesList.add("BusDepot");
                classNamesList.add("BaggageDepot");
                classNamesList.add("TankDepot");
                classNamesList.add("CateringDepot");

                if (GameInstance.Settings().level >= 3){
                    classNamesList.add("Terminal");
                }
                if (GameInstance.Settings().level > 4 && GameInstance.Airport().getTower() == null) {
                    classNamesList.add("Tower");
                }

                String[] classNames = new String[classNamesList.size()];
                intent.putExtra("ClassNames", classNamesList.toArray(classNames));

                game.startActivity(intent);

                switchBuild(3);

            }
        });

//        buildBuildingButton.setContent(game.getString(R.string.BuildBuildingButton_Text));
        buildBuildingButton.setImageID(BitmapLoader.indexButtonBuildDepot);

        BitmapLoader.reposition(buildBuildingButton);
        objects.add(buildBuildingButton);
        stack.addButton(buildBuildingButton);


        Button deleteRoadbutton = new Button(Alignment.TopRight, xTopRight - (diameter + distance), yTopRight, diameter, diameter, new Command() {
            @Override
            public void execute(Object object) {
                switchBuild(2);
            }
        });
//        deleteRoadbutton.setContent(game.getString(R.string.DeleteRoadButton_Text));
        deleteRoadbutton.setImageID(BitmapLoader.indexButtonDelete);
//        setObjectBitmap(deleteRoadbutton);
        BitmapLoader.reposition(deleteRoadbutton);
        objects.add(deleteRoadbutton);
        stack.addButton(deleteRoadbutton);

        buttonStack = stack;
    }

    private void clearSelectableObjects() {
        manager.clearSelectableObjects();
        removeSelectionButton.setEnabled(false);
        removeSelectionButton.setNoVisual(true);
    }

    void clickButtonCircle(Object object, int mx, int my) {
        //create Button Circle for clicked object

        if (object instanceof StreetVehicle) {
            final StreetVehicle vehicle = (StreetVehicle) object;


            if (GameInstance.Settings().CollisionDetection && GameInstance.Settings().DebugMode) {

                removeCircleButtons();
                buttonCircle = new ButtonCircle(vehicle);

                Button deleteButton = new Button(Alignment.Table, 0, 0, Settings.Instance().ButtonWidth, Settings.Instance().ButtonHeight, new Command() {
                    @Override
                    public void execute(Object object) {

                        //to prevent dialog showing multiple times
                        if (!Settings.Instance().isShowingDialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(game);

                            builder.setTitle(game.getString(R.string.DeleteVehicleAlertDialogTitle_Text));
                            builder.setMessage(game.getString(R.string.DeleteVehicleAlertDialogMessage_Text));

                            builder.setPositiveButton(game.getString(R.string.AlertDialog_YesButton_Text), new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {


                                    GameInstance.Airport().RemoveVehicle(vehicle);
                                    removeCircleButtons();
                                    Settings.Instance().isShowingDialog = false;
                                    dialog.dismiss();
                                }
                            });

                            builder.setNegativeButton(game.getString(R.string.AlertDialog_NoButton_Text), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Settings.Instance().isShowingDialog = false;
                                    // Do nothing
                                    dialog.dismiss();
                                }
                            });

                            Settings.Instance().isShowingDialog = true;
                            AlertDialog alert = builder.create();
                            alert.show();
                        }


                    }
                });
                deleteButton.setContent(game.getString(R.string.ButtonCircle_DeleteVehicleButton_Text));


                buttonCircle.addButton(deleteButton);


                Button ignoreCollisionButton = new Button(Alignment.Table, 0, 0, Settings.Instance().ButtonWidth, Settings.Instance().ButtonHeight, new Command() {
                    @Override
                    public void execute(Object object) {

                        vehicle.IgnoreCollision();


                    }
                });
                ignoreCollisionButton.setContent(game.getString(R.string.ButtonCircle_IgnoreCollisionButton_Text));
                buttonCircle.addButton(ignoreCollisionButton);

                objects.addAll(buttonCircle.getButtons());
            }

        }

        if (object instanceof Depot) {

            Depot depot = (Depot) object;

            removeCircleButtons();
            buttonCircle = new ButtonCircle(depot);

            Button showInfo = new Button(Alignment.Table, 0, 0, Settings.Instance().ButtonCircleDiameter, Settings.Instance().ButtonCircleDiameter, new Command() {
                @Override
                public void execute(Object object) {
                    game.setDepotScreen();
                }
            });
//                    showInfo.setContent(game.getString(R.string.ButtonCircle_InfoButton_Text));
            showInfo.setImageID(BitmapLoader.indexCircleButtonInfo);
            buttonCircle.addButton(showInfo);

            objects.addAll(buttonCircle.getButtons());
        }


        if (object instanceof Airplane) {
            final Airplane plane = (Airplane) object;
            //clearSelectableObjects();

            removeCircleButtons();
            buttonCircle = new ButtonCircle(plane);

            if (GameInstance.Settings().DebugMode) {

                Button deleteButton = new Button(Alignment.Table, 0, 0, Settings.Instance().ButtonWidth, Settings.Instance().ButtonHeight, new Command() {
                    @Override
                    public void execute(Object object) {

                        //to prevent dialog showing multiple times
                        if (!Settings.Instance().isShowingDialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(game);

                            builder.setTitle(game.getString(R.string.DeleteVehicleAlertDialogTitle_Text));
                            builder.setMessage(game.getString(R.string.DeleteVehicleAlertDialogMessage_Text));

                            builder.setPositiveButton(game.getString(R.string.AlertDialog_YesButton_Text), new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {


                                    GameInstance.Airport().RemoveVehicle(plane);
                                    removeCircleButtons();
                                    Settings.Instance().isShowingDialog = false;
                                    dialog.dismiss();
                                }
                            });

                            builder.setNegativeButton(game.getString(R.string.AlertDialog_NoButton_Text), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Settings.Instance().isShowingDialog = false;
                                    // Do nothing
                                    dialog.dismiss();
                                }
                            });

                            Settings.Instance().isShowingDialog = true;
                            AlertDialog alert = builder.create();
                            alert.show();
                        }


                    }
                });
                deleteButton.setContent(game.getString(R.string.ButtonCircle_DeleteVehicleButton_Text));


                buttonCircle.addButton(deleteButton);
            }

            if (GameInstance.Settings().CollisionDetection && GameInstance.Settings().DebugMode) {

                Button ignoreCollisionButton = new Button(Alignment.Table, 0, 0, Settings.Instance().ButtonWidth, Settings.Instance().ButtonHeight, new Command() {
                    @Override
                    public void execute(Object object) {

                        plane.IgnoreCollision();


                    }
                });
                ignoreCollisionButton.setContent(game.getString(R.string.ButtonCircle_IgnoreCollisionButton_Text));
                buttonCircle.addButton(ignoreCollisionButton);
            }

            if (plane.getState() == AirplaneState.Boarding || plane.getState() == AirplaneState.ArrivedAtGate){
                Button showInfo = new Button(Alignment.Table, 0, 0, Settings.Instance().ButtonCircleDiameter, Settings.Instance().ButtonCircleDiameter, new Command() {
                    @Override
                    public void execute(Object object) {
                        game.setAirplaneBoardingScreen();
                    }
                });
                showInfo.setImageID(BitmapLoader.indexCircleButtonInfo);
                buttonCircle.addButton(showInfo);

            }else{
                Button showInfo = new Button(Alignment.Table, 0, 0, Settings.Instance().ButtonCircleDiameter, Settings.Instance().ButtonCircleDiameter, new Command() {
                    @Override
                    public void execute(Object object) {
                        game.setAirplaneInfoScreen();
                    }
                });
                showInfo.setImageID(BitmapLoader.indexCircleButtonInfo);
                buttonCircle.addButton(showInfo);
            }

            switch (plane.getState()) {

                case Init:
                    break;
                case Waiting:
                    break;
                case Arrival:
                    break;
                case Landing:
                    break;
                case WaitingForGate:
                case ReadyForPushback:

                    Button goToTarget = new Button(Alignment.Table, 0, 0, Settings.Instance().ButtonCircleDiameter, Settings.Instance().ButtonCircleDiameter, new Command() {
                        @Override
                        public void execute(Object object) {
                            clearSelectableObjects();
                            handler.selectableGameObjects.addAll(plane.getPossibleTargets());
                            handler.choiceForThis = plane;
                            if (handler.selectableGameObjects.size() > 0) {
                                removeSelectionButton.setEnabled(true);
                                removeSelectionButton.setNoVisual(false);
                            }
                            removeCircleButtons();
                        }
                    });
//                    goToTarget.setContent(game.getString(R.string.ButtonCircle_GoToTargetVehicleButton_Text));
                    goToTarget.setImageID(BitmapLoader.indexCircleButtonGoto);
                    buttonCircle.addButton(goToTarget);
                    break;

                case ArrivedAtGate:
                case Boarding:
                    if (plane.isServiceNotPossible()) {
                        goToTarget = new Button(Alignment.Table, 0, 0, Settings.Instance().ButtonCircleDiameter, Settings.Instance().ButtonCircleDiameter, new Command() {
                            @Override
                            public void execute(Object object) {
                                clearSelectableObjects();
                                handler.selectableGameObjects.addAll(plane.getPossibleTargets());
                                handler.choiceForThis = plane;
                                if (handler.selectableGameObjects.size() > 0) {
                                    removeSelectionButton.setEnabled(true);
                                    removeSelectionButton.setNoVisual(false);
                                }
                                removeCircleButtons();
                            }
                        });
//                        goToTarget.setContent(game.getString(R.string.ButtonCircle_GoToTargetVehicleButton_Text));
                        goToTarget.setImageID(BitmapLoader.indexCircleButtonGoto);
                        buttonCircle.addButton(goToTarget);
                    }
                    break;

                case Pushback:
                    break;
                case TaxiToGate:
                case TaxiToRunway:
                    Button toggleHoldPosition = new Button(Alignment.Table, 0, 0, Settings.Instance().ButtonCircleDiameter, Settings.Instance().ButtonCircleDiameter, new Command() {
                        @Override
                        public void execute(Object object) {
                            plane.setHoldPosition(!plane.isHoldPosition());
                        }
                    });
//                    toggleHoldPosition.setContent(game.getString(R.string.ButtonCircle_HoldPositionVehicleButton_Text));
                    toggleHoldPosition.setImageID(BitmapLoader.indexCircleButtonHold);
                    buttonCircle.addButton(toggleHoldPosition);
                    break;

                case ReadyForDeparture:
                    Button clearedForDepartureButton = new Button(Alignment.Table, 0, 0, Settings.Instance().ButtonCircleDiameter, Settings.Instance().ButtonCircleDiameter, new Command() {
                        @Override
                        public void execute(Object object) {
                            for (RoadIntersection intersection : plane.getPossibleTargets()) {
                                plane.searchRoute(intersection);
                                break;
                            }
                            plane.setHoldPosition(false);
                            buttonCircle.setCoolDown();
                        }
                    });
//                    clearedForDepartureButton.setContent(game.getString(R.string.ButtonCircle_ClearedForDepartureVehicleButton_Text));
                    clearedForDepartureButton.setImageID(BitmapLoader.indexCircleButtonTakeOff);
                    buttonCircle.addButton(clearedForDepartureButton);
                    break;
                case ClearedForDeparture:
                    break;
                case Takeoff:
                    break;
                case Departure:
                    break;
            }


            objects.addAll(buttonCircle.getButtons());
        }
    }

    void setSelectableRoadIntersections(RoadIntersection lastIntersection) {
        handler.selectableGameObjects.clear();
        for (int i = 0; i < GameInstance.Airport().getRoadIntersectionCount(); i++) {
            RoadIntersection intersection = GameInstance.Airport().getRoadIntersection(i);

            if (intersection.equals(lastIntersection)) continue;//dont add selected intersection

            Road[] raodArray = intersection.getRoadArray();

            if (GameInstance.Settings().buildRoad == null) {
                GameInstance.Settings().buildRoad = RoadType.None;
            }

            switch (GameInstance.Settings().buildRoad) {

                case None:
                    break;

                case taxiway:
                    boolean noTaxiwayOrRunway = true;
                    for (int j = 0; j < raodArray.length; j++) {
                        Road connectedRoad = raodArray[j];
                        if (connectedRoad instanceof Taxiway || connectedRoad instanceof Runway) {
                            noTaxiwayOrRunway = false;
                            break;
                        }
                    }
                    if (noTaxiwayOrRunway) {
                        continue;
                    }
                    break;

                case runway:
                    boolean noTaxiway = true;
                    for (int j = 0; j < raodArray.length; j++) {
                        Road connectedRoad = raodArray[j];
                        if (connectedRoad instanceof Runway) {
                            noTaxiway = true;
                            break;
                        }
                        if (connectedRoad instanceof Taxiway) {
                            noTaxiway = false;
                        }
                    }
                    if (noTaxiway) {
                        continue;
                    }
                    break;

                case street:
                    boolean noStreet = true;
                    for (int j = 0; j < raodArray.length; j++) {
                        Road connectedRoad = raodArray[j];
                        if (connectedRoad instanceof Street) {
                            noStreet = false;
                            break;
                        }
                        if (connectedRoad instanceof ParkGate && connectedRoad.getNext().equals(intersection)) {
                            noStreet = false;
                            break;
                        }
                    }
                    if (noStreet) {
                        continue;
                    }
                    break;

                case parkGate:

                    boolean noTaxiwayForGate = true;
                    for (int j = 0; j < raodArray.length; j++) {
                        Road connectedRoad = raodArray[j];
                        if (connectedRoad instanceof ParkGate || connectedRoad instanceof Runway) {
                            noTaxiwayForGate = true;
                            break;
                        }
                        if (handler.buildRoad == null && connectedRoad instanceof Taxiway) {
                            noTaxiwayForGate = false;
                        }
                        if (handler.buildRoad != null && connectedRoad instanceof Street){
                            noTaxiwayForGate = false;
                        }
                    }
                    if (noTaxiwayForGate) {
                        continue;
                    }
                    break;
                default:

            }

            handler.selectableGameObjects.add(intersection);
        }
    }

    void switchBuild(int newBuild) {
        clearSelectableObjects();

        //reset buttons
        buildRoadbutton.setEnabled(false);
        buildRoadbutton.setNoVisual(true);
        showNextAirplanesButton.setEnabled(false);
        showNextAirplanesButton.setNoVisual(true);
        switch (newBuild) {
            case 1:
                GameInstance.Airport().setPauseSimulation(true);
                GameInstance.Settings().buildMode = 1;
                buildRoadbutton.setEnabled(true);
                buildRoadbutton.setNoVisual(false);
                setSelectableRoadIntersections(null);
                break;
            case 2:
                GameInstance.Settings().buildMode = 2;
                GameInstance.Airport().setPauseSimulation(true);

                handler.setAllDeletableObjects();

                break;
            case 3:
                GameInstance.Settings().buildMode = 3;
                GameInstance.Airport().setPauseSimulation(true);
//                handler.selectableGameObjects.clear();

                handler.setSelectableObjectsForBuildBuilding();

//                for (int i = 0; i < GameInstance.Airport().getRoadCount(); i++) {
//                    Road road = GameInstance.Airport().getRoad(i);
//                    if (road.getLength() > GameInstance.Settings().buildMinRadius * 1.2 && road instanceof Street && road.getBuilding() == null)
//                        handler.selectableGameObjects.add(road);
//                }
                break;
            default:
                showNextAirplanesButton.setEnabled(true);
                showNextAirplanesButton.setNoVisual(false);

                GameInstance.Settings().buildMode = newBuild;
                GameInstance.Settings().buildRoad = RoadType.None;
                GameInstance.Settings().buildDepot = BuildingType.None;
                GameInstance.Settings().selectionCompleted = false;
                GameInstance.Settings().buildPrice = 0L;
                handler.selectableGameObjects.clear();
                handler.firstRoadIntersection = null;
                handler.buildRoad = null;
                GameInstance.Airport().setPauseSimulation(false);
                GameInstance.Airport().CheckGateServicePossibility();
                break;
        }

    }
}
