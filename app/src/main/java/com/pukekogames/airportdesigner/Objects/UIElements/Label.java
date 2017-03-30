package com.pukekogames.airportdesigner.Objects.UIElements;

import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Objects.GameObject;

/**
 * Created by Marko Rapka on 06.11.2015.
 */
public class Label extends GameObject {

    private static final long serialVersionUID = -1036573840812751638L;
    private String content;
    private int fontSize;

    public Label(String content, Alignment alignment, float x, float y, int fontSize) {
        super(alignment, x, y);
        this.content = content;
        this.fontSize = fontSize;
    }

    public void tick() {

    }

    public void setContent(String content){
        this.content = content;
    }

    public String getContent(){
        return content;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
}
