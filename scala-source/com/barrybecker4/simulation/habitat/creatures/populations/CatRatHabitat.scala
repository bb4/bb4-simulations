// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures.populations

import com.barrybecker4.simulation.habitat.creatures.CreatureType
import com.barrybecker4.simulation.habitat.creatures.populations.{Population, Habitat}

/**
  * Collection of creature populations that would appear on the Serengeti.
  */
class CatRatHabitat extends Habitat {
  override def addPopulations(): Unit = {
    this.append(Population.createPopulation(CreatureType.RAT, 5, rnd))
    this.append(Population.createPopulation(CreatureType.CAT, 2, rnd))
    this.append(Population.createPopulation(CreatureType.GRASS, 2, rnd))
  }

  def getName: String = "Cats and Rats"
}
