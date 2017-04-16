package com.pukekogames.airportdesigner.OpenGL;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import com.pukekogames.airportdesigner.Activities.Game;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Geometry.PointFloat;
import com.pukekogames.airportdesigner.Objects.ClickableGameObject;
import com.pukekogames.airportdesigner.Objects.GameObject;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Objects.RoadIntersection;
import com.pukekogames.airportdesigner.Objects.Roads.Road;
import com.pukekogames.airportdesigner.Objects.Roads.Runway;
import com.pukekogames.airportdesigner.Objects.Roads.Street;
import com.pukekogames.airportdesigner.Objects.Roads.Taxiway;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplaneDataA320;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplaneDataCessna;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplaneDataSmall;
import com.pukekogames.airportdesigner.Objects.Vehicles.VehicleData.AirplanePerformance;
import com.pukekogames.airportdesigner.OpenGL.Draw.DrawObject;
import com.pukekogames.airportdesigner.R;
import org.w3c.dom.Text;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Marko Rapka on 14.04.2017.
 */
public class OpenGLRenderer implements GLSurfaceView.Renderer {

    private static int FPS = 30;
    private Game game;
    private float red;


    private ArrayList<ClickableGameObject> testObjects;

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

        testObjects = new ArrayList<>();

        Random random = new Random();

        for (int i = 0; i < 1000; i++) {
            AirplanePerformance p;

            int ran = random.nextInt(3);
            if (ran == 0) {
                p = new AirplaneDataSmall();
            } else if (ran == 1) {
                p = new AirplaneDataCessna();
            } else if (ran == 2) {
                p = new AirplaneDataA320();
            } else {
                p = new AirplaneDataA320();
            }

            Airplane airplane = new Airplane(p, null);
            airplane.setPosition(random.nextInt(1000), random.nextInt(400) + 50);

            airplane.setHeading(10 * i);

            testObjects.add(airplane);
        }

        for (int i = 0; i < 200; i++) {
            RoadIntersection firstInter = new RoadIntersection(new PointFloat(random.nextInt(2000) + 1000, random.nextInt(2000) + 1000));
            RoadIntersection nextInter = new RoadIntersection(new PointFloat(random.nextInt(5000), random.nextInt(2000) + 3000));

            Road road;
            switch (random.nextInt(2)) {
                case 0:
                    road = new Runway();
                    break;
                case 1:
                    road = new Taxiway();
                    break;
                default:
                    road = new Street();
                    break;
            }


            road.setLast(firstInter);
            road.setNext(nextInter);

            road.updatePosition();
            testObjects.add(road);


        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        PrimitiveHelper.setProjectionMatrix(width, height);

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

        PrimitiveHelper.initDraw();

//        int texture = TextureLoader.Instance().getTexture(Images.indexAirplaneSmall);
//
//
////        for (int i = 0; i < 50; i++) {
//        PrimitiveHelper.drawImageSize(texture, 0, 0, 100, 100);
////        }

        for (ClickableGameObject object : testObjects) {
//            object.setPosition(object.getX() + 1f, object.getY());
            object.setHeading(object.getHeading() + 1f);
        }

        for (ClickableGameObject object : testObjects) {
            DrawObject.draw(object);
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