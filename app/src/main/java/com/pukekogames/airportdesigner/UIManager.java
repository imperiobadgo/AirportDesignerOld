package com.pukekogames.airportdesigner;

import android.view.MotionEvent;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Helper.Command;
import com.pukekogames.airportdesigner.Helper.ScreenState;
import com.pukekogames.airportdesigner.Objects.GameObject;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.UIElements.Button;
import com.pukekogames.airportdesigner.Objects.UIElements.ScrollList;
import com.pukekogames.airportdesigner.Objects.UIElements.TimeTable;
import com.pukekogames.airportdesigner.Objects.UIElements.UIStack;
import com.pukekogames.airportdesigner.Activities.Game;
import com.pukekogames.airportdesigner.Rendering.BitmapLoader;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Marko Rapka on 12.11.2016.
 */
public class UIManager {
    private Handler handler;
    private Game game;
    private ScreenState screenState;
    private Screen currentScreen;
    private GameScreen gameScreen;
    private TimeWindowScreen timeWindowScreen;
    private AirlineScreen airlineScreen;

    private static UIManager manager;

    CopyOnWriteArrayList<GameObject> objects;

    private boolean shiftAirport = false;
    private float xTopRight = -110;
    private float yTopRight = 10;


    UIManager(Handler handler, Game game) {
        this.handler = handler;
        this.game = game;
        objects = new CopyOnWriteArrayList<>();
        GameInstance.Settings().airportShift = 0;
        manager = this;
    }

    void tick() {
        for (GameObject tempObject : objects) {
            tempObject.tick();
        }

        currentScreen.tick();
        if (!shiftAirport) {
            GameInstance.Settings().airportShift += GameInstance.Settings().shiftPerTick;
        } else {
            GameInstance.Settings().airportShift -= GameInstance.Settings().shiftPerTick;
        }
        if (GameInstance.Settings().airportShift < 0 && GameInstance.Settings().airportShift > -1) {
            GameInstance.Settings().airportShift = 0;
        }
        if (GameInstance.Settings().airportShift > 1) {
            GameInstance.Settings().airportShift = 1;
        }
    }

    CopyOnWriteArrayList<GameObject> getUIObject() {
        return objects;
    }

    CopyOnWriteArrayList<GameObject> getScreenObjects() {
        return currentScreen.getObjects();
    }

    public Game getGame() {
        return game;
    }

    public Handler getHandler() {
        return handler;
    }

    public void clearSelectableObjects() {
        handler.clearSelectableObjects();
        if (gameScreen != null) {
            gameScreen.removeSelectionButton.setEnabled(false);
            gameScreen.removeSelectionButton.setNoVisual(true);
        }
    }

    boolean onTouch(MotionEvent event) {
        return currentScreen.onTouch(event);
    }

    boolean touchReleased(MotionEvent event) {
        return currentScreen.touchReleased(event);
    }

