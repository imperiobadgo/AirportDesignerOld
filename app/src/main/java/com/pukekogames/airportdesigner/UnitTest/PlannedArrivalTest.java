package com.pukekogames.airportdesigner.UnitTest;

import com.pukekogames.airportdesigner.Objects.Airlines.PlannedArrival;

/**
 * Created by Marko Rapka on 09.03.2017.
 */
public class PlannedArrivalTest {

    static void testPercentage() {

        System.out.println("---Start------------- Test PlannedArrival getPercentage ----------------------");

        for (int i = 0; i < 24; i++) {
            PlannedArrival arrival = new PlannedArrival(i);

            int min = (i - arrival.getMaxHourOffset());

            int max = (i + arrival.getMaxHourOffset());

            for (int j = min; j <= max; j++) {
                int hour = j % 24;
                if (hour < 0) hour += 24;
                arrival.setHour(hour);
                System.out.println(i + " - " + arrival.getHour() + " : " + arrival.getPercentage());
            }
            System.out.println(" ");
        }

        System.out.println("---End  ------------- Test PlannedArrival getPercentage ----------------------");

    }
}
