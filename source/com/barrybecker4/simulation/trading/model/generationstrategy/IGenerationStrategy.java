/*
 * Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/license.MIT
 */
package com.barrybecker4.simulation.trading.model.generationstrategy;


import javax.swing.*;

/**
 * Defines how a stock series should be generated.
 * There are various strategies that we might employ varying from simple to sophisticated.
 *
 * @author Barry Becker
 */
public interface IGenerationStrategy {

    String getName();
    String getDescription();

    double calcNewPrice(double stockPrice);

    /** The UI to allow the user to configure the generation options */
    JPanel getOptionsUI();

    /** Call when OK button is pressed to persist selections */
    void acceptSelectedOptions();
}
