// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model.kernal

import com.barrybecker4.simulation.cave.model.Cave


/**
  * Looks only at all neighbors a distance of 2 away and weighs them by 1/distance
  *
  * 1/4   1/rt3  1/2  1/rt3  1/4
  * 1/rt3 1/rt2   1   1/rt2  1/rt3
  * 1/2    1      0    1     1/2
  * 1/rt3  1/rt2  1    etc...
  *
  * @param size kernal size. Must be odd, like 3, 5, 7, 9, etc
  * @author Barry Becker
  */
class RadialKernel(cave: Cave, val size: Int = 5) extends AbstractKernel(cave) {

  assert(size > 2 && size % 2 == 1)
  private var distanceLookup = Array.ofDim[Double](size, size)
  private var totalWeight = .0
  initDistanceLookup()

  private def initDistanceLookup() = {
    var sum: Double = 0
    val range = size / 2
    for (i <- 0 to range) {
      for (j <- 0 to range) {
        val value = Math.sqrt(i * i + j * j)
        distanceLookup(i)(j) = value
        val multiplier = if (i == 0 || j == 0) 2 else 4
        sum += multiplier * value
      }
    }
    totalWeight = sum
  }

  override def countNeighbors(x: Int, y: Int): Double = {
    var count: Double = 0
    val range = size / 2
    for (i <- -range to range) {
      val neighborX = x + i
      for (j <- -range to range) {
        val neighborY = y + j
        val distance = distanceLookup(Math.abs(i))(Math.abs(j))
        // If we're looking at the middle point
        if (i != 0 || j != 0) {
          if (isOffEdge(neighborX, neighborY)) count += distance
          else count += distance * cave.getValue(neighborX, neighborY)
        }
      }
    }
    count / totalWeight
  }
}
