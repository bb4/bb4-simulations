// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.predprey.creatures

/**
  * Everything we need to know about a population of creatures.
  * @author Barry Becker
  */
object Population {
  private val MAX_POPULATION = 100000
}

abstract class Population() {

  var birthRate = .0
  var deathRate = .0
  private var population: Int = 0
  reset()

  def getName: String
  def getPopulation: Int = population

  def reset(): Unit = {
    population = getInitialPopulation
    birthRate = getInitialBirthRate
    deathRate = getInitialDeathRate
  }

  def setPopulation(value: Double): Unit = {
    population = Math.max(0, value.round).toInt
    if (population > Population.MAX_POPULATION)
      population = Math.round(0.8 * population).toInt
  }

  def getInitialPopulation: Int
  def getInitialBirthRate: Double
  def getInitialDeathRate: Double
  def getMaxBirthRate = 3.0
  def getMaxDeathRate = 30.0
}
