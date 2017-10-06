/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading1.options.ui;

import com.barrybecker4.simulation.trading1.options.GraphingOptions;
import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;

/**
 * @author Barry Becker
 */
public class GraphingOptionsPanel extends JPanel {

    /** if true the x axis will have a log scale */
    private JCheckBox useLogScale;

    /** Granularity fo the histogram bins on the x axis.  */
    private NumberInput xResolutionField;
    /** show series history for line charts */
    private NumberInput numRecentSeriesField;

    private GraphingOptions graphOptions;

    /**
     * constructor
     */
    public GraphingOptionsPanel() {

        graphOptions = new GraphingOptions();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        xResolutionField =
                new NumberInput("Histogram Resolution (1 - 5): ", graphOptions.histogramXResolution,
                        "1 is low resolution 5 is high (meaning more bins on the x axis).",
                        1, 5, true);

        useLogScale = new JCheckBox("Use log scale on x axis",
                graphOptions.histogramUseLogScale);
        useLogScale.setToolTipText("If checked, " +
                "the x axis will be shown on a log scale so that the histogram will be easier to interpret.");

        numRecentSeriesField = new NumberInput("Num recent series: ", graphOptions.numRecentSeries,
                "The number of recent time series to show in the stock generation and investment line " +
                        "charts on the left",
                1, 1000, true);
        add(xResolutionField);
        add(useLogScale);
        add(numRecentSeriesField);

        setBorder(Section.createBorder("Graphing Options"));
    }


    GraphingOptions getOptions() {

        graphOptions.histogramXResolution = xResolutionField.getIntValue();
        graphOptions.histogramUseLogScale = useLogScale.isSelected();
        graphOptions.numRecentSeries = numRecentSeriesField.getIntValue();

        return graphOptions;
    }

}