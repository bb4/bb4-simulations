/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.predprey.creatures;

/**
 * Everything we need to know about a population of creatures.
 *
 * @author Barry Becker
 */
public abstract class Population {

    private static final long MAX_POPULATION = 100000;

    public double birthRate;
    public double deathRate;
    private int population;


    public Population() {
        reset();
    }

    public abstract String getName();

    public void reset() {
        population = getInitialPopulation();
        birthRate = getInitialBirthRate();
        deathRate = getInitialDeathRate();
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(double value) {

        population = (int) Math.max(0, Math.round(value));
        if (population > MAX_POPULATION)  {
            population *= 0.8;
        }
    }

    public abstract int getInitialPopulation();
    public abstract double getInitialBirthRate();
    public abstract double getInitialDeathRate();

    public double getMaxBirthRate() {
        return 3.0;
    }
    public double getMaxDeathRate() {
        return 30.0;
    }
}
