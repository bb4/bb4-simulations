/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.model.tradingstrategy;


/**
 * This naive strategy puts everything in the market at the start and sells it all at the end.
 *
 * @author Barry Becker
 */
public class BuyAndHoldStrategy extends AbstractTradingStrategy {


    public BuyAndHoldStrategy(double startingTotal) {
        super(startingTotal, 1.0);
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
