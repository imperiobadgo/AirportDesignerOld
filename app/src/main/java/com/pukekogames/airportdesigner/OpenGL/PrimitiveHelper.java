package com.pukekogames.airportdesigner.OpenGL;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Marko Rapka on 15.04.2017.
 */
public class PrimitiveHelper {

    private static FloatBuffer quadBuffer, quadTextureBuffer;

    private static float[] quad = {
            -0.5f, 0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.0f, 1.0f,

            -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.0f, 1.0f

    };
    private static float[] texCoords = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };

    public static void init() {
        ByteBuffer quadByteBuffer = ByteBuffer.allocateDirect(quad.length * 4);
        quadByteBuffer.order(ByteOrder.nativeOrder());
        quadBuffer = quadByteBuffer.asFloatBuffer();
        quadBuffer.put(quad);
        quadBuffer.rewind();

        ByteBuffer texCoordsByteBuffer = ByteBuffer.allocateDirect(texCoords.length * 4);
        texCoordsByteBuffer.order(ByteOrder.nativeOrder());
        quadTextureBuffer = texCoordsByteBuffer.asFloatBuffer();
        quadTextureBuffer.put(texCoords);
        quadTextureBuffer.rewind();

    }

    public static void drawImageSize(int texture, float x, float y, float width, float height){

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

    }

}
