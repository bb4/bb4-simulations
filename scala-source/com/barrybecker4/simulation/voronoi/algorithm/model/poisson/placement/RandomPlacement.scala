// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.poisson.placement

import com.barrybecker4.simulation.voronoi.algorithm.model.poisson.PoissonParams
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point
import com.barrybecker4.ui.renderers.OfflineGraphics
import com.barrybecker4.ui.util.ColorMap

import scala.util.Random


/**
  * Everything we need to know to compute the Voronoi diagram.
  *
  * @author Barry Becker
  */
class RandomPlacement(val width: Int, val height: Int, margin: Int,
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
      randomPoints :+= new Point(margin + w * rnd.nextDouble(), margin + h * rnd.nextDouble())
      count += 1
    }
  }

}
