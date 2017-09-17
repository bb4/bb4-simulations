// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.data

/**
  * Basic Snake geometry data
  * @author Barry Becker
  */
object BasicSnakeData {
  /** The widths starting at the nose and edging at the tip of the tail  */
  private val WIDTHS = Array(9.0, 18.0, 12.0, 13.0, 15.0, 17.0, 18.0, 19.0, 20.0,
    20.5, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 20.0,
    19.5, 19.0, 18.0, 17.0, 16.0, 15.0, 14.0, 13.0, 12.0, 10.0, 8.0, 6.0, 4.0)
}

final class BasicSnakeData extends SnakeData {
  override def getNumSegments = 34
  override def getSegmentLength = 20
  override def getWidths: Array[Double] = BasicSnakeData.WIDTHS
}
