/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.model.tradingstrategy;

import javax.swing.*;

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


    /**
     * buy some shares
     */
    protected void buy(double amountToInvest, double stockPrice) {
        assert amountToInvest <= reserve;
        reserve -= amountToInvest;
        double sharesToBuy = amountToInvest / stockPrice;
        //System.out.println(" + buying $" + amountToInvest + " which is " + sharesToBuy + " shares @" + stockPrice);
        sharesOwned += sharesToBuy;
        invested = sharesOwned * stockPrice;
        priceAtLastTransaction = stockPrice;
    }

    /**
     * Sell the specified number of shares at the specified price.
     */
    protected void sell(double sharesToSell, double stockPrice) {
        assert sharesToSell >= 0: "you cannot sell " + sharesToSell + " shares";
        assert sharesToSell <= sharesOwned : "You cannot sell more shares ("+sharesToSell+") than you have ("+sharesOwned +")";
        sharesOwned -= sharesToSell;
        reserve += sharesToSell * stockPrice;
        invested = sharesOwned * stockPrice;
        priceAtLastTransaction = stockPrice;
        //System.out.println(" - selling $" + (sharesToSell * stockPrice) + " which is " + sharesToSell + " shares @" + stockPrice);
    }


    /** The UI to allow the user to configure the options */
    public JPanel getOptionsUI() {
        return new JPanel();
    }

    /** Call when OK button is pressed to persist selections */
    public void acceptSelectedOptions() {}

}
