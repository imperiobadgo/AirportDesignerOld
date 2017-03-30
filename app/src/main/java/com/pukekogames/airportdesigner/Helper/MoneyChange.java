package com.pukekogames.airportdesigner.Helper;

import java.io.Serializable;

/**
 * Created by Marko Rapka on 30.10.2016.
 */
public class MoneyChange implements Serializable {
    private static final long serialVersionUID = 6062466789867332782L;
    public long amount;
    public int time;

    public MoneyChange(long amount){
        this.amount = amount;
    }
}
