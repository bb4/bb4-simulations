// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT 
package com.barrybecker4.simulation.habitat.creatures.populations

import com.barrybecker4.simulation.habitat.creatures.CreatureType



/**
  * Create populations for one creature.
  * Shows example of Verhulst dynamics.
  *
  * @author Barry Becker
  */
class SingleCreatureHabitat extends Habitat {

  override def addPopulations(): Unit = {
    this.append(Population.createPopulation(CreatureType.RAT, 10))
    this.append(Population.createPopulation(CreatureType.GRASS, 10))
  }

  def getName: String = "Just Rats"
}
