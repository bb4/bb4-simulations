/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.model.tradingstrategy;

/**
 * This naive strategy puts everything in the market at the start and sells it all at the end.
 *
 * @author Barry Becker
 */
public abstract class AbstractTradingStrategy implements ITradingStrategy {

    private double startingTotal;

    protected double reserve;
    protected double invested;
    protected double sharesOwned;
    protected double priceAtLastTransaction;


    @Override
    public MarketPosition initialInvestment(double stockPrice, double startingTotal, double startingInvestmentPercent) {

        this.startingTotal = startingTotal;
        invested = startingInvestmentPercent * startingTotal;
        sharesOwned = invested / stockPrice;
        reserve = startingTotal - invested;
        priceAtLastTransaction = stockPrice;
        return new MarketPosition(invested, reserve, sharesOwned);
    }

    /**
     * do nothing
     */
    @Override
    public abstract MarketPosition updateInvestment(double stockPrice);

    @Override
    public MarketPosition finalizeInvestment(double stockPrice) {

        double finalSell = sharesOwned * stockPrice;
        reserve += finalSell;
        sharesOwned = 0;
        invested = 0;
        return new MarketPosition(invested, reserve, sharesOwned);
    }


    public double getGain() {
        return reserve - startingTotal;
    }

}
