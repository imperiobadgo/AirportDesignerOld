package com.pukekogames.airportdesigner.OpenGL;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import com.pukekogames.airportdesigner.Activities.Game;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Marko Rapka on 14.04.2017.
 */
public class OpenGLRenderer implements GLSurfaceView.Renderer {

    private Game game;
    private float red;
    private float[] quad = {
            -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.0f, 1.0f,
            0.0f, 0.5f, 0.0f, 1.0f

    };
    private FloatBuffer quadBuffer;
    private ShaderProgram basicShader;

    public OpenGLRenderer(Game game){
        this.game = game;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(quad.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        quadBuffer = byteBuffer.asFloatBuffer();
        quadBuffer.put(quad);
        quadBuffer.rewind();


        String vertexShaderSource = "" +
                "attribute vec4 position;" +
                "" +
                "void main()" +
                "{" +
                "   gl_Position = position;" +
                "}" +
                "";

        String fragmentShaderSource = "" +
                "" +
                "" +
                "void main()" +
                "{" +
                "   gl_FragColor = vec4(0.8, 1.0, 1.0, 1.0);" +
                "}" +
                "";

        basicShader = ShaderProgram.createBasicShader(vertexShaderSource, fragmentShaderSource);
        basicShader.bindAttribLocation(0, "position");


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        red += 0.01f;
        if (red > 1) {
            red = 0f;
        }
        GLES20.glClearColor(red, 0.0f, 0.0f, 1.0f);

        basicShader.enable();

        GLES20.glVertexAttribPointer(0, 4, GLES20.GL_FLOAT, false, 4 * 4, quadBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        basicShader.disable();
    }
}
