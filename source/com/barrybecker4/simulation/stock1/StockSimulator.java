/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.stock1;

import com.barrybecker4.common.app.AppContext;
import com.barrybecker4.common.format.CurrencyFormatter;
import com.barrybecker4.common.math.function.InvertibleFunction;
import com.barrybecker4.common.math.function.LinearFunction;
import com.barrybecker4.common.math.function.LogFunction;
import com.barrybecker4.simulation.common.ui.DistributionSimulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.ui.renderers.HistogramRenderer;
import com.barrybecker4.ui.util.Log;

import java.util.Arrays;

/**
 * Simulates the N stocks over M time periods (and other options).
 * Graphs the resulting distribution of values for the sample.
 *
 * @author Barry Becker
 */
public class StockSimulator extends DistributionSimulator {

    /**
     * Sometime the numbers on the x axis can get very large. Scientific notation is used in those cases.
     * If this is large, there will be fewer labels shown.
     */
    private static final int LABEL_WIDTH = 70;

    private StockSampleOptions opts = new StockSampleOptions();


    public StockSimulator() {
        super("Stock Market Simulation");
        AppContext.initialize("ENGLISH", Arrays.asList("com.barrybecker4.ui.message"), new Log());
        initHistogram();
    }

    public void setSampleOptions(StockSampleOptions stockSampleOptions) {
        opts = stockSampleOptions;
        initHistogram();
    }

    @Override
    protected void initHistogram() {

        double max = opts.getTheoreticalMaximum();
        double xScale = Math.pow(10, Math.max(0, Math.log10(max) - opts.xResolution));
        double xLogScale = 3 * opts.xResolution * opts.xResolution;

        InvertibleFunction xFunction =
                opts.useLogScale ? new LogFunction(xLogScale, 10.0, true) : new LinearFunction(1/xScale);

        int maxX = (int)xFunction.getValue(max);
        data = new int[maxX + 1];

        histogram = new HistogramRenderer(data, xFunction);
        histogram.setXFormatter(new CurrencyFormatter());
        histogram.setMaxLabelWidth(LABEL_WIDTH);
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
        return new StockOptionsDialog(frame, this );
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
        for (int j = 0; j < opts.numStocks; j++) {
            total += calculateFinalStockPrice();
        }
        return total / opts.numStocks;
    }

    /**
     * @return final stock price for a single stock after numTimePeriods.
     */
    private double calculateFinalStockPrice() {

        double stockPrice = opts.startingValue;
        for (int i = 0; i < opts.numTimePeriods; i++) {
            double percentChange =
                    Math.random() > 0.5 ? opts.percentIncrease : -opts.percentDecrease;
            if (opts.useRandomChange)
                stockPrice *= (1.0 + Math.random() * percentChange);
            else
                stockPrice *= (1.0 + percentChange);
        }
        return stockPrice;
    }

    public static void main( String[] args )
    {
        final StockSimulator sim = new StockSimulator();
        runSimulation(sim);
    }
}