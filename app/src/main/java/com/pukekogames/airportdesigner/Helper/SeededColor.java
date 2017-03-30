package com.pukekogames.airportdesigner.Helper;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 03.12.2016.
 */
public class SeededColor {

    public static int getSeededColor(int seed) {
        double[] lastPeriod = new double[2];
        lastPeriod[0] = 1.0;
        lastPeriod[1] = 2.0;

        ArrayList<Double> values = new ArrayList<>();
        values.add(0.0);
        for (int i = 0; i < lastPeriod.length; i++) {
            values.add(lastPeriod[i]);
        }

        while (seed >= values.size()){
            lastPeriod = getNextPeriod(lastPeriod);

            for (int i = 0; i < lastPeriod.length; i++) {
                values.add(lastPeriod[i]);
            }
        }

        return getColorHSV(values.get(seed));
    }

    private static double[] getNextPeriod(double[] lastPeriod){
        double[] newPeriod = new double[lastPeriod.length];
        newPeriod[0] = lastPeriod[0] / 2;
        newPeriod[lastPeriod.length - 1] = (lastPeriod[lastPeriod.length - 2] + 3) / 2;

        for (int i = 1; i < lastPeriod.length; i++) {
            newPeriod[i] = (lastPeriod[i] + lastPeriod[i - 1]) / 2;
        }
        return newPeriod;

    }


    private static int getColorHSV(double t) {
        int i = 0;
        double j = 1.0 / 3.0;

        for (int k = 0; k < 1; k++) {
            if (t > 1.0 / 3.0) {
                i += 1;
                t -= j;
            }
        }
        int color1 = 0;
        int color2 = 0;

        switch (i) {
            case 0:
                color1 = Color.argb(255, 255, 0, 0);
                color2 = Color.argb(255, 0, 255, 0);
                break;
            case 1:
                color1 = Color.argb(255, 0, 255, 0);
                color2 = Color.argb(255, 0, 0, 255);
                break;
            case 2:
                color1 = Color.argb(255, 0, 0, 255);
                color2 = Color.argb(255, 255, 0, 0);
                break;
        }
        return getColor((float)(i / j), color1, color2);
    }

    public static int getColor(float ratio, int i1, int i2) {
//        int c0;
//        int c1;
//        if (p <= 0.5f) {
//            p *= 2;
//            c0 = FIRST_COLOR;
//            c1 = SECOND_COLOR;
//        } else {
//            p = (p - 0.5f) * 2;
//            c0 = SECOND_COLOR;
//            c1 = THIRD_COLOR;
//        }
//        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
//        int r = ave(Color.red(c0), Color.red(c1), p);
//        int g = ave(Color.green(c0), Color.green(c1), p);
//        int b = ave(Color.blue(c0), Color.blue(c1), p);


        if ( ratio > 1f ) ratio = 1f;
        else if ( ratio < 0f ) ratio = 0f;
        float iRatio = 1.0f - ratio;

        //0xAARRGGBB
//        int i1 = c0.getRGB();
//        int i2 = c1.getRGB();

        int a1 = (i1 >> 24 & 0xff);//Masking everything out except alpha
        int r1 = ((i1 & 0xff0000) >> 16);//Masking everything out except red
        int g1 = ((i1 & 0xff00) >> 8);//Masking everything out except green
        int b1 = (i1 & 0xff);//Masking everything out except blue

        int a2 = (i2 >> 24 & 0xff);
        int r2 = ((i2 & 0xff0000) >> 16);
        int g2 = ((i2 & 0xff00) >> 8);
        int b2 = (i2 & 0xff);

        int a = (int)((a1 * iRatio) + (a2 * ratio));
        int r = (int)((r1 * iRatio) + (r2 * ratio));
        int g = (int)((g1 * iRatio) + (g2 * ratio));
        int b = (int)((b1 * iRatio) + (b2 * ratio));

//        if ( ratio > 1f ) ratio = 1f;
//        else if ( ratio < 0f ) ratio = 0f;
//        float iRatio = 1.0f - ratio;
//
//
//        int a = (int)((Color.alpha(c0) * iRatio) + (Color.alpha(c1) * ratio));
//        int r = (int)((Color.red(c0) * iRatio) + (Color.red(c1) * ratio));
//        int g = (int)((Color.green(c0) * iRatio) + (Color.green(c1) * ratio));
//        int b = (int)((Color.blue(c0) * iRatio) + (Color.blue(c1) * ratio));

        return Color.argb(a, r, g, b);
    }

    private static int ave(int src, int dst, float p) {
        return src + Math.round(p * (dst - src));
    }

}
