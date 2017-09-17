/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.predprey1.creatures;

import java.awt.*;

/**
 * Everything we need to know about the current fox population.
 * Foxes are the predators in the simulation.
 *
 * @author Barry Becker
 */
public class Foxes extends Population {

    public static final Color COLOR = Color.RED;

    public static final int INITIAL_NUM_FOXES = 100;
    private static final double FOX_BIRTH_RATE = 1.2;
    private static final double FOX_DEATH_RATE = 10;

    @Override
    public String getName() {
        return "Fox";
    }

    @Override
    public int getInitialPopulation() {
        return INITIAL_NUM_FOXES;
    }
    @Override
    public double getInitialBirthRate() {
        return FOX_BIRTH_RATE;
    }
    @Override
    public double getInitialDeathRate() {
        return FOX_DEATH_RATE;
    }

}
