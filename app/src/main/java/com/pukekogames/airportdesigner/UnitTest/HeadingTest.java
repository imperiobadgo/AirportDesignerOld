package com.pukekogames.airportdesigner.UnitTest;

/**
 * Created by Marko Rapka on 02.04.2017.
 */
public class HeadingTest {

    static void test() {

        System.out.println("---Start------------- Test Heading ----------------------");

        for (int heading = 0; heading < 360; heading += 10) {
            float dirX = (float) Math.cos(Math.toRadians(heading));
            float dirY = (float) Math.sin(Math.toRadians(heading));

            float headingDirect = (float) Math.toDegrees(Math.atan2(dirX, dirY));

            System.out.println("Given: " + heading + " dirX: " + dirX + " dirY: " + dirY + " heading: " + headingDirect);

        }

        System.out.println("---End  ------------- Test Heading ----------------------");

    }

    static void TestDirectHeading() {

        System.out.println("---Start------------- Test Direct Heading ----------------------");

        float selfX = 100;
        float selfY = 100;

        int stepSize = 10;

        for (int x = 0; x < 200; x += stepSize) {

            float diffX = x - selfX;
            float diffY = 0 - selfY;
            float headingDirectToTarget = (float) Math.toDegrees(Math.atan2(diffY, diffX)) % 360;

            System.out.println(" diffX: " + diffX + " diffY: " + diffY + " heading: " + headingDirectToTarget);

        }

        for (int y = 0; y < 200; y += stepSize) {

            float diffX = 200 - selfX;
            float diffY = y - selfY;
            float headingDirectToTarget = (float) Math.toDegrees(Math.atan2(diffY, diffX)) % 360;

            System.out.println(" diffX: " + diffX + " diffY: " + diffY + " heading: " + headingDirectToTarget);

        }

        for (int x = 0; x < 200; x += stepSize) {

            float diffX = (200 - x) - selfX;
            float diffY = 200 - selfY;
            float headingDirectToTarget = (float) Math.toDegrees(Math.atan2(diffY, diffX)) % 360;

            System.out.println(" diffX: " + diffX + " diffY: " + diffY + " heading: " + headingDirectToTarget);

        }

        for (int y = 0; y < 200; y += stepSize) {

            float diffX = 0 - selfX;
            float diffY = (200 - y) - selfY;
            float headingDirectToTarget = (float) Math.toDegrees(Math.atan2(diffY, diffX)) % 360;

            System.out.println(" diffX: " + diffX + " diffY: " + diffY + " heading: " + headingDirectToTarget);

        }

        System.out.println("---End  ------------- Test Direct Heading ----------------------");
    }
}
