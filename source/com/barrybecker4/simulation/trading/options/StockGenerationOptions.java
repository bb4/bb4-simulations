/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.options;

/**
 * Defaults for stock time series generation.
 * A single stock or a collection of stocks can be simulated.
 *
 * @author Barry Becker
 */
public class StockGenerationOptions {

    private static final int DEFAULT_NUM_STOCKS = 1;
    private static final int DEFAULT_NUM_TIME_PERIODS = 100;
    private static final double DEFAULT_PERCENT_INCREASE = 0.04;
    private static final double DEFAULT_PERCENT_DECREASE = 0.03;
    private static final double DEFAULT_STARTING_VALUE = 100;
    private static final boolean DEFAULT_USE_RANDOM_CHANGE = false;

    public int numStocks = DEFAULT_NUM_STOCKS;
    public int numTimePeriods = DEFAULT_NUM_TIME_PERIODS;
    public double percentIncrease = DEFAULT_PERCENT_INCREASE;
    public double percentDecrease = DEFAULT_PERCENT_DECREASE;
    public double startingValue = DEFAULT_STARTING_VALUE;
    public boolean useRandomChange = DEFAULT_USE_RANDOM_CHANGE;

    /**
     * This theoretical maximum is something that could occur, but rarely
     * ever would by chance. One use of it is used to set an upper limit on the axis.
     * @return upper limit on sample value.
     */
    public double getTheoreticalMaximum() {
        return startingValue * Math.pow(1.0 + percentIncrease, numTimePeriods);
    }

}
