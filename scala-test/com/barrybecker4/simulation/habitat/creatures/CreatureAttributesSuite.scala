// Copyright by Barry G. Becker, 2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.creatures

import org.scalatest.funsuite.AnyFunSuite

import javax.vecmath.Point2d

class CreatureAttributesSuite extends AnyFunSuite {

  private val rat = CreatureType.RAT

  test("adjustAgeAndHunger increments pregnancy when mature and well fed") {
    val a = new CreatureAttributes(
      age = 12,
      hunger = 0,
      numDaysPregnant = 0,
      speed = 0.0,
      direction = 0.0,
      new Point2d(0.5, 0.5),
      hitPoints = 10
    )
    a.adjustAgeAndHunger(rat)
    assert(a.age == 13)
    assert(a.numDaysPregnant == 1)
    assert(a.hunger == 1)
  }

  test("adjustAgeAndHunger resets pregnancy when hunger is too high") {
    // Must exceed PREGNANCY_HUNGER_THRESH (0.8) * starvationThreshold
    val hunger = math.ceil(0.81 * rat.starvationThreshold).toInt
    val a = new CreatureAttributes(
      age = 20,
      hunger = hunger,
      numDaysPregnant = 5,
      speed = 0.0,
      direction = 0.0,
      new Point2d(0.5, 0.5),
      hitPoints = 10
    )
    a.adjustAgeAndHunger(rat)
    assert(a.numDaysPregnant == 0)
  }

  test("computeNewPosition is stationary when speed is zero") {
    val loc = new Point2d(0.25, 0.75)
    val a = new CreatureAttributes(0, 0, 0, 0.0, 0.0, loc, 10)
    val next = a.computeNewPosition()
    val tol = 1e-12
    assert(math.abs(next.x - loc.x) < tol)
    assert(math.abs(next.y - loc.y) < tol)
  }
}
