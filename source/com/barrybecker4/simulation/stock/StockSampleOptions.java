/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.stock;

/**
 * Options for a sample of stocks.
 * Use VerhulstOptionsDialog to set them.
 *
 * @author Barry Becker
 */
public class StockSampleOptions {

    static final int DEFAULT_NUM_STOCKS = 1;
    static final int DEFAULT_NUM_TIME_PERIODS = 100;
    static final double DEFAULT_PERCENT_INCREASE = 0.6;
    static final double DEFAULT_PERCENT_DECREASE = 0.4;
    static final double DEFAULT_STARTING_VALUE = 1000;
    static final int DEFAULT_X_RESOLUTION = 2;
    static final boolean DEFAULT_USE_LOG_SCALE = true;
    static final boolean DEFAULT_USE_RANDOM_CHANGE = false;

    public int numStocks = DEFAULT_NUM_STOCKS;
    public int numTimePeriods = DEFAULT_NUM_TIME_PERIODS;
    public double percentIncrease = DEFAULT_PERCENT_INCREASE;
    public double percentDecrease = DEFAULT_PERCENT_DECREASE;
    public double startingValue = DEFAULT_STARTING_VALUE;
    public int xResolution = DEFAULT_X_RESOLUTION;
    public boolean useLogScale = DEFAULT_USE_LOG_SCALE;
    public boolean useRandomChange = DEFAULT_USE_RANDOM_CHANGE;

    /**
     * @return upper limit on sample value.
     */
    public double getTheoreticalMaximum() {
        return startingValue * Math.pow(1.0 + percentIncrease, numTimePeriods);
    }

}
