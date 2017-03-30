package com.pukekogames.airportdesigner.Objects.UIElements;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Helper.Command;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 22.09.2016.
 */
public class UIStack extends Button {

    private static final long serialVersionUID = -7922286670261438867L;
    private ArrayList<Button> buttons = new ArrayList<>();
    boolean isExtended = false;
    float extendState = 0f;

    public UIStack(Alignment alignment, float x, float y, float width, float height, Command method) {
        super(alignment, x, y, width, height, method);


    }

    @Override
    public void clicked(int mx, int my) {
        isExtended = !isExtended;
        updateChildren();

        super.clicked(mx, my);
    }

    @Override
    public void tick() {
        super.tick();

        if (isExtended){
            extendState += GameInstance.Settings().shiftPerTick;
        }else{
            extendState -= GameInstance.Settings().shiftPerTick;
        }
        if (extendState < 0){
            extendState = 0;
            for (Button button : buttons) {
                button.setEnabled(isExtended);
                button.setNoVisual(!isExtended);
            }

        }
        if (extendState > 1) {
            extendState = 1;
            for (Button button : buttons) {
                button.setEnabled(isExtended);
                button.setNoVisual(!isExtended);
            }
        }

        if (extendState > 0) {
            for (int i = 0; i < buttons.size(); i++) {
                Button button = buttons.get(i);
                button.setAnimationPoint(x, y);
                button.setAni_Ratio(extendState);
                if (extendState < 1) {
                    button.setEnabled(false);
                    button.setNoVisual(false);
                }
            }
        }
    }

    public void addButton(Button button) {
        button.setEnabled(isExtended);
        button.setNoVisual(!isExtended);
        buttons.add(button);
    }

    public void clearButtons() {
        buttons.clear();
    }

    public ArrayList<Button> getButtons() {
        if (isExtended) return buttons;
        else return new ArrayList<Button>();
    }

    private void updateChildren(){
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            if (button instanceof UIStack && !isExtended){
                UIStack stack = (UIStack) button;
                stack.setExtended(false);
            }
        }
    }

    public void setExtended(boolean extended){
        if (isExtended != extended){
            method.execute(this);
        }
        isExtended = extended;
        updateChildren();
    }

    public void setExtendedWithOutExecute(boolean extended){
        isExtended = extended;
        updateChildren();
    }

    public boolean isExtended() {
        return isExtended;
    }
}
