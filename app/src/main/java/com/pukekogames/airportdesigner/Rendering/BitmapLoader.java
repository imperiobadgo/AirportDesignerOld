package com.pukekogames.airportdesigner.Rendering;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Helper.Geometry.PointInt;
import com.pukekogames.airportdesigner.Objects.GameObject;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.Activities.Game;
import com.pukekogames.airportdesigner.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Marko Rapka on 25.05.2016.
 */
public class BitmapLoader {
    private static BitmapLoader ourInstance = new BitmapLoader();

    public static BitmapLoader Instance() {
        return ourInstance;
    }

    //    public static int indexAirplane = 30;
//    public static int indexBus = 40;
//    public static int indexRunwayEnd = 80;
//    public static int indexRunwayMiddle = 81;
//    public static int indexStreet = 85;
//    public static int indexTaxiway = 90;
//    public static int indexParkGate = 95;
    public static int indexCircleButtonBackground = 70;
    public static int indexCircleButtonBackgroundClicked = 71;
    public static int indexOptionButton = 72;
    public static int indexCircleButtonGoto = 73;
    public static int indexCircleButtonHold = 74;
    public static int indexCircleButtonInfo = 75;
    public static int indexCircleButtonTakeOff = 76;
    public static int indexButtonBuild = 80;
    public static int indexButtonBuildRoad = 81;
    public static int indexButtonBuildDepot = 82;
    public static int indexButtonDelete = 83;
    public static int indexButtonConstruct = 84;
    public static int indexButtonAccept = 85;
    public static int indexButtonCancel = 86;

    public static int width = 600;
    public static int height = 400;

//    public static int SampleSize = 1;

    private Bitmap[] bitmaps = new Bitmap[100];

