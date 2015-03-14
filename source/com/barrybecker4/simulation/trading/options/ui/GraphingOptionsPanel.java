/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.options.ui;

import com.barrybecker4.simulation.trading.options.GraphingOptions;
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

    private GraphingOptions graphOptions;

    /**
     * constructor
     */
    public GraphingOptionsPanel() {

        graphOptions = new GraphingOptions();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        xResolutionField =
                new NumberInput("Resolution (1 - 5): ", graphOptions.xResolution,
                        "1 is low resolution 5 is high (meaning more bins on the x axis).",
                        1, 5, true);

        useLogScale = new JCheckBox("Use log scale on x axis",
                graphOptions.useLogScale);
        useLogScale.setToolTipText("If checked, " +
                "the x axis will be shown on a log scale so that the histogram will be easier to interpret.");

        add(xResolutionField);
        add(useLogScale);
        setBorder(BorderFactory.createTitledBorder("Graphing Options"));
    }


    GraphingOptions getOptions() {

        graphOptions.xResolution = xResolutionField.getIntValue();
        graphOptions.useLogScale = useLogScale.isSelected();

        return graphOptions;
    }

}