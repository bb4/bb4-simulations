// Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model.kernal

import com.barrybecker4.simulation.cave.model.Cave
import BasicKernel.TOTAL_WEIGHT

/**
  * Looks only at immediate neighbors
  * @author Barry Becker
  */
object BasicKernel {
  private val TOTAL_WEIGHT = 8
}

class BasicKernel(cave: Cave) extends AbstractKernel(cave) {

  override def countNeighbors(x: Int, y: Int): Double = {
    var count: Double = 0
    for (i <- -1 to 1) {
      val neighborX = x + i
      for (j <- -1 to 1) {
        val neighborY = y + j
        // skip the middle point
        if (i != 0 || j != 0) {
          // In case the index we're looking at is off the edge of the map, or a filled neighbor
          if (isOffEdge(neighborX, neighborY)) count += 1.0
          else count += cave.getValue(neighborX, neighborY)
        }
      }
    }
    count / TOTAL_WEIGHT
  }
}
