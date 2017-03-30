package com.pukekogames.airportdesigner.Objects;

import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;

import java.io.Serializable;

/**
 * Created by Marko Rapka on 04.11.2015.
 */
public abstract class GameObject implements Serializable, Cloneable {
    private static final long serialVersionUID = 4314721654176150088L;
    protected float x, y;
    protected float Align_X, Align_Y;
    protected float ani_X, ani_Y;
    protected float ani_Ratio;
    Alignment alignment;
    private int renderOrder;

    public boolean isInView;

    protected int imageID = -1;

    public GameObject(Alignment alignment, float align_X, float align_Y) {
        this.alignment = alignment;
        this.Align_X = align_X;
        this.Align_Y = align_Y;
        this.renderOrder = 3;
        ani_Ratio = -1;
    }

    public static PointInt getPositionForRender(float x, float y) {
        float zoom = GameInstance.Settings().Zoom * GameInstance.Settings().Scale;
        PointInt tableCenter = GameInstance.Settings().MapCenter;
        int tableSizeX = GameInstance.Settings().MapSizeX;
        int tableSizeY = GameInstance.Settings().MapSizeY;
        //Position of the top left corner of the table in the Viewport
        PointInt tableTopLeft = new PointInt((int) (tableCenter.x - tableSizeX * zoom), (int) (tableCenter.y - tableSizeY * zoom));
        return new PointInt(x * zoom + tableTopLeft.x, y * zoom + tableTopLeft.y);
    }

    public abstract void tick();

    public void setImageID(int imageID) {this.imageID = imageID;}

    public int getImageID() {return imageID;}


    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlign_X(float align_X) {
        Align_X = align_X;
    }

    public void setAlign_Y(float align_Y) {
        Align_Y = align_Y;
    }

    public float getAlign_X() {
        return Align_X;
    }

    public float getAlign_Y() {
        return Align_Y;
    }

    public void setAnimationPoint(float xPos, float yPos){
        ani_X = xPos;
        ani_Y = yPos;
    }

    public float getAni_X() {
        return ani_X;
    }

    public float getAni_Y() {
        return ani_Y;
    }

    public float getAni_Ratio() {
        return ani_Ratio;
    }

    public void setAni_Ratio(float ani_Ratio) {
        this.ani_Ratio = ani_Ratio;
    }

    public void setRenderOrder(int renderOrder) {
        this.renderOrder = renderOrder;
    }

    public void setTopRender(){renderOrder = GameInstance.Settings().RenderDepth;}

    public int getRenderOrder() {
        return renderOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameObject)) return false;

        GameObject that = (GameObject) o;

        if (Float.compare(that.x, x) != 0) return false;
        if (Float.compare(that.y, y) != 0) return false;
        if (Float.compare(that.Align_X, Align_X) != 0) return false;
        if (Float.compare(that.Align_Y, Align_Y) != 0) return false;
        if (renderOrder != that.renderOrder) return false;
        if (imageID != that.imageID) return false;
        return alignment == that.alignment;

    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (Align_X != +0.0f ? Float.floatToIntBits(Align_X) : 0);
        result = 31 * result + (Align_Y != +0.0f ? Float.floatToIntBits(Align_Y) : 0);
        result = 31 * result + (alignment != null ? alignment.hashCode() : 0);
        result = 31 * result + renderOrder;
        result = 31 * result + imageID;
        return result;
    }
}
