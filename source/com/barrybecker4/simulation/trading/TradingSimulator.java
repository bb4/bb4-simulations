/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading;

import com.barrybecker4.common.app.AppContext;
import com.barrybecker4.common.math.function.Function;
import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.trading.model.StockRunResult;
import com.barrybecker4.simulation.trading.model.StockRunner;
import com.barrybecker4.simulation.trading.options.GraphingOptions;
import com.barrybecker4.simulation.trading.options.TradingOptions;
import com.barrybecker4.simulation.trading.options.ui.OptionsDialog;
import com.barrybecker4.simulation.trading.options.StockGenerationOptions;
import com.barrybecker4.ui.animation.AnimationFrame;
import com.barrybecker4.ui.util.Log;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;


/**
 * Simulates applying a specific trading strategy to a simulated stock market time series.
 * There are strategies for both trading policies and time series generation representing the market.
 * Graphs the resulting distribution of expected profits given the assumptions of the model.
 *
 * @author Barry Becker
 */
public class TradingSimulator extends Simulator {

    private static final double TIME_STEP = 1.0;
    private static final int DEFAULT_STEPS_PER_FRAME = 100;

    private JSplitPane splitPane;

    private StockChartPanel stockChartPanel;
    private InvestmentChartPanel investmentPanel;
    private ProfitHistogramPanel profitPanel;


    private StockGenerationOptions generationOpts = new StockGenerationOptions();
    private TradingOptions tradingOpts = new TradingOptions();
    private GraphingOptions graphingOpts = new GraphingOptions();


    public TradingSimulator() {
        super("Stock Market Simulation");
        AppContext.initialize("ENGLISH", Arrays.asList("com.barrybecker4.ui.message"), new Log());
        initUI();
    }

    public void setOptions(
            StockGenerationOptions stockSampleOpts, TradingOptions tradingOpts, GraphingOptions graphingOpts) {
        generationOpts = stockSampleOpts;
        this.tradingOpts = tradingOpts;
        this.graphingOpts = graphingOpts;
        update();
        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);
    }

    @Override
    protected void reset() {
        update();
    }

    private void initUI() {

        stockChartPanel = new StockChartPanel();
        investmentPanel = new InvestmentChartPanel();
        profitPanel = new ProfitHistogramPanel();

        JSplitPane chartSplit =
                new JSplitPane(JSplitPane.VERTICAL_SPLIT, stockChartPanel, investmentPanel);
        splitPane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chartSplit, profitPanel);

        //Provide minimum sizes for the two components in the split pane
        Dimension minimumSize = new Dimension(100, 50);
        stockChartPanel.setMinimumSize(minimumSize);
        investmentPanel.setMinimumSize(minimumSize);
        profitPanel.setMinimumSize(minimumSize);

        chartSplit.setDividerLocation(350);
        splitPane.setDividerLocation(600);

        this.add(splitPane);
        update();
    }

    private void update() {
        stockChartPanel.clear();
        investmentPanel.clear();
        profitPanel.setOptions(tradingOpts.theoreticalMaxGain, graphingOpts);
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
        return new OptionsDialog( frame_, this );
    }

    @Override
    protected double getInitialTimeStep() {
        return TIME_STEP;
    }

    @Override
    public double timeStep() {
        if ( !isPaused() ) {
            profitPanel.increment(getXPositionToIncrement());
        }
        return timeStep_;
    }


    @Override
    public void paint( Graphics g ) {
        splitPane.setSize(getSize());
        splitPane.paint(g);
    }


    protected double getXPositionToIncrement() {
        return createSample();
    }

    /**
     * @return value gain achieved by applying a trading strategy to a set of numStocks after numTimePeriods.
     */
    private double createSample() {

        StockRunner runner = new StockRunner(tradingOpts);

        double total = 0;
        for (int j = 0; j < generationOpts.numStocks; j++) {
            StockRunResult result = runner.doRun(generationOpts);
            stockChartPanel.addSeries(result.getStockSeries());
            investmentPanel.addSeries(result.getInvestmentSeries(), result.getReserveSeries());
            total += result.getFinalGain();
        }
        return total / generationOpts.numStocks;
    }


    public static void main( String[] args ) {
        final TradingSimulator simulator = new TradingSimulator();
        simulator.setPaused(false);
        new AnimationFrame( simulator );
    }

}