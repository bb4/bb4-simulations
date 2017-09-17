// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.data


/**
  * Snake geometry data
  * it is defined by the width of the transverse cross-sectional edges (of which there are num segments+1)
  * the magnitude of each segment is the same as its longer width
  * @author Barry Becker
  */
object TestSnakeData {
  private val NUM_SEGMENTS = 22
  private val SEGMENT_LENGTH = 26
  /** The widths starting at the nose and edging at the tip of the tail  */
  private val WIDTHS = Array(10.0, 17.0, 12.0, 14.0, 16.0, 18.0, 19.1, 20.2, 20.8, 21.0, 21.0,
    21.0, 21.0, 20.0, 19.0, 18.0, 17.0, 16.0, 14.0, 12.0, 10.0, 8.0, 6.0)
}

final class TestSnakeData extends SnakeData {
  override def getNumSegments: Int = TestSnakeData.NUM_SEGMENTS
  override def getSegmentLength: Double = TestSnakeData.SEGMENT_LENGTH
  override def getWidths: Array[Double] = TestSnakeData.WIDTHS
}
