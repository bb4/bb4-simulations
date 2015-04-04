/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.model.runner;

import com.barrybecker4.common.math.function.Function;

import java.util.LinkedList;


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
