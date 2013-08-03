// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures;

/**
 * Create populations for all our creatures.
 *
 * @author Barry Becker
 */
public class SerengetiPopulations extends Populations {

    @Override
    public void addPopulations() {

        this.add(Population.createPopulation(CreatureType.GRASS, 40));
        this.add(Population.createPopulation(CreatureType.WILDEBEEST, 10));
        this.add(Population.createPopulation(CreatureType.RAT, 15));
        this.add(Population.createPopulation(CreatureType.CAT, 9));
        this.add(Population.createPopulation(CreatureType.LION, 4));
    }
}
