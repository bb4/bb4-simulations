// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.placement.method

import com.barrybecker4.simulation.voronoi.algorithm.model.placement.method.GaussianPlacement.SIZE
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point
import com.barrybecker4.ui.renderers.OfflineGraphics
import com.barrybecker4.ui.util.ColorMap

import scala.util.Random


object GaussianPlacement {
  private val SIZE: Double = 3.0
}

/**
  * Everything we need to know to compute the Voronoi diagram.
  *
  * @author Barry Becker
  */
class GaussianPlacement(val width: Int, val height: Int, val margin: Int,
                       var numPoints: Int, rnd: Random) extends PlacementMethod {

  private var randomPoints: IndexedSeq[Point] = _
  rnd.setSeed(0)
  randomPoints = IndexedSeq()

  def getSamples: IndexedSeq[Point] = randomPoints

  /** @param numPoints number of points to add on this increment */
  def increment(numPoints: Int): Unit = synchronized {
    var count: Int = 0
    val w = width - 2 * margin
    val h = height - 2 * margin
    while (count < numPoints) {
      randomPoints :+= new Point(w * getGaussianInBounds + margin, h * getGaussianInBounds + margin)
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