    public void loadBitmaps(Resources resources) {
        int quality = 5;
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inScaled = false;
        bitmaps[Images.indexAirplaneSmall] = BitmapFactory.decodeResource(resources, R.drawable.airplane_small, options);
        bitmaps[Images.indexAirplaneCessna] = BitmapFactory.decodeResource(resources, R.drawable.airplane_cessna, options);
        bitmaps[Images.indexAirplaneA320] = BitmapFactory.decodeResource(resources, R.drawable.airplane_a320, options);
        bitmaps[Images.indexAirplane777] = BitmapFactory.decodeResource(resources, R.drawable.airplane_777, options);
        bitmaps[Images.indexBus] = BitmapFactory.decodeResource(resources, R.drawable.bus, options);
        bitmaps[Images.indexCateringTruck] = BitmapFactory.decodeResource(resources, R.drawable.cateringtruck, options);
        bitmaps[Images.indexCrewBus] = BitmapFactory.decodeResource(resources, R.drawable.crewbus, options);
        bitmaps[Images.indexTankTruck] = BitmapFactory.decodeResource(resources, R.drawable.tanktruck, options);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        bitmaps[indexAirplane].compress(Bitmap.CompressFormat.PNG, quality, out);
//        bitmaps[indexAirplane] = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
//        bitmaps[indexAirplane] = Bitmap.createScaledBitmap(bitmaps[indexAirplane], bitmaps[indexAirplane].getWidth() / SampleSize,bitmaps[indexAirplane].getHeight() / SampleSize, false);
        out = new ByteArrayOutputStream();
        bitmaps[Images.indexRunwayEnd] = BitmapFactory.decodeResource(resources, R.drawable.runway_end, options);
//        bitmaps[indexRunwayEnd].compress(Bitmap.CompressFormat.PNG, quality, out);
//        bitmaps[indexRunwayEnd] = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
//        bitmaps[indexRunwayEnd] = Bitmap.createScaledBitmap(bitmaps[indexRunwayEnd], bitmaps[indexRunwayEnd].getWidth() / SampleSize,bitmaps[indexRunwayEnd].getHeight() / SampleSize, false);
        out = new ByteArrayOutputStream();
        bitmaps[Images.indexRunwayMiddle] = BitmapFactory.decodeResource(resources, R.drawable.runway_middle, options);
//        bitmaps[indexRunwayMiddle].compress(Bitmap.CompressFormat.PNG, quality, out);
//        bitmaps[indexRunwayMiddle] = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
//        bitmaps[indexRunwayMiddle] = Bitmap.createScaledBitmap(bitmaps[indexRunwayMiddle], bitmaps[indexRunwayMiddle].getWidth() / SampleSize,bitmaps[indexRunwayMiddle].getHeight() / SampleSize, false);
        out = new ByteArrayOutputStream();
        bitmaps[Images.indexTaxiway] = BitmapFactory.decodeResource(resources, R.drawable.taxiway, options);
        bitmaps[Images.indexStreet] = BitmapFactory.decodeResource(resources, R.drawable.street, options);
        bitmaps[Images.indexParkGate] = BitmapFactory.decodeResource(resources, R.drawable.parkgate, options);
        bitmaps[Images.indexBusDepot] = BitmapFactory.decodeResource(resources, R.drawable.depot_bus, options);
        bitmaps[Images.indexCateringDepot] = BitmapFactory.decodeResource(resources, R.drawable.depot_catering, options);
        bitmaps[Images.indexCrewBusDepot] = BitmapFactory.decodeResource(resources, R.drawable.depot_crewbus, options);
        bitmaps[Images.indexTankDepot] = BitmapFactory.decodeResource(resources, R.drawable.depot_tank, options);
        bitmaps[Images.indexTower] = BitmapFactory.decodeResource(resources, R.drawable.tower, options);
        bitmaps[Images.indexTerminal] = BitmapFactory.decodeResource(resources, R.drawable.terminal, options);
//        bitmaps[indexTaxiway].compress(Bitmap.CompreBitmapFactory.decodeResource(resources, R.drawable.tower, options);ssFormat.PNG, quality, out);
//        bitmaps[indexTaxiway] = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
//        bitmaps[indexTaxiway] = Bitmap.createScaledBitmap(bitmaps[indexTaxiway], bitmaps[indexTaxiway].getWidth() / SampleSize,bitmaps[indexTaxiway].getHeight() / SampleSize, false);

        bitmaps[indexCircleButtonBackground] = BitmapFactory.decodeResource(resources, R.drawable.button_circlebackground);
        bitmaps[indexCircleButtonBackgroundClicked] = BitmapFactory.decodeResource(resources, R.drawable.button_clickedcirclebackground);

        bitmaps[indexOptionButton] = BitmapFactory.decodeResource(resources, R.drawable.button_options, options);
        bitmaps[indexCircleButtonGoto] = BitmapFactory.decodeResource(resources, R.drawable.button_goto, options);
        bitmaps[indexCircleButtonHold] = BitmapFactory.decodeResource(resources, R.drawable.button_hold, options);
        bitmaps[indexCircleButtonInfo] = BitmapFactory.decodeResource(resources, R.drawable.button_info, options);
        bitmaps[indexCircleButtonTakeOff] = BitmapFactory.decodeResource(resources, R.drawable.button_takeoff, options);

        bitmaps[indexButtonBuild] = BitmapFactory.decodeResource(resources, R.drawable.button_build, options);
        bitmaps[indexButtonBuildRoad] = BitmapFactory.decodeResource(resources, R.drawable.button_buildroad, options);
        bitmaps[indexButtonBuildDepot] = BitmapFactory.decodeResource(resources, R.drawable.button_builddepot, options);
        bitmaps[indexButtonConstruct] = BitmapFactory.decodeResource(resources, R.drawable.button_construct, options);
        bitmaps[indexButtonDelete] = BitmapFactory.decodeResource(resources, R.drawable.button_delete, options);
        bitmaps[indexButtonAccept] = BitmapFactory.decodeResource(resources, R.drawable.button_accept, options);
        bitmaps[indexButtonCancel] = BitmapFactory.decodeResource(resources, R.drawable.button_cancel, options);

        Log.i(Game.TAG, "Imagewidth: " + bitmaps[Images.indexRunwayMiddle].getWidth() + " height: " + bitmaps[Images.indexRunwayMiddle].getHeight());
    }

    public Bitmap getBitmap(int BitmapIndex) {
        if (BitmapIndex > 0 && BitmapIndex < bitmaps.length) return bitmaps[BitmapIndex];
        return null;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
//        bm.recycle();
        return resizedBitmap;
    }

    public void resizeAllBitmaps() {
        int screenWidth = GameInstance.Settings().screenSize.x;
        int screenHeight = GameInstance.Settings().screenSize.y;
        float normWidth = 1920;
        float normHeight = 1080;
        float scaleWidth = screenWidth / normWidth;
        float scaleHeight = screenHeight / normHeight;
//        float scaleWidth = normWidth / screenWidth;
//        float scaleHeight = normHeight / screenHeight;
        Log.i(Game.TAG, "scaleWidth: " + scaleWidth + " scaleHeight: " + scaleHeight);
        for (int i = 0; i < bitmaps.length; i++) {
            if (bitmaps[i] == null) continue;
            int originalWidth = bitmaps[i].getWidth();
            int originalHeight = bitmaps[i].getHeight();
            Log.i(Game.TAG, "Bitmap: " + i + " width: " + originalWidth + " height: " + originalHeight);
            bitmaps[i] = Bitmap.createScaledBitmap(bitmaps[i], (int) (originalWidth * scaleWidth), (int) (originalHeight * scaleHeight), false);
        }
    }

