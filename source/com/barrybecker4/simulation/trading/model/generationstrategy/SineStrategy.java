/*
 * Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/license.MIT
 */

package com.barrybecker4.simulation.trading.model.generationstrategy;

import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * @author Barry Becker
 */
public class SineStrategy extends AbstractGenerationStrategy {

    // radians
    private static final double DEFAULT_INCREMENT = 0.01;
    private static final double DEFAULT_AMPLITUDE = 100;

    /** Amount to increase after each time period if heads   */
    private NumberInput amplitudeField;
    private NumberInput incrementField;

    private double theta = 0.1;
    private double increment = DEFAULT_INCREMENT;
    private double amplitude = DEFAULT_AMPLITUDE;



    public String getName() {
        return "Sine wave";
    }

    public String getDescription() {
        return "Sine Wave strategy";
    }

    @Override
    public double calcNewPrice(double stockPrice) {
        double change = amplitude * Math.sin(theta);
        theta += increment;
        return Math.max(0, stockPrice + change);
    }


    /** The UI to allow the user to configure the options */
    public JPanel getOptionsUI() {
        JPanel strategyPanel = new JPanel();
        strategyPanel.setLayout(new BoxLayout(strategyPanel, BoxLayout.Y_AXIS));
        strategyPanel.setBorder(BorderFactory.createEtchedBorder());

        amplitudeField =
                new NumberInput("Amplitude of the sin wave : ",
                        amplitude,
                        "amp",
                        0.0, 500.0, false);

        incrementField =
                new NumberInput("Increment for each step (in radians) : ",
                        increment,
                        "x increment",
                        0.0, 500.0, false);


        amplitudeField.setAlignmentX(Component.CENTER_ALIGNMENT);
        incrementField.setAlignmentX(Component.CENTER_ALIGNMENT);

        strategyPanel.add(amplitudeField);
        strategyPanel.add(incrementField);

        return strategyPanel;
    }

    /** Call when OK button is pressed to persist selections */
    public void acceptSelectedOptions() {

        this.amplitude = amplitudeField.getValue();
        this.increment = incrementField.getValue();
    }
}
