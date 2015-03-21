package com.barrybecker4.simulation.trading;

import com.barrybecker4.common.format.CurrencyFormatter;
import com.barrybecker4.common.math.function.Function;
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * @author Barry Becker
 */
public class StockChartPanel extends JPanel {

    /**
     * Sometime the numbers on the x axis can get very large. Scientific notation is used in those cases.
     * If this is large, there will be fewer labels shown.
     */
    private static final int LABEL_WIDTH = 70;

    MultipleFunctionRenderer stockChart;

    StockChartPanel() {
        List<Function> funcs = Collections.<Function>emptyList();
        stockChart = new MultipleFunctionRenderer(funcs);
        stockChart.setXFormatter(new CurrencyFormatter());
        stockChart.setMaxLabelWidth(LABEL_WIDTH);
    }

    public void setSeries(List<Function> functions) {
        stockChart.setFunctions(functions);
    }

    public void paint(Graphics g) {
        stockChart.setSize(getWidth(), getHeight());
        stockChart.paint(g);
    }
}
