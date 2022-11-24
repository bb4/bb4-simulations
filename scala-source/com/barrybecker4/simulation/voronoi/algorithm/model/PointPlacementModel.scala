// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model

import com.barrybecker4.simulation.voronoi.algorithm.PoissonParams
import com.barrybecker4.simulation.voronoi.algorithm.model.placement.{PlacementMethod, PoissonPlacement, RandomPlacement}
import com.barrybecker4.ui.renderers.OfflineGraphics
import com.barrybecker4.ui.util.ColorMap

import javax.vecmath.Point2d
import scala.util.Random

object PointPlacementModel {
  private val RND = new Random(0)
}

/**
  * Everything we need to know to compute the Voronoi diagram.
  * @author Barry Becker
  */
class PointPlacementModel private[algorithm](
  val width: Int, val height: Int,
  var params: PoissonParams, val usePoissonDistribution: Boolean, val connectPoints: Boolean,
  var numPoints: Int, var cmap: ColorMap, rnd: Random = RND) {

  private var placementModel: PlacementMethod = _

  def getWidth: Int = width
  def getHeight: Int = height

  def reset(): Unit = synchronized {
    rnd.setSeed(0)
    placementModel =
      if (usePoissonDistribution) new PoissonPlacement(width, height, params, numPoints, cmap, rnd)
      else new RandomPlacement(width, height, numPoints, cmap, rnd)
  }

  def getSamples: IndexedSeq[Point2d] = placementModel.getSamples

  /** @param numPoints number of points to add on this increment */
  def increment(numPoints: Int): Unit = synchronized {
    placementModel.increment(numPoints)
  }

}
