/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading1.charts;

import com.barrybecker4.common.format.CurrencyFormatter;
import com.barrybecker4.common.math.function.Function;
import com.barrybecker4.simulation.trading1.model.runner.StockSeries;
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shows how much is invested and how much is held in reserve over time.
 *
 * @author Barry Becker
 */
public class InvestmentChartPanel extends JPanel {

    private static final Color INVESTMENT_COLOR = new Color(200, 10,30, 80);
    private static final Color RESERVE_COLOR = new Color(0, 100, 190, 80);
    private static final Color TEXT_COLOR = new Color(10, 10, 80);

    private static final int LEGEND_X = 140;
    private static final int LEGEND_Y = 10;
    private static final int LEGEND_SWATCH_SIZE = 12;


    private StockSeries series = new StockSeries(20);

    private MultipleFunctionRenderer investmentChart;


    public InvestmentChartPanel() {
        List<Function> functions = Collections.emptyList();
        investmentChart = new MultipleFunctionRenderer(functions);
        investmentChart.setXFormatter(new CurrencyFormatter());
        //investmentChart.setMaxLabelWidth(LABEL_WIDTH);
    }

    public void addSeries(Function investmentFunction, Function reserveFunction) {

        series.add(investmentFunction);
        series.add(reserveFunction);

        // this should change
        int size = series.size();
        List<Color> lineColors = new ArrayList<>(size);

        for (int i=0; i<size; i++) {
            lineColors.add((i % 2 == 0) ? INVESTMENT_COLOR : RESERVE_COLOR);
        }
        investmentChart.setFunctions(series, lineColors);
    }

    public void clear(int numRecentSeries) {
        series.clear(numRecentSeries);
    }

    public void paint(Graphics g) {
        investmentChart.setSize(getWidth(), getHeight());
        investmentChart.paint(g);

        Graphics2D g2 = (Graphics2D)g;
        drawLegend(g2);
    }

    private void drawLegend(Graphics2D g2) {
        g2.setColor(new Color(INVESTMENT_COLOR.getRGB()));
        g2.fillRect(LEGEND_X, LEGEND_Y, LEGEND_SWATCH_SIZE, LEGEND_SWATCH_SIZE);
        g2.fillRect(LEGEND_X, LEGEND_Y, LEGEND_SWATCH_SIZE, LEGEND_SWATCH_SIZE);
        g2.setColor(new Color(RESERVE_COLOR.getRGB()));
        g2.fillRect(LEGEND_X, LEGEND_Y + LEGEND_SWATCH_SIZE + 10, LEGEND_SWATCH_SIZE, LEGEND_SWATCH_SIZE);
        g2.fillRect(LEGEND_X, LEGEND_Y + LEGEND_SWATCH_SIZE + 10, LEGEND_SWATCH_SIZE, LEGEND_SWATCH_SIZE);

        g2.setColor(TEXT_COLOR);
        g2.drawString("Investment amount", LEGEND_X + LEGEND_SWATCH_SIZE + 10, LEGEND_Y + 10);
        g2.drawString("Reserve amount", LEGEND_X + LEGEND_SWATCH_SIZE + 10, LEGEND_Y + LEGEND_SWATCH_SIZE + 20);
    }
}
