package com.pukekogames.airportdesigner.Objects.UIElements;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Helper.Command;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Objects.ClickableGameObject;


/**
 * Created by Marko Rapka on 05.11.2015.
 */
public class Button extends ClickableGameObject {

    private static final long serialVersionUID = -979446396276322847L;
    private String content;
    private boolean isEnabled;
    private boolean contentSet;
    private boolean showBackground;
    private int hasClicked;//To show that the button was clicked
    private int reactionTime;
    Command method;//The Method called when user clicks on the button


    public Button(Alignment alignment, float x, float y, float width, float height, Command method) {
        super(alignment, x, y, width, height);
        this.method = method;
        isEnabled = true;
        reactionTime = 0;
    }

    public void setContent(String content) {
        this.content = content;

        contentSet = true;
    }

    public void tick() {
        if (hasClicked > 0) hasClicked--;
    }

    @Override
    public boolean isColliding(PointInt pos) {
        return isEnabled && (pos.x > x && pos.x < x + width && pos.y > y && pos.y < y + height);
    }


    public void clicked(int mx, int my) {
        if (hasClicked < 1) {
            if (reactionTime <= 0) {
                hasClicked = GameInstance.Settings().buttonReactionShowTime;
            }else{
                hasClicked = reactionTime;
            }

            method.execute(this);
        }
    }

    @Override
    public void setImageID(int id){
        imageID = id;
        showBackground = true;
    }

    public String getContent() {
        return content;
    }

    public boolean isContentSet() {
        return contentSet;
    }

    public int getHasClicked() {
        return hasClicked;
    }

    public void setEnabled(boolean enabled){
        isEnabled = enabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setReactionTime(int reactionTime) {
        this.reactionTime = reactionTime;
    }

    public boolean isShowBackground() {
        return showBackground;
    }

    public void setShowBackground(boolean showBackground) {
        this.showBackground = showBackground;
    }
}