/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.predprey1.creatures;

import java.awt.*;

/**
 * Everything we need to know about the current lion population.
 * Lions eat foxes or rabbits.
 *
 * @author Barry Becker
 */
public class Lions extends Population {

    public static final Color COLOR = Color.ORANGE;

    public static final int INITIAL_NUM_LIONS = 0;
    private static final double LION_BIRTH_RATE = 0.1;
    private static final double LION_DEATH_RATE = 10;

    @Override
    public String getName() {
        return "Lion";
    }

    @Override
    public int getInitialPopulation() {
        return INITIAL_NUM_LIONS;
    }
    @Override
    public double getInitialBirthRate() {
        return LION_BIRTH_RATE;
    }
    @Override
    public double getInitialDeathRate() {
        return LION_DEATH_RATE;
    }

    @Override
    public double getMaxDeathRate() {
        return 60;
    }

}
