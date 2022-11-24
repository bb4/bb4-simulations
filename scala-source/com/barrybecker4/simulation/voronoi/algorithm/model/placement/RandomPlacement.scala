// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.placement

import com.barrybecker4.simulation.voronoi.algorithm.PoissonParams
import com.barrybecker4.ui.renderers.OfflineGraphics
import com.barrybecker4.ui.util.ColorMap

import javax.vecmath.Point2d
import scala.util.Random


/**
  * Everything we need to know to compute the Voronoi diagram.
  *
  * @author Barry Becker
  */
class RandomPlacement(val width: Int, val height: Int,
                           var numPoints: Int, rnd: Random) extends PlacementMethod {

  private var randomPoints: IndexedSeq[Point2d] = _
  rnd.setSeed(0)
  randomPoints = IndexedSeq()

  def getSamples: IndexedSeq[Point2d] = randomPoints

  /** @param numPoints number of points to add on this increment */
  def increment(numPoints: Int): Unit = synchronized {
    var count: Int = 0
    while (count < numPoints) {
      randomPoints :+= new Point2d(width * rnd.nextDouble(), height * rnd.nextDouble())
      count += 1
    }
  }

}
