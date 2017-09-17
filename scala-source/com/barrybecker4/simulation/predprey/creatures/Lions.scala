// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.predprey.creatures

import java.awt._


/**
  * Everything we need to know about the current lion population.
  * Lions eat foxes or rabbits.
  * @author Barry Becker
  */
object Lions {
  val COLOR: Color = Color.ORANGE
  val INITIAL_NUM_LIONS = 0
  private val LION_BIRTH_RATE = 0.1
  private val LION_DEATH_RATE = 10
}

class Lions extends Population {
  override def getName = "Lion"
  override def getInitialPopulation: Int = Lions.INITIAL_NUM_LIONS
  override def getInitialBirthRate: Double = Lions.LION_BIRTH_RATE
  override def getInitialDeathRate: Double = Lions.LION_DEATH_RATE
  override def getMaxDeathRate = 60
}
