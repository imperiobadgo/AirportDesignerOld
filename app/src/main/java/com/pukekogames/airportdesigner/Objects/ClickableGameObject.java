package com.pukekogames.airportdesigner.Objects;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;

import java.io.File;

/**
 * Created by Marko Rapka on 06.11.2015.
 */
public abstract class ClickableGameObject extends GameObject {
    private static final long serialVersionUID = 2065928420680740124L;
    protected float width, height;
    private boolean movable = false;
    protected boolean selected;
    protected boolean lastSelected;//is used to tell the inputfield, that is was selected last for keyboard input
    private File clickedSoundFile;
    boolean mouseOver;
    private boolean zoomable;
    private boolean ontable;//moves with the table when dragging
    boolean noVisual = false;
//    protected Rectangle collisionBox;
    protected float heading;

    protected ClickableGameObject(Alignment alignment, float x, float y, float width, float height) {
        super(alignment, x, y);
        this.width = width;
        this.height = height;
    }

    ClickableGameObject(Alignment alignment, float x, float y, float width, float height, boolean movable) {
        super(alignment, x, y);
        this.width = width;
        this.height = height;
        this.movable = movable;
    }

    public boolean isColliding(PointInt pos) {

        float scale = GameInstance.Settings().Zoom;
        if (alignment == Alignment.Table && imageID != -1) {
            int scaledWidth = (int) (scale *width);
            int scaledHeight = (int) (scale * height);
            PointInt centerPos = getPositionForRender(Align_X, Align_Y);
            int diffX = centerPos.x - pos.x;
            int diffY = centerPos.y - pos.y;
            double distance = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
            return distance < GameInstance.Settings().clickRadius;
        } else {
            PointInt centerPos = getPositionForRender(Align_X + width / 2, Align_Y + height / 2);
            int diffX = centerPos.x - pos.x;
            int diffY = centerPos.y - pos.y;
            double distance = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
            return distance < GameInstance.Settings().clickRadius;
        }




//        setCollisionBox();
//
//        int size = 2;
//        Rectangle source = new Rectangle(pos.x - size, pos.y - size, size * 2, size * 2);
//
//        AffineTransform af = new AffineTransform();
//        af.rotate(Math.toRadians(heading), collisionBox.getCenterX(), collisionBox.getCenterY());
//        Area area = new Area(collisionBox);
//        area = area.createTransformedArea(af);
//
//        return area.intersects(source);

    }

    public abstract void clicked(int mx, int my);

    public void setSelected(Object o) {
        selected = this.equals(o);
    }

    public void setLastSelected(Object o) {
        lastSelected = this.equals(o);
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public void setClickedSoundFile(String strFilename) {
        try {
            clickedSoundFile = new File(strFilename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getClickedSoundFile() {
        return clickedSoundFile;
    }

    public void setDimension(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void isVisual() {
        noVisual = true;
    }

    public boolean isNoVisual() {
        return noVisual;
    }

    public void setNoVisual(boolean noVisual){
        this.noVisual = noVisual;
    }

    public boolean isMovable() {
        return movable;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isLastSelected() {
        return lastSelected;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public void setHeading(float heading) {
        this.heading = heading;
    }

    public float getHeading() {
        return heading;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }
}
