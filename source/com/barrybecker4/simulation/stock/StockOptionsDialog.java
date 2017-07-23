/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.stock;

import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
public class StockOptionsDialog extends SimulatorOptionsDialog {

    /** number of dice to use.  */
    private NumberInput numStocksField;

    /** Number of time periods (for example months or years)  */
    private NumberInput numTimePeriodsField;

    /** Amount to increase after each time period if heads   */
    private NumberInput percentIncreaseField;

    /** Amount to decrease after each time period if tails  */
    private NumberInput percentDecreaseField;

    /** Starting value of each stock in dollars  */
    private NumberInput startingValueField;

    /** Granularity fo the histogram bins on the x axis.  */
    private NumberInput xResolutionField;

    /** if true the x axis will have a log scale */
    private JCheckBox useLogScale;

    /** if true changes are between 0 and percent change. */
    private JCheckBox useRandomChange;

    private StockSampleOptions options;

    /**
     * constructor
     */
    StockOptionsDialog(Component parent, Simulator simulator )
    {
        super( parent, simulator );
        options = new StockSampleOptions();
    }

    @Override
    public String getTitle()
    {
        return "Stock Simulation Configuration";
    }

    @Override
    protected JPanel createCustomParamPanel()
    {
        JPanel paramPanel = new JPanel();
        paramPanel.setLayout(new BorderLayout());
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout( new BoxLayout(innerPanel, BoxLayout.Y_AXIS));

        initializeFields();

        JPanel booleanOptions = createBooleanOptions();

        innerPanel.add(numStocksField);
        innerPanel.add(numTimePeriodsField);
        innerPanel.add(percentIncreaseField);
        innerPanel.add(percentDecreaseField);
        innerPanel.add(startingValueField);
        innerPanel.add(xResolutionField);
        paramPanel.add(innerPanel, BorderLayout.NORTH);
        paramPanel.add(booleanOptions, BorderLayout.CENTER);

        return paramPanel;
    }

    private void initializeFields() {
        numStocksField =
                new NumberInput("Number of stocks in each sample (1 - 1000): ", StockSampleOptions.DEFAULT_NUM_STOCKS,
                        "The number of stocks in each trial. The average value of which will be one data point.",
                        1, 1000, true);
        numTimePeriodsField =
                new NumberInput("Number of time periods (1 - 1000): ", StockSampleOptions.DEFAULT_NUM_TIME_PERIODS,
                        "Number of time periods (for example months or years).",
                        1, 1000, true);
        percentIncreaseField =
                new NumberInput("Amount to increase each time period if heads (0 - 100): ",
                        100* StockSampleOptions.DEFAULT_PERCENT_INCREASE,
                        "Amount to increase after each time period if coin toss is heads.",
                        0, 100, true);
        percentDecreaseField =
                new NumberInput("Amount to decrease each time period if tails (0 - 100): ",
                        100* StockSampleOptions.DEFAULT_PERCENT_DECREASE,
                        "Amount to decrease after each time period if coin toss is tails.",
                        0, 100, true);
        startingValueField =
                new NumberInput("Starting stock value : ", StockSampleOptions.DEFAULT_STARTING_VALUE,
                        "Starting value of each stock in the sample (in dollars). For simplicity, they are all the same.",
                        1, 1000000, false);
        xResolutionField =
                new NumberInput("Resolution (1 - 5): ", StockSampleOptions.DEFAULT_X_RESOLUTION,
                        "1 is low resolution 5 is high (meaning more bins on the x axis).",
                        1, 5, true);
    }

    private JPanel createBooleanOptions() {
        JPanel booleanOptionsPanel = new JPanel();
        booleanOptionsPanel.setLayout(new BoxLayout(booleanOptionsPanel, BoxLayout.Y_AXIS));

        useLogScale = new JCheckBox("Use log scale on x axis",
                StockSampleOptions.DEFAULT_USE_LOG_SCALE);
        useLogScale.setToolTipText("If checked, " +
                "the x axis will be shown on a log scale so that the histogram will be easier to interpret.");

        useRandomChange = new JCheckBox("Use random change",
                StockSampleOptions.DEFAULT_USE_RANDOM_CHANGE);
        useRandomChange.setToolTipText("If checked, " +
                "then the amount of change at each time step will be a " +
                "random amount between 0 and the percent increase or decrease.");

        booleanOptionsPanel.add(useLogScale);
        booleanOptionsPanel.add(useRandomChange);

        return booleanOptionsPanel;
    }

    @Override
    protected void ok() {
        super.ok();

        options.numStocks = numStocksField.getIntValue();
        options.percentDecrease = (double) percentDecreaseField.getIntValue() / 100.0;
        options.percentIncrease = (double) percentIncreaseField.getIntValue() / 100.0;
        options.numTimePeriods = numTimePeriodsField.getIntValue();
        options.startingValue = startingValueField.getValue();
        options.xResolution = xResolutionField.getIntValue();
        options.useLogScale = useLogScale.isSelected();
        options.useRandomChange = useRandomChange.isSelected();

        StockSimulator simulator = (StockSimulator) getSimulator();
        simulator.setSampleOptions(options);
    }

}