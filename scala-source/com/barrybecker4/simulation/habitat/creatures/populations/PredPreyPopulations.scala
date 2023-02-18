// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT 
package com.barrybecker4.simulation.habitat.creatures.populations

import com.barrybecker4.simulation.habitat.creatures.CreatureType
import com.barrybecker4.simulation.habitat.creatures.populations.{Population, Populations}

/**
  * Collection of creature populations that would appear on the Serengeti.
  *
  * @author Barry Becker
  */
class PredPreyPopulations extends Populations {
  override def addPopulations(): Unit = {
    this.append(Population.createPopulation(CreatureType.RAT, 5))
    this.append(Population.createPopulation(CreatureType.CAT, 2))
  }
}
