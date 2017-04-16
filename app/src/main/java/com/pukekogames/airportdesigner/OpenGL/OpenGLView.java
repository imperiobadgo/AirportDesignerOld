package com.pukekogames.airportdesigner.OpenGL;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import com.pukekogames.airportdesigner.Activities.Game;

/**
 * Created by Marko Rapka on 15.04.2017.
 */
public class OpenGLView extends GLSurfaceView {

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    private OpenGLRenderer renderer;


    public OpenGLView(Game game) {
        super(game);

        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        renderer = new OpenGLRenderer(game);
        setRenderer(renderer);

    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1;
                }

                PrimitiveHelper.Angle += ((dx + dy) * TOUCH_SCALE_FACTOR);
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;

    }
}
