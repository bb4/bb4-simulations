/*
 * Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/license.MIT
 */

package com.barrybecker4.simulation.trading1.model.generationstrategy;

import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * @author Barry Becker
 */
public class GaussianStrategy extends AbstractGenerationStrategy implements IGenerationStrategy {

    private static final double DEFAULT_MEAN = 0.01;
    private static final double DEFAULT_VARIANCE = 0.1;

    public double mean = DEFAULT_MEAN;
    public double variance = DEFAULT_VARIANCE;


    /** Amount to increase after each time period if heads   */
    private NumberInput meanField;

    /** Amount to decrease after each time period if tails  */
    private NumberInput varianceField;

    /** random number generator */
    private Random rand = new Random();


    public String getName() {
        return "gaussian";
    }

    public String getDescription() {
        return "Each time period the market goes up or down by a guassian " +
                "distributed random value whose mean and variance are specified";
    }

    @Override
    public double calcNewPrice(double stockPrice) {
        double change = variance * rand.nextGaussian() + mean;
        return stockPrice * Math.max(0, 1.0 + change);
    }


    /** The UI to allow the user to configure the options */
    public JPanel getOptionsUI() {
        JPanel strategyPanel = new JPanel();
        strategyPanel.setLayout(new BoxLayout(strategyPanel, BoxLayout.Y_AXIS));
        strategyPanel.setBorder(BorderFactory.createEtchedBorder());

        meanField =
                new NumberInput("Mean of the gaussian random variable : ",
                        mean,
                        "Mean of the gaussian values generated",
                        -1.0, 1.0, false);
        varianceField =
                new NumberInput("Variance of the gaussian random variable : ",
                        variance,
                        "Variance of the gaussian values generated",
                        -100, 100, false);

        meanField.setAlignmentX(Component.CENTER_ALIGNMENT);
        varianceField.setAlignmentX(Component.CENTER_ALIGNMENT);

        strategyPanel.add(meanField);
        strategyPanel.add(varianceField);
        return strategyPanel;
    }

    /** Call when OK button is pressed to persist selections */
    public void acceptSelectedOptions() {

        this.mean = meanField.getValue();
        this.variance = varianceField.getValue();
    }
}
