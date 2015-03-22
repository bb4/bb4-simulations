/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.options;

import com.barrybecker4.simulation.trading.model.tradingstrategy.BuyAndHoldStrategy;
import com.barrybecker4.simulation.trading.model.tradingstrategy.BuyPercentOfInvestmentStrategy;
import com.barrybecker4.simulation.trading.model.tradingstrategy.ITradingStrategy;
import com.barrybecker4.simulation.trading.model.tradingstrategy.BuyPercentOfReserveStrategy;
import com.barrybecker4.simulation.trading.model.tradingstrategy.TradingStrategyEnum;

/**
 * Stock trading strategy options and default values.
 *
 * @author Barry Becker
 */
public class TradingOptions {

    public static final TradingStrategyEnum DEFAULT_TRADING_STRATEGY = TradingStrategyEnum.BUY_AND_HOLD;

    private static final double DEFAULT_STARTING_TOTAL = 100000;
    private static final double DEFAULT_STARTING_INVESTMENT_PERCENT = 0.5;
    private static final double DEFAULT_THEORETICAL_MAX_GAIN = 100000;

    public double startingTotal = DEFAULT_STARTING_TOTAL;
    public double startingInvestmentPercent = DEFAULT_STARTING_INVESTMENT_PERCENT;

    public double theoreticalMaxGain = DEFAULT_THEORETICAL_MAX_GAIN;

    public ITradingStrategy tradingStrategy = DEFAULT_TRADING_STRATEGY.getStrategy();
}
