package com.barrybecker4.simulation.trading;

import com.barrybecker4.common.math.function.HeightFunction;
import com.barrybecker4.simulation.trading.options.ChangePolicy;
import com.barrybecker4.simulation.trading.options.StockGenerationOptions;
import com.barrybecker4.simulation.trading.options.TradingOptions;

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
    private double finalGain;


    StockRunResult(HeightFunction stockSeries, double finalGain) {
        this.stockSeries = stockSeries;
        this.finalGain = finalGain;
    }


    public HeightFunction getStockSeries() {
        return stockSeries;
    }

    public double getFinalValuation() {
        return stockSeries.getValue(1.0);
    }

    public double getFinalGain() {
        return finalGain;
    }


}
