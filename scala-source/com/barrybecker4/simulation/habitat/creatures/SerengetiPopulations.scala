// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures


/**
  * Collection of creature populations that would appear on the Serengeti.
  * @author Barry Becker
  */
class SerengetiPopulations extends Populations {
  override def addPopulations(): Unit = {
    this.append(Population.createPopulation(CreatureType.GRASS, 40))
    this.append(Population.createPopulation(CreatureType.WILDEBEEST, 10))
    this.append(Population.createPopulation(CreatureType.RAT, 15))
    this.append(Population.createPopulation(CreatureType.CAT, 9))
    this.append(Population.createPopulation(CreatureType.LION, 4))
  }
}
