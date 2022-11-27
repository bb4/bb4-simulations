// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.poisson

import com.barrybecker4.simulation.cave.model.CaveProcessor
import com.barrybecker4.simulation.voronoi.algorithm.model.poisson.PointPlacementModel.DistributionType
import com.barrybecker4.simulation.voronoi.algorithm.model.poisson.placement.{GaussianPlacement, PlacementMethod, PoissonPlacement, RandomPlacement}
import com.barrybecker4.simulation.voronoi.algorithm.model.poisson.PoissonParams
import com.barrybecker4.ui.renderers.OfflineGraphics
import com.barrybecker4.ui.util.ColorMap
import PointPlacementModel.DistributionType._

import javax.vecmath.Point2d
import scala.util.Random


object PointPlacementModel {
  enum DistributionType:
    case UNIFORM, GAUSSIAN, POISSON
    
  val DEFAULT_DISTRIBUTION_TYPE: DistributionType = DistributionType.POISSON
  private val RND = new Random(0)
}

/**
  * Everything we need to know to compute the Voronoi diagram.
  * @author Barry Becker
  */
class PointPlacementModel private[algorithm](val width: Int, val height: Int,
  var params: PoissonParams, var numPoints: Int, distributionType: DistributionType, rnd: Random = RND) {

  
  private var placementMethod: PlacementMethod = _

  def getWidth: Int = width
  def getHeight: Int = height

  def reset(): Unit = synchronized {
    rnd.setSeed(0)
    placementMethod = distributionType match {
      case UNIFORM => new RandomPlacement(width, height, numPoints, rnd)
      case GAUSSIAN => new GaussianPlacement(width, height, numPoints, rnd)
      case POISSON => new PoissonPlacement(width, height, params, numPoints, rnd)
    }
  }

  def getSamples: IndexedSeq[Point2d] = placementMethod.getSamples

  /** @param numPoints number of points to add on this increment */
  def increment(numPoints: Int): Unit = synchronized {
    placementMethod.increment(numPoints)
  }

}