    //is called from window when resized to repostion all the gameobjects
    public static void repositionAll(PointInt dimension, CopyOnWriteArrayList<GameObject> objects) {
        if (dimension != null)
            GameInstance.Settings().screenSize = dimension;
        for (GameObject object : objects) {
            reposition(object);
        }
    }

    //is called from window when resized to repostion all the gameobjects
    public static void repositionAll(PointInt dimension, ArrayList<GameObject> objects) {
        if (dimension != null)
            GameInstance.Settings().screenSize = dimension;
        for (GameObject object : objects) {
            reposition(object);
        }
    }

    public static void reposition(GameObject object) {
//
//        int width = GameInstance.Settings().screenSize.x;
//        int height = GameInstance.Settings().screenSize.y;
//
//
//        Alignment objectAlignment = object.getAlignment();
//        float objectAlign_X = object.getAlign_X();
//        float objectAlign_Y = object.getAlign_Y();
//
//        float x = 0;
//        float y = 0;
//
//        switch (objectAlignment) {
//            case TopLeft:
//                x = objectAlign_X;
//                y = objectAlign_Y;
//                break;
//            case TopRight:
//                x = width + objectAlign_X;
//                y = objectAlign_Y;
//                break;
//            case BottomLeft:
//                x = objectAlign_X;
//                y = height + objectAlign_Y;
//                break;
//            case BottomRight:
//                x = width + objectAlign_X;
//                y = height + objectAlign_Y;
//                break;
//            case Center:
//                x = (width / 2) + objectAlign_X;
//                y = (height / 2) + objectAlign_Y;
//                break;
//            case Top:
//                x = (width / 2) + objectAlign_X;
//                y = objectAlign_Y;
//                break;
//            case Table:
//                x = objectAlign_X;
//                y = objectAlign_Y;
//                break;
//        }
        object.setPosition(getRepositionX(object), getRepositionY(object));
    }


    public static float getRepositionX(GameObject object) {
        int width = GameInstance.Settings().screenSize.x;

        Alignment objectAlignment = object.getAlignment();
        float objectAlign_X = object.getAlign_X();

        float x = 0;

        switch (objectAlignment) {
            case TopLeft:
                x = objectAlign_X;
                break;
            case TopRight:
                x = width + objectAlign_X;
                break;
            case BottomLeft:
                x = objectAlign_X;
                break;
            case BottomRight:
                x = width + objectAlign_X;
                break;
            case Center:
                x = (width / 2) + objectAlign_X;
                break;
            case Top:
                x = (width / 2) + objectAlign_X;
                break;
            case Table:
                x = objectAlign_X;
                break;
        }
        return x;
    }

    public static float getRepositionY(GameObject object) {
        int height = GameInstance.Settings().screenSize.y;

        Alignment objectAlignment = object.getAlignment();

        float objectAlign_Y = object.getAlign_Y();

        float y = 0;

        switch (objectAlignment) {
            case TopLeft:
                y = objectAlign_Y;
                break;
            case TopRight:
                y = objectAlign_Y;
                break;
            case BottomLeft:
                y = height + objectAlign_Y;
                break;
            case BottomRight:
                y = height + objectAlign_Y;
                break;
            case Center:
                y = (height / 2) + objectAlign_Y;
                break;
            case Top:
                y = objectAlign_Y;
                break;
            case Table:
                y = objectAlign_Y;
                break;
        }
        return y;
    }


    public static void setPosition(GameObject object, int newX, int newY) {

        int width = GameInstance.Settings().screenSize.x;
        int height = GameInstance.Settings().screenSize.y;

        Alignment objectAlignment = object.getAlignment();
        float objectAlign_X = object.getAlign_X();
        float objectAlign_Y = object.getAlign_Y();


        switch (objectAlignment) {
            case TopLeft:
                objectAlign_X = newX;
                objectAlign_Y = newY;
                break;
            case TopRight:
                objectAlign_X = newX - width;
                objectAlign_Y = newY;
                break;
            case BottomLeft:
                objectAlign_X = newX;
                objectAlign_Y = newY - height;
                break;
            case BottomRight:
                objectAlign_X = newX - width;
                objectAlign_Y = newY - height;
                break;
            case Center:
                objectAlign_X = newX - (width / 2);
                objectAlign_Y = newY - (height / 2);
                break;
            case Top:
                objectAlign_X = newX - (width / 2);
                objectAlign_Y = newY;
                break;
            case Table:
                objectAlign_X = newX;
                objectAlign_Y = newY;
                break;
        }
        object.setAlign_X(objectAlign_X);
        object.setAlign_Y(objectAlign_Y);

    }
}
