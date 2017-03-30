package com.pukekogames.airportdesigner.UnitTest;

import com.pukekogames.airportdesigner.Helper.TimeStamp;

/**
 * Created by Marko Rapka on 13.03.2017.
 */
public class TimeStempTest {


    static void RunTests(){
//        increaseTest();
        minuteDifferenceTest();
    }

    private static void increaseTest(){
        TimeStamp time = new TimeStamp(0);

        for (int i = 0; i < 3000; i++) {
            System.out.println("hour " + time.getHour() + " minute " + time.getMinute());
            time.increase();
        }
    }

    private static void minuteDifferenceTest(){
        TimeStamp time = new TimeStamp(500);

        TimeStamp time2 = new TimeStamp(100);

        for (int i = 0; i < 9000; i++) {
            if (i % 100 == 0){
                System.out.println(time.getMinuteDifference(time2) + " ");
            }

            time.increase();
        }

    }
}
