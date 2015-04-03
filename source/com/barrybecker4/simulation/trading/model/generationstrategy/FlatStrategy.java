/*
 * Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/license.MIT
 */

package com.barrybecker4.simulation.trading.model.generationstrategy;

/**
 * @author Barry Becker
 */
public class FlatStrategy extends AbstractGenerationStrategy {

    public String getName() {
        return "flat";
    }

    public String getDescription() {
        return "Does not change at all. Horizontal line.";
    }


    @Override
    public double calcNewPrice(double stockPrice) {
        return stockPrice;
    }

}
