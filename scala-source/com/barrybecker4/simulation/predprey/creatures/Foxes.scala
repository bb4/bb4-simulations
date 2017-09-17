// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.predprey.creatures

import java.awt._


/**
  * Everything we need to know about the current fox population.
  * Foxes are the predators in the simulation.
  * @author Barry Becker
  */
object Foxes {
  val COLOR: Color = Color.RED
  val INITIAL_NUM_FOXES = 100
  private val FOX_BIRTH_RATE = 1.2
  private val FOX_DEATH_RATE = 10
}

class Foxes extends Population {
  override def getName = "Fox"
  override def getInitialPopulation: Int = Foxes.INITIAL_NUM_FOXES
  override def getInitialBirthRate: Double = Foxes.FOX_BIRTH_RATE
  override def getInitialDeathRate: Double = Foxes.FOX_DEATH_RATE
}
