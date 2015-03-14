/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.options.ui;

import com.barrybecker4.simulation.trading.options.StockGenerationOptions;
import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;

/**
 * @author Barry Becker
 */
public class StockGenerationOptionsPanel extends JPanel {

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


    /** if true changes are between 0 and percent change. */
    private JCheckBox useRandomChange_;

    private StockGenerationOptions generationOptions_;


    /**
     * constructor
     */
    public StockGenerationOptionsPanel() {

        generationOptions_ = new StockGenerationOptions();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

         numStocksField_ =
                new NumberInput("Number of stocks in each sample (1 - 1000): ", generationOptions_.numStocks,
                        "The number of stocks in each trial. The average value of which will be one data point.",
                        1, 1000, true);
        numTimePeriodsField_ =
                new NumberInput("Number of time periods (1 - 1000): ", generationOptions_.numTimePeriods,
                        "Number of time periods (for example months or years).",
                        1, 1000, true);
        percentIncreaseField_ =
                new NumberInput("% to increase each time period if heads (0 - 100): ",
                        100* generationOptions_.percentIncrease,
                        "Amount to increase after each time period if coin toss is heads.",
                        0, 100, true);
        percentDecreaseField_ =
                new NumberInput("% to decrease each time period if tails (0 - 100): ",
                        100* generationOptions_.percentDecrease,
                        "Amount to decrease after each time period if coin toss is tails.",
                        0, 100, true);
        startingValueField_ =
                new NumberInput("Starting stock value : ", generationOptions_.startingValue,
                        "Starting value of each stock in the sample (in dollars). For simplicity, they are all the same.",
                        1, 1000000, false);

        useRandomChange_ = new JCheckBox("Use random change", generationOptions_.useRandomChange);
        useRandomChange_.setToolTipText("If checked, " +
                "then the amount of change at each time step will be a " +
                "random amount between 0 and the percent increase or decrease.");

        add(numStocksField_);
        add(numTimePeriodsField_);
        add(percentIncreaseField_);
        add(percentDecreaseField_);
        add(startingValueField_);
        add(useRandomChange_);
        setBorder(Section.createBorder("Stock Generation Options"));

    }

    StockGenerationOptions getOptions() {

        generationOptions_.numStocks = numStocksField_.getIntValue();
        generationOptions_.percentDecrease = (double) percentDecreaseField_.getIntValue() / 100.0;
        generationOptions_.percentIncrease = (double) percentIncreaseField_.getIntValue() / 100.0;
        generationOptions_.numTimePeriods = numTimePeriodsField_.getIntValue();
        generationOptions_.startingValue = startingValueField_.getValue();
        generationOptions_.useRandomChange = useRandomChange_.isSelected();

        return generationOptions_;
    }

}