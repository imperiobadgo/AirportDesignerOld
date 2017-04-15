package com.pukekogames.airportdesigner.Main;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import com.pukekogames.airportdesigner.Helper.ChangeValues;
import com.pukekogames.airportdesigner.Activities.Game;
import com.pukekogames.airportdesigner.Rendering.Render;

/**
 * Created by Marko Rapka on 25.05.2016.
 */
public class MainThread extends Thread {


    private static int FPS = 30;
    private ChangeValues cV;
    private final SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    private boolean paused;

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel, ChangeValues cV) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
        this.cV = cV;
    }

    @Override
    public void run() {
        //TODO: change the gameloop!
        Log.d(Game.TAG, "Starting game loop");

        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000 / FPS;
        running = true;

        while (running) {
//            if (paused) {
//                try {
//                    Thread.sleep(200);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                continue;
//            }
            startTime = System.nanoTime();
            Canvas canvas = null;

            canvas = surfaceHolder.lockCanvas();
            if (canvas == null) {
                //if canvas is null stop thread because SurfaceView is destroyed
                break;
            }
            synchronized (surfaceHolder) {
                this.gamePanel.update();
                this.gamePanel.draw(canvas);
            }

            try {
                surfaceHolder.unlockCanvasAndPost(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            }

            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;

            try {
                if (waitTime > 0) Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == FPS) {
                double averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
                cV.message = "FPS: " + Math.round(averageFPS) + " RC: " + Render.renderCalls;
                Render.renderCalls = 0;
                frameCount = 0;
                totalTime = 0;
//                Log.d(Game.TAG, "FPS: " + averageFPS);
            }

        }


    }

    public void setRunning(boolean b) {
        running = b;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean setPause(boolean paused) {
        return this.paused = paused;
    }
}
