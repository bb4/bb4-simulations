// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.data

/**
  * Snake that has just eaten several mice.
  * @author Barry Becker
  */
object BumpySnakeData {
  private val BUMP_TROUGH = 14
  private val BUMP_1 = 20
  private val BUMP_2 = 40
  private val BUMP_PEAK = 48
  /** The widths starting at the nose and edging at the tip of the tail  */
  private val WIDTHS = Array(9.0, 22.0, 10.0, 13.0, 17.0, 22.0, 30.0,
    BUMP_2, BUMP_PEAK, BUMP_2, BUMP_1, BUMP_TROUGH, BUMP_1, BUMP_2, BUMP_PEAK, BUMP_2, BUMP_1,
    BUMP_TROUGH, BUMP_1, BUMP_2, BUMP_PEAK, BUMP_2, BUMP_1, BUMP_TROUGH, BUMP_1, BUMP_2, BUMP_PEAK,
    BUMP_2, BUMP_1, BUMP_TROUGH, BUMP_1, BUMP_2, BUMP_PEAK,
    36.0, 31.0, 26.0, 22.0, 18.0, 14.0, 10.0, 6.0, 2.0)
}

final class BumpySnakeData extends SnakeData {
  override def getNumSegments = 42
  override def getSegmentLength = 22
  override def getWidths: Array[Double] = BumpySnakeData.WIDTHS
}
