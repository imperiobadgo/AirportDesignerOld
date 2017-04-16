package com.pukekogames.airportdesigner.OpenGL;

/**
 * Created by Marko Rapka on 16.04.2017.
 */
public class Texture {

    private int id;
    private float width, height;

    public Texture(int id, float width, float height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
