package com.barrybecker4.simulation.trading.model.tradingstrategy;

import com.barrybecker4.simulation.trading.options.ChangePolicy;

/**
 * This naive strategy puts everything in the market at the start and sells it all at the end.
 *
 * @author Barry Becker
 */
public class BuyAndHoldStrategy implements ITradingStrategy {

    public double startingTotal;
    public double startingInvestmentPercent = 1.0;

    double reserve;
    double invested;
    double sharesOwned;
    double priceAtLastTransaction;


    public BuyAndHoldStrategy(double startingTotal) {
        this.startingTotal = startingTotal;
    }


    @Override
    public MarketPosition initialInvestment(double stockPrice) {

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
    public MarketPosition updateInvestment(double stockPrice) {

        return new MarketPosition(invested, reserve, sharesOwned);
    }

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
