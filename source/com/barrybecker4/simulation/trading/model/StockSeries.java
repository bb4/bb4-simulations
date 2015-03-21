package com.barrybecker4.simulation.trading.model;

import com.barrybecker4.common.math.function.Function;
import com.barrybecker4.common.math.function.HeightFunction;

import java.util.LinkedList;
import java.util.List;


/**
 * A collection of stock market series to show.
 * Just keeps track of the last N series.
 * @author Barry Becker
 */
public class StockSeries extends LinkedList<Function> {

    private int maxNum;


    public StockSeries(int maxSeriesToKeep) {
        maxNum = maxSeriesToKeep;
    }

    public boolean add(Function func) {
        boolean success = super.add(func);
        if (size() > maxNum) {
            remove(0);
        }
        return success;
    }
}
