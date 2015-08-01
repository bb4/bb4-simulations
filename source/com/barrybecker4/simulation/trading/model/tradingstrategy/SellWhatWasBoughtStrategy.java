/*
 * Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/license.MIT
 */
package com.barrybecker4.simulation.trading.model.tradingstrategy;

import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * This is the strategy suggested by John Roh.
 * The idea is to sell what was bought when the market increases by some threshold, and buy a fixed amount on dips
 *
 * Example
 *  startingTotal = 100,000
 *  fixedPurchasePercent = 5%
 *  fixedPurchaseAmount = 5000
 *  startingInvestmentPercent = 10%
 *  invested = 10,000 (initially)
 *
 *  If the market goes up, we do nothing - we never sell the initial investment
 *  If the market goes down by lossChangePercent, then buy fixedPurchaseAmount.
 *  If the market again goes down by lossChangePercent more, then buy fixedPurchaseAmount.
 *  Remember these buy transactions
 *  If the market ever increases gainChangePercent higher than one of those purchases, then sell it.
 *  Buy whenever the marked drops by lossChangePercent from last transaction.
 *
 * @author Barry Becker
 */
public class SellWhatWasBoughtStrategy extends AbstractTradingStrategy {

    private double gainThresholdPct = 0.05;
    private double lossThresholdPct = 0.05;

    /** This percent times the startingTotal gives the fixed purchase amount */
    private double fixedPurchasePercent = 0.05;

    /**
     * This is the amount to buy on dips (assuming that much is in reserve).
     * It is computed from fixedPurchasePercent * startingTotal
     */
    private double fixedPurchaseAmount;

    private List<Transaction> transactions;

    private NumberInput fixedPurchasePctField;
    private NumberInput gainThresholdField;
    private NumberInput lossThresholdField;


    public String getName() {
        return "sell what was bought";
    }

    public String getDescription() {
        return "Initial investment is never sold, but a fixed percentage is bought when market drops, " +
             "and that same amount is then sold when market rises some threshold above the price that it was bought at.";
    }

    @Override
    public MarketPosition initialInvestment(double stockPrice, double startingTotal, double startingInvestmentPercent) {
        fixedPurchaseAmount = fixedPurchasePercent * startingTotal;
        transactions = new ArrayList<>(100);
        return super.initialInvestment(stockPrice, startingTotal, startingInvestmentPercent);
    }

    /**
     * if this new price triggers a transaction, then do it
     */
    @Override
    public MarketPosition updateInvestment(double stockPrice) {

        // loop through all the buys and see if any of them are ready to sell.
        Iterator<Transaction> it = transactions.iterator();
        while (it.hasNext()) {
            Transaction trans = it.next();
            if (stockPrice >= (1.0 + gainThresholdPct) * trans.stockPrice) {
                sell(Math.max(trans.numShares, sharesOwned), stockPrice);
                it.remove();
             }
        }

        double pctChange = (stockPrice - priceAtLastTransaction) / priceAtLastTransaction;

        if (-pctChange >= lossThresholdPct) {
            // buy more because its cheaper
            double amountToInvest = Math.min(reserve, fixedPurchaseAmount);
            buy(amountToInvest, stockPrice);
            transactions.add(new Transaction(stockPrice, amountToInvest));
        }
        return new MarketPosition(invested, reserve, sharesOwned);
    }


    /** The UI to allow the user to configure the options */
    public JPanel getOptionsUI() {
        JPanel strategyPanel = new JPanel();
        strategyPanel.setLayout(new BoxLayout(strategyPanel, BoxLayout.Y_AXIS));

        strategyPanel.setBorder(BorderFactory.createEtchedBorder());

        fixedPurchasePctField = new NumberInput("Fixed purchase percent: ", 100 * gainThresholdPct,
                        "This is the fixed amount that is bought and sold at each transaction," +
                                " specified as a percent of the starting amount.",
                        0, 100, false);

        gainThresholdField = new NumberInput("Gain threshold percent: ", 100 * gainThresholdPct,
                        "Triggers a sale if market goes up by this much.",
                        0, 100, false);

        lossThresholdField = new NumberInput("Loss threshold percent: ", 100 * gainThresholdPct,
                        "Triggers a buy if market goes down by this much from last transaction.",
                        0, 100, false);

        fixedPurchasePctField.setAlignmentX(Component.CENTER_ALIGNMENT);
        gainThresholdField.setAlignmentX(Component.CENTER_ALIGNMENT);
        lossThresholdField.setAlignmentX(Component.CENTER_ALIGNMENT);

        strategyPanel.add(fixedPurchasePctField);
        strategyPanel.add(gainThresholdField);
        strategyPanel.add(lossThresholdField);

        return strategyPanel;
    }

    /** Call when OK button is pressed to persist selections */
    public void acceptSelectedOptions() {
        this.gainThresholdPct = gainThresholdField.getValue() / 100.0;
        this.lossThresholdPct = lossThresholdField.getValue() / 100.0;
        this.fixedPurchasePercent = fixedPurchasePctField.getValue() / 100.0;
    }
}
