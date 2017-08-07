/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.dice

/**
  * Defaults for disce simulation app.
  *
  * @author Barry Becker
  */
object DiceOptions {
  private[dice] val DEFAULT_NUMBER_OF_DICE = 2
  private[dice] val DEFAULT_NUMBER_OF_SIDES = 6
}

class DiceOptions {
  private[dice] var numDice = DiceOptions.DEFAULT_NUMBER_OF_DICE
  private[dice] var numSides = DiceOptions.DEFAULT_NUMBER_OF_SIDES
}
