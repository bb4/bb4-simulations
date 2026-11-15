// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.data

/**
  * Snake geometry preset.
  *
  * @param numSegments   number of body segments (including head)
  * @param segmentLength length of each segment along the spine
  * @param widths        transverse cross-section widths; must have length `numSegments + 1`.
  *                      Segment `i` uses front width `widths(i)` and rear width `widths(i + 1)`.
  */
case class SnakeData(numSegments: Int, segmentLength: Int, widths: Array[Double]) {
  require(
    widths.length == numSegments + 1,
    s"widths.length=${widths.length}, expected numSegments+1=${numSegments + 1}"
  )
}