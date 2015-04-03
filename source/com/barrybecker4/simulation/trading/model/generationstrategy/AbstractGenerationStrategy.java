/*
 * Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/license.MIT
 */
package com.barrybecker4.simulation.trading.model.generationstrategy;

import javax.swing.*;

/**
 * This naive strategy puts everything in the market at the start and sells it all at the end.
 *
 * @author Barry Becker
 */
public abstract class AbstractGenerationStrategy implements IGenerationStrategy {


    /** The UI to allow the user to configure the options */
    public JPanel getOptionsUI() {
        return new JPanel();
    }

    /** Call when OK button is pressed to persist selections */
    public void acceptSelectedOptions() {}
}
