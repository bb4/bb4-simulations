package com.barrybecker4.simulation.trading;

import com.barrybecker4.simulation.trading.options.ChangePolicy;
import com.barrybecker4.simulation.trading.options.StockGenerationOptions;
import com.barrybecker4.simulation.trading.options.TradingOptions;

/**
 * Given a time series representing a stock's (or collection of stocks) price, calculate the expected gain (or loss)
 * by applying some trading strategy.
 *
 * @author Barry Becker
 */
public class GainCalculator {

    private TradingOptions tradingOpts;

    GainCalculator(TradingOptions tradingOpts) {
        this.tradingOpts = tradingOpts;
    }


    /**
     * @return amount of gain (or loss if negative) achieved by applying a certain trading strategy to a generated
     *   time series simulating a changing stock price over time.
     */
    public double calculateGain(StockGenerationOptions generationOpts) {

        double stockPrice = generationOpts.startingValue;
        double reserve = tradingOpts.startingTotal;

        ChangePolicy gainPolicy = tradingOpts.gainPolicy;
        ChangePolicy lossPolicy = tradingOpts.lossPolicy;

        // initial buy
        double initialInvestment = tradingOpts.startingInvestmentPercent * reserve;
        reserve -= initialInvestment;
        double sharesOwned = initialInvestment / stockPrice;
        double priceAtLastTransaction = stockPrice;


        for (int i = 0; i < generationOpts.numTimePeriods; i++) {
            stockPrice = calcNewPrice(generationOpts, stockPrice);

            // if this new price triggers a transaction, then do it
            double pctChange = (stockPrice - priceAtLastTransaction) / priceAtLastTransaction;
            if (pctChange >= gainPolicy.getChangePercent()) {
                // sell, and take some profit. Assume we can sell partial shares
                double sharesToSell = gainPolicy.getTransactPercent() * sharesOwned;
                //System.out.println(" - selling $" + (sharesToSell * stockPrice) + " which is " + sharesToSell + " shares @" + stockPrice);
                sharesOwned -= sharesToSell;
                reserve += sharesToSell * stockPrice;
                priceAtLastTransaction = stockPrice;
            }
            else if (-pctChange >= lossPolicy.getChangePercent()) {
                // buy more because its cheaper
                double amountToInvest = lossPolicy.getTransactPercent() * reserve;
                reserve -= amountToInvest;
                double sharesToBuy = amountToInvest / stockPrice;
                //System.out.println(" + buying $" + amountToInvest + " which is " + sharesToBuy + " shares @" + stockPrice);
                sharesOwned += sharesToBuy;
                priceAtLastTransaction = stockPrice;
            }
            //System.out.println("reserve = "+ reserve  +  "   num shares = " + sharesOwned + " @"+ stockPrice);
        }

        // at the end of the last time step we need to sell everything and take the ultimate profit/loss.
        double finalSell = sharesOwned * stockPrice;

        reserve += finalSell;
        double totalGain = reserve - tradingOpts.startingTotal;

        System.out.println("*** final sell = " + finalSell + " reserve = " + reserve + " totalGain = " + totalGain + " ending stock price = " + stockPrice);

        return totalGain;
    }

    /**
     * @return final stock price for a single stock after numTimePeriods.
     */
    public double calculateFinalStockPrice(StockGenerationOptions generationOpts) {

        double stockPrice = generationOpts.startingValue;
        for (int i = 0; i < generationOpts.numTimePeriods; i++) {
            stockPrice = calcNewPrice(generationOpts, stockPrice);
        }
        return stockPrice;
    }


    private double calcNewPrice(StockGenerationOptions generationOpts, double stockPrice) {
        double percentChange =
                Math.random() > 0.5 ? generationOpts.percentIncrease : -generationOpts.percentDecrease;
        if (generationOpts.useRandomChange)
            stockPrice *= (1.0 + Math.random() * percentChange);
        else
            stockPrice *= (1.0 + percentChange);
        return stockPrice;
    }
}
