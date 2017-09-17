// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.data


/**
  * Snake geometry data
  * it is defined by the width of the transverse cross-sectional edges (of which there are num segments+1)
  * the magnitude of each segment is the same as its longer width
  *
  * @author Barry Becker
  */
object StrangeSnakeData {
  /** The widths starting at the nose and edging at the tip of the tail  */
  private val WIDTHS = Array(9.0, 20.0, 10.0, 11.0, 12.0, 14.0, 24.0, 20.0, 23.0,
    26.0, 28.0, 30.0, 29.0, 27.0, 36.0, 24.0, 22.0, 21.0, 20.0, 21.0, 42.0, 24.0,
    26.0, 27.0, 26.0, 25.0, 23.0, 31.0, 19.0, 17.0, 25.0, 13.0, 11.0, 9.0, 6.0, 2.0)
}

final class StrangeSnakeData extends SnakeData {
  override def getNumSegments = 35
  override def getSegmentLength = 22
  override def getWidths: Array[Double] = StrangeSnakeData.WIDTHS
}
