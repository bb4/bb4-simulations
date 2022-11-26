// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.event

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point
import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Arc
import com.barrybecker4.simulation.voronoi.algorithm.VoronoiProcessor


class CircleEvent(val arc: Arc, p: Point, val vert: Point) extends SiteEvent(p) {
  override def handleEvent(v: VoronoiProcessor): Unit = {
    v.handleCircleEvent(p, arc, vert)
  }
}
