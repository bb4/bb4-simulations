// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.verhulst

import java.awt._


/**
  * Everything we need to know about the current rabbit population.
  * Rabbits are the prey in the simulation.
  *
  * @author Barry Becker
  */
object Rabbits {
  val COLOR: Color = Color.BLUE
  val INITIAL_NUM_RABBITS = 0.1
  private val RABBIT_BIRTH_RATE = 2.0
  private val MAX_BIRTH_RATE = 3.0
}

class Rabbits extends Population {
  override def getName = "Rabbit"
  override def getInitialPopulation: Double = Rabbits.INITIAL_NUM_RABBITS
  override def getInitialBirthRate: Double = Rabbits.RABBIT_BIRTH_RATE
  override def getMaxBirthRate: Double = Rabbits.MAX_BIRTH_RATE
}
