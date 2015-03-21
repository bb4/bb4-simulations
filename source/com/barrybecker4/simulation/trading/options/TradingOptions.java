/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.options;

/**
 * Stock trading strategy options and default values.
 *
 * @author Barry Becker
 */
public class TradingOptions {

    private static final double DEFAULT_STARTING_TOTAL = 100000;
    private static final double DEFAULT_STARTING_INVESTMENT_PERCENT = 0.1;
    private static final ChangePolicy DEFAULT_GAIN_POLICY = new ChangePolicy(0.02, 0.05);
    private static final ChangePolicy DEFAULT_LOSS_POLICY = new ChangePolicy(0.02, 0.05);
    private static final double DEFAULT_THEORETICAL_MAX_GAIN = 100000;

    public double startingTotal = DEFAULT_STARTING_TOTAL;
    public double startingInvestmentPercent = DEFAULT_STARTING_INVESTMENT_PERCENT;

    public ChangePolicy gainPolicy = DEFAULT_GAIN_POLICY;
    public ChangePolicy lossPolicy = DEFAULT_LOSS_POLICY;

    public double theoreticalMaxGain = DEFAULT_THEORETICAL_MAX_GAIN;

}
