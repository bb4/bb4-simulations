/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading1.model.tradingstrategy;

import com.barrybecker4.simulation.trading1.model.plugin.IStrategyPlugin;

import javax.swing.*;

/**
 * Defines how a trading strategy and how it is applied.
 *
 * @author Barry Becker
 */
public interface ITradingStrategy extends IStrategyPlugin {

    /**
     * How the investor initially gets started with their strategy.
     * @param stockPrice the initial stock price of the stop or index
     * @param startingTotal the total amount of money available to invest
     * @param startingInvestmentPercent the percent of that total to put in the marke initially.
     */
    MarketPosition initialInvestment(double stockPrice, double startingTotal, double startingInvestmentPercent);

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

    /** The UI to allow the user to configure the options */
    JPanel getOptionsUI();

    /** Call when OK button is pressed to persist selections */
    void acceptSelectedOptions();
}
