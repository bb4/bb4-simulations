// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.predprey.creatures

import java.awt._


/**
  * Everything we need to know about the current rabbit population.
  * Rabbits are the prey in the simulation.
  * @author Barry Becker
  */
object Rabbits {
  val COLOR: Color = Color.BLUE
  val INITIAL_NUM_RABBITS = 100
  private val RABBIT_BIRTH_RATE = 1.2
  private val RABBIT_DEATH_RATE = 0.003
}

class Rabbits extends Population {
  override def getName = "Rabbit"
  override def getInitialPopulation: Int = Rabbits.INITIAL_NUM_RABBITS
  override def getInitialBirthRate: Double = Rabbits.RABBIT_BIRTH_RATE
  override def getInitialDeathRate: Double = Rabbits.RABBIT_DEATH_RATE
  override def getMaxDeathRate = 0.1
}