    public boolean OnScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (currentScreen == null) return false;
        boolean scrolled = false;
        for (int i = 0; i < currentScreen.getObjects().size(); i++) {
            GameObject object = currentScreen.getObjects().get(i);
            if (object instanceof ScrollList) {
                ScrollList list = (ScrollList) object;
                scrolled = list.OnScroll(e1, e2, distanceX, distanceY);
            } else if (object instanceof TimeTable) {
                TimeTable table = (TimeTable) object;
                scrolled = table.onScroll(e1, e2, distanceX, distanceY);
            }
            if (scrolled) break;
        }
        return scrolled;
    }

    void changeScreen(ScreenState nextState) {
        boolean initialize = false;
        if (currentScreen != null) {
            if (currentScreen == gameScreen) {
                gameScreen.switchBuild(0);
            }
            currentScreen.setButtonStackExtended(false);
//            currentScreen.ClearScreen();
        } else {
            initialize = true;
        }
        switch (nextState) {

            case Game:
                currentScreen = gameScreen;
                shiftAirport = false;
                GameInstance.Airport().setPauseSimulation(false);
                break;
            case TimeWindow:
                currentScreen = timeWindowScreen;
                shiftAirport = true;
                GameInstance.Airport().setPauseSimulation(true);
                break;
            case Airline:
                currentScreen = airlineScreen;
                shiftAirport = true;
                GameInstance.Airport().setPauseSimulation(true);
            default:
                break;
        }
        if (initialize) {
            currentScreen.SetupScreen();
        } else {
            currentScreen.UpdateScreen();
        }
//        if (currentScreen != gameScreen) {
//            currentScreen.setButtonStackExtended(true);
//        }
        screenState = nextState;

    }

    Button removeSelectionButton() {
        return gameScreen.removeSelectionButton;
    }

    void clickButtonCircle(Object object, int mx, int my) {
        gameScreen.clickButtonCircle(object, mx, my);
    }

    void setSelectableRoadIntersections(RoadIntersection lastIntersection) {
        gameScreen.setSelectableRoadIntersections(lastIntersection);
    }

    int getButtonCircleCount() {
        return gameScreen.buttonCircle.getButtons().size();
    }

    void removeCircleButtons() {
        gameScreen.removeCircleButtons();
    }

    void repositionAll() {
        BitmapLoader.repositionAll(GameInstance.Settings().screenSize, objects);
        if (currentScreen == null) return;
        BitmapLoader.repositionAll(GameInstance.Settings().screenSize, currentScreen.getObjects());
    }


    private void setMainButtonStack() {

        int diameter = (int) (Settings.Instance().ButtonCircleDiameter * 1.2);
        int distance = 20;

        UIStack stack = new UIStack(Alignment.TopRight, xTopRight, yTopRight, diameter, diameter, new Command() {
            @Override
            public void execute(Object object) {
                if (object instanceof UIStack) {
                    UIStack callerStack = (UIStack) object;

                    if (!callerStack.isExtended()) {
                        //return to main gameScreen when options are retracted
                        if (screenState != ScreenState.Game) {
                            changeScreen(ScreenState.Game);
                        }
                        gameScreen.switchBuild(0);
                    }

                }
            }
        });

        stack.setImageID(BitmapLoader.indexOptionButton);
        stack.setShowBackground(false);
        BitmapLoader.reposition(stack);

        UIStack buildButtonStack = new UIStack(Alignment.TopRight, xTopRight, yTopRight + diameter + distance, diameter, diameter, new Command() {
            @Override
            public void execute(Object object) {
                if (screenState != ScreenState.Game) {
                    changeScreen(ScreenState.Game);
                }

                if (object instanceof UIStack) {
                    UIStack callerStack = (UIStack) object;

                    gameScreen.switchBuild(0);

                    if (callerStack.isExtended()) {
                        GameInstance.Airport().setPauseSimulation(true);
                    }

                }
            }
        });

        buildButtonStack.setImageID(BitmapLoader.indexButtonBuild);
        gameScreen.setSubMenuButtons(buildButtonStack);
        stack.addButton(buildButtonStack);
        objects.add(buildButtonStack);


        Button saveLoadButton = new Button(Alignment.TopRight, xTopRight, yTopRight + 200, 100, 50, new Command() {
            @Override
            public void execute(Object object) {
                game.setLoadSaveScreen();
            }
        });
        saveLoadButton.setContent(game.getString(R.string.SaveButton_Text));
//        setObjectBitmap(airlineScreenButton);
        BitmapLoader.reposition(saveLoadButton);
        objects.add(saveLoadButton);
        stack.addButton(saveLoadButton);

        Button mainmenuButton = new Button(Alignment.TopRight, xTopRight, yTopRight + 280, 100, 50, new Command() {
            @Override
            public void execute(Object object) {
                game.setMainMenu();
            }
        });
        mainmenuButton.setContent(game.getString(R.string.BackToMainMenuButton_Text));
        BitmapLoader.reposition(mainmenuButton);
        objects.add(mainmenuButton);
        stack.addButton(mainmenuButton);

        Button timeSlotScreenButton = new Button(Alignment.TopRight, xTopRight, yTopRight + 360, 100, 50, new Command() {
            @Override
            public void execute(Object object) {
                if (screenState != ScreenState.TimeWindow) {
                    changeScreen(ScreenState.TimeWindow);
                } else if (screenState == ScreenState.TimeWindow) {
                    changeScreen(ScreenState.Game);
                }

//                game.setAirlineScreen();
            }
        });
        timeSlotScreenButton.setContent("TimeSlots");
//        setObjectBitmap(airlineScreenButton);
        objects.add(timeSlotScreenButton);
        stack.addButton(timeSlotScreenButton);


        UIStack airlineScreenButtonStack = new UIStack(Alignment.TopRight, xTopRight, yTopRight + 440, 100, 50, new Command() {
            @Override
            public void execute(Object object) {
                if (screenState != ScreenState.Airline) {
                    changeScreen(ScreenState.Airline);
                } else if (screenState == ScreenState.Airline) {
                    changeScreen(ScreenState.Game);
                }
            }
        });

        airlineScreenButtonStack.setContent(game.getString(R.string.SwitchToAirlineScreenButton_Text));
        airlineScreen.setSubMenuButtons(airlineScreenButtonStack);
//        setObjectBitmap(airlineScreenButton);
        objects.add(airlineScreenButtonStack);
        stack.addButton(airlineScreenButtonStack);

        objects.add(stack);

        repositionAll();
    }

    void setMainOptions() {
        currentScreen = null;
        gameScreen = new GameScreen(this);
        timeWindowScreen = new TimeWindowScreen(this);
        airlineScreen = new AirlineScreen(this);
        timeWindowScreen.SetupScreen();
        airlineScreen.SetupScreen();
        changeScreen(ScreenState.Game);


        setMainButtonStack();

        Button switchGameSpeedButton = new Button(Alignment.TopLeft, 150, 10, 100, 50, new Command() {
            @Override
            public void execute(Object object) {
                if (GameInstance.Settings().gameSpeed == 1) {
                    GameInstance.Settings().gameSpeed = 2;
                } else if (GameInstance.Settings().gameSpeed == 2) {
                    GameInstance.Settings().gameSpeed = 5;
                } else if (GameInstance.Settings().gameSpeed == 5) {
                    GameInstance.Settings().gameSpeed = 10;
                } else {
                    GameInstance.Settings().gameSpeed = 1;
                }

            }
        });
        switchGameSpeedButton.setContent("GS");
        objects.add(switchGameSpeedButton);

        Button addMoneyButton = new Button(Alignment.TopLeft, 0, 0, 120, 70, new Command() {
            @Override
            public void execute(Object object) {
                if (GameInstance.Settings().DebugMode) {
                    GameInstance.Instance().addMoney(100L);
                }
            }
        });
        addMoneyButton.setNoVisual(true);
        objects.add(addMoneyButton);

        Button toggleCollisionButton = new Button(Alignment.TopLeft, 0, 90, 120, 70, new Command() {
            @Override
            public void execute(Object object) {
                if (GameInstance.Settings().DebugMode) {
                    GameInstance.Settings().CollisionDetection = !GameInstance.Settings().CollisionDetection;
                }
            }

        });
        toggleCollisionButton.setNoVisual(true);
        objects.add(toggleCollisionButton);

        Button addLevelButton = new Button(Alignment.TopLeft, 0, 180, 120, 70, new Command() {
            @Override
            public void execute(Object object) {
                if (GameInstance.Settings().DebugMode) {
                    GameInstance.Settings().level += 1;
                }
            }
        });
        addLevelButton.setNoVisual(true);
        objects.add(addLevelButton);

        repositionAll();

    }

    public ScreenState getScreenState() {
        return screenState;
    }

    public static void UpdateScreens() {
        if (manager == null) return;
        manager.currentScreen.UpdateScreen();
    }
}
