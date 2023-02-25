// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures.populations

import com.barrybecker4.simulation.habitat.creatures.CreatureType
import com.barrybecker4.simulation.habitat.creatures.populations.{Population, Populations}

/**
  * Collection of creature populations that would appear on the Serengeti.
  *
  * @author Barry Becker
  */
class CatRatPopulations extends Populations {
  override def addPopulations(): Unit = {
    this.append(Population.createPopulation(CreatureType.RAT, 70))
    this.append(Population.createPopulation(CreatureType.CAT, 15))
    this.append(Population.createPopulation(CreatureType.GRASS, 100))
  }

  def getName: String = "Cats and Rats"
}
