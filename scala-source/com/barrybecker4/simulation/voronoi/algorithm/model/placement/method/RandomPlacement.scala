// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.placement.method

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point

import scala.collection.mutable.ArrayBuffer
import scala.util.Random


/**
  * Places sample points uniformly at random within the margin-inset rectangle.
  */
class RandomPlacement(val width: Int, val height: Int, margin: Int,
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
        bounds.margin + bounds.innerWidth * rnd.nextDouble(),
        bounds.margin + bounds.innerHeight * rnd.nextDouble()
      )
      count += 1
    }
  }

}
