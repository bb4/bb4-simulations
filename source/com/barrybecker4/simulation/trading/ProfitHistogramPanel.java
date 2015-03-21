/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading;

import com.barrybecker4.common.format.CurrencyFormatter;
import com.barrybecker4.common.math.function.InvertibleFunction;
import com.barrybecker4.common.math.function.LinearFunction;
import com.barrybecker4.common.math.function.LogFunction;
import com.barrybecker4.simulation.trading.options.GraphingOptions;
import com.barrybecker4.ui.renderers.HistogramRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * Show histogram of expected profit distribution given current strategy.
 *
 * @author Barry Becker
 */
public class ProfitHistogramPanel extends JPanel {

    /**
     * Sometime the numbers on the x axis can get very large. Scientific notation is used in those cases.
     * If this is large, there will be fewer labels shown.
     */
    private static final int LABEL_WIDTH = 70;

    private HistogramRenderer histogram;

    ProfitHistogramPanel() {
    }

    void setOptions(double maxGain, GraphingOptions graphingOpts)  {
        double xScale = Math.pow(10, Math.max(0, Math.log10(maxGain) - graphingOpts.xResolution));
        double xLogScale = 3 * graphingOpts.xResolution * graphingOpts.xResolution;
        int maxX = (int) (maxGain / xScale);

        // go from domain to bin index
        InvertibleFunction xFunction = graphingOpts.useLogScale ?
                new LogFunction(xLogScale, 10.0, false) :
                new LinearFunction(1/(1.5 * xScale), maxX / 4.0);

        int[] data = new int[maxX + 1];

        histogram = new HistogramRenderer(data, xFunction);
        histogram.setXFormatter(new CurrencyFormatter());
        histogram.setMaxLabelWidth(LABEL_WIDTH);
    }

    public void increment(double xpos) {
        histogram.increment(xpos);
    }

    public void paint(Graphics g) {
        histogram.setSize(getWidth(), getHeight());
        histogram.paint(g);
    }
}
