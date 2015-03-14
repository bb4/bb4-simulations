/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading;

import com.barrybecker4.common.app.AppContext;
import com.barrybecker4.common.format.CurrencyFormatter;
import com.barrybecker4.common.math.function.InvertibleFunction;
import com.barrybecker4.common.math.function.LinearFunction;
import com.barrybecker4.common.math.function.LogFunction;
import com.barrybecker4.simulation.common.ui.DistributionSimulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.trading.options.GraphingOptions;
import com.barrybecker4.simulation.trading.options.ui.OptionsDialog;
import com.barrybecker4.simulation.trading.options.StockGenerationOptions;
import com.barrybecker4.ui.renderers.HistogramRenderer;
import com.barrybecker4.ui.util.Log;

import java.util.Arrays;

/**
 * Simulates applying a specific trading strategy to a simulated stock market time series.
 * There are strategies for both trading policies and time series generation representing the market.
 * Graphs the resulting distribution of expected profits given the assumptions of the model.
 *
 * @author Barry Becker
 */
public class TradingSimulator extends DistributionSimulator {

    /**
     * Sometime the numbers on the x axis can get very large. Scientific notation is used in those cases.
     * If this is large, there will be fewer labels shown.
     */
    private static final int LABEL_WIDTH = 70;

    private StockGenerationOptions generationOpts_ = new StockGenerationOptions();
    private GraphingOptions graphingOpts_ = new GraphingOptions();


    public TradingSimulator() {
        super("Stock Market Simulation");
        AppContext.initialize("ENGLISH", Arrays.asList("com.barrybecker4.ui.message"), new Log());
        initHistogram();
    }

    public void setSampleOptions(StockGenerationOptions stockSampleOptions) {
        generationOpts_ = stockSampleOptions;
        initHistogram();
    }

    public void setGraphingOptions(GraphingOptions graphingOptions) {
        graphingOpts_ = graphingOptions;
        initHistogram();
    }

    @Override
    protected void initHistogram() {

        double max = generationOpts_.getTheoreticalMaximum();
        double xScale = Math.pow(10, Math.max(0, Math.log10(max) - graphingOpts_.xResolution));
        double xLogScale = 3 * graphingOpts_.xResolution * graphingOpts_.xResolution;

        InvertibleFunction xFunction =
                graphingOpts_.useLogScale ? new LogFunction(xLogScale, 10.0, true) : new LinearFunction(1/xScale);

        int maxX = (int)xFunction.getValue(max);
        data_ = new int[maxX + 1];

        histogram_ = new HistogramRenderer(data_, xFunction);
        histogram_.setXFormatter(new CurrencyFormatter());
        histogram_.setMaxLabelWidth(LABEL_WIDTH);
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
        return new OptionsDialog( frame_, this );
    }

    @Override
    protected double getXPositionToIncrement() {
        return createSample();
    }

    /**
     * @return value of a set of numStocks after numTimePeriods.
     */
    private double createSample() {

        double total = 0;
        for (int j = 0; j < generationOpts_.numStocks; j++) {
            total += calculateFinalStockPrice();
        }
        return total / generationOpts_.numStocks;
    }

    /**
     * @return final stock price for a single stock after numTimePeriods.
     */
    private double calculateFinalStockPrice() {

        double stockPrice = generationOpts_.startingValue;
        for (int i = 0; i < generationOpts_.numTimePeriods; i++) {
            double percentChange =
                    Math.random() > 0.5 ? generationOpts_.percentIncrease : -generationOpts_.percentDecrease;
            if (generationOpts_.useRandomChange)
                stockPrice *= (1.0 + Math.random() * percentChange);
            else
                stockPrice *= (1.0 + percentChange);
        }
        return stockPrice;
    }

    public static void main( String[] args ) {
        final TradingSimulator sim = new TradingSimulator();
        runSimulation(sim);
    }
}