/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.charts;

import com.barrybecker4.common.format.CurrencyFormatter;
import com.barrybecker4.common.math.function.Function;
import com.barrybecker4.simulation.trading.model.runner.StockSeries;
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * Shows series of generated stock market simulations represented as height functions.
 * @author Barry Becker
 */
public class StockChartPanel extends JPanel {

    /**
     * Sometime the numbers on the x axis can get very large. Scientific notation is used in those cases.
     * If this is large, there will be fewer labels shown.
     */
    private static final int LABEL_WIDTH = 70;

    private StockSeries stockSeries = new StockSeries(10);

    private MultipleFunctionRenderer stockChart;

    public StockChartPanel() {
        List<Function> functions = Collections.emptyList();
        stockChart = new MultipleFunctionRenderer(functions);
        stockChart.setXFormatter(new CurrencyFormatter());
        stockChart.setMaxLabelWidth(LABEL_WIDTH);
    }

    public void addSeries(Function function) {
        stockSeries.add(function);
        stockChart.setFunctions(stockSeries);
    }

    public void clear(int numRecentSeries) {
        stockSeries.clear(numRecentSeries);
    }

    public void paint(Graphics g) {
        stockChart.setSize(getWidth(), getHeight());
        stockChart.paint(g);
    }
}
