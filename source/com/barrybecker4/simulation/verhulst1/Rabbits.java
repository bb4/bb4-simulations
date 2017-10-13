// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.verhulst1;

import java.awt.*;

/**
 * Everything we need to know about the current rabbit population.
 * Rabbits are the prey in the simulation.
 *
 * @author Barry Becker
 */
public class Rabbits extends Population {

    public static final Color COLOR = Color.BLUE;

    public static final double INITIAL_NUM_RABBITS = 0.1;
    private static final double RABBIT_BIRTH_RATE = 2.0;
    private static final double MAX_BIRTH_RATE = 3.0;

    @Override
    public String getName() {
        return "Rabbit";
    }

    @Override
    public double getInitialPopulation() {
        return INITIAL_NUM_RABBITS;
    }
    @Override
    public double getInitialBirthRate() {
        return RABBIT_BIRTH_RATE;
    }

    @Override
    public double getMaxBirthRate() {
        return MAX_BIRTH_RATE;
    }
}
