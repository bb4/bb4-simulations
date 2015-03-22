/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.model;

import com.barrybecker4.common.math.function.HeightFunction;
import com.barrybecker4.simulation.trading.model.tradingstrategy.ITradingStrategy;
import com.barrybecker4.simulation.trading.model.tradingstrategy.MarketPosition;
import com.barrybecker4.simulation.trading.options.ChangePolicy;
import com.barrybecker4.simulation.trading.options.StockGenerationOptions;
import com.barrybecker4.simulation.trading.options.TradingOptions;

/**
 * Given a time series representing a stock's (or collection of stocks) price, calculate the expected gain (or loss)
 * by applying some trading strategy.
 *
 * @author Barry Becker
 */
public class StockRunner {

    private TradingOptions tradingOpts;

    public StockRunner(TradingOptions tradingOpts) {
        this.tradingOpts = tradingOpts;
    }


    /**
     * @return everything about the run including the time series for the stock, the amounts invested in the stock and
     *   in the reserve account, and the amount of gain (or loss if negative) achieved by applying a certain
     *   trading strategy to a generated time series simulating a changing stock price over time.
     */
    public StockRunResult doRun(StockGenerationOptions generationOpts) {

        ITradingStrategy strategy = tradingOpts.tradingStrategy;
        double stockPrice = generationOpts.startingValue;
        int numPeriods = generationOpts.numTimePeriods;

        // initial buy
        MarketPosition position =
                strategy.initialInvestment(stockPrice, tradingOpts.startingTotal, tradingOpts.startingInvestmentPercent);

        double[] yValues = new double[numPeriods + 1];
        double[] investValues = new double[numPeriods + 1];
        double[] reserveValues = new double[numPeriods + 1];


        for (int i = 0; i < numPeriods; i++) {
            yValues[i] = stockPrice;
            investValues[i] = position.getInvested();
            reserveValues[i] = position.getReserve();

            stockPrice = calcNewPrice(generationOpts, stockPrice);

            position = strategy.updateInvestment(stockPrice);
        }

        position = strategy.finalizeInvestment(stockPrice);

        yValues[numPeriods] = stockPrice;
        investValues[numPeriods] = 0;
        reserveValues[numPeriods] = position.getReserve();

        //System.out.println("*** final sell = " + finalSell
        //        + " reserve = " + reserve + " totalGain = " + totalGain + " ending stock price = " + stockPrice);

        return new StockRunResult(
                new HeightFunction(yValues),
                new HeightFunction(investValues),
                new HeightFunction(reserveValues),
                strategy.getGain());
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
