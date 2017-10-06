/*
 * Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/license.MIT
 */
package com.barrybecker4.simulation.trading1.model.generationstrategy;


import com.barrybecker4.simulation.trading1.model.plugin.IStrategyPlugin;

import javax.swing.*;

/**
 * Defines how a stock series should be generated.
 * There are various strategies that we might employ varying from simple to sophisticated.
 *
 * @author Barry Becker
 */
public interface IGenerationStrategy extends IStrategyPlugin {


    double calcNewPrice(double stockPrice);

    /** The UI to allow the user to configure the generation options */
    JPanel getOptionsUI();

    /** Call when OK button is pressed to persist selections */
    void acceptSelectedOptions();
}
