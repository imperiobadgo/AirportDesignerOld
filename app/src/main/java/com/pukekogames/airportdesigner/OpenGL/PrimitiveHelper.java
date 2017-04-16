package com.pukekogames.airportdesigner.OpenGL;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Objects.ClickableGameObject;
import com.pukekogames.airportdesigner.Objects.GameObject;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Roads.Runway;
import com.pukekogames.airportdesigner.OpenGL.Draw.DrawObject;
import com.pukekogames.airportdesigner.Rendering.Render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Marko Rapka on 15.04.2017.
 */
public class PrimitiveHelper {

    private static FloatBuffer quadBuffer, quadTextureBuffer;
    private static ShaderProgram basicShader;
    private static int WIDTH, HEIGHT;

    private static final float[] mMVPMatrix = new float[16];
    private static final float[] mProjectionMatrix = new float[16];
    private static final float[] mViewMatrix = new float[16];

    private static float[] mRotationMatrix = new float[16];
    private static float[] mTranslationMatrix = new float[16];
    private static float[] mScaleMatrix = new float[16];
    private static float[] matrix = new float[16];

    public static volatile float Angle;

    private static String vertexShaderSource = "" +
            "attribute vec4 a_position;" +
            "attribute vec2 a_texCoordinate;" +
            "" +
            "uniform mat4 u_MVPMatrix;" +
            "" +
            "varying vec2 v_TexCoordinate;" +
            "" +
            "void main()" +
            "{" +
            "   gl_Position = u_MVPMatrix * a_position;" +
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

    static void setProjectionMatrix(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    static void initDraw() {

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, -1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

    }

    public static void drawClickleableGameObject(ClickableGameObject object,float scaledWidth, float scaledHight, float alpha) {
        basicShader.enable();

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        Texture texture = TextureLoader.Instance().getTexture(object.getImageID());

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getId());

        int mTextureUniformHandle = basicShader.getUniformLocation("u_Texture");

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);


//        float[] matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);



//        float[] mRotationMatrix = new float[16];
//        float[] mTranslationMatrix = new float[16];
        Matrix.setIdentityM(mTranslationMatrix,0);

//        float[] mScaleMatrix = new float[16];
        Matrix.setIdentityM(mScaleMatrix,0);

        Matrix.translateM(mTranslationMatrix, 0, ((object.getX() / WIDTH) * -2) + 1, ((object.getY() / HEIGHT) * 2) - 1, 0.0f);

        Matrix.scaleM(mScaleMatrix, 0, scaledWidth, scaledHight, 1.0f);

        Matrix.setRotateM(mRotationMatrix, 0, object.getHeading(), 0.0f, 0.0f, 1.0f);


        float[] temp = new float[16];

//        Matrix.setRotateM(matrix, 0, object.getHeading(), 0.0f, 0.0f, 0.0f);
        Matrix.multiplyMM(temp, 0, mTranslationMatrix, 0, mRotationMatrix, 0);
        Matrix.multiplyMM(matrix, 0, temp, 0, mScaleMatrix, 0);
        Matrix.multiplyMM(matrix, 0, mMVPMatrix, 0, matrix, 0);


        int mMVPMatrixHandle = basicShader.getUniformLocation("u_MVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, matrix, 0);

        //enable alpha blending
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisable(GLES20.GL_BLEND);
        basicShader.disable();
    }

