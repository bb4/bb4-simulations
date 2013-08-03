// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.verhulst;

/**
 * Everything we need to know about a population of creatures.
 *
 * @author Barry Becker
 */
public abstract class Population {

    public double birthRate;
    private double population;

    public Population() {
        reset();
    }

    public abstract String getName();

    public void reset() {
        population = getInitialPopulation();
        birthRate = getInitialBirthRate();
    }

    public double getPopulation() {
        return population;
    }

    public void setPopulation(double value) {

        population = value;
    }

    public abstract double getInitialPopulation();
    public abstract double getInitialBirthRate();

    public double getMaxBirthRate() {
        return 3.0;
    }
}
