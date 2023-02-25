// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures.populations

import com.barrybecker4.simulation.habitat.creatures.CreatureType
import com.barrybecker4.simulation.habitat.creatures.populations.{Population, Habitat}

/**
  * Collection of creature populations that would appear on the Serengeti.
  *
  * @author Barry Becker
  */
class SerengetiHabitat extends Habitat {
  override def addPopulations(): Unit = {
    this.append(Population.createPopulation(CreatureType.GRASS, 60))
    this.append(Population.createPopulation(CreatureType.COW, 10))
    this.append(Population.createPopulation(CreatureType.RAT, 18))
    this.append(Population.createPopulation(CreatureType.CAT, 9))
    this.append(Population.createPopulation(CreatureType.LION, 4))
  }

  def getName: String = "Serengeti"
}
