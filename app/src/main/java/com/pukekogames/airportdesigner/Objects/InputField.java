package com.pukekogames.airportdesigner.Objects;

import com.pukekogames.airportdesigner.Helper.Alignment;

/**
 * Created by Marko Rapka on 09.11.2015.
 */
public class InputField extends ClickableGameObject {

    private static final String acceptedCharacters = "abcdefghijklmnopqrstuvwxyz äöü1234567890.,:?!=€@()+-#^&/";
    private static final long serialVersionUID = 8855219187877244640L;
    private String text;
    private String function;
    public boolean enterPressed;

    public InputField(Alignment alignment, float x, float y, boolean moveable, String function) {
        super(alignment, x, y, 30, 15, moveable);
        this.function = function;
        text = "";
        selected = false;
    }

//    public void keyPressed(KeyEvent k) {
//        int code = (int) k.getKeyChar();
//
//        if (lastSelected) {
////            System.out.println("KeyCode: " + code);
//            if (code == 8) {
//                if (text.length() >= 1) {
//                    text = text.substring(0, text.length() - 1);
//                }
//            } else if (code == 10) {
//                enterPressed = true;// sets enterPressed, so handler can handle this action and resets afterwards
//            } else {
//                String character = "" + (char) code;
//                if (acceptedCharacters.contains(character.toLowerCase()))
//                    text = text + (char) code;
//            }
//        }
//    }

    @Override
    public void tick() {

    }


    @Override
    public void clicked(int mx, int my) {

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFunction() {
        return function;
    }
}
