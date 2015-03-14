/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading;

import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
public class TradingOptionsDialog extends SimulatorOptionsDialog {

    /** number of dice to use.  */
    private NumberInput numStocksField_;

    /** Number of time periods (for example months or years)  */
    private NumberInput numTimePeriodsField_;

    /** Amount to increase after each time period if heads   */
    private NumberInput percentIncreaseField_;

    /** Amount to decrease after each time period if tails  */
    private NumberInput percentDecreaseField_;

    /** Starting value of each stock in dollars  */
    private NumberInput startingValueField_;

    /** Granularity fo the histogram bins on the x axis.  */
    private NumberInput xResolutionField_;

    /** if true the x axis will have a log scale */
    private JCheckBox useLogScale_;

    /** if true changes are between 0 and percent change. */
    private JCheckBox useRandomChange_;

    private TradingSampleOptions options_;

    /**
     * constructor
     */
    public TradingOptionsDialog(Component parent, Simulator simulator)
    {
        super( parent, simulator );
        options_ = new TradingSampleOptions();
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

        innerPanel.add( numStocksField_);
        innerPanel.add( numTimePeriodsField_);
        innerPanel.add( percentIncreaseField_);
        innerPanel.add( percentDecreaseField_);
        innerPanel.add( startingValueField_);
        innerPanel.add( xResolutionField_);
        paramPanel.add(innerPanel, BorderLayout.NORTH);
        paramPanel.add(booleanOptions, BorderLayout.CENTER);

        return paramPanel;
    }

    private void initializeFields() {
        numStocksField_ =
                new NumberInput("Number of stocks in each sample (1 - 1000): ", TradingSampleOptions.DEFAULT_NUM_STOCKS,
                        "The number of stocks in each trial. The average value of which will be one data point.",
                        1, 1000, true);
        numTimePeriodsField_ =
                new NumberInput("Number of time periods (1 - 1000): ", TradingSampleOptions.DEFAULT_NUM_TIME_PERIODS,
                        "Number of time periods (for example months or years).",
                        1, 1000, true);
        percentIncreaseField_ =
                new NumberInput("Amount to increse each time period if heads (0 - 100): ",
                        100* TradingSampleOptions.DEFAULT_PERCENT_INCREASE,
                        "Amount to increase after each time period if coin toss is heads.",
                        0, 100, true);
        percentDecreaseField_ =
                new NumberInput("Amount to decrese each time period if tails (0 - 100): ",
                        100* TradingSampleOptions.DEFAULT_PERCENT_DECREASE,
                        "Amount to decrease after each time period if coin toss is tails.",
                        0, 100, true);
        startingValueField_ =
                new NumberInput("Starting stock value : ", TradingSampleOptions.DEFAULT_STARTING_VALUE,
                        "Starting value of each stock in the sample (in dollars). For simplicity, they are all the same.",
                        1, 1000000, false);
        xResolutionField_ =
                new NumberInput("Resolution (1 - 5): ", TradingSampleOptions.DEFAULT_X_RESOLUTION,
                        "1 is low resolution 5 is high (meaning more bins on the x axis).",
                        1, 5, true);
    }

    private JPanel createBooleanOptions() {
        JPanel booleanOptionsPanel = new JPanel();
        booleanOptionsPanel.setLayout(new BoxLayout(booleanOptionsPanel, BoxLayout.Y_AXIS));

        useLogScale_ = new JCheckBox("Use log scale on x axis",
                TradingSampleOptions.DEFAULT_USE_LOG_SCALE);
        useLogScale_.setToolTipText("If checked, " +
                "the x axis will be shown on a log scale so that the histogram will be easier to interpret.");

        useRandomChange_ = new JCheckBox("Use random change",
                TradingSampleOptions.DEFAULT_USE_RANDOM_CHANGE);
        useRandomChange_.setToolTipText("If checked, " +
                "then the amount of change at each time step will be a " +
                "random amount between 0 and the percent increase or decrease.");

        booleanOptionsPanel.add(useLogScale_);
        booleanOptionsPanel.add(useRandomChange_);

        return booleanOptionsPanel;
    }

    @Override
    protected void ok()
    {
        super.ok();

        options_.numStocks = numStocksField_.getIntValue();
        options_.percentDecrease = (double) percentDecreaseField_.getIntValue() / 100.0;
        options_.percentIncrease = (double) percentIncreaseField_.getIntValue() / 100.0;
        options_.numTimePeriods = numTimePeriodsField_.getIntValue();
        options_.startingValue = startingValueField_.getValue();
        options_.xResolution = xResolutionField_.getIntValue();
        options_.useLogScale = useLogScale_.isSelected();
        options_.useRandomChange = useRandomChange_.isSelected();

        TradingSimulator simulator = (TradingSimulator) getSimulator();
        simulator.setSampleOptions(options_);
    }

}