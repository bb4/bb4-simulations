/** Copyright by Barry G. Becker, 2000-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.dice

import scala.util.Random

/**
  * Pure dice-roll math used by [[DiceSimulator]] and tests.
  */
object DiceRollLogic {

  /** Number of histogram bins for sums of `numDice` fair dice with `numSides` faces (inclusive range). */
  def histogramBinCount(numDice: Int, numSides: Int): Int = {
    require(numDice >= 0, "numDice must be >= 0")
    require(numSides >= 1, "numSides must be >= 1")
    numDice * (numSides - 1) + 1
  }

  /** Sum of one roll of `numDice` dice, faces `1`..`numSides`, using `random`. */
  def rollSum(numDice: Int, numSides: Int, random: Random): Int = {
    require(numDice >= 0, "numDice must be >= 0")
    require(numSides >= 1, "numSides must be >= 1")
    Iterator.fill(numDice)(random.nextInt(numSides) + 1).sum
  }
}
