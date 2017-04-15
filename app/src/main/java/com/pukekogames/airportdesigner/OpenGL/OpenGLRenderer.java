package com.pukekogames.airportdesigner.OpenGL;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import com.pukekogames.airportdesigner.Activities.Game;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.R;
import org.w3c.dom.Text;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Marko Rapka on 14.04.2017.
 */
public class OpenGLRenderer implements GLSurfaceView.Renderer {

    private static int FPS = 30;
    private Game game;
    private float red;

    private long startTime;
    private long timeMillis;
    private long waitTime;
    private long totalTime = 0;
    private int frameCount = 0;
    private long targetTime = 1000 / FPS;


    public OpenGLRenderer(Game game) {
        this.game = game;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

        PrimitiveHelper.init();
        TextureLoader.Instance().loadTextures(game);


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        startTime = System.nanoTime();

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        red += 0.01f;
        if (red > 1) {
            red = 0f;
        }
        GLES20.glClearColor(red, 0.0f, 0.0f, 1.0f);

        int texture = TextureLoader.Instance().getTexture(Images.indexAirplaneSmall);

        for (int i = 0; i < 1000; i++) {
            PrimitiveHelper.drawImageSize(texture, 0, 0, 100, 100);
        }


        timeMillis = (System.nanoTime() - startTime) / 1000000;
        waitTime = targetTime - timeMillis;

        //wait
        try {
            if (waitTime > 0) Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        totalTime += System.nanoTime() - startTime;
        frameCount++;

        if (frameCount == FPS) {
            double averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
            frameCount = 0;
            totalTime = 0;
            System.out.println("FPS: " + averageFPS);
        }

    }
}
