/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading1.model.runner;

import com.barrybecker4.common.math.function.HeightFunction;

/**
 * Contains the result of a stock simulation run.
 * Contains the generated stock function.
 * The final stock valuation.
 * The gain from applying a trading strategy.
 *
 * @author Barry Becker
 */
public class StockRunResult {

    private HeightFunction stockSeries;
    private HeightFunction investmentSeries;
    private HeightFunction reserveSeries;
    private double finalGain;


    /** Constructor */
    StockRunResult(HeightFunction stockSeries, HeightFunction investmentSeries,
                   HeightFunction reserveSeries, double finalGain) {
        this.stockSeries = stockSeries;
        this.investmentSeries = investmentSeries;
        this.reserveSeries = reserveSeries;
        this.finalGain = finalGain;
    }

    public HeightFunction getStockSeries() {
        return stockSeries;
    }

    public HeightFunction getInvestmentSeries() {
        return investmentSeries;
    }

    public HeightFunction getReserveSeries() {
        return reserveSeries;
    }

    public double getFinalValuation() {
        return stockSeries.getValue(1.0);
    }

    public double getFinalGain() {
        return finalGain;
    }
}
