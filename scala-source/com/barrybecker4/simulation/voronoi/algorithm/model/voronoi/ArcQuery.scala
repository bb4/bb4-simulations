// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.ArcKey


class ArcQuery(val p: Point) extends ArcKey {
  
  override protected def getLeft: Point = p
  override protected def getRight: Point = p
  
}
