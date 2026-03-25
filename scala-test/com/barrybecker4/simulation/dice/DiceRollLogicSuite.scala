/** Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.dice

import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random

class DiceRollLogicSuite extends AnyFunSuite {

  test("histogramBinCount matches distinct sums for standard dice") {
    assert(DiceRollLogic.histogramBinCount(2, 6) === 11)
    assert(DiceRollLogic.histogramBinCount(3, 6) === 16)
    assert(DiceRollLogic.histogramBinCount(0, 6) === 1)
    assert(DiceRollLogic.histogramBinCount(5, 2) === 6)
  }

  test("rollSum stays within min and max inclusive for many seeded rolls") {
    val rnd = new Random(42L)
    val numDice = 4
    val numSides = 8
    val minSum = numDice
    val maxSum = numDice * numSides
    (0 until 5000).foreach { _ =>
      val s = DiceRollLogic.rollSum(numDice, numSides, rnd)
      assert(s >= minSum && s <= maxSum)
    }
  }

  test("rollSum is zero when numDice is zero") {
    val rnd = new Random(0L)
    assert(DiceRollLogic.rollSum(0, 6, rnd) === 0)
  }
}
