package com.pukekogames.airportdesigner.Main;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.ChangeValues;
import com.pukekogames.airportdesigner.*;
import com.pukekogames.airportdesigner.Activities.Game;

/**
 * Created by Marko Rapka on 25.05.2016.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    Activity context;
    Handler handler;
    MainThread mainThread;
    private GameContent gC;
    private Paint paint;
    private GestureDetectorCompat mDetector;
    private ScaleGestureDetector scaleDetector;
    private TouchHandler touchHandler;

    private static final int INVALID_POINTER_ID = 1;
    private int mActivePointerId = INVALID_POINTER_ID;

    private float mLastTouchX;
    private float mLastTouchY;
    private int justTouched = 0;


    public GamePanel(Activity context, Handler handler) {
        super(context);
        this.context = context;
        paint = new Paint();
        Game game = (Game) context;
        //add the callback to the surfaceHolder to intercept events
        getHolder().addCallback(this);

        this.handler = handler;

//        gC = new GameContent(handler);
//        gC.setGameContent(ScreenState.Menu);
        GameContent.setNewGame();


        touchHandler = new TouchHandler(handler);

        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(context, touchHandler);
        mDetector.setOnDoubleTapListener(touchHandler);
        scaleDetector = new ScaleGestureDetector(context, touchHandler);


        //make gamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i(Game.TAG, "Surface created.");

        ChangeValues cV = new ChangeValues();
        mainThread = new MainThread(getHolder(), this, cV);
        handler.setFpsLabel(cV);

        GameInstance.Settings().screenSize.set(getWidth(), getHeight());
        handler.setupUIManager();// firs setup UIManager, before starting MainThread and reposition
        handler.repositionAll(GameInstance.Settings().screenSize);

        mainThread.start();

        Log.i(Game.TAG, "ScreenSize: x" + GameInstance.Settings().screenSize.x + " y" + GameInstance.Settings().screenSize.y);

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
//        Settings.Instance().screenSize.set(width, height);
//        Log.i(Game.TAG, "ScreenSize: " + Settings.Instance().screenSize.toString());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mainThread.setRunning(false);
        if (context instanceof Game) {
            ((Game) context).save();
        }
        Log.i(Game.TAG, "Surface destroyed.");

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.scaleDetector.onTouchEvent(event);
        handler.setIsMoving(false);

        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = event.getX();
                final float y = event.getY();

                justTouched = 0;

                mLastTouchX = x;
                mLastTouchY = y;

                // Save the ID of this pointer
                mActivePointerId = event.getPointerId(0);

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex = event.findPointerIndex(mActivePointerId);
                final float x = event.getX(pointerIndex);
                final float y = event.getY(pointerIndex);

                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                if (!handler.dontDragWorld()) {
                    handler.setIsMoving(true);
                    if (handler.isDraggingBuildIntersection(x, y)) {
                        handler.touchMoved(event.getX(), event.getY());
                    } else {

                        GameInstance.Settings().MapCenter.x += (int) dx;
                        GameInstance.Settings().MapCenter.y += (int) dy;


                        justTouched++;
                        mLastTouchX = x;
                        mLastTouchY = y;
                    }

                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                handler.touchReleased(event);
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                // Extract the index of the pointer that left the touch sensor
                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = event.getX(newPointerIndex);
                    mLastTouchY = event.getY(newPointerIndex);
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }
        }


        this.mDetector.onTouchEvent(event);
        handler.setIsMoving(false);
        return true;
    }

    public void update() {
        handler.tick();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        paint.reset();
        paint.setColor(Settings.Instance().clearColor);
        canvas.drawRect(0, 0, GameInstance.Settings().screenSize.x, GameInstance.Settings().screenSize.y, paint);

//        int size = 10;
//        for (int i = 0; i < 50; i++) {
//            int color = SeededColor.getSeededColor(i);
//            paint.setColor(color);
//            int x = 100;
//            int y = i * (size + 5);
//            canvas.drawRect(x,y , x + 100, y + size, paint);
//
//        }

        handler.render(canvas, paint);
    }

    public MainThread getMainThread() {
        return mainThread;
    }
}
