// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.placement.method

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point

import scala.collection.mutable.ArrayBuffer
import scala.util.Random


/**
  * Everything we need to know to compute the Voronoi diagram.
  *
  * @author Barry Becker
  */
class RandomPlacement(val width: Int, val height: Int, margin: Int,
                      var numPoints: Int, rnd: Random) extends PlacementMethod {

  private val randomPoints = ArrayBuffer.empty[Point]
  rnd.setSeed(0)

  def getSamples: IndexedSeq[Point] = randomPoints.toIndexedSeq

  /** @param numPoints number of points to add on this increment */
  def increment(numPoints: Int): Unit = synchronized {
    var count: Int = 0
    val w = width - 2 * margin
    val h = height - 2 * margin
    while (count < numPoints) {
      randomPoints += new Point(margin + w * rnd.nextDouble(), margin + h * rnd.nextDouble())
      count += 1
    }
  }

}
