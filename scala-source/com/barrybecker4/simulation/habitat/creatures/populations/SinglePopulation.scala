// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT 
package com.barrybecker4.simulation.habitat.creatures.populations

import com.barrybecker4.simulation.habitat.creatures.CreatureType



/**
  * Create populations for one creature.
  * Shows example of Verhulst dynamics.
  *
  * @author Barry Becker
  */
class SinglePopulation extends Populations {

  override def addPopulations(): Unit = {
    this.append(Population.createPopulation(CreatureType.CAT, 9))
  }

  def getName: String = "Just Cats"
}
