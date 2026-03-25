// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model.kernel

import com.barrybecker4.simulation.cave.model.Cave

/**
  * Looks only at all neighbors a distance of 2 away and weighs them by 1/distance
  *
  * 1/4   1/rt3  1/2  1/rt3  1/4
  * 1/rt3 1/rt2   1   1/rt2  1/rt3
  * 1/2    1      0    1     1/2
  * 1/rt3  1/rt2  1    etc...
  *
  * @param size kernel size. Must be odd, like 3, 5, 7, 9, etc
  * @author Barry Becker
  */
class RadialKernel(cave: Cave, val size: Int = 5) extends AbstractKernel(cave) {

  assert(size > 2 && size % 2 == 1)

  private val (distanceLookup, totalWeight) = {
    val lookup = Array.ofDim[Double](size, size)
    var sum = 0.0
    val range = size / 2
    for (i <- 0 to range) {
      for (j <- 0 to range) {
        val value = math.sqrt(i * i + j * j)
        lookup(i)(j) = value
        val multiplier = if (i == 0 || j == 0) 2 else 4
        sum += multiplier * value
      }
    }
    (lookup, sum)
  }

  override def countNeighbors(x: Int, y: Int): Double = {
    var count = 0.0
    val range = size / 2
    for (i <- -range to range) {
      val neighborX = x + i
      for (j <- -range to range) {
        val neighborY = y + j
        val distance = distanceLookup(math.abs(i))(math.abs(j))
        if (i != 0 || j != 0) {
          if (isOffEdge(neighborX, neighborY)) count += distance
          else count += distance * cave.getValue(neighborX, neighborY)
        }
      }
    }
    count / totalWeight
  }
}
