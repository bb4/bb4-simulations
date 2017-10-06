/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.stock1;

/**
 * Defaults for stock app.
 * Options for a sample of stocks being held for some number of years.
 *
 * @author Barry Becker
 */
class StockSampleOptions {

    static final int DEFAULT_NUM_STOCKS = 1;
    static final int DEFAULT_NUM_TIME_PERIODS = 100;
    static final double DEFAULT_PERCENT_INCREASE = 0.6;
    static final double DEFAULT_PERCENT_DECREASE = 0.4;
    static final double DEFAULT_STARTING_VALUE = 1000;
    static final int DEFAULT_X_RESOLUTION = 2;
    static final boolean DEFAULT_USE_LOG_SCALE = true;
    static final boolean DEFAULT_USE_RANDOM_CHANGE = false;

    int numStocks = DEFAULT_NUM_STOCKS;
    int numTimePeriods = DEFAULT_NUM_TIME_PERIODS;
    double percentIncrease = DEFAULT_PERCENT_INCREASE;
    double percentDecrease = DEFAULT_PERCENT_DECREASE;
    double startingValue = DEFAULT_STARTING_VALUE;
    int xResolution = DEFAULT_X_RESOLUTION;
    boolean useLogScale = DEFAULT_USE_LOG_SCALE;
    boolean useRandomChange = DEFAULT_USE_RANDOM_CHANGE;

    /**
     * This theoretical maximum is something that could occur but rarely
     * ever would by chance. One use of it is used to set an upper limit on the axis.
     * @return upper limit on sample value.
     */
    double getTheoreticalMaximum() {
        return startingValue * Math.pow(1.0 + percentIncrease, numTimePeriods);
    }
}
