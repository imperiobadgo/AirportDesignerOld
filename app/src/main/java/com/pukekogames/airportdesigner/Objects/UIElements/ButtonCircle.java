package com.pukekogames.airportdesigner.Objects.UIElements;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Objects.GameObject;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Marko Rapka on 08.08.2016.
 */
public class ButtonCircle extends GameObject {

    private static final long serialVersionUID = -3958312525628931930L;
    private GameObject targetObject;
    private CopyOnWriteArrayList<Button> buttons = new CopyOnWriteArrayList<>();
    private int radius = 100;
    private int coolDown;


    public ButtonCircle(GameObject object) {
        super(Alignment.Table, 0, 0);
        targetObject = object;
        coolDown = GameInstance.Settings().SelectionTime;
    }

    @Override
    public void tick() {
        coolDown--;
        if (buttons.size() == 0 || targetObject == null) return;
        float anglePerButton = 360 / buttons.size();
        PointInt pos = getPositionForRender(targetObject.getX(), targetObject.getY());
//        int minDistance = 20;
//        if (pos.x - radius < 0){
//            pos.x = pos.x + radius;
//        }
//        if (pos.x + radius > GameInstance.Settings().screenSize.x){
//            pos.x = pos.x - radius;
//        }
//        if (pos.y - radius < 0){
//            pos.y = pos.y + radius;
//        }
//        if (pos.y + radius > GameInstance.Settings().screenSize.y){
//            pos.y = pos.y - radius;
//        }

//        System.out.println("pos : " + pos.x + " " + pos.y);
        for (int i = 0; i < buttons.size(); i++) {
            int xPos = (int) Math.round(Math.cos(Math.toRadians((270 + anglePerButton * i) % 360)) * radius + pos.x);
            int yPos = (int) Math.round(Math.sin(Math.toRadians((270 + anglePerButton * i) % 360)) * radius + pos.y);
            try {
                Button button = buttons.get(i);
                int width = (int) button.getWidth();
                int height = (int) button.getHeight();

                button.setPosition(xPos - width / 2, yPos - height / 2);
            }catch (Exception e){
                //problem between mainthread and event
                return;
            }
        }


    }

    public void addButton(Button button) {
        buttons.add(button);
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setTargetObject(GameObject targetObject) {
        this.targetObject = targetObject;
    }

    public void clearButtons(){
        buttons.clear();
    }

    public CopyOnWriteArrayList<Button> getButtons() {
        return buttons;
    }

    public boolean shouldRemove() {
        return coolDown < 0 && buttons.size() > 0;
    }

    public void setCoolDown() {
        coolDown = 0;
    }
}
