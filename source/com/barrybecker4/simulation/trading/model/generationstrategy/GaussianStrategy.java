/*
 * Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/license.MIT
 */

package com.barrybecker4.simulation.trading.model.generationstrategy;

import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
public class GaussianStrategy extends AbstractGenerationStrategy {

    private static final double DEFAULT_PERCENT_INCREASE = 0.04;
    private static final double DEFAULT_PERCENT_DECREASE = 0.03;
    private static final boolean DEFAULT_USE_RANDOM_CHANGE = true;

    public double percentIncrease = DEFAULT_PERCENT_INCREASE;
    public double percentDecrease = DEFAULT_PERCENT_DECREASE;
    public boolean useRandomChange = DEFAULT_USE_RANDOM_CHANGE;


      /** Amount to increase after each time period if heads   */
    private NumberInput percentIncreaseField;

    /** Amount to decrease after each time period if tails  */
    private NumberInput percentDecreaseField;

    /** if true changes are between 0 and percent change. */
    private JCheckBox useRandomChangeCB;


    @Override
    public double calcNewPrice(double stockPrice) {
        double percentChange =
                Math.random() > 0.5 ? percentIncrease : -percentDecrease;
        if (useRandomChange)
            stockPrice *= (1.0 + Math.random() * percentChange);
        else
            stockPrice *= (1.0 + percentChange);
        return stockPrice;
    }




    /** The UI to allow the user to configure the options */
    public JPanel getOptionsUI() {
        JPanel strategyPanel = new JPanel();
        strategyPanel.setLayout(new BoxLayout(strategyPanel, BoxLayout.Y_AXIS));
        strategyPanel.setBorder(BorderFactory.createEtchedBorder());

        percentIncreaseField =
                new NumberInput("% to increase each time period if heads (0 - 100): ",
                        100 * percentIncrease,
                        "Amount to increase after each time period if coin toss is heads.",
                        0, 100, false);
        percentDecreaseField =
                new NumberInput("% to decrease each time period if tails (0 - 100): ",
                        100 * percentDecrease,
                        "Amount to decrease after each time period if coin toss is tails.",
                        -100, 100, false);


        useRandomChangeCB = new JCheckBox("Use random change", useRandomChange);
        useRandomChangeCB.setToolTipText("If checked, " +
                "then the amount of change at each time step will be a " +
                "random amount between 0 and the percent increase or decrease.");

        percentIncreaseField.setAlignmentX(Component.CENTER_ALIGNMENT);
        percentDecreaseField.setAlignmentX(Component.CENTER_ALIGNMENT);
        useRandomChangeCB.setAlignmentX(Component.CENTER_ALIGNMENT);

        strategyPanel.add(percentIncreaseField);
        strategyPanel.add(percentDecreaseField);
        strategyPanel.add(useRandomChangeCB);
        return strategyPanel;
    }

    /** Call when OK button is pressed to persist selections */
    public void acceptSelectedOptions() {

        this.percentDecrease = percentDecreaseField.getValue() / 100.0;
        this.percentIncrease = percentIncreaseField.getValue() / 100.0;
        this.useRandomChange = useRandomChangeCB.isSelected();
    }
}
