// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.habitat

import com.barrybecker4.math.MathUtil

package object creatures {

  val TWO_PI: Double =  2.0 * Math.PI


  def absMod(value: Double): Double = (value + 1.0) % 1.0

  def randomDirection() = TWO_PI * MathUtil.RANDOM.nextDouble()

  def perturbDirection(direction: Double): Double = {
    val perturbation = 0.1 * TWO_PI * MathUtil.RANDOM.nextDouble()
    val perturbedDirection = direction + perturbation
    if (perturbedDirection < 0) perturbedDirection + TWO_PI
    else if (perturbedDirection > TWO_PI) perturbedDirection - TWO_PI
    else perturbedDirection
  }
}
