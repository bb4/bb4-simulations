/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.options;

/**
 * Stock trading strategy options and default values.
 *
 * @author Barry Becker
 */
public class TradingOptions {

    private static final float DEFAULT_STARTING_TOTAL = 100000;
    private static final float DEFAULT_STARTING_INVESTMENT_PERCENT = 10;
    private static final ChangePolicy DEFAULT_GAIN_POLICY = new ChangePolicy(2, 5);
    private static final ChangePolicy DEFAULT_LOSS_POLICY = new ChangePolicy(2, 5);

    public double startingTotal = DEFAULT_STARTING_TOTAL;
    public double startingInvestmentPercent = DEFAULT_STARTING_INVESTMENT_PERCENT;
    public ChangePolicy gainStrategy = DEFAULT_GAIN_POLICY;
    public ChangePolicy lossStrategy = DEFAULT_LOSS_POLICY;

}
