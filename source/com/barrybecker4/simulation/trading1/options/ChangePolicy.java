/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading1.options;

/**
 * When the market changes by "changePercent" %,
 * then buy or sell (determined by sign) "transactPercent" % of current invested dollars.
 *
 * @author Barry Becker
 */
public class ChangePolicy {

    /** the percent increase or decrease in the market.  (0 - 1.0) */
    private double changePercent;

    /** Either the percent of current investment to sell, or percent of current reserve to buy more with. (0 - 1.0) */
    private double transactPercent;

    public ChangePolicy(double changePercent, double transactPercent) {
        this.changePercent = changePercent;
        this.transactPercent = transactPercent;
    }


    public double getChangePercent() {
        return changePercent;
    }

    public double getTransactPercent() {
        return transactPercent;
    }
}
