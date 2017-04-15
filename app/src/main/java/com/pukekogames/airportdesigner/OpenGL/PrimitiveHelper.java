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
    private static ShaderProgram basicShader;

    private static String vertexShaderSource = "" +
            "attribute vec4 a_position;" +
            "attribute vec2 a_texCoordinate;" +
            "" +
            "varying vec2 v_TexCoordinate;" +
            "" +
            "void main()" +
            "{" +
            "   gl_Position = a_position;" +
            "   v_TexCoordinate = a_texCoordinate;" +
            "}" +
            "";

    private static String fragmentShaderSource = "" +
            "uniform sampler2D u_Texture;" +
            "" +
            "varying vec2 v_TexCoordinate;" +
            "" +
            "void main()" +
            "{" +
            "   gl_FragColor = texture2D(u_Texture, v_TexCoordinate);" +
            "}" +
            "";

    private static float[] quad = {
            -0.5f, 0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.0f, 1.0f,

            -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.0f, 1.0f

    };
    private static float[] texCoordinates = {
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

        ByteBuffer texCoordsByteBuffer = ByteBuffer.allocateDirect(texCoordinates.length * 4);
        texCoordsByteBuffer.order(ByteOrder.nativeOrder());
        quadTextureBuffer = texCoordsByteBuffer.asFloatBuffer();
        quadTextureBuffer.put(texCoordinates);
        quadTextureBuffer.rewind();

        basicShader = ShaderProgram.createBasicShader(vertexShaderSource, fragmentShaderSource);
        basicShader.bindAttribLocation(0, "a_position");
        basicShader.bindAttribLocation(1, "a_texCoordinate");

        GLES20.glVertexAttribPointer(0, 4, GLES20.GL_FLOAT, false, 4 * 4, quadBuffer);
        GLES20.glVertexAttribPointer(1, 4, GLES20.GL_FLOAT, false, 2 * 4, quadTextureBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glEnableVertexAttribArray(1);

    }

    public static void drawImageSize(int texture, float x, float y, float width, float height){
        basicShader.enable();

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        int mTextureUniformHandle = basicShader.getUniformLocation("u_Texture");

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        //enable alpha blending
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisable(GLES20.GL_BLEND);
        basicShader.disable();

    }

}
