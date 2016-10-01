/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.options;

/**
 * Defaults for graphing options.
 * There are 3 graphs: stock market, investment, and profit histogram.
 *
 * @author Barry Becker
 */
public class GraphingOptions {

    private static final int DEFAULT_X_RESOLUTION = 2;
    private static final boolean DEFAULT_USE_LOG_SCALE = false;

    public int xResolution = DEFAULT_X_RESOLUTION;
    public boolean useLogScale = DEFAULT_USE_LOG_SCALE;

}
