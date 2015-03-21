package com.barrybecker4.simulation.trading.model.tradingstrategy;

/**
 * Defines how a trading strategy and how it is applied.
 *
 * @author Barry Becker
 */
public interface ITradingStrategy {

    /**
     * How the investor initially gets started with their strategy.
     */
    MarketPosition initialInvestment(double stockPrice);

    /**
     * The incremental update to the investment on a time change.
     * It's possible that there may be no action.
     */
    MarketPosition updateInvestment(double stockPrice);

    /**
     * Does the final cashing out at the end.
     */
    MarketPosition finalizeInvestment(double stockPrice);

    /**
     * Get current gain (or loss)
     * Typically called after finalizeInvestment.
     */
    double getGain();
}
