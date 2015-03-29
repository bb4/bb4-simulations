/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.options;

import com.barrybecker4.simulation.trading.model.generationstrategy.GenerationStrategyEnum;
import com.barrybecker4.simulation.trading.model.generationstrategy.IGenerationStrategy;

/**
 * Defaults for stock time series generation.
 * A single stock or a collection of stocks can be simulated.
 *
 * @author Barry Becker
 */
public class StockGenerationOptions {

    public static final GenerationStrategyEnum DEFAULT_GENERATION_STRATEGY = GenerationStrategyEnum.RANDOM_UPS_AND_DOWNS;

    private static final int DEFAULT_NUM_STOCKS = 1;
    private static final int DEFAULT_NUM_TIME_PERIODS = 100;
    private static final double DEFAULT_STARTING_VALUE = 100;

    public int numStocks = DEFAULT_NUM_STOCKS;
    public int numTimePeriods = DEFAULT_NUM_TIME_PERIODS;
    public double startingValue = DEFAULT_STARTING_VALUE;

    public IGenerationStrategy generationStrategy = DEFAULT_GENERATION_STRATEGY.getStrategy();
}
