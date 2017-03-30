package com.pukekogames.airportdesigner.Helper;

/**
 * Created by Marko Rapka on 05.12.2015.
 */
public class Helper {
    public static boolean IsNumericInt(String str){
        try{
            int i = Integer.parseInt(str);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
