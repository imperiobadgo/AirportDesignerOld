package com.pukekogames.airportdesigner.Helper;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;

import java.io.Serializable;

/**
 * Created by Marko Rapka on 03.10.2016.
 */
public class GameSave implements Serializable {

    private static final long serialVersionUID = -4824758681103404720L;
    public static int SAVECOUNT = 3;
    private static GameSave ourInstance = new GameSave();

    private GameSave(){
        gameInstances = new GameInstance[SAVECOUNT];
        currentSlot = 0;
    }
    public static GameSave Instance() {
        return ourInstance;
    }

    public static void setGameSave(GameSave GameInstance) {
        ourInstance = GameInstance;
    }

    public int currentSlot;

    transient public GameInstance[] gameInstances;//save each GameInstance separate, to avoid Stackoverflow errors

}
