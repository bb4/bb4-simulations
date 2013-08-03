/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.predprey.creatures;

import java.awt.*;

/**
 * Everything we need to know about the current rabbit population.
 * Rabbits are the prey in the simulation.
 *
 * @author Barry Becker
 */
public class Rabbits extends Population {

    public static final Color COLOR = Color.BLUE;

    public static final int INITIAL_NUM_RABBITS = 100;
    private static final double RABBIT_BIRTH_RATE = 1.2;
    private static final double RABBIT_DEATH_RATE = 0.003;

    @Override
    public String getName() {
        return "Rabbit";
    }

    @Override
    public int getInitialPopulation() {
        return INITIAL_NUM_RABBITS;
    }
    @Override
    public double getInitialBirthRate() {
        return RABBIT_BIRTH_RATE;
    }
    @Override
    public double getInitialDeathRate() {
        return RABBIT_DEATH_RATE;
    }

    @Override
    public double getMaxDeathRate() {
        return 0.1;
    }
}