    public static void drawRoad(Road road){
        float ratio = road.getAni_Ratio();
        if (ratio < 0) {
            ratio = 1;
        } else if (ratio < DrawObject.RATIOMIN) {
            ratio = 0;
        }



        Texture texture = TextureLoader.Instance().getTexture(road.getImageID());
        if (texture != null) {
            float scale = GameInstance.Settings().Zoom * GameInstance.Settings().Scale;

            float length = road.getLength();
            float heading = road.getHeading();
            int imageCount = Math.round(length / texture.getWidth());
            float scaleX = (length / (imageCount * texture.getWidth())) * scale;

            PointInt centerPos = Render.getPositionForRender(road.getCenterPosition().x, road.getCenterPosition().y);

            for (int i = 0; i < imageCount; i++) {

                Matrix.setIdentityM(matrix, 0);
                Matrix.setIdentityM(mTranslationMatrix,0);
                Matrix.setIdentityM(mScaleMatrix,0);
                if (i == imageCount - 1 && road instanceof Runway) {

                    float rotHead = (heading + 180) % 360;

                    Matrix.scaleM(mScaleMatrix, 0, scaleX, scale, 1.0f);

                    Matrix.setRotateM(mRotationMatrix, 0, rotHead, 0.0f, 0.0f, 1.0f);

                    Matrix.translateM(mTranslationMatrix, 0, ((centerPos.x / WIDTH) * -2) + 1, ((centerPos.y / HEIGHT) * 2) - 1, 0.0f);

//                    matrix.postScale(scaleX, scale);
//                    matrix.postTranslate(-length * scale / 2, -imageHeight * scale / 2f);
//                    matrix.postRotate(rotHead);
//                    matrix.postTranslate(renderCenter.x, renderCenter.y);

                } else {

                    Matrix.scaleM(mScaleMatrix, 0, scaleX, scale, 1.0f);

                    Matrix.setRotateM(mRotationMatrix, 0, heading, 0.0f, 0.0f, 1.0f);

                    Matrix.translateM(mTranslationMatrix, 0, ((centerPos.x / WIDTH) * -2) + 1, ((centerPos.y / HEIGHT) * 2) - 1, 0.0f);

//                    matrix.postScale(scaleX, scale);
//                    matrix.postTranslate(-(length * scale / 2) + (i * scaleX * imageLength), -imageHeight * scale / 2f);
//                    matrix.postRotate(heading);
//                    matrix.postTranslate(renderCenter.x, renderCenter.y);

                }

                int textureId;

                if (road instanceof Runway) {
                    if (i == 0 || i == imageCount - 1) {
                        textureId = texture.getId();

                    } else {
                        Texture middle = TextureLoader.Instance().getTexture(((Runway)road).getMiddleID());
                        textureId = middle.getId();
                    }
                }else{
                    textureId = texture.getId();
                }

                basicShader.enable();

                // Set the active texture unit to texture unit 0.
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

                // Bind the texture to this unit.
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

                int mTextureUniformHandle = basicShader.getUniformLocation("u_Texture");

                // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
                GLES20.glUniform1i(mTextureUniformHandle, 0);

                float[] temp = new float[16];

//        Matrix.setRotateM(matrix, 0, object.getHeading(), 0.0f, 0.0f, 0.0f);
                Matrix.multiplyMM(temp, 0, mTranslationMatrix, 0, mRotationMatrix, 0);
                Matrix.multiplyMM(matrix, 0, temp, 0, mScaleMatrix, 0);
                Matrix.multiplyMM(matrix, 0, mMVPMatrix, 0, matrix, 0);


                int mMVPMatrixHandle = basicShader.getUniformLocation("u_MVPMatrix");

                // Pass the projection and view transformation to the shader
                GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, matrix, 0);

                //enable alpha blending
                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
                GLES20.glEnable(GLES20.GL_BLEND);

                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

                GLES20.glDisable(GLES20.GL_BLEND);
                basicShader.disable();
            }

        }
    }

    public static void drawImageSize(int texture, float x, float y, float width, float height) {
        basicShader.enable();

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        int mTextureUniformHandle = basicShader.getUniformLocation("u_Texture");

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);


        float[] mRotationMatrix = new float[16];
        float[] scratch = new float[16];

        // Create a rotation transformation for the triangle
//        long time = SystemClock.uptimeMillis() % 4000L;
//        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, Angle, 0, 0, -1.0f);


        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);


        int mMVPMatrixHandle = basicShader.getUniformLocation("u_MVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, scratch, 0);

        //enable alpha blending
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisable(GLES20.GL_BLEND);
        basicShader.disable();

    }

}
