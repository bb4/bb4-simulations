// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.rendering

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point


trait IPointRenderer {

  def drawPoints(samples: Seq[Point]): Unit
  
}
