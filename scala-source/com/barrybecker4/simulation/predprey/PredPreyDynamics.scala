// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.predprey

/**
  * Pure predator–prey update step (same model as the historic applet).
  *
  * Rabbit count appears in the denominator of the fox starvation term; when it is zero we use 1 instead
  * of dividing by zero (foxes still decline via the model’s structure).
  */
object PredPreyDynamics:

  /** @return raw fox population after one step (before clamping to non-negative integers). */
  def nextFoxCount(foxCount: Int, rabbitCount: Int, birthRate: Double, deathRate: Double): Double =
    val safePrey = math.max(rabbitCount, 1)
    foxCount * birthRate - foxCount * deathRate / safePrey

  /** @return raw rabbit population after one step (before clamping). */
  def nextRabbitCount(rabbitCount: Int, foxCount: Int, birthRate: Double, deathRate: Double): Double =
    rabbitCount * birthRate - foxCount * deathRate * rabbitCount
