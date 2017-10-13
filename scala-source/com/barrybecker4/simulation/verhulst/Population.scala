// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.verhulst

/**
  * Everything we need to know about a population of creatures.
  * @author Barry Becker
  */
abstract class Population() {

  var birthRate = .0
  private var population = .0
  reset()

  def reset(): Unit = {
    population = getInitialPopulation
    birthRate = getInitialBirthRate
  }

  def getName: String
  def getPopulation: Double = population
  def setPopulation(value: Double): Unit = { population = value}
  def getInitialPopulation: Double
  def getInitialBirthRate: Double
  def getMaxBirthRate = 3.0
}
