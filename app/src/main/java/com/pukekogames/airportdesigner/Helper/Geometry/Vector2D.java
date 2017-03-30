package com.pukekogames.airportdesigner.Helper.Geometry;

import java.io.Serializable;

/**
 * Created by Marko Rapka on 14.10.2016.
 */
public class Vector2D implements Serializable {
    private static final long serialVersionUID = -754760069958727320L;
    private float x;
    private float y;

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(PointFloat p1, PointFloat p2) {
        if (p1 == null || p2 == null) return;
        x = p2.x - p1.x;
        y = p2.y - p1.y;
    }

    public Vector2D(float x1, float y1, float x2, float y2) {
        x = x2 - x1;
        y = y2 - y1;
    }

    public Vector2D(float angle) {
        x = (float) Math.cos(Math.toRadians(angle));
        y = (float) Math.sin(Math.toRadians(angle));
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float angle) {
        x = (float) Math.cos(Math.toRadians(angle));
        y = (float) Math.sin(Math.toRadians(angle));
    }

    public void set(PointFloat p1, PointFloat p2) {
        if (p1 == null || p2 == null) return;
        x = p2.x - p1.x;
        y = p2.y - p1.y;
    }

    public void set(float x1, float y1, float x2, float y2) {
        x = x2 - x1;
        y = y2 - y1;
    }

    public void Add(Vector2D b) {
        if (b == null) return;
        x = x + b.x;
        y = y + b.y;
    }

    public void Subtract(Vector2D b) {
        if (b == null) return;
        x = x - b.x;
        y = y - b.y;
    }

    public void Multiply(Vector2D b) {
        if (b == null) return;
        x = x * b.x;
        y = y * b.y;
    }

    public void Multiply(float b) {
        x = x * b;
        y = y * b;
    }

    public void Normalize(){
        float length = Length();
        x = x / length;
        y = y / length;
    }

    public float Length(){
        return  (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public static Vector2D Add(Vector2D a, Vector2D b) {
        if (a == null || b == null) return null;
        float resultX = a.x + b.x;
        float resultY = a.y + b.y;
        return new Vector2D(resultX, resultY);
    }

    public static Vector2D Subtract(Vector2D a, Vector2D b) {
        if (a == null || b == null) return null;
        float resultX = a.x - b.x;
        float resultY = a.y - b.y;
        return new Vector2D(resultX, resultY);
    }



    public static Vector2D Multiply(Vector2D a, Vector2D b) {
        if (a == null || b == null) return null;
        float resultX = a.x * b.x;
        float resultY = a.y * b.y;
        return new Vector2D(resultX, resultY);
    }


    public static float AngleBetween(Vector2D a, Vector2D b) {
        if (a == null || b == null) return 0;

        Vector2D v1 = new Vector2D(a.x, a.y);
        Vector2D v2 = new Vector2D(b.x, b.y);
        v1.Normalize();
        v2.Normalize();
        float dot = DotProduct(v1, v2);


        return (float) Math.toDegrees(Math.acos(dot));
    }

    public static float DotProduct(Vector2D a, Vector2D b) {
        if (a == null || b == null) return 0;

        return a.x * b.x + a.y * b.y;
    }
}
