// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT 

package com.barrybecker4.simulation.voronoi.algorithm.model.poisson.placement

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point


trait PlacementMethod {

  def increment(numPoints: Int): Unit

  def getSamples: IndexedSeq[Point]

}
