package com.pukekogames.airportdesigner;

import android.view.MotionEvent;
import com.pukekogames.airportdesigner.Objects.GameObject;
import com.pukekogames.airportdesigner.Objects.UIElements.UIStack;
import com.pukekogames.airportdesigner.Activities.Game;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Marko Rapka on 20.11.2016.
 */
public abstract class Screen {

    protected float xTopRight = -110;
    protected float yTopRight = 10;

    UIManager manager;
    Handler handler;
    Game game;

    protected CopyOnWriteArrayList<GameObject> objects;
    UIStack buttonStack;

    public Screen(UIManager manager){
        this.manager = manager;
        game = manager.getGame();
        handler = manager.getHandler();
        objects = new CopyOnWriteArrayList<>();
    }

    abstract public void SetupScreen();

    public void UpdateScreen() {
        manager.repositionAll();
    }

    public void ClearScreen(){
        objects.clear();
    }

    public void tick(){
        for (GameObject object: objects){
            object.tick();
        }
    }

    abstract public boolean onTouch(MotionEvent event);

    abstract public boolean touchReleased(MotionEvent event);

    abstract public void setSubMenuButtons(UIStack stack);

    public CopyOnWriteArrayList<GameObject> getObjects(){
        return objects;
    }

    public void setButtonStackExtended(boolean extended){
        if (buttonStack == null) return;
        buttonStack.setExtendedWithOutExecute(extended);
    }
}
