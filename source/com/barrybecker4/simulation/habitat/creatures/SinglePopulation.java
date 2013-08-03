// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures;

/**
 * Create populations for one creature.
 * Shows example of Verhulst dynamics.
 *
 * @author Barry Becker
 */
public class SinglePopulation extends Populations {

    @Override
    public void addPopulations() {
        this.add(Population.createPopulation(CreatureType.CAT, 9));
    }

}
