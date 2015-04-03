/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.model.tradingstrategy;


/**
 * This naive strategy puts everything in the market at the start and sells it all at the end.
 *
 * @author Barry Becker
 */
public class BuyAndHoldStrategy extends AbstractTradingStrategy {


    public String getName() {
        return "buy and hold";
    }

    public String getDescription() {
        return "No transactions are made after the initial investment";
    }

    /**
     * do nothing
     */
    @Override
    public MarketPosition updateInvestment(double stockPrice) {

        invested = sharesOwned * stockPrice;
        return new MarketPosition(invested, reserve, sharesOwned);
    }

}
