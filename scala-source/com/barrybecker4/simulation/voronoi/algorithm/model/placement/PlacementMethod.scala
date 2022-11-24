// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT 

package com.barrybecker4.simulation.voronoi.algorithm.model.placement

import javax.vecmath.Point2d

trait PlacementMethod {

  def increment(numPoints: Int): Unit

  def getSamples: IndexedSeq[Point2d]

}
