// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.data

/**
  * Snake data is defined by the width of the transverse cross-sectional edges (of which there are num segments+1).
  * The magnitude of each segment is the same as its longer width.
  * @author Barry Becker
  */
case class SnakeData(numSegments: Int, segmentLength: Int, widths: Array[Double])
