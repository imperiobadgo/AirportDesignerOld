package com.pukekogames.airportdesigner.Activities;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Marko Rapka on 01.06.2017.
 */
public class MainActivity extends SimpleBaseGameActivity {


    private static final int CAMERA_WIDTH = 1920; //720;
    private static final int CAMERA_HEIGHT = 1080;

    private SmoothCamera mSmoothCamera;
    private BitmapTextureAtlas mBitmapTextureAtlas;
    private ITexture mTexture;
    private ITextureRegion mFaceTextureRegion;
    ArrayList<Sprite> rotAirplane;

    @Override
    public EngineOptions onCreateEngineOptions() {
        this.mSmoothCamera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 10, 10, 1.0f);

        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mSmoothCamera);
    }

    @Override
    protected void onCreateResources() throws IOException {
//        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("drawable/");

        try {
            this.mTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("airplane_777.png");//"face_box.png");
                }
            });

            this.mTexture.load();
            this.mFaceTextureRegion = TextureRegionFactory.extractFromTexture(this.mTexture);
        } catch (IOException e) {
            Debug.e(e);
        }

//        this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
//        this.mFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "face_box.png", 0, 0);
//        this.mBitmapTextureAtlas.load();
    }


    @Override
    protected Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = new Scene();
        scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));

		/* Calculate the coordinates for the screen-center. */
        final float centerX = (CAMERA_WIDTH - this.mFaceTextureRegion.getWidth()) / 2;
        final float centerY = (CAMERA_HEIGHT - this.mFaceTextureRegion.getHeight()) / 2;

		/* Create some faces and add them to the scene. */
        final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();

        Random r = new Random();

        rotAirplane = new ArrayList<>();

        for (int i = 0; i < 300; i++) {
            Sprite sprite = new Sprite(r.nextInt(CAMERA_WIDTH), r.nextInt(CAMERA_HEIGHT), this.mFaceTextureRegion, vertexBufferObjectManager);
            sprite.setScale(0.5f);
            sprite.setRotation(r.nextFloat() * 180);
            rotAirplane.add(sprite);
            scene.attachChild(sprite);
        }

//        rotAirplane = new Sprite(centerX, centerY, this.mFaceTextureRegion, vertexBufferObjectManager);

//        scene.attachChild(new Sprite(centerX - 25, centerY - 25, this.mFaceTextureRegion, vertexBufferObjectManager));
//        scene.attachChild(new Sprite(centerX  + 25, centerY - 25, this.mFaceTextureRegion, vertexBufferObjectManager));
//        scene.attachChild(new Sprite(centerX, centerY + 25, this.mFaceTextureRegion, vertexBufferObjectManager));


        scene.registerUpdateHandler(new TimerHandler(0.1f, true, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                if (rotAirplane == null) return;
                for (Sprite sprite: rotAirplane){
                    sprite.setRotation(sprite.getRotation() + 1f);
                }

            }
        }));

        scene.setOnSceneTouchListener(new IOnSceneTouchListener() {
            @Override
            public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
                switch(pSceneTouchEvent.getAction()) {
                    case TouchEvent.ACTION_DOWN:
                        MainActivity.this.mSmoothCamera.setZoomFactor(5.0f);
                        break;
                    case TouchEvent.ACTION_UP:
                        MainActivity.this.mSmoothCamera.setZoomFactor(1.0f);
                        break;
                }
                return true;
            }
        });

        return scene;
    }
}
