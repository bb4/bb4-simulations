// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.placement.method

import com.barrybecker4.simulation.voronoi.algorithm.model.placement.method.GaussianPlacement.SIZE
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point

import scala.collection.mutable.ArrayBuffer
import scala.util.Random


object GaussianPlacement {
  private val SIZE: Double = 3.0
}

/**
  * Places sample points with a truncated Gaussian distribution around the canvas center.
  */
class GaussianPlacement(val width: Int, val height: Int, val margin: Int,
                       var numPoints: Int, rnd: Random) extends PlacementMethod {

  private val randomPoints = ArrayBuffer.empty[Point]
  private val bounds = PlacementBounds(margin, width, height)
  rnd.setSeed(0)

  def getSamples: IndexedSeq[Point] = randomPoints.toIndexedSeq

  /** @param numPoints number of points to add on this increment */
  def increment(numPoints: Int): Unit = synchronized {
    var count: Int = 0
    while (count < numPoints) {
      randomPoints += new Point(
        bounds.innerWidth * getGaussianInBounds + bounds.margin,
        bounds.innerHeight * getGaussianInBounds + bounds.margin
      )
      count += 1
    }
  }

  private def getGaussianInBounds: Double = {
    var outOfBounds = true
    var r: Double = 0
    while (outOfBounds) {
      r = rnd.nextGaussian() + SIZE
      outOfBounds = r < 0 || r > 2 * SIZE
    }
    0.5 * r / SIZE
  }

}